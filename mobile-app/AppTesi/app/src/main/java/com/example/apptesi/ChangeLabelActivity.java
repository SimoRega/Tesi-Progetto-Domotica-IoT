package com.example.apptesi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChangeLabelActivity extends AppCompatActivity {

    private EditText editText;
    private Button btnChange;
    private SmartDevice smartDevice;
    private DataSingleton ds = DataSingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_label);


        Intent intent = getIntent();
        String iString = intent.getStringExtra("device");

        smartDevice = ds.getSmartDevice(iString);

        editText = findViewById(R.id.editTxtLabel);
        btnChange = findViewById(R.id.btnChangeLabel);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smartDevice.setLabel(editText.getText().toString());
                finish();
            }
        });


    }
}