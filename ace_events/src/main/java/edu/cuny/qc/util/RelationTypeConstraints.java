package edu.cuny.qc.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelationTypeConstraints
{
	public static List<String> roles = new ArrayList<String>();
	
	// a mapping from relation subtype --> arg role --> allowed entity types
	public static Map<String, Map<String, List<String>>> relationArgsMap = new HashMap<String, Map<String, List<String>>>();
	
	// a mapping from relation subtype to type
	public static Map<String, String> relationTypeMap = new HashMap<String, String>();

	static
	{
		try
		{
			// read argument role mapping
			BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/data/ace/relationTypeConstraints"));
			String line = "";
			while((line = reader.readLine()) != null)
			{
				String fields[] = line.split("\\s");
				
				if(fields.length > 3)
				{
					String type = fields[0];
					String subtype = fields[1];
					String role = fields[2];
					List<String> entityTypes = Arrays.asList((Arrays.copyOfRange(fields, 3, fields.length)));
					
					// add relation type->subtype pair
					relationTypeMap.put(subtype, type);
					
					// add relation type -> role -> entity types
					Map<String, List<String>> role2Types = relationArgsMap.get(subtype);
					if(role2Types == null)
					{
						role2Types = new HashMap<String, List<String>>();
						relationArgsMap.put(subtype, role2Types);
					}
					role2Types.put(role, entityTypes);
					
					// put roles into a list (in ACE, there are only two roles: Arg-1 and Arg-2)
					if(!roles.contains(role))
					{
						roles.add(role);
					}
				}
			}
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Given an relation subtype, return the type. e.g. Ownership --> ORG-AFF
	 * @param type
	 * @return
	 */
	public static String getRelationSuperType(String type)
	{
		return relationTypeMap.get(type);
	}
	
	/**
	 * given a pair of entity sub types, get a list of possible relation sub types and arg roles assignment
	 * @param entityType1
	 * @param entityType2
	 * @return List<String[]> String[0] is relation subtype, String[1] is role1, String[2] is role2
	 */
	public static List<String[]> getPossibleRelations(String entityType1, String entityType2)
	{
		List<String[]> ret = new ArrayList<String[]>();
		for(String relationType : relationArgsMap.keySet())
		{
			List<String> entityTypes1 = relationArgsMap.get(relationType).get(roles.get(0));
			List<String> entityTypes2 = relationArgsMap.get(relationType).get(roles.get(1));
			
			if(entityTypes1.contains(entityType1) && entityTypes2.contains(entityType2)) 
			{
				ret.add(new String[]{relationType, roles.get(0), roles.get(1)});
			}
			if(entityTypes1.contains(entityType2) && entityTypes2.contains(entityType1))
			{
				ret.add(new String[]{relationType, roles.get(1), roles.get(0)});
			}
		}
		
		return ret;
	}
	
	public static void main(String[] args)
	{
		System.out.println(roles);
		for(String[] entry : getPossibleRelations("PER", "LOC"))
		{
			System.out.println(Arrays.toString(entry));
		}
	}
}
