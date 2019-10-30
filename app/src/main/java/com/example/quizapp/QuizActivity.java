package com.example.quizapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    // PART 6 hightscore share (definde what we waht to send back value = score )
 public static final String EXTRA_SCORE = "extraScore ";

 private static final long  COUNTDOWN_IN_MILLIS= 30000;


private TextView textViewQuestion;
private TextView textViewScore;
private TextView textViewQuestionCount;
private TextView textViewCountDown;
private RadioGroup rbGroup;
private RadioButton rd1;
private RadioButton rd2;
private RadioButton rd3;
private Button buttonConfirmNext;

private ColorStateList textColorDefaultRb; //reset the color of the txt to default

private ColorStateList  textColorDefaultCd;
private CountDownTimer countDownTimer;
private long timeLeftInMillis;


private List<Question> questionList;
private int questionCounter; // how many question we are show
private int questionCountTotal; // nbr totale of question
private Question currentQuestion; //

private int score; //
private boolean answered; // showed the next question if the 1st question is answe
private  long backPressedTime ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        textViewQuestion = findViewById(R.id.text_view_question);
        textViewScore = findViewById(R.id.text_view_score);
        textViewQuestionCount = findViewById(R.id.text_view_question_count);
        textViewCountDown = findViewById(R.id.text_view_countdown);
        rbGroup = findViewById(R.id.radio_group);
        rd1 = findViewById(R.id.radio_button1);
        rd2 = findViewById(R.id.radio_button2);
        rd3 = findViewById(R.id.radio_button3);
        buttonConfirmNext = findViewById(R.id.button_confirm_next);


        textColorDefaultRb = rd1.getTextColors(); //

        textColorDefaultCd = textViewCountDown.getLinkTextColors();

        QuizDbHelper dbHelper = new QuizDbHelper( this);
        questionList = dbHelper.getAllQuestions();
        questionCountTotal = questionList.size(); // to get total question
        Collections.shuffle(questionList);

        showNextQuestion();//
//v5: 7 min
        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if question was not answerd or not == answerd is false
                if(!answered) {
                      // check if radiob selected
                      if(rd1.isChecked() || rd2.isChecked()|| rd3.isChecked()){

                          checkAnswer();
                    }else {

                        Toast.makeText(QuizActivity.this ,  "please select an answer" ,Toast.LENGTH_LONG).show();
                    }
                } else{

                     showNextQuestion();
                }
            }
        });
    }


    private void showNextQuestion(){
   // reset text  color of our radio button the question red or green
     rd1.setTextColor(textColorDefaultRb);
     rd2.setTextColor(textColorDefaultRb);
     rd3.setTextColor(textColorDefaultRb);
     rbGroup.clearCheck();  // clear the selection (rb inselected)
  // v 5 3.36 only if we hav any question left we can show the next question

    if(questionCounter < questionCountTotal){
        currentQuestion = questionList.get(questionCounter); //currentquesti
// 4.10 min
      textViewQuestion.setText(currentQuestion.getQuestion());
      rd1.setText(currentQuestion.getOption1());
        rd2.setText(currentQuestion.getOption2());
        rd3.setText(currentQuestion.getOption3());

        questionCounter++;
       //4.50
         textViewQuestionCount.setText("Question: " + questionCounter + "/" + questionCountTotal);
        answered = false; //c
        buttonConfirmNext.setText("Confirm"); // switch to next or confirm
       // part 7
        timeLeftInMillis = COUNTDOWN_IN_MILLIS;
        startCountDoun();

    }else{

        finishQuiz();
    }
    }
// part 7
  private void startCountDoun(){
      countDownTimer = new CountDownTimer(timeLeftInMillis , 1000) {
          @Override
          public void onTick(long millisUntilFinished) {
              timeLeftInMillis = millisUntilFinished;
              updateCountDownText();
          }

          @Override
          public void onFinish() {

              timeLeftInMillis = 0;
              updateCountDownText();
              checkAnswer();
          }
      }.start(); // start min 3:46 part7

  }


  private void updateCountDownText(){
        int minutes = (int) (timeLeftInMillis / 1000) / 60; //we get ethe time em minute
        int seconds = (int) (timeLeftInMillis / 1000) % 60; // le reste de la dic en minute
         String timeFormated = String.format(Locale.getDefault(), "%02d:%02d:" , minutes , seconds);

         textViewCountDown.setText(timeFormated);

         if(timeLeftInMillis < 10000){

             textViewCountDown.setTextColor(Color.RED);
         }else {

             textViewCountDown.setTextColor(textColorDefaultCd);
         }





  }


   private void checkAnswer() {
       // reset our anserder boolen to true
       // because our question was answered
       answered = true;
       countDownTimer.cancel(); // si en valide une question on doit arreter lecompter
       // selected radio butoo
       // return l'id of any  radio buton  was cheked et le stock ai rbselected
       RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
       //return id du radio button that was cheked in our radio group|| return the selected b to a number
       int answerNr = rbGroup.indexOfChild(rbSelected) + 1; // v5 min 9


       if (answerNr == currentQuestion.getAnswerNr()) {

           score++;
           textViewScore.setText("Score, " + score);

       }

       showSolution();
   }

          private void showSolution(){

            rd1.setTextColor(Color.RED);
           rd2.setTextColor(Color.RED);
           rd3.setTextColor(Color.RED);


           switch (currentQuestion.getAnswerNr()) {
               case 1:
                   rd1.setTextColor(Color.GREEN);
                   textViewQuestion.setText(" Answer 1 is correct");
                   break;

               case 2:
                   rd2.setTextColor(Color.GREEN);
                   textViewQuestion.setText(" Answer 2 is correct");
                   break;

               case 3:
                   rd3.setTextColor(Color.GREEN);
                   textViewQuestion.setText(" Answer 3 is correct");
                   break;

           }
           if (questionCounter < questionCountTotal) {

                 buttonConfirmNext.setText("Next");

           }else{

               buttonConfirmNext.setText("Finish");
           }
       }

    private void  finishQuiz(){
        // part 6 send the result
        Intent resultIntent = new Intent();
        //
        resultIntent.putExtra(EXTRA_SCORE, score);
        setResult(RESULT_OK , resultIntent);
        finish();

    }

    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {

            finishQuiz();

        } else {

            Toast.makeText(this, "press back again to finish ", Toast.LENGTH_SHORT).show();

        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countDownTimer == null){

            countDownTimer.cancel();
        }
    }
}
