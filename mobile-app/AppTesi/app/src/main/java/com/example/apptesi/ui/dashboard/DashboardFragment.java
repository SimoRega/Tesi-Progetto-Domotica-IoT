package com.example.apptesi.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private View view=null;
    private Button btnScan;
    private Button btnToggle;
    private ListView lstViewDevices;

    private TextView txtTotPower;
    private TextView txtAvgPower;
    private TextView txtAvgVoltage;
    private TextView txtAvgAmpere;
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
        dashboardViewModel.getTxtTotPower().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                txtTotPower.setText(s);
            }
        });
        dashboardViewModel.getTxtAvgAmpere().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                txtAvgAmpere.setText(s);
            }
        });
        dashboardViewModel.getTxtAvgVoltage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                txtAvgVoltage.setText(s);
            }
        });
        dashboardViewModel.getTxtAvgPower().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                txtAvgPower.setText(s);
            }
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
    }
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        btnScan = view.findViewById(R.id.btnScan);
        btnToggle = view.findViewById(R.id.btnToggle);
        lstViewDevices = view.findViewById(R.id.lstViewDevices);

        txtAvgAmpere = view.findViewById(R.id.txtAvgAmpere);
        txtAvgPower = view.findViewById(R.id.txtAvgPower);
        txtAvgVoltage = view.findViewById(R.id.txtAvgVoltage);
        txtTotPower = view.findViewById(R.id.txtTotPower);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}