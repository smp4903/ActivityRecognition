package fat_unicorns.activityrecognition;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.security.Timestamp;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Stefan on 25-09-2014.
 */
public class ActivityEntry{

    private String name;
    private int confidence;
    private String timestamp;
    private int type;
    private String elapsed;
    private String currentPos;

    public ActivityEntry(String n, int conf, int t, String e, String pos){
        name = n;
        confidence = conf;
        type = t;
        elapsed = e;
        currentPos = pos;

        DateFormat df = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
        timestamp = df.format(Calendar.getInstance().getTime());
    }

    public String toString(){
        return timestamp + ";" + name + ";" + confidence + ";" + elapsed + ";" + currentPos;
    }

    public String getTimestamp(){
        return timestamp;
    }

    public String getName(){
        return name;
    }

    public int getConfidence(){
        return confidence;
    }

    public int getType(){ return type;}

    public String getElapsedTime() {return elapsed;}

    public String getCurrentPos() {return currentPos;}


}
