package com.prometrx.myinstagramclone.UserLoginSignup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.prometrx.myinstagramclone.MainActivity;
import com.prometrx.myinstagramclone.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth fuser;
    private FirebaseFirestore firestore;
    private EditText usernameEditText,passEditText,passconfirmEditText,emailEditText;
    private Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
        signupButtonClicked();
    }
    private void init() {
        //UI
        usernameEditText = findViewById(R.id.usernameEditTextSignUp);
        passEditText = findViewById(R.id.passwordEditTextSignUp);
        passconfirmEditText = findViewById(R.id.passwordConfirmEditTextSignUp);
        emailEditText = findViewById(R.id.emailEditTextSignUp);
        signUp = findViewById(R.id.buttonSignUp);

        //Firebase
        fuser = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    private void signupButtonClicked() {

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(usernameEditText.getText().toString().equals("") || passEditText.getText().toString().equals("") || passconfirmEditText.getText().toString().equals("") || emailEditText.getText().toString().equals("")) {
                    Toast.makeText(SignUpActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
                }else{
                    if(passEditText.getText().toString().equals(passconfirmEditText.getText().toString())) {
                        fuser.createUserWithEmailAndPassword(emailEditText.getText().toString(), passEditText.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                HashMap<String,Object> hashMap = new HashMap<>();
                                List<String> followers = new ArrayList<>();
                                List<String> follow = new ArrayList<>();

                                hashMap.put("id", fuser.getCurrentUser().getUid());
                                hashMap.put("imageUrl", "0");
                                hashMap.put("username", usernameEditText.getText().toString());
                                hashMap.put("followers",followers);
                                hashMap.put("follow",follow);

                                firestore.collection("UsersData").document(fuser.getCurrentUser().getUid()).set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Intent intent = new Intent(SignUpActivity.this, SelectProfileImageActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        Toast.makeText(SignUpActivity.this, "Please check password!", Toast.LENGTH_SHORT).show();
                    }
                }




            }
        });
    }

}