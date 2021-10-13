package com.example.managingpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity ;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnLogout, btntest;
    private FirebaseUser user;
    private DatabaseReference ref;
    private String userID;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnLogout = findViewById(R.id.logout);
        btnLogout.setOnClickListener(this);
        btntest = (Button) findViewById(R.id.btntest);
        btntest.setOnClickListener(this);

        user=FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        final TextView welcome, fullNameView, emailView, phoneView;
        welcome =(TextView)findViewById(R.id.welcome);
        fullNameView =(TextView)findViewById(R.id.fullNameView);
        emailView=(TextView)findViewById(R.id.emailView);
        phoneView=(TextView)findViewById(R.id.phoneView);

        ref.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null)
                {
                    String fullName = userProfile.fullName;
                    String email = userProfile.email;
                    String phone = userProfile.phone;

                    welcome.setText("ברוכים הבאים  " +fullName + "!");
                    fullNameView.setText(fullName);
                    emailView.setText(email);
                    phoneView.setText(phone);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "אירעה שגיאה לא צפויה, נא לנסות שוב!", Toast.LENGTH_LONG).show();

            }
        });
    }



    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent ToMain = new Intent(HomeActivity.this,MainActivity.class);
                startActivity(ToMain);
                break;
            case R.id.btntest:




        }

    }
}