package com.yin.lister;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference listRef;
    private SimpleDateFormat dateFormat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        final ListView itemsView = (ListView) findViewById(R.id.items);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        itemsView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText newItem = (EditText) findViewById(R.id.new_item_text);
                Log.i("LISTER:", "Adding [" + newItem.getText().toString() + "] to table");
                addToList(newItem.getText().toString());
                newItem.setText("");
                adapter.add(newItem.getText().toString());
            }
        });

        itemsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Query myQuery = listRef.orderByValue().equalTo((String)
                        itemsView.getItemAtPosition(position));

                myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ListItem item = dataSnapshot.getValue(ListItem.class);
                        Log.i("LISTER:", "Removing [" + item.getItemName() + "] from firebase db");
                        Toast.makeText(getApplicationContext(), "Removing " + item.getItemName(), Toast.LENGTH_LONG).show();
                        adapter.remove(item.getItemName());
                        // remove from firebase
                        listRef.child(item.getItemName()).removeValue();
//                        if (dataSnapshot.hasChildren()) {
//                            DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
//                            Log.i("LISTER:", "Removing [" + firstChild.getValue(ListItem.class).getItemName() + "] from firebase db");
//                            Toast.makeText(getApplicationContext(), "Removing " + firstChild.getValue(ListItem.class).getItemName(), Toast.LENGTH_LONG).show();
//                            firstChild.getRef().removeValue();
//                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                return true;
                }
        });

        // attach listener for changes to list
        listRef = FirebaseDatabase.getInstance().getReference();
        listRef = listRef.getRoot();

        // Add listener to sync the firebase db with the listview
        listRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("LISTER:", "Adding [" + dataSnapshot.getValue(ListItem.class).getItemName() + "] to table due to firebase add");
                Toast.makeText(getApplicationContext(), "Adding " + dataSnapshot.getValue(ListItem.class).getItemName(), Toast.LENGTH_LONG).show();
                adapter.add(dataSnapshot.getValue(ListItem.class).getItemName());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("LISTER:", "Removing [" + dataSnapshot.getValue(ListItem.class).getItemName() + "] from table due to firebase remove");
                String value = dataSnapshot.getValue(ListItem.class).getItemName();
                adapter.remove(value);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.i("TAG:", "Failed to read value.", error.toException());
            }
        });
    }

    /**
     * Adds the string to the Firebase list
     * @param str Value to add to firebase list
     */
    private void addToList(String str) {
        Log.d("LISTER:", "Adding [" + str + "] to list");

        ListItem item = new ListItem();
        item.setItemName(str);
        item.setAddedDate(dateFormat.format(new Date()));
        item.setAddedBy(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        listRef.child(str).setValue(item);
    }

}
