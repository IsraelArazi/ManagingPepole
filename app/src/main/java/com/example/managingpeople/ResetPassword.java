package com.example.managingpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class ResetPassword extends AppCompatActivity implements View.OnClickListener {

    EditText editTextemail;
    Button ResetPassword;
    TextView Home;
    ProgressBar BarReset;
    FirebaseAuth mAuth;
    ImageView imgHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        editTextemail =(EditText)findViewById(R.id.email);
        ResetPassword = (Button)findViewById(R.id.ptnResetPassword);
        //Home = (TextView) findViewById(R.id.Home);
        BarReset = (ProgressBar) findViewById(R.id.BarReset);
        mAuth = (FirebaseAuth) FirebaseAuth.getInstance();
        imgHome = (ImageView) findViewById(R.id.imgHome);

        ResetPassword.setOnClickListener(this);
        //Home.setOnClickListener(this);
        imgHome.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ptnResetPassword:
                resetPassword();
                break;


            case R.id.imgHome:
                Intent ToHome = new Intent(ResetPassword.this, MainActivity.class);
                startActivity(ToHome);
                break;

        }


    }

    private void resetPassword()
    {
        String email = editTextemail.getText().toString().trim();

        if (email.isEmpty())
        {
            editTextemail.setError("נדרש להזין כתובת דוא''ל.");
            editTextemail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextemail.setError("נדרש להזין כתובת דוא''ל חוקית.");
            editTextemail.requestFocus();
            return;
        }
        if (!checkEmail(email))
        {
            return;
        }

        BarReset.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful())
                {
                    Toast.makeText(ResetPassword.this,"קישור לאיפוס החשבון נשלח בהצלחה, יש לבדוק את תיבת הדוא''ל.", Toast.LENGTH_LONG).show();
                }

                else
                {
                    Toast.makeText(ResetPassword.this,"אירעה שגיאה בלתי צפויה, נא לנסות שוב", Toast.LENGTH_LONG).show();
                }
                BarReset.setVisibility(View.INVISIBLE);
            }
        });

    }

    public boolean checkEmail(String email)
    {
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                boolean test = !task.getResult().getSignInMethods().isEmpty();
                if (!test)
                {
                    editTextemail.setError("משתמש לא קיים במערכת.");
                    editTextemail.requestFocus();
                    return;
                }
            }
        });
        return true;
    }
}