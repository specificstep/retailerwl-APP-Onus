package specificstep.com.onus.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import specificstep.com.onus.Activities.HomeActivity;
import specificstep.com.onus.Activities.MainActivity;
import specificstep.com.onus.Adapters.RecentTransListAdapter;
import specificstep.com.onus.Database.DatabaseHelper;
import specificstep.com.onus.GlobalClasses.CheckConnection;
import specificstep.com.onus.GlobalClasses.Constants;
import specificstep.com.onus.GlobalClasses.MCrypt;
import specificstep.com.onus.GlobalClasses.TransparentProgressDialog;
import specificstep.com.onus.GlobalClasses.URL;
import specificstep.com.onus.Interface.OnCustomClickListener;
import specificstep.com.onus.Models.Default;
import specificstep.com.onus.Models.Recharge;
import specificstep.com.onus.Models.User;
import specificstep.com.onus.R;
import specificstep.com.onus.utility.InternetUtil;
import specificstep.com.onus.utility.LogMessage;
import specificstep.com.onus.utility.Utility;

/**
 * Created by ubuntu on 9/1/17.
 */

public class RecentTransactionFragment extends Fragment implements RecentTransListAdapter.SubmitComplainClickListener ,OnCustomClickListener {
    // private LogMessage log;
    private final int SUCCESS = 1, ERROR = 2, SUCCESS_BALANCE = 3;
    // , ERROR = 3;
    // private Vector<AlertDialog> dialogs = new Vector<AlertDialog>();
    private Context context;
    private AlertDialog alertDialog_1, alertDialog_2, alertDialog_3;

    View view;
    ListView lv_rec_trans;
    DatabaseHelper databaseHelper;
    String str_mac_address, str_user_name, str_otp_code;
    boolean loadmoreFlage = false;
    boolean FLAG_INVALID_DETAIL = false;
    int count = 0;
    private TransparentProgressDialog transparentProgressDialog;
    private View footerView;
    private View footerViewNoMoreData;
    ArrayList<User> userArrayList;
    ArrayList<Recharge> rechargeArrayList;
    ArrayList<Recharge> beforeRefreshArrayList;

    int start = 0, end = 10;
    View view_list;
    RecentTransListAdapter searchListAdapter;

    String balance;
    SharedPreferences sharedPreferences;
    Constants constants;

    // count down timer times
    private long onFinishCallTime = 12000;
    private long onTickCallTime = 1000;

    // private boolean callUpdateListView = false;
    private int UPDATE = 1;

    TextView textView2;

    private HomeActivity mainActivity() {
        return ((HomeActivity) getActivity());
    }

    private Context getContextInstance() {
        if (context == null) {
            context = RecentTransactionFragment.this.getActivity();
            return context;
        } else {
            return context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recent_transaction, null);
        context = RecentTransactionFragment.this.getActivity();
        // log = new LogMessage(RecentTransactionFragment.class.getSimpleName());
        footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.load_more_items, null);
        footerViewNoMoreData = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_no_moredata, null);
        constants = new Constants();
        sharedPreferences = getActivity().getSharedPreferences(constants.SHAREEDPREFERENCE, Context.MODE_PRIVATE);
        userArrayList = new ArrayList<User>();
        rechargeArrayList = new ArrayList<Recharge>();
        beforeRefreshArrayList = new ArrayList<Recharge>();
        databaseHelper = new DatabaseHelper(getContextInstance());

        transparentProgressDialog = new TransparentProgressDialog(getContextInstance(), R.drawable.fotterloading);

        textView2 = (TextView) view.findViewById(R.id.textView2);

        userArrayList = databaseHelper.getUserDetail();
        str_mac_address = userArrayList.get(0).getDevice_id();
        str_user_name = userArrayList.get(0).getUser_name();
        str_otp_code = userArrayList.get(0).getOtp_code();

//        mainActivity().setNotificationCounter();

        init();
        showProgressDialog();
        /*call webservice only if user
        is connected with internet*/
        CheckConnection checkConnection = new CheckConnection();
        if (checkConnection.isConnectingToInternet(getContextInstance()) == true) {
            // makeJsonBalance();
            makeBalance();
            makeNativeRecentTransaction();

        } else {
            dismissProgressDialog();
            // progressDialog1.dismiss();
            Utility.toast(getContextInstance(), "Check your internet connection");
        }

        searchListAdapter = new RecentTransListAdapter(getContextInstance(), rechargeArrayList,
                this
        ,RecentTransactionFragment.this);
        lv_rec_trans.setAdapter(searchListAdapter);

        // start update recent transaction timer
        // callUpdateListView = true;
        // updateRecentTransactionTimer.start();
        // updateListView();

        return view;
    }

    private void init() {
        view_list = (View) view.findViewById(R.id.view_recent_trans);
        lv_rec_trans = (ListView) view.findViewById(R.id.lv_rec_trans_fragment_rec_trans);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            mainActivity().openRecentTransactionFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    /* [START] - 2017_04_27 - Add native code for recent transaction, and Remove volley code */
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
        LogMessage.i("balance Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {

                String encrypted_response = jsonObject.getString("data");
                String decrypted_data = decryptAPI(encrypted_response);
                JSONObject object = new JSONObject(decrypted_data);
                balance = object.getString("balance");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void makeNativeRecentTransaction() {
        // create new thread for recent transaction
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set latest_recharge url
                    String url = URL.latest_recharge;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "start",
                            "end",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            str_user_name,
                            str_mac_address,
                            str_otp_code,
                            start + "",
                            end + "",
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    myHandler.obtainMessage(SUCCESS, response).sendToTarget();
                } catch (Exception ex) {
                    LogMessage.e("Error in recent transaction native method");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    private void parseTransactionResponse(String response) {
        LogMessage.i("Latest Search Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            String responseStatus = jsonObject.getString("status");
            // responseStatus = "3";
            if (responseStatus.equals("1")) {
                long delayInMillis = 1000;
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                    }
                }, delayInMillis);
                view_list.setVisibility(View.VISIBLE);
                String encrypted_response = jsonObject.getString("data");
                String decrypted_response = decryptAPI(encrypted_response);
                LogMessage.d("Response Recent Transaction : " + decrypted_response);
                loadMoreData(decrypted_response);
            } else if (responseStatus.equals("2") && jsonObject.getString("msg").equalsIgnoreCase("Invalid details")) {

                // progressDialog1.dismiss();
                // lv_rec_trans.removeFooterView(footerView);
                removeFooterView();
                lv_rec_trans.addFooterView(footerViewNoMoreData);
                loadmoreFlage = true;
                FLAG_INVALID_DETAIL = true;
                count++;
                LogMessage.d("Count ++ : " + count);
                /* [START] - 2017_04_25 - Close all alert dialog logic */
                alertDialog_2 = new AlertDialog.Builder(getContextInstance()).create();
                alertDialog_2.setTitle("Info!");
                alertDialog_2.setCancelable(false);
                alertDialog_2.setMessage(jsonObject.getString("msg"));
                alertDialog_2.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog_2.dismiss();
                    }
                });
                // Showing Alert Message
                alertDialog_2.show();
                // [END]
//                if (count >= 3) {
//                    Toast.makeText(RecentTransactionFragment.this.getActivity(), "You have attempted more than 3 times", Toast.LENGTH_SHORT).show();
//                    Intent i = new Intent(RecentTransactionFragment.this.getActivity(), RegistrationActivity.class);
//                    startActivity(i);
//                }
            } else {
                if (start == 0) {
                    /* [START] - 2017_04_25 - Close all alert dialog logic */
                    alertDialog_1 = new AlertDialog.Builder(getContextInstance()).create();
                    // Setting Dialog Title
                    alertDialog_1.setTitle("Info!");
                    // set cancelable
                    alertDialog_1.setCancelable(false);
                    // Setting Dialog Message
                    alertDialog_1.setMessage(jsonObject.getString("msg"));
                    // Setting OK Button
                    alertDialog_1.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog_1.dismiss();
                        }
                    });
                    // Showing Alert Message
                    alertDialog_1.show();
                    // [END]
                } else {
                    // textView2.setVisibility(View.VISIBLE);
                }
                // lv_rec_trans.removeFooterView(footerView);
                removeFooterView();
                lv_rec_trans.addFooterView(footerViewNoMoreData);
                loadmoreFlage = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Utility.toast(getContextInstance(), "Please check your internet access");
        }
    }

    private void displayRechargeErrorDialog(String message) {
        /* [START] - 2017_05_01 - Close all alert dialog logic */
        try {
            alertDialog_3 = new AlertDialog.Builder(getContextInstance()).create();
            alertDialog_3.setTitle("Info!");
            alertDialog_3.setCancelable(false);
            alertDialog_3.setMessage(message);
            alertDialog_3.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog_3.dismiss();
                }
            });
            alertDialog_3.show();
        } catch (Exception ex) {
            LogMessage.e("Error in error dialog");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
            try {
                Utility.toast(getContextInstance(), message);
            } catch (Exception e) {
                LogMessage.e("Error in toast message");
                LogMessage.e("ERROR : " + e.getMessage());
            }
        }
        // [END]
    }

    // handle recent transaction messages
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == SUCCESS) {
                dismissProgressDialog();
                parseTransactionResponse(msg.obj.toString());
            } else if (msg.what == SUCCESS_BALANCE) {
                parseBalanceResponse(msg.obj.toString());
            } else if (msg.what == ERROR) {
                dismissProgressDialog();
                displayRechargeErrorDialog(msg.obj.toString());
            }

        }
    };
    // [END]

    /* Method  name :makeRecentTransaction
    Webservice for load recent transactions */
//    private void makeRecentTransaction() {
//
//    }

    // Close all alert dialogs
    public void closeDialogs() {
        LogMessage.d("Close dialogs call");
        if (alertDialog_1 != null) {
            if (alertDialog_1.isShowing()) {
                LogMessage.d("Close dialog alertDialog_1");
                alertDialog_1.dismiss();
            }
        }
        if (alertDialog_2 != null) {
            if (alertDialog_2.isShowing()) {
                LogMessage.d("Close dialog alertDialog_2");
                alertDialog_2.dismiss();
            }
        }
        if (alertDialog_3 != null) {
            if (alertDialog_3.isShowing()) {
                LogMessage.d("Close dialog alertDialog_3");
                alertDialog_3.dismiss();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeDialogs();
        LogMessage.d("Recent transaction onDestroy call");
    }



    /*Method : decryptAPI
          Decrypt response of webservice*/

    public String decryptAPI(String response) {
        ArrayList<Default> defaultArrayList;
        defaultArrayList = databaseHelper.getDefaultSettings();

        String user_id = defaultArrayList.get(0).getUser_id();
        MCrypt mCrypt = new MCrypt(user_id, str_mac_address);
        String decrypted_response = null;

        byte[] decrypted_bytes = Base64.decode(response, Base64.DEFAULT);
        try {
            decrypted_response = new String(mCrypt.decrypt(mCrypt.bytesToHex(decrypted_bytes)), "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return decrypted_response;
    }

    private void removeFooterView() {
        int footerCount = lv_rec_trans.getFooterViewsCount();
        LogMessage.d("Footer Count All : " + footerCount);
        lv_rec_trans.removeFooterView(footerView);
        lv_rec_trans.removeFooterView(footerViewNoMoreData);
    }

     /*Method : loadMoreData
          load data on scroll*/

    public void loadMoreData(String response) {
        try {
            JSONObject jsonObject1 = new JSONObject(response);
            JSONArray jsonArray = jsonObject1.getJSONArray("latest");

            if (jsonArray.length() < 10) {
                // lv_rec_trans.removeFooterView(footerView);
                removeFooterView();
                lv_rec_trans.addFooterView(footerViewNoMoreData);
                loadmoreFlage = true;
                // textView2.setVisibility(View.VISIBLE);
            } else {
                if (start == 0) {
                    removeFooterView();
                    lv_rec_trans.addFooterView(footerView);
                }
                loadmoreFlage = false;
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Recharge recharge = new Recharge();
                recharge.setClient_trans_id(object.getString("client_transaction_id"));
                recharge.setMo_no(object.getString("mobile"));
                recharge.setAmount(object.getString("amount"));
                recharge.setCompnay_name(object.getString("company_name"));
                recharge.setProduct_name(object.getString("product_name"));
                recharge.setTrans_date_time(object.getString("trans_date_time"));
                recharge.setStatus(object.getString("status"));
                recharge.setRecharge_status(object.getString("recharge_status"));
                recharge.setOperator_trans_id(object.getString("operator_trans_id"));

                rechargeArrayList.add(recharge);

            }

            searchListAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            new AlertDialog.Builder(getContextInstance())
                    .setTitle("Alert!")
                    .setCancelable(false)
                    .setMessage(getResources().getString(R.string.alert_servicer_down))
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        }

        lv_rec_trans.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) {
                    // LogMessage.e("Scroll State Idle : " + scrollState);
                    updateRecentTransactionTimer.start();
                } else {
                    // LogMessage.e("Scroll State : " + scrollState);
                    updateRecentTransactionTimer.cancel();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if ((firstVisibleItem + visibleItemCount - 1) == rechargeArrayList.size() && !(loadmoreFlage)) {
                    loadmoreFlage = true;
                    start = start + 10;
                    end = 10;
                    // makeRecentTransaction();
                    makeNativeRecentTransaction();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // callUpdateListView = true;
        updateRecentTransactionTimer.start();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener
                    Intent intent = new Intent(getContextInstance(), MainActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        // callUpdateListView = false;
        updateRecentTransactionTimer.cancel();
    }

    /* [START] - Update data after 10 second interval. */
    private void updateListView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // while (true) {
                LogMessage.d("Run() call");
                myhandler.obtainMessage(UPDATE).sendToTarget();
                // }
            }
        }, 10000);
    }

    private Handler myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UPDATE) {
                LogMessage.d("handler update call");
                try {
                    if(Constants.isDialogOpen!=true){
                        updateRecentTransaction();
                    }

                } catch (Exception ex) {
                    LogMessage.d("Error in updateListView()");
                    LogMessage.d("Error : " + ex.getMessage());
                    ex.printStackTrace();
                }
//                if (callUpdateListView) {
                LogMessage.d("Again start thread");
                if(Constants.isDialogOpen!=true){
                     updateListView();
                }

//            }
            }
        }
    };

    private void updateRecentTransaction() {
        mainActivity().openRecentTransactionFragment();
    }
    // [END]

    /* [START] - Update data after 10 second interval. */
    private CountDownTimer updateRecentTransactionTimer = new CountDownTimer(onFinishCallTime, onTickCallTime) {
        @Override
        public void onTick(long millisUntilFinished) {
            // LogMessage.d("Update Time : " + millisUntilFinished / 1000);
        }

        @Override
        public void onFinish() {
            try {
//                if (callUpdateListView) {
                LogMessage.d("Call update recent transaction");
                if(Constants.isDialogOpen!=true){
                    updateRecentTransaction();
                }

                // updateRecentTransactionTimer.start();
//                }
//                else {
//                    LogMessage.d("Not update recent transaction");
//                }
            } catch (Exception ex) {
                LogMessage.d("Error in onFinish()");
                LogMessage.d("Error : " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    };
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
        } catch (Exception ex) {
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
        } catch (Exception ex) {
            LogMessage.e("Error in dismiss progress");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void onComplainClick(int position, String complainMessage) {
        Log.e("onComplainClick", "Position : " + position + " Complain Text " + complainMessage);

        showProgressDialog();
        /*call webservice only if user
        is connected with internet*/
        CheckConnection checkConnection = new CheckConnection();
        if (checkConnection.isConnectingToInternet(getContextInstance()) == true) {
            addComplain(position, complainMessage);
        } else {
            dismissProgressDialog();
            // progressDialog1.dismiss();
            Utility.toast(getContextInstance(), "Check your internet connection");
        }
    }

    private void addComplain(int position, String complainMessage) {
        // create new thread for recent transaction
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set latest_recharge url6
                    String url = URL.complain;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "trans_id",
                            "provider_id",
                            "amount",
                            "mobile",
                            "reason_id",
                            "description",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            str_user_name,
                            str_mac_address,
                            str_otp_code,
                            rechargeArrayList.get(position).getClient_trans_id(),
                            "",
                            rechargeArrayList.get(position).getAmount(),
                            rechargeArrayList.get(position).getMo_no(),
                            "1",
                            complainMessage,
                            Constants.APP_VERSION

                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    complainHandler.obtainMessage(SUCCESS, response).sendToTarget();
                } catch (Exception ex) {
                    LogMessage.e("Error in recent transaction native method");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    complainHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    // handle add complain messages
    private Handler complainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == SUCCESS) {
                dismissProgressDialog();
                parseComplainResponse(msg.obj.toString());
            } else if (msg.what == ERROR) {
                dismissProgressDialog();
                displayComplainDialog(msg.obj.toString());
            }
        }
    };

    private void displayComplainDialog(String message) {
        alertDialog_1 = new AlertDialog.Builder(getContextInstance()).create();
        // Setting Dialog Title
        alertDialog_1.setTitle("Info!");
        // set cancelable
        alertDialog_1.setCancelable(false);
        // Setting Dialog Message
        alertDialog_1.setMessage(message);
        // Setting OK Button
        alertDialog_1.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog_1.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog_1.show();
    }

    private void parseComplainResponse(String response) {
        LogMessage.i("Latest Complain Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            String responseStatus = jsonObject.getString("status");

            if (responseStatus.equals("1")) {
                String encrypted_response = jsonObject.getString("data");
                String decrypted_response = decryptAPI(encrypted_response);
                LogMessage.d("Decrypted Complain Response  : " + decrypted_response);

            }else{

            }

            displayComplainDialog(jsonObject.getString("msg"));

        } catch (JSONException e) {
            e.printStackTrace();
            Utility.toast(getContextInstance(), "Please check your internet access");
        }
    }

    @Override
    public void OnCustomClick(View aView, int position) {
        //show dialog.......
    }
}
