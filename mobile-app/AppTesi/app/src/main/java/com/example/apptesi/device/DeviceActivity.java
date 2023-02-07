package com.example.apptesi.device;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apptesi.CustomHttpRequest;
import com.example.apptesi.DataSingleton;
import com.example.apptesi.HttpRequestType;
import com.example.apptesi.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DeviceActivity extends AppCompatActivity {

    private TextView txtName;
    private TextView txtState;
    private TextView txtIp;
    private TextView txtLabel;
    private ImageView imgState;
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

    private void makeChart(){
        LineChart lineChart = findViewById(R.id.graficoProva);


        // Disable description text
        lineChart.getDescription().setEnabled(false);

        // Enable touch gestures
        lineChart.setTouchEnabled(true);

        // Enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);

        // Enable pinch zoom to avoid scaling x and y axis separately
        lineChart.setPinchZoom(true);

        // Get the data from DataSingleton
        List<JSONObject> measures = ds.getMeasures();
        System.out.println(measures);

        // Add data to the line chart
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < measures.size(); i++) {
            JSONObject measure = measures.get(i);
            float power = 0;
            String totalStartTime ="";
            try {
                power = (float) measure.getJSONObject("ENERGY").getDouble("ApparentPower");
                totalStartTime = measure.getString("Time");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Convert the date-time string to a long value in milliseconds
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            DateFormat outputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            try {
                Date date = inputFormat.parse(totalStartTime);
                String onlyTime = outputFormat.format(date);
                Date onlyTimeDate = outputFormat.parse(onlyTime);
                long time = onlyTimeDate.getTime();
                //TODO non va con json da server
                entries.add(new Entry(time, power));
                //entries.add(new Entry(i, power));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        LineDataSet dataSet = new LineDataSet(entries, "Power");
        System.out.println("DataSet"+dataSet);
        // Customize the line chart appearance
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // make the chart curved
        dataSet.setCubicIntensity(0.2f);
        dataSet.setDrawFilled(true); // fill the area underneath the line
        dataSet.setFillColor(getResources().getColor(R.color.teal_200));
        dataSet.setColor(getResources().getColor(R.color.teal_700));
        dataSet.setLineWidth(3f);
        dataSet.setDrawCircles(false);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Get the x-axis and format it as date-time
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                // Format the x-axis label as date-time
                DateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                return df.format(new Date((long) value));
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelRotationAngle(-45);
        xAxis.setGranularity(1f); // one hour

        // Refresh the line chart
        lineChart.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println(ds.getMeasures());
        if(!ds.getMeasures().isEmpty()){
            makeChart();
        }
        txtIp = findViewById(R.id.txtIpDevice);
        txtName = findViewById(R.id.txtNameDevice);
        txtLabel = findViewById(R.id.txtLabelDevice);

        btnCmnd = findViewById(R.id.btnMeasureDevice);
        btnInfo = findViewById(R.id.btnInfoDevice);
        btnToggle = findViewById(R.id.btnToggleDevice);
        btnChgLabel = findViewById(R.id.btnChgLblDevice);

        imgState = findViewById(R.id.imgStateDevice);
        imgState.setImageResource(smartDevice.getState()==true? R.drawable.on_dot:R.drawable.off_dot);

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
                imgState.setImageResource(smartDevice.getState()==true? R.drawable.on_dot:R.drawable.off_dot);
            }
        });

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CustomHttpRequest(smartDevice).makeHttpRequest(HttpRequestType.STATE);
                smartDeviceViewModel.setInstance(smartDevice);
                smartDeviceViewModel.txtState.postValue(smartDevice.getState() == true ? "ON" : "OFF");
                imgState.setImageResource(smartDevice.getState()==true? R.drawable.on_dot:R.drawable.off_dot);

            }
        });

        btnCmnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CustomHttpRequest("http://192.168.1.58:80/",List.of("energyDevice=",smartDevice.getName())).makeHttpRequest(HttpRequestType.SERVER);
            }
        });
    }
}