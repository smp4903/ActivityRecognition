package fat_unicorns.activityrecognition;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by Stefan on 30-09-2014.
 */
public class LogHelper {

    private Context context;

    public LogHelper(Context c){
        context = c;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * This method uses toString() method of each element in the data-arraylist to log the information to a .txt file.
     * @param data
     * @param filename string parameter for the target file, e.g. "LocationTrace.txt"
     */
    public void writeToFile(ArrayList data, String filename) {
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File (root.getAbsolutePath() + "/download");
        dir.mkdirs();
        File file = new File(dir, filename);

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);

            String result = "";
            for(Object obj : data){
                pw.print(obj.toString() + "\r\n");
            }
            pw.close();
            f.close();
        }
        catch (IOException e) {
            //Log.e(TAG, "File write failed: " + e.toString());
        }
        Toast.makeText(context, "Trace written to file " + file, Toast.LENGTH_SHORT).show();
    }
}
