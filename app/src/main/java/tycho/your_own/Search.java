package tycho.your_own;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class Search extends AppCompatActivity {

    private EditText text;

    private Button searchBtn;

    private MyCustomAdapter adapter;

    private ArrayList<String> titles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        text = (EditText)findViewById(R.id.editText);

        searchBtn = (Button)findViewById(R.id.button2);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }

            @Override
            public void onClick(View v) {
                String searchItem = text.getText().toString();
                searchItem = searchItem.replace(" ", "+");
                GetURL geturl = new GetURL();
                geturl.execute(searchItem);

                ListView lv = (ListView)findViewById(R.id.list2);
                adapter = new MyCustomAdapter(titles, getApplicationContext());
                adapter.notifyDataSetChanged();
                lv.setAdapter(adapter);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("Books");

                        String book = titles.get(position);
                        myRef.setValue(book);
                        Toast toast = Toast.makeText(getApplicationContext(), "Book Added To Read List", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

            }
        });

    }

    class GetURL extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {

            ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar2);
            progress.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... params) {
            String url1 = "https://www.googleapis.com/books/v1/volumes?q=";
            String url2 = params[0];
            String url3 = "&projection=lite";
            String FullUrl = url1 + url2 + url3;
            String result = "";
            try {
                URL url = new URL(FullUrl);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.setRequestProperty("Content-length", "0");

                httpConnection.connect();

                int responseCode = httpConnection.getResponseCode();

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
            ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar2);
            progress.setVisibility(View.INVISIBLE);

            String items = "";
            try {
                JSONObject jObject = new JSONObject(result);
                items = jObject.getString("items");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for(Integer i = 0; i < 10; i++){

                String array = "";
                try {
                    JSONArray jsonArray = new JSONArray(items);
                    array = jsonArray.getString(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String volumeinfo = "";
                try {
                    JSONObject jObject = new JSONObject(array);
                    volumeinfo = jObject.getString("volumeInfo");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String title = "";
                try {
                    JSONObject jObject = new JSONObject(volumeinfo);
                    title = jObject.getString("title");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                titles.add(title);
            }

            if(titles.size() == 0){
                Toast toast = Toast.makeText(getApplicationContext(), "Book not found", Toast.LENGTH_SHORT);
                toast.show();
                EditText text = (EditText) findViewById(R.id.editText);
                text.setText("");
            }
        }
    }
}
