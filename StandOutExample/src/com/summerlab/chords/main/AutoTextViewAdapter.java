package com.summerlab.chords.main;

import java.util.ArrayList;

import com.summerlab.chords.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

public class AutoTextViewAdapter extends ArrayAdapter<String> {

	private Activity context;
	private int resourceId;
	private ArrayList<String> items;
	private ArrayList<String> suggestions;
	private DataBaseHelper dataBaseHelper;
	private final String signTextSong = "[S]";
	private final String signTextArtist = "[A]";

	public AutoTextViewAdapter(Activity context, int resource,
			ArrayList<String> objects, DataBaseHelper dbBaseHelper) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.resourceId = resource;
		this.items = objects;
		this.dataBaseHelper = dbBaseHelper;
		suggestions = new ArrayList<String>();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = context.getLayoutInflater().inflate(resourceId, null);
		}

		String result = items.get(position);
		if (result != null) {
			TextView txtSearchResult = (TextView) convertView
					.findViewById(R.id.search_result);
			if (txtSearchResult != null) {
				txtSearchResult.setText(result.substring(3));
			}
			ImageView imageView = (ImageView) convertView
					.findViewById(R.id.imageResult);
			if (imageView != null) {
				if (result.contains(signTextSong)) {
					imageView.setBackgroundResource(R.drawable.song);
				} else if (result.contains(signTextArtist)) {
					imageView.setBackgroundResource(R.drawable.artist);
				}
			}
		}
		return convertView;
	}

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return nameFilter;
	}

	Filter nameFilter = new Filter() {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			// TODO Auto-generated method stub
			if (constraint != null) {
				suggestions.clear();
				suggestions = dataBaseHelper
						.searchFunctionI((String) constraint);
				FilterResults filterResults = new FilterResults();
				filterResults.values = suggestions;
				filterResults.count = suggestions.size();
				System.out.println("" + suggestions.size());
				return filterResults;
			} else {
				return new FilterResults();
			}
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			ArrayList<String> filteredList;
			try {
				filteredList = (ArrayList<String>) results.values;
			} catch (Exception e) {
				// TODO: handle exception
				return;
			}
			if (results != null && results.count > 0) {
				clear();
				for (String c : filteredList) {
					add(c);
				}
				notifyDataSetChanged();
			}
		}

	};

}
