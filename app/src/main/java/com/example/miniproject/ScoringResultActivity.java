package com.example.miniproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ScoringResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoring_result);

        Intent intent = getIntent();
        int iTotalQuestions = intent.getIntExtra("total_questions", -1);
        int iRightAnswer = intent.getIntExtra("scoring", -1);
        String strResult;
        if(iTotalQuestions == -1 || iRightAnswer == -1 || iRightAnswer > iTotalQuestions){
            strResult = "Statistics is wrong";
        }
        else{
            strResult = String.format("Answered %d out of %d questions correctly", iRightAnswer, iTotalQuestions);
        }

        TextView tvScoring = findViewById(R.id.scoring);
        tvScoring.setText(strResult);
    }
}