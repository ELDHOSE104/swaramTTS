package com.example.swaram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class AssetExtraction extends AsyncTask<Integer, Integer, Boolean> {

	SharedPreferences preferences;
	Context context;
	ProgressDialog dialog;
	AssetManager assetManager;
	String dirPath;
	FileReader fileReader;
	File consonentFile, vowelFile, signsFile, signsTwoFile, wordsFile,
			learnedWordsFile;
	BufferedReader reader;
	static final String CONSONENTS_FILENAME = "consonents.txt";
	static final String VOWELS_FILENAME = "vowels.txt";
	static final String SIGNS_FILENAME = "signs.txt";
	static final String SIGNSTWO_FILENAME = "signstwo.txt";
	static final String WORDS_FILENAME = "words.txt";
	static final String LEARNED_WORDS_FILENAME = "learned_words.txt";
	static final String TRAINED_WORDS_FILENAME = "learned_words_train.traindata";

	static HashMap<String, String> consonentMap = new HashMap<String, String>();
	static HashMap<String, String> vowelMap = new HashMap<String, String>();
	static HashMap<String, String> signsMap = new HashMap<String, String>();
	static HashMap<String, String> signsTwoMap = new HashMap<String, String>();
	static HashMap<String, String> wordsMap = new HashMap<String, String>();
	static HashMap<String, Integer> learnedWordsMap = new HashMap<String, Integer>();

	public AssetExtraction(Context context) {
		this.context = context;
		dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/MR/Voices";
		String parentDirPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/MR";
		File parentDir = new File(parentDirPath);
		if (parentDir.exists()) {
			parentDir.delete();
		}
	}

	@Override
	protected void onPreExecute() {
		// mProgress.setVisibility(View.VISIBLE);

		dialog = new ProgressDialog(context);

		dialog.setIndeterminate(true);

		dialog.setMessage("Initialising..");

		dialog.show();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Boolean doInBackground(Integer... params) {

		assetManager = context.getAssets();
		InputStream in = null;
		FileOutputStream out = null;
		try {
			String[] files = assetManager.list("");
			for (String f : files) {

				if (f.endsWith(".mp3") || f.endsWith(".wav")
						|| f.endsWith(".txt")) {
					in = assetManager.open(f);
					File outFile = new File(dirPath + "/" + f);
					Log.d("[out_file_paths]:", outFile.getAbsolutePath());
					out = new FileOutputStream(outFile);
					copyFile(in, out);
				}

			}

			files = assetManager.list("words");
			for (String f : files) {

				if (f.endsWith(".mp3") || f.endsWith(".wav")
						|| f.endsWith(".txt")) {
					in = assetManager.open("words/" + f);
					File outFile = new File(dirPath + "/words/" + f);
					File parentDir = new File(outFile.getParent());
					if (!parentDir.exists()) {
						parentDir.mkdirs();
					}
					Log.d("[out_file_paths]:", outFile.getAbsolutePath());
					out = new FileOutputStream(outFile);
					copyFile(in, out);
				}

			}
			preferences = context.getSharedPreferences(Globals.PREFERENCE_NAME,
					Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(Globals.VOICE_DOWNLOADED, true);
			editor.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// NOOP
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// NOOP
				}
			}
		}
		try {

			File file = new File(dirPath);
			if (!file.exists()) {
				file.mkdirs();
			}

			consonentFile = new File(dirPath + "/" + CONSONENTS_FILENAME);
			fileReader = new FileReader(consonentFile);
			reader = new BufferedReader(fileReader);
			String line = "";
			while ((line = reader.readLine()) != null) {
				String[] split = line.split("-");
				consonentMap.put(split[1], split[0]);
			}
			reader.close();
			fileReader.close();

			vowelFile = new File(dirPath + "/" + VOWELS_FILENAME);
			fileReader = new FileReader(vowelFile);
			reader = new BufferedReader(fileReader);
			String line2 = "";
			while ((line2 = reader.readLine()) != null) {
				String[] split = line2.split("-");
				vowelMap.put(split[1], split[0]);
			}
			reader.close();
			fileReader.close();

			signsFile = new File(dirPath + "/" + SIGNS_FILENAME);
			fileReader = new FileReader(signsFile);
			reader = new BufferedReader(fileReader);
			String line3 = "";
			while ((line3 = reader.readLine()) != null) {
				String[] split = line3.split("-");
				signsMap.put(split[1], split[0]);
			}
			reader.close();
			fileReader.close();

			signsTwoFile = new File(dirPath + "/" + SIGNSTWO_FILENAME);
			fileReader = new FileReader(signsTwoFile);
			reader = new BufferedReader(fileReader);
			String line4 = "";
			while ((line4 = reader.readLine()) != null) {
				String[] split = line4.split("-");
				signsTwoMap.put(split[1], split[0]);
			}
			reader.close();
			fileReader.close();

			wordsFile = new File(dirPath + "/words/" + WORDS_FILENAME);
			fileReader = new FileReader(wordsFile);
			reader = new BufferedReader(fileReader);
			String line5 = "";
			while ((line5 = reader.readLine()) != null) {
				String[] split = line5.split("-");
				wordsMap.put(split[1], split[0]);
				// Log.d("[word:]", split[0] + "-" + split[1]);
			}
			reader.close();
			fileReader.close();

			wordsFile = new File(dirPath + "/words/" + LEARNED_WORDS_FILENAME);
			File trainFile = new File(dirPath + "/words/"
					+ TRAINED_WORDS_FILENAME);
			if (trainFile.exists()) {

				ObjectInputStream inputStream = new ObjectInputStream(
						new FileInputStream(trainFile));
				try {
					learnedWordsMap = (HashMap<String, Integer>) inputStream
							.readObject();

					inputStream.close();

					ReadContent.setLearnedWordsMap(learnedWordsMap);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}

			} else {
				trainFile.createNewFile();
				learnedWordsFile = new File(dirPath + "/words/"
						+ LEARNED_WORDS_FILENAME);
				fileReader = new FileReader(learnedWordsFile);
				reader = new BufferedReader(fileReader);
				String line6 = "";
				while ((line6 = reader.readLine()) != null) {
					learnedWordsMap.put(line6, 0);
					// Log.d("[word:]", split[0] + "-" + split[1]);
				}
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(
						new FileOutputStream(trainFile));
				objectOutputStream.writeObject(learnedWordsMap);
				objectOutputStream.close();
				reader.close();
				fileReader.close();
				ReadContent.setLearnedWordsMap(learnedWordsMap);
			}

			ReadContent.setConsonentMap(consonentMap);
			ReadContent.setSignsMap(signsMap);
			ReadContent.setSignsTwoMap(signsTwoMap);
			ReadContent.setVowelMap(vowelMap);
			ReadContent.setWordsMap(wordsMap);

			Log.d("[consonent-map-size]", consonentMap.size() + "");
			Log.d("[vowel-map-size]", vowelMap.size() + "");
			Log.d("[signs-map-size]", signsMap.size() + "");
			Log.d("[signsTwo-map-size]", signsTwoMap.size() + "");
			Log.d("[words-map-size]", wordsMap.size() + "");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {

		dialog.cancel();

		if (result) {

			Toast.makeText(context, "Loading completed", Toast.LENGTH_LONG)
					.show();

		}
	}
}