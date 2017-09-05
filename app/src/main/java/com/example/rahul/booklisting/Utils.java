package com.example.rahul.booklisting;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahul on 2017-09-03.
 */

public final class Utils {

    /** Sample JSON response for a USGS query */
    /**
     * Create a private constructor because no one should ever create a {@link Utils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    public static String LOG_TAG = Utils.class.getSimpleName();
    static String useURL = "https://www.googleapis.com/books/v1/volumes?q=search+";

    public static List<Book> fetchBook(String enterText) {

        URL url = createURL(enterText);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        List<Book> book = extractBook(jsonResponse);

        return book;
    }

    private static URL createURL(String enterText) {

        URL url = null;
        String modifiedURL = enterText.trim().replaceAll("\\s+", "+");
        try {
            url = new URL(useURL + modifiedURL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error during creating URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        int timeout = 15000;
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(timeout);
            urlConnection.setConnectTimeout(timeout);
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {

                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Books JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing a JSON response.
     *
     * @param jsonResponse
     */
    public static List<Book> extractBook(String jsonResponse) {

        // Create an empty ArrayList that we can start adding earthquakes to
        List<Book> bookList = new ArrayList<>();
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }


        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs
        try {
            JSONObject root = new JSONObject(jsonResponse);

            int count = root.getInt("totalItems");
            if (count == 0) {  //if total items is 0 means no book found
                return null;
            }

            JSONArray itemsArray = root.getJSONArray("items");
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject currentBook = itemsArray.getJSONObject(i);
                JSONObject volumeBook = currentBook.getJSONObject("volumeInfo");
                String bookTitle = volumeBook.getString("title");

                JSONArray authorArray = null;
                try {
                    authorArray = volumeBook.getJSONArray("authors");
                } catch (JSONException ignored) {

                }
                String authors = extractAuthors(authorArray);
                Book books = new Book(bookTitle, authors);
                bookList.add(books);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("Utils", "Problem parsing the book JSON results", e);
        }

        // Return the list of earthquakes
        return bookList;
    }

    private static String extractAuthors(JSONArray array) throws JSONException {
        String authorsList = null;
        if (array.length() == 0)
            authorsList = "no authors were found";

        for (int i = 0; i < array.length(); i++) {

            if (i == 0)
                authorsList = "-" + array.getString(0);
            else
                authorsList = authorsList + "," + array.getString(i);
        }
        return authorsList;
    }
}

