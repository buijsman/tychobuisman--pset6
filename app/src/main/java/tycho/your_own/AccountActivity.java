package tycho.your_own;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {
    private Button mLogOutBtn;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private ArrayList<String> items = new ArrayList<>();

    private ListView lv;

    private MyCustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // populate list view
        items.add("Search");
        items.add("To Read List");
        items.add("Read List");

        lv = (ListView) findViewById(R.id.list);
        adapter = new MyCustomAdapter(items, this);
        adapter.notifyDataSetChanged();
        lv.setAdapter(adapter);

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
                    //startActivity(new Intent(getApplicationContext(),ReadBooks.class));
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

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }
}
