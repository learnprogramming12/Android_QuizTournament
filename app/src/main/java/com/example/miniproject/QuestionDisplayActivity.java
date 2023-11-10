package com.example.miniproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionDisplayActivity extends AppCompatActivity {

    TextView tvQuestion;
    TextView tvResult;
    RadioGroup radioGroup;
    RadioButton rbTrueOption;
    RadioButton rbFalseOption;
    Button btnConfirm;
    List<Map<String, Object>> questionList;
    String tournamentName;
    int iCurrentQuestion = 0;
    int iTotalQuestions = 0;
    int iAnswerRight = 0;
    boolean bHasComputedScore = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_display);

        questionList = new ArrayList<>();
        tvQuestion = findViewById(R.id.question);
        tvResult = findViewById(R.id.result);
        radioGroup = findViewById(R.id.radio_group);
        rbTrueOption = findViewById(R.id.true_option);
        rbFalseOption = findViewById(R.id.false_option);


        Intent intent = getIntent();
        tournamentName = intent.getStringExtra("tournamentName");
        if (tournamentName != null) {
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("tournaments").child(tournamentName).child("questions");
            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists()){
                        for(DataSnapshot child : snapshot.getChildren()){
                            Map<String, Object> mapQuestionDetails = new HashMap<>();
                            mapQuestionDetails.put("correct_answer", child.child("correct_answer").getValue(String.class));
                            mapQuestionDetails.put("question", child.child("question").getValue(String.class));
                            mapQuestionDetails.put("type", child.child("type").getValue(String.class));
                            //List<String> incorrect_answers = new ArrayList<>();
                            DataSnapshot snapshotOfIncorrectAnswers = child.child("incorrect_answers");
                            //here just for TrueOrFalse question in order to simplicity. So only one correct or incorrect answer
                            for(DataSnapshot eachAnswer : snapshotOfIncorrectAnswers.getChildren()){
                                //incorrect_answers.add(eachAnswer.getValue(String.class));
                                mapQuestionDetails.put("incorrect_answers", eachAnswer.getValue(String.class));
                                break;
                            }
                            questionList.add(mapQuestionDetails);
                        }
                        iTotalQuestions = questionList.size();
                        Toast.makeText(getApplicationContext(), "Count: " + iTotalQuestions + "  Current: " + iCurrentQuestion, Toast.LENGTH_LONG).show();
                        updateQuestion(iCurrentQuestion);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
//        //for previous button
//        Button btnPrevious = findViewById(R.id.previous);
//        btnPrevious.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                computeScoring();
//                --iCurrentQuestion;
//                updateQuestion(iCurrentQuestion);
//            }
//        });
        //for next button
        Button btnNext = findViewById(R.id.next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bHasComputedScore == false) {
                    bHasComputedScore = true;
                    computeScoring();
                }
                ++iCurrentQuestion;
                updateQuestion(iCurrentQuestion);
            }
        });
        //for confirm button
        btnConfirm = findViewById(R.id.confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int iResult = computeScoring();
                String strDisplay = "Correct answer";
                if(iResult == 0)
                    strDisplay = "Incorrect answer";
                else if(iResult == -1)
                    strDisplay = "Out of index";

                tvResult.setText(strDisplay);
                btnConfirm.setEnabled(false);
                bHasComputedScore = true;
            }
        });
    }

    private int computeScoring(){
        if(iCurrentQuestion < 0 || iCurrentQuestion >= iTotalQuestions){
            return -1;
        }
        String strRightAnswer = questionList.get(iCurrentQuestion).get("correct_answer").toString();
        String strChoice = "true";
        int iSelectedRadioBtnId = radioGroup.getCheckedRadioButtonId();
        if(iSelectedRadioBtnId == R.id.true_option)
        {
        }
        else if(iSelectedRadioBtnId == R.id.false_option)
        {
            strChoice = "false";
        }
        else
        {
            return 0;//do not choose
        }
        if(strChoice.equalsIgnoreCase(strRightAnswer)){
            iAnswerRight++;
            return 1;
        }
        return 0;
    }
    private void updateQuestion(int index){
        if(index < 0){
            //Toast.makeText(getApplicationContext(), "Out of index of questions", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        else if(index >= questionList.size()){
            if(Account.getInstance().getRole() == Role.Player){
                //start the scoring activity
                Intent intent;
                intent = new Intent(QuestionDisplayActivity.this, ScoringResultActivity.class);
                intent.putExtra("scoring", iAnswerRight);
                intent.putExtra("total_questions", iTotalQuestions);
                startActivity(intent);
                finish();
            }
            else if(Account.getInstance().getRole() == Role.Administrator){
                Toast.makeText(getApplicationContext(), "This is last question.", Toast.LENGTH_LONG).show();
            }
            return;
        }
        bHasComputedScore = false;
        Map<String, Object> questionMap = questionList.get(index);
        tvQuestion.setText(questionMap.get("question").toString());
        tvResult.setText("");
        rbTrueOption.setChecked(false);
        rbFalseOption.setChecked(false);
        btnConfirm.setEnabled(true);
    }
}