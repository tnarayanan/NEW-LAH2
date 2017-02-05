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

import static android.widget.Toast.*;

public class Summarize extends AppCompatActivity {
    TextView textSegment;
    TextView wrongText;
    public static int counter = 0;
    Button record;
    private final int SPEECH_RECOGNITION_CODE = 1;
    private String txtOutput;
    Boolean read = false;
    Boolean summaryActive = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readthrough);
        textSegment = (TextView)findViewById(R.id.textSegment);
        textSegment.setText(MainActivity.stringSentences.get(0));
        wrongText = (TextView) findViewById(R.id.wrongText);
        record = (Button)findViewById(R.id.record);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpeechToText();
            }
        });

    }
    private void startSpeechToText() {

        String finalString;

        if (summaryActive) {

            SimpleSummariser summariser = new SimpleSummariser();
            String summarisedText = summariser.summarise(textSegment.getText().toString(), 3);
            int prevSentenceEnd = 0;

            finalString = "";
            for (int i = 0; i < summarisedText.length(); i++) {
                String currChar = Character.toString(summarisedText.charAt(i));
                if (currChar.equals(".") || currChar.equals("?") || currChar.equals("!")) {
                    finalString += "- ";
                    finalString += summarisedText.substring(prevSentenceEnd, i);
                    finalString += "\n";
                    prevSentenceEnd = i + 2;
                }
            }
        } else {
            finalString = textSegment.getText().toString();
        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, finalString);
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    LENGTH_SHORT).show();
        }
    }
    /**
     * Callback for speech recognition activity
     * */
    @Override
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
                    if(!read) {
                        if (txtOutput.equals(correctWithoutPunc)) {
                            if (summaryActive) {
                                Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
                                counter ++;
                                if (counter < MainActivity.stringSentences.size()) {
                                    textSegment.setText(MainActivity.stringSentences.get(counter));
                                } else {
                                    read = true;
                                    counter = 0;
                                }
                            } else {
                                summaryActive = true;
                            }
                        } else {
                            wrongText.setText(Html.fromHtml("<b>" + txtOutput + "</b> is what I heard, but <b>" + correctWithoutPunc + "</b> is the answer."
                                    + " You got <b>" + Math.round(getPercent(toWords(txtOutput), toWords(correctWithoutPunc)) * 100) + "% </b> of words correct"));

                            summaryActive = false;
                        }
                    }
                }
                break;
            }
        }
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

    private String removePunctuation(String str) {
        String newString = str;
        String[] punctuation = {"/", ".", ",", "\"", ";", ":", "(", ")", "!", "?", "-"};
        for(int i = 0; i < punctuation.length; i++){
            newString = newString.replace(punctuation[i], "");
        }
        return newString;
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
