package com.example.miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth fbAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fbAuth = FirebaseAuth.getInstance();
        EditText etEmail = findViewById(R.id.email);
        EditText etPassword = findViewById(R.id.password);

        Button btnLogin = findViewById(R.id.login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail = etEmail.getText().toString();
                String strPassword = etPassword.getText().toString();
                if(TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPassword))
                {
                    Toast.makeText(getApplicationContext(), "Input cannot be whitespace.", Toast.LENGTH_LONG).show();
                    return;
                }
                fbAuth.signInWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = fbAuth.getCurrentUser();
                                String strUid = currentUser.getUid();
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(strUid);
                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            String strRole = snapshot.child("role").getValue(String.class);
                                            String strUsername = snapshot.child("username").getValue(String.class);
                                            if(TextUtils.isEmpty(strRole)){
                                                Toast.makeText(getApplicationContext(), "The user has no roles.", Toast.LENGTH_LONG).show();
                                                return;
                                            }
                                            Role role;
                                            if(strRole.equalsIgnoreCase("player"))
                                                role = Role.Player;
                                            else if(strRole.equalsIgnoreCase("admin"))
                                                role = Role.Administrator;
                                            else {
                                                Toast.makeText(getApplicationContext(), "Roles error.", Toast.LENGTH_LONG).show();
                                                return;
                                            }
                                            //store user information
                                            Account.getInstance().setUid(strUid);
                                            Account.getInstance().setUserName(strUsername);
                                            Account.getInstance().setRole(role);
                                            //open main activity
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(), "No user's extra information is stored.", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                        }
                        else{
                                Exception exception = task.getException();
                                Toast.makeText(LoginActivity.this, "Authentication failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        Button btnSignup = findViewById(R.id.start_signup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
                //finish();
            }
        });
    }
}