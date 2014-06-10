package edu.cuny.qc.perceptron.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

public enum SerializationMethod {
	BZ2(".bz2", CompressorStreamFactory.BZIP2, true, 2*1024*1024)/* {
		@Override public InputStream getInputStream(InputStream innerStream) throws IOException {
			return new BZip2CompressorInputStream(innerStream);	}
		@Override public OutputStream getOutputStream(OutputStream innerStream) throws IOException {
			return new BZip2CompressorOutputStream(innerStream); }
	}*/,
	NONE(".plain", null, true, 2*1024*1024)/* {
		@Override public InputStream getInputStream(InputStream innerStream) {
			return innerStream;	}
		@Override public OutputStream getOutputStream(OutputStream innerStream) {
			return innerStream; }
	}*/;

//	public abstract InputStream getInputStream (InputStream innerStream) throws IOException;
//	public abstract OutputStream getOutputStream (OutputStream innerStream) throws IOException;
	public InputStream getInputStream(InputStream innerStream) throws IOException {
		try {
			InputStream result = innerStream;
			if (shouldBuffer) {
				if (bufferSize == null) {
					bufferSize = DEFAULT_BUFFER_SIZE;
				}
				result = new BufferedInputStream(result, bufferSize);
			}
			if (compressorName != null) {
				result = new CompressorStreamFactory().createCompressorInputStream(compressorName, result);
			}
			return result;
		} catch (CompressorException e) {
			throw new IOException(e);
		}
	}
	public OutputStream getOutputStream(OutputStream innerStream) throws IOException {
		try {
			OutputStream result = innerStream;
			if (shouldBuffer) {
				if (bufferSize == null) {
					bufferSize = DEFAULT_BUFFER_SIZE;
				}
				result = new BufferedOutputStream(result, bufferSize);
			}
			if (compressorName != null) {
				result = new CompressorStreamFactory().createCompressorOutputStream(compressorName, result);
			}
			return result;
		} catch (CompressorException e) {
			throw new IOException(e);
		}
	}
	
	private SerializationMethod(String extension, String compressorName,
			Boolean shouldBuffer, Integer bufferSize) {
		this.extension = extension;
		this.compressorName = compressorName;
		this.shouldBuffer = shouldBuffer;
		this.bufferSize = bufferSize;
	}
	
	public String extension;
	public String compressorName;
	public Boolean shouldBuffer;
	public Integer bufferSize;
	
	public static final int DEFAULT_BUFFER_SIZE = 8192; // Copied from BufferedInputStream.defaultBufferSize
}
