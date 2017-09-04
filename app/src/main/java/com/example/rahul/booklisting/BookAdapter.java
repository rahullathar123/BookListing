package com.example.rahul.booklisting;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by rahul on 2017-09-02.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    private static final String LOG_TAG = BookAdapter.class.getSimpleName();

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context The current context. Used to inflate the layout file.
     */
    public BookAdapter(Activity context) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The position in the list of data that should be displayed in the
     *                    list item view.
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main2, parent, false);
        }

        // Get the {@link Book} object located at this position in the list
        Book currentBook = getItem(position);

        // Find the TextView in the activity_main2.xml layout with the ID title
        TextView nameBook = (TextView) listItemView.findViewById(R.id.title);
        // Get the version name from the current Book object and
        // set this text on the name TextView
        nameBook.setText(currentBook.getTitle());

        // Find the TextView in the activity_main2.xml layout with the ID author
        TextView numberTextView = (TextView) listItemView.findViewById(R.id.author);
        // Get the version number from the current Book object and
        // set this text on the number TextView
        numberTextView.setText(currentBook.getAuthor());

        return listItemView;
    }
}