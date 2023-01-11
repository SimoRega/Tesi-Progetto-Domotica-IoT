package com.example.mobileapptesi;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;

public class PostAsyncTask extends AsyncTask<String, Void, String> {

    private MainActivity activity;
    private AsyncCallBack asyncCallBack;
    private ProgressBar progressBar;

    PostAsyncTask setInstance(Context context, ProgressBar progressBar){
        this.activity= (MainActivity) context;
        asyncCallBack = (AsyncCallBack) context;
        this.progressBar = progressBar;
        return this;
    }

    @Override
    protected String doInBackground(String... strings) {
        String response = "";
        BufferedReader reader = null;

        try{
            URL url = new URL("http://192.168.1.16:8081/zeroconf/switch");
            String data = strings[0];
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = reader.readLine())!= null){
                sb.append(line+"\n");
            }
            response=sb.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                /*activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

                 */
                reader.close();
            }catch(Exception e){

            }
        }
        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        asyncCallBack.setResult(s);
    }
}
