package com.example.swaram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends ActionBarActivity {

	ProgressDialog dialog;
	SharedPreferences preferences;
	SharedPreferences.Editor editor;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// action with ID action_refresh was selected
		case R.id.action_settings:
			String webUrl = browser.getUrl();

			ReadTask readTask = new ReadTask(webUrl);
			readTask.execute();

			break;
		case R.id.addBookMark:
			String url = browser.getUrl();
			String title = browser.getTitle();
			editor = preferences.edit();
			editor.putString(title, url);
			editor.commit();
			break;
		case R.id.finishBrowsing:
			finish();
		default:
			break;
		}

		return true;
	}

	@Override
	public void onBackPressed() {

		if (browser.canGoBack()) {
			browser.goBack();

		} else {
			finish();
		}
	}

	WebView browser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
				| ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
		String url = getIntent().getExtras().getString(Globals.EXTRA_LINK);

		if (!url.startsWith("http")) {
			url = "http://" + url;
		}
		preferences = getSharedPreferences(Globals.PREFERENCE_NAME,
				MODE_PRIVATE);

		browser = (WebView) findViewById(R.id.webView1);
		browser.getSettings().setLoadsImagesAutomatically(true);
		browser.getSettings().setBuiltInZoomControls(true);
		browser.getSettings().setJavaScriptEnabled(true);
		browser.loadUrl(url);
		browser.setWebViewClient(new WebClient());

	}

	public class WebClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
//			view.loadUrl(url);
//			return true;
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			Log.d("[page_started]", url);
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			Log.d("[page_finished]", url);
			super.onPageFinished(view, url);
		}

	}

	class ReadTask extends AsyncTask<Void, Void, String> {

		String url;

		public ReadTask(String url) {
			// TODO Auto-generated constructor stub
			this.url = url;
		}

		public String readContent(String url) {

			String text = "";
			StringBuilder builder = new StringBuilder();
			BufferedReader in = null;

			URL urlLink;
			try {
				urlLink = new URL(url);
				in = new BufferedReader(new InputStreamReader(
						urlLink.openStream()));
				String inputLine;

				while ((inputLine = in.readLine()) != null) {
					builder.append(inputLine);
				}
				text = Jsoup.parse(builder.toString()).text();

				in.close();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return text;
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub

			return this.readContent(url);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dialog = new ProgressDialog(MainActivity.this);
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.setTitle("Reading");
			dialog.setMessage("Please wait..");
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			dialog.dismiss();
			if (result != null) {
				Intent intent = new Intent(MainActivity.this, ReadContent.class);
				intent.putExtra("content", result);
				startActivity(intent);
			} else {
				Toast.makeText(MainActivity.this,
						"Reading returned null value", Toast.LENGTH_LONG)
						.show();
			}
			super.onPostExecute(result);
		}

	}

}
