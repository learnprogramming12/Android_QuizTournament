package com.example.miniproject;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

enum Role{
    Player,
    Administrator
}

public class Account {
    private static Account account;
    private String uid;
    private Role role;
    private String userName;

    private Account()
    {

    }
    public static Account getInstance()
    {
        if(account == null)
            account = new Account();

        return account;
    }
    public String getUid(){return this.uid;}
    public void setUid(String uid){this.uid = uid;}
    public String getUserName(){return this.userName;}
    public void setUserName(String userName){this.userName = userName;}
    public Role getRole(){return this.role;}
    public void setRole(Role role){this.role = role;}
}
