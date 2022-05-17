package com.edd.kulapopote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
//import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.edd.kulapopote.Common.Common;
import com.edd.kulapopote.Model.Category;
import com.edd.kulapopote.Model.Food;
import com.edd.kulapopote.Model.User;
import com.edd.kulapopote.ViewHolder.MenuViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;
import io.paperdb.Paper;

import static com.edd.kulapopote.Common.Common.currentUser;

public class SignIn extends AppCompatActivity {

    EditText edtPhone, edtPassword;
    Button bSignIn;
    com.rey.material.widget.CheckBox ckbRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPhone = (MaterialEditText) findViewById(R.id.edtPhone);
        edtPassword = (MaterialEditText)findViewById(R.id.edtPassword);
        bSignIn = (Button)findViewById(R.id.bSignIn);

        ckbRemember = (CheckBox)findViewById(R.id.ckbRemember);

//        init paper
        Paper.init(this);

        //        Init Firebase

        final FirebaseDatabase database= FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("Users");


        bSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectedToInternet(getBaseContext())) {

//                    save user and password
                    if (ckbRemember.isChecked())
                    {
                        Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                        Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());
                    }

                    final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                    mDialog.setMessage("Please wait...");
                    mDialog.show();


                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //Check if user exist in database
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                //Get user information
                                mDialog.dismiss();


                                User Users = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                Users.setPhone(edtPhone.getText().toString());// set phone number
                                if (Users.getPassword().equals(edtPassword.getText().toString())) {


                                    Toast.makeText(SignIn.this, "Welcome", Toast.LENGTH_SHORT).show();
                                    Intent homeIntent = new Intent(SignIn.this, Home.class);
                                    currentUser = Users;
                                    startActivity(homeIntent);
                                    finish();

                                } else {
                                    Toast.makeText(SignIn.this, "Wrong number or password!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(SignIn.this, "User not found", Toast.LENGTH_SHORT).show();

                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {


                        }
                    });
                }
                else
                {
                    Toast.makeText(SignIn.this, "Please check your internet connection !!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        }

        );
    }
}
