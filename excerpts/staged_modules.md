Changes 4 files
Unversioned Files 7 files

# ---Notes---

1. ans_correct and ans_incorrect to /drawables

# ---VALUES---

## colors.xml

<color name="accentDialog">#F79145</color>

## strings.xml

<string name="dialogAnswer1">Your answer is</string>  
<string name="dialogResponse">CORRECT!</string>  

<string name="dialogAnswer2">The correct answer is:</string>  
<string name="dialogCorrect">The Answer</string>

# ---JAVA---

## Question.java
public String getCorrectAnswerDescription() {
        return choices.get(correctAnswerKey);
    }
   
##  QuestionResults.java
import static els.quiz.geode.QuestionManager.questionIndex;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class QuestionResults {

    private Context context;
    private AlertDialog dialog;
    private String userAnswer;
    private String correctAnswerKey;
    private String correctAnswerDesc;
    private boolean isCorrect;

    // Constructor
    public QuestionResults(Context context, String userAnswer, String correctAnswerKey, String correctAnswerDesc, boolean isCorrect) {
        this.context = context;
        this.userAnswer = userAnswer;
        this.correctAnswerKey = correctAnswerKey;
        this.correctAnswerDesc = correctAnswerDesc;
        this.isCorrect = isCorrect;
    }

    // Method to display the result dialog
    public void showResultDialog() {
        // Inflate custom layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_question_result, null);

        // Find views in the custom layout
        ImageView dialogSymbol = dialogView.findViewById(R.id.result_symbol);
        TextView dialogResult = dialogView.findViewById(R.id.dialog_result);
        TextView dialogCorrect = dialogView.findViewById(R.id.dialog_correct);
        Button nextButton = dialogView.findViewById(R.id.btnNext);

        // Set the text for result and correct answer
        if (isCorrect) {
            dialogResult.setText("Correct!");
            dialogSymbol.setImageResource(R.drawable.ans_correct);
        } else {
            dialogResult.setText("Incorrect!");
            dialogSymbol.setImageResource(R.drawable.ans_incorrect);
        }

        dialogCorrect.setText(correctAnswerDesc);

        // Build and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Prevent the dialog from closing when clicking outside
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();

        // Set up the Next button action
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss(); // Close the dialog

                // Check if there are more questions
                if (questionIndex != QuestionManager.questionList.size()) {
                    // Load the next question
                    ((QuizActivity) context).loadNextQuestion();
                } else {
                    // Navigate to ResultsActivity
                    Intent intent = new Intent(context, ResultsActivity.class);
                    context.startActivity(intent);
                    ((QuizActivity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });
    }


}

## QuizActivity.java
private void choiceOnClick(String choiceKey) {
        String correctAnswerKey = question.getCorrectAnswerKey();
        String correctAnswerDesc = question.getCorrectAnswerDescription();
        boolean isCorrect = choiceKey.equals(correctAnswerKey);

        // Update the user's score if the answer is correct
        if (isCorrect) {
            int currentScore = User.currentUser.getScore();
            User.currentUser.setScore(currentScore + 1);
        }

        // Show result dialog
        QuestionResults resultDialog = new QuestionResults(
                QuizActivity.this,
                choiceKey,
                correctAnswerKey,
                correctAnswerDesc,
                isCorrect
        );
        resultDialog.showResultDialog();
    }
    
  # ---DRAWABLES---
  ## dialog_background.xml
  <?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="@color/accentDialog" />
    <corners android:radius="16dp" />
    <stroke android:width="2dp" android:color="@color/accent" />
</shape>

## dialog_correct.xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="@color/secondary" />
    <corners android:radius="10dp" />
</shape>

## dialog_next_button.xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="@color/secondary" />
    <corners android:radius="0dp" android:topLeftRadius="0dp" android:topRightRadius="0dp" android:bottomLeftRadius="16dp" android:bottomRightRadius="16dp"/>
</shape>

# ---LAYOUT---
## dialog_question_result.xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:paddingTop="32dp"
    android:background="@drawable/dialog_background"
    android:elevation="8dp">

    <ImageView
        android:id="@+id/result_symbol"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/ans_correct"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        />

    <TextView
        android:id="@+id/dialog_text1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/dialogAnswer1"
        android:textColor="@color/secondary"
        android:textSize="20sp"
        android:fontFamily="@font/manrope_regular"
        app:layout_constraintTop_toBottomOf="@id/result_symbol"
        android:layout_marginTop="16sp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <TextView
        android:id="@+id/dialog_result"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/dialogResponse"
        android:textColor="@color/secondary"
        android:textSize="26sp"
        android:fontFamily="@font/manrope_bold"
        app:layout_constraintTop_toBottomOf="@id/dialog_text1"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <TextView
        android:id="@+id/dialog_text2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/dialogAnswer2"
        android:textColor="@color/secondary"
        android:textSize="16sp"
        android:fontFamily="@font/manrope_regular"
        app:layout_constraintTop_toBottomOf="@id/dialog_result"
        android:layout_marginTop="24sp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <TextView
        android:id="@+id/dialog_correct"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="@string/dialogCorrect"
        android:background="@drawable/dialog_correct"
        android:paddingHorizontal="20dp"
        android:paddingVertical="10dp"
        android:textAlignment="center"
        android:textColor="@color/primary"
        android:textSize="16sp"
        android:fontFamily="@font/manrope_regular"
        app:layout_constraintTop_toBottomOf="@id/dialog_text2"
        android:layout_marginTop="6sp"
        android:layout_marginHorizontal="20sp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />


    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnNext"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Next Question"
        app:cornerRadius="50dp"
        android:paddingVertical="10dp"
        android:background="@drawable/dialog_next_button"
        app:backgroundTint="@color/tertiary"
        android:textColor="@color/primary"
        android:fontFamily="@font/manrope_bold"
        app:layout_constraintTop_toBottomOf="@id/dialog_correct"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        android:layout_marginTop="32dp" />

</androidx.constraintlayout.widget.ConstraintLayout>


