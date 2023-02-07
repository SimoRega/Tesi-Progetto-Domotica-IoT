package com.example.apptesi.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.apptesi.CustomHttpRequest;
import com.example.apptesi.DataSingleton;
import com.example.apptesi.HttpRequestType;
import com.example.apptesi.R;
import com.example.apptesi.SearchDeviceActivity;
import com.example.apptesi.device.SmartDevice;
import com.example.apptesi.databinding.FragmentDashboardBinding;
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

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private View view=null;
    private Button btnScan;
    private Button btnToggle;
    private ListView lstViewDevices;
    private DataSingleton ds = DataSingleton.getInstance();
    private boolean isOn = false;

    @Override
    public void onResume() {
        super.onResume();
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        dashboardViewModel.getDevices().observe(getViewLifecycleOwner(),dev->{
            lstViewDevices.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, dev));
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), SearchDeviceActivity.class);
                //myIntent.putExtra("key", "value"); //Optional parameters
                startActivity(myIntent);
            }
        });

        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (SmartDevice sd :
                        ds.getSmartDevices()) {
                    new CustomHttpRequest(sd).makeHttpRequest(HttpRequestType.STATE);
                }
            }
        });

        if(!ds.getMeasures().isEmpty()){
            displayGraph();
        }
    }

    private void displayGraph() {
        LineChart lineChart = view.findViewById(R.id.dashboardChart);

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

        // Add data to the line chart
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < measures.size(); i++) {
            JSONObject measure = measures.get(i);
            float power = 0;
            String totalStartTime ="";
            try {
                power = (float) measure.getDouble("Power");
                totalStartTime = measure.getString("TotalStartTime");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Convert the date-time string to a long value in milliseconds
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            try {
                Date date = df.parse(totalStartTime);
                long time = date.getTime();
                entries.add(new Entry(time, power));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        LineDataSet dataSet = new LineDataSet(entries, "Power");
        // Customize the line chart appearance
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // make the chart curved
        dataSet.setCubicIntensity(0.2f);
        dataSet.setDrawFilled(true); // fill the area underneath the line

        // Create a gradient to fill the area
        /*int colori[] = {Color.BLUE, Color.GREEN};
        Shader shader = new LinearGradient(0, 0, 0, lineChart.getHeight(),colori,null, Shader.TileMode.CLAMP);
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RectShape());
        shapeDrawable.getPaint().setShader(shader);
        dataSet.setFillDrawable(shapeDrawable);

         */
        dataSet.setFillColor(getResources().getColor(R.color.teal_200));
        //dataSet.setFillAlpha(80);
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

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        btnScan = view.findViewById(R.id.btnScan);
        btnToggle = view.findViewById(R.id.btnToggle);
        lstViewDevices = view.findViewById(R.id.lstViewDevices);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}