package edu.cuny.qc.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;

import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PennPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PennPartOfSpeech.PennPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;

public class PosMap {

	public static LoadingCache<CanonicalPosTag, ? extends PartOfSpeech> byCanonical = CacheBuilder.newBuilder()
			.maximumSize(10)
			.build(new CacheLoader<CanonicalPosTag, PartOfSpeech>() {
				public PartOfSpeech load(CanonicalPosTag tag) throws UnsupportedPosTagStringException {
					switch(tag) {
					case N: return new PennPartOfSpeech(PennPosTag.NN);
					case V: return new PennPartOfSpeech(PennPosTag.VB);
					case ADJ: return new PennPartOfSpeech(PennPosTag.JJ);
					case ADV: return new PennPartOfSpeech(PennPosTag.RB);
					default: return new PennPartOfSpeech(tag.toString());
					}
				}
			});

	public static LoadingCache<String, ? extends PartOfSpeech> byString = CacheBuilder.newBuilder()
			.maximumSize(100)
			.build(new CacheLoader<String, PartOfSpeech>() {
				public PartOfSpeech load(String posStr) throws UnsupportedPosTagStringException {
					return new PennPartOfSpeech(posStr);
				}
			});

}
