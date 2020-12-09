package edu.uga.cs.roommateshopping;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Provides a list that items that were recently purchased.
 * User can calculate the price split amongst roommates.
 * The amount of roommates are inputted if dynamically if roommates
 * decides to go home for the holidays.
 */
public class RecentlyPurchasedActivity extends AppCompatActivity {

    private DatabaseReference groceryList;
    private ListView list;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    private ArrayList<String> itemList;
    private Button calculate;
    private TextView numOfRoommates;
    private TextView splitCost;
    private double totalCost;
    private double number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recently_purchased);

        calculate = (Button) findViewById( R.id.button3 );
        numOfRoommates = (TextView) findViewById( R.id.numOfRoommates);
        splitCost = (TextView) findViewById( R.id.textView7);

        list = (ListView) findViewById(R.id.itemList1);
        arrayList = new ArrayList<String>();
        itemList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        list.setAdapter(adapter);



        groceryList = FirebaseDatabase.getInstance().getReference().child("Grocery List");
        groceryList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { //reads the data from data base
                groceryList = FirebaseDatabase.getInstance().getReference().child("Grocery List");
                arrayList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    String item = postSnapshot.getKey();
                    String name = "      ";
                    String price = "     ";
                    // making sure there the item was purchased with price and name
                    if(postSnapshot.child("Is purchased").getValue().toString().equals("yes") && postSnapshot.child("Price").getValue() != null && postSnapshot.child("Name").getValue() != null) {
                        name = postSnapshot.child("Name").getValue().toString();
                        price = postSnapshot.child("Price").getValue().toString();
                        totalCost = totalCost + Double.parseDouble(price);
                        String info = "     Name: " + name + "     Item: "  + item + "     Price: $" + price; // creating the row
                        arrayList.add(info);
                        itemList.add(item);
                        adapter.notifyDataSetChanged();
                    }
                }
                if(arrayList.isEmpty()){
                    String empty = "                     Nothing was recently purchased";
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


        // Caclulates the cost for the roommates.
        calculate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!numOfRoommates.getText().toString().isEmpty() && !arrayList.isEmpty()) { // checks if the text input is empty
                    groceryList = FirebaseDatabase.getInstance().getReference().child("Grocery List");
                    DecimalFormat twoDecimal = new DecimalFormat("#.##"); // format class
                    number = Double.parseDouble(numOfRoommates.getText().toString()); // change number of roommates to double
                    double split = totalCost / number; // Do math
                    String display = twoDecimal.format(split); // round
                    splitCost.setText(display); // display the amount.
                    for (int i = 0; i < arrayList.size(); i++) { // gets the list of purchase prices
                        String item = itemList.get(i);
                        groceryList.child(item).child("Is purchased").setValue("Settled");
                    }
                    arrayList.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        });


    }

}
