package com.example.managingpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText emailId , PasswordID;
    Button btnSignIN;
    TextView tvSignUP, ForgotPassword;
    ProgressBar barLogin;
    FirebaseAuth mAuth;
    String email, password;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = (FirebaseAuth) FirebaseAuth.getInstance();
        emailId = (EditText) findViewById(R.id.EmailAddressID);
        PasswordID = (EditText) findViewById(R.id.PasswordID);
        tvSignUP = (TextView) findViewById(R.id.TSignUP);
        ForgotPassword = (TextView) findViewById(R.id.ForgotPassword);
        btnSignIN = (Button) findViewById(R.id.btnSignIn);
        barLogin = (ProgressBar) findViewById(R.id.barLogin);

        tvSignUP.setOnClickListener(this);
        btnSignIN.setOnClickListener(this);
        ForgotPassword.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.TSignUP:
                Intent intSignUP = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intSignUP);
                break;
            case R.id.btnSignIn:
                userLogin();
                break;
            case R.id.ForgotPassword:

                Intent ToReset = new Intent(MainActivity.this,ResetPassword.class);
                startActivity(ToReset);
                break;
        }

    }

    private void userLogin()
    {
        email = emailId.getText().toString().trim();
        password = PasswordID.getText().toString().trim();

        if (email.isEmpty()) {
            emailId.setError("נדרש להזין מייל");
            emailId.requestFocus();
            return;

        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailId.setError("נדרש להזין דוא''ל בתוקף");
            emailId.requestFocus();
            return;
        }
        if (!checkEmail(email))
        {
            return;
        }
        if (password.isEmpty()) {
            PasswordID.setError("נדרש להזין סיסמה");
            PasswordID.requestFocus();
            return;
        }

        barLogin.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {



                if (task.isSuccessful())
                {
                    FirebaseUser Verified= FirebaseAuth.getInstance().getCurrentUser();
                    if (Verified.isEmailVerified())
                    {
                        barLogin.setVisibility(View.INVISIBLE);
                        Intent ToHome = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(ToHome);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "עליך לאשר את המייל תחילה",Toast.LENGTH_LONG).show();
                        barLogin.setVisibility(View.INVISIBLE);
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "התחברות נכשלה, אנא נסה שוב.",Toast.LENGTH_LONG).show();
                    barLogin.setVisibility(View.INVISIBLE);
                }
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
                    emailId.setError("משתמש לא קיים במערכת.");
                    emailId.requestFocus();
                    return;
                }
            }
        });
        return true;
    }
}
