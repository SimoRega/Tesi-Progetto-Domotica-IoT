package com.example.apptesi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class SearchDeviceActivity extends AppCompatActivity {

    private Button btnScan;
    private ListView listViewIp;
    private Context ctx=null;
    private DataSingleton ds = DataSingleton.getInstance();

    private ArrayList<String> ipList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device);

        ctx=this;
        btnScan = (Button)findViewById(R.id.scan);
        listViewIp = (ListView)findViewById(R.id.lstViewIP);


        ipList = new ArrayList();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, ipList);
        listViewIp.setAdapter(adapter);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanIpTask s=new ScanIpTask(ctx,ipList,adapter);
                s.execute();
            }
        });

        listViewIp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int position, long mylng) {
                System.out.println("PRESSED");
                String s = (String) listViewIp.getItemAtPosition(position).toString();
                String spl[]=s.split(" /");
                System.out.println(spl[0]+" "+spl[1]);
                ds.addDevice(spl[0],spl[1]);

                System.out.println(ds.getDevices());
            }
        });

    }

}