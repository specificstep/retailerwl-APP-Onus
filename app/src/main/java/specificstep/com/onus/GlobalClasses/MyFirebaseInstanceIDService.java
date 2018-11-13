package specificstep.com.onus.GlobalClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by ubuntu on 6/2/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    SharedPreferences sharedPreferences;
    Constants constants;
    String refreshedToken;

    @Override
    public void onTokenRefresh() {
        //Getting registration token
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        constants = new Constants();
        sharedPreferences = getSharedPreferences(constants.SHAREEDPREFERENCE, Context.MODE_PRIVATE);
        if (refreshedToken == null) {
            refreshedToken = FirebaseInstanceId.getInstance().getToken();
        }

        sharedPreferences.edit().putString(constants.TOKEN, refreshedToken).commit();
        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);

    }

    private void sendRegistrationToServer(String token) {

        constants = new Constants();
        sharedPreferences = getSharedPreferences(constants.SHAREEDPREFERENCE, Context.MODE_PRIVATE);

        sharedPreferences.edit().putString(constants.TOKEN, token).commit();

        //You can implement this method to store the token on your server
        //Not required for current project
    }
}
