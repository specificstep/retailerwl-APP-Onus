package specificstep.com.onus.Activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import specificstep.com.onus.Database.DatabaseHelper;
import specificstep.com.onus.Database.NotificationTable;
import specificstep.com.onus.Fragments.RechargeFragment;
import specificstep.com.onus.Fragments.RechargeMainFragment;
import specificstep.com.onus.GlobalClasses.Constants;
import specificstep.com.onus.GlobalClasses.MCrypt;
import specificstep.com.onus.GlobalClasses.URL;
import specificstep.com.onus.Models.DateTime;
import specificstep.com.onus.Models.Default;
import specificstep.com.onus.Models.User;
import specificstep.com.onus.R;
import specificstep.com.onus.utility.InternetUtil;
import specificstep.com.onus.utility.LogMessage;
import specificstep.com.onus.utility.MyPrefs;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    MenuItem menuItem;
    private final int SUCCESS_BALANCE = 1, ERROR = 2;
    private Context context;
    /* [START] - Other class objects */
    // Custom log message class
    // private LogMessage log;
    private SharedPreferences sharedPreferences;
    private MyPrefs prefs;
    // All static variables class
    private Constants constants;
    // Database class
    private DatabaseHelper databaseHelper;
    // [END]

    /* [START] - Controls objects */
    private LinearLayout llRecharge, llRecentTransaction, llTransSearch, llLogout,
            llMain, llUpdate, llChangePassword, llNotification, llCashBook, llComplainReport, llAccountLedger;
    private TextView txtTotalNotification;
    // Bottom navigation control view
    private BottomNavigationView navigation;
    // [END]

    /* [START] - Variables */
    private String deviceId = "", balance = "", title = "";
    // Position is use for which fragment display
    private int position = 0;
    // Get user data from database and store into array list
    private ArrayList<User> userArrayList;
    // Count down timer variables
//    private long onFinishCallTime = 5000, onTickCallTime = 1000;
    // [END]

    // Notification receiver
    private BroadcastReceiver notificationReceiver = null;
    public static final String ACTION_REFRESH_NOTIFICATION = "specificstep.com.metroenterprise.REFRESH_NOTIFICATION";

    private final String MENU_RECHARGE = "Recharge";

    private Context getContextInstance() {
        if (context == null) {
            context = MainActivity.this;
            return context;
        } else {
            return context;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_testing);

        context = MainActivity.this;
        /* [START] - Set actionbar title */
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#4F7D7B\">" + "\t" + getResources().getString(R.string.app_name) + "</font>"));
        // [END]

        initControls();
        getBundleData();
        setListener();

        // Display number of unread message
        setNotificationCounter();
        // Start count down timer for count total number of unread message
//        countDownTimer.start();

        // new NotificationUtil(MainActivity.this).sendNotification("Test", "Notification size : " + Constants.TOTAL_UNREAD_NOTIFICATION);
        // send testing notification message
        // String message = "9925049355 - Success, Aircel, Flexi, 123465789798987, 2017-03-27 06:00:00";
        // new NotificationUtil(MainActivity.this).sendNotification("Demo", message);

        // TODO : Update data after login
        updateDataAfterLogin();
    }

    private void updateDataAfterLogin() {
        boolean checkDataUpdateRequire = false;
        // Update data if database is empty
        if (databaseHelper.checkEmpty() == false) {
            checkDataUpdateRequire = true;
        } else
            LogMessage.d("Data not empty");
        // Update data if date change
        String updateDate = prefs.retriveString(constants.PREF_UPDATE_DATE, "0");
        String currentDate = DateTime.getDate();
        if (TextUtils.equals(updateDate, "0")) {
            checkDataUpdateRequire = true;
        } else
            LogMessage.d("Update date available");
        if (!TextUtils.equals(updateDate, currentDate)) {
            checkDataUpdateRequire = true;
        } else
            LogMessage.d("Update date and current date are same");
        if (checkDataUpdateRequire) {
            position = 6;
            Intent intent = new Intent(getContextInstance(), HomeActivity.class);
            intent.putExtra("position", position);
            intent.putExtra(constants.KEY_REQUIRE_UPDATE, "1");
            startActivity(intent);
        }
    }

    private void initControls() {
        /* [START] - Initialise class objects */
        // LogMessage = new LogMessage(MainActivity.class.getSimpleName());
        constants = new Constants();
        databaseHelper = new DatabaseHelper(getContextInstance());
        sharedPreferences = getSharedPreferences(constants.SHAREEDPREFERENCE, Context.MODE_PRIVATE);
        prefs = new MyPrefs(getContextInstance(), constants.PREF_NAME);
        // [END]

        /* [START] - get user data from database and store into array list */
        userArrayList = databaseHelper.getUserDetail();
        // [END]

        /* [START] - Initialise control objects */
        // Bottom navigation control
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        // All linear layout
        llRecharge = (LinearLayout) findViewById(R.id.lin_recharge_act_main);
        llRecentTransaction = (LinearLayout) findViewById(R.id.lin_recent_trans_act_main);
        llTransSearch = (LinearLayout) findViewById(R.id.lin_trans_search_act_main);
        llUpdate = (LinearLayout) findViewById(R.id.lin_update_button);
        llChangePassword = (LinearLayout) findViewById(R.id.ll_ChangePassword_act_main);
        llNotification = (LinearLayout) findViewById(R.id.lin_Notification_act_main);
        llCashBook = (LinearLayout) findViewById(R.id.lin_CashBook_act_main);
        llLogout = (LinearLayout) findViewById(R.id.lin_logout_act_main);
        llMain = (LinearLayout) findViewById(R.id.ll_main);

        llComplainReport = (LinearLayout) findViewById(R.id.ll_complain_report_act_main);
        llAccountLedger = (LinearLayout) findViewById(R.id.ll_account_ledger_act_main);


        // Text view
        txtTotalNotification = (TextView) findViewById(R.id.txt_TotalNotification);
        // [END]

        registerNotificationReceiver();

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
                MainActivity.this.registerReceiver(notificationReceiver, intentFilter);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LogMessage.e("Error in register receiver");
            LogMessage.e("Error : " + ex.getMessage());
        }
        // [END]
    }

    private void unregisterNotificationReceiver() {
        try {
            if (notificationReceiver != null) {
                MainActivity.this.unregisterReceiver(notificationReceiver);
                notificationReceiver = null;
            }
        } catch (Exception ex) {
            LogMessage.e("Error in un register receiver");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void getBundleData() {
        if (getIntent().getStringExtra("device_id") != null
                && !TextUtils.isEmpty(getIntent().getStringExtra("device_id"))) {
            deviceId = getIntent().getStringExtra("device_id");
        }
    }

    private void setListener() {
        // Navigation selected listener
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // On click listener
        llRecharge.setOnClickListener(this);
        llRecentTransaction.setOnClickListener(this);
        llTransSearch.setOnClickListener(this);
        llLogout.setOnClickListener(this);
        llUpdate.setOnClickListener(this);
        llChangePassword.setOnClickListener(this);
        llNotification.setOnClickListener(this);
        llCashBook.setOnClickListener(this);

        llComplainReport.setOnClickListener(this);
        llAccountLedger.setOnClickListener(this);

    }

    /**
     * Display number of unread message
     */
    public void setNotificationCounter() {
        // Get total number of unread message and store into global static variable
        Constants.TOTAL_UNREAD_NOTIFICATION = new NotificationTable(getContextInstance()).getNumberOfNotificationRecord() + "";
        int totalNotification = 0;
        try {
            totalNotification = Integer.parseInt(Constants.TOTAL_UNREAD_NOTIFICATION);
        } catch (Exception ex) {
            ex.printStackTrace();
            LogMessage.e("Error in notification");
            LogMessage.d("Error : " + ex.getMessage());
            totalNotification = 0;
        }
        // Check if total number of unread message if grater then 0 then display total notification text view
        if (totalNotification > 0) {
            txtTotalNotification.setVisibility(View.VISIBLE);
            txtTotalNotification.setText(Constants.TOTAL_UNREAD_NOTIFICATION);
        }
        // else number of unread message is 0 then set notification text view visible gone
        else {
            txtTotalNotification.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem = menu.findItem(R.id.action_balance_menu_main);
        /*set balance on actionbar*/
        // makeJsonBalance(menuItem);
        makeBalance();
        setNotificationCounter();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {

        Intent intent = null;
        if (v == llRecharge) {
            position = 0;
            intent = new Intent(getContextInstance(), HomeActivity.class);
            intent.putExtra("position", position);

            //@kns.p

            //openFragment(MENU_RECHARGE, new RechargeFragment());

          /*  Bundle bundle = new Bundle();
            bundle.putString("company_id", "");
            bundle.putString("product_id", "");
            bundle.putString("company_name","");
            bundle.putString("company_image", "");
            bundle.putString("product_name", "");
            bundle.putString("product_image","");

            RechargeFragment rechargeFragment = new RechargeFragment();
            rechargeFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, rechargeFragment).addToBackStack(null).commit();
*/

        } else if (v == llRecentTransaction) {
            position = 1;
            intent = new Intent(getContextInstance(), HomeActivity.class);
            intent.putExtra("position", position);

        } else if (v == llTransSearch) {
            position = 2;
            intent = new Intent(getContextInstance(), HomeActivity.class);
            intent.putExtra("position", position);
        } else if (v == llComplainReport) {
            position = 3;
            intent = new Intent(getContextInstance(), HomeActivity.class);
            intent.putExtra("position", position);
        } else if (v == llCashBook) {
            position = 4;
            intent = new Intent(getContextInstance(), HomeActivity.class);
            intent.putExtra("position", position);
        } else if (v == llAccountLedger) {
           // Toast.makeText(MainActivity.this,"coming soon!",Toast.LENGTH_LONG).show();

            position = 5;
            intent = new Intent(getContextInstance(), HomeActivity.class);
            intent.putExtra("position", position);
        } else if (v == llUpdate) {
            position = 6;
            intent = new Intent(getContextInstance(), HomeActivity.class);
            intent.putExtra("position", position);
        } else if (v == llChangePassword) {
            position = 7;
            intent = new Intent(getContextInstance(), HomeActivity.class);
            intent.putExtra("position", position);
        } else if (v == llNotification) {
            position = 8;
            intent = new Intent(getContextInstance(), HomeActivity.class);
            intent.putExtra("position", position);
        } else if (v == llLogout) {
            intent = new Intent(getContextInstance(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            sharedPreferences.edit().clear().commit();
            sharedPreferences.edit().putBoolean(constants.LOGOUT, true).commit();
            sharedPreferences.edit().putString(constants.VERIFICATION_STATUS, "1").commit();
            sharedPreferences.edit().putString(constants.LOGIN_STATUS, "0").commit();
        }
        if (intent != null)
            startActivity(intent);
    }

    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /* [START] - 2017_04_28 - Add native code for update balance, and Remove volley code */
    private void makeBalance() {
        // create new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.balance;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            userArrayList.get(0).getUser_name(),
                            userArrayList.get(0).getDevice_id(),
                            userArrayList.get(0).getOtp_code(),
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    myHandler.obtainMessage(SUCCESS_BALANCE, response).sendToTarget();
                } catch (Exception ex) {
                    LogMessage.e("Error in Cash book native method");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    // myHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    // parse success response
    private void parseBalanceResponse(String response) {
        LogMessage.i("Balance Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {

                String encrypted_response = jsonObject.getString("data");
                String decrypted_data = decryptAPI(encrypted_response);
                JSONObject object = new JSONObject(decrypted_data);
                balance = object.getString("balance");
                menuItem.setTitleCondensed(getResources().getString(R.string.Rs) + "  " + balance);
            } else {
                LogMessage.d("Balance response not found. Status = " + jsonObject.getString("status"));
            }
        } catch (JSONException e) {
            LogMessage.e("Error while get balance");
            LogMessage.e("Error : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // display error in dialog
    private void displayErrorDialog(String message) {
        new AlertDialog.Builder(getContextInstance())
                .setTitle("Info!")
                .setCancelable(false)
                .setMessage(message)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    // handle thread messages
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == SUCCESS_BALANCE) {
                parseBalanceResponse(msg.obj.toString());
            } else if (msg.what == ERROR) {
                displayErrorDialog(msg.obj.toString());
            }
        }
    };
    // [END]

    /*Method : decryptAPI
          Decrypt response of webservice*/
    public String decryptAPI(String response) {
        ArrayList<Default> defaultArrayList;
        defaultArrayList = databaseHelper.getDefaultSettings();
        String user_id = defaultArrayList.get(0).getUser_id();
        MCrypt mCrypt = new MCrypt(user_id, userArrayList.get(0).getDevice_id());
        String decrypted_response = null;

        byte[] decrypted_bytes = Base64.decode(response, Base64.DEFAULT);
        try {
            decrypted_response = new String(mCrypt.decrypt(mCrypt.bytesToHex(decrypted_bytes)), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decrypted_response;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    position = 7;
                    title = "Change Password";
                    getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#4F7D7B\">" + "\t" + title + "</font>"));
                    llMain.setVisibility(View.GONE);
                    Intent intent_ChangePassword = new Intent(getContextInstance(), HomeActivity.class);
                    intent_ChangePassword.putExtra("position", position);
                    startActivity(intent_ChangePassword);
                    return true;
                case R.id.navigation_dashboard:
//                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
//                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // stop notification update count down timer
        unregisterNotificationReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop notification update count down timer
        unregisterNotificationReceiver();
    }

    /* [START] - Custom check notification data class */
    private class CheckNotification extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogMessage.d("Receiver action : " + action);
            if (action.equals(ACTION_REFRESH_NOTIFICATION)) {
                LogMessage.i("Receiver call ACTION_REFRESH_MAINACTIVITY");
                try {
                    setNotificationCounter();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    LogMessage.e("Error in receiver ACTION_REFRESH_MAINACTIVITY");
                    LogMessage.e("Error : " + ex.getMessage());
                }
            }
        }
    }
    // [END]


    private void openFragment(String title, Fragment fragment) {
        getSupportActionBar().setTitle(title);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // fragmentTransaction.add(R.id.container, fragment).commit();
        fragmentTransaction.replace(R.id.container, fragment).commit();
    }
}