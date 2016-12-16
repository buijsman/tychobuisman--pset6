package tycho.your_own;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Read extends AppCompatActivity {

    // defining
    private ArrayList<String> titles = new ArrayList<>();

    private MyCustomAdapter adapter;

    private ArrayList<String> keys = new ArrayList<>();

    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        // set list view
        ListView lv = (ListView)findViewById(R.id.list4);
        adapter = new MyCustomAdapter(titles, getApplicationContext());
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // retrieve user ID
        SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uid = sharedpreferences.getString("userID", "");

        // get the reference to the database for the  users ToRead items
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userRef = database.getReference(uid);
        userRef.child("Read");

        // retrieve all childs from database
        userRef.child("Read").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // Add book to the listview
                String value = dataSnapshot.getValue(String.class);
                titles.add(value);
                adapter.notifyDataSetChanged();

                // retrieve the keys
                String key = dataSnapshot.getKey();
                keys.add(key);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Onlongclick delete the child from the database
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                // remove book from the database and listview
                String _id = keys.get(position);
                userRef.child("Read").child(_id).setValue(null);
                titles.remove(position);
                adapter.notifyDataSetChanged();
                Toast toast = Toast.makeText(getApplicationContext(), "Book Deleted", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToBookDetails = new Intent(getApplicationContext(), book_details.class);
                goToBookDetails.putExtra("book", titles.get(position));//bundle doorgeven aan intent
                startActivity(goToBookDetails);
            }
        });

    }
}
