package com.example.apptesi;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SmartDeviceViewModel extends ViewModel {

    public final MutableLiveData<String> txtState;
    private DataSingleton ds = DataSingleton.getInstance();
    private SmartDevice smartDevice;

    public SmartDeviceViewModel() {
        txtState = new MutableLiveData<>();
    }

    public void setInstance(SmartDevice s){
        smartDevice=ds.getSmartDevice(s.getName());
        if(smartDevice != null){
            txtState.setValue(smartDevice.getState()==true?"ON":"OFF");
        }
    }

    public LiveData<String> getStateText() {
        return txtState;
    }
}