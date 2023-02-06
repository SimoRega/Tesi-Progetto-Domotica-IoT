package com.example.apptesi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.apptesi.DataSingleton;
import com.example.apptesi.R;
import com.example.apptesi.ScanIpTask;
import com.example.apptesi.device.SmartDevice;

import java.util.ArrayList;

public class SearchDeviceActivity extends AppCompatActivity {

    private Button btnScan;
    private Button btnAdd;
    private ListView listViewIp;
    private Context ctx=null;
    private DataSingleton ds = DataSingleton.getInstance();
    private ArrayList<String> tmpSelected;
    private ArrayList<String> ipList;
    private ArrayAdapter<String> adapter;
    private ScanIpTask s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device);

        ctx=this;
        btnScan = (Button)findViewById(R.id.scan);
        btnAdd = findViewById(R.id.btnAddDevice);
        listViewIp = (ListView)findViewById(R.id.lstViewIP);


        //Appena entro nella activity
    }

    @Override
    protected void onResume() {
        super.onResume();
        ipList = new ArrayList();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, ipList);
        listViewIp.setAdapter(adapter);
        adapter.clear();
        adapter.notifyDataSetInvalidated();
        s=new ScanIpTask(ctx,ipList,adapter);
        s.execute();

        tmpSelected = new ArrayList<>();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(String tmpS : tmpSelected){
                    String spl[]=tmpS.split("\n /");
                    System.out.println("ADDED: "+tmpS);
                    ds.addDevice(spl[0],spl[1]);
                    //TODO
                    ds.addSmartDevice(new SmartDevice(spl[0],spl[1],"[No Label]"));
                }
                System.out.println(ds.getDevices());
                finish();
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clear();
                adapter.notifyDataSetInvalidated();
                s.cancel(true);
                s=new ScanIpTask(ctx,ipList,adapter);
                s.execute();
            }
        });

        listViewIp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int position, long mylng) {
                String s = (String) listViewIp.getItemAtPosition(position).toString();
                System.out.println("PRESSED: "+s);
                if(tmpSelected.contains(s)){
                    tmpSelected.remove(s);
                    myView.setBackgroundColor(Color.WHITE);
                }else{
                    tmpSelected.add(s);
                    myView.setBackgroundColor(getResources().getColor(R.color.teal_200));
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.notifyDataSetInvalidated();
        s.cancel(true);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        adapter.notifyDataSetInvalidated();
        s.cancel(true);
        this.finish();
    }
}