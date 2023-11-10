package com.example.miniproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TournamentDetailsForPlayerActivity extends AppCompatActivity {

    private String tournamentName;
    TextView tvLikes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_details_for_player);

        TextView tvTournamentName = findViewById(R.id.tournament_name);
        TextView tvCategory = findViewById(R.id.category);
        TextView tvDifficulty = findViewById(R.id.difficulty);
        TextView tvStatus = findViewById(R.id.status);
        tvLikes = findViewById(R.id.likes);
        TextView tvStartDate = findViewById(R.id.start_date);
        TextView tvEndDate = findViewById(R.id.end_date);
        Button btnParticipate = findViewById(R.id.participate);

        Intent intent = getIntent();
        tournamentName = intent.getStringExtra("tournamentName");
        if (tournamentName != null) {
            Tournament currentTournament = SingletonForTournamentManager.getInstance().getTournament(tournamentName);
            if(currentTournament != null){
                tvTournamentName.setText(currentTournament.name);
                tvCategory.setText(currentTournament.category);
                tvDifficulty.setText(currentTournament.difficulty);
                tvStatus.setText("Status: " + currentTournament.status);
                tvStartDate.setText("Start Date: " + currentTournament.startDate);
                tvEndDate.setText("End Date: " + currentTournament.endDate);
                tvLikes.setText(currentTournament.likes + " likes");

                if(currentTournament.status.equalsIgnoreCase(getString(R.string.status_ongoing))){
                    btnParticipate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            currentTournament.status = getString(R.string.status_participated);
                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("tournaments").child(currentTournament.name).child("participants");
                            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    List<String> participatedUsers = new ArrayList<>();
                                    if(snapshot.exists()){
                                        for(DataSnapshot child : snapshot.getChildren()){
                                            String userName = child.getValue(String.class);
                                            participatedUsers.add(userName);
                                        }
                                    }
                                    participatedUsers.add(Account.getInstance().getUserName());
                                    databaseRef.setValue(participatedUsers, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            if(error == null){
                                                //Toast.makeText(getApplicationContext(), Account.getInstance().getUserName()+" is added to participants.", Toast.LENGTH_LONG).show();
                                            }
                                            else{
                                                Toast.makeText(getApplicationContext(), Account.getInstance().getUserName()+" can not be added to participants.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            //display questions
                            Intent intent;
                            intent = new Intent(TournamentDetailsForPlayerActivity.this, QuestionDisplayActivity.class);
                            intent.putExtra("tournamentName", currentTournament.name);
                            startActivity(intent);
                        }
                    });
                }
                else{
                    btnParticipate.setVisibility(View.GONE);
                }
            }
        }


        ImageView thumbup = findViewById(R.id.thumb_up);
        thumbup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealWithUserClickThumb(1);
            }
        });

        ImageView thumbdown = findViewById(R.id.thumb_down);
        thumbdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealWithUserClickThumb(0);
            }
        });
    }
    private void dealWithUserClickThumb(int iThumb){
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("tournaments").child(tournamentName).child("likes");
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> UsersOfLikes = new ArrayList<>();
                if(snapshot.exists()){
                    for(DataSnapshot child : snapshot.getChildren()){
                        String userName = child.getValue(String.class);
                        UsersOfLikes.add(userName);
                    }
                }
                boolean bUserFound = false;
                for(int i = 0; i < UsersOfLikes.size(); i++){
                    if(Account.getInstance().getUserName().equals(UsersOfLikes.get(i))){
                        bUserFound = true;
                        if(iThumb == 0){
                            //deal with thumbdown
                            UsersOfLikes.remove(i);
                        }
                        break;
                    }
                }
                if(iThumb == 1 && bUserFound == false){
                    //deal with thumbup
                    UsersOfLikes.add(Account.getInstance().getUserName());
                }
                tvLikes.setText(UsersOfLikes.size() + " likes");

                databaseRef.setValue(UsersOfLikes, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if(error == null){
                            //Toast.makeText(getApplicationContext(), "Deal with thumb behaviour successfully", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Fail to deal with thumb behaviour.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}