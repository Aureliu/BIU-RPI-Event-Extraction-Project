package edu.cuny.qc.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import opennlp.tools.util.InvalidFormatException;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

/**
 * this is a thin wrapper of Stanford dependency parser
 * @author z0034d5z
 *
 */
public class ParserWrapper 
{
	static private ParserWrapper parser;
	static private Morphology morphology = new edu.stanford.nlp.process.Morphology();
	
	static public ParserWrapper getParserWrapper()
	{
		if(parser == null)
		{
			parser = new ParserWrapper(new File("src/main/resources/data/englishPCFG.ser.gz"));
		}
		return parser;
	}
	
	public LexicalizedParser lp;
	public TreebankLanguagePack tlp;
	public GrammaticalStructureFactory gsf;
	
	ParserWrapper(File modelFile)
	{
		lp = LexicalizedParser.loadModel(modelFile.getAbsolutePath());
		tlp = new PennTreebankLanguagePack();
	    gsf = tlp.grammaticalStructureFactory();
	}
	
	/**
	 * get lemma using Stanford morphology
	 * @param token
	 * @param word
	 * @return
	 */
	public static String lemmanize(String token, String pos)
	{
		return morphology.lemma(token, pos);
	}
	
	private Tree getParseTree(String[] tokens)
	{
		List<HasWord> sentence = new ArrayList<HasWord>();
		for(int i=0; i<tokens.length; i++)
		{
			String tok = tokens[i];
			Word word = new Word(tok);
			sentence.add(word);
		}
		Tree parseTree = lp.apply(sentence);
		return parseTree;
	}
	
	/**
	 * get a parse tree from tokenized sentence with pos tags
	 * @param tokens
	 * @param postags
	 * @return
	 */
	private Tree getParseTree(String[] tokens, String[] postags)
	{
		List<HasWord> sentence = new ArrayList<HasWord>();
		for(int i=0; i<tokens.length; i++)
		{
			String tok = tokens[i];
			String pos = postags[i];
			TaggedWord word = new TaggedWord(tok, pos);
			sentence.add(word);
		}
		Tree parseTree = lp.apply(sentence);
		return parseTree;
	}
	
	/**
	 * get the collection of typedDependencies from a parse tree
	 * @param parseTree
	 * @return
	 */
	private Collection<TypedDependency> getTypedDeps(Tree parseTree)
	{
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parseTree);
		Collection<TypedDependency> tdl = gs.typedDependenciesCCprocessed(true);
		return tdl;
	}
	
	public static class ParseResult
	{
		public Tree tree;
		public Collection<TypedDependency> deps;
		
		ParseResult(Tree tree, Collection<TypedDependency> deps)
		{
			this.tree = tree;
			this.deps = deps;
		}
	}
	
	/**
	 * given tokens, get deps, parse tree, and pos tags (as the leaf nodes of the parse tree)
	 * @param tokens
	 * @return
	 */
	private ParseResult getTypedDeps(String[] tokens)
	{
		Tree tree = getParseTree(tokens);
		Collection<TypedDependency> deps = getTypedDeps(tree);
		
		ParseResult result = new ParseResult(tree, deps);
		return result;
	}
	
	private Collection<TypedDependency> getTypedDeps(String[] tokens, String[] postags)
	{
		Tree tree = getParseTree(tokens, postags);
		return getTypedDeps(tree);
	}
	
	/**
	 * This is the method required for ODIE, as we cannot have the collapsed (e.g. prep_in)
	 * relations - it is incompatible with dkpro types!
	 */
	public ParseResult getTypedDepsUncollapsed(String[] tokens, String[] postags)
	{
		Tree tree = getParseTree(tokens, postags);
		GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
		Collection<TypedDependency> tdl = gs.typedDependencies(true);
		return new ParseResult(tree, tdl);
	}
	
	/**
	 * get the collection of typedDependencies from a parse tree
	 * @param parseTree
	 * @return
	 */
	private Collection<TypedDependency> getTypedDepsCollapsed(Tree parseTree)
	{
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parseTree);
		Collection<TypedDependency> tdl = gs.typedDependenciesCollapsed(true);
		return tdl;
	}
	
	private Collection<TypedDependency> getTypedDepsCollapsed(String[] tokens, String[] postags)
	{
		Tree tree = getParseTree(tokens, postags);
		return getTypedDepsCollapsed(tree);
	}
	
	static public void main(String[] args) throws InvalidFormatException, IOException
	{
		String word = morphology.lemma("men", "NNS");
		System.out.println(word);
	}
}
