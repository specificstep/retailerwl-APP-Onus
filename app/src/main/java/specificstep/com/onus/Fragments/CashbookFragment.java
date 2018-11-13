package specificstep.com.onus.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import specificstep.com.onus.Activities.HomeActivity;
import specificstep.com.onus.Activities.MainActivity;
import specificstep.com.onus.Adapters.CashbookAdapter;
import specificstep.com.onus.Database.DatabaseHelper;
import specificstep.com.onus.GlobalClasses.Constants;
import specificstep.com.onus.GlobalClasses.MCrypt;
import specificstep.com.onus.GlobalClasses.TransparentProgressDialog;
import specificstep.com.onus.GlobalClasses.URL;
import specificstep.com.onus.Models.CashbookModel;
import specificstep.com.onus.Models.DateTime;
import specificstep.com.onus.Models.Default;
import specificstep.com.onus.Models.User;
import specificstep.com.onus.R;
import specificstep.com.onus.utility.InternetUtil;
import specificstep.com.onus.utility.LogMessage;
import specificstep.com.onus.utility.Utility;

/**
 * Created by ubuntu on 16/3/17.
 */
// extends DialogFragment
public class CashbookFragment extends Fragment implements View.OnClickListener {

    private final int SUCCESS = 1, ERROR = 2;
    /* [START] - All View objects */
    // View class object for display fragment view
    private View view;
    // Load more data view
    private View footerView;
    private View footerViewNoMoreData;
    // [END]

    /* [START] - Other class objects */
    private Context context;
    // Custom log message class
    // private LogMessage log;
    private SharedPreferences sharedPreferences;
    // All static variables class
    private Constants constants;
    // Database class
    private DatabaseHelper databaseHelper;
    private CashbookAdapter cashbookAdapter;
    private TransparentProgressDialog transparentProgressDialog;
    // private Calendar myCalendar;
    private Calendar fromCalendar, toCalendar;
    private SimpleDateFormat simpleDateFormat;
    // [END]

    /* [START] - Controls objects */
    private EditText edtFromDate, edtToDate;
    private EditText edtFromDateTest, edtToDateTest;
    private Button btnSearch;
    private TextView txtNoMoreData;
    private ListView lstCashbookSearch;
    private LinearLayout ll_recycler_view;
    private DatePicker dpResult;
    // [END]

    /* [START] - Variables */
    private static boolean loadMoreFlage = false;
    boolean FLAG_INVALID_DETAIL = false;
    // Counter for 3 times invalid details
    private int count = 0;
    // use for get cashbook data from and to limit
    private int start = 0, end = 10;
    // use for set date time picker
    private int year, month, day;
    private String strMacAddress, strUserName, strOtpCode, strRegistrationDateTime;
    private ArrayList<User> userArrayList;
    private ArrayList<CashbookModel> cashbookModels;
    private ArrayList<CashbookModel> beforeCashbookModels;
    // [END]

    private HomeActivity mainActivity() {
        return ((HomeActivity) getActivity());
    }

    private Context getContextInstance() {
        if (context == null) {
            context = CashbookFragment.this.getActivity();
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
        view = inflater.inflate(R.layout.fragment_cashbook, null);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        context = CashbookFragment.this.getActivity();

        initControls();
        setListener();

        setCurrentDateOnView();
        formDatePicker();
        toDatePicker();

        loadDefaultCashbook();
        return view;
    }

    private void initControls() {
        /* [START] - Initialise class objects */

        // log = new LogMessage(CashbookFragment.class.getSimpleName());
        constants = new Constants();
        databaseHelper = new DatabaseHelper(getContextInstance());
        sharedPreferences = getActivity().getSharedPreferences(constants.SHAREEDPREFERENCE, Context.MODE_PRIVATE);
        transparentProgressDialog = new TransparentProgressDialog(getContextInstance(), R.drawable.fotterloading);
        cashbookModels = new ArrayList<CashbookModel>();
        beforeCashbookModels = new ArrayList<CashbookModel>();
        fromCalendar = Calendar.getInstance();
        toCalendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        // [END]

        /* [START] - get user data from database and store into string variables */
        userArrayList = databaseHelper.getUserDetail();
        // Store user information in variables
        strMacAddress = userArrayList.get(0).getDevice_id();
        strUserName = userArrayList.get(0).getUser_name();
        strOtpCode = userArrayList.get(0).getOtp_code();
        strRegistrationDateTime = userArrayList.get(0).getReg_date();
        // [END]

        /* [START] - Initialise control objects */
        // All TextView
        txtNoMoreData = (TextView) view.findViewById(R.id.txt_NoMoreData);
        // All Button
        btnSearch = (Button) view.findViewById(R.id.btn_search_CashBook);
        // All EditText
        edtFromDate = (EditText) view.findViewById(R.id.from_date_CashBook);
        edtFromDateTest = (EditText) view.findViewById(R.id.from_date_CashBook_Test);
        edtToDate = (EditText) view.findViewById(R.id.to_date_CashBook);
        edtToDateTest = (EditText) view.findViewById(R.id.to_date_CashBook_Test);
        // ListView
        lstCashbookSearch = (ListView) view.findViewById(R.id.lv_trans_search_CashBook);
        // LinearLayout
        ll_recycler_view = (LinearLayout) view.findViewById(R.id.ll_recycler_view_CashBook);
        // Load more data view
        footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.load_more_items, null);
        footerViewNoMoreData = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_no_moredata, null);
        // [END]
    }

    // Load current cashbook
    private void loadDefaultCashbook() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date from_date = sdf.parse(edtFromDate.getText().toString());
            Date to_date = sdf.parse(edtToDate.getText().toString());

            if (from_date.getMonth() == to_date.getMonth()) {
                cashbookModels.clear();
                start = 0;
                end = 10;
                showProgressDialog();
                txtNoMoreData.setVisibility(View.GONE);

                makeNativeCashBook();

                cashbookAdapter = new CashbookAdapter(getContextInstance(), cashbookModels);
                lstCashbookSearch.setAdapter(cashbookAdapter);
            } else {
                Utility.toast(getContextInstance(), "Please select dates of same month.");
            }
        }
        catch (Exception ex) {
            LogMessage.d("Cashbook : " + "Error 3 : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void setListener() {
        // On click listener
        btnSearch.setOnClickListener(this);
        edtFromDateTest.setOnClickListener(this);
        edtToDateTest.setOnClickListener(this);
    }

    /**
     * Set from date and it's click listener
     */
    private void formDatePicker() {
        final DatePickerDialog.OnDateSetListener fromDatePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                try {
                    fromCalendar.set(Calendar.YEAR, year);
                    fromCalendar.set(Calendar.MONTH, monthOfYear);
                    fromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateFromLabel(edtFromDate);

                    /* [START] - 2017_04_18 - set to date selection validation and update to date label */
                    try {
                        // set default year in to_date_picker as per from_date_picker
                        toCalendar.set(Calendar.YEAR, year);
                        // set default month in to_date_picker as per from_date_picker
                        toCalendar.set(Calendar.MONTH, monthOfYear);
                        // get last date from from_date_picker selected month
                        int lastDayOfMonth = fromCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        // get current date for validate to_date_picker max date
                        String currentFullDate = DateTime.getDate();
                        // get current date from full date
                        String currentDate = DateTime.getDayFromFullDate(currentFullDate);
                        // get current month from full date
                        String currentMonth = DateTime.getMonthFromFullDate(currentFullDate);
                        // Check current month and from_date_picker month are same or not.
                        if (Integer.parseInt(currentMonth) == monthOfYear + 1) {
                            // if current month and from_date_picker month are same, check current date and last day of month
                            if (Integer.parseInt(currentDate) < lastDayOfMonth) {
                                // if current date is less then last day of month then set current date in to calender
                                toCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(currentDate));
                            } else {
                                // else set last day of month in to calender
                                toCalendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
                            }
                        } else {
                            // else set last day of month in to calender
                            toCalendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
                        }
                        updateToLabel(edtToDate);
                    }
                    catch (Exception ex) {
                        LogMessage.e("Error in set to date");
                        LogMessage.e("Error : " + ex.getMessage());
                        ex.printStackTrace();
                    }
                    // [END]
                }
                catch (Exception ex) {
                    LogMessage.d("Cashbook : " + "Error 1 : " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        };

        edtFromDate.setText(simpleDateFormat.format(fromCalendar.getTime()));
        edtFromDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                try {
                    long timeInMilliseconds = new Date().getTime();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                            fromDatePicker,
                            fromCalendar.get(Calendar.YEAR),
                            fromCalendar.get(Calendar.MONTH),
                            fromCalendar.get(Calendar.DAY_OF_MONTH));
                    // datePickerDialog.getDatePicker().setMaxDate(max_TimeInMilliseconds);
                    datePickerDialog.getDatePicker().setMaxDate(timeInMilliseconds);
                    datePickerDialog.show();
                }
                catch (Exception e) {
                    LogMessage.d("Cashbook : " + "Error 1 : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Set to date and it's click listener
     */
    private void toDatePicker() {
        final DatePickerDialog.OnDateSetListener toDatePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                toCalendar.set(Calendar.YEAR, year);
                toCalendar.set(Calendar.MONTH, monthOfYear);
                toCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateToLabel(edtToDate);
            }
        };

        edtToDate.setText(simpleDateFormat.format(toCalendar.getTime()));
        edtToDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                try {
                    Calendar calendar = Calendar.getInstance();
                    Date mDate = sdf.parse(edtFromDate.getText().toString());
                    long min_TimeInMilliseconds = mDate.getTime();
                    long max_Time = calendar.getTimeInMillis();
                    long max_TimeInMilliseconds = 0;

                    /* [START] - 2017_04_19 - set to date selection validation and update to date label */
                    try {
                        // set default year in to_date_picker as per from_date_picker
                        calendar.set(Calendar.YEAR, year);
                        // set default month in to_date_picker as per from_date_picker
                        calendar.set(Calendar.MONTH, mDate.getMonth());
                        int monthOfYear = mDate.getMonth();
                        // get last date from from_date_picker selected month
                        int lastDayOfMonth = fromCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        // get current date for validate to_date_picker max date
                        String currentFullDate = DateTime.getDate();
                        // get current date from full date
                        String currentDate = DateTime.getDayFromFullDate(currentFullDate);
                        // get current month from full date
                        String currentMonth = DateTime.getMonthFromFullDate(currentFullDate);
                        // Check current month and from_date_picker month are same or not.
                        if (Integer.parseInt(currentMonth) == monthOfYear + 1) {
                            // if current month and from_date_picker month are same, check current date and last day of month
                            if (Integer.parseInt(currentDate) < lastDayOfMonth) {
                                // if current date is less then last day of month then set current date in to calender
                                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(currentDate));
                            } else {
                                // else set last day of month in to calender
                                calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
                            }
                        } else {
                            // else set last day of month in to calender
                            calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
                        }
                        max_TimeInMilliseconds = calendar.getTimeInMillis();
                    }
                    catch (Exception ex) {
                        LogMessage.e("Error in set to date");
                        LogMessage.e("Error : " + ex.getMessage());
                        ex.printStackTrace();
                        max_TimeInMilliseconds = max_Time;
                    }
                    // [END]

                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                            toDatePicker,
                            toCalendar.get(Calendar.YEAR),
                            toCalendar.get(Calendar.MONTH),
                            toCalendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.getDatePicker().setMinDate(min_TimeInMilliseconds);
                    datePickerDialog.getDatePicker().setMaxDate(max_TimeInMilliseconds);
                    // datePickerDialog.updateDate(2017, 03, 03);
                    datePickerDialog.show();
                }
                catch (Exception e) {
                    LogMessage.d("Cashbook : " + "Error 2 : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateFromLabel(EditText editText) {
        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(sdf.format(fromCalendar.getTime()));
    }

    private void updateToLabel(EditText editText) {
        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(sdf.format(toCalendar.getTime()));
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
        if (v == edtFromDateTest) {

        } else if (v == edtToDateTest) {

        } else if (v == btnSearch) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date from_date = sdf.parse(edtFromDate.getText().toString());
                Date to_date = sdf.parse(edtToDate.getText().toString());

                if (from_date.getMonth() == to_date.getMonth()) {
                    cashbookModels.clear();
                    start = 0;
                    end = 10;
                    showProgressDialog();
                    txtNoMoreData.setVisibility(View.GONE);

                    makeNativeCashBook();

                    cashbookAdapter = new CashbookAdapter(getContextInstance(), cashbookModels);
                    lstCashbookSearch.setAdapter(cashbookAdapter);
                } else {
                    Utility.toast(getContextInstance(), "Please select dates of same month.");
                }
            }
            catch (Exception ex) {
                LogMessage.d("Cashbook : " + "Error 3 : " + ex.getMessage());
                ex.printStackTrace();
            }
        }

    }

    // display current date
    public void setCurrentDateOnView() {
        dpResult = (DatePicker) view.findViewById(R.id.dpResult);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // set current date into text view
        edtFromDateTest.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("-").append(day).append("-")
                .append(year).append(" "));

        // set current date into date picker
        dpResult.init(year, month, day, null);
    }

    /* [START] - 2017_04_28 - Add native code for cash book, and Remove volley code */
    private void makeNativeCashBook() {
        // create new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                    DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date from_date = sdf.parse(edtFromDate.getText().toString());
                    Date to_date = sdf.parse(edtToDate.getText().toString());

                    // set cashBook url
                    String url = URL.cashBook;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "start_limit",
                            "end_limit",
                            "user_type",
                            "from",
                            "to",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            strUserName,
                            strMacAddress,
                            strOtpCode,
                            start + "",
                            end + "",
                            "4",
                            outputFormat.format(from_date),
                            outputFormat.format(to_date),
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    myHandler.obtainMessage(SUCCESS, response).sendToTarget();
                }
                catch (Exception ex) {
                    LogMessage.e("Error in Cash book native method");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    // parse success response
    private void parseSuccessResponse(String response) {
        LogMessage.i("CashBook Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                ll_recycler_view.setVisibility(View.VISIBLE);
                String encrypted_response = jsonObject.getString("data");
                String message = jsonObject.getString("msg");
                LogMessage.d("CashBook : " + "Message : " + message);
                LogMessage.d("CashBook : " + "encrypted_response : " + encrypted_response);
                String decrypted_response = decryptAPI(encrypted_response);
                LogMessage.d("CashBook : " + "decrypted_response : " + decrypted_response);
                loadMoreData(decrypted_response);
            } else if (jsonObject.getString("status").equals("2") &&
                    jsonObject.getString("message").equalsIgnoreCase("Invalid Details")) {
                int footerCount = lstCashbookSearch.getFooterViewsCount();
                LogMessage.d("Footer Count 1 : " + footerCount);
                // lstCashbookSearch.removeFooterView(footerView);
                removeFooterView();
                lstCashbookSearch.addFooterView(footerViewNoMoreData);

                loadMoreFlage = true;
                FLAG_INVALID_DETAIL = true;
                count++;
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Info!");
                alertDialog.setCancelable(false);
                alertDialog.setMessage(jsonObject.getString("message"));
                alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.show();
            } else {
                int footerCount = lstCashbookSearch.getFooterViewsCount();
                LogMessage.d("Footer Count 2 : " + footerCount);
                // lstCashbookSearch.removeFooterView(footerView);
                removeFooterView();
                lstCashbookSearch.addFooterView(footerViewNoMoreData);
                if (start == 0) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("No cash book found")
                            .setCancelable(false)
                            .setMessage(jsonObject.getString("message"))
                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                } else {
                    // txtNoMoreData.setVisibility(View.VISIBLE);
                    txtNoMoreData.setVisibility(View.GONE);
                }
            }
        }
        catch (JSONException e) {
            LogMessage.d("Cashbook : " + "Error 4 : " + e.getMessage());
            Utility.toast(getContextInstance(), "No result found");
            e.printStackTrace();
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

    /*Method : decryptAPI
           Decrypt response of webservice*/
    public String decryptAPI(String response) {
        ArrayList<Default> defaultArrayList;
        defaultArrayList = databaseHelper.getDefaultSettings();
        String user_id = defaultArrayList.get(0).getUser_id();
        MCrypt mCrypt = new MCrypt(user_id, strMacAddress);
        String decrypted_response = null;
        byte[] decrypted_bytes = Base64.decode(response, Base64.DEFAULT);
        try {
            decrypted_response = new String(mCrypt.decrypt(mCrypt.bytesToHex(decrypted_bytes)), "UTF-8");
        }
        catch (Exception e) {
            LogMessage.d("Cashbook : " + "Error 7 : " + e.getMessage());
            e.printStackTrace();
        }
        return decrypted_response;
    }

    private void removeFooterView() {
        int footerCount = lstCashbookSearch.getFooterViewsCount();
        LogMessage.d("Footer Count All : " + footerCount);
        lstCashbookSearch.removeFooterView(footerView);
        lstCashbookSearch.removeFooterView(footerViewNoMoreData);
    }

    /*Method : loadMoreData
               load data on scroll*/
    public void loadMoreData(String response) {
        try {
            JSONObject jsonObject1 = new JSONObject(response);
            JSONArray jsonArray = jsonObject1.getJSONArray("data");

            if (jsonArray.length() < 10) {

                removeFooterView();

                lstCashbookSearch.addFooterView(footerViewNoMoreData);

                loadMoreFlage = true;
                // txtNoMoreData.setVisibility(View.VISIBLE);
                if (jsonArray.length() == 0) {
                    Utility.toast(getContextInstance(), "No more cashbook data.");
                }
            } else {
                if (start == 0) {
                    int footerCount = lstCashbookSearch.getFooterViewsCount();
                    LogMessage.d("Footer Count 4 : " + footerCount);
                    removeFooterView();
                    lstCashbookSearch.addFooterView(footerView);
                }
                loadMoreFlage = false;
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                CashbookModel cashbookModel = new CashbookModel();
                cashbookModel.paymentId = object.getString("Payment Id");
                cashbookModel.amount = object.getString("Amount");
                /* [START] - Remove Balance field */
                // cashbookModel.balance = object.getString("Balance");
                cashbookModel.balance = "";
                // [END]
                cashbookModel.dateTime = object.getString("Datetime");
                cashbookModel.paymentFrom = object.getString("Payment From");
                cashbookModel.paymentTo = object.getString("Payment To");
                cashbookModel.remarks = object.getString("Remarks");
                cashbookModel.userType = object.getString("UserType");
                cashbookModels.add(cashbookModel);
                beforeCashbookModels.add(cashbookModel);
            }
            cashbookAdapter.notifyDataSetChanged();
        }
        catch (JSONException e) {
            e.printStackTrace();
            LogMessage.d("Cashbook : " + "Error 8 : " + e.toString());
            new AlertDialog.Builder(getActivity())
                    .setTitle("Alert!")
                    .setCancelable(false)
                    .setMessage(getResources().getString(R.string.alert_servicer_down))
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        }
        lstCashbookSearch.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if ((firstVisibleItem + visibleItemCount - 1) == cashbookModels.size() && !(loadMoreFlage)) {
                    loadMoreFlage = true;
                    start = start + 10;
                    end = 10;
                    makeNativeCashBook();
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
                    Intent intent = new Intent(getActivity(), MainActivity.class);
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
}
