package com.example.swaram;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

@SuppressLint("ViewHolder")
public class CustomViewBookmarksList extends ArrayAdapter<BookMarkBean> {

	SharedPreferences preferences;
	ArrayList<BookMarkBean> beans;
	Context context;

	public CustomViewBookmarksList(Context context, int resource,
			ArrayList<BookMarkBean> list) {
		super(context, R.layout.custom_view_bookmarks_list, list);

		this.beans = list;
		this.context = context;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		preferences = context.getSharedPreferences(Globals.PREFERENCE_NAME,
				Context.MODE_PRIVATE);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.custom_view_bookmarks_list,
				parent, false);
		final TextView textviewTitle = (TextView) rowView
				.findViewById(R.id.textViewBookmarksTitle);

		textviewTitle.setText(beans.get(position).getTitle());

		rowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, MainActivity.class);
				intent.putExtra(Globals.EXTRA_LINK, preferences.getString(
						textviewTitle.getText().toString(),
						"http://www.google.com"));
				context.startActivity(intent);

			}
		});
		return rowView;
	}
}
