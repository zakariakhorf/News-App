package com.example.android.guardiannews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class NewsAdapter extends ArrayAdapter<NewsItem> {

    /**
     * Member Variables
     */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * Constructor
     */
    public NewsAdapter(Context context, List<NewsItem> newsList) {
        super(context, 0, newsList);
    }

    /**
     * Overridden methods
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }

        NewsItem currentItem = getItem(position);

        TextView titleTV = convertView.findViewById(R.id.list_item_title);
        TextView sectionTV = convertView.findViewById(R.id.list_item_section_name);
        TextView authorTV = convertView.findViewById(R.id.list_item_author_name);
        TextView dateTV = convertView.findViewById(R.id.list_item_date);

        titleTV.setText(currentItem.getTitle());
        sectionTV.setText(currentItem.getSection());
        authorTV.setText(currentItem.getAuthor());
        dateTV.setText(formatDate(currentItem.getPubDate()));

        return convertView;
    }

    /**
     * Other methods
     */
    private String formatDate(String str) {
        String[] parts = str.split("T");
        return parts[0];
    }
}
