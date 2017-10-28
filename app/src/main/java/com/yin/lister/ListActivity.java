package com.yin.lister;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
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
import com.yin.lister.obj.List;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ListActivity extends AppCompatActivity {
    private DatabaseReference listRef;
    private SimpleDateFormat dateFormat;
    private final TaskStackBuilder taskStack = TaskStackBuilder.create(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        final ListView listsView = (ListView) findViewById(R.id.items);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        listsView.setAdapter(adapter);

        /**
         * On clicking the FAB, create a new list
         */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText newItem = (EditText) findViewById(R.id.new_item_text);
                if (newItem.getText() != null && !"".equals(newItem.getText().toString().trim())) {
                    Log.d("LISTER:", "Adding list named [" + newItem.getText().toString() + "] due to manual add");
                    addToList(newItem.getText().toString());
                }
                newItem.setText("");
            }
        });

        /**
         * On click, open the list in the ListItem activity
         */
        listsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                Query myQuery = listRef.orderByKey().equalTo((String)
                        listsView.getItemAtPosition(position));

                final int pos = position;

                myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                            List list = firstChild.getValue(List.class);
                            Log.d("LISTER:", "Opening list named [" + list.getListName() + "] from firebase db due to click");

                            Intent itemIntent = new Intent(ListActivity.this, ListItemActivity.class);
                            itemIntent.putExtra("listName", list.getListName());

                            // Use TaskStackBuilder to build the back stack and get the PendingIntent
                            taskStack.addNextIntentWithParentStack(itemIntent);

                            startActivity(itemIntent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }
        });

        /**
         * On long click, delete the list
         */
        listsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                Query myQuery = listRef.orderByKey().equalTo((String)
                        listsView.getItemAtPosition(position));

                final int pos = position;

                myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                            List list = firstChild.getValue(List.class);
                            // TODO: Confirmation with user that they want to delete this list

                            Log.d("LISTER:", "Removing list named [" + list.getListName() + "] from firebase db due to long click");
                            firstChild.getRef().removeValue();
                        }
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

        /**
         * Add listener to sync the firebase db with the listview
          */
        listRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("LISTER:", "Adding [" + dataSnapshot.getValue(List.class).getListName() + "] to table due to firebase add");
                adapter.add(dataSnapshot.getValue(List.class).getListName());
                Toast.makeText(getApplicationContext(), "Added list named " + dataSnapshot.getValue(List.class).getListName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("LISTER:", "Removing [" + dataSnapshot.getValue(List.class).getListName() + "] from table due to firebase remove");
                String value = dataSnapshot.getValue(List.class).getListName();
                adapter.remove(value);
                Toast.makeText(getApplicationContext(), "Removed list named " + dataSnapshot.getValue(List.class).getListName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("TAG:", "Failed to read value.", error.toException());
            }
        });
    }

    /**
     * Adds the string to the Firebase list
     * @param str Value to add to firebase list
     */
    private void addToList(String str) {
        Log.d("LISTER:", "Adding list named [" + str + "]");

        List list = new List();
        list.setListName(str);
        list.setAddedDate(dateFormat.format(new Date()));
        list.setAddedBy(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        listRef.child(str).setValue(list);
    }

}
