package com.example.swaram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.googlecode.tesseract.android.TessBaseAPI;

public class Ocr extends Activity {

	ImageView mImageView;
	EditText editText1;
	Bitmap imageBitmap;
	ProgressDialog dialog;
	String rootPath;
	TextToSpeech speech;
	private static final int BUFFER_SIZE = 4096;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ocr);

		editText1 = (EditText) findViewById(R.id.editText1);
		mImageView = (ImageView) findViewById(R.id.imageView1);

		OcrTask ocrTask = new OcrTask();
		ocrTask.execute();

		Button buttonTranslate = (Button) findViewById(R.id.buttonTranslate);

		Button buttonRead = (Button) findViewById(R.id.buttonRead);

		buttonRead.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				speech = new TextToSpeech(Ocr.this, new OnInitListener() {

					@Override
					public void onInit(int status) {
						speech.speak(editText1.getText().toString(), 0, null);
					}
				});

			}
		});

		buttonTranslate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TranslateTask translateTask = new TranslateTask();
				translateTask.execute();
			}
		});

	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	class OcrTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(Ocr.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Reading..");
			dialog.show();
			Bundle bundle = getIntent().getExtras();
			imageBitmap = (Bitmap) bundle.get("data");
			mImageView.setImageBitmap(imageBitmap);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {

			rootPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			final String dirPath = rootPath + "/MR/Images/tessdata";
			File file = new File(dirPath);
			if (!file.exists()) {
				file.mkdirs();
			}

			try {
				InputStream in = null;
				FileOutputStream out = null;
				AssetManager assetManager = getAssets();
				String files[] = assetManager.list("tesseract-ocr/tessdata");
				for (String eachpath : files) {
					Log.d("[tess_paths]:", eachpath);
					File f = new File(eachpath);
					in = assetManager.open("tesseract-ocr/tessdata/" + f);
					File outFile = new File(dirPath + "/" + f);
					out = new FileOutputStream(outFile);
					copyFile(in, out);

				}

				TessBaseAPI baseApi = new TessBaseAPI();
				String DATA_PATH = rootPath + "/MR/Images";
				baseApi.init(DATA_PATH, "eng");
				baseApi.setImage(imageBitmap);
				String recognizedText = baseApi.getUTF8Text();
				baseApi.end();
				return recognizedText;

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "";

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			Log.d("[recognised_text]:", result);
			dialog.dismiss();
			editText1.setText(result);
			super.onPostExecute(result);
		}

	}

	class TranslateTask extends AsyncTask<Void, Void, String> {

		String path = "";

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(Ocr.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Translating..");
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			String word = editText1.getText().toString();

			word = word.replaceAll(" ", "%20");
			try {
				URL urlLink = new URL(
						"http://translate.google.co.uk/translate_a/t?client=webapp&sl=en&tl=ml&hl=en&sc=1&q="
								+ word);

				HttpURLConnection httpConn = (HttpURLConnection) urlLink
						.openConnection();
				int responseCode = httpConn.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					// String fileName = "";
					// String disposition = httpConn
					// .getHeaderField("Content-Disposition");
					// String contentType = httpConn.getContentType();
					// int contentLength = httpConn.getContentLength();

					path = rootPath + "/MR/Images/f.txt";
					// File tempFile = new File(path);
					// if (disposition != null) {
					// // extracts file name from header field
					// int index = disposition.indexOf("filename=");
					// if (index > 0) {
					// fileName = disposition.substring(index + 10,
					// disposition.length() - 1);
					// }
					// } else {
					// // extracts file name from URL
					// // fileName = fileURL.substring(fileURL.lastIndexOf("/")
					// // + 1,
					// // fileURL.length());
					// path = rootPath + "/MR/Images/f.txt";
					//
					// }

					InputStream inputStream = httpConn.getInputStream();

					// opens an output stream to save into file
					FileOutputStream outputStream = new FileOutputStream(path);

					int bytesRead = -1;
					byte[] buffer = new byte[BUFFER_SIZE];
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, bytesRead);
					}

					outputStream.close();
					inputStream.close();

					System.out.println("File downloaded");
					// //////////////////////

					//
					// BufferedReader in = new BufferedReader(new
					// InputStreamReader(
					// urlLink.openStream()));
					//
					// String inputLine;
					//
					// while ((inputLine = in.readLine()) != null) {
					// builder.append(inputLine);
					// }
					//
					// in.close();
					// /////////////////////////
					// String path = rootPath + "/MR/Images/f.txt";
					// File tempFile = new File(path);
					// if (tempFile.exists()) {
					// tempFile.delete();
					// }
					// tempFile.createNewFile();
					//
					// URLConnection connection = urlLink.openConnection();
					// connection.connect();
					// int lenghtOfFile = connection.getContentLength();
					// Log.d("[length]", "Lenght of file: " + lenghtOfFile);
					// InputStream input = new BufferedInputStream(
					// urlLink.openStream());
					// OutputStream output = new FileOutputStream(path);
					// byte data[] = new byte[1024];
					// long total = 0;
					// int count = 0;
					// while ((count = input.read(data)) != -1) {
					// total += count;
					// output.write(data, 0, count);
					// }
					// output.flush();
					// output.close();
					// input.close();
					//

					StringBuilder builder = new StringBuilder();
					FileReader fileReader = new FileReader(new File(path));
					BufferedReader br = new BufferedReader(fileReader);
					String text = "";
					while ((text = br.readLine()) != null) {
						builder.append(text);
					}
					JSONObject reader = new JSONObject(builder.toString());
					JSONArray array = reader.getJSONArray("sentences");
					JSONObject obj = array.getJSONObject(0);
					String read = obj.get("trans").toString();

					Log.d("[content]:", read);
					return read;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			Intent intent = new Intent(Ocr.this, ReadContent.class);
			intent.putExtra(Globals.CONTENT, result);
			startActivity(intent);
			// Typeface font = Typeface.createFromAsset(getAssets(),
			// "AnjaliOldLipi-0.730.ttf");
			// editText1.setTypeface(font);
			// editText1.setText(result);
			super.onPostExecute(result);
		}
	}
}
