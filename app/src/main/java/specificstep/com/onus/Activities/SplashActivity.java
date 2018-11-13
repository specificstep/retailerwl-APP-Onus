package specificstep.com.onus.Activities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

import specificstep.com.onus.Database.DatabaseHelper;
import specificstep.com.onus.Database.NotificationTable;
import specificstep.com.onus.GlobalClasses.Constants;
import specificstep.com.onus.Models.DateTime;
import specificstep.com.onus.Models.NotificationModel;
import specificstep.com.onus.R;
import specificstep.com.onus.utility.LogMessage;

/**
 * Created by ubuntu on 4/1/17.
 */

public class SplashActivity extends Activity {

    private Context context;
    // private LogMessage log;
    DatabaseHelper databaseHelper;
    SharedPreferences sharedPreferences;
    Constants constants;
    String is_app_installed_form_play_store;

    String str_reg_date_time;
    int month, yer;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_splash);

        context = SplashActivity.this;
        // log = new LogMessage(SplashActivity.class.getSimpleName());
/*get year and month from registered date*/

        databaseHelper = new DatabaseHelper(SplashActivity.this);
        constants = new Constants();
        sharedPreferences = getSharedPreferences(constants.SHAREEDPREFERENCE, Context.MODE_PRIVATE);

        /* [START] - Manage create short cut function */
        // getPackageInformation();
        // is_app_istalled_form_play_store = sharedPreferences.getString(constants.is_app_istalled_from_play_store, "");
        is_app_installed_form_play_store = sharedPreferences.getString
                (constants.isAppInstallFromPlayStore, constants.isAppInstallFromPlayStore_No);
        // [END]

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (sharedPreferences.getString(constants.VERIFICATION_STATUS, "").equals("1")
                        && sharedPreferences.getString(constants.LOGIN_STATUS, "").equals("1")
                        || sharedPreferences.getString(constants.LOGIN_STATUS, "") == "1") {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (sharedPreferences.getString(constants.LOGIN_STATUS, "").equals("0")
                        || sharedPreferences.getString(constants.LOGIN_STATUS, "") == "0") {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, RegistrationActivity.class);
                    startActivity(intent);
                }
            }
        }, 2000);

        /* [START] - Manage create short cut function */
//        if (is_app_istalled_form_play_store.equals("No")) {
//            // addShortcut(SplashActivity.this);
//            log.d("Splash : " + "App install from play store");
//        } else {
//            sharedPreferences.edit().putString(is_app_istalled_form_play_store, "Yes").commit();
//            log.d("Splash : " + "App install from mobile");
//            addShortcut(SplashActivity.this);
//        }
        if (is_app_installed_form_play_store.equals("No")) {
            // addShortcut(SplashActivity.this);
            LogMessage.d("Splash : " + "App install from play store");
            // Send app install notification
            // sendNotification();
        } else {
            sharedPreferences.edit().putString(constants.isAppInstallFromPlayStore, "Yes").commit();
            LogMessage.d("Splash : " + "App install from mobile");
            addShortcut(SplashActivity.this);
        }
        // [END]

//        createOrUpdateShortcut();
    }

    /*
    Method : addShortcut
    (for adding shortcut on home page while app is installing first time)
    */
    public void addShortcut(Context context) {
        SharedPreferences prefs = null;
        prefs = getSharedPreferences("specificstep.com.rechargeengine", MODE_PRIVATE);

        if (prefs.getBoolean("firstrun", true)) {

            // Create explicit intent which will be used to call Our application
            // when some one clicked on short cut
            Intent shortcutIntent = new Intent(getApplicationContext(),
                    SplashActivity.class);
            shortcutIntent.setAction(Intent.ACTION_MAIN);
            Intent intent = new Intent();

            // Create Implicit intent and assign Shortcut Application Name, Icon
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                            R.mipmap.ic_launcher));
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            // don't add duplicate shortcut
            intent.putExtra("duplicate", false);
            getApplicationContext().sendBroadcast(intent);

            sendNotification();
            // save app install message.
//            NotificationModel model = new NotificationModel();
//            model.message = "App installed successfully";
//            model.title = getResources().getString(R.string.app_name);
//            model.receiveDateTime = DateTime.getDate();
//            model.saveDateTime = DateTime.getTime();
//            model.readFlag = "0";
//            databaseHelper.addNotificationData(model);

            prefs.edit().putBoolean("firstrun", false).commit();
        }
    }

    /*
        Method : sendNotification
        (sends notification while app is successfully installed)
        */
    public void sendNotification() {

        // --- START --- save recharge message.
        NotificationModel model = new NotificationModel();
        model.title = getResources().getString(R.string.app_name);
        model.message = "App installed successfully";
        model.receiveDateTime = DateTime.getCurrentDateTime();
        model.saveDateTime = DateTime.getCurrentDateTime();
        model.readFlag = "0";
        model.readDateTime = "";
        LogMessage.d("Notification = " + "title : " + model.title + "Message : " + model.message);
        new NotificationTable(SplashActivity.this).addNotificationData(model);
        // --- END ---

        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText("App installed successfully and shortcut created.");

        // Dismiss notification after action has been clicked
        mBuilder.setAutoCancel(true);

// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, SplashActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(SplashActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());

//        NotificationUtil notificationUtil = AppController.getNotificationUtil();
//        if(notificationUtil == null) {
//            notificationUtil = new NotificationUtil(SplashActivity.this);
//        }
//        notificationUtil.sendNotification(getResources().getString(R.string.app_name), "App installed successfully");
    }

    /* [START] -
     * How to know an application is installed from google play or side-load?
      * Get Application Installed Date on Android */
    public void getPackageInformation() {
        try {
            // SplashActivity: Package Name : null
            LogMessage.i("Package Name : " + context.getPackageManager().getInstallerPackageName("specificstep.com.jyoutsnaweb_dist"));
            // long installed = context.getPackageManager().getPackageInfo(context.getPackag‌​eName(), 0).firstInstallTime;
            // Install Time : 1491806914448
            // Install Time : 2017-04-10 12:18:34
            // Install Time : 2017-04-10 19:18:34
            long millisecond = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).firstInstallTime;
            LogMessage.i("Install Time in millisecond : " + millisecond);
            // String dateString = new SimpleDateFormat("MM/dd/yyyy").format(new Date(millisecond));
            // String dateString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US).format(new Date(millisecond));
            String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(millisecond));
            LogMessage.i("Install Time : " + dateString);
        }
        catch (Exception ex) {
            LogMessage.e("Error while get app install time");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    // [END]
}

