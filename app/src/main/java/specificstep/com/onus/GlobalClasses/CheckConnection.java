package specificstep.com.onus.GlobalClasses;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created by ubuntu on 12/1/17.
 */

public class CheckConnection {

    /*check user is connected
    to internet or not*/
    public boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;

    }

    public static boolean isConnected(Context context) {
/*        ConnectivityManager
                cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();*/

        //@kns.p ======================================
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
            // do something
        } else if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
            // check NetworkInfo subtype
            if(activeNetwork.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS){
                // Bandwidth between 100 kbps and below
               // activeNetwork = null;
            } else if(activeNetwork.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE){
                // Bandwidth between 50-100 kbps
              //  activeNetwork = null;
            } else if(activeNetwork.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_0){
                // Bandwidth between 400-1000 kbps
               // activeNetwork = null;
            } else if(activeNetwork.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_A){
                // Bandwidth between 600-1400 kbps
               // activeNetwork = null;
            }
            else if(activeNetwork.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA){
                // Bandwidth between 14-64 kbps
                activeNetwork = null;
            }
            else{
                //> 1400 kbps
                //allow api calls
            }

            // Other list of various subtypes you can check for and their bandwidth limits
            // TelephonyManager.NETWORK_TYPE_1xRTT       ~ 50-100 kbps
            // TelephonyManager.NETWORK_TYPE_CDMA        ~ 14-64 kbps
            // TelephonyManager.NETWORK_TYPE_HSDPA       ~ 2-14 Mbps
            // TelephonyManager.NETWORK_TYPE_HSPA        ~ 700-1700 kbps
            // TelephonyManager.NETWORK_TYPE_HSUPA       ~ 1-23 Mbps
            // TelephonyManager.NETWORK_TYPE_UMTS        ~ 400-7000 kbps
            // TelephonyManager.NETWORK_TYPE_UNKNOWN     ~ Unknown
        }
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
        //====================================================

    }
}
