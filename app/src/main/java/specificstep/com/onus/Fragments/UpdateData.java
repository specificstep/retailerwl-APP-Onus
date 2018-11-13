package specificstep.com.onus.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import specificstep.com.onus.Activities.HomeActivity;
import specificstep.com.onus.Activities.LoginActivity;
import specificstep.com.onus.Activities.MainActivity;
import specificstep.com.onus.Database.DatabaseHelper;
import specificstep.com.onus.GlobalClasses.Constants;
import specificstep.com.onus.GlobalClasses.MCrypt;
import specificstep.com.onus.GlobalClasses.URL;
import specificstep.com.onus.Models.Color;
import specificstep.com.onus.Models.Company;
import specificstep.com.onus.Models.DateTime;
import specificstep.com.onus.Models.Default;
import specificstep.com.onus.Models.Product;
import specificstep.com.onus.Models.State;
import specificstep.com.onus.Models.User;
import specificstep.com.onus.R;
import specificstep.com.onus.utility.InternetUtil;
import specificstep.com.onus.utility.LogMessage;
import specificstep.com.onus.utility.MyPrefs;
import specificstep.com.onus.utility.NotificationUtil;
import specificstep.com.onus.utility.Utility;

/**
 * Created by ubuntu on 9/1/17.
 */

public class UpdateData extends Fragment {

    private View view;

    /* Other class objects */
    private SharedPreferences sharedPreferences;
    private Constants constants;
    private ProgressBar progressBar;
    private DatabaseHelper databaseHelper;
    private MyPrefs prefs;
    private Context context;
    private AlertDialog alertDialog, alertDialog_Logout;

    /* All local int and string variables */
    private final int SUCCESS_MOBILE_COMPANY = 1, ERROR = 2, SUCCESS_DTH_COMPANY = 3,
            SUCCESS_MOBILE_PRODUCT = 4, SUCCESS_DTH_PRODUCT = 5, SUCCESS_STATE = 6, SUCCESS_SETTING = 7,
            ERROR_INVALID_DETAILS = 8;
    private String requireUpdate = "0";

    /* All ArrayList */
    private ArrayList<User> userArrayList;
    private ArrayList<Product> productArrayList;
    private ArrayList<State> stateArrayList;
    private ArrayList<Company> companyArrayList;

    /* All views */
    private TextView txtLastUpdate, textView;
    private Button updateButton, homeButton;

    private Context getContextInstance() {
        if (context == null) {
            context = UpdateData.this.getActivity();
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
        view = inflater.inflate(R.layout.fragment_update_data, null);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        context = UpdateData.this.getActivity();
        databaseHelper = new DatabaseHelper(getContextInstance());
        constants = new Constants();
        sharedPreferences = getContextInstance().getSharedPreferences(constants.SHAREEDPREFERENCE, Context.MODE_PRIVATE);
        prefs = new MyPrefs(getContextInstance(), constants.PREF_NAME);

        userArrayList = databaseHelper.getUserDetail();

        productArrayList = new ArrayList<Product>();
        stateArrayList = new ArrayList<State>();
        companyArrayList = new ArrayList<Company>();

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setMax(100);

        textView = (TextView) view.findViewById(R.id.textView);

        updateButton = (Button) view.findViewById(R.id.btn_update_data);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
            }
        });
        homeButton = (Button) view.findViewById(R.id.btn_return_home);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                Intent intent = new Intent(getContextInstance(), MainActivity.class);
                startActivity(intent);
            }
        });

        txtLastUpdate = (TextView) view.findViewById(R.id.txt_Update_LastUpdateDate);
        String updateDate = prefs.retriveString(constants.PREF_UPDATE_DATE, "0");
        String updateTime = prefs.retriveString(constants.PREF_UPDATE_TIME, "0");
        if (TextUtils.equals(updateDate, "0")) {
            txtLastUpdate.setVisibility(View.GONE);
        } else {
            txtLastUpdate.setVisibility(View.VISIBLE);
            txtLastUpdate.setText("Last update : " + updateDate + " " + updateTime);
        }

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getString(constants.KEY_REQUIRE_UPDATE) != null
                    && !bundle.getString(constants.KEY_REQUIRE_UPDATE).equals("")) {
                requireUpdate = bundle.getString(constants.KEY_REQUIRE_UPDATE);
                if (TextUtils.equals(requireUpdate, "1")) {
                    updateData();
                }
            }
        }

        return view;
    }

    private void updateData() {
        updateButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
        txtLastUpdate.setVisibility(View.GONE);
        progressBar.setProgress(0);
        databaseHelper.truncateUpdateData();
        makeMobileCompany();
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

    /* [START] - 2017_04_28 - Add native code for update data, and Remove volley code */
    private void makeMobileCompany() {
        textView.setText("Updating\n\n1/5) Mobile Companies (Processing)");
        // create new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.company;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "service",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            userArrayList.get(0).getUser_name(),
                            userArrayList.get(0).getDevice_id(),
                            userArrayList.get(0).getOtp_code(),
                            "1",
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    myHandler.obtainMessage(SUCCESS_MOBILE_COMPANY, response).sendToTarget();
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
    private void parseMobileCompanyResponse(String response) {
        LogMessage.i("Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String encrypted_string = jsonObject.getString("data");
                String decryptedResponse = decryptAPI(encrypted_string);
                LogMessage.d("Mobile company 1 : " + decryptedResponse);
                JSONObject jsonObject1 = new JSONObject(decryptAPI(encrypted_string));
                JSONArray jsonArray = jsonObject1.getJSONArray("company");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    Company company = new Company();
                    company.setId(jsonObject2.getString("id"));
                    company.setCompany_name(jsonObject2.getString("company_name"));
                    company.setLogo(jsonObject2.getString("logo"));
                    company.setService_type("Mobile");
                    companyArrayList.add(company);
                }
                databaseHelper.deleteCompanyDetail("Mobile");
                databaseHelper.addCompanysDetails(companyArrayList);

                progressBar.setProgress(20);
                // get dth company data
                makeDTHCompany();
            } else if (jsonObject.getString("status").equals("2") &&
                    TextUtils.equals(jsonObject.getString("msg").toLowerCase().trim(), "invalid details")) {
                myHandler.obtainMessage(ERROR_INVALID_DETAILS, jsonObject.getString("msg")).sendToTarget();
            } else {
                myHandler.obtainMessage(ERROR, jsonObject.getString("msg")).sendToTarget();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            myHandler.obtainMessage(ERROR, "Mobile data not found").sendToTarget();
        }
    }

    // DTH company
    private void makeDTHCompany() {
        textView.setText("Updating\n\n1/5) Mobile Companies (Done)\n2/5) DTH Companies (Processing)");
        // create new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.company;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "service",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            userArrayList.get(0).getUser_name(),
                            userArrayList.get(0).getDevice_id(),
                            userArrayList.get(0).getOtp_code(),
                            "2",
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    myHandler.obtainMessage(SUCCESS_DTH_COMPANY, response).sendToTarget();
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
    private void parseDTHCompanyResponse(String response) {
        LogMessage.i("DTH company Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String encrypted_string = jsonObject.getString("data");
                String decryptedResponse = decryptAPI(encrypted_string);
                LogMessage.d("Mobile company 2 : " + decryptedResponse);
                companyArrayList.clear();
                JSONObject jsonObject1 = new JSONObject(decryptAPI(encrypted_string));
                JSONArray jsonArray = jsonObject1.getJSONArray("company");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    Company company = new Company();
                    company.setId(jsonObject2.getString("id"));
                    company.setCompany_name(jsonObject2.getString("company_name"));
                    company.setLogo(jsonObject2.getString("logo"));
                    company.setService_type("DTH");
                    companyArrayList.add(company);
                }
                databaseHelper.deleteCompanyDetail("DTH");
                databaseHelper.addCompanysDetails(companyArrayList);
                progressBar.setProgress(40);
                // get mobile product
                makeMobileProduct();
            } else if (jsonObject.getString("status").equals("2") &&
                    TextUtils.equals(jsonObject.getString("msg").toLowerCase().trim(), "invalid details")) {
                myHandler.obtainMessage(ERROR_INVALID_DETAILS, jsonObject.getString("msg")).sendToTarget();
            } else {
                LogMessage.d("Status = " + jsonObject.getString("status"));
                myHandler.obtainMessage(ERROR, jsonObject.getString("msg")).sendToTarget();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            myHandler.obtainMessage(ERROR, "DTH data not found").sendToTarget();
        }
    }

    // Mobile Product
    private void makeMobileProduct() {
        textView.setText("Updating\n\n1/5) Mobile Companies (Done)\n2/5) DTH Companies (Done)\n3/5) Mobile Services (Processing)");
        // create new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.product;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "service",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            userArrayList.get(0).getUser_name(),
                            userArrayList.get(0).getDevice_id(),
                            userArrayList.get(0).getOtp_code(),
                            "1",
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    myHandler.obtainMessage(SUCCESS_MOBILE_PRODUCT, response).sendToTarget();
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
    private void parseMobileProductResponse(String response) {
        LogMessage.i("Mobile Product Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String encrypted_string = jsonObject.getString("data");
                String decryptedResponse = decryptAPI(encrypted_string);
                LogMessage.d("Mobile product 1 : " + decryptedResponse);
                JSONObject jsonObject1 = new JSONObject(decryptAPI(encrypted_string));
                JSONArray jsonArray = jsonObject1.getJSONArray("product");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    Product product = new Product();
                    product.setId(jsonObject2.getString("id"));
                    product.setProduct_name(jsonObject2.getString("product_name"));
                    product.setCompany_id(jsonObject2.getString("company_id"));
                    product.setProduct_logo(jsonObject2.getString("logo"));
                    product.setService_type("Mobile");
                    productArrayList.add(product);
                }
                databaseHelper.deleteProductDetail("Mobile");
                databaseHelper.addProductsDetails(productArrayList);
                progressBar.setProgress(60);
                // get dth product data
                makeDTHProduct();
            } else if (jsonObject.getString("status").equals("2") &&
                    TextUtils.equals(jsonObject.getString("msg").toLowerCase().trim(), "invalid details")) {
                myHandler.obtainMessage(ERROR_INVALID_DETAILS, jsonObject.getString("msg")).sendToTarget();
            } else {
                LogMessage.d("Status = " + jsonObject.getString("status"));
                myHandler.obtainMessage(ERROR, jsonObject.getString("msg")).sendToTarget();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            myHandler.obtainMessage(ERROR, "Mobile data not found").sendToTarget();
        }
    }

    // DTH Product
    private void makeDTHProduct() {
        textView.setText("Updating\n\n1/5) Mobile Companies (Done)\n2/5) DTH Companies (Done)\n3/5) Mobile Services (Done)\n4/5) DTH Services (Processing)");
        // create new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.product;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "service",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            userArrayList.get(0).getUser_name(),
                            userArrayList.get(0).getDevice_id(),
                            userArrayList.get(0).getOtp_code(),
                            "2",
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    myHandler.obtainMessage(SUCCESS_DTH_PRODUCT, response).sendToTarget();
                }
                catch (Exception ex) {
                    LogMessage.e("Error in DTH Product native method");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    // parse success response
    private void parseDTHProductResponse(String response) {
        LogMessage.i("DTH Product Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String encrypted_string = jsonObject.getString("data");
                String decryptedResponse = decryptAPI(encrypted_string);
                LogMessage.d("Mobile product 2 : " + decryptedResponse);
                JSONObject jsonObject1 = new JSONObject(decryptAPI(encrypted_string));
                JSONArray jsonArray = jsonObject1.getJSONArray("product");
                productArrayList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    Product product = new Product();
                    product.setId(jsonObject2.getString("id"));
                    product.setProduct_name(jsonObject2.getString("product_name"));
                    product.setCompany_id(jsonObject2.getString("company_id"));
                    product.setProduct_logo(jsonObject2.getString("logo"));
                    product.setService_type("DTH");
                    productArrayList.add(product);
                }
                databaseHelper.deleteProductDetail("DTH");
                databaseHelper.addProductsDetails(productArrayList);
                progressBar.setProgress(80);
                // get state data
                makeState();
            } else if (jsonObject.getString("status").equals("2") &&
                    TextUtils.equals(jsonObject.getString("msg").toLowerCase().trim(), "invalid details")) {
                myHandler.obtainMessage(ERROR_INVALID_DETAILS, jsonObject.getString("msg")).sendToTarget();
            } else {
                LogMessage.d("Status = " + jsonObject.getString("status"));
                myHandler.obtainMessage(ERROR, jsonObject.getString("msg")).sendToTarget();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            myHandler.obtainMessage(ERROR, "DTH data not found").sendToTarget();
        }
    }

    // get State data
    private void makeState() {
        textView.setText("Updating\n\n1/5) Mobile Companies (Done)\n2/5) DTH Companies (Done)\n3/5) Mobile Services (Done)\n4/5) DTH Services (Done)\n5/5) States (Processing)");
        // create new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.state;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "service",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            userArrayList.get(0).getUser_name(),
                            userArrayList.get(0).getDevice_id(),
                            userArrayList.get(0).getOtp_code(),
                            "1",
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    myHandler.obtainMessage(SUCCESS_STATE, response).sendToTarget();
                }
                catch (Exception ex) {
                    LogMessage.e("Error in DTH Product native method");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    // parse success response
    private void parseStateResponse(String response) {
        LogMessage.i("State Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String encrypted_string = jsonObject.getString("data");
                String decryptedResponse = decryptAPI(encrypted_string);
                LogMessage.d("All state : " + decryptedResponse);
                JSONObject jsonObject1 = new JSONObject(decryptAPI(encrypted_string));
                JSONArray jsonArray = jsonObject1.getJSONArray("state");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    State state = new State();
                    state.setCircle_id(jsonObject2.getString("circle_id"));
                    state.setCircle_name(jsonObject2.getString("circle_name"));
                    stateArrayList.add(state);
                }
                if (stateArrayList.size() > 0) {
                    databaseHelper.deleteStateDetail();
                    databaseHelper.addStatesDetails(stateArrayList);
                }
                // get setting data (color and recharge order field data)
                makeJsonSetting();
            } else if (jsonObject.getString("status").equals("2") &&
                    TextUtils.equals(jsonObject.getString("msg").toLowerCase().trim(), "invalid details")) {
                myHandler.obtainMessage(ERROR_INVALID_DETAILS, jsonObject.getString("msg")).sendToTarget();
            } else {
                LogMessage.d("Status = " + jsonObject.getString("status"));
                myHandler.obtainMessage(ERROR, jsonObject.getString("msg")).sendToTarget();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            myHandler.obtainMessage(ERROR, "States data not found").sendToTarget();
        }
    }

    // get mobile company data after getting mobile company data
    private void makeJsonSetting() {
        // create new thread for register user
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set OTP verification url
                    String url = URL.setting;
                    // Set parameters list in string array
                    String[] parameters = {
                            "otp_code",
                            "username",
                            "mac_address",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            userArrayList.get(0).getOtp_code(),
                            userArrayList.get(0).getUser_name(),
                            userArrayList.get(0).getDevice_id(),
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    parseSettingsResponse(response);
                }
                catch (Exception ex) {
                    LogMessage.e("Error in OTP verification user");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    // parse dth company response
    private void parseSettingsResponse(String response) {
        LogMessage.i("Setting Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                myHandler.obtainMessage(SUCCESS_SETTING, response).sendToTarget();
            } else if (jsonObject.getString("status").equals("2") &&
                    TextUtils.equals(jsonObject.getString("msg").toLowerCase().trim(), "invalid details")) {
                myHandler.obtainMessage(ERROR_INVALID_DETAILS, jsonObject.getString("msg")).sendToTarget();
            } else {
                LogMessage.d("Setting fail. Status = " + jsonObject.getString("status"));
                myHandler.obtainMessage(ERROR, jsonObject.getString("msg")).sendToTarget();
            }
        }
        catch (JSONException ex) {
            LogMessage.e("Error in parse setting response");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
            myHandler.obtainMessage(ERROR, "Setting data not found").sendToTarget();
        }
    }

    // parse success dth company
    private void parseSuccessSettingsResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {

                ArrayList<Color> colorArrayList = new ArrayList<Color>();

                String encrypted_response = jsonObject.getString("data2");
                String decrypted_response = decryptAPI(encrypted_response);
                LogMessage.i("Decoded settings : " + decrypted_response);

                String encrypted_response1 = jsonObject.getString("data");
                String decrypted_response1 = decryptAPI(encrypted_response1);
                LogMessage.d("Setting Response : " + decrypted_response1);

                // parse color data
                JSONObject object = new JSONObject(decrypted_response);
                JSONArray jsonArray = object.getJSONArray("color");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object1 = jsonArray.getJSONObject(i);
                    Color color = new Color();
                    color.setColor_name(object1.getString("name"));
                    color.setColo_value(object1.getString("value"));
                    colorArrayList.add(color);
                }

                // parse recharge control order data
                object = new JSONObject(decrypted_response1);
                jsonArray = object.getJSONArray("order");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object1 = jsonArray.getJSONObject(i);
                    if (object1.getString("name").compareTo("circle") == 0) {
                        sharedPreferences.edit().putString(constants.PREF_IS_CIRCLE_VISIBILITY, object1.getString("status")).commit();
                    }
                    /* [START] - 2017_05_30 - Add is credit parameter in recharge
                     * According to the status of isCredit set is credit check box in recharge screen
                     * If is credit status is 1 then display is credit check box other wise hide check box */
                    String isCreditValue = object1.getString("name");
                    if (TextUtils.equals(isCreditValue.toLowerCase(), "iscredit")) {
                        sharedPreferences.edit().putString(constants.PREF_IS_CREDIT_STATUS, object1.getString("status")).commit();
                    }
                    // save name1 visibility status in prefrence
                    String nameValue = object1.getString("name");
                    if (TextUtils.equals(nameValue.toLowerCase(), "name1")) {
                        sharedPreferences.edit().putString(constants.PREF_NAME_STATUS, object1.getString("status")).commit();
                    }
                    // [END]
                }
                // [END]
                if (colorArrayList.size() > 0) {
                    databaseHelper.deleteStatusColor();
                    databaseHelper.addColors(colorArrayList);
                }
            }
            // Put code after update data
            progressBar.setProgress(100);
            new NotificationUtil(getContextInstance()).sendNotification("Update", "Data updated successfully.", DateTime.getCurrentDateTime());
            prefs.saveString(constants.PREF_UPDATE_DATE, DateTime.getDate());
            prefs.saveString(constants.PREF_UPDATE_TIME, DateTime.getTime());

            /* [START] - Display data update date time */
            String updateDate = prefs.retriveString(constants.PREF_UPDATE_DATE, "0");
            String updateTime = prefs.retriveString(constants.PREF_UPDATE_TIME, "0");
            if (TextUtils.equals(updateDate, "0")) {
                txtLastUpdate.setVisibility(View.GONE);
            } else {
                txtLastUpdate.setVisibility(View.VISIBLE);
                txtLastUpdate.setText("Last update : " + updateDate + " " + updateTime);
            }
            // [END]
            // [START] - Back to home after update
            if (TextUtils.equals(requireUpdate, "1")) {
                LogMessage.i("Back to home");
                getActivity().finish();
                Intent intent = new Intent(getContextInstance(), MainActivity.class);
                startActivity(intent);
            }
            // [END]

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("Updating\n\n1/5) Mobile Companies (Done)\n2/5) DTH Companies (Done)\n3/5) Mobile Services (Done)\n4/5) DTH Services (Done)\n5/5) States (Done)\n\nDone.");
                    homeButton.setVisibility(View.VISIBLE);
                }
            });
        }
        catch (JSONException ex) {
            LogMessage.e("Error in parse settings response");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
            myHandler.obtainMessage(ERROR, "Setting data not found").sendToTarget();
        }
    }

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

    // display error in dialog and LogMessage out from application
    private void displayErrorDialog_Logout(String message) {
        /* [START] - 2017_05_01 - Close all alert dialog logic */
        try {
            alertDialog_Logout = new AlertDialog.Builder(getActivity())
                    .setTitle("Info!")
                    .setMessage(message + "\nPlease logout and login again.")
                    .setPositiveButton("Logout",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    sharedPreferences.edit().clear().commit();
                                    Intent intent1 = new Intent(getContextInstance(), LoginActivity.class);
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent1);
                                    UpdateData.this.getActivity().finish();
                                    sharedPreferences.edit().putBoolean(constants.LOGOUT, true).commit();
                                }
                            }
                    ).setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    alertDialog_Logout.dismiss();
                                }
                            }
                    ).create();
            alertDialog_Logout.show();
        }
        catch (Exception ex) {
            LogMessage.e("Error in error dialog");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
            try {
                Utility.toast(getContextInstance(), message + "\nPlease logout and login again.");
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
            if (msg.what == SUCCESS_MOBILE_COMPANY) {
                parseMobileCompanyResponse(msg.obj.toString());
            } else if (msg.what == SUCCESS_DTH_COMPANY) {
                parseDTHCompanyResponse(msg.obj.toString());
            } else if (msg.what == SUCCESS_MOBILE_PRODUCT) {
                parseMobileProductResponse(msg.obj.toString());
            } else if (msg.what == SUCCESS_DTH_PRODUCT) {
                parseDTHProductResponse(msg.obj.toString());
            } else if (msg.what == SUCCESS_STATE) {
                parseStateResponse(msg.obj.toString());
            } else if (msg.what == SUCCESS_SETTING) {
                parseSuccessSettingsResponse(msg.obj.toString());
            } else if (msg.what == ERROR) {
                displayErrorDialog(msg.obj.toString());
                displayError(msg.obj.toString());
            } else if (msg.what == ERROR_INVALID_DETAILS) {
                displayErrorDialog_Logout(msg.obj.toString());
                displayError(msg.obj.toString());
            }
        }
    };

    private void displayError(String error) {
        /* [START] - Display update date */
        String updateDate = prefs.retriveString(constants.PREF_UPDATE_DATE, "0");
        String updateTime = prefs.retriveString(constants.PREF_UPDATE_TIME, "0");
        if (TextUtils.equals(updateDate, "0")) {
            txtLastUpdate.setVisibility(View.GONE);
        } else {
            txtLastUpdate.setVisibility(View.VISIBLE);
            txtLastUpdate.setText("Last update : " + updateDate + " " + updateTime);
        }
        // [END]
        textView.setText("Error\n\n" + error);
        homeButton.setVisibility(View.VISIBLE);
    }
    // [END]

    public String decryptAPI(String response) {
        ArrayList<Default> defaultArrayList;
        defaultArrayList = databaseHelper.getDefaultSettings();
        String user_id = defaultArrayList.get(0).getUser_id();
        MCrypt mCrypt = new MCrypt(user_id, userArrayList.get(0).getDevice_id());
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
}
