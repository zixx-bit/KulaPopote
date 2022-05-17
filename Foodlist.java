package com.edd.kulapopote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.edd.kulapopote.Common.Common;
import com.edd.kulapopote.Interface.ItemClickListener;
import com.edd.kulapopote.Model.Food;
import com.edd.kulapopote.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Foodlist extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;
    String categoryId="";

    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodlist);

        database=FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");

        recyclerView=(RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager =new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Get intent here
        if (getIntent() !=null)
            categoryId = getIntent().getStringExtra("CategoryId");

        if (!categoryId.isEmpty() && categoryId !=null){

            if (Common.isConnectedToInternet(getBaseContext()))
            loadListFood(categoryId);
            else
                {
                    Toast.makeText(Foodlist.this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                }
        }

    }
        private void loadListFood(String categoryId){
            adapter= new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class, R.layout.food_item,
                FoodViewHolder.class, foodList.orderByChild("menuId").equalTo(categoryId)){
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.food_name.setText(model.getName());


                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);

                final Food local= model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override

                        public void onClick(View view, int position, boolean isLongClick) {
                            Toast.makeText(Foodlist.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                        Intent foodDetail=new Intent(Foodlist.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());
                        startActivity(foodDetail);


                    }

                });
            }
        };

        recyclerView.setAdapter(adapter);
        }
}
