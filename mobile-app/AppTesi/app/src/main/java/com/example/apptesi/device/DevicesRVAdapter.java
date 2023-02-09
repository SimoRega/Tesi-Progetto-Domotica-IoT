package com.example.apptesi.device;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptesi.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DevicesRVAdapter  extends RecyclerView.Adapter<DevicesRVAdapter.MyViewHolder> {

    private List<SmartDevice> devices;
    private Context context = null;

    public DevicesRVAdapter(Context context, List<SmartDevice> devices){
        this.context = context;
        this.devices = devices;
    }

    @NonNull
    @Override
    public DevicesRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_devices_rv,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesRVAdapter.MyViewHolder holder, int position) {
        holder.bind(devices.get(position));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView txtName = this.itemView.findViewById(R.id.itemDevName);
        private TextView txtIP = this.itemView.findViewById(R.id.itemDevIP);
        private TextView txtLabel = this.itemView.findViewById(R.id.itemDevLabel);

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(SmartDevice s) {
            txtName.setText(s.getName());
            txtIP.setText(s.getIp());
            txtLabel.setText(s.getLabel());
        }
    }
}
