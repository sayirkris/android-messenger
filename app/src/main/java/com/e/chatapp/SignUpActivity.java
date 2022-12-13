package com.e.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private EditText editUsername, editPassowrd, editEmail;
    private Button btnSubmit;
    private TextView txtLoginInfo;

    private boolean isSigningUp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        getSupportActionBar().hide();

        // CHANGING COLOR OF NAVIGATION BAR
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
        }



        editEmail = findViewById(R.id.editEmail);
        editPassowrd = findViewById(R.id.editPassword);
        editUsername = findViewById(R.id.editUsername);

        btnSubmit = findViewById(R.id.btnSubmit);
        txtLoginInfo = findViewById(R.id.txtLoginInfo);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(SignUpActivity.this, FriendsActivity.class));
            finish();
        };

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editEmail.getText().toString().isEmpty() || editPassowrd.getText().toString().isEmpty()){
                    if(isSigningUp && editUsername.getText().toString().isEmpty()){
                        Toast.makeText(SignUpActivity.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                    }
                }

                if (isSigningUp){
                    handleSignUp();
                }else {
                    handleLogin();
                }

            }
        });

        txtLoginInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSigningUp){
                    isSigningUp = false;
                    editUsername.setVisibility(View.GONE);
                    btnSubmit.setText("Login");
                    txtLoginInfo.setText("Don't have an account? Sign Up");
                } else {
                    isSigningUp = true;
                    editUsername.setVisibility(View.VISIBLE);
                    btnSubmit.setText("Sign Up");
                    txtLoginInfo.setText("Already have an account? Login");
                }
            }
        });
    }

    private void handleSignUp(){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(editEmail.getText().toString(), editPassowrd.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(editUsername.getText().toString(), editEmail.getText().toString(), ""));
                    startActivity(new Intent(SignUpActivity.this, FriendsActivity.class));
                    Toast.makeText(SignUpActivity.this, "Signed up successfully", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void handleLogin(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(editEmail.getText().toString(), editPassowrd.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(SignUpActivity.this, FriendsActivity.class));
                    Toast.makeText(SignUpActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}