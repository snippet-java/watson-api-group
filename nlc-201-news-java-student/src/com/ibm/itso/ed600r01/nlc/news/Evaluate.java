package com.ibm.itso.ed600r01.nlc.news;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.opencsv.CSVReader;

public class Evaluate {
	
	private String getLocation(String path){
		
		File check = new File(path);
		
		//If path does not exists, program will try to find into resources dir
		if (!check.exists())
		{
			String root = System.getProperty("user.dir");
			path = root + File.separator+"resources"+ File.separator+path;
			check = new File(path);
			if (!check.exists())
			{
				System.out.println("File does not exists on location requested and neither into project resources folder");
				System.exit(0);
			}			
		}
		
		return path;
	}
	
	public List<String[]> batchClassify(String classID, String user, String password, String file) throws Exception{
		  ArrayList<String[]> response = new ArrayList<String[]>();
		  CSVReader reader = null;
		
		  try
		  {
			  file = getLocation(file);
			  reader = new CSVReader(new InputStreamReader(new FileInputStream(file),"UTF-8"), ',' , '"');
		  	  //Read CSV line by line and use the string array as you want
			  String[] nextLine;
			  String[] item= null;
			  Query query = new Query();
			  String jsonResult = null;
			  JSONObject jobj = null;
			  String textToClassify = null;
			  while ((nextLine = reader.readNext()) != null) {
				  textToClassify = "";
				  item = new String[2];
				  item[0] = nextLine[nextLine.length-1];
				  for(int i=0;i<nextLine.length-1;++i)
					  textToClassify+=nextLine[i];
				  jsonResult = query.classify(classID, user, password, textToClassify);
				  jobj = new JSONObject(jsonResult);
				  item[1] = jobj.getString("top_class");
				  response.add(item);				  
			  }
		  }
		  finally{
			  if (reader != null)
				  reader.close();
		  }
	      
	      return response;
	}
	
	public String generateAcuracy(List<String[]> classResults){
		
		String[] item=null;
		int hits=0;
		for(int i=0;i<classResults.size();++i){
			item = classResults.get(i);
			if (item[0].equalsIgnoreCase(item[1]))
				hits++;
		}
		
		DecimalFormat df = new DecimalFormat("#.00");
			
		return df.format((100 * (hits/(double)classResults.size())));	
	}
	
	public static void main(String args[]){
		if (args.length < 4){
			System.out.println("Usage : ");
			System.out.println("java com.ibm.itso.ed600r01.nlc.news.Evaluate <CLASSIFIER_ID> <USER> <PASSWORD> <CSV_FILE_TEST>");
		}
		
		Evaluate evaluate = new Evaluate();
		try {
			System.out.println(evaluate.generateAcuracy(evaluate.batchClassify(args[0], args[1], args[2], args[3]))+ " % of accuracy");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
