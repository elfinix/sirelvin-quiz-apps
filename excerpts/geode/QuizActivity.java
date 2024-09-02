package els.quiz.geode;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import static els.quiz.geode.QuestionManager.questionIndex;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedHashMap;

public class QuizActivity extends AppCompatActivity {

    private TextView questionNumber, questionText;
    private Button btnChoiceA, btnChoiceB, btnChoiceC, btnChoiceD;
    private Button btnSeeImage;
    private Button btnHelp;

    private Question question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz);

        initializeQuizQuestion();
        loadQuestion();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backPressed();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

    }

    private void initializeQuizQuestion() {
        questionNumber = findViewById(R.id.quizNumber);
        questionText = findViewById(R.id.quizQuestion);

        btnChoiceA = findViewById(R.id.btnChoiceA);
        btnChoiceB = findViewById(R.id.btnChoiceB);
        btnChoiceC = findViewById(R.id.btnChoiceC);
        btnChoiceD = findViewById(R.id.btnChoiceD);

        btnSeeImage = findViewById(R.id.btnImg);
        btnHelp = findViewById(R.id.btnHelp);
    }

    private void loadQuestion() {
        // Retrieve the question
        question = QuestionManager.questionList.get(String.valueOf(questionIndex));

        // Set the questions
        questionNumber.setText("Question " + questionIndex + " out of " + QuestionManager.questionList.size());
        questionText.setText(question.getQuestionText());

        // Retrieve the choices
        LinkedHashMap<String, String> choices = question.getChoices();

        // Set the button choices
        btnChoiceA.setText(choices.get("A"));
        btnChoiceB.setText(choices.get("B"));
        btnChoiceC.setText(choices.get("C"));
        btnChoiceD.setText(choices.get("D"));

        // Set image button if applicable
        if (question.hasImage()) {
            btnSeeImage.setVisibility(View.VISIBLE);
            btnSeeImage.setEnabled(true);
        } else {
            btnSeeImage.setVisibility(View.GONE);
            btnSeeImage.setEnabled(false);
        }

        // Set button listeners
        btnSeeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageOnClick();
            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpOnClick();
            }
        });

        // Set choice listeners
        btnChoiceA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceOnClick("A");
            }
        });

        btnChoiceB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceOnClick("B");
            }
        });

        btnChoiceC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceOnClick("C");
            }
        });

        btnChoiceD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceOnClick("D");
            }
        });

    }

    private void imageOnClick() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_image, null);

        // Find the ImageView and Return button in the dialog layout
        ImageView imageView = dialogView.findViewById(R.id.dialogImageView);
        Button returnButton = dialogView.findViewById(R.id.dialogReturnButton);

        // Determine which image to show based on questionIndex
        int imageResource = getImageResourceForQuestion(questionIndex);
        imageView.setImageResource(imageResource);

        // Build and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
        builder.setTitle("Analyze the image carefully!");
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Set up the Return button action
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Close the dialog
            }
        });
    }

    private int getImageResourceForQuestion(int questionIndex) {
        String resourceName = "q" + questionIndex;
        int resourceId = getResources().getIdentifier(resourceName, "raw", getPackageName());

        if (resourceId == 0) {
            return R.raw.graphics_logo;
        }

        return resourceId;
    }


    private void helpOnClick() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_instructions, null);

        // Find the Return button in the dialog layout
        Button returnButton = dialogView.findViewById(R.id.dialogReturnButton);

        // Build and show the dialog with a title
        AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
        builder.setTitle("Instructions");
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        // Set up the Return button action
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Close the dialog
            }
        });
    }

    private void choiceOnClick(String choiceKey) {
        String correctAnswerKey = question.getCorrectAnswerKey();

        if (choiceKey.equals(correctAnswerKey)) {
            int currentScore = User.currentUser.getScore();
            User.currentUser.setScore(currentScore + 1);
        }

        if (questionIndex != QuestionManager.questionList.size()) {
            loadNextQuestion();
        } else {
            FrameLayout overlayLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.loading_overlay, null);

            FrameLayout rootView = findViewById(android.R.id.content);
            rootView.addView(overlayLayout);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    rootView.removeView(overlayLayout);
                    Intent intent = new Intent(QuizActivity.this, ResultsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }, 2000);
        }
    }


    private void loadNextQuestion() {
        questionIndex++;
        refreshIntent();
    }

    private void refreshIntent() {
        startActivity(getIntent());
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Finish the current activity
                finish();
            }
        }, 1000);
    }


    public void backPressed() {
        // Create a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Are you sure you want to quit?");
        builder.setMessage("All the progress you have now in the quiz will be lost.");

        builder.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(QuizActivity.this, DashboardActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}