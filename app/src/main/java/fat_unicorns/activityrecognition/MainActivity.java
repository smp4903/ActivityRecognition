package fat_unicorns.activityrecognition;
        import android.app.Activity;
        import android.app.ListActivity;
        import android.app.PendingIntent;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import android.widget.SimpleCursorAdapter;
        import android.widget.Toast;

        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.GooglePlayServicesClient;
        import com.google.android.gms.common.GooglePlayServicesUtil;
        import com.google.android.gms.location.ActivityRecognitionClient;

        import java.util.ArrayList;


public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener{

    private ActivityRecognitionClient arclient;
    private PendingIntent pIntent;
    private BroadcastReceiver receiver;
    private ListView activity_history;
    private ArrayList<ActivityEntry> activity_history_list;
    private ActivityHistoryAdapter ah_adapter;

    // This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Toast.makeText(getApplicationContext(), "Removing Activity Entry Number " + position, Toast.LENGTH_LONG).show();
                view.animate().setDuration(2000).alpha(0).scaleY(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                activity_history_list.remove(activity_history_list.get(position));
                                ah_adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });
            }
        });

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity_history_list.add(new ActivityEntry(intent.getStringExtra("Activity"), intent.getExtras().getInt("Confidence"), intent.getIntExtra("Type",0), intent.getLongExtra("Timestamp",0)));
                ah_adapter.notifyDataSetChanged();
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
        arclient.requestActivityUpdates(1000, pIntent);
    }
    @Override
    public void onDisconnected() {
    }
}

