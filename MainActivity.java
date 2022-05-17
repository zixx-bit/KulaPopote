
package com.edd.kulapopote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.edd.kulapopote.Common.Common;
import com.edd.kulapopote.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

import static com.edd.kulapopote.Common.Common.currentUser;

public class MainActivity extends AppCompatActivity {

    Button btnSignUp, btnSignIn;
    TextView txtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = findViewById(R.id.btnSignActive);
        btnSignUp = findViewById(R.id.btnSignUp);

        txtSlogan = findViewById(R.id.textSlogan);
        //Typeface face =Typeface.createFromAsset(getAssets(), fonts/backslash.TTf);
        //txtSlogan.setTypeface(face);

//        init paper
        Paper.init(this);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SignUp= new Intent(MainActivity.this, SignUp.class);


                startActivity(SignUp);

            }
        });



        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SignIn= new Intent(MainActivity.this, com.edd.kulapopote.SignIn.class);
                startActivity(SignIn);
            }
        });

        //Check remember
        String user =  Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);
        if (user !=null && pwd !=null)
        {
            if (!user.isEmpty() && !pwd.isEmpty())
            {
                login(user, pwd);
            }
        }
    }

    private void login(final String phone, final String pwd) {
         final  FirebaseDatabase database= FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("Users");

        if (Common.isConnectedToInternet(getBaseContext())) {



            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Please wait...");
            mDialog.show();


            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //Check if user exist in database
                    if (dataSnapshot.child(phone).exists()) {
                        //Get user information
                        mDialog.dismiss();


                        User Users = dataSnapshot.child(phone).getValue(User.class);
                        Users.setPhone(phone);// set phone number
                        if (Users.getPassword().equals(pwd)) {


                            Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                            Intent homeIntent = new Intent(MainActivity.this, Home.class);
                            currentUser = Users;
                            startActivity(homeIntent);
                            finish();

                        } else {
                            Toast.makeText(MainActivity.this, "Wrong number or password!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_SHORT).show();

                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {


                }
            });
        }
        else
        {
            Toast.makeText(MainActivity.this, "Please check your internet connection !!", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
