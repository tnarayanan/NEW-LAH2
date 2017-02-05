package com.example.tejas.lah2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button progress;
    TextView topText;
    EditText fullText;
    String fullTextString;
    public static ArrayList<String> stringSentences;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fullText = (EditText)findViewById(R.id.fullText);
        topText = (TextView)findViewById(R.id.topText);
        progress = (Button)findViewById(R.id.start);

        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullTextString = fullText.getText().toString();
                stringSentences = makeParagraphs(fullTextString);
                Toast.makeText(getApplicationContext(), stringSentences.size() + "", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), ReadThrough.class);
                startActivity(intent);
            }
        });

    }

    public ArrayList<String> makeParagraphs(String str){
        ArrayList<String> sentences = new ArrayList<String>();
        int lastPeriod = 0;
        for(int i = 0; i < str.length(); i++){
            if(Character.toString(str.charAt(i)).equals("\n")) {
                sentences.add(str.substring(lastPeriod, i));
                i++;
                lastPeriod = i + 1;
            }
        }
        sentences.add(str.substring(lastPeriod, str.length()));
        return sentences;
    }
}

