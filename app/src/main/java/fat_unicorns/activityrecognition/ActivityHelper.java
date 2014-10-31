package fat_unicorns.activityrecognition;

import com.google.android.gms.location.DetectedActivity;

/**
 * Created by Stefan on 30-10-2014.
 */
public class ActivityHelper {


    public static String typeIntToString(int type) {
        if (type == DetectedActivity.UNKNOWN)
            return "Unknown";
        else if (type == DetectedActivity.IN_VEHICLE)
            return "In Vehicle";
        else if (type == DetectedActivity.ON_BICYCLE)
            return "On Bicycle";
        else if (type == DetectedActivity.ON_FOOT)
            return "On Foot";
        else if (type == DetectedActivity.RUNNING)
            return "Running";
        else if (type == DetectedActivity.WALKING)
            return "Walking";
        else if (type == DetectedActivity.STILL)
            return "Still";
        else if (type == DetectedActivity.TILTING)
            return "Tilting";
        else
            return "";
    }

    public static int typeStringToInt(String name){
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

    public static int typeToImageID(int type){
        if(type == DetectedActivity.UNKNOWN)
            return R.drawable.unknown;
        else if(type == DetectedActivity.IN_VEHICLE)
            return R.drawable.in_vehicle;
        else if(type == DetectedActivity.ON_BICYCLE)
            return R.drawable.on_bycicle;
        else if(type == DetectedActivity.ON_FOOT)
            return R.drawable.on_foot;
        else if(type == DetectedActivity.RUNNING)
            return R.drawable.running;
        else if(type == DetectedActivity.WALKING)
            return R.drawable.on_foot;
        else if(type == DetectedActivity.STILL)
            return R.drawable.still;
        else if(type == DetectedActivity.TILTING)
            return R.drawable.tilting;
        else
            return R.drawable.unknown;
    }
}
