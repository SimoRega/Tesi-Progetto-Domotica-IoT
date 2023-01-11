package com.example.mobileapptesi;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mobileapptesi.databinding.ActivityMainBinding;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements AsyncCallBack{

    private ActivityMainBinding binding;
    private Context ctx = null;
    private TextView hum=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx=this;
        CommHandler ch = new CommHandler();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(ch, 0, 3, TimeUnit.SECONDS);
        //Thread t= new Thread(ch,"ch");
        //t.start();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        hum = findViewById(R.id.txtHum);
        Button btnOn = findViewById(R.id.btnTurnOn);
        Button btnOff = findViewById(R.id.btnTurnOff);

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PostAsyncTask().setInstance(MainActivity.this,null).execute("{ \n" +
                        "    \"deviceid\": \"\", \n" +
                        "    \"data\": {\n" +
                        "        \"switch\": \"on\" \n" +
                        "    } \n" +
                        " }");

            }
        });
        /*
        try {

            hum.setText("Hum: "+(ch.getJson().getJSONObject("sensors").get("hum")==null?"":ch.getJson().getJSONObject("sensors").get("hum")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
    }


    @Override
    public void setResult(String result) {
        hum.setText(result);
    }
}