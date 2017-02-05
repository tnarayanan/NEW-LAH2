package com.example.tejas.lah2;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

import static android.widget.Toast.*;

public class Memorize extends AppCompatActivity {
    TextView textSegment;
    int counter = 0;
    Button record;
    private final int SPEECH_RECOGNITION_CODE = 1;
    private String txtOutput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        counter = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorize);
        textSegment = (TextView)findViewById(R.id.textSegment);
        textSegment.setText(MainActivity.stringSentences.get(0));
        record = (Button)findViewById(R.id.record);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpeechToText();
            }
        });


    }
    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
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
                    if(txtOutput == removePunctuation(MainActivity.stringSentences.get(counter).toLowerCase())){
                        counter++;
                        textSegment.setText(MainActivity.stringSentences.get(counter));
                        Toast.makeText(getApplicationContext(), "Nice. Next Sentence", Toast.LENGTH_SHORT);
                    } else {
                        Toast.makeText(getApplicationContext(), "Sorry, try again", Toast.LENGTH_SHORT);
                    }
                }
                break;
            }
        }
    }
    private String removePunctuation(String str) {
        String newString = str;
        String[] punctuation = {"/", ".", ",", "'", "\"", ";", ":", "(", ")", "!", "?"};
        for(int i = 0; i < punctuation.length; i++){
            newString = newString.replace(punctuation[i], "");
        }
        return newString;
    }
}
