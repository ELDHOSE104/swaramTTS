package com.example.swaram;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Home extends Activity {

	Button buttonFeed, buttonFiles, buttonReadFromImage;
	SharedPreferences preferences;
	int REQUEST_IMAGE_CAPTURE = 11;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		ArrayList<String> list = new ArrayList<String>();
		String rootPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		// String inDirPath = rootPath + "/MR/Voices/story2.txt";
		// String outDirPath = rootPath + "/MR/Voices/one.txt";
		// String text = Globals.readFromFile(inDirPath);
		// text = text.replace(".", " ");
		// text = text.replace(",", " ");
		// text = text.replace("  ", " ");
		//
		// String[] texts = text.split(" ");
		//
		// FileWriter writer;
		// try {
		// File file = new File(outDirPath);
		// if (!file.exists()) {
		// try {
		// file.createNewFile();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// writer = new FileWriter(file);
		// for (String str : texts) {
		// if (!list.contains(str)) {
		// list.add(str);
		// }
		// }
		// for (int i = 0; i < list.size(); i++) {
		// Log.d("[str]:", list.get(i));
		// writer.write(i + "-" + list.get(i));
		// writer.write("\n\r");
		// writer.flush();
		// }
		// Log.d("[out_file]:", file.getAbsolutePath());
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		preferences = getSharedPreferences(Globals.PREFERENCE_NAME,
				MODE_PRIVATE);
//		boolean voiceDownloaded = preferences.getBoolean(
//				Globals.VOICE_DOWNLOADED, false);
//		if (!voiceDownloaded) {
//			AssetExtraction assetExtraction = new AssetExtraction(Home.this);
//			assetExtraction.execute();
//
//		}
		buttonFeed = (Button) findViewById(R.id.buttonMyFeeds);
		buttonFiles = (Button) findViewById(R.id.buttonMyFiles);
		buttonReadFromImage = (Button) findViewById(R.id.buttonReadFromImage);

		buttonReadFromImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent takePictureIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

			}
		});

		buttonFeed.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						UrlActivity.class));

			}
		});

		buttonFiles.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						ReadContent.class);

				intent.putExtra(Globals.CONTENT, "");
				startActivity(intent);

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			Intent intent = new Intent(Home.this, Ocr.class);
			intent.putExtras(extras);
			startActivity(intent);
			
		}
	}
}
