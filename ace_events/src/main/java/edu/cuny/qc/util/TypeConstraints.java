package edu.cuny.qc.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.cuny.qc.ace.acetypes.AceEntityMention;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.types.SentenceInstance;

/**
 * In this class, we make a list of mapping between trigger type and argument type
 * according to the ACE 2005 guidelilne: "Each Event type and subtype will have its own set of potential participant roles for the Entities which occur within the scopes of its exemplars"
 * @author che
 *
 */
public class TypeConstraints
{
	// map event subtype --> entity types
	public static Map<String, Set<String>> eventEntityTypes = new HashMap<String, Set<String>>();
	// map event subtype --> argument role
	public static Map<String, Set<String>> argumentRoles = new HashMap<String, Set<String>>();
	// map argument_role --> entity types 
	public static Map<String, Set<String>> roleEntityTypes = new HashMap<String, Set<String>>();
	// independent argument roles: argument roles that are not dependent on the types of event
	public static List<String> independentRoles = new ArrayList<String>();
	
	// a mapping from event subtype to type
	public static Map<String, String> eventTypeMap = new HashMap<String, String>();
	public static Map<String, String> eventTypeMapModified = new HashMap<String, String>();
	
	static
	{
		eventTypeMap.put("Be-Born","Life");
		eventTypeMap.put("Marry","Life");
		eventTypeMap.put("Divorce","Life");
		eventTypeMap.put("Injure","Life");
		eventTypeMap.put("Die","Life");
		eventTypeMap.put("Transport","Movement");
		eventTypeMap.put("Transfer-Ownership","Transaction");
		eventTypeMap.put("Transfer-Money","Transaction");
		eventTypeMap.put("Start-Org","Business");
		eventTypeMap.put("Merge-Org","Business");
		eventTypeMap.put("Declare-Bankruptcy","Business");
		eventTypeMap.put("End-Org","Business");
		eventTypeMap.put("Attack","Conflict");
		eventTypeMap.put("Demonstrate","Conflict");
		eventTypeMap.put("Meet","Contact");
		eventTypeMap.put("Phone-Write","Contact");
		eventTypeMap.put("Start-Position","Personnel");
		eventTypeMap.put("End-Position","Personnel");
		eventTypeMap.put("Nominate","Personnel");
		eventTypeMap.put("Elect","Personnel");
		eventTypeMap.put("Arrest-Jail","Justice");
		eventTypeMap.put("Release-Parole","Justice");
		eventTypeMap.put("Trial-Hearing","Justice");
		eventTypeMap.put("Charge-Indict","Justice");
		eventTypeMap.put("Sue","Justice");
		eventTypeMap.put("Convict","Justice");
		eventTypeMap.put("Sentence","Justice");
		eventTypeMap.put("Fine","Justice");
		eventTypeMap.put("Execute","Justice");
		eventTypeMap.put("Extradite","Justice");
		eventTypeMap.put("Acquit","Justice");
		eventTypeMap.put("Appeal","Justice");
		eventTypeMap.put("Pardon","Justice");
		
		eventTypeMapModified.put("Be-Born","Life");
		eventTypeMapModified.put("Marry","Life");
		eventTypeMapModified.put("Divorce","Life");
		eventTypeMapModified.put("Transport","Movement");
		eventTypeMapModified.put("Transfer-Ownership","Transaction");
		eventTypeMapModified.put("Transfer-Money","Transaction");
		eventTypeMapModified.put("Start-Org","Business");
		eventTypeMapModified.put("Merge-Org","Business");
		eventTypeMapModified.put("Declare-Bankruptcy","Business");
		eventTypeMapModified.put("End-Org","Business");
		eventTypeMapModified.put("Injure","Conflict");
		eventTypeMapModified.put("Die","Conflict");
		eventTypeMapModified.put("Attack","Conflict");
		eventTypeMapModified.put("Demonstrate","Conflict");
		eventTypeMapModified.put("Meet","Contact");
		eventTypeMapModified.put("Phone-Write","Contact");
		eventTypeMapModified.put("Start-Position","Personnel");
		eventTypeMapModified.put("End-Position","Personnel");
		eventTypeMapModified.put("Nominate","Personnel");
		eventTypeMapModified.put("Elect","Personnel");
		eventTypeMapModified.put("Arrest-Jail","Justice");
		eventTypeMapModified.put("Release-Parole","Justice");
		eventTypeMapModified.put("Trial-Hearing","Justice");
		eventTypeMapModified.put("Charge-Indict","Justice");
		eventTypeMapModified.put("Sue","Justice");
		eventTypeMapModified.put("Convict","Justice");
		eventTypeMapModified.put("Sentence","Justice");
		eventTypeMapModified.put("Fine","Justice");
		eventTypeMapModified.put("Execute","Justice");
		eventTypeMapModified.put("Extradite","Justice");
		eventTypeMapModified.put("Acquit","Justice");
		eventTypeMapModified.put("Appeal","Justice");
		eventTypeMapModified.put("Pardon","Justice");
	}
	
	static
	{
		try
		{
			// initialize independentRoles
			independentRoles.add("Place");
			independentRoles.add("Time");
			
			// read argument role mapping
			BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/data/ace/argumentRoles"));
			String line = "";
			while((line = reader.readLine()) != null)
			{
				String fields[] = line.split("\\s");
				
				if(fields.length > 2)
				{
					String event_type = fields[0];
					String argument_role = fields[1];
					
					Set<String> set = argumentRoles.get(event_type);
					if(set == null)
					{
						set = new HashSet<String>();
					}
					set.add(argument_role);
					argumentRoles.put(event_type, set);
					
					for(int i=2; i<fields.length; i++)
					{
						Set<String> entityTypes = roleEntityTypes.get(argument_role);
						if(entityTypes == null)
						{
							entityTypes = new HashSet<String>();
						}
						entityTypes.add(fields[i]);
						roleEntityTypes.put(argument_role, entityTypes);
					}
				}
			}
			reader.close();
			
			for(String eventType : argumentRoles.keySet())
			{
				Set<String> roles = argumentRoles.get(eventType);
				for(String role : roles)
				{
					Set<String> entityTypes = roleEntityTypes.get(role);
					if(entityTypes != null)
					{
						Set<String> possibleEntityTypes = eventEntityTypes.get(eventType);
						if(possibleEntityTypes == null)
						{
							possibleEntityTypes = new HashSet<String>();
							eventEntityTypes.put(eventType, possibleEntityTypes);
						}
						possibleEntityTypes.addAll(entityTypes);
					}
				}
			}
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Given an event subtype, return the type. e.g. End_Position --> Personnel
	 * @param type
	 * @return
	 */
	public static String getEventSuperType(String type)
	{
		return eventTypeMap.get(type);
	}
	
	/**
	 * given an argument role, get the compatible entity types
	 * @param role
	 * @return
	 */
	public static Set<String> getCompatibleEntityTypes(String role)
	{
		return roleEntityTypes.get(role);
	}
	
	/**
	 * given an argument role, and an entity type, check if they are compatible
	 * @param role
	 * @param type
	 * @return
	 */
	public static boolean isEntityTypeCompatible(String role, String type)
	{
		role = getCanonicalRoleName(role);
		Set<String> entityTypes = roleEntityTypes.get(role);
		if(entityTypes != null && entityTypes.contains(type))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * get cannonical name of role. e.g TIME-Wthin --> TIME
	 * @param role
	 * @return
	 */
	protected static String getCanonicalRoleName(String role)
	{
		if(role.startsWith("Time"))
		{
			return "Time";
		}
		else
		{
			return role;
		}
		
	}
	
	/**
	 * given a event subtype and argument role, check if it's comptible 
	 * @param subtype
	 * @param role
	 * @return
	 */
	public static boolean isRoleCompatible(String subtype, String role)
	{
		role = getCanonicalRoleName(role);
		Set<String> roles = argumentRoles.get(subtype);
		if(roles != null && roles.contains(role))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * given a argument role type, check if it's independent of event types
	 * @param role
	 * @return
	 */
	public static boolean isIndependentRole(String role)
	{
		role = getCanonicalRoleName(role);
		if(independentRoles.contains(role))
		{
			return true;
		}
		return false;
	}

	public static boolean isEntityTypeEventCompatible(String eventType, String entityType)
	{
		Set<String> types = eventEntityTypes.get(eventType);
		return types.contains(entityType);
	}
	
	/**
	 * judge if the current node is a possible trigger
	 * basically, if current token is not one of (Verb, Noun, or Adj), it's not a possible trigger
	 * @param problem
	 * @param i
	 * @return
	 */
	public static boolean isPossibleTriggerByPOS(SentenceInstance problem, int i)
	{
		final String allowedPOS = "IN|JJ|RB|DT|VBG|VBD|NN|NNPS|VB|VBN|NNS|VBP|NNP|PRP|VBZ";
		String[] posTags = problem.getPosTags();
		if(posTags[i].matches(allowedPOS))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * if the token is in head of an entity, then it can not be a possible trigger
	 * @param problem
	 * @param i
	 * @return
	 */
	public static boolean isPossibleTriggerByEntityType(SentenceInstance problem, int i)
	{
		for(AceMention mention : problem.eventArgCandidates)
		{
			if(mention instanceof AceEntityMention && mention.getHeadIndices().contains(i))
			{
				return false;
			}
		}
		return true;
	}
}
