package com.example.howlogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Pattern;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {
    private TextView banner,registerUser;
    private EditText editfullName, editSurname,editDate,editmail,editpass;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        mAuth = FirebaseAuth.getInstance();
        banner=findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerUser=findViewById(R.id.resgisterUser);
        registerUser.setOnClickListener(this);

        editfullName=findViewById(R.id.fullname);
        editSurname=findViewById(R.id.surname);
        editDate=findViewById(R.id.date);
        editmail=findViewById(R.id.loginmail);
        editpass=findViewById(R.id.loginpassword);

        Calendar calendar =Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);


        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        RegisterUser.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        //year = year +1;
                        month = month + 1;
                        String date = day + "/" + month + "/" + year;
                        editDate.setText(date);
                    }
                }
                    ,year,month,day);
                datePickerDialog.show();

        }

     });
    }

    @Override
    public void onClick(View view) {
    switch (view.getId()){
        case R.id.banner:
            startActivity(new Intent(RegisterUser.this,MainActivity.class));
            break;

        case R.id.resgisterUser:
            registerUser();
            break;
    }

    }

    private void registerUser() {
        String Email=editmail.getText().toString().trim();
        String password=editpass.getText().toString().trim();
        String  Name=editfullName.getText().toString().trim();
        String Surname=editSurname.getText().toString().trim();
        String Birthday=editDate.getText().toString().trim();

        if( Name.isEmpty()){
            editfullName.setError("Full Name is required");
            editfullName.requestFocus();
            return;
        }
        if(Surname.isEmpty()){
            editSurname.setError(" SurName is required");
            editSurname.requestFocus();
            return;
        }


        if(Birthday.isEmpty()){
            editDate.setError("Date is required");
            editDate.requestFocus();
            return;}



        if(Email.isEmpty()){
            editmail.setError("Email is required");
            editmail.requestFocus();
            return;}

        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            editmail.setError("Please provide valid email");
            editmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editpass.setError("Password is required");
            editpass.requestFocus();
            return;
        }
        if (password.length()<6){
            editpass.setError("Min password length should be 6 characters");
            editpass.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(Email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            User user=new User( Name,Surname,Birthday,Email);

                            FirebaseDatabase.getInstance().getReference("Path")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(RegisterUser.this,"User has been registred",Toast.LENGTH_LONG).show();
                                            }else
                                            {
                                                Toast.makeText(RegisterUser.this,"failed",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                        }else{
                            Toast.makeText(RegisterUser.this,"failed",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}