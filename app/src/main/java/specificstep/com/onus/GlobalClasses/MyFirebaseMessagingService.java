package specificstep.com.onus.GlobalClasses;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import specificstep.com.onus.Database.DatabaseHelper;
import specificstep.com.onus.Models.DateTime;
import specificstep.com.onus.utility.NotificationUtil;

/**
 * Created by ubuntu on 6/2/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    DatabaseHelper databaseHelper;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        databaseHelper = new DatabaseHelper(MyFirebaseMessagingService.this);
        //Calling method to generate notification
        sendNotification(remoteMessage.getNotification().getBody(), DateTime.getTime());
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody, String date_time) {

//        // --- START --- save recharge message.
//        NotificationModel model = new NotificationModel();
//        model.title = "Recharge Notification";
//        model.message = messageBody;
//        model.receiveDateTime = date_time;
//        model.saveDateTime = DateTime.getDateTimeString();
//        model.readFlag = "0";
//        model.readDateTime = "";
//        Log.d("Notification", "title : " + model.title + "Message : " + model.message + "Receive DateTime : " + date_time);
//        databaseHelper.addNotificationData(model);
//        // --- END ---
//
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("Recharge Notification")
//                .setContentText(messageBody)
//                .setStyle(new android.support.v7.app.NotificationCompat.BigTextStyle().bigText(messageBody))
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0, notificationBuilder.build());

        Log.d("Notification", "title : " + "Recharge Notification" + "Message : " + messageBody + "Receive DateTime : " + date_time);
        new NotificationUtil(this)
                .sendNotification("Recharge Notification",
                        messageBody, DateTime.getCurrentDateTime());
    }
}
