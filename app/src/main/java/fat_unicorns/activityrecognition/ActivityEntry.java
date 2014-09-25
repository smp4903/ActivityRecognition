package fat_unicorns.activityrecognition;

import java.security.Timestamp;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Stefan on 25-09-2014.
 */
public class ActivityEntry {

    private String name;
    private double confidence;
    private String timestamp;
    private int type;
    private Date date;

    public ActivityEntry(String n, double conf, int t, long ts){
        name = n;
        confidence = conf;
        type = t;
        date = new Date(ts);

        DateFormat df = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
        timestamp = df.format(Calendar.getInstance().getTime());
    }

    public String toString(){
        return timestamp + " - " + name + " (" + confidence + "%)";
    }

    public String getTimestamp(){
        return timestamp;
    }

    public String getName(){
        return name;
    }

    public double getConfidence(){
        return confidence;
    }

    public int getType(){ return type;}

    public Date getDate(){ return date;}
}
