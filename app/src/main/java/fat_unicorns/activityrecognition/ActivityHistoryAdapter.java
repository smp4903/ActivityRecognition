package fat_unicorns.activityrecognition;
import android.app.Activity;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Stefan on 25-09-2014.
 */
public class ActivityHistoryAdapter extends BaseAdapter {

    private Activity context;
    private int layoutResourceId;
    private ArrayList<ActivityEntry> data = null;
    private long last_time = 0;

        public ActivityHistoryAdapter(Activity context, ArrayList<ActivityEntry> data) {
            this.context = context;
            this.data = data;
            last_time = SystemClock.elapsedRealtime();
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

        String[] time_elements = data.get(position).getElapsedTime().split("-");


        title.setText(data.get(position).getName() + " (" + time_elements[0] + "m " + time_elements[1] + "s " + time_elements[2] + "ms since last)" );
        description.setText(data.get(position).getTimestamp() + " - " + data.get(position).getConfidence() + "% confidence");
        icon.setImageResource(ActivityHelper.typeToImageID(data.get(position).getType()));

        //Animation animation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.abc_slide_in_bottom);
        //rowView.startAnimation(animation);

        return rowView;
    }
}
