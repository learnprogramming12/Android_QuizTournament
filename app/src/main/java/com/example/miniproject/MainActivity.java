package com.example.miniproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    boolean bHasSetRecyclerView = false;
    Button btnCreateTournament;
    RecyclerView recyclerViewForTournaments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TimeZone.setDefault(TimeZone.getTimeZone("Pacific/Auckland"));

        Toast.makeText(getApplicationContext(), "role: " + Account.getInstance().getRole(), Toast.LENGTH_LONG).show();

        btnCreateTournament = findViewById(R.id.create_tournament);
        if(Account.getInstance().getRole() == Role.Player){
            btnCreateTournament.setVisibility(View.GONE);
        }
        else if(Account.getInstance().getRole() == Role.Administrator){
            btnCreateTournament.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent;
                    intent = new Intent(MainActivity.this, CreateTournamentActivity.class);
                    startActivity(intent);
                }
            });
        }

        DatabaseReference referenceTournaments = FirebaseDatabase.getInstance().getReference("tournaments");
        referenceTournaments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Tournament> tournamentList = new ArrayList<>();
                    for (DataSnapshot tournamentSnapshot : dataSnapshot.getChildren()) {
                        String tournamentName = tournamentSnapshot.getKey();
                        Map<String, Object> tournamentData = (Map<String, Object>) tournamentSnapshot.getValue();

                        Tournament tournament = new Tournament();
                        tournament.name = tournamentName;
                        tournament.category = tournamentData.get("category").toString();
                        tournament.difficulty = tournamentData.get("difficulty").toString();
                        tournament.startDate = tournamentData.get("start_date").toString();
                        tournament.endDate = tournamentData.get("end_date").toString();
                        List<String> UserListOfLikes = new ArrayList<>();
                        for(DataSnapshot userOfLikes : tournamentSnapshot.child("likes").getChildren()){
                            UserListOfLikes.add(userOfLikes.getValue(String.class));
                        }
                        tournament.likes = UserListOfLikes.size();

                        DataSnapshot participants = tournamentSnapshot.child("participants");
                        if(participants.exists()) {
                            for (DataSnapshot child : participants.getChildren()) {
                                if (child.getValue(String.class).equals(Account.getInstance().getUserName())) {
                                    tournament.status = getString(R.string.status_participated);
                                    break;
                                }
                            }
                        }

                        if(getString(R.string.status_participated).equals(tournament.status) == false){

                            if(DateCompare.compare(tournament.endDate) == -1){
                                tournament.status = getString(R.string.status_past);
                            }
                            else if(DateCompare.compare(tournament.startDate) == 1){
                                tournament.status = getString(R.string.status_upcoming);
                            }
                            else if(DateCompare.compare(tournament.startDate) == -3 || DateCompare.compare(tournament.startDate) == -2){
                                tournament.status = getString(R.string.status_unknown);
                            }
                            else{
                                tournament.status = getString(R.string.status_ongoing);
                            }
                        }

                        tournamentList.add(tournament);
                    }
                    SingletonForTournamentManager.getInstance().setTournamentList(tournamentList);
                    //use tournamentList to update recyclerView
                    if(bHasSetRecyclerView == false){
                        bHasSetRecyclerView = true;
                        recyclerViewForTournaments = findViewById(R.id.recycler_view_for_tournaments);
                        recyclerViewForTournaments.setLayoutManager(new LinearLayoutManager(btnCreateTournament.getContext()));
                        RecyclerViewAdapter adapter = new RecyclerViewAdapter(SingletonForTournamentManager.getInstance().getTournamentList());
                        recyclerViewForTournaments.setAdapter(adapter);
                    }
                    else{
                        ((RecyclerViewAdapter) (recyclerViewForTournaments.getAdapter())).updateData();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Tournament does not exist.", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}