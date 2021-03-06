package tycho.your_own;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {

    // defining
    private Button mLogOutBtn;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private ArrayList<String> items = new ArrayList<>();

    private ListView lv;

    private MyCustomAdapter adapter;

    private TextView ReadBooks;

    private ArrayList<String> titles = new ArrayList<>();

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // retrieve used id
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();

            SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();

            // save user id for furhter purpose
            editor.putString("userID", uid);
            editor.commit();
        }

        // populate list view
        items.add("Search");
        items.add("To Read List");
        items.add("Read List");

        lv = (ListView) findViewById(R.id.list);
        adapter = new MyCustomAdapter(items, this);
        adapter.notifyDataSetChanged();
        lv.setAdapter(adapter);

        // on item click go to the assigned activity
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    startActivity(new Intent(getApplicationContext(), Search.class));
                }
                if(position == 1){
                    startActivity(new Intent(getApplicationContext(), ToRead.class));
                }
                if(position == 2){
                    startActivity(new Intent(getApplicationContext(),Read.class));
                }
            }
        });

        // handle log out
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){

                    startActivity(new Intent(AccountActivity.this, MainActivity.class));
                    finish();
                }
            }
        };

        mLogOutBtn = (Button)findViewById(R.id.logoutButton);
        mLogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });

        // retrieve the amount of read books from the database and set the text "you have read ... books!"
        titles.clear();
        // get database reference to read books
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference(uid);

        userRef.child("Read").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // add all books to the array list
                String value = dataSnapshot.getValue(String.class);
                titles.add(value);
                ReadBooks = (TextView)findViewById(R.id.textView2);

                // get the size of the array list and set the text
                int books = titles.size();
                String text = "";
                if(books == 0){
                    text = "You Have Read 0 Books :(";
                }
                if(books == 1){
                    text = "You Have Read 1 Book!";
                }
                if(books > 1){
                    text = "You Have Read " + books + " Books!";
                }
                ReadBooks.setText(text);
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

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }
}
