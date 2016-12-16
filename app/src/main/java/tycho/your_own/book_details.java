package tycho.your_own;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class book_details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        String searchItem = getIntent().getExtras().getString("book");
        searchItem = searchItem.replace(" ", "+");
        GetURL geturl = new GetURL();
        geturl.execute(searchItem);
    }

    class GetURL extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {

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
            // get the items from the result
            String items = "";
            try {
                JSONObject jObject = new JSONObject(result);
                items = jObject.getString("items");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // get one book
            String array = "";
            try {
                JSONArray jsonArray = new JSONArray(items);
                array = jsonArray.getString(0);
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
            TextView Title = (TextView)findViewById(R.id.Title);
            Title.setText(title);
            TextView Author = (TextView)findViewById(R.id.Author);
            Author.setText(author);
            TextView Description = (TextView)findViewById(R.id.Description);
            Description.setText(description);

            // if the result is empty, toast
            if(title == ""){
                Toast toast = Toast.makeText(getApplicationContext(), "Book not found", Toast.LENGTH_SHORT);
                toast.show();
                EditText text = (EditText) findViewById(R.id.editText);
                text.setText("");
            }

            }

        }
    }
