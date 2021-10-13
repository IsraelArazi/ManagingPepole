package com.example.managingpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Network;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener{

    EditText RemailId, RPasswordID , ConfirmRPasswordID, Phone, FullName;
    Button btnSignUP;
    TextView tvSignIN;
    private FirebaseAuth mAuth;
    String email , pas, conpas ,name, phone, chackpasLowerCase ,chackpasUpperCase;
    ProgressBar Bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        FullName = (EditText)findViewById(R.id.Name);
        RemailId = (EditText)findViewById(R.id.REmailAddress);
        RPasswordID = (EditText) findViewById(R.id.RPassword);
        ConfirmRPasswordID = (EditText)findViewById(R.id.ComfPassword);
        Phone = (EditText)findViewById(R.id.Phone);
        tvSignIN = (TextView) findViewById(R.id.tvSignIN);
        btnSignUP = (Button) findViewById(R.id.btnSignUp);

        Bar = (ProgressBar) findViewById(R.id.Bar);

        mAuth = (FirebaseAuth) FirebaseAuth.getInstance();

        tvSignIN.setOnClickListener(this);
        btnSignUP.setOnClickListener(this);


    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnSignUp:
                regUser();
                break;
            case R.id.tvSignIN:
                Intent ToMain = new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(ToMain);
                break;
        }

    }

    private void regUser() {
        name = FullName.getText().toString().trim();
        email = RemailId.getText().toString().trim();
        pas = RPasswordID.getText().toString().trim();
        conpas = ConfirmRPasswordID.getText().toString().trim();
        chackpasLowerCase= pas.toLowerCase();
        chackpasUpperCase=pas.toUpperCase();
        phone = Phone.getText().toString().trim();

        if (name.isEmpty()) {
            FullName.setError("נדרש להזין שם");
            FullName.requestFocus();
            return;

        }
        if (email.isEmpty()) {
            RemailId.setError("נדרש להזין מייל");
            RemailId.requestFocus();
            return;

        }
        if (!checkEmail(email))
        {
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            RemailId.setError("נדרש להזין דוא''ל בתוקף");
            RemailId.requestFocus();
            return;
        }
        if (pas.isEmpty()) {
            RPasswordID.setError("נדרש להזין סיסמה");
            RPasswordID.requestFocus();
            return;

        }
        if (conpas.isEmpty()) {
            ConfirmRPasswordID.setError("נדרש לאמת סיסמה");
            ConfirmRPasswordID.requestFocus();
            return;

        }
        if (!(pas.equals(conpas)))
        {
            RPasswordID.setError("סיסמאות לא זהות");
            RPasswordID.requestFocus();
            ConfirmRPasswordID.setError("סיסמאות לא זהות");
            ConfirmRPasswordID.requestFocus();
            return;
        }
        if (pas.equals(chackpasLowerCase) || pas.equals(chackpasUpperCase) | pas.length() < 8 || conpas.length()<8)
        {
            RPasswordID.setError("הסיסמה חייבת להכיל אות גדולה (A) אותה קטנה (a) ולפחות 8 תווים");
            RPasswordID.requestFocus();
            ConfirmRPasswordID.setError("הסיסמה חייבת להכיל אות גדולה (A) אותה קטנה (a) ולפחות 8 תווים");
            ConfirmRPasswordID.requestFocus();
            return;
        }


        if (phone.isEmpty())
        {
            Phone.setError("נדרש להזין טלפון");
            Phone.requestFocus();
            return;

        }



        Bar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,pas).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {

                    User user = new User(name, email, phone, pas);

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        FirebaseUser userVerified= FirebaseAuth.getInstance().getCurrentUser();
                                        userVerified.sendEmailVerification();
                                        Toast.makeText(RegistrationActivity.this, "ברכות, נרשמת בהצלחה! בדוק את תיבת הדואר שלך ע''מ לאשר את ההרשמה!", Toast.LENGTH_LONG).show();
                                        Bar.setVisibility(View.INVISIBLE);
                                        Intent intToMain = new Intent(RegistrationActivity.this, MainActivity.class);
                                        startActivity(intToMain);
                                    }
                                    else
                                        {
                                            Toast.makeText(RegistrationActivity.this, "הרשמה נכשלה, אנא נסה/י שוב", Toast.LENGTH_LONG).show();
                                            Bar.setVisibility(View.GONE);
                                        }
                                }
                            });

                }
                else
                {
                    Toast.makeText(RegistrationActivity.this, "הרשמה נכשלה, אנא נסה/י שוב", Toast.LENGTH_SHORT).show();
                    Bar.setVisibility(View.GONE);
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
                if (test)
                {
                    RemailId.setError("משתמש כבר קיים במערכת.");
                    RemailId.requestFocus();
                    return;
                }
            }
        });
        return true;
    }
}