package com.example.rahul.booklisting;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button findButton;
    TextView notFound;
    EditText textEntered;
    ProgressBar mLoader;
    BookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoader = (ProgressBar) findViewById(R.id.progress_bar);
        textEntered = (EditText) findViewById(R.id.enter_text);
        notFound = (TextView) findViewById(R.id.no_book_found);
        findButton = (Button) findViewById(R.id.search_button);


        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // hiding the soft keyboard
                View v = getCurrentFocus();
                // Check if no view has focus:
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                adapter.clear();

                if (textEntered.getText().toString().trim().matches("")) { //if search box is empty and button is clicked
                    notFound.setVisibility(View.VISIBLE);
                    notFound.setText("Please Enter Something in Search Box");
                } else {
                    if (isNetworkConnected()) {                     //if search box is NOT empty and network is connected
                        BookAsyncTask task = new BookAsyncTask();
                        task.execute();
                    } else {                                        ////if search box is NOT empty and network is disconnected
                        notFound.setVisibility(View.VISIBLE);
                        notFound.setText("Not connected to Internet");
                    }
                }
            }
        });

        ListView bookListView = (ListView) findViewById(R.id.listView);
        adapter = new BookAdapter(this);
        bookListView.setAdapter(adapter);


    }

    public boolean isNetworkConnected() { //check network connection
        ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) //IMPORTANT as u can't call info.isConnected() if info is NULL. Will throw null point exception. So first check it's null or not
            return true;
        else
            return false;
    }

    //get the text which the user entered in the search bar
    public String getText() {
        return textEntered.getText().toString();
    }


    private class BookAsyncTask extends AsyncTask<Void, Void, List<Book>> {

        @Override
        protected void onPreExecute() {
            notFound.setVisibility(View.INVISIBLE);  // display the progress bar
            mLoader.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Book> doInBackground(Void... voids) {
            List<Book> result = Utils.fetchBook(getText());
            return result;

        }

        @Override
        protected void onPostExecute(List<Book> books) {
            mLoader.setVisibility(View.INVISIBLE);   //hide the progress bar

            if (books == null) {
                notFound.setVisibility(View.VISIBLE);
            } else {
                notFound.setVisibility(View.GONE);
                adapter.addAll(books);
            }
        }
    }
}

