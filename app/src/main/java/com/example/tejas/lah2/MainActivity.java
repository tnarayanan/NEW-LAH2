package com.example.tejas.lah2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button progress;
    TextView topText;
    public EditText fullText;

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
                String fullTextString = fullText.getText().toString();
                Toast.makeText(getApplicationContext(), fullTextString, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), Memorize.class);
                startActivity(intent);
            }
        });

    }
}

