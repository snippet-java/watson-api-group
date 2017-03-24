package com.ibm.itso.ed600r01.nlc.news;

import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classifier.Status;

public class Query {
	
	public String classify(String classifierID,String user,String password,String text){
		NaturalLanguageClassifier nlc = new NaturalLanguageClassifier(user,password);
		
		return nlc.classify(classifierID, text).execute().toString();
	}
	
	public Status getStatus(String classifierID,String user, String password){
		NaturalLanguageClassifier nlc = new NaturalLanguageClassifier(user,password);
		
		return nlc.getClassifier(classifierID).execute().getStatus();
	}
	
	public static void main(String[] args){
		if (args.length < 4){
			System.out.println("Usage : ");
			System.out.println("java Query <CLASSIFIER_ID> <USER> <PASSWORD> <QUERY_TEXT>");
			System.exit(0);
		}
		Query query = new Query();
		Status status = query.getStatus(args[0], args[1], args[2]);
		System.out.println("Status of Classifier "+args[0]+" - "+status.toString());
		
		if (status.equals(Status.AVAILABLE)){
			System.out.println("Results for query "+args[3]);
			System.out.println(query.classify(args[0], args[1], args[2], args[3]));
		}
		else
			System.out.println("Classifier "+args[0] +" is not ready for requests ");
	}
}
