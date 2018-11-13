package specificstep.com.onus.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import specificstep.com.onus.Database.DatabaseHelper;
import specificstep.com.onus.GlobalClasses.CheckConnection;
import specificstep.com.onus.GlobalClasses.Constants;
import specificstep.com.onus.GlobalClasses.MCrypt;
import specificstep.com.onus.GlobalClasses.TransparentProgressDialog;
import specificstep.com.onus.GlobalClasses.URL;
import specificstep.com.onus.Models.Color;
import specificstep.com.onus.Models.Company;
import specificstep.com.onus.Models.Default;
import specificstep.com.onus.R;
import specificstep.com.onus.Sms.SmsListener;
import specificstep.com.onus.Sms.SmsReceiver;
import specificstep.com.onus.utility.InternetUtil;
import specificstep.com.onus.utility.LogMessage;
import specificstep.com.onus.utility.Utility;

/**
 * Created by ubuntu on 12/1/17.
 */

public class VerifyRegistrationActivity extends Activity implements View.OnClickListener {
    /* Other class objects */
    private Context context;
    private SharedPreferences sharedPreferences;
    private Constants constants;
    private DatabaseHelper databaseHelper;
    private TransparentProgressDialog transparentProgressDialog;
    private BroadcastReceiver messageReadReceiver;

    /* All local int and string variables */
    private final int SUCCESS_OTP_VERIFICATION = 1, ERROR_OTP_VERIFICATION = 2, ERROR = 3,
            SUCCESS_MOBILE_COMPANY = 4, SUCCESS_DTH_COMPANY = 5, SUCCESS_SETTING = 6,
            SUCCESS_REGISTER_USER = 7, ERROR_REGISTER_USER = 8, ERROR_TOAST = 9;
    private final int PERMISSION_REQUEST_CODE = 123;
    private String strOtp, strUserName, strToken, strUserId, strStateName, strStateId, strName;

    /* All ArrayList */
    private ArrayList<Company> companyArrayList;
    private ArrayList<Color> colorArrayList;

    /* All views */
    private EditText edtOtp;
    private Button btnRegisterApp, btnResend;
    private TextView txtTimer;

    /* variables of Count down timer for resend otp */
    private int minute = 1;
    private long onFinishCallTime = 1000 * 60 * minute;
    private long onTickCallTime = 1000;
    private String firstMinute = "01";
    private boolean secondTimeCall = false;

    private Context getContextInstance() {
        if (context == null) {
            context = VerifyRegistrationActivity.this;
            return context;
        } else {
            return context;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_reg);

        context = VerifyRegistrationActivity.this;
        constants = new Constants();
        companyArrayList = new ArrayList<Company>();
        colorArrayList = new ArrayList<Color>();
        sharedPreferences = getSharedPreferences(constants.SHAREEDPREFERENCE, Context.MODE_PRIVATE);
        databaseHelper = new DatabaseHelper(getContextInstance());
        transparentProgressDialog = new TransparentProgressDialog(this, R.drawable.fotterloading);
        strUserName = getIntent().getStringExtra("uname");
        strToken = sharedPreferences.getString(constants.TOKEN, "-1");


        if (Build.VERSION.SDK_INT >= 23) {
            int hasContactPermission = ActivityCompat.checkSelfPermission(getContextInstance(), android.Manifest.permission.READ_SMS);
            if (hasContactPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(VerifyRegistrationActivity.this, new String[]{android.Manifest.permission.READ_SMS}, PERMISSION_REQUEST_CODE);
            }
        }
        init();
    }

    private void init() {
        edtOtp = (EditText) findViewById(R.id.edt_otp_act_reg_verify);
        btnRegisterApp = (Button) findViewById(R.id.btn_reg_app_act_reg_verify);
        /* [START] - Resend verification timer and button and Count down timer */
        txtTimer = (TextView) findViewById(R.id.txt_Verify_Timer);
        btnResend = (Button) findViewById(R.id.btn_Verify_ResendCode);
        btnResend.setEnabled(false);
        btnResend.setOnClickListener(this);
        // [END]
        btnRegisterApp.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register sms receiver
        registerSmsReceiver();
        // start otp update timer
        countDownTimer.start();
    }

    private void registerSmsReceiver() {
        try {
            if (messageReadReceiver == null) {
                Constants.IS_RECEIVE_MESSAGE = true;
                LogMessage.d("Register SMS register");
                IntentFilter filter = new IntentFilter();
                filter.addAction("android.provider.Telephony.SMS_RECEIVED");
                messageReadReceiver = new SmsReceiver();
                SmsReceiver.bindListener(new SmsListener() {
                    @Override
                    public void messageReceived(String messageText) {
                        LogMessage.d("Registration : " + "Message : " + messageText);
                        try {
                            String[] separated = messageText.split(" : ");
                            edtOtp.setText(separated[1]);
                            Constants.IS_RECEIVE_MESSAGE = false;
                        }
                        catch (Exception ex) {
                            LogMessage.e("Registration : " + "Error in message receive : " + ex.toString());
                            ex.printStackTrace();
                            edtOtp.setText("");
                            Constants.IS_RECEIVE_MESSAGE = false;
                        }
                        if (!TextUtils.isEmpty(edtOtp.getText().toString())
                                && edtOtp.getText().toString().trim().length() != 0) {
                            Constants.IS_RECEIVE_MESSAGE = false;
                            checkOtp();
                        }
                    }
                });
                // register receiver
                registerReceiver(messageReadReceiver, filter);
            } else {
                Constants.IS_RECEIVE_MESSAGE = false;
                LogMessage.d("SMS receiver already register");
            }
        }
        catch (Exception ex) {
            LogMessage.e("Error in register receiver");
            LogMessage.e("Error : " + ex.toString());
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        // un register sms receiver
        unRegisterReceiver();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        // un register sms receiver
        unRegisterReceiver();
        // cancel otp update timer
        countDownTimer.cancel();
    }

    private void unRegisterReceiver() {
        try {
            Constants.IS_RECEIVE_MESSAGE = false;
            LogMessage.d("Un - Register SMS register");
            if (messageReadReceiver != null) {
                unregisterReceiver(messageReadReceiver);
                messageReadReceiver = null;
                LogMessage.d("Unregister sms receiver done");
            }
            messageReadReceiver = null;
        }
        catch (Exception ex) {
            Constants.IS_RECEIVE_MESSAGE = false;
            LogMessage.e("Error in unregister receiver");
            LogMessage.e("Error : " + ex.toString());
            ex.printStackTrace();
            messageReadReceiver = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnRegisterApp) {
            checkOtp();
        } else if (v == btnResend) {
            resendOtp();
        }
    }

    private void resendOtp() {
        try {
            String strUsernameOrEmail = strUserName;
            // Generate token
            if (strToken == null || strToken.equals(null) || strToken.equals("") || strToken.equals("-1")) {
                strToken = FirebaseInstanceId.getInstance().getToken();
                sharedPreferences.edit().putString(constants.TOKEN, strToken).commit();
            }
            if (strToken.equals(null) || strToken.equals("")) {
                new AlertDialog.Builder(getContextInstance())
                        .setTitle("Error")
                        .setCancelable(false)
                        .setMessage("Please check your internet access")
                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            } else if (strUsernameOrEmail.equals(null) || strUsernameOrEmail.equals("")) {
                Utility.toast(getContextInstance(), "Please enter email id or Username");
            } else {
                /*Checks whether user's phone is connected to internet or not*/
                CheckConnection checkConnection = new CheckConnection();
                if (checkConnection.isConnected(this) && strToken != null) {
                    showProgressDialog();
                    makeRegisterUser();
                } else {
                    Utility.toast(getContextInstance(), "Check your internet connection");
                }
            }
        }
        catch (Exception ex) {
            LogMessage.e("Error while click on register button");
            LogMessage.d("ERROR : " + ex.toString());
        }
    }

    private void checkOtp() {
        strOtp = edtOtp.getText().toString();
        CheckConnection checkConnection = new CheckConnection();
        if (strToken == null || strToken.equals(null) || strToken.equals("") || strToken.equals("-1")) {
            strToken = FirebaseInstanceId.getInstance().getToken();
            sharedPreferences.edit().putString(constants.TOKEN, strToken).commit();
        }
        if (strToken.equals(null) || strToken.equals("")) {
            new AlertDialog.Builder(getContextInstance())
                    .setTitle("Error")
                    .setCancelable(false)
                    .setMessage("Please check your internet access")
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        } else if (strOtp.isEmpty()) {
            Utility.toast(getContextInstance(), "Enter OTP");
        } else if (checkConnection.isConnectingToInternet(this) == true) {
            showProgressDialog();
            makeOTPVerification();
        } else {
            Utility.toast(getContextInstance(), "Check your internet connection");
        }
    }

    /* [START] - 2017_04_27 - Add native code for OTP verification, and Remove volley code */
    private void makeOTPVerification() {
        // create new thread for register user
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set OTP verification url
                    String url = URL.register;
                    // Set parameters list in string array
                    String[] parameters = {
                            "otp_code",
                            "username",
                            "mac_address",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            strOtp,
                            strUserName,
                            strToken,
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    parseOTPVerificationResponse(response);
                }
                catch (Exception ex) {
                    LogMessage.e("Error in OTP verification user");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR_OTP_VERIFICATION, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    // method for parse OTP Verification user response
    private void parseOTPVerificationResponse(String response) {
        LogMessage.i("OTP Verification Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("0")) {
                myHandler.obtainMessage(ERROR_OTP_VERIFICATION, jsonObject.getString("msg")).sendToTarget();
            } else if (jsonObject.getString("status").equals("1")) {
                myHandler.obtainMessage(SUCCESS_OTP_VERIFICATION, response).sendToTarget();
            } else {
                LogMessage.d("OTP Verification fail. Status = " + jsonObject.getString("status"));
                myHandler.obtainMessage(ERROR_OTP_VERIFICATION, jsonObject.getString("msg")).sendToTarget();
            }
        }
        catch (JSONException ex) {
            LogMessage.e("Error in parse register user response");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
            myHandler.obtainMessage(ERROR, "OTP verification fail").sendToTarget();
        }
    }

    // Display OTP verification user error dialog
    private void displayOTPVerificationErrorDialog(String message) {
        new AlertDialog.Builder(getContextInstance())
                .setTitle("Error in verify OTP")
                .setCancelable(false)
                .setMessage(message)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    // Parse success response and display dialog
    private void parseSuccessOTPVerificationResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            sharedPreferences.edit().putString(constants.VERIFICATION_STATUS, "1").commit();

            strUserId = jsonObject.getString("user_id");
            strStateId = jsonObject.getString("state_id");
            strStateName = jsonObject.getString("state_name");
            strName = jsonObject.getString("name");

            databaseHelper.deleteDefaultSettings();
            databaseHelper.addDefaultSettings(strUserId, strStateId, strStateName);
            databaseHelper.deleteUsersDetail();
            databaseHelper.addUserDetails(strUserId, strOtp, strUserName, strToken, strName, "0");

            SmsReceiver.bindListener(null);
            // get mobile company data
            makeJsonMobileCompany();
        }
        catch (Exception ex) {
            LogMessage.e("Error in parse success message");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
            myHandler.obtainMessage(ERROR_TOAST, "OTP verification fail").sendToTarget();
        }
    }

    // handle OTP verification user messages
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == SUCCESS_OTP_VERIFICATION) {
                parseSuccessOTPVerificationResponse(msg.obj.toString());
            } else if (msg.what == SUCCESS_MOBILE_COMPANY) {
                parseSuccessMobileCompanyResponse(msg.obj.toString());
            } else if (msg.what == SUCCESS_DTH_COMPANY) {
                parseSuccessDTHCompanyResponse(msg.obj.toString());
            } else if (msg.what == SUCCESS_SETTING) {
                parseSuccessSettingsResponse(msg.obj.toString());
            } else if (msg.what == ERROR_OTP_VERIFICATION) {
                dismissProgressDialog();
                displayOTPVerificationErrorDialog(msg.obj.toString());
            } else if (msg.what == SUCCESS_REGISTER_USER) {
                dismissProgressDialog();
                parseRegisterUserResponse(msg.obj.toString());
            } else if (msg.what == ERROR) {
                dismissProgressDialog();
                Utility.toast(getContextInstance(), msg.obj.toString());
            } else if (msg.what == ERROR_TOAST) {
                dismissProgressDialog();
                Utility.toast(getContextInstance(), msg.obj.toString());
            }
        }
    };

    // get mobile company data after OTP verification
    private void makeJsonMobileCompany() {
        // create new thread for register user
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set OTP verification url
                    String url = URL.company;
                    // Set parameters list in string array
                    String[] parameters = {
                            "otp_code",
                            "username",
                            "mac_address",
                            "service",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            strOtp,
                            strUserName,
                            strToken,
                            "1",
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    parseMobileCompanyResponse(response);
                }
                catch (Exception ex) {
                    LogMessage.e("Error in OTP verification user");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR_OTP_VERIFICATION, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    // parse mobile company response
    private void parseMobileCompanyResponse(String response) {
        LogMessage.i("Mobile Company Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String encrypted_string = jsonObject.getString("data");
                String decryptedString = decryptAPI(encrypted_string);
                LogMessage.i("Response : " + encrypted_string);
                LogMessage.i("Decrypt : " + decryptedString);
                myHandler.obtainMessage(SUCCESS_MOBILE_COMPANY, decryptedString).sendToTarget();
            } else {
                LogMessage.d("Mobile Company fail. Status = " + jsonObject.getString("status"));
                myHandler.obtainMessage(ERROR_OTP_VERIFICATION, jsonObject.getString("msg")).sendToTarget();
            }
        }
        catch (JSONException ex) {
            LogMessage.e("Error in parse mobile company response");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
            myHandler.obtainMessage(ERROR_TOAST, "OTP verification fail").sendToTarget();
        }
    }

    // parse success mobile company
    private void parseSuccessMobileCompanyResponse(String response) {
        try {
            JSONObject jsonObject1 = new JSONObject(response);
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
            if (companyArrayList.size() > 0) {
                databaseHelper.deleteCompanyDetail("Mobile");
                databaseHelper.addCompanysDetails(companyArrayList);
            }
            // get dth company data
            makeJsonDTHCompany();
        }
        catch (Exception ex) {
            LogMessage.e("Error in parse success mobile company response");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
            myHandler.obtainMessage(ERROR, "OTP verification fail").sendToTarget();
        }
    }

    // get mobile company data after getting mobile company data
    private void makeJsonDTHCompany() {
        // create new thread for register user
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set OTP verification url
                    String url = URL.company;
                    // Set parameters list in string array
                    String[] parameters = {
                            "otp_code",
                            "username",
                            "mac_address",
                            "service",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            strOtp,
                            strUserName,
                            strToken,
                            "2",
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    parseDTHCompanyResponse(response);
                }
                catch (Exception ex) {
                    LogMessage.e("Error in OTP verification user");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR_OTP_VERIFICATION, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    // parse dth company response
    private void parseDTHCompanyResponse(String response) {
        LogMessage.i("DTH Company Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String encrypted_string = jsonObject.getString("data");
                String decryptedString = decryptAPI(encrypted_string);
                LogMessage.i("Response : " + encrypted_string);
                LogMessage.i("Decrypt : " + decryptedString);
                myHandler.obtainMessage(SUCCESS_DTH_COMPANY, decryptedString).sendToTarget();
            } else {
                LogMessage.d("DTH Company fail. Status = " + jsonObject.getString("status"));
                myHandler.obtainMessage(ERROR_OTP_VERIFICATION, jsonObject.getString("msg")).sendToTarget();
            }
        }
        catch (JSONException ex) {
            LogMessage.e("Error in parse mobile company response");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
            myHandler.obtainMessage(ERROR, "OTP verification fail").sendToTarget();
        }
    }

    // parse success dth company
    private void parseSuccessDTHCompanyResponse(String response) {
        try {
            JSONObject jsonObject1 = new JSONObject(response);
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
            if (companyArrayList.size() > 0) {
                databaseHelper.deleteCompanyDetail("DTH");
                databaseHelper.addCompanysDetails(companyArrayList);
            }
            // get setting data (color data and recharge control order data)
            makeJsonSetting();
        }
        catch (Exception ex) {
            LogMessage.e("Error in parse success mobile company response");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
            myHandler.obtainMessage(ERROR, "OTP verification fail").sendToTarget();
        }
    }

    // TODO : make to get plan types

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
                            strOtp,
                            strUserName,
                            strToken,
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    parseSettingsResponse(response);
                }
                catch (Exception ex) {
                    LogMessage.e("Error in OTP verification user");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR_OTP_VERIFICATION, "Please check your internet access").sendToTarget();
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
            } else {
                LogMessage.d("DTH Company fail. Status = " + jsonObject.getString("status"));
                myHandler.obtainMessage(ERROR_OTP_VERIFICATION, jsonObject.getString("msg")).sendToTarget();
            }
        }
        catch (JSONException ex) {
            LogMessage.e("Error in parse mobile company response");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
            myHandler.obtainMessage(ERROR, "OTP verification fail").sendToTarget();
        }
    }

    // parse success dth company
    private void parseSuccessSettingsResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
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
            dismissProgressDialog();
            // stop otp update timer
            countDownTimer.cancel();
            // after getting setting data start login activity
            Intent intent = new Intent(getContextInstance(), LoginActivity.class);
            intent.putExtra("device_id", strToken);
            startActivity(intent);
        }
        catch (JSONException ex) {
            LogMessage.e("Error in parse settings response");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
            myHandler.obtainMessage(ERROR, "OTP verification fail").sendToTarget();
        }
    }

    private void makeRegisterUser() {
        // create new thread for register user
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set register user url
                    String url = URL.register;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            strUserName,
                            strToken,
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    myHandler.obtainMessage(SUCCESS_REGISTER_USER, response).sendToTarget();
                }
                catch (Exception ex) {
                    LogMessage.e("Error in register user");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR_REGISTER_USER, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    // method for parse register user response
    private void parseRegisterUserResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("0")) {
                /* [START] - start otp update timer */
                edtOtp.setEnabled(true);
                txtTimer.setVisibility(View.VISIBLE);
                btnResend.setEnabled(false);
                firstMinute = "01";
                secondTimeCall = false;
                countDownTimer.start();
                // [END]
            } else if (jsonObject.getString("status").equals("2")) {
                Utility.toast(getContextInstance(), jsonObject.getString("msg"));
            } else {
                LogMessage.d("Application Registration fail. Status = " + jsonObject.getString("status"));
                Utility.toast(getContextInstance(), jsonObject.getString("msg"));
            }
        }
        catch (JSONException e) {
            LogMessage.e("Error occur while register application");
            LogMessage.e("Registration : " + "Error :" + e.toString());
            e.printStackTrace();
            Utility.toast(getContextInstance(), "User verification fail");
        }
    }
    // [END]

    /*Method : decryptAPI
        Decrypt response of webservice*/
    public String decryptAPI(String response) {
        ArrayList<Default> defaultArrayList;
        defaultArrayList = databaseHelper.getDefaultSettings();
        String userId = defaultArrayList.get(0).getUser_id();
        MCrypt mCrypt = new MCrypt(userId, strToken);
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

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /* [START] - Count down timer for resend otp, interval = 2 minute */
    // private CountDownTimer countDownTimer = new CountDownTimer(5000, 1000) { // (onFinish call time, onTick call time)
    private CountDownTimer countDownTimer = new CountDownTimer(onFinishCallTime, onTickCallTime) {
        @Override
        public void onTick(long millisUntilFinished) {
            try {
                long currentSecond = millisUntilFinished / 1000;
                String checkCurrentSecondLength = currentSecond + "";
                // LogMessage.d("Tick Second : " + currentSecond);
                if (currentSecond == 60) {
                    currentSecond = 59;
                }
                if (checkCurrentSecondLength.length() == 1) {
                    txtTimer.setText(firstMinute + ":0" + currentSecond);
                } else {
                    txtTimer.setText(firstMinute + ":" + currentSecond);
                }
            }
            catch (Exception ex) {
                LogMessage.e("Error in on tick");
                LogMessage.e("Error : " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        @Override
        public void onFinish() {
            LogMessage.d("Finish call");
            try {
                // txtTimer.setText("00:00");
                if (secondTimeCall) {
                    txtTimer.setVisibility(View.INVISIBLE);
                    btnResend.setEnabled(true);
                    edtOtp.setEnabled(false);
                } else {
                    secondTimeCall = true;
                    firstMinute = "00";
                    countDownTimer.start();
                }
            }
            catch (Exception ex) {
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
