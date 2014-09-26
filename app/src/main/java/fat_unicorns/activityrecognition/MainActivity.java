package fat_unicorns.activityrecognition;
        import android.app.Activity;
        import android.app.ListActivity;
        import android.app.PendingIntent;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.os.Bundle;
        import android.os.Environment;
        import android.util.Log;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ListView;
        import android.widget.SimpleCursorAdapter;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.GooglePlayServicesClient;
        import com.google.android.gms.common.GooglePlayServicesUtil;
        import com.google.android.gms.location.ActivityRecognitionClient;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.PrintWriter;
        import java.util.ArrayList;


public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener{

    private static final String FILENAME = "ActivityRecognitionTrace.txt";
    private ActivityRecognitionClient arclient;
    private PendingIntent pIntent;
    private BroadcastReceiver receiver;
    private ListView activity_history;
    private ArrayList<ActivityEntry> activity_history_list;
    private ActivityHistoryAdapter ah_adapter;

    // save button
    Button save_btn;

    // entry counter
    TextView entry_cnt;

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
                writeToFile(activity_history_list);
            }
        });

        // Initialize entry counter
        entry_cnt = (TextView) findViewById(R.id.entry_counter);

        checkServices();

        // Initialize activity recognition
        activity_history_list = new ArrayList<ActivityEntry>();
        activity_history = (ListView) findViewById(R.id.activity_history);
        ah_adapter = new ActivityHistoryAdapter(this, activity_history_list);
        activity_history.setAdapter(ah_adapter);
        activity_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,final View view,
                                    final int position, long id) {
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
            }
        });

        // Register broadcast reciever to recieve broadcasts from the ActivityRecognitionService.
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity_history_list.add(new ActivityEntry(intent.getStringExtra("Activity"), intent.getExtras().getInt("Confidence"), intent.getIntExtra("Type",0), intent.getLongExtra("Timestamp",0)));
                ah_adapter.notifyDataSetChanged();
                entry_cnt.setText("Entries: " + activity_history_list.size());
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("fat_unicorns.ACTIVITY_RECOGNITION_DATA");
        registerReceiver(receiver, filter);
    }

    private void checkServices(){
        int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resp == ConnectionResult.SUCCESS){
            arclient = new ActivityRecognitionClient(this, this, this);
            arclient.connect();
        }
        else{
            Toast.makeText(this, "Please install Google Play Service.", Toast.LENGTH_SHORT).show();
        }
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

        arclient.requestActivityUpdates(0, pIntent);
    }
    @Override
    public void onDisconnected() {

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void writeToFile(ArrayList<ActivityEntry> data) {
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File (root.getAbsolutePath() + "/download");
        dir.mkdirs();
        File file = new File(dir, FILENAME);

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);

            String result = "";
            for(ActivityEntry ae : data){
                pw.println(ae.toString());
            }
            pw.close();
            f.close();
        }
        catch (IOException e) {
            //Log.e(TAG, "File write failed: " + e.toString());
        }
        Toast.makeText(this, "Trace written to file "+file, Toast.LENGTH_SHORT).show();

    }
}

