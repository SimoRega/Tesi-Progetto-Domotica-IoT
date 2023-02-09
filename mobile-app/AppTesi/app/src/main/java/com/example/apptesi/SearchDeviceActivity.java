package com.example.apptesi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.apptesi.DataSingleton;
import com.example.apptesi.R;
import com.example.apptesi.ScanIpTask;
import com.example.apptesi.device.SmartDevice;

import java.util.ArrayList;
import java.util.List;

public class SearchDeviceActivity extends AppCompatActivity {

    private ImageButton btnScan;
    private Button btnAdd;
    private Button btnSever;
    private ListView listViewIp;
    private Context ctx=null;
    private DataSingleton ds = DataSingleton.getInstance();
    private ArrayList<String> tmpSelected;
    private ArrayList<String> ipList;
    private ArrayAdapter<String> adapter;
    private ScanIpTask s;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device);

        ctx=this;
        btnScan = (ImageButton)findViewById(R.id.scan);
        btnAdd = findViewById(R.id.btnAddDevice);
        btnSever = findViewById(R.id.btnAddServer);
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
                    SmartDevice tmp = new SmartDevice(spl[0],spl[1],"[No Label]");
                    ds.addSmartDevice(tmp);
                    System.out.println(tmp);
                    new CustomHttpRequest("http://"+ds.getServer().getIp()+":80/", List.of("addDevice?","devName=",spl[0],"&","devIp=",spl[1])).makeHttpRequest(HttpRequestType.SERVER);
                }
                finish();
            }
        });

        btnSever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String spl[]=tmpSelected.get(0).split("\n /");
                ds.setServer(new SmartDevice(spl[0],spl[1],"[SERVER]"));
                System.out.println(ds.getServer());

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