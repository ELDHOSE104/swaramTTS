package com.example.swaram;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ViewBookMarks extends Activity {

	SharedPreferences preferences;
	ListView bookmarksList;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_book_marks);

		bookmarksList = (ListView) findViewById(R.id.listViewViewBookmarks);

		preferences = getSharedPreferences(Globals.PREFERENCE_NAME,
				MODE_PRIVATE);

		HashMap<String, String> map = (HashMap<String, String>) preferences
				.getAll();

		ArrayList<String> emptylist = new ArrayList<String>();
		emptylist.add("NO BOOKMARKS");
		if (map.isEmpty() || map == null) {
			Log.d("[adapter]:", "preference null");
			bookmarksList.setAdapter(new ArrayAdapter<String>(
					ViewBookMarks.this, android.R.layout.simple_list_item_1,
					emptylist));
		} else {
			ArrayList<BookMarkBean> list = new ArrayList<BookMarkBean>();
			for (String title : map.keySet()) {

				if (title.equals(Globals.VOICE_DOWNLOADED)) {

				} else {
					BookMarkBean bean = new BookMarkBean();
					bean.setTitle(title);
					list.add(bean);
				}
			}
			CustomViewBookmarksList adapter = new CustomViewBookmarksList(
					ViewBookMarks.this, R.layout.custom_view_bookmarks_list,
					list);
			bookmarksList.setAdapter(adapter);
		}
	}
}
