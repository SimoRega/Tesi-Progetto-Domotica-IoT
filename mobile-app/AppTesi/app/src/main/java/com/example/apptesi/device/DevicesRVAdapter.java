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

    private List<Map.Entry<String,String>> devices;
    private Context context = null;

    public DevicesRVAdapter(Context context, Map<String,String> devices){
        this.context = context;
        this.devices = new ArrayList<>(devices.entrySet());
    }

    @NonNull
    @Override
    public DevicesRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_devices_rv,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesRVAdapter.MyViewHolder holder, int position) {
        Map.Entry<String, String> entry = devices.get(position);
        holder.bind(entry);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView txtName = this.itemView.findViewById(R.id.itemDevName);
        private TextView txtIP = this.itemView.findViewById(R.id.itemDevIP);

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Map.Entry<String, String> s) {
            txtName.setText(s.getKey());
            txtIP.setText(s.getValue());
        }
    }
}
