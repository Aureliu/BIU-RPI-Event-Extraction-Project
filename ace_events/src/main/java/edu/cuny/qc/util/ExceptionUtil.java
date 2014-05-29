package edu.cuny.qc.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Collections of utilities (public static methods) for
 * handling exceptions.
 * 
 * @author Asher Stern
 *
 */
public class ExceptionUtil
{
	/**
	 * Returns the exception's message plug all nested
	 * exceptions messages.
	 * @param throwable an Exception (or Error)
	 * 
	 * @return The exception's message plug all nested
	 * exceptions messages.
	 */
	public static String getMessages (Throwable throwable)
	{
		StringBuffer buffer = new StringBuffer();
		while (throwable != null)
		{
			if (throwable.getMessage()!=null)
			{
				buffer.append(throwable.getClass().getSimpleName()).append(": ").append(throwable.getMessage()).append("\n");
			}
			throwable = throwable.getCause();
		}
		return buffer.toString();
	}
	
	public static String getStackTrace(Throwable t)
	{
		StringWriter stringWriter = new StringWriter();
		t.printStackTrace(new PrintWriter(stringWriter));
		String ret = stringWriter.toString();
		try{stringWriter.close();}catch(IOException e){}
		return ret;
	}
	

}
