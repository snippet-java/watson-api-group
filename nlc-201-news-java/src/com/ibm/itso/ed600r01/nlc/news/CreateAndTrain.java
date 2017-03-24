package com.ibm.itso.ed600r01.nlc.news;

import java.io.File;

import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classifier;

public class CreateAndTrain {
	
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

	public Classifier createClassifier(String csvFile,String user, String pwd,String classifierName,String lang){
		csvFile = getLocation(csvFile);
		NaturalLanguageClassifier nlc = new NaturalLanguageClassifier(user, pwd);
		return nlc.createClassifier(classifierName, lang, new File(csvFile)).execute();
	}
	
	public static void main(String args[]){
		if (args.length < 5)
		{
			System.out.println("Usage : ");
			System.out.println("java CreateAndTrain <CSVFILE> <USER> <PASSWORD> <CLASSIFIER_NAME> <LANGUAGE>");
			System.out.println("about valid Languages consult URL");
			System.out.println("https://www.ibm.com/watson/developercloud/doc/natural-language-classifier/using-your-data.html");
			System.exit(0);
		}
		
		System.out.println(new CreateAndTrain().createClassifier(args[0], args[1], args[2], args[3],  args[4]));
	}	
}

