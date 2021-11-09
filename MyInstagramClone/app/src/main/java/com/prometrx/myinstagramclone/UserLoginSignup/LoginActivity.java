package com.prometrx.myinstagramclone.UserLoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prometrx.myinstagramclone.MainActivity;
import com.prometrx.myinstagramclone.R;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailEditText,loginPasswordEditText;
    private TextView signupTextView;
    private Button loginButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser fuser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        loginButton();
        signupTextViewClick();

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        if(fuser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
    private void init() {
        loginButton = findViewById(R.id.loginButton);
        signupTextView = findViewById(R.id.notMemberSignUpTextView);
        loginEmailEditText = findViewById(R.id.loginEmailEditText);
        loginPasswordEditText = findViewById(R.id.loginPasswordEditText);
        firebaseAuth =FirebaseAuth.getInstance();

    }

    private void loginButton() {

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(loginEmailEditText.getText().toString().equals("") || loginPasswordEditText.getText().toString().equals("")){

                }else{
                    firebaseAuth.signInWithEmailAndPassword(loginEmailEditText.getText().toString(), loginPasswordEditText.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
        });

    }
    private void signupTextViewClick() {

        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }
}