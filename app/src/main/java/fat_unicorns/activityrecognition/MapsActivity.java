package fat_unicorns.activityrecognition;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ArrayList<ActivityEntry> activity_history_list = new ArrayList<ActivityEntry>();
    private ArrayList<Marker> mMarkers = new ArrayList<Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        getEntries();


        setUpMapIfNeeded();
    }

    private void getEntries(){
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            ArrayList<String> tmp = getIntent().getStringArrayListExtra("Entries");
            for (String s : tmp){
                String[] parts = s.split(";");

                ActivityEntry ae = new ActivityEntry(parts[1], Integer.parseInt(parts[2]), getTypeFromName(parts[1]), parts[3], parts[4]);
                activity_history_list.add(ae);
                Log.e("ADDING", "Added element to history");



            }
        } else {
            Log.e("PARSING", "Extras == null");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getEntries();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMarkers = new ArrayList<Marker>();

        if(activity_history_list != null && !activity_history_list.isEmpty()) {
            String[] locs = activity_history_list.get(0).getCurrentPos().split(",");
            LatLng pos = new LatLng(Double.parseDouble(locs[0]), Double.parseDouble(locs[1]));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(pos)      // Sets the center of the map to Mountain View
                    .zoom(12)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            for(ActivityEntry ae : activity_history_list){
                mMarkers.add(mMap.addMarker(new MarkerOptions().position(pos).title(getType(ae.getType())).icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot)).snippet(ae.getTimestamp() + "\n" + ae.getConfidence() + "% confidence")));

                Log.e("MARKERS", "Set marker");
            }
        }

    }

    private String getType(int type){
        if(type == DetectedActivity.UNKNOWN)
            return "Unknown";
        else if(type == DetectedActivity.IN_VEHICLE)
            return "In Vehicle";
        else if(type == DetectedActivity.ON_BICYCLE)
            return "On Bicycle";
        else if(type == DetectedActivity.ON_FOOT)
            return "On Foot";
        else if(type == DetectedActivity.STILL)
            return "Still";
        else if(type == DetectedActivity.TILTING)
            return "Tilting";
        else
            return "";
    }

    private int getTypeFromName(String name){
        if(name ==  "Unknown" )
            return DetectedActivity.UNKNOWN;
        else if(name == "In Vehicle")
            return DetectedActivity.IN_VEHICLE;
        else if(name == "On Bicycle")
            return DetectedActivity.ON_BICYCLE;
        else if(name == "On Foot")
            return DetectedActivity.ON_FOOT;
        else if(name == "Still")
            return DetectedActivity.STILL;
        else if(name == "Tilting")
            return DetectedActivity.TILTING;
        else
            return DetectedActivity.UNKNOWN;
    }

}
