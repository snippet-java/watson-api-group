package com.ibm.itso.ed600r01.nlc.news.beans;

public class ClassifyNews extends QueryNews{
	
	//Atrributes needed for CloudantDB
	
	private String _id = System.currentTimeMillis()+"";
	
	private String _rev = null;
	
	//Attributes used by application 
	
	private String classReturned;

	private String classSuggested;

	public String getClassReturned() {
		return classReturned;
	}

	public void setClassReturned(String classReturned) {
		this.classReturned = classReturned;
	}

	public String getClassSuggested() {
		return classSuggested;
	}

	public void setClassSuggested(String classSuggested) {
		this.classSuggested = classSuggested;
	}
	
	public String toString() {
	    return "{ id: " + _id + ",\nrev: " + _rev + ",\ntextNews: " + textNews 
	    		+ ",\nclassReturned: " + classReturned
	    		+ ",\nclassSuggested: " + classSuggested	    		 
	    		+ "\n}";
	  }
	
}
