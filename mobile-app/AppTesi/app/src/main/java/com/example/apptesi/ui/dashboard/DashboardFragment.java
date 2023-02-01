package com.example.apptesi.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.apptesi.CustomHttpRequest;
import com.example.apptesi.DataSingleton;
import com.example.apptesi.R;
import com.example.apptesi.SearchDeviceActivity;
import com.example.apptesi.databinding.FragmentDashboardBinding;

import java.util.Map;

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
                for (Map.Entry<String, String> e :
                        ds.getDevices().entrySet()) {
                    if(e.getKey().contains("tasmota")){
                        new Thread(new CustomHttpRequest("http://"+e.getValue()+"/?m=1&o=1",false)).start();
                    }else{
                        if(isOn){
                            new Thread(new CustomHttpRequest("http://192.168.1.138:8081/zeroconf/switch","{\"deviceid\": \"\",\"data\": {\"switch\": \"off\"} }")).start();
                        }else{
                            new Thread(new CustomHttpRequest("http://192.168.1.138:8081/zeroconf/switch","{\"deviceid\": \"\",\"data\": {\"switch\": \"on\"} }")).start();
                        }
                        isOn=!isOn;
                    }
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

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}