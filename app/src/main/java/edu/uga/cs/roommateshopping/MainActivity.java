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
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * This class allows the user to add items to the list.
 * Clicking the item provides a prompt that will
 * let users input the name and price of the item that was
 * just purchased.
 *
 */
public class MainActivity extends AppCompatActivity {
    private DatabaseReference groceryList;
    private ListView list;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    private Button Button1;
    private Button Button2;

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
        Button2 = (Button) findViewById( R.id.button2 );

        list = (ListView) findViewById(R.id.itemList); // creating a list to sow the grocery list
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
            public void onDataChange(DataSnapshot dataSnapshot) { // This will read data live, and show what is suppose ot be shown
                groceryList = FirebaseDatabase.getInstance().getReference().child("Grocery List");
                arrayList.clear(); // clear so it won't stack
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String item = postSnapshot.getKey();
                    if(postSnapshot.child("Is purchased").getValue().toString().equals("no")) { // adds to the list if not marked as purchased yet
                        arrayList.add(item);
                        adapter.notifyDataSetChanged();
                    }
                }
                if(arrayList.isEmpty()){
                    String empty = "                     List is empty, press + to add item";
                    arrayList.add(empty);
                    adapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

                // ...
            }
        });


        // Listener for recently purchased list
        Button1.setOnClickListener(new View.OnClickListener() { // button for to go to recently purchased

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RecentlyPurchasedActivity.class);
                startActivity(intent);

            }
        });

        // Listener for recently purchased list
        Button2.setOnClickListener(new View.OnClickListener() { // button to sign out

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LoginActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "You have successfully signed-out", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Dialog to add a new item into the data base.
     * @param c gathers the context to save in database
     */
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


    /**
     * Allows user to input the price of the item that was purchased.
     * The function will also place the item into recently purchased list.
     */
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

                        groceryList.child(item).child("Price").setValue(price);

                        showAddName(c,item);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    /**
     * Adds the name of the purchaser of the item to the database
     * @param c gathers the context.
     * @param item shows which item to add the context to.
     */
    private void showAddName(final Context c, final String item){
        groceryList = FirebaseDatabase.getInstance().getReference().child("Grocery List");
        final EditText nameEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Please enter name of purchaser")
                .setView(nameEditText)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() { // submit button
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item2 = String.valueOf(nameEditText.getText());
                        groceryList.child(item).child("Name").setValue(item2); // adding name
                        groceryList.child(item).child("Is purchased").setValue("yes"); // making it purchased
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
}