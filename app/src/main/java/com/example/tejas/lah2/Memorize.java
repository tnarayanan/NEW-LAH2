package com.example.tejas.lah2;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.sf.classifier4J.summariser.SimpleSummariser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class Memorize extends AppCompatActivity {

    TextView textSegment;
    TextView wrongText;
    TextView currentStage;

    Button record;
    Button next;
    Button override;

    final int NUM_OF_PARAGRAPHS = MainActivity.stringParagraphs.size();
    private final int SPEECH_RECOGNITION_CODE = 1;
    int COUNT = 0;

    int levelsDown = 0;

    Boolean nextStageMoved = true;

    String MODE = "Read";
    String txtOutput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorize);

        //for color
        // getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#90AFC5")));
        textSegment = (TextView) findViewById(R.id.textSegment);
        wrongText = (TextView) findViewById(R.id.wrongText);
        currentStage = (TextView) findViewById(R.id.stage);

        record = (Button) findViewById(R.id.record);
        next = (Button) findViewById(R.id.next);
        override = (Button) findViewById(R.id.override);

        newSection();

        override.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((COUNT + 1) % NUM_OF_PARAGRAPHS == 0) {
                    nextStageScreen();
                } else {
                    COUNT++;
                    newSection();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MODE == "Read") {
                    MODE = "Summarize";
                } else if (MODE == "Summarize") {
                    MODE = "Memorize";
                }
                nextStageMoved = true;
                System.out.println(MODE);

                COUNT++;
                newSection();
            }
        });

    }

    private void newSection() {

        System.out.println(COUNT);

        textSegment.setText(MainActivity.stringParagraphs.get(COUNT % NUM_OF_PARAGRAPHS));
        next.setVisibility(View.INVISIBLE);
        record.setVisibility(View.VISIBLE);

        currentStage.setText(MODE);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpeechToText();
            }
        });
    }

    private void startSpeechToText() {
        String finalMode = "";
        switch (MODE) {
            case "Read":
                finalMode = textSegment.getText().toString();
                break;
            case "Summarize":
                SimpleSummariser summariser = new SimpleSummariser();
                String summarisedText = summariser.summarise(textSegment.getText().toString(), 2);
                int prevSentenceEnd = 0;

                finalMode = "";
                for (int i = 0; i < summarisedText.length(); i++) {
                    String currChar = Character.toString(summarisedText.charAt(i));
                    if (currChar.equals(".") || currChar.equals("?") || currChar.equals("!")) {
                        finalMode += "- ";
                        finalMode += summarisedText.substring(prevSentenceEnd, i);
                        finalMode += "\n";
                        prevSentenceEnd = i + 2;
                    }
                }
                break;
            case "Memorize":
                finalMode = "";
        }
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, finalMode);
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    txtOutput = text;
                    txtOutput = txtOutput.toLowerCase();
                    String correctWithoutPunc = removePunctuation(textSegment.getText().toString()).toLowerCase();

                    if (txtOutput.equals(correctWithoutPunc)) {
                        wrongText.setText("");
                        Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_LONG).show();
                        wrongText.setText("");
                        if (levelsDown > 0) {
                            System.out.println("BAD------------------------------");
                            levelsDown--;
                            if (MODE == "Read") {
                                MODE = "Summarize";
                            } else if (MODE == "Summarize") {
                                MODE = "Memorize";
                            }
                            COUNT += NUM_OF_PARAGRAPHS;
                            newSection();

                        } else {
                            System.out.println("Here----------------");
                            if ((COUNT + 1) % NUM_OF_PARAGRAPHS == 0) {
                                nextStageScreen();
                            } else {
                                COUNT++;
                                newSection();
                            }
                        }
                    } else {
                        wrongText.setText(Html.fromHtml("<b>" + txtOutput + "</b> is what I heard, but <b>" + correctWithoutPunc + "</b> is the answer."
                                + " You got <b>" + Math.round(getPercent(toWords(txtOutput), toWords(correctWithoutPunc)) * 100) + "% </b> of words correct"));
                        if (COUNT >= NUM_OF_PARAGRAPHS) {
                            levelsDown++;
                            if (MODE == "Memorize") {
                                MODE = "Summarize";
                            } else if (MODE == "Summarize") {
                                MODE = "Read";
                            }
                            COUNT -= NUM_OF_PARAGRAPHS;
                        }
                        newSection();
                    }

                }
                break;
            }
        }
    }

    private void nextStageScreen() {
        System.out.println("IN NEXT STAGE SCREEN");
        textSegment.setText("Move on to the next section!");
        next.setVisibility(View.VISIBLE);
        record.setVisibility(View.INVISIBLE);
    }

    private String removePunctuation(String str) {
        String newString = str;
        String[] punctuation = {"/", ".", ",", "\"", ";", ":", "(", ")", "!", "?", "-"};
        for(int i = 0; i < punctuation.length; i++){
            newString = newString.replace(punctuation[i], "");
        }
        return newString;
    }

    private double getPercent(ArrayList<String> userText, ArrayList<String> correctText) {
        int correct1 = 0;
        int correct2 = 0;

        System.out.println(Arrays.toString(toWords(txtOutput).toArray()));

        for(int i = 0; i< correctText.size(); i++){
            while (userText.size() < correctText.size()) {
                userText.add("");
            }
            int index = userText.indexOf(correctText.get(i));
            if(userText.contains(correctText.get(i))){
                if (Math.abs(index - i) < 3) {
                    correct1++;
                }
            }

            System.out.println("index: " + index);
            System.out.println("userContainsCorrect: " + Boolean.toString(userText.contains(correctText.get(i))));
            System.out.println("Math.abs: " + Boolean.toString(Math.abs(index - i) < 3));
            System.out.println("Correct: " + correct1);

        }

        for(int i = 0; i< correctText.size(); i++){
            while (correctText.size() < userText.size()) {
                correctText.add("");
            }
            int index = correctText.indexOf(userText.get(i));
            if(correctText.contains(userText.get(i))){
                if (Math.abs(index - i) < 3) {
                    correct2++;
                }
            }

            System.out.println("index: " + index);
            System.out.println("userContainsCorrect: " + Boolean.toString(correctText.contains(correctText.get(i))));
            System.out.println("Math.abs: " + Boolean.toString(Math.abs(index - i) < 3));
            System.out.println("Correct: " + correct2);

        }

        int finalCorrect = Math.min(correct1, correct2);

        return ((double) finalCorrect)/(double) (correctText.size());
    }

    private  ArrayList<String> toWords(String str) {
        ArrayList<String> words = new ArrayList<String>();
        String str1 = str;
        int lastWord = 0;
        for(int i = 0; i < str.length(); i++){
            if(Character.toString(str1.charAt(i)).equals(" ") || Character.toString(str1.charAt(i)).equals("!") || Character.toString(str1.charAt(i)).equals("?") || Character.toString(str1.charAt(i)).equals(".")){
                words.add(str1.substring(lastWord, i));
                if(Character.toString(str1.charAt(i)).equals(".") || Character.toString(str1.charAt(i)).equals("!") || Character.toString(str1.charAt(i)).equals("?")){
                    lastWord = i + 2;
                } else {
                    lastWord = i + 1;
                }
            } else if (i == str1.length() - 1) {
                words.add(str1.substring(lastWord));
            }
        }
        return words;
    }
}
