package fat_unicorns.activityrecognition;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import android.app.IntentService;
import android.content.Intent;

public class ActivityRecognitionService extends IntentService {

    private String TAG = this.getClass().getSimpleName();

    public ActivityRecognitionService() {
        super("My Activity Recognition Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            // Extract result from recieved intent
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            // Put information into new intent
            Intent i = new Intent("fat_unicorns.ACTIVITY_RECOGNITION_DATA");
            i.putExtra("Activity", getType(result.getMostProbableActivity().getType()));
            i.putExtra("Confidence", result.getMostProbableActivity().getConfidence());
            i.putExtra("Type", result.getMostProbableActivity().getType());
            i.putExtra("Elapsed", result.getElapsedRealtimeMillis());
            i.putExtra("Timestamp", result.getTime());

            // Broadcast intent
            sendBroadcast(i);
        }
    }

    private String getType(int type) {
        if (type == DetectedActivity.UNKNOWN)
            return "Unknown";
        else if (type == DetectedActivity.IN_VEHICLE)
            return "In Vehicle";
        else if (type == DetectedActivity.ON_BICYCLE)
            return "On Bicycle";
        else if (type == DetectedActivity.ON_FOOT)
            return "On Foot";
        else if (type == DetectedActivity.STILL)
            return "Still";
        else if (type == DetectedActivity.TILTING)
            return "Tilting";
        else
            return "";
    }

}

