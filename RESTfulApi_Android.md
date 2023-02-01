# Data Singleton

    public class DataSingleton {
        private static DataSingleton dataSingleton;
        private static String currentState="";
        private static String lastState="";
        private static List<String> data=new ArrayList<>();

        private DataSingleton(){}

        public static DataSingleton getInstance(){
            if(dataSingleton==null){
                dataSingleton = new DataSingleton();
            }
            return dataSingleton;
        }

        synchronized public void setData(String s){
            data.add(s);
            lastState=currentState;
            try {
                currentState = String.valueOf(new JSONObject(s).getJSONObject("Status").get("Power"));
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }

        synchronized public List<String> getData() {
            return data;
        }

        synchronized public String getLastData(){
            return data.get(data.size() - 1);
        }

        synchronized public String getCurrentState(){
            return currentState;
        }

        synchronized public static String getLastState() {
            return lastState;
        }
    }

# HTTPrequest implemented as a Runnable
*ho dovuto fare in modo che ogni chiamata REST avvenga in un thread separato dal mainthread per le policy di android*

    public class REST implements Runnable{
        final OkHttpClient client = new OkHttpClient();
        private String url="";
        private List<String> params=null;
        private boolean isStatusInfo = false;
        private DataSingleton ds = null;

        public REST(String url, List<String> params){
            this.url=url;
            this.params=params;
            ds=DataSingleton.getInstance();
            if(params.contains("cm?cmnd=Status") && params.size()==1){
                isStatusInfo = true;
            }else{
                this.url+='?';
            }
        }

        @Override
        public void run() {
            StringBuilder paramsSB = new StringBuilder();
            for (String s : params) {
                paramsSB.append(s);
            }

            System.out.println("REST REQUEST : "+url+paramsSB);
            Request request = new Request.Builder()
                    .url(url+paramsSB)
                    .build();
            Response response=null;
            try {
                response = client.newCall(request).execute();
                String tmpR=response.body().string();
                System.out.println(tmpR);

                if(isStatusInfo){
                    ds.setData(tmpR);
                }
            }
            catch(IOException e) {
                System.out.println(e);
            }

        }
    }

# Risoluzione per le policy android di sicurezza delle richieste HTTP
* Aggiungere nel Manifest.xml i permessi

    \<uses-permission android:name="android.permission.INTERNET" \/>\
    \<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" \/>\
    \<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" \/>

* Aggiungere al tag _application_ dei parametri

    android:usesCleartextTraffic="true" \
    android:networkSecurityConfig="@xml/network_security_config"

* Creare il file usato prima, *network_security_config*

    \<?xml version="1.0" encoding="utf-8"?>\
    \<network-security-config>\
        \<domain-config cleartextTrafficPermitted="true">\
            \<domain includeSubdomains="true">\
                192.168.1.105</domain>\
        \</domain-config>\
    \</network-security-config>

    