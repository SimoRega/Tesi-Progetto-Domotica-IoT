package com.example.apptesi.device;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.apptesi.DataSingleton;
import com.example.apptesi.R;
import com.example.apptesi.device.SmartDevice;

public class SmartDeviceViewModel extends ViewModel {

    public final MutableLiveData<String> txtState;
    public final MutableLiveData<Integer> imgState;
    private DataSingleton ds = DataSingleton.getInstance();
    private SmartDevice smartDevice;

    public SmartDeviceViewModel() {
        txtState = new MutableLiveData<>();
        imgState = new MutableLiveData<>();
    }

    public void setInstance(SmartDevice s){
        smartDevice=ds.getSmartDevice(s.getName());
        if(smartDevice != null){
            txtState.setValue(smartDevice.getState()==true?"ON":"OFF");
            imgState.setValue(smartDevice.getState()==true? R.drawable.on_dot:R.drawable.off_dot);
        }
    }

    public LiveData<String> getStateText() {
        return txtState;
    }

    public MutableLiveData<Integer> getImgState() {
        return imgState;
    }
}