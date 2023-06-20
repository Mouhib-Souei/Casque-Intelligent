package com.example.howlogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView register ;
    private EditText editEmail, editPass ;
    private Button signIn;

    private FirebaseAuth mAuth ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register=findViewById(R.id.txtregister);

        register.setOnClickListener(this);
        signIn=findViewById(R.id.butlog);
        signIn.setOnClickListener(this);
        mAuth=FirebaseAuth.getInstance();

        editEmail=findViewById(R.id.loginmail);
        editPass=findViewById(R.id.loginpassword);





    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txtregister:
                startActivity(new Intent(MainActivity.this,RegisterUser.class));
                break;

            case R.id.butlog:
                UserLogin();
                break ;
        }


    }

    private void UserLogin() {
        String email=editEmail.getText().toString().trim();
        String pass=editPass.getText().toString().trim();

        if (email.isEmpty()){

            editEmail.setError("Email is required !");
            editEmail.requestFocus();
            return;

        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("Please enter a valid email");
            editEmail.requestFocus();
            return;
        }

        if (pass.isEmpty()){
            editPass.setError("Password is required !");
            editPass.requestFocus();
            return;
        }

        if (pass.length()<6){
            editPass.setError("Min Password length is 6 characters !");
            editPass.requestFocus();
            return;

    }
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(MainActivity.this,ProfileActivity.class));

                }
                else {
                    Toast.makeText(MainActivity.this,"Failed to login ! Please check your credentials",Toast.LENGTH_LONG).show();
                }
            }
        });
}}