package com.ibm.itso.ed600r01.nlc.news;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

public class PrepareData {

	public static final String TEST = "test";
	public static final String TRAIN = "train";

	public List<String> buildData(String path) {

		ArrayList<String> csvSource = new ArrayList<String>();
		String data = null;

		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(path), ',', '"', 0);

			// Read CSV line by line and use the string array as you want
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				if (nextLine != null) {
					data = "";
					if (nextLine.length < 2) {
						//Trying to reach ; format of csv
						reader = new CSVReader(new FileReader(path), ';', '"', 0);
						nextLine = reader.readNext();
						if (nextLine!=null && nextLine.length < 2)
						{
							System.out.println("CSV format is invalid needs to have: \"text to classify\",classification");
							System.exit(0);
						}	
					}
					if (nextLine.length > 2) {
						for(int i=0;i<nextLine.length-1;++i){
							data+=nextLine[i];
						}
					}	
					if (nextLine.length == 2)
						data = nextLine[0];
					int outBound = 1024;
					if (data.length() < 1024)
						outBound = data.length();
					data = data.substring(0, outBound);
					data = data.replace("\"", "\"\"");
					data = data.replace('\t', ' ');
					data = data.replace('\n', ' ');
					data = data.replace('\r', ' ');
					data = data.replace("\r\n", " ");
					if (data.indexOf(",") != -1) {
						data = data.substring(0, outBound - 2);
						if (data.endsWith("\"") && !data.endsWith("\"\"")) {
							data = '\"' + data;
							outBound -= 1;
						} else
							data = '\"' + data + '\"';
					}
					if (nextLine.length == 2)
						csvSource.add(data.substring(0, outBound) + "," + nextLine[1]);
					else
						csvSource.add(data.substring(0, outBound) + "," + nextLine[nextLine.length-1]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if (reader!=null)
			{
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return csvSource;
	}

	public void writeData(String path, List<String> data) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, false), "UTF-8"));

			for (int i = 0; i < data.size(); ++i) {
				if (i != 0)
					out.newLine();
				out.write(data.get(i));
				out.flush();
			}
			
			System.out.println("File "+path+" updated !");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

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
	
	public static void main(String args[]) {
		PrepareData pdata = new PrepareData();
		if (args.length < 1) {
			System.out.println("java PrepareData <CSV_FILE_PATH>");
			System.exit(0);
		}

		System.out.println("Preparing File " + args[0] + " to be ready for Natural Language Classifier input");
		System.out.println("Fixing 1024 chars for text lengh, handling special chars like line feed and format to UTF-8 format");
		// Prepare train data
		
		args[0] = pdata.getLocation(args[0]);
		pdata.writeData(args[0], pdata.buildData(args[0]));

		System.out.println("Data prepared !");
	}
}
