package fat_unicorns.activityrecognition;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

/**
 * Created by Stefan on 25-09-2014.
 */
public class ActivityHistoryAdapter extends BaseAdapter {

    private Activity context;
    private int layoutResourceId;
    private ArrayList<ActivityEntry> data = null;

        public ActivityHistoryAdapter(Activity context, ArrayList<ActivityEntry> data) {
            this.context = context;
            this.data = data;
        }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_item_layout, parent, false);

        TextView title = (TextView) rowView.findViewById(R.id.title);
        TextView description = (TextView) rowView.findViewById(R.id.description);
        ImageView icon = (ImageView) rowView.findViewById(R.id.icon);

        title.setText(data.get(position).getName());
        description.setText(data.get(position).getTimestamp() + " - " + data.get(position).getConfidence() + "% confidence");
        icon.setImageResource(typeToImageID(data.get(position).getType()));

        //Animation animation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.abc_slide_in_bottom);
        //rowView.startAnimation(animation);

        return rowView;
    }

    private int typeToImageID(int type){
        if(type == DetectedActivity.UNKNOWN)
            return R.drawable.unknown;
        else if(type == DetectedActivity.IN_VEHICLE)
            return R.drawable.in_vehicle;
        else if(type == DetectedActivity.ON_BICYCLE)
            return R.drawable.on_bycicle;
        else if(type == DetectedActivity.ON_FOOT)
            return R.drawable.on_foot;
        else if(type == DetectedActivity.STILL)
            return R.drawable.still;
        else if(type == DetectedActivity.TILTING)
            return R.drawable.tilting;
        else
            return R.drawable.unknown;
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

}
