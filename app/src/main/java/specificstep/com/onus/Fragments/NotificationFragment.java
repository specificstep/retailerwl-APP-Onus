package specificstep.com.onus.Fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import specificstep.com.onus.Activities.HomeActivity;
import specificstep.com.onus.Activities.MainActivity;
import specificstep.com.onus.Adapters.NotificationAdapter;
import specificstep.com.onus.Database.DatabaseHelper;
import specificstep.com.onus.Database.NotificationTable;
import specificstep.com.onus.GlobalClasses.Constants;
import specificstep.com.onus.GlobalClasses.TransparentProgressDialog;
import specificstep.com.onus.Models.DateTime;
import specificstep.com.onus.Models.NotificationModel;
import specificstep.com.onus.R;
import specificstep.com.onus.utility.LogMessage;
import specificstep.com.onus.utility.NotificationUtil;

/**
 * Created by ubuntu on 14/3/17.
 */

public class NotificationFragment extends Fragment {

//    private static final String ARG_NOTIFICATION_ID = "notification_id";

//    public static NotificationFragment newInstance(String notificationId) {
//        NotificationFragment fragment = new NotificationFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_NOTIFICATION_ID, notificationId);
//        fragment.setArguments(args);
//        return fragment;
//    }

    // private LogMessage log;
    // View for set layout in frame layout
    private View view;
    // ListView for
    private ListView lstNotification;
    // TextView for display total number of unread notification message
    private TextView txtNewNotification;
    // Dialog for display notification message
    // private Dialog dialogNotification;
    // Database for get notification data from database
    private DatabaseHelper databaseHelper;
    // Notification Model List for store notification message data
    private ArrayList<NotificationModel> notificationModels;
    // Adapter of notification message list
    private NotificationAdapter notificationAdapter;
    private TransparentProgressDialog transparentProgressDialog;
    private static final int SUCCESS_NOTIFICATION = 1;

    // count down timer times
    private long onFinishCallTime = 3000;
    private long onTickCallTime = 1000;
//    private boolean callUpdateListView = false;
//    private int UPDATE = 1;

    // Notification receiver
    private Context context;
    boolean isNotify = false;
    private NotificationUtil notificationUtil;
    private BroadcastReceiver notificationReceiver = null;
    public static final String ACTION_REFRESH_NOTIFICATION = "specificstep.com.metroenterprise.REFRESH_NOTIFICATION";

    private Context getContextInstance() {
        if (context == null) {
            context = NotificationFragment.this.getActivity();
            return context;
        } else {
            return context;
        }
    }

    private HomeActivity mainActivity() {
        return ((HomeActivity) getActivity());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification, null);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // log = new LogMessage(NotificationFragment.class.getSimpleName());

        mainActivity().setNotificationCounter();

        initControls();
        setListView();
        setListener();
        getBundleData();

        // Start count down timer for update notification list
        // updateListCountDownTimer.start();
//        callUpdateListView = true;
//        updateListView();

        return view;
    }

    private void getBundleData() {
        /* [START] - Display notification dialog if user press on notification */
        Bundle bundle = NotificationFragment.this.getArguments();
        String notificationId = "-1";
        if (bundle != null) {
            if (bundle.getString(Constants.KEY_NOTIFICATION_ID) != null) {
                notificationId = bundle.getString(Constants.KEY_NOTIFICATION_ID, "");
                LogMessage.d("Notification Id : " + notificationId);
                ArrayList<NotificationModel> notificationModels = new NotificationTable(NotificationFragment.this.getActivity()).getNotificationData(notificationId);
                if (notificationModels.size() > 0) {
                    NotificationModel model = notificationModels.get(0);
                    showNotificationDialog(model.id, model.title, model.message, model.receiveDateTime);
                }
            }
        }
        // [END]
    }

    private void initControls() {
        // NotificationUtil notificationUtil = AppController.getNotificationUtil();
        transparentProgressDialog = new TransparentProgressDialog(getActivity(), R.drawable.fotterloading);
        databaseHelper = new DatabaseHelper(getActivity());
        // dialogNotification = new Dialog(getActivity());
        txtNewNotification = (TextView) view.findViewById(R.id.txt_NewNotification);
        lstNotification = (ListView) view.findViewById(R.id.lst_Adapter_Notification);

        notificationUtil = new NotificationUtil(getContextInstance());

        // Already register in onResume
        // registerNotificationReceiver();
        // databaseHelper.backupDatabase();
    }

    private void registerNotificationReceiver() {
        /* [START] - Create custom notification for receiver notification data */
        try {
            if (notificationReceiver == null) {
                // Add notification filter
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(ACTION_REFRESH_NOTIFICATION);
                // Create notification object
                notificationReceiver = new CheckNotification();
                // Register receiver
                NotificationFragment.this.getActivity().registerReceiver(notificationReceiver, intentFilter);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            LogMessage.e("Error in register receiver");
            LogMessage.e("Error : " + ex.getMessage());
        }
        // [END]
    }

    private void unregisterNotificationReceiver() {
        try {
            if (notificationReceiver != null) {
                NotificationFragment.this.getActivity().unregisterReceiver(notificationReceiver);
                notificationReceiver = null;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            LogMessage.e("Error in register receiver");
            LogMessage.e("Error : " + ex.getMessage());
        }
    }

    private void setListView() {
        // Display progress dialog while loading notification data
        showProgressDialog();
        // Select notification data from database and store in array list
        notificationModels = new NotificationTable(getContextInstance()).getNotificationData_OrderBy();
        // check notification data is available or not
        if (notificationModels.size() > 0) {
            notificationAdapter = new NotificationAdapter(getContextInstance(), notificationModels);
            lstNotification.setAdapter(notificationAdapter);
            lstNotification.setVisibility(View.VISIBLE);
            txtNewNotification.setVisibility(View.GONE);
        } else {
            lstNotification.setVisibility(View.GONE);
            txtNewNotification.setVisibility(View.VISIBLE);
        }
        mainActivity().setNotificationCounter();
        myHandler.obtainMessage(SUCCESS_NOTIFICATION).sendToTarget();
    }

    private void setListener() {
//        txtNewNotification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveDummyData();
//            }
//        });
        lstNotification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if(!isNotify) {
//                    isNotify = true;
//                    notificationUtil.sendNotification("Test", "Demo : " + Constants.TOTAL_NOTIFICATION);
//                }
//                else {
//                    isNotify = false;
                NotificationModel model = (NotificationModel) notificationAdapter.getItem(position);
                LogMessage.d("Selected Item Id : " + model.id);
                showNotificationDialog(model.id, model.title, model.message, model.receiveDateTime);
//                }

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        // callUpdateListView = false;
        unregisterNotificationReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();

        registerNotificationReceiver();
        // callUpdateListView = true;
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

    }

    public void showNotificationDialog(final String notificationId, String title, String message, String receiveDateTime) {
        final Dialog dialogNotification = new Dialog(getActivity());
        dialogNotification.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogNotification.setContentView(R.layout.dialog_notification);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogNotification.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialogNotification.getWindow().setAttributes(lp);
        dialogNotification.setCancelable(false);

        TextView txtId = (TextView) dialogNotification.findViewById(R.id.txt_Dialog_NotificationId);
        TextView txtTitle = (TextView) dialogNotification.findViewById(R.id.txt_Dialog_NotificationTitle);
        TextView txtMessage = (TextView) dialogNotification.findViewById(R.id.txt_Dialog_NotificationMessage);
        TextView txtDateTime = (TextView) dialogNotification.findViewById(R.id.txt_Dialog_Notification_DateTime);
        TextView txtReadDateTime = (TextView) dialogNotification.findViewById(R.id.txt_Dialog_Notification_ReadDateTime);
        Button btn_ok = (Button) dialogNotification.findViewById(R.id.btn_Dialog_NotificationOk);

        /* [START] - Display message in proper format
            recharge notification message = 9925049355 - Success/Failure/Credit, Aircel, Flexi, 123465789798987, 2017-03-27 06:00:00
            Simple message = App installed successfully and shortcut created.
         */
        String formattedMessage = "";
        String originalMessage = message;
        String notificationDate = "";
        try {
            // Check message contains mobile number or not
            if (originalMessage.contains("-")) {
                formattedMessage = "Number : " + originalMessage.substring(0, originalMessage.indexOf("-")) + "\n";
                originalMessage = originalMessage.substring(originalMessage.indexOf("-") + 1, originalMessage.length());
                // Check message contains comma separated value or not
                if (originalMessage.contains(",")) {
                    // Convert comma separated value in list
                    List<String> notificationMessageItems = Arrays.asList(originalMessage.split("\\s*,\\s*"));
                    // check list contains all value or not
                    if (notificationMessageItems.size() == 5) {
                        // get all value from list
                        formattedMessage += "Transaction Id : " + notificationMessageItems.get(3) + "\n";
                        formattedMessage += "Status : " + notificationMessageItems.get(0) + "\n";
                        formattedMessage += "Company : " + notificationMessageItems.get(1) + "\n";
                        formattedMessage += "Product : " + notificationMessageItems.get(2);
                        // formattedMessage += "Date Time : " + notificationMessageItems.get(4);
                        notificationDate = notificationMessageItems.get(4);
                    } else {
                        // formattedMessage += "Message : " + originalMessage;
                        formattedMessage += originalMessage;
                    }
                } else {
                    // formattedMessage += "Message : " + originalMessage;
                    formattedMessage += originalMessage;
                }
            } else {
                formattedMessage = originalMessage;
            }
        }
        catch (Exception ex) {
            LogMessage.d("Error while format notification message");
            LogMessage.d("Error : " + ex.getMessage());
            ex.printStackTrace();
            formattedMessage = originalMessage;
            notificationDate = "";
        }
        // [END]

        txtId.setText(notificationId);
        txtTitle.setText(title);
        // txtMessage.setText(message);
        txtMessage.setText(formattedMessage);
        if (TextUtils.isEmpty(notificationDate))
            txtDateTime.setText("Date Time : " + receiveDateTime);
        else
            txtDateTime.setText("Date Time : " + notificationDate);
        txtId.setVisibility(View.GONE);

        /* [START] - Display notification read date */
        final ArrayList<NotificationModel> models = new NotificationTable(getContextInstance()).getNotificationData(notificationId);
        if (TextUtils.equals(models.get(0).readFlag, "0")) {
            txtReadDateTime.setText("Read : " + DateTime.getCurrentDateTime());
        } else {
            txtReadDateTime.setText("Read : " + models.get(0).readDateTime);
        }
        // [END]

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogNotification.dismiss();
                if (TextUtils.equals(models.get(0).readFlag, "0")) {
                    // Log.d(TAG, "Message is unread");
                    NotificationModel notificationModel = new NotificationModel();
                    notificationModel.id = models.get(0).id;
                    notificationModel.receiveDateTime = models.get(0).receiveDateTime;
                    notificationModel.message = models.get(0).message;
                    notificationModel.readFlag = "1";
                    notificationModel.saveDateTime = models.get(0).saveDateTime;
                    notificationModel.title = models.get(0).title;
                    notificationModel.readDateTime = DateTime.getCurrentDateTime();
                    new NotificationTable(getContextInstance()).updateNotification(notificationModel, notificationId);
                    setListView();

                    /* [START] - Remove notification */
                    int cancelNotificationId = 0;
                    try {
                        cancelNotificationId = Integer.parseInt(notificationModel.id);
                    }
                    catch (Exception ex) {
                        cancelNotificationId = 0;
                    }
                    notificationUtil.cancelNotification(cancelNotificationId);
                    // [END]
                }
            }
        });

        dialogNotification.show();
    }

    // Handler for handle message
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SUCCESS_NOTIFICATION:
                    dismissProgressDialog();
                    break;
            }
        }
    };

    private void saveDummyData() {
        NotificationModel model = new NotificationModel();

        String amount_1 = "5";
        String amount_2 = "10";
        String amount_3 = "100";

        String status_1 = "Pending";
        String status_2 = "Fail";
        String status_3 = "Success";

        String rechargeId = "03041418233016819222420172419643";
        String dateTime = "2017-03-14 18:23:04";

        int randomNum = new Random().nextInt((3 - 1) + 1) + 1;
        LogMessage.d("Random : " + "No : " + randomNum);

        String fullMessage_1 = "Mobile No : 9925049355\nAmount : " + amount_1 + "\nRecharge Status : " + status_1;
        String fullMessage_2 = "Mobile No : 9925049355\nAmount : " + amount_2 + "\nRecharge Status : " + status_2;
        String fullMessage_3 = "Mobile No : 9925049355\nAmount : " + amount_3 + "\nRecharge Status : " + status_3;

        if (randomNum == 1)
            model.message = fullMessage_1;
        else if (randomNum == 2)
            model.message = fullMessage_2;
        else if (randomNum == 3)
            model.message = fullMessage_3;
        else
            model.message = fullMessage_1;

        model.title = "Recharge Id : " + rechargeId;
        model.receiveDateTime = dateTime;
        model.saveDateTime = DateTime.getCurrentDateTime();
        model.readDateTime = "";
        model.readFlag = "0";

        LogMessage.d("Recharge : " + "title : " + "Recharge Id : " + rechargeId);
        LogMessage.d("Recharge : " + "Message : " + model.message);
        LogMessage.d("Recharge : " + "Receive DateTime : " + dateTime);

        new NotificationTable(getContextInstance()).addNotificationData(model);
    }

    private void updateNotificationList() {
        try {
            int oldTotalNotificationCounter = Constants.TOTAL_NOTIFICATION;
            Constants.TOTAL_NOTIFICATION = new NotificationTable(getContextInstance()).getAllNotificationRecordCounter();
            int newTotalNotificationCounter = Constants.TOTAL_NOTIFICATION;
            if (oldTotalNotificationCounter != newTotalNotificationCounter) {
                setListView();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            LogMessage.d("Notification : " + "Error : " + ex.toString());
        }
    }

    /* [START] - Count down timer for change notification counter, interval = 2 second */
    // private CountDownTimer countDownTimer = new CountDownTimer(5000, 1000) { // (onFinish call time, onTick call time)
//    private CountDownTimer updateListCountDownTimer = new CountDownTimer(onFinishCallTime, onTickCallTime) {
//        @Override
//        public void onTick(long millisUntilFinished) {
//             LogMessage.d("Update List remaining time : " + millisUntilFinished / 1000);
//        }
//
//        @Override
//        public void onFinish() {
//            try {
//                // LogMessage.d("On finish call");
//                updateNotificationList();
//                updateListCountDownTimer.start();
//            }
//            catch (Exception ex) {
//                LogMessage.d("Error in onFinish()");
//                LogMessage.d("Error : " + ex.getMessage());
//                ex.printStackTrace();
//            }
//        }
//    };
    // [END]

//    private void updateListView() {
////        new Thread(new Runnable() {
////            @Override
////            public void run() {
////
////            }
////        }).start();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // while (true) {
//                LogMessage.d("Run call");
//                myhandler.obtainMessage(UPDATE).sendToTarget();
//                // }
//            }
//        }, 3000);
//    }

//    private Handler myhandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == UPDATE) {
//                LogMessage.d("Run handler call");
//                try {
//                    updateNotificationList();
//                }
//                catch (Exception ex) {
//                    LogMessage.d("Error in updateListView()");
//                    LogMessage.d("Error : " + ex.getMessage());
//                    ex.printStackTrace();
//                }
//                if (callUpdateListView)
//                    // updateListView();
//            }
//        }
//    };

    //    /**
//     * Call this method for stop countDownTimer
//     */
//    public void stopNotificationUpdateTimer() {
//        Log.d("Notification", "Home Activity timer stop call");
//        try {
//            if (updateListCountDownTimer != null) {
//                Log.d("Notification", "Timer stop");
//                updateListCountDownTimer.cancel();
//            }
//        }
//        catch (Exception ex) {
//            Log.d("Notification", "Error while stop notification counter");
//            Log.d("Notification", "Error : " + ex.getMessage());
//            ex.printStackTrace();
//        }
//    }

    /* [START] - Custom check notification data class */
    private class CheckNotification extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogMessage.d("Receiver action : " + action);
            if (action.equals(ACTION_REFRESH_NOTIFICATION)) {
                LogMessage.i("Receiver call ACTION_REFRESH_NOTIFICATION");
                try {
                    updateNotificationList();
                    setListView();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    LogMessage.e("Error in ACTION_REFRESH_NOTIFICATION");
                    LogMessage.e("Error : " + ex.getMessage());
                }
            }
        }
    }
    // [END]

    // show progress dialog
    private void showProgressDialog() {
        try {
            if (transparentProgressDialog == null) {
                transparentProgressDialog = new TransparentProgressDialog(getContextInstance(), R.drawable.fotterloading);
            }
            if (transparentProgressDialog != null) {
                if (!transparentProgressDialog.isShowing()) {
                    transparentProgressDialog.show();
                }
            }
        }
        catch (Exception ex) {
            LogMessage.e("Error in show progress");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // dismiss progress dialog
    private void dismissProgressDialog() {
        try {
            if (transparentProgressDialog != null) {
                if (transparentProgressDialog.isShowing())
                    transparentProgressDialog.dismiss();
            }
        }
        catch (Exception ex) {
            LogMessage.e("Error in dismiss progress");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
