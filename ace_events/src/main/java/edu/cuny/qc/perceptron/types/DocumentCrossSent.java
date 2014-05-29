package edu.cuny.qc.perceptron.types;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cuny.qc.ace.acetypes.AceEntity;
import edu.cuny.qc.ace.acetypes.AceEntityMention;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.featureGenerator.TextFeatureGenerator;
import edu.cuny.qc.perceptron.types.Sentence.Sent_Attribute;
import edu.cuny.qc.util.TokenAnnotations;
import edu.cuny.qc.util.TypeConstraints;

public class DocumentCrossSent extends Document
{
	// this defines max number of sents in a cluster
	static int maxNumInCluster = Integer.MAX_VALUE;
	
	static void setMaxNumInCluster(int num)
	{
		maxNumInCluster = num;
	}
	
	// priority Queue about entities for clustering sentences
	static List<String> priorityQueueEntities = new ArrayList<String>();
	
	// Event type --> Trigger token
	public static Map<String, List<String>> triggerTokens = new HashMap<String, List<String>>();
	// Event subtype --> Trigger token
	public static Map<String, List<String>> triggerTokensFineGrained = new HashMap<String, List<String>>();
	// Event subtype --> trigger token with high confidence value
	public static Map<String, List<String>> triggerTokensHighQuality = new HashMap<String, List<String>>();
	
	static
	{
		// initialize priorityQueueEntities
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/data/entityPriorityQueue"));
			String line;
			while((line = reader.readLine()) != null)
			{
				if(line.length() == 0)
				{
					continue;
				}
				String[] fields = line.split("\\t");
				String name = fields[0];
				double freq = Double.parseDouble(fields[1]);
				// igore entities with freq < 0.4
				if(freq > 0.4)
				{
					priorityQueueEntities.add(name);
				}
			}
			reader.close();
			
			// initialize dict of triggerTokens
			reader = new BufferedReader(new FileReader("src/main/resources/data/triggerTokens"));
			while((line = reader.readLine()) != null)
			{
				if(line.length() == 0)
				{
					continue;
				}
				String[] fields = line.split("\\t");
				String eventSubType = fields[0];
				String triggerToken = fields[1];
				Double confidence = Double.parseDouble(fields[2]);
				if(confidence < 0.150)
				{
					continue;
				}
				String eventType = TypeConstraints.eventTypeMap.get(eventSubType);
				List<String> triggers = triggerTokens.get(eventType);
				if(triggers == null)
				{
					triggers = new ArrayList<String>();
					triggerTokens.put(eventType, triggers);
				}
				if(!triggers.contains(triggerToken))
				{
					triggers.add(triggerToken);
				}
				
				triggers = triggerTokensFineGrained.get(eventSubType);
				if(triggers == null)
				{
					triggers = new ArrayList<String>();
					triggerTokensFineGrained.put(eventSubType, triggers);
				}
				if(!triggers.contains(triggerToken))
				{
					triggers.add(triggerToken);
				}
				
				if(confidence >= 0.50)
				{
					triggers = triggerTokensHighQuality.get(eventSubType);
					if(triggers == null)
					{
						triggers = new ArrayList<String>();
						triggerTokensHighQuality.put(eventSubType, triggers);
					}
					if(!triggers.contains(triggerToken))
					{
						triggers.add(triggerToken);
					}
				}
			}
			reader.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public DocumentCrossSent(String baseFileName, boolean hasLabel, boolean monoCase) throws IOException
	{
		super(baseFileName, hasLabel, monoCase);
	}
	
	/**
	 * the container for the sentence "clusters"
	 */
	protected List<List<Sentence>> sentClusters = new ArrayList<List<Sentence>>(); 
	
	/**
	 * return the sentence clusters to the client
	 * @return
	 */
	public List<List<Sentence>> getSentenceClusters()
	{
		return sentClusters;
	}
	
	/**
	 * get sentence cluster
	 * basically gather consecutive sentences with is linked by entity coreference
	 */
	public void setSentenceClustersStrict()
	{
		this.sentClusters.clear();
		// consider the first sent as an individual clusters
		// since it's usually a head line
		List<Sentence> cluster = new ArrayList<Sentence>();
		cluster.add(this.sentences.get(0));
		this.sentClusters.add(cluster);
		
		cluster = new ArrayList<Sentence>();
		cluster.add(this.sentences.get(1));
		this.sentClusters.add(cluster);
		for(int i=2; i<this.getSentences().size(); i++)
		{
			Sentence sent = this.getSentences().get(i);
			List<Sentence> currentCluster = this.sentClusters.get(this.sentClusters.size() - 1);
			Sentence sent_pre = currentCluster.get(currentCluster.size() - 1);
			
			// check if there is entity co-reference chain between two sents
			List<AceEntityMention> mentions = sent.entityMentions;
			List<AceEntityMention> mentions_pre = sent_pre.entityMentions;
			boolean flag = false;
			for(AceEntityMention mention : mentions)
			{
				for(AceEntityMention mention_next : mentions_pre)
				{
					if(mention.getParent().equals(mention_next.getParent()))
					{
						flag = true;
						break;
					}
				}
			}
			if(flag)
			{
				currentCluster.add(sent);
			}
			else
			{
				List<Sentence> new_cluster = new ArrayList<Sentence>();
				new_cluster.add(sent);
				this.sentClusters.add(new_cluster);
			}
		}
	}
	
	/**
	 * rank entities according to priorityQueueEntities
	 * @param entities
	 * @return
	 */
	static private List<AceEntity> rankEntities(final List<AceEntity> entities)
	{
		List<AceEntity> ret = new ArrayList<AceEntity>();
		ret.addAll(entities);
		final List<Integer> ranks = new ArrayList<Integer>();
		for(AceEntity entity : entities)
		{
			AceEntityMention mention = entity.mentions.get(0);
			String name = mention.getNAMMention().getHeadText();
			name = name.toLowerCase();
			int rank = priorityQueueEntities.indexOf(name);
			if(rank == -1)
			{
				// if not in the priority Queue, then it has least priority
				rank = Integer.MAX_VALUE;
			}
			ranks.add(rank);
		}
		// sort entities by ranks
		Collections.sort(ret, new Comparator<AceEntity>()
				{
					@Override
					public int compare(AceEntity entity1, AceEntity entity2)
					{
						int index1 = entities.indexOf(entity1);
						int index2 = entities.indexOf(entity2);
						int rank1 = ranks.get(index1);
						int rank2 = ranks.get(index2);
						if(rank1 > rank2)
						{
							return 1;
						}
						else if(rank1 < rank2)
						{
							return -1;
						}
						else
						{
							// if the rank in priority Queue equals
							// then use frequency in the doc to sort
							if(entity1.mentions.size() > entity2.mentions.size())
							{
								return -1;
							}
							else if(entity1.mentions.size() < entity2.mentions.size())
							{
								return 1;
							}
							else
							{
								return 0;
							}
						}
					}
				}
			);
		return ret;
	}
	
	/**
	 * get sentence cluster
	 * basically gather consecutive sentences which is linked by entity coreference
	 */
	public void setSentenceClustersSimple()
	{
		this.sentClusters.clear();
		// travels each entity to get relevant sents
		for(AceEntity entity : this.aceAnnotations.entities)
		{
			List<Sentence> cluster = new ArrayList<Sentence>();
			// add sentence that contains this entity
			for(int i=0; i<this.sentences.size(); i++)
			{
				// only add sents that are not in any existing clusters
				Sentence sent = this.sentences.get(i);
				if(isNotInCluster(sent))
				{
					for(AceMention mention : sent.entityMentions)
					{
						if(mention.getParent() !=null && mention.getParent() == entity)
						{
							cluster.add(sent);
							break;
						}
					}
				}
			}
			
			List<List<Sentence>> new_clusters = new ArrayList<List<Sentence>>();
			new_clusters.add(cluster);
			// if the number sents exceed max number, then devide them by other entities
			if(cluster.size() > maxNumInCluster)
			{
				new_clusters = resizeCluster(cluster, this.aceAnnotations.entities, this.aceAnnotations.entities.indexOf(entity));
			}
			
			for(List<Sentence> new_cluster : new_clusters)
			{
				// add cluster if doesn't exist
				if(new_cluster.size() > 0 && !this.sentClusters.contains(new_cluster))
				{
					boolean flagContains = false;
					for(List<Sentence> preCluster : this.sentClusters)
					{
						if(preCluster.containsAll(new_cluster))
						{
							flagContains = true;
							break;
						}
					}
					if(!flagContains)
					{
						this.sentClusters.add(new_cluster);
					}
				}
			}
		}
		
		// set the remaining sents as singleton clusters
		for(int i=0; i<this.sentences.size(); i++)
		{
			// only add sents that are not in any existing clusters
			Sentence sent = this.sentences.get(i);
			if(isNotInCluster(sent))
			{
				List<Sentence> cluster = new ArrayList<Sentence>();
				cluster.add(sent);
				this.sentClusters.add(cluster);
			}
		}
	}
	
	/**
	 * get sentence cluster
	 * cluster sentences by using known triggers
	 */
	public void setSentenceClustersByTokens()
	{
		final String allowedPOS = "IN|JJ|RB|DT|VBG|VBD|NN|NNPS|VB|VBN|NNS|VBP|NNP|PRP|VBZ";
		
		this.sentClusters.clear();
		// travels each entity to get relevant sents
		
		for(String eventType : triggerTokens.keySet())
		{
			List<String> triggers = triggerTokens.get(eventType);
			List<Sentence> cluster = new ArrayList<Sentence>();
			// add sentence that contains this entity
			for(int i=0; i<this.sentences.size(); i++)
			{
				// only add sents that are not in any existing clusters
				Sentence sent = this.sentences.get(i);
				List<Map<Class<?>, Object>> tokens = (List<Map<Class<?>, Object>>) sent.get(Sent_Attribute.Token_FEATURE_MAPs);
				for(int j=0; j<tokens.size(); j++)
				{
					String pos = (String) tokens.get(j).get(TokenAnnotations.PartOfSpeechAnnotation.class);
					if(pos.matches(allowedPOS))
					{
						String lemma = (String) tokens.get(j).get(TokenAnnotations.LemmaAnnotation.class);
						if(triggers.contains(lemma))
						{
							cluster.add(sent);
							break;
						}
					}
				}
			}
			
			// add cluster if doesn't exist
			if(cluster.size() > 0 && !this.sentClusters.contains(cluster))
			{
				this.sentClusters.add(cluster);
			}
		}
		
		// sort clusters by size and remove overlapping sents
		Collections.sort(this.sentClusters, new Comparator<List<Sentence>>()
			{			@Override
				public int compare(List<Sentence> set1, List<Sentence> set2)
				{
					if(set1.size() > set2.size())
					{
						return -1;
					}
					else if(set1.size() < set2.size())
					{
						return 1;
					}
					else
					{
						return 0;
					}
				}
			}
		);
		
		for(int i=1; i<this.sentClusters.size(); i++)
		{
			List<Sentence> cluster = this.sentClusters.get(i);
			for(int j=0; j<i; j++)
			{
				List<Sentence> pre_cluster = this.sentClusters.get(j);
				// remove overlapping sents
				cluster.removeAll(pre_cluster);
			}
			if(cluster.size() == 0)
			{
				this.sentClusters.remove(cluster);
				i--;
			}
		}
		
		// set the remaining sents as singleton clusters
		for(int i=0; i<this.sentences.size(); i++)
		{
			// only add sents that are not in any existing clusters
			Sentence sent = this.sentences.get(i);
			if(isNotInCluster(sent))
			{
				List<Sentence> cluster = new ArrayList<Sentence>();
				cluster.add(sent);
				this.sentClusters.add(cluster);
			}
		}
		
		// split sents that are more than 10 sents
		List<List<Sentence>> newClusters = new ArrayList<List<Sentence>>();
		for(List<Sentence> cluster : this.sentClusters)
		{
			if(cluster.size() <= maxNumInCluster)
			{
				newClusters.add(cluster);
			}
			else
			{
				for(int i=0; i<cluster.size() / maxNumInCluster + 1; i++)
				{
					int startIndx = i * maxNumInCluster;
					if(startIndx >= cluster.size())
					{
						break;
					}
					int toIndex = (i+1) * maxNumInCluster;
					if(toIndex > cluster.size())
					{
						toIndex = cluster.size();
					}
					List<Sentence> subCluster = cluster.subList(i * maxNumInCluster, toIndex);
					if(subCluster.size() > 0)
					{
						newClusters.add(subCluster);
					}
				}
			}
		}
		this.sentClusters = newClusters;
	}
	
	/**
	 * get sentence cluster
	 * basically gather consecutive sentences which is linked by entity coreference
	 * use entityPriorityQueue to rank entities in advance
	 */
	public void setSentenceClusters()
	{
		this.sentClusters.clear();
		List<AceEntity> entitiesRanked = rankEntities(this.aceAnnotations.entities);
		// travels each entity to get relevant sents
		for(AceEntity entity : entitiesRanked)
		{
			List<Sentence> cluster = new ArrayList<Sentence>();
			// add sentence that contains this entity
			for(int i=0; i<this.sentences.size(); i++)
			{
				// only add sents that are not in any existing clusters
				Sentence sent = this.sentences.get(i);
				if(isNotInCluster(sent))
				{
					for(AceMention mention : sent.entityMentions)
					{
						if(mention.getParent() !=null && mention.getParent() == entity)
						{
							cluster.add(sent);
							break;
						}
					}
				}
			}
			
			List<List<Sentence>> new_clusters = new ArrayList<List<Sentence>>();
			new_clusters.add(cluster);
			// if the number sents exceed max number, then devide them by other entities
			if(cluster.size() > maxNumInCluster)
			{
				new_clusters = resizeCluster(cluster, entitiesRanked, entitiesRanked.indexOf(entity));
			}
			
			for(List<Sentence> new_cluster : new_clusters)
			{
				// add cluster if doesn't exist
				if(new_cluster.size() > 0 && !this.sentClusters.contains(new_cluster))
				{
					boolean flagContains = false;
					for(List<Sentence> preCluster : this.sentClusters)
					{
						if(preCluster.containsAll(new_cluster))
						{
							flagContains = true;
							break;
						}
					}
					if(!flagContains)
					{
						this.sentClusters.add(new_cluster);
					}
				}
			}
		}
		
		// set the remaining sents as singleton clusters
		for(int i=0; i<this.sentences.size(); i++)
		{
			// only add sents that are not in any existing clusters
			Sentence sent = this.sentences.get(i);
			if(isNotInCluster(sent))
			{
				List<Sentence> cluster = new ArrayList<Sentence>();
				cluster.add(sent);
				this.sentClusters.add(cluster);
			}
		}
	}
	
	/**
	 * use other entities to devide this cluster
	 * @param cluster
	 * @param start: start from which index to get entities 
	 * @param entitiesRanked 
	 * @return
	 */
	private List<List<Sentence>> resizeCluster(List<Sentence> cluster, List<AceEntity> entities, 
			int start)
	{
		List<List<Sentence>> ret = new ArrayList<List<Sentence>>();
		for(int i=start+1; i<entities.size(); i++)
		{
			AceEntity entity = entities.get(i);
			List<Sentence> subCluster = new ArrayList<Sentence>();
			for(int j=0; j<cluster.size(); j++)
			{
				Sentence sent = cluster.get(j);
				for(AceMention mention : sent.entityMentions)
				{
					if(mention.getParent() !=null && mention.getParent() == entity)
					{
						subCluster.add(sent);
						cluster.remove(sent);
						j--;
						break;
					}
				}
			}
			if(subCluster.size() > 0 && subCluster.size() <= maxNumInCluster)
			{
				ret.add(subCluster);
			}
			else if(subCluster.size() > 0)
			{
				// recursively devide the subcluster
				List<List<Sentence>> new_clusters = resizeCluster(subCluster, entities, i);
				ret.addAll(new_clusters);
			}
		}
		
		int size_remain = cluster.size() / maxNumInCluster;
		if(size_remain * maxNumInCluster < cluster.size())
		{
			size_remain++;
		}
		for(int i=0; i<size_remain; i++)
		{
			List<Sentence> subCluster = new ArrayList<Sentence>();
			for(int j=0; j<maxNumInCluster && i*maxNumInCluster+j<cluster.size(); j++)
			{
				Sentence sent = cluster.get(i*maxNumInCluster + j);
				subCluster.add(sent);
			}
			if(subCluster.size() > 0)
			{
				ret.add(subCluster);
			}
		}
		
		return ret;
	}

	/**
	 * check if sent is in any cluster or not
	 * @param sent
	 * @return
	 */
	private boolean isNotInCluster(Sentence sent)
	{
		for(List<Sentence> cluster : this.sentClusters)
		{
			if(cluster.contains(sent))
			{
				return false;
			}
		}
		return true;
	}

	public void printDocCluster(PrintStream out)
	{
		int i=0;
		for(List<Sentence> cluster : this.sentClusters)
		{
			out.println("cluster " + i++);
			for(Sentence sent : cluster)
			{
				String[] tokens = (String[]) sent.get(Sent_Attribute.TOKENS);
				for(String token : tokens)
				{
					out.print(token + " ");
				}
				out.println();
			}
		}
	}
	
	/**
	 * get a list of SentenceInstance from this 
	 * @param nodeTargetAlphabet
	 * @param edgeTargetAlphabet
	 * @param featureAlphabet
	 * @param controller
	 * @param b
	 * @return
	 */
	public List<SentenceInstance> getInstanceList(Alphabet nodeTargetAlphabet, Alphabet edgeTargetAlphabet, Alphabet featureAlphabet, 
			Controller controller, boolean learnable)
	{	
		List<SentenceInstance> instancelist = new ArrayList<SentenceInstance>();
		for(int cluster_id=0 ; cluster_id<this.getSentenceClusters().size(); cluster_id++)
		{
			List<Sentence> cluster = this.getSentenceClusters().get(cluster_id);
			// add all instances
			SentenceInstance inst = new ClusterInstance(cluster, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, 
					controller, learnable);
			instancelist.add(inst);
		}
		return instancelist;
	}
	
	static public void main(String[] args) throws IOException
	{
		File txtFile = new File("/Users/XX/Data/ACE/ACE2005-TrainingData-V6.0/English/nw/timex2norm/AFP_ENG_20030417.0004");
		DocumentCrossSent doc = new DocumentCrossSent(txtFile.getAbsolutePath(), true, false);
		TextFeatureGenerator.doPreprocessCheap(doc);
		doc.setSentenceClustersByTokens();
		doc.printDocCluster(System.out);
	}
}
