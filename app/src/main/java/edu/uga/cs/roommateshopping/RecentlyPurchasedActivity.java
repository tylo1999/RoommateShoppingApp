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

public class RecentlyPurchasedActivity extends AppCompatActivity {

    private DatabaseReference groceryList;
    private ListView list;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
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
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        list.setAdapter(adapter);



        groceryList = FirebaseDatabase.getInstance().getReference().child("Grocery List");
        groceryList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groceryList = FirebaseDatabase.getInstance().getReference().child("Grocery List");
                arrayList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    String item = postSnapshot.getKey();

                    if(postSnapshot.child("Is purchased").getValue().toString().equals("yes")) {
                        String name = postSnapshot.child("Name").getValue().toString();
                        String price = postSnapshot.child("Price").getValue().toString();
                        totalCost = totalCost + Double.parseDouble(price);
                        String info = "     Name: " + name + "     Item: "  + item + "     Price: " + price;
                        arrayList.add(info);
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
        calculate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DecimalFormat twoDecimal = new DecimalFormat("#.##"); // format class
                number = Double.parseDouble(numOfRoommates.getText().toString()); // change number of roommates to double
                double split = totalCost/number; // Do math
                String display = twoDecimal.format(split); // round
                splitCost.setText(display); // display the amount.
            }
        });

    }

}
