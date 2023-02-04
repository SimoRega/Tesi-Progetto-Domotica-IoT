package com.example.apptesi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.apptesi.ui.notifications.NotificationsViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.w3c.dom.Text;

import java.util.ArrayList;

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

    private LineChart mchart;


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

        mchart= findViewById(R.id.graficoProva);

        mchart.setDragEnabled(true);
        mchart.setScaleEnabled(false);

        ArrayList<Entry> y = new ArrayList<>();
        y.add(new Entry(0,60f));
        y.add(new Entry(1,50f));
        y.add(new Entry(2,70));
        y.add(new Entry(3,30f));
        y.add(new Entry(4,50f));
        y.add(new Entry(5,60f));
        y.add(new Entry(6,65f));

        LineDataSet set1 = new LineDataSet(y,"Dataset 1");
        set1.setFillAlpha(110);

        set1.setColor(Color.RED);
        set1.setLineWidth(3f);
        set1.setValueTextSize(10f);
        set1.setValueTextColor(Color.GREEN);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);
        mchart.setData(data);


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