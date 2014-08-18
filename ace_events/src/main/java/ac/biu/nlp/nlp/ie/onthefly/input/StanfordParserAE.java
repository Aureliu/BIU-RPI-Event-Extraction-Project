package ac.biu.nlp.nlp.ie.onthefly.input;

import java.util.Collection;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.util.JCasUtil;

import com.google.common.collect.Lists;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.perceptron.featureGenerator.TextFeatureGenerator;
import edu.cuny.qc.perceptron.graph.DependencyGraph;
import edu.cuny.qc.perceptron.graph.GraphEdge;
import edu.cuny.qc.perceptron.graph.GraphNode;
import edu.cuny.qc.util.ParserWrapper;
import edu.cuny.qc.util.Utils;
import edu.cuny.qc.util.ParserWrapper.ParseResult;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;

/**
 * Taken mostly just wraps Qi's Stanford Parser.
 * 
 * @author Ofer Bronstein
 *
 */
public class StanfordParserAE extends JCasAnnotator_ImplBase {
	

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
//		try {
			for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
				List<Token> tokensList = Lists.newArrayList(JCasUtil.selectCovered(jcas, Token.class, sentence));
				List<String> tokenStrsList = JCasUtil.toText(tokensList);
				String[] tokenStrs = new String[tokenStrsList.size()];
				tokenStrsList.toArray(tokenStrs);

				String[] posStrs = new String[tokenStrsList.size()];
				for (int n=0; n<tokenStrsList.size(); n++) {
					posStrs[n] = tokensList.get(n).getPos().getPosValue();
				}

				Collection<TypedDependency> tdl;
				ParserWrapper parser = ParserWrapper.getParserWrapper();
				
				/// 1. prep_in
				//ParseResult parse = parser.getTypedDeps(tokenStrs);
				//tdl = parse.deps;
				
				/// 2. prep_in
				//tdl = parser.getTypedDepsCollapsed(tokenStrs, posStrs);
				
//				Tree tree = parser.getParseTree(tokenStrs, posStrs);
//				GrammaticalStructure gs = parser.gsf.newGrammaticalStructure(tree);

				/// 3. V - only one! The chosen one! (with "true")
				//tdl = gs.typedDependencies(true);
				ParseResult parse = parser.getTypedDepsUncollapsed(tokenStrs, posStrs);
				
				/// 4. prepr_in
				//tdl = gs.typedDependenciesCollapsed(true);

				/// 5. prep_in
				//tdl = gs.typedDependenciesCollapsedTree();

				/// 6. prep_in
				//tdl = gs.typedDependenciesCCprocessed(true);

				DependencyGraph graph = new DependencyGraph(parse.deps, tokenStrs.length);
				
				/// DEBUG
				List<GraphEdge> allEdges = Lists.newArrayList();
				List<GraphEdge> onlyMyEdges = Lists.newArrayList();
				////
				for (int i=0; i<tokenStrs.length; i++) {
					GraphNode node = graph.getVertices().get(i);
					allEdges.addAll(node.edges);
					for (GraphEdge edge : node.edges) {
						int otherIndex = edge.getGovernor();
						if (otherIndex == i) {
							onlyMyEdges.add(edge);
							Token governor = tokensList.get(edge.getGovernor());
							Token dependent = tokensList.get(edge.getDependent());
							TextFeatureGenerator.addDependencyToJCas(jcas, edge, governor, dependent, sentence);
						}
					}
				}
				/// DEBUG
				System.out.printf("\nSentence: %s\n", sentence.getCoveredText());
				//System.out.printf("All edges: %s\nMy edges: %s", Utils.edgesToStr(allEdges, tokensList), Utils.edgesToStr(onlyMyEdges, tokensList));
				System.out.printf("Edges to jcas: %s", Utils.edgesToStr(onlyMyEdges, tokensList));
				System.out.printf("\n");
				///
			}
//		} catch (IOException e) {
//			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
//		} catch (AeException e) {
//			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
//		}
	}
}
