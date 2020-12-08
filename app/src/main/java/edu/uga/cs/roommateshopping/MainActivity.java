package edu.uga.cs.roommateshopping;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference groceryList;
    private ListView list;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    private Button Button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddItemDialog(MainActivity.this);
            }
        });

        Button1 = (Button) findViewById( R.id.button1 );

        list = (ListView) findViewById(R.id.itemList);
        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                showPricePurchasedDialog(MainActivity.this, position);
            }

        });

        groceryList = FirebaseDatabase.getInstance().getReference().child("Grocery List");
        groceryList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groceryList = FirebaseDatabase.getInstance().getReference().child("Grocery List");
                arrayList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    String item = postSnapshot.getKey();
                    if(postSnapshot.child("Is purchased").getValue().toString().equals("no")) {
                        arrayList.add(item);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

                // ...
            }
        });


        // Listener for recently purchased list
        Button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RecentlyPurchasedActivity.class);
                startActivity(intent);

            }
        });
    }

    private void showAddItemDialog(Context c) {
        groceryList = FirebaseDatabase.getInstance().getReference();
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Add a new item")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = String.valueOf(taskEditText.getText());
                        groceryList.child("Grocery List").child(item).child("Is purchased").setValue("no");

                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }


    private void showPricePurchasedDialog(final Context c, final int position) {
        final EditText priceEditText = new EditText(c);
        priceEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Please enter the price of the item")
                .setMessage("Note: Pressing okay indicates that this item has been purchased")
                .setView(priceEditText)
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = arrayList.get(position);
                        Double price =  Double.parseDouble(String.valueOf(priceEditText.getText()));
                        groceryList.child(item).child("Is purchased").setValue("yes");
                        groceryList.child(item).child("Price").setValue(price);

                        showAddName(c,item);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void showAddName(final Context c, final String item){
        groceryList = FirebaseDatabase.getInstance().getReference();
        final EditText nameEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Please enter name of purchaser")
                .setView(nameEditText)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item2 = String.valueOf(nameEditText.getText());
                        groceryList.child(item).child("Name").setValue(item2);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
}