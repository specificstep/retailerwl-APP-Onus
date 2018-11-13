package specificstep.com.onus.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import specificstep.com.onus.Activities.HomeActivity;
import specificstep.com.onus.Activities.MainActivity;
import specificstep.com.onus.Adapters.SearchListAdapter;
import specificstep.com.onus.Database.DatabaseHelper;
import specificstep.com.onus.GlobalClasses.CheckConnection;
import specificstep.com.onus.GlobalClasses.Constants;
import specificstep.com.onus.GlobalClasses.MCrypt;
import specificstep.com.onus.GlobalClasses.TransparentProgressDialog;
import specificstep.com.onus.GlobalClasses.URL;
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

public class TransSearchFragment extends Fragment implements View.OnClickListener, SearchListAdapter.SubmitComplainClickListener {
    // private LogMessage log;
    private Context context;
    private final int SUCCESS = 1, ERROR = 2;
    View view;
    EditText edt_mo_no;
    Spinner sp_year, sp_month;
    Button btn_search;
    ListView lstTransactionSearch;
    LinearLayout ll_recycler_view;
    String str_mo_no, str_year, str_month, str_selected_month, str_mac_address, str_user_name, str_otp_code, str_month_year, str_reg_date_time;
    Calendar calendar;

    // set selected labels variables
    private LinearLayout llSearchSelection; //, llSearchResult;
    private TextView txtSelectedYear, txtSelectedMonth, txtSelectedMobile;

    DatabaseHelper databaseHelper;
    ArrayList<User> userArrayList;
    ArrayList<Recharge> rechargeArrayList;
    ArrayList<Recharge> beforeRefreshArrayList;
    private View footerView;
    private View footerViewNoMoreData;
    public static boolean loadmoreFlage = false;
    private int start = 0, end = 10;
    boolean FLAG_INVALID_DETAIL = false;
    int count = 0;
    SearchListAdapter searchListAdapter;
    SharedPreferences sharedPreferences;
    Constants constants;
    private TransparentProgressDialog transparentProgressDialog;
    int month, yer;
    int selected_year;
    int current_month, current_year;
    ArrayList<String> year_array;
    List<String> month_list;
    String month_array[];

    TextView textView2;

    private AlertDialog alertDialog_1;

    private HomeActivity mainActivity() {
        return ((HomeActivity) getActivity());
    }

    private Context getContextInstance() {
        if (context == null) {
            context = TransSearchFragment.this.getActivity();
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
        view = inflater.inflate(R.layout.fragment_trans_search, null);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        context = TransSearchFragment.this.getActivity();
        // log = new LogMessage(TransSearchFragment.class.getSimpleName());
        calendar = Calendar.getInstance();
        constants = new Constants();
        sharedPreferences = getActivity().getSharedPreferences(constants.SHAREEDPREFERENCE, Context.MODE_PRIVATE);
        transparentProgressDialog = new TransparentProgressDialog(getContextInstance(), R.drawable.fotterloading);
        databaseHelper = new DatabaseHelper(getContextInstance());
        userArrayList = new ArrayList<User>();
        rechargeArrayList = new ArrayList<Recharge>();
        beforeRefreshArrayList = new ArrayList<Recharge>();
        userArrayList = databaseHelper.getUserDetail();
        str_mac_address = userArrayList.get(0).getDevice_id();
        str_user_name = userArrayList.get(0).getUser_name();
        str_otp_code = userArrayList.get(0).getOtp_code();
        str_reg_date_time = userArrayList.get(0).getReg_date();

        textView2 = (TextView) view.findViewById(R.id.textView2);


/*get year   and month from registered date*/
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (str_reg_date_time != null) {
                Date d = sdf.parse(str_reg_date_time);
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                month = (cal.get(Calendar.MONTH) + 1);
                yer = (cal.get(Calendar.YEAR));
                LogMessage.d("Year : " + String.valueOf(yer));
            } else {
                Utility.toast(getContextInstance(), "Error");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

//        mainActivity().setNotificationCounter();

        init();
        return view;
    }

    private void init() {
        edt_mo_no = (EditText) view.findViewById(R.id.edt_mo_no_fragment_trans_search);

        sp_year = (Spinner) view.findViewById(R.id.sp_year_fragment_trans_search);
        sp_month = (Spinner) view.findViewById(R.id.sp_month_fragment_trans_search);
        lstTransactionSearch = (ListView) view.findViewById(R.id.lv_trans_search_fragment_trans_search);
        btn_search = (Button) view.findViewById(R.id.btn_search_fragment_trans_search);
        ll_recycler_view = (LinearLayout) view.findViewById(R.id.ll_recycler_view);
        footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.load_more_items, null);
        footerViewNoMoreData = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_no_moredata, null);

        // [START] - set selected labels
        llSearchSelection = (LinearLayout) view.findViewById(R.id.ll_TrasactionSearch_SearchSelection);
//        llSearchResult = (LinearLayout)view.findViewById(R.id.ll_TrasactionSearch_SearchResult);
        txtSelectedMobile = (TextView) view.findViewById(R.id.txt_TrasactionSearch_SelectedMobileNo);
        txtSelectedMonth = (TextView) view.findViewById(R.id.txt_TrasactionSearch_SelectedMonth);
        txtSelectedYear = (TextView) view.findViewById(R.id.txt_TrasactionSearch_SelectedYear);
//        llSearchResult.setVisibility(View.GONE);
        llSearchSelection.setVisibility(View.VISIBLE);
        // [END]

        btn_search.setOnClickListener(this);

        year_array = new ArrayList<String>();
        month_list = new ArrayList<String>();
        month_array = getActivity().getResources().getStringArray(R.array.month_array);

        current_month = calendar.get(Calendar.MONTH) + 1;

        current_year = calendar.get(Calendar.YEAR);
        /*add years from registered year to current year*/
        if (yer != 0) {
            for (int i = yer; i <= current_year; i++) {
                year_array.add(String.valueOf(i));

            }
        } else {
            year_array.add(String.valueOf(current_year));
        }
        int current_yr_position = year_array.indexOf(String.valueOf(current_year));

        ArrayAdapter yearAdapter = new ArrayAdapter(getContextInstance(), R.layout.spinner_item, year_array);
        sp_year.setAdapter(yearAdapter);
        sp_year.setSelection(current_yr_position);
        selected_year = current_year;

//        for (int i = 0; i < current_month; i++) {
//            month_list.add(month_array[i]);
//        }
        ArrayAdapter monthAdapter = new ArrayAdapter(getContextInstance(), R.layout.spinner_item, month_list);
        sp_month.setAdapter(monthAdapter);
        sp_month.setSelection(current_month - 1);

        /*set months from registered date to
        current date according to selected year*/
        sp_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#ffffff"));
                str_year = sp_year.getSelectedItem().toString();
                selected_year = Integer.parseInt(str_year);

                if (selected_year == current_year) {
                    month_list.clear();
                    for (int i = 0; i < current_month; i++) {
                        month_list.add(month_array[i]);
                    }
                    ArrayAdapter monthAdapter = new ArrayAdapter(getContextInstance(), R.layout.spinner_item, month_list);
                    sp_month.setAdapter(monthAdapter);
                    sp_month.setSelection(current_month - 1);

                } else {
                    month_list.clear();
                    for (int i = month - 1; i <= 11; i++) {
                        month_list.add(month_array[i]);
                    }
                    ArrayAdapter monthAdapter = new ArrayAdapter(getContextInstance(), R.layout.spinner_item, month_list);
                    sp_month.setAdapter(monthAdapter);
                    sp_month.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) sp_month.getSelectedView()).setTextColor(Color.parseColor("#ffffff"));
                int selected_month_position = Arrays.asList(month_array).indexOf(sp_month.getSelectedItem().toString());
                str_month = String.valueOf(selected_month_position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        str_mo_no = edt_mo_no.getText().toString();

        if (str_month.length() != 2) {
            str_selected_month = "0" + str_month;
            str_month_year = str_year + "-" + str_selected_month;
        } else {
            str_selected_month = str_month;
            str_month_year = str_year + "-" + str_selected_month;
        }
        if (str_mo_no.isEmpty()) {
            Utility.toast(getContextInstance(), "Enter " + getResources().getString(R.string.str_mo_no));
        } else if (str_mo_no.length() < 4) {
            Utility.toast(getContextInstance(), "Invalid Mobile number");
        } else if (str_selected_month.contains("Select Month")) {
            Utility.toast(getContextInstance(), "Select Month");
        } else if (str_year.isEmpty()) {
            Utility.toast(getContextInstance(), "Select year");
        } else {
            rechargeArrayList.clear();
            start = 0;
            end = 10;
            showProgressDialog();
            textView2.setVisibility(View.GONE);

            makeNativeTransactionSearch();

            searchListAdapter = new SearchListAdapter(getContextInstance(), rechargeArrayList,this);
            lstTransactionSearch.setAdapter(searchListAdapter);
        }
    }

    /* [START] - 2017_04_28 - Add native code for transaction search, and Remove volley code */
    private void makeNativeTransactionSearch() {
        // create new thread for recent transaction
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set search_recharge url
                    String url = URL.search_recharge;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "mobile",
                            "start",
                            "end",
                            "mon_year",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            str_user_name,
                            str_mac_address,
                            str_otp_code,
                            str_mo_no,
                            start + "",
                            end + "",
                            str_month_year,
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    myHandler.obtainMessage(SUCCESS, response).sendToTarget();
                }
                catch (Exception ex) {
                    LogMessage.e("Error in transaction search native method");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    // parse success response
    private void parseSuccessResponse(String response) {
        LogMessage.i("Trans Search Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                ll_recycler_view.setVisibility(View.VISIBLE);
                String encrypted_response = jsonObject.getString("data");
                String decrypted_response = decryptAPI(encrypted_response);
                LogMessage.i("Trans Search DECR : " + decrypted_response);
                loadMoreData(decrypted_response);

            } else if (jsonObject.getString("status").equals("2")
                    && jsonObject.getString("msg").equalsIgnoreCase("Invalid Details")) {
                // lstTransactionSearch.removeFooterView(footerView);
                removeFooterView();
                lstTransactionSearch.addFooterView(footerViewNoMoreData);

                loadmoreFlage = true;
                FLAG_INVALID_DETAIL = true;
                count++;
                AlertDialog alertDialog = new AlertDialog.Builder(getContextInstance()).create();
                alertDialog.setTitle("Info!");
                alertDialog.setCancelable(false);
                alertDialog.setMessage(jsonObject.getString("msg"));
                alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.show();

//                if (count >= 3) {
//                    Toast.makeText(getActivity(), "You have attempted more than 3 times", Toast.LENGTH_SHORT).show();
//                    Intent i = new Intent(getActivity(), RegistrationActivity.class);
//                    startActivity(i);
//                }
            } else {
                // lstTransactionSearch.removeFooterView(footerView);
                removeFooterView();
                lstTransactionSearch.addFooterView(footerViewNoMoreData);
                if (start == 0) {
                    new AlertDialog.Builder(getContextInstance())
                            .setTitle("No Recharge found")
                            .setCancelable(false)
                            .setMessage(jsonObject.getString("msg"))
                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                } else {
                    // textView2.setVisibility(View.VISIBLE);
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            Utility.toast(getContextInstance(), "No result found");
        }
    }

    private AlertDialog alertDialog;

    // display error in dialog
    private void displayErrorDialog(String message) {
        /* [START] - 2017_05_01 - Close all alert dialog logic */
        try {
            alertDialog = new AlertDialog.Builder(getContextInstance()).create();
            alertDialog.setTitle("Info!");
            alertDialog.setCancelable(false);
            alertDialog.setMessage(message);
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
        catch (Exception ex) {
            LogMessage.e("Error in error dialog");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
            try {
                Utility.toast(getContextInstance(), message);
            }
            catch (Exception e) {
                LogMessage.e("Error in toast message");
                LogMessage.e("ERROR : " + e.getMessage());
            }
        }
        // [END]
    }

    // handle thread messages
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == SUCCESS) {
                dismissProgressDialog();
                parseSuccessResponse(msg.obj.toString());
            } else if (msg.what == ERROR) {
                dismissProgressDialog();
                displayErrorDialog(msg.obj.toString());
            }
        }
    };
    // [END]

    /*Method : makeSearchTransaction
          webservice for search transaction*/
//    private void makeSearchTransaction() {
//    }

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
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return decrypted_response;
    }

    private void removeFooterView() {
        int footerCount = lstTransactionSearch.getFooterViewsCount();
        LogMessage.d("Footer Count All : " + footerCount);
        lstTransactionSearch.removeFooterView(footerView);
        lstTransactionSearch.removeFooterView(footerViewNoMoreData);
    }

    /*Method : loadMoreData
             load data on scroll*/
    public void loadMoreData(String response) {
        try {
            JSONObject jsonObject1 = new JSONObject(response);
            JSONArray jsonArray = jsonObject1.getJSONArray("search");

            if (jsonArray.length() < 10) {
                // lstTransactionSearch.removeFooterView(footerView);
                removeFooterView();
                lstTransactionSearch.addFooterView(footerViewNoMoreData);
                loadmoreFlage = true;
                // textView2.setVisibility(View.VISIBLE);
            } else {
                if (start == 0) {
                    removeFooterView();
                    lstTransactionSearch.addFooterView(footerView);
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
                beforeRefreshArrayList.add(recharge);
            }
            searchListAdapter.notifyDataSetChanged();

            // [START] - set selected labels
            txtSelectedMonth.setText("Month : " + sp_month.getSelectedItem().toString());
            txtSelectedYear.setText("Year : " + str_year);
            txtSelectedMobile.setText("Number : " + str_mo_no);
//            llSearchResult.setVisibility(View.VISIBLE);
//            llSearchSelection.setVisibility(View.GONE);
//            btn_search.setVisibility(View.GONE);
            // [END]

        }
        catch (JSONException e) {
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
        lstTransactionSearch.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if ((firstVisibleItem + visibleItemCount - 1) == rechargeArrayList.size() && !(loadmoreFlage)) {
                    loadmoreFlage = true;
                    start = start + 10;
                    end = 10;
                    makeNativeTransactionSearch();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

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
}
