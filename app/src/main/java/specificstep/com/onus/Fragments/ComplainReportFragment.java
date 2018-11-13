package specificstep.com.onus.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import specificstep.com.onus.Activities.HomeActivity;
import specificstep.com.onus.Adapters.ComplainListAdapter;
import specificstep.com.onus.Adapters.RecentTransListAdapter;
import specificstep.com.onus.Database.DatabaseHelper;
import specificstep.com.onus.GlobalClasses.CheckConnection;
import specificstep.com.onus.GlobalClasses.Constants;
import specificstep.com.onus.GlobalClasses.MCrypt;
import specificstep.com.onus.GlobalClasses.TransparentProgressDialog;
import specificstep.com.onus.GlobalClasses.URL;
import specificstep.com.onus.Models.Complain;
import specificstep.com.onus.Models.Default;
import specificstep.com.onus.Models.Recharge;
import specificstep.com.onus.Models.User;
import specificstep.com.onus.R;
import specificstep.com.onus.utility.InternetUtil;
import specificstep.com.onus.utility.LogMessage;
import specificstep.com.onus.utility.Utility;

public class ComplainReportFragment extends Fragment {

    private Context context;

    private ListView lv_complain_trans;
    private TextView tv_no_data;

    private ArrayList<User> userArrayList;
    private DatabaseHelper databaseHelper;
    private String str_mac_address, str_user_name, str_otp_code;

    private int start = 0, end = 10;
    private final int SUCCESS = 1, ERROR = 2;
    private AlertDialog alertDialog;

    private TransparentProgressDialog progressDialog;

    private ArrayList<Complain> complainArrayList;
    private ComplainListAdapter complainListAdapter;

    private View footerView;
    private View footerViewNoMoreData;
    boolean loadmoreFlage = false;

    View view;

    private HomeActivity mainActivity() {
        return ((HomeActivity) getActivity());
    }

    private Context getContextInstance() {
        if (context == null) {
            context = ComplainReportFragment.this.getActivity();
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
        view = inflater.inflate(R.layout.fragment_complain_report, null);
        context = ComplainReportFragment.this.getActivity();


        footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.load_more_items, null);
        footerViewNoMoreData = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_no_moredata, null);

        databaseHelper = new DatabaseHelper(getContextInstance());
        userArrayList = databaseHelper.getUserDetail();
        str_mac_address = userArrayList.get(0).getDevice_id();
        str_user_name = userArrayList.get(0).getUser_name();
        str_otp_code = userArrayList.get(0).getOtp_code();

        init();

        showProgressDialog();
        /*call webservice only if user
        is connected with internet*/
        CheckConnection checkConnection = new CheckConnection();
        if (checkConnection.isConnectingToInternet(getContextInstance()) == true) {
            // makeJsonBalance();
            getComplainList();

        } else {
            dismissProgressDialog();
            // progressDialog1.dismiss();
            Utility.toast(getContextInstance(), "Check your internet connection");
        }
        return view;
    }


    private void init() {

        lv_complain_trans = (ListView) view.findViewById(R.id.lv_complain_report_fragment_complain_trans);
        tv_no_data = (TextView) view.findViewById(R.id.tv_no_data);

        complainArrayList = new ArrayList<Complain>();
        complainListAdapter = new ComplainListAdapter(getContextInstance(), complainArrayList);
        lv_complain_trans.setAdapter(complainListAdapter);
    }

    // show progress dialog
    private void showProgressDialog() {
        try {
            if (progressDialog == null) {
                progressDialog = new TransparentProgressDialog(getContextInstance(), R.drawable.fotterloading);
            }
            if (progressDialog != null) {
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
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
            if (progressDialog != null) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        } catch (Exception ex) {
            LogMessage.e("Error in dismiss progress");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    private void getComplainList() {
        // create new thread for recent transaction
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set latest_recharge url6
                    String url = URL.complainList;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "user_type",
                            "start",
                            "end",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            str_user_name,
                            str_mac_address,
                            str_otp_code,
                            "4",
                            String.valueOf(start),
                            String.valueOf(end),
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    complainHandler.obtainMessage(SUCCESS, response).sendToTarget();
                } catch (Exception ex) {
                    LogMessage.e("Error in recent transaction native method");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    //complainHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                    dismissProgressDialog();
                }
            }
        }).start();
    }


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
        alertDialog = new AlertDialog.Builder(getContextInstance()).create();
        // Setting Dialog Title
        alertDialog.setTitle("Info!");
        // set cancelable
        alertDialog.setCancelable(false);
        // Setting Dialog Message
        alertDialog.setMessage(message);
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    private void parseComplainResponse(String response) {

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            String responseStatus = jsonObject.getString("status");

            if (responseStatus.equals("1")) {
                String encrypted_response = jsonObject.getString("data");
                String decrypted_response = decryptAPI(encrypted_response);
                LogMessage.d("Response Complain List : " + decrypted_response);
                loadMoreData(decrypted_response);
            } else {
                displayComplainDialog(jsonObject.optString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

       /*Method : loadMoreData
          load data on scroll*/

    public void loadMoreData(String response) {

        try {
            JSONArray jsonArray = new JSONArray(response);

            if (jsonArray.length() < 10) {
                // lv_rec_trans.removeFooterView(footerView);
                removeFooterView();
                lv_complain_trans.addFooterView(footerViewNoMoreData);
                loadmoreFlage = true;
                // textView2.setVisibility(View.VISIBLE);
            } else {
                if (start == 0) {
                    removeFooterView();
                    lv_complain_trans.addFooterView(footerView);
                }
                loadmoreFlage = false;
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Complain complain = new Complain();
                complain.setComplain_id(object.optString("complain_id"));
                complain.setTitle(object.optString("title"));
                complain.setComplain_type(object.optString("complain_type"));
                complain.setDescription(object.optString("description"));
                complain.setReason_code(object.optString("reason_code"));
                complain.setTransaction_id(object.optString("transaction_id"));
                complain.setStatus(object.optString("status"));
                complain.setComplain_status(object.optString("complain_status"));
                complain.setRemarks(object.optString("remarks"));
                complain.setCompnay_name(object.optString("company_name"));
                complain.setCircle_name(object.optString("circle_name"));
                complain.setMo_no(object.optString("mobile"));
                complain.setAmount(object.optString("amount"));
                complain.setService_id(object.optString("service_id"));
                complain.setProduct_id(object.optString("product_id"));
                complain.setOperator_code(object.optString("operator_code"));
                complain.setCircle_code(object.optString("circle_code"));
                complain.setTrans_date_time(object.optString("trans_date_time"));
                complain.setRecharge_status(object.optString("recharge_status"));
                complain.setRecharge_id(object.optString("recharge_id"));
                complain.setReason(object.optString("reason"));
                complain.setReason_id(object.optString("reason_id"));
                complain.setTran_service_id(object.optString("tran_service_id"));

                complainArrayList.add(complain);
            }
            complainListAdapter.notifyDataSetChanged();

            removeFooterView();
            lv_complain_trans.addFooterView(footerViewNoMoreData);
            loadmoreFlage = true;
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

        lv_complain_trans.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if ((firstVisibleItem + visibleItemCount - 1) == complainArrayList.size() && !(loadmoreFlage)) {
                    loadmoreFlage = true;
                    start = start + 10;
                    end = 10;
                    // makeRecentTransaction();
                    getComplainList();
                }
            }
        });
    }

    private void removeFooterView() {
        int footerCount = lv_complain_trans.getFooterViewsCount();
        LogMessage.d("Footer Count All : " + footerCount);
        lv_complain_trans.removeFooterView(footerView);
        lv_complain_trans.removeFooterView(footerViewNoMoreData);
    }
}
