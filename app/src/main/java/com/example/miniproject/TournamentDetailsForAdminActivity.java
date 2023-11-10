package com.example.miniproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TournamentDetailsForAdminActivity extends AppCompatActivity {


    EditText startDate;
    EditText endDate;
    String tournamentName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_details_for_admin);

        TextView tvTournamentName = findViewById(R.id.tournament_name);
        TextView tvCategory = findViewById(R.id.category);
        TextView tvDifficulty = findViewById(R.id.difficulty);
        TextView tvStatus = findViewById(R.id.status);
        TextView tvLikes = findViewById(R.id.likes);
        startDate = findViewById(R.id.start_date);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDatePicker(startDate);
            }
        });

        endDate = findViewById(R.id.end_date);
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDatePicker(endDate);
            }
        });


        Intent intent = getIntent();
        tournamentName = intent.getStringExtra("tournamentName");
        if (tournamentName != null) {
            Tournament currentTournament = SingletonForTournamentManager.getInstance().getTournament(tournamentName);
            if(currentTournament != null){
                tvTournamentName.setText(currentTournament.name);
                tvCategory.setText(currentTournament.category);
                tvDifficulty.setText(currentTournament.difficulty);
                startDate.setText(currentTournament.startDate);
                endDate.setText(currentTournament.endDate);
                tvStatus.setText("Status: " + currentTournament.status);
                tvLikes.setText(currentTournament.likes + " likes");
            }
        }
        //for administrator's view
        Button btnView = findViewById(R.id.view);
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(TournamentDetailsForAdminActivity.this, QuestionDisplayActivity.class);
                intent.putExtra("tournamentName", tournamentName);
                startActivity(intent);
            }
        });
        //for administrator's update
        Button btnUpdate = findViewById(R.id.update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> updateData = new HashMap<>();
                updateData.put("start_date", startDate.getText().toString());
                updateData.put("end_date", endDate.getText().toString());
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                DatabaseReference nodeReference = databaseReference.child("tournaments").child(tournamentName);
                nodeReference.updateChildren(updateData).addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void unused) {
                              Toast.makeText(getApplicationContext(), tournamentName + " has been updated.", Toast.LENGTH_LONG).show();
                          }
                      }
                ).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(getApplicationContext(), tournamentName + " update failed.", Toast.LENGTH_LONG).show();
                           }
                       }
                );
            }
        });
        //for administrator's delete
        Button btnDelete = findViewById(R.id.delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                DatabaseReference nodeReference = databaseReference.child("tournaments").child(tournamentName);
                nodeReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                         @Override
                         public void onSuccess(Void unused) {
                             Toast.makeText(getApplicationContext(), tournamentName + " has been deleted.", Toast.LENGTH_LONG).show();
                         }
                     }
                ).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), tournamentName + " deletion failed.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    private void setDatePicker(EditText editText){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(TournamentDetailsForAdminActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                editText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}