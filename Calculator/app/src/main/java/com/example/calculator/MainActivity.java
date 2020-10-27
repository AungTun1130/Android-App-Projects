package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    Button AddBtn;
    EditText firstNumText;
    EditText secondNumText;
    TextView ResultTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AddBtn = (Button) findViewById(R.id.AddBtn);
        firstNumText = (EditText) findViewById(R.id.firstNumText);
        secondNumText = (EditText) findViewById(R.id.secondNumText);
        ResultTextView = (TextView) findViewById(R.id.resultTextView);

        AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num1=Integer.parseInt(firstNumText.getText().toString());
                int num2=Integer.parseInt(secondNumText.getText().toString());
                int result = num1 + num2;
                ResultTextView.setText(result + "");
            }
        });
    }
}