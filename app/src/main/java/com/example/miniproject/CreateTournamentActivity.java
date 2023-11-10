package com.example.miniproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateTournamentActivity extends AppCompatActivity {

    private String strSelectedDifficulty;
    private String strSelectedCategory;
    private int iQuestionCount = 10;
    private EditText tournamentName;
    private EditText startDate;
    private EditText endDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tournament);

        tournamentName = findViewById(R.id.tournament_name);
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

        Spinner spinnerDifficulty = findViewById(R.id.difficulty);
        spinnerDifficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                strSelectedDifficulty = (String) parentView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                strSelectedDifficulty = "easy";
            }
        });

        Spinner spinnerCategory = findViewById(R.id.category);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                strSelectedCategory = (String) parentView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                strSelectedCategory = "books";
            }
        });

        Button btnCreate = findViewById(R.id.create);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tournaments").child(tournamentName.getText().toString());
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(getApplicationContext(), tournamentName.getText().toString() + " already exists.", Toast.LENGTH_LONG).show();
                        } else {
                            int iCategory;
                            switch (strSelectedCategory) {
                                case "Music":
                                    iCategory = 12;
                                    break;
                                case "Video Games":
                                    iCategory = 15;
                                    break;
                                case "Nature":
                                    iCategory = 17;
                                    break;
                                case "Computers":
                                    iCategory = 18;
                                    break;
                                default:
                                    iCategory = 15;
                            }
                            String strUrl = String.format("https://opentdb.com/api.php?amount=%d&category=%d&difficulty=%s&type=boolean", iQuestionCount, iCategory, strSelectedDifficulty);
                            Log.i("Request url", strUrl);
                            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, strUrl, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.i("Response successfully:", response.toString());
                                    try {
                                        int responseCode = response.getInt("response_code");
                                        //Toast.makeText(getApplicationContext(), "responseCode: " + responseCode , Toast.LENGTH_LONG).show();

                                        switch (responseCode) {
                                            case 0:
                                                JSONArray questions = response.getJSONArray("results");
                                                List<Map<String, Object>> questionsList = new ArrayList<>();
                                                for (int i = 0; i < questions.length(); i++) {
                                                    JSONObject questionObject = questions.getJSONObject(i);
                                                    Map<String, Object> questionMap = new HashMap<>();
                                                    questionMap.put("category", questionObject.getString("category"));
                                                    questionMap.put("type", questionObject.getString("type"));
                                                    questionMap.put("difficulty", questionObject.getString("difficulty"));
                                                    questionMap.put("question", questionObject.getString("question"));
                                                    questionMap.put("correct_answer", questionObject.getString("correct_answer"));

                                                    JSONArray incorrectAnswers = questionObject.getJSONArray("incorrect_answers");
                                                    List<String> incorrectAnswersList = new ArrayList<>();
                                                    for(int j = 0; j < incorrectAnswers.length();j++){
                                                        incorrectAnswersList.add(incorrectAnswers.getString(j));
                                                    }
                                                    questionMap.put("incorrect_answers", incorrectAnswersList);

                                                    questionsList.add(questionMap);
                                                }

                                                Map<String, Object> mapTournament = new HashMap<>();
                                                mapTournament.put("category", strSelectedCategory);
                                                mapTournament.put("difficulty", strSelectedDifficulty);
                                                mapTournament.put("start_date", startDate.getText().toString());
                                                mapTournament.put("end_date", endDate.getText().toString());
                                                mapTournament.put("likes", new ArrayList<String>());
                                                mapTournament.put("questions", questionsList);
                                                mapTournament.put("participants", new ArrayList<String>());
                                                databaseReference.setValue(mapTournament, new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                        if(error == null){
                                                            Toast.makeText(getApplicationContext(), tournamentName.getText().toString() + " is created.", Toast.LENGTH_LONG).show();
                                                        }
                                                        else{
                                                            String errorMessage = "Error creating: "+ error.getMessage();
                                                            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });

                                                break;
                                            case 1:
                                                Toast.makeText(getApplicationContext(), "The API doesn't have enough questions for your query.", Toast.LENGTH_LONG).show();
                                                break;
                                            case 2:
                                                Toast.makeText(getApplicationContext(), "Arguments passed in aren't valid.", Toast.LENGTH_LONG).show();
                                                break;
                                            default:
                                                Toast.makeText(getApplicationContext(), "Token error.", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        Toast.makeText(getApplicationContext(), "Error in parsing.", Toast.LENGTH_LONG).show();
                                        Log.e("JSON Error", "Error in parsing");
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("VolleyError:", error.toString());
                                }
                            });
                            SingletonForRequest.getInstance(getApplicationContext()).getRequestQueue().add(objectRequest);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTournamentActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                editText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}