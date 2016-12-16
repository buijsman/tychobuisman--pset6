package tycho.your_own;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {

    // defining
    private EditText text;

    private Button searchBtn;

    private SecondCustomAdapter adapter;

    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> authors = new ArrayList<String>();
    private ArrayList<String> descriptions = new ArrayList<String>();

    private FirebaseDatabase database;

    private DatabaseReference userRef;

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        lv = (ListView)findViewById(R.id.list2);

        // populate list view
        adapter = new SecondCustomAdapter(titles, authors, descriptions, getApplicationContext());
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // retrieve user ID
        SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uid = sharedpreferences.getString("userID", "");

        // get the reference to the database for the  users ToRead items
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference(uid);
        userRef.child("ToRead");

        text = (EditText)findViewById(R.id.editText);

        searchBtn = (Button)findViewById(R.id.button2);

        // On click search for the books with the search item given by the user
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }

            @Override
            public void onClick(View v) {
                // empty the listview (for a second search)
                titles.clear();
                authors.clear();
                descriptions.clear();

                // hide keyboard after search
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(text.getWindowToken(), 0);

                // get the search item and search for it with the google books api
                String searchItem = text.getText().toString();
                searchItem = searchItem.replace(" ", "+");
                GetURL geturl = new GetURL();
                geturl.execute(searchItem);

                // on item click add the book to the database
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        addToDatabase(position);
                    }
                });

            }
        });

    }

    public void addToDatabase(int position){

        // Add the book to the database
        String book = titles.get(position);
        userRef.child("ToRead").push().setValue(book);
        Toast toast = Toast.makeText(getApplicationContext(), "Book Added To Your To Read List", Toast.LENGTH_SHORT);
        toast.show();
    }

    class GetURL extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {

            // make progressbar visible when searching
            ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar2);
            progress.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... params) {
            // make the url
            String url1 = "https://www.googleapis.com/books/v1/volumes?q=";
            String url2 = params[0];
            String url3 = "&projection=lite";
            String FullUrl = url1 + url2 + url3;
            String result = "";
            try {
                // set up connection
                URL url = new URL(FullUrl);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.setRequestProperty("Content-length", "0");

                httpConnection.connect();

                int responseCode = httpConnection.getResponseCode();

                // response code is positive add the data to a String
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                    result = sb.toString();
                }
                // if response code is negative, toast
                else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Could Not Retrieve Data", Toast.LENGTH_SHORT);
                    toast.show();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        protected void onPostExecute(String result) {
            // set progressbar invisible when searching is done
            ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar2);
            progress.setVisibility(View.INVISIBLE);

            // get the items from the result
            String items = "";
            try {
                JSONObject jObject = new JSONObject(result);
                items = jObject.getString("items");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Retrieve up to 10 books from the items
            for(Integer i = 0; i < 10; i++){

                // get one book
                String array = "";
                try {
                    JSONArray jsonArray = new JSONArray(items);
                    array = jsonArray.getString(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // retrieve volumeinfo from the book
                String volumeinfo = "";
                try {
                    JSONObject jObject = new JSONObject(array);
                    volumeinfo = jObject.getString("volumeInfo");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // retrieve title and description and authors array from volumeinfo
                String title = "";
                String authorsArray = "";
                String description = "";
                try {
                    JSONObject jObject = new JSONObject(volumeinfo);
                    title = jObject.getString("title");
                    authorsArray = jObject.getString("authors");
                    description = jObject.getString("description");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // retrieve the author from the authors array
                String author = "";
                try {
                    JSONArray jsonArray = new JSONArray(authorsArray);
                    author = jsonArray.getString(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // add the info about the book to the array lists
                titles.add(title);
                descriptions.add(description);
                authors.add(author);

            }

            // populate list view+
            adapter = new SecondCustomAdapter(titles, authors, descriptions, getApplicationContext());
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            // if the result is empty, toast
            if(titles.size() == 0){
                Toast toast = Toast.makeText(getApplicationContext(), "Book not found", Toast.LENGTH_SHORT);
                toast.show();
                EditText text = (EditText) findViewById(R.id.editText);
                text.setText("");
            }
        }
    }
}
