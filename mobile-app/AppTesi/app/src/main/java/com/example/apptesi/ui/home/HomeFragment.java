package com.example.apptesi.ui.home;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptesi.DataSingleton;
import com.example.apptesi.device.DeviceActivity;
import com.example.apptesi.R;
import com.example.apptesi.databinding.FragmentHomeBinding;
import com.example.apptesi.device.CustomRVIListener;
import com.example.apptesi.device.DevicesRVAdapter;

import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private View view;
    private RecyclerView recyclerView ;
    private DataSingleton ds = DataSingleton.getInstance();
    private boolean isOn = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        recyclerView = view.findViewById(R.id.devicesRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DevicesRVAdapter a = new DevicesRVAdapter(getContext(),ds.getSmartDevices());
        recyclerView.setAdapter(a);

        recyclerView.addOnItemTouchListener(new CustomRVIListener(getActivity(), recyclerView, new CustomRVIListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Map<String, String> map = ds.getDevices();
                String key = (String) map.keySet().toArray()[position];
                /*List<Map.Entry<String, String>> entries = new ArrayList<>(map.entrySet());
                Map.Entry<String, String> entry = entries.get(position);*/
                CallActivity(key);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void CallActivity(String dev){
        Intent i = new Intent(getContext(), DeviceActivity.class);
        i.putExtra("device",dev );
        startActivity(i);
    }
}