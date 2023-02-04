package com.example.apptesi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.apptesi.ui.notifications.NotificationsViewModel;

import org.w3c.dom.Text;

public class DeviceActivity extends AppCompatActivity {

    private TextView txtName;
    private TextView txtState;
    private TextView txtIp;
    private TextView txtLabel;
    private Button btnInfo;
    private Button btnCmnd;
    private Button btnToggle;
    private Button btnChgLabel;
    private Activity act;

    private SmartDevice smartDevice;
    private DataSingleton ds= DataSingleton.getInstance();
    private SmartDeviceViewModel smartDeviceViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        act=this;

        Intent intent = getIntent();
        String iString = intent.getStringExtra("device");
        System.out.println("DEVICE: "+iString);

        smartDevice=ds.getSmartDevice(iString);
        txtState = findViewById(R.id.txtStateDevice);
        smartDeviceViewModel =
                new ViewModelProvider(this).get(SmartDeviceViewModel.class);
        smartDeviceViewModel.setInstance(smartDevice);
        smartDeviceViewModel.getStateText().observe(this, txtState::setText);


    }

    @Override
    protected void onResume() {
        super.onResume();
        txtIp = findViewById(R.id.txtIpDevice);
        txtName = findViewById(R.id.txtNameDevice);
        txtLabel = findViewById(R.id.txtLabelDevice);

        btnCmnd = findViewById(R.id.btnCommandDevice);
        btnInfo = findViewById(R.id.btnInfoDevice);
        btnToggle = findViewById(R.id.btnToggleDevice);
        btnChgLabel = findViewById(R.id.btnChgLblDevice);

        txtName.setText(smartDevice.getName());
        txtIp.setText(smartDevice.getIp());
        txtState.setText(smartDevice.getState()==true?"ON":"OFF");
        txtLabel.setText(smartDevice.getLabel());

        btnChgLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(act, ChangeLabelActivity.class);
                myIntent.putExtra("device", smartDevice.getName()); //Optional parameters
                startActivity(myIntent);
            }
        });

        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CustomHttpRequest(smartDevice).makeHttpRequest(HttpRequestType.TOGGLE);
                smartDeviceViewModel.setInstance(smartDevice);
                smartDeviceViewModel.txtState.postValue(smartDevice.getState() == true ? "ON" : "OFF");
            }
        });

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CustomHttpRequest(smartDevice).makeHttpRequest(HttpRequestType.STATE);
                smartDeviceViewModel.setInstance(smartDevice);
                smartDeviceViewModel.txtState.postValue(smartDevice.getState() == true ? "ON" : "OFF");
            }
        });
    }
}