package com.example.miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EditText etEmail = findViewById(R.id.signup_email);
        EditText etUserName = findViewById(R.id.signup_user_name);
        EditText etPassword = findViewById(R.id.signup_password);
        RadioGroup radioGroup = findViewById(R.id.radio_group);
        Button btnSignup = findViewById(R.id.signup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail = etEmail.getText().toString();
                String strUserName = etUserName.getText().toString();
                String strPassword = etPassword.getText().toString();
                if(TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strUserName) || TextUtils.isEmpty(strPassword))
                {
                    Toast.makeText(getApplicationContext(), "Input cannot be whitespace.", Toast.LENGTH_LONG).show();
                    return;
                }

                String strRole;
                int iSelectedRadioBtnId = radioGroup.getCheckedRadioButtonId();
                if(iSelectedRadioBtnId == R.id.player)
                {
                    strRole = "player";
                }
                else if(iSelectedRadioBtnId == R.id.admin)
                {
                    strRole = "admin";
                }
                else
                {
                    Toast.makeText(SignupActivity.this, "Please select a role", Toast.LENGTH_LONG).show();
                    return;
                }

                FirebaseAuth fbAuth = FirebaseAuth.getInstance();
                fbAuth.createUserWithEmailAndPassword(strEmail, strPassword)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String userId = fbAuth.getCurrentUser().getUid();
                                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                                    Map<String, Object> mapUser = new HashMap<>();
                                    mapUser.put("username", strUserName);
                                    mapUser.put("role", strRole);
                                    usersRef.child(userId).setValue(mapUser);
                                    Log.d("Registration", "Registration successful!");
                                    Toast.makeText(SignupActivity.this, "Sign up successfully!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Log.e("Registration", "Registration failed: " + task.getException().getMessage());
                                    Toast.makeText(SignupActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                });
            }
        });
    }
}