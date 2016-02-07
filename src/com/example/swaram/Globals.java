package com.example.swaram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Globals {

	protected static final String EXTRA_LINK = "extra_link";
	public static final String CONTENT = "content";
	public static final String VOICE_DOWNLOADED = "voice_downloaded";
	public static final int OPEN_FILE_REQUEST_CODE = 10;
	public static final String PREDICTION = "prediction";
	public static String PREFERENCE_NAME = "swaram";

	public static boolean saveFile(String path, String fileName,
			String fileContent) {
		File filePath = new File(path);
		try {
			File file = new File(path + "/" + fileName + ".txt");
			if (!filePath.exists()) {
				filePath.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fileOutputStream = new FileOutputStream(file);

			PrintWriter writer = new PrintWriter(fileOutputStream);
			writer.write(fileContent);
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	public static ArrayList<String> sortHashMap(HashMap<String, Integer> map) {

		ArrayList<String> sortedList = new ArrayList<String>();

		Set<Entry<String, Integer>> set = map.entrySet();
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(
				set);
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
					Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		
		
		for (Map.Entry<String, Integer> entry : list) {
			sortedList.add(entry.getKey());
			System.out.println(entry.getKey() + " ==== " + entry.getValue());
		}

		return sortedList;
	}

	public static String readFromFile(String filePath) {
		try {
			FileInputStream stream = new FileInputStream(new File(filePath));
			String line = "";
			StringBuilder builder = new StringBuilder();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(stream));
			while ((line = br.readLine()) != null) {
				builder.append(line);
			}
			return builder.toString();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}
}
