package specificstep.com.onus.utility;

import android.util.Log;

/**
 * Created by ubuntu on 23/3/17.
 */

public class LogMessage {
    private static String TAG = "LogMessage";

    public static void d(String message) {
        Log.d(TAG, message);
    }

    public static void e(String message) {
        Log.e(TAG, message);
    }

    public static void i(String message) {
        Log.i(TAG, message);
    }

    public static void v(String message) {
        Log.v(TAG, message);
    }

    public static void d(int message) {
        Log.d(TAG, message + "");
    }

    public static void e(int message) {
        Log.e(TAG, message + "");
    }

    public static void i(int message) {
        Log.i(TAG, message + "");
    }

    public static void v(int message) {
        Log.v(TAG, message + "");
    }
}
