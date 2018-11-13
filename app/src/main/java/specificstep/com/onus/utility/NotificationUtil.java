package specificstep.com.onus.utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.ArrayList;

import specificstep.com.onus.Activities.HomeActivity;
import specificstep.com.onus.Activities.SplashActivity;
import specificstep.com.onus.Database.DatabaseHelper;
import specificstep.com.onus.Database.NotificationTable;
import specificstep.com.onus.GlobalClasses.Constants;
import specificstep.com.onus.Models.DateTime;
import specificstep.com.onus.Models.NotificationModel;
import specificstep.com.onus.Models.User;
import specificstep.com.onus.R;

/**
 * Created by ubuntu on 27/3/17.
 */

public class NotificationUtil {
    private Context context;
    private DatabaseHelper databaseHelper;
    private NotificationManager mNotificationManager = null;
    public static final String ACTION_REFRESH_NOTIFICATION = "specificstep.com.metroenterprise.REFRESH_NOTIFICATION";
    public static final String ACTION_REFRESH_MAINACTIVITY = "specificstep.com.metroenterprise.REFRESH_MAINACTIVITY";
    public static final String ACTION_REFRESH_HOMEACTIVITY = "specificstep.com.metroenterprise.REFRESH_HOMEACTIVITY";

    public NotificationUtil(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context);

        initNotification();
    }

    private void initNotification() {

    }

//    public void sendNotification(String title, String message) {
//        // --- START --- save recharge message.
//        NotificationModel model = new NotificationModel();
//        model.title = title;
//        model.message = message;
//        model.receiveDateTime = DateTime.getCurrentDateTime();
//        model.saveDateTime = DateTime.getCurrentDateTime();
//        model.readFlag = "0";
//        model.readDateTime = "";
//        Log.d("Notification", "title : " + model.title + "Message : " + model.message);
//        databaseHelper.addNotificationData(model);
//        // --- END ---
//
//        /* [START] - Get last record id and set in notification */
//        String lastNotificationId = databaseHelper.getLastNotificationId();
//        int lastNotification = 0;
//        try {
//            lastNotification = Integer.parseInt(lastNotificationId);
//        }
//        catch (Exception ex) {
//            lastNotification = 0;
//        }
//        // [END]
//
//        android.support.v4.app.NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(context)
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setPriority(Notification.PRIORITY_MAX)
//                        .setContentTitle(title)
//                        .setContentText(message);
//
//        // Dismiss notification after action has been clicked
//        mBuilder.setAutoCancel(true);
//
//        ArrayList<NotificationModel> notificationModels = databaseHelper.getLastNotificationData();
//        int lastId = -1;
//        if (notificationModels.size() > 0) {
//            NotificationModel notificationModel = notificationModels.get(0);
//            try {
//                lastId = Integer.parseInt(notificationModel.id);
//                Log.d("Notification", "Last id : " + lastId);
//            }
//            catch (Exception ex) {
//                Log.d("Notification", "Error while parse id");
//                ex.printStackTrace();
//                lastId = -1;
//            }
//        }
//
//        /* [START] - Check if application verify and login success then open notification activity or open splash screen */
//        ArrayList<User> userArrayList = new ArrayList<User>();
//        userArrayList = databaseHelper.getUserDetail();
//        Intent resultIntent = null;
//        if (userArrayList.size() > 0) {
//            // Creates an explicit intent for an Activity in your app
//            resultIntent = new Intent(context, HomeActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString(Constants.KEY_SCREEN_NO, "6");
//            bundle.putString(Constants.KEY_NOTIFICATION_ID, lastId + "");
//            resultIntent.putExtras(bundle);
//        } else {
//            // Creates an explicit intent for an Activity in your app
//            resultIntent = new Intent(context, SplashActivity.class);
//        }
//        // [END]
//
//// The stack builder object will contain an artificial back stack for the
//// started Activity.
//// This ensures that navigating backward from the Activity leads out of
//// your application to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//// Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(SplashActivity.class);
//// Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//        // PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(Constants.TOTAL_NOTIFICATION, PendingIntent.FLAG_UPDATE_CURRENT);
//        mBuilder.setContentIntent(resultPendingIntent);
//        mNotificationManager =
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//// mId allows you to update the notification later on.
//         mNotificationManager.notify(0, mBuilder.build());
////        mNotificationManager.notify(lastNotification, mBuilder.build());
//
//        Intent intent1 = new Intent(ACTION_REFRESH_NOTIFICATION);
//        context.sendBroadcast(intent1);
//
//        Intent intent2 = new Intent(ACTION_REFRESH_MAINACTIVITY);
//        context.sendBroadcast(intent2);
//
//        Intent intent3 = new Intent(ACTION_REFRESH_HOMEACTIVITY);
//        context.sendBroadcast(intent3);
//    }

    public void sendNotification(String title, String message, String dateTime) {
        // --- START --- save recharge message.
        NotificationModel model = new NotificationModel();
        model.title = title;
        model.message = message;
        // model.receiveDateTime = DateTime.getCurrentDateTime();
        model.receiveDateTime = dateTime;
        model.saveDateTime = DateTime.getCurrentDateTime();
        model.readFlag = "0";
        model.readDateTime = "";
        Log.d("Notification", "title : " + model.title + "Message : " + model.message);
        new NotificationTable(context).addNotificationData(model);
        // --- END ---

        /* [START] - Get last record id and set in notification */
        String lastNotificationId = new NotificationTable(context).getLastNotificationId();
        int lastNotification = 0;
        try {
            lastNotification = Integer.parseInt(lastNotificationId);
        }
        catch (Exception ex) {
            lastNotification = 0;
        }
        // [END]

        ArrayList<NotificationModel> notificationModels = new NotificationTable(context).getLastNotificationData();
        int lastId = -1;
        if (notificationModels.size() > 0) {
            NotificationModel notificationModel = notificationModels.get(0);
            try {
                lastId = Integer.parseInt(notificationModel.id);
                Log.d("Notification", "Last id : " + lastId);
            }
            catch (Exception ex) {
                Log.d("Notification", "Error while parse id");
                ex.printStackTrace();
                lastId = -1;
            }
        }

        /* [START] - Check if application verify and login success then open notification activity or open splash screen */
        ArrayList<User> userArrayList = new ArrayList<User>();
        userArrayList = databaseHelper.getUserDetail();
        Intent resultIntent = null;
        if (userArrayList.size() > 0) {
            // Creates an explicit intent for an Activity in your app
            resultIntent = new Intent(context, HomeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.KEY_SCREEN_NO, "6");
            bundle.putString(Constants.KEY_NOTIFICATION_ID, lastId + "");
            resultIntent.putExtras(bundle);
        } else {
            // Creates an explicit intent for an Activity in your app
            resultIntent = new Intent(context, SplashActivity.class);
        }
        // [END]

        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setContentTitle(title)
                        .setContentText(message);

        // Dismiss notification after action has been clicked
        mBuilder.setAutoCancel(true);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(SplashActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        // PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(Constants.TOTAL_NOTIFICATION, PendingIntent.FLAG_UPDATE_CURRENT);
        // PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(lastNotification, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mNotificationManager.notify(lastNotification, mBuilder.build());
        mNotificationManager.notify(0, mBuilder.build());

        Intent intent1 = new Intent(ACTION_REFRESH_NOTIFICATION);
        context.sendBroadcast(intent1);

        Intent intent2 = new Intent(ACTION_REFRESH_MAINACTIVITY);
        context.sendBroadcast(intent2);

        Intent intent3 = new Intent(ACTION_REFRESH_HOMEACTIVITY);
        context.sendBroadcast(intent3);
    }

    public void cancelNotification(int notificationId) {
        if (mNotificationManager == null) {
            mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        try {
            // mNotificationManager.cancel(notificationId);
            mNotificationManager.cancel(0);
        }
        catch (Exception ex) {
            Log.e("Notification", "Error in cancel notification");
            Log.e("Notification", "Error : " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
