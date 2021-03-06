package fat_unicorns.activityrecognition;
        import android.app.Activity;
        import android.app.PendingIntent;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.location.Location;
        import android.os.Bundle;
        import android.os.Environment;
        import android.util.Log;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.Button;
        import android.widget.ListView;
        import android.widget.SimpleCursorAdapter;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.GooglePlayServicesClient;
        import com.google.android.gms.common.GooglePlayServicesUtil;
        import com.google.android.gms.location.ActivityRecognitionClient;
        import com.google.android.gms.location.LocationClient;
        import com.google.android.gms.location.LocationListener;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.maps.model.LatLng;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.PrintWriter;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;


public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener{

    private static final String FILENAME = "ActivityRecognitionTrace.txt";
    private ActivityRecognitionClient arclient;
    private PendingIntent pIntent;
    private BroadcastReceiver receiver;
    private ListView activity_history;
    private ArrayList<ActivityEntry> activity_history_list;
    private ActivityHistoryAdapter ah_adapter;
    private long last_time = 0;
    private ArrayList<Long> elapsed_time_list = new ArrayList<Long>();
    private LatLng currentPos = new LatLng(0,0);

    // Location Client
    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;

    // save button
    Button save_btn;

    // entry counter
    TextView entry_cnt;

    // map button
    Button map_btn;

    // This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize save button
        save_btn = (Button) findViewById(R.id.save_button);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogHelper lh = new LogHelper(getApplicationContext());
                lh.writeToFile(activity_history_list, "ARTrace.txt");
            }
        });

        // Initialize entry counter
        entry_cnt = (TextView) findViewById(R.id.entry_counter);

        connectAR();

        // Get button object
        map_btn = (Button) findViewById(R.id.map_btn);
        map_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent map_intent = new Intent(getApplicationContext(), MapsActivity.class);

                // Convert list of Activity Entries to list of AE-strings -- parsed on the other side
                ArrayList<String> tmp = new ArrayList<String>();
                for (ActivityEntry ae : activity_history_list){
                    tmp.add(ae.toString());
                }

                map_intent.putStringArrayListExtra("Entries", tmp);
                startActivity(map_intent);

            }
        });
        // Initialize activity recognition
        activity_history_list = new ArrayList<ActivityEntry>();
        activity_history = (ListView) findViewById(R.id.activity_history);
        ah_adapter = new ActivityHistoryAdapter(this, activity_history_list);
        activity_history.setAdapter(ah_adapter);
        activity_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,final View view,
                                    final int position, long id) {


                /*
                view.animate().setDuration(500).alpha(0).scaleY(0).scaleX(0).rotationX(90)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    activity_history_list.remove(activity_history_list.get(position));
                                    ah_adapter.notifyDataSetChanged();
                                    view.setAlpha(1);
                                    Toast.makeText(getApplicationContext(), "Removed Activity Entry", Toast.LENGTH_SHORT).show();
                                    entry_cnt.setText("Entries: " + activity_history_list.size());


                                } catch (IndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Entry Already Removed!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
    */
                Toast.makeText(getApplicationContext(), activity_history_list.get(position).getCurrentPos(), Toast.LENGTH_SHORT).show();

            }
        });

        // Register broadcast reciever to recieve broadcasts from the ActivityRecognitionService.
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Here we do something with the result
                // Add activity to history (list)

                long time_elapsed_long = intent.getLongExtra("Elapsed",0) - last_time;
                last_time = intent.getLongExtra("Elapsed", 0);
                elapsed_time_list.add(time_elapsed_long);
                String time_elapsed = new SimpleDateFormat("mm-ss-SSS").format(new Date(time_elapsed_long).getTime()).toString();


                ActivityEntry ae = new ActivityEntry(
                        intent.getStringExtra("Activity"),
                        intent.getIntExtra("Confidence",0),
                        intent.getIntExtra("Type", 0),
                        time_elapsed,
                        currentPos.latitude + "," + currentPos.longitude);
                activity_history_list.add(ae);

                Log.i("LOG", ae.toString());

                // Tell adapter that the data has changed to update the View
                ah_adapter.notifyDataSetChanged();
                // Update textview to show how many entries have been collected
                entry_cnt.setText("Entries: " + activity_history_list.size());
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("fat_unicorns.ACTIVITY_RECOGNITION_DATA");
        registerReceiver(receiver, filter);
    }

    private void connectAR(){
        // check for Google Play Services
        int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resp == ConnectionResult.SUCCESS){
            arclient = new ActivityRecognitionClient(this, this, this);
            arclient.connect();
        }
        else{
            Toast.makeText(this, "Please install Google Play Service.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLocationTracking() {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            mLocationClient = new LocationClient(this,this,this);
            mLocationClient.connect();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        startLocationTracking();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startLocationTracking();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(arclient!=null){
            arclient.removeActivityUpdates(pIntent);
            arclient.disconnect();
        }
        unregisterReceiver(receiver);
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle arg0) {
        Intent intent = new Intent(this, ActivityRecognitionService.class);
        pIntent = PendingIntent.getService(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        if(arclient.isConnected()) arclient.requestActivityUpdates(0, pIntent);

        // Locationing
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(100).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("LOCATIONING", "Received a new location " + location + " acc: " + location.getAccuracy());
                currentPos = new LatLng(location.getLatitude(),location.getLongitude());

            }
        };
        if(mLocationClient.isConnected()) mLocationClient.requestLocationUpdates(locationRequest,mLocationListener);
    }
    @Override
    public void onDisconnected() {

    }
}

