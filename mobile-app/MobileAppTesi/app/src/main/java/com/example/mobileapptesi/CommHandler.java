package com.example.mobileapptesi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;


public class CommHandler implements Runnable{

    private final String SERVER_IP="http://192.168.1.50:80";
    private final String GET_DATA_CMD="/getData";
    private static JSONObject jsonObject=null;

    @Override
    public void run() {
        System.out.println("Connection...");
        int respCode=0;
        try {
            URL url = new URL(SERVER_IP+GET_DATA_CMD);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            String tmpResponse=new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            JSONObject response = new JSONObject(tmpResponse);
            if(response!=null)
                jsonObject=response;
            System.out.println("Json: "+jsonObject);

            urlConnection.disconnect();
        }  catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getJson() {
        return jsonObject;
    }
}
