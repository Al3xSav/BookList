package com.example.android.bookapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String GOOGLE_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String ORDER_NEWEST = "&orderBy=newest";
    //private static final String API_KEY = "&key=AIzaSyDywPkt3dZacJouLdSSfe_iOHe6WrwVdWQ";
    ListView list;
    public TextView emptyView;
    private static String urlString;
    //JSON Node Names
    private static final String TAG_TITLE = "title";
    private static final String TAG_AUTHORS = "authors";
    private static final String TAG_URL = "url";

    ArrayList<HashMap<String, String>> bookList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_main);

        ImageButton imageButton = (ImageButton ) findViewById(R.id.search_button);
        assert imageButton != null;
        emptyView = (TextView) findViewById(R.id.empty_view);
        emptyView.setText(getResources().getString(R.string.search_for_books));
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = (EditText) findViewById(R.id.input);
                String inputQuery = input.getText().toString();
                inputQuery = inputQuery.replace(" ","+");
                urlString = GOOGLE_REQUEST_URL + inputQuery + ORDER_NEWEST;
                Log.v("URL:",urlString);
                if (inputQuery != null) {
                    input.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            input.setText("");
                        }
                    });
                }
                new ProcessJSON().execute(urlString);
            }
        });

    }

    private class ProcessJSON extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Loading Books...", Toast.LENGTH_SHORT).show();
        }

        protected String doInBackground(String... strings) {
            String stream;
            String urlString = strings[0];

            HTTPDataHandler handler = new HTTPDataHandler();
            stream = handler.GetHTTPData(urlString);

            // Return the data from specified url
            return stream;
        }

        protected void onPostExecute(String stream) {
            list = (ListView) findViewById(R.id.list);
            if (stream != null) {
                try {
                    // Get the full HTTP Data as JSONObject
                    JSONObject reader = new JSONObject(stream);
                    int totalItems = reader.getInt("totalItems");
                    if (totalItems == 0){
                        list.setVisibility(View.INVISIBLE);
                        emptyView.setVisibility(View.VISIBLE);
                        emptyView.setText(getResources().getString(R.string.empty));
                    }
                    else {
                        emptyView.setVisibility(View.INVISIBLE);
                        // Get the JSONArray weather
                        JSONArray bookArray = reader.getJSONArray("items");
                        // Get the weather array first JSONObject
                        for (int i = 0; i < bookArray.length(); i++) {

                            JSONObject bookObject = bookArray.getJSONObject(i);
                            JSONObject bookVolumeInfo = bookObject.getJSONObject("volumeInfo");

                            // Get the title from the volume info
                            String bookTitle = bookVolumeInfo.getString("title");
                            // Some books don't have an authors node, use try/catch to prevent null pointers
                            JSONArray bookAuthors = null;
                            try {
                                bookAuthors = bookVolumeInfo.getJSONArray("authors");
                            } catch (JSONException ignored) {
                            }
                            // Convert the authors to a String
                            String bookAuthorsString = "";
                            // If the author is empty, set it as "Unknown"
                            if (bookAuthors == null) {
                                bookAuthorsString = "Unknown";
                            } else {
                                // Format the authors as "author1, author2, and author3"
                                int countAuthors = bookAuthors.length();
                                for (int e = 0; e < countAuthors; e++) {
                                    String author = bookAuthors.getString(e);
                                    if (bookAuthorsString.isEmpty()) {
                                        bookAuthorsString = author;
                                    } else if (e == countAuthors - 1) {
                                        bookAuthorsString = bookAuthorsString + " and " + author;
                                    } else {
                                        bookAuthorsString = bookAuthorsString + ", " + author;
                                    }
                                }
                            }

                            // Image Links
                            JSONObject bookImageLinks = null;
                            try {
                                bookImageLinks = bookVolumeInfo.getJSONObject("imageLinks");
                            } catch (JSONException ignored){
                            }
                            // Convert the image link to a string
                            String bookSmallThumbnail;
                            if ( bookImageLinks == null){
                                bookSmallThumbnail = "null";
                            }else{
                                bookSmallThumbnail  = bookImageLinks.getString("smallThumbnail");
                            }

                            JSONObject bookAccessInfo = bookObject.getJSONObject("accessInfo");
                            String bookUrlLinks = bookAccessInfo.getString("webReaderLink");

                            Log.v(TAG_TITLE, bookTitle);
                            Log.v(TAG_AUTHORS, bookAuthorsString);
                            Log.v(TAG_URL, bookUrlLinks);

                            // Adding value HashMap key => value

                            HashMap<String, String> map = new HashMap<>();
                            map.put(TAG_TITLE, bookTitle);
                            map.put(TAG_AUTHORS, bookAuthorsString);
                            map.put(TAG_URL, bookUrlLinks);

                            bookList.add(map);

                            ListAdapter adapter = new SimpleAdapter(MainActivity.this,
                                    bookList,
                                    R.layout.list,
                                    new String[]{TAG_TITLE, TAG_AUTHORS, TAG_URL},
                                    new int[]{ R.id.bookTitle, R.id.bookAuthors}
                            );

                            list.setAdapter(adapter);
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    String url = bookList.get(+position).get(TAG_URL);
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                }
                            });
                        }
                    }
                    // process other data as this way..............
                }catch (JSONException e) {
                    e.printStackTrace();

                }

            } // if statement end
        } // onPostExecute() end
    } // ProcessJSON class end

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
