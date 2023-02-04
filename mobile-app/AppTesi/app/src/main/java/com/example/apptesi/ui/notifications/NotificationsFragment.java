package com.example.apptesi.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.apptesi.CustomHttpRequest;
import com.example.apptesi.DataSingleton;
import com.example.apptesi.HttpRequestType;
import com.example.apptesi.R;
import com.example.apptesi.SmartDevice;
import com.example.apptesi.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private Button btnProva;
    private Button btnProvaToggle;
    private Button btnProvaCommand;
    private View view;
    private DataSingleton ds = DataSingleton.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        btnProva=view.findViewById(R.id.btnProvaInfo);
        btnProva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (SmartDevice sd :
                        ds.getSmartDevices()) {
                    new CustomHttpRequest(sd).makeHttpRequest(HttpRequestType.STATE);
                }
            }
        });

        btnProvaToggle = view.findViewById(R.id.btnProvaToggle);
        btnProvaToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (SmartDevice sd :
                        ds.getSmartDevices()) {
                    new CustomHttpRequest(sd).makeHttpRequest(HttpRequestType.TOGGLE);
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}