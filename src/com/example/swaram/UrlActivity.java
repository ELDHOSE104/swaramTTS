package com.example.swaram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class UrlActivity extends Activity {

	Button buttonUrlGo, buttonViewBookmarks;
	EditText editTextUrlLink;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_url);

		buttonUrlGo = (Button) findViewById(R.id.buttonUrlGo);
		buttonViewBookmarks = (Button) findViewById(R.id.buttonViewBookmarks);
		editTextUrlLink = (EditText) findViewById(R.id.editTextUrlLink);
		// editTextUrlLink.setText("http://www.mathrubhumi.com/story.php?id=510204");
		buttonUrlGo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UrlActivity.this, MainActivity.class);
				String urlText = editTextUrlLink.getText().toString();
				intent.putExtra(Globals.EXTRA_LINK, urlText);
				startActivity(intent);
			}
		});

		buttonViewBookmarks.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(UrlActivity.this, ViewBookMarks.class));
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		getMenuInflater().inflate(R.menu.url_activity_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemViewBookMark:
			startActivity(new Intent(UrlActivity.this, ViewBookMarks.class));
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
