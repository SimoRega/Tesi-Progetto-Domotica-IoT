package com.example.apptesi.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.apptesi.DataSingleton;

import java.util.List;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private MutableLiveData<String> txtTotPower;
    private MutableLiveData<String> txtAvgPower;
    private MutableLiveData<String> txtAvgVoltage;
    private MutableLiveData<String> txtAvgAmpere;
    private DataSingleton ds = DataSingleton.getInstance();

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
        txtAvgPower = new MutableLiveData<>();
        txtAvgPower.setValue("");
        txtAvgVoltage = new MutableLiveData<>();
        txtAvgVoltage.setValue("");
        txtAvgAmpere = new MutableLiveData<>();
        txtAvgAmpere.setValue("");
        txtTotPower = new MutableLiveData<>();
        txtTotPower.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<String>> getDevices() {

        return new MutableLiveData<>(ds.getDevicesAsList());
    }

    public MutableLiveData<String> getTxtAvgAmpere() {
        return new MutableLiveData<>(String.format("%.2f",ds.getAvgAmpere()));
    }

    public MutableLiveData<String> getTxtAvgPower() {
        return new MutableLiveData<>(String.format("%.2f",ds.getAvgPower()));
    }

    public MutableLiveData<String> getTxtAvgVoltage() {
        return new MutableLiveData<>(String.format("%.2f",ds.getAvgVoltage()));
    }

    public MutableLiveData<String> getTxtTotPower() {
        return new MutableLiveData<>(String.format("%.2f",ds.getTotPower()));
    }

}