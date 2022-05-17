package com.edd.kulapopote;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edd.kulapopote.Common.Common;
import com.edd.kulapopote.Database.Database;
import com.edd.kulapopote.Model.Order;
import com.edd.kulapopote.Model.Request;
import com.edd.kulapopote.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice;
    Button btnPlace;


    List <Order> cart = new ArrayList<>();

    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Firebase
        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");

        //Initialize
        recyclerView =(RecyclerView) findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice =(TextView)findViewById(R.id.total);
        btnPlace = (Button)findViewById(R.id.btnPlaceOrder);


        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cart.size()>0)
                showAlertDialog();
                else
                    Toast.makeText(Cart.this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            }
        });


        loadFoodList();
    }

       private void showAlertDialog(){
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("Just one more step");
        alertDialog.setMessage("Enter your address");

        final EditText edtAddress= new EditText(Cart.this);
        LinearLayout.LayoutParams layoutP= new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        edtAddress.setLayoutParams(layoutP);
        alertDialog.setView(edtAddress);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                //Create New Request

               Request request = new Request(
                       Common.currentUser.getPhone(),
                       Common.currentUser.getName(),
                       edtAddress.getText().toString(),
                       txtTotalPrice.getText().toString(),
                       cart
               );

                //Submit to firebase

                requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);


                //Delete cart
               new  Database(getBaseContext()).cleanCart();

                Toast.makeText(Cart.this, "Thank you!  Your Order  has been placed", Toast.LENGTH_SHORT).show();
                finish();
//                MediaPlayer mediaPlayer = MediaPlayer.create(Cart.this, R.raw.freak);
//                mediaPlayer.start();

            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                Toast.makeText(Cart.this, "You have not placed any order", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

    private void loadFoodList(){

        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);


        //Total calculations

        int total = 0;
        for (Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));

            Locale locale = new Locale("en ", "KE");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

            txtTotalPrice.setText(fmt.format(total));

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }
    private void deleteCart(int position)
    {

//        remove item at list<order > by position
        cart.remove(position);
//        after that delete all the old data from SQLlite?
        new Database(this).cleanCart();
//        And final, we will update new data from sqllite
        for (Order item:cart)
            new Database(this).addToCart(item) ;
//        Refresh
        loadFoodList();
    }
}
