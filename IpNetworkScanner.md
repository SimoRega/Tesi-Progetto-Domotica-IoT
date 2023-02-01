# Activity che chiama lo scan
    public class SearchDeviceActivity extends AppCompatActivity {

        private Button btnScan;
        private ListView listViewIp;
        private Context ctx=null;

        ArrayList<String> ipList;
        ArrayAdapter<String> adapter;

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

            }

        }

# AsyncTask che effettua lo scan
    public class ScanIpTask extends AsyncTask<Void, String, Void> {

        /*
        Scan IP 192.168.1.100~192.168.1.110
        you should try different timeout for your network/devices
        */
        private ArrayList<String> ipList;
        private ArrayAdapter<String> adapter;
        static final String subnet = "192.168.1.";
        static final int lower = 100;
        static final int upper = 110;
        static final int timeout = 5000;
        private Context ctx=null;

        public ScanIpTask(Context ctx, ArrayList<String> ipList,ArrayAdapter<String> adapter){
            this.ctx=ctx;
            this.ipList=ipList;
            this.adapter=adapter;
        }

        @Override
        protected void onPreExecute() {
            ipList.clear();
            adapter.notifyDataSetInvalidated();
            Toast.makeText(ctx, "Scan IP...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            for (int i = lower; i <= upper; i++) {
                String host = subnet + i;

                try {
                    InetAddress inetAddress = InetAddress.getByName(host);
                    if (inetAddress.isReachable(timeout)){
                        publishProgress(InetAddress.getByName(host).getHostName()+
                                " "+inetAddress.toString());
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            ipList.add(values[0]);
            adapter.notifyDataSetInvalidated();
            Toast.makeText(ctx, values[0], Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(ctx, "Done", Toast.LENGTH_LONG).show();
        }
    }

# Layout dell'activity

    <?xml version="1.0" encoding="utf-8"?>
    <androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        xmlns:tools="http://schemas.android.com/tools"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".SearchDeviceActivity">

        <ListView
            android:id="@+id/lstViewIP"
            android:layout_width="284dp"
            android:layout_height="529dp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintHorizontal_bias="0.496"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintVertical_bias="0.435" />

        <Button
            android:id="@+id/scan"
            android:layout_width="229dp"
            android:layout_height="43dp"
            android:text="Scan"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintVertical_bias="0.922" />
    </androidx.constraintlayout.widget.ConstraintLayout>