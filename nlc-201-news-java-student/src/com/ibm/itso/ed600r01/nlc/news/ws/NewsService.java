package com.ibm.itso.ed600r01.nlc.news.ws;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.ibm.itso.ed600r01.nlc.news.Query;
import com.ibm.itso.ed600r01.nlc.news.beans.ClassifyNews;
import com.ibm.itso.ed600r01.nlc.news.beans.QueryNews;

@Path("/news")
public class NewsService {
	
	private static final String CLASS_FEEDBACK_DB = "class_feedback_db";
	
	public String user;
	public String password;
	public String userDB;
	public String passwordDB;
	public String newsClassifierID;
	
	public NewsService(){
		try {
			loadProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadProperties() throws Exception{
		
		Map<String,String> env = System.getenv();
		
		JSONObject vcap = null;
		
		//VCAP_SERVICES is loaded from Bluemix (Cloud) or Tomcar/Liberty/configuration.properties (Locally)
		if (env.containsKey("VCAP_SERVICES"))
		{
			vcap = new JSONObject(env.get("VCAP_SERVICES"));
			newsClassifierID = env.get("CLASSIFIER_ID");
		}
		else // If VCAP_SERVICES is not set into Tomcat or Liberty or BlueMix variable
		{	
			InputStream in = null;
			
			try
			{				
				Properties localEnv = new Properties();
				
				in = getClass().getResourceAsStream("/configuration.properties");
				
				localEnv.load(in);				
				
				in.close();			
				
				vcap = new JSONObject(localEnv.getProperty("VCAP_SERVICES"));
				
				newsClassifierID = localEnv.getProperty("CLASSIFIER_ID");
			}
			finally{
				if (in != null){
					in.close();
				}			
			}			
		}
		
		//Natural Language Entry
		JSONObject service = (JSONObject)vcap.getJSONArray("natural_language_classifier").getJSONObject(0);
		
		if (service !=null)
		{
			JSONObject creds = service.getJSONObject("credentials");
			user = creds.getString("username");
			password = creds.getString("password");
		}
		
		service = (JSONObject)vcap.getJSONArray("cloudantNoSQLDB").getJSONObject(0);
		
		if (service !=null)
		{
			JSONObject creds = service.getJSONObject("credentials");
			userDB = creds.getString("username");
			passwordDB = creds.getString("password");
		}
		//Cloudant Entry for classifier feedback
		
	}
	
	@POST
	@Path("/classify")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response classifyText(QueryNews query){
		String result = null;
		
		try	{
			result = new Query().classify(newsClassifierID,user, password,query.getTextNews());
		}
		catch(Exception e){
			e.printStackTrace();
			return Response.status(500).entity("Classifier failed - Error Message : " + e.getMessage() + " - Contact Administrator").build();			
		}
		
		return Response.ok(result).build();		
	}

	@POST
	@Path("/feedback")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response feedbackClassification(ClassifyNews feedback){
		//On this method it will call cloudant DB and save the user feedback about classification
		
		try	{
			CloudantClient client = ClientBuilder.account(userDB)
	                .username(userDB)
	                .password(passwordDB)
	                .build();
			// Get a Database instance to interact with, but create it if it doesn't already exist
			Database db = client.database(CLASS_FEEDBACK_DB, true);
			
			db.save(feedback);
			return Response.ok("Feedback saved with success !").build();		
		}
		catch(Exception e){
			e.printStackTrace();
			return Response.status(500).entity("Feedback failed - Error Message : " + e.getMessage() + " - Contact Administrator").build();
		}
	}
}
