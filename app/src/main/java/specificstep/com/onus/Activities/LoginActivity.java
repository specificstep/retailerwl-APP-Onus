package specificstep.com.onus.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import specificstep.com.onus.Database.DatabaseHelper;
import specificstep.com.onus.GlobalClasses.CheckConnection;
import specificstep.com.onus.GlobalClasses.Constants;
import specificstep.com.onus.GlobalClasses.MCrypt;
import specificstep.com.onus.GlobalClasses.TransparentProgressDialog;
import specificstep.com.onus.GlobalClasses.URL;
import specificstep.com.onus.Models.Default;
import specificstep.com.onus.Models.User;
import specificstep.com.onus.R;
import specificstep.com.onus.utility.InternetUtil;
import specificstep.com.onus.utility.LogMessage;
import specificstep.com.onus.utility.Utility;

/**
 * Created by ubuntu on 5/1/17.
 */

public class LoginActivity extends Activity implements View.OnClickListener {
    /* Other class objects */
    private Context context;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private Constants constants;
    private TransparentProgressDialog transparentProgressDialog;
    private CheckConnection connection;
    private AlertDialog alertDialog;

    /* All local int and string and boolean variables */
    private final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 100;
    private final int RESPONSE_LOGIN = 1, ERROR_LOGIN = 2;
    private int wrongPasswordCounter = 3;
    private boolean isEdtUserNameCilcked = false, isOldPasswordShow = false, isRememberPasswordChecked = false;
    private String strUserName, strPassword, strEncodedPassword, strDeviceId, strOtp,
            strRememberMe = "", strEnteredPassword, strOriginalPassword = "";

    /* All ArrayList */
    private ArrayList<User> userArrayList;
    private final int SUCCESS = 1, ERROR = 2;
    String TAG = "Sign In Fragment :: ";

    /* All Views */
    private EditText edtUserName, edtPassword;
    private Button btnLogin;
    private CheckBox chkRemember;
    private TextView txtForgotPassword;
    private ImageView imgShowPassword, imgHidePassword, imgLocked, imgUnlocked;
    private LinearLayout llRootView;
    public static final String EXTRA_USERNAME = "user_name";

    private Context getContextInstance() {
        if (context == null) {
            context = LoginActivity.this;
            return context;
        } else {
            return context;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = LoginActivity.this;
        constants = new Constants();
        sharedPreferences = getSharedPreferences(constants.SHAREEDPREFERENCE, Context.MODE_PRIVATE);
        databaseHelper = new DatabaseHelper(this);
        connection = new CheckConnection();
        transparentProgressDialog = new TransparentProgressDialog(this, R.drawable.fotterloading);

        sharedPreferences.edit().putString(constants.LOGIN_STATUS, "0").commit();

        userArrayList = new ArrayList<User>();
        userArrayList = databaseHelper.getUserDetail();
        strOtp = userArrayList.get(0).getOtp_code();
        strRememberMe = userArrayList.get(0).getRemember_me();
        strPassword = userArrayList.get(0).getPassword();
        strUserName = userArrayList.get(0).getUser_name();
        strDeviceId = userArrayList.get(0).getDevice_id();

        sharedPreferences.edit().putString("fetch_data", "1").commit();

        readPhoneState();
        init();

        chkRemember.setChecked(false);
        edtUserName.setText(strUserName);

        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    imgLocked.setVisibility(View.GONE);
                    imgUnlocked.setVisibility(View.VISIBLE);
                    imgShowPassword.setVisibility(View.GONE);
                    imgHidePassword.setVisibility(View.GONE);
                } else {
                    imgLocked.setVisibility(View.VISIBLE);
                    imgUnlocked.setVisibility(View.GONE);
                    if (isOldPasswordShow) {
                        imgShowPassword.setVisibility(View.VISIBLE);
                        imgHidePassword.setVisibility(View.GONE);
                    } else {
                        imgShowPassword.setVisibility(View.GONE);
                        imgHidePassword.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        /*Checks user have saved password or not*/
        if (strRememberMe.equals("1") && strPassword != null) {
            edtPassword.setText(strPassword);
            chkRemember.setChecked(true);
            imgHidePassword.setVisibility(View.VISIBLE);
            LogMessage.d("password : " + strPassword);
        } else if (strRememberMe.equals("0")) {
            edtPassword.setText("");
            chkRemember.setChecked(false);
            imgHidePassword.setVisibility(View.GONE);
        }

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProgressIndicator();
                makeForgotPasswordApiCall();
            }
        });

    }

    //Forgot password call
    public void makeForgotPasswordApiCall() {

        //scheme name thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.forgot_password;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            strUserName,
                            strDeviceId,
                            strOtp,
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    System.out.println("Forgot response: " + response);
                    myHandlerForgot.obtainMessage(SUCCESS, response).sendToTarget();
                }
                catch (Exception ex) {
                    Log.e(TAG, "  Error  : "+ ex.getMessage() );

                    ex.printStackTrace();
                    myHandlerForgot.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();

    }

    // display error in dialog
    private void displayErrorDialog(String message) {
        /* [START] - 2017_05_01 - Close all alert dialog logic */

        try {
            alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
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
            // Log.e(TAG,"Error in error dialog");
            // Log.e(TAG,"Error : " + ex.getMessage());
            ex.printStackTrace();
            try {
                Utility.toast(LoginActivity.this, message);
            }
            catch (Exception e) {
                //Log.e(TAG,"Error in toast message");
                // Log.e(TAG,"ERROR : " + e.getMessage());
            }
        }
        // [END]
    }

    // handle submit thread messages
    private Handler myHandlerForgot = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == SUCCESS) {
                dismissProgressDialog();
                parseSubmitSuccessResponse(msg.obj.toString());
            } else if (msg.what == ERROR) {
                dismissProgressDialog();
                displayErrorDialog(msg.obj.toString());
            }
        }
    };

    //parse submit api
    public void parseSubmitSuccessResponse(String response) {

        Log.e(TAG, " Forgot Response : "+ response );

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String message = jsonObject.getString("message");
                Log.e(TAG, "Message : "+ message );
                Log.e(TAG, "Message : "+ message );
                Toast.makeText(LoginActivity.this,message,Toast.LENGTH_LONG).show();
                dismissProgressDialog();
                Intent intent = new Intent(getApplicationContext(), ForgotOtpVarificationActivity.class);
                intent.putExtra(EXTRA_USERNAME, strUserName);
                startActivity(intent);
            } else if (jsonObject.getString("status").equals("2")) {
                Toast.makeText(LoginActivity.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                dismissProgressDialog();
            } else {

            }
        }
        catch (JSONException e) {
            Log.e(TAG,"Cashbook : " + "Error 4 : " + e.getMessage());
            Utility.toast(LoginActivity.this, "No result found");
            e.printStackTrace();
        }

    }

    public void setProgressIndicator() {
        if (transparentProgressDialog != null) {
            transparentProgressDialog.dismiss();
            transparentProgressDialog = null;
        }
        transparentProgressDialog = new TransparentProgressDialog(LoginActivity.this, R.drawable.fotterloading);

        if (!transparentProgressDialog.isShowing()) {
            transparentProgressDialog.show();
        }

    }

    /*for marshmallow and above that version Runtime permission for reading phone state*/
    public void readPhoneState() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        } else {
            //getIMEI(this);
        }
    }

    private void init() {
        edtUserName = (EditText) findViewById(R.id.edt_uname_act_login);
        edtPassword = (EditText) findViewById(R.id.edt_pwd_act_login);
        chkRemember = (CheckBox) findViewById(R.id.cb_remember_me);
        txtForgotPassword = (TextView) findViewById(R.id.forgot_password_text);
        btnLogin = (Button) findViewById(R.id.btn_loin_act_login);
        imgShowPassword = (ImageView) findViewById(R.id.iv_sho_pwd);
        imgHidePassword = (ImageView) findViewById(R.id.iv_hide_pwd);
        imgUnlocked = (ImageView) findViewById(R.id.iv_unlocked);
        imgLocked = (ImageView) findViewById(R.id.iv_locked);
        llRootView = (LinearLayout) findViewById(R.id.ll_root_view);
        // set listener
        imgShowPassword.setOnClickListener(this);
        imgHidePassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        chkRemember.setOnClickListener(this);
        edtUserName.setOnClickListener(this);
    }

    /*get device id*/
    @SuppressLint("MissingPermission")
    public String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        strDeviceId = telephonyManager.getDeviceId();
        return strDeviceId;
    }

    @Override
    public void onClick(View v) {
        strUserName = edtUserName.getText().toString();
        strEnteredPassword = edtPassword.getText().toString();
        switch (v.getId()) {
            case R.id.btn_loin_act_login:
                if (strUserName.isEmpty()) {
                    Utility.toast(getContextInstance(), "Enter username");
                } else if (strEnteredPassword.isEmpty()) {
                    Utility.toast(getContextInstance(), "Enter Password");
                } else if (!connection.isConnectingToInternet(this)) {
                    new AlertDialog.Builder(getContextInstance())
                            .setTitle("Connection Error")
                            .setCancelable(false)
                            .setMessage("Please make sure your device is connected to internet")
                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                } else {
                    CheckConnection checkConnection = new CheckConnection();
                    if (checkConnection.isConnectingToInternet(this) == true) {
                        showProgressDialog();
                        strOriginalPassword = strEnteredPassword;
                        makeLoginUser();

                    } else {
                        Utility.toast(getContextInstance(), "Check your internet connection");
                    }
                }
                break;

            case R.id.iv_sho_pwd:
                isOldPasswordShow = false;
                edtPassword.setTransformationMethod(new PasswordTransformationMethod());
                break;

            case R.id.iv_hide_pwd:
                isOldPasswordShow = true;
                edtPassword.setTransformationMethod(null);
                break;

            case R.id.cb_remember_me:
                if (chkRemember.isChecked()) {
                    isRememberPasswordChecked = false;
                    chkRemember.setChecked(true);
                    /*for text align justify of dialog content*/
                    String htmlText = " %s ";
                    String alert_message;
                    alert_message = "<html><body leftmargin=\"0\" topmargin=\"0\" rightmargin=\"0\" bottommargin=\"0\"><p align=\"justify\">";
                    alert_message += "Are you sure want to have " + getResources().getString(R.string.app_name)
                            + " remember your password ? This will significantly decrease the security of your "
                            + getResources().getString(R.string.app_name) + " Account !";
                    alert_message += "</p></body></html>";

                    final Dialog dialog = new Dialog(this);
                    dialog.setContentView(R.layout.alert_login);
                    int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
                    int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.50);

                    dialog.getWindow().setLayout(width, height);
                    WebView webView = (WebView) dialog.findViewById(R.id.web_alert_content);
                    TextView txtYes = (TextView) dialog.findViewById(R.id.tv_yes_alert_login);
                    TextView txtNo = (TextView) dialog.findViewById(R.id.tv_no_alert_login);

                    webView.setScrollContainer(false);
                    webView.loadData(String.format(htmlText, alert_message), "text/html", "utf-8");

                    txtYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            isRememberPasswordChecked = true;
                            chkRemember.setChecked(true);
                            databaseHelper.updateUserDetails(strUserName, strPassword, "1", "");
                        }
                    });
                    txtNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            isRememberPasswordChecked = false;
                            chkRemember.setChecked(false);
                            databaseHelper.updateUserDetails(strUserName, strPassword, "0", "");
                        }
                    });
                    /* manage remember me checked and unchecked while dialog dismiss */
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (isRememberPasswordChecked) {
                                chkRemember.setChecked(true);
                            } else {
                                chkRemember.setChecked(false);
                            }
                        }
                    });
                    dialog.show();
                } else {
                    chkRemember.setChecked(false);
                    databaseHelper.updateUserDetails(strUserName, strPassword, "0", "");
                }
                break;

            case R.id.edt_uname_act_login:
                isEdtUserNameCilcked = true;
                break;
        }
    }

    /* [START] - 2017_04_27 - Add native code for user login */
    private void makeLoginUser() {
        // create new thread for user login
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Default> defaultArrayList;
                defaultArrayList = databaseHelper.getDefaultSettings();
                String user_id = defaultArrayList.get(0).getUser_id();
                LogMessage.d("device_id : " + strDeviceId);
                MCrypt mCrypt = new MCrypt(user_id, strDeviceId);
                try {
                    byte[] encrypted_bytes = mCrypt.encrypt(strOriginalPassword);
                    strEncodedPassword = Base64.encodeToString(encrypted_bytes, Base64.DEFAULT);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    // set login user url
                    String url = URL.login;
                    // Set parameters list in string array
                    String[] parameters = {
                            "otp_code",
                            "username",
                            "mac_address",
                            "password",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            strOtp,
                            strUserName,
                            strDeviceId,
                            strEncodedPassword,
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    myHandler.obtainMessage(RESPONSE_LOGIN, response).sendToTarget();
                }
                catch (Exception ex) {
                    LogMessage.e("Error in register user");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR_LOGIN, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    // method for parse user login response
    private void parseUserLoginResponse(String response) {
        LogMessage.i("Login Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String decrypted_response = decryptAPI(jsonObject.getString("data"));
                JSONObject decrypted_response1 = new JSONObject(decrypted_response);
                String reg_date = decrypted_response1.getString("reg_date");

                Intent intent = new Intent(getContextInstance(), MainActivity.class);
                startActivity(intent);
                /*for saving remember me checked in SQLite */
                if (chkRemember.isChecked()) {
                    databaseHelper.updateUserDetails(strUserName, strOriginalPassword, "1", reg_date);
                } else {
                    databaseHelper.updateUserDetails(strUserName, strOriginalPassword, "0", reg_date);
                }
                sharedPreferences.edit().putString(constants.LOGIN_STATUS, "1").commit();
                sharedPreferences.edit().putString(constants.VERIFICATION_STATUS, "1").commit();
            } else if (jsonObject.getString("status").equals("2") && jsonObject.getString("msg").equals("Invalid details")) {
                /* [START] - Wrong password counter with message */
                wrongPasswordCounter--;
                String message = "";
                if (wrongPasswordCounter == 1)
                    message = "You are left " + wrongPasswordCounter + " login attempt.";
                else
                    message = "You are left " + wrongPasswordCounter + " login attempts.";
                sharedPreferences.edit().putString(constants.LOGIN_STATUS, "0").commit();
                // [END]
                if (wrongPasswordCounter == 0) {
                    new AlertDialog.Builder(getContextInstance())
                            .setTitle("Error in Login")
                            .setCancelable(false)
                            .setMessage("You have attempted more than 3 times")
                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sharedPreferences.edit().putString(constants.TOKEN, "");
                                    sharedPreferences.edit().putString(constants.LOGIN_STATUS, "-1").commit();
                                    Intent i = new Intent(getContextInstance(), RegistrationActivity.class);
                                    startActivity(i);
                                }
                            }).show();
                } else {
                    myHandler.obtainMessage(ERROR_LOGIN, jsonObject.getString("msg") + "\n" + message).sendToTarget();
                }
            }
        }
        catch (JSONException e) {
            Utility.toast(getContextInstance(), "Login failed...");
            e.printStackTrace();
        }
    }

    // Display user login error dialog
    private void displayUserLoginErrorDialog(String message) {
        /* [START] - 2017_05_01 - Close all alert dialog logic */
        try {
            alertDialog = new AlertDialog.Builder(getContextInstance()).create();
            alertDialog.setTitle("Login Error");
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

    // handle user login messages
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == RESPONSE_LOGIN) {
                dismissProgressDialog();
                parseUserLoginResponse(msg.obj.toString());
            } else if (msg.what == ERROR_LOGIN) {
                dismissProgressDialog();
                displayUserLoginErrorDialog(msg.obj.toString());
            }
        }
    };
    // [END]

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //getIMEI(this);
                } else { }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*for managing focus while both edit text are not empty*/
        if (edtUserName.getText().toString().isEmpty()) {
            edtUserName.requestFocus();
        } else if (edtPassword.getText().toString().isEmpty()) {
            edtPassword.requestFocus();
        } else {
            llRootView.clearFocus();
        }
    }

    /*Method : decryptAPI Decrypt response of webservice*/
    public String decryptAPI(String response) {
        ArrayList<Default> defaultArrayList;
        defaultArrayList = databaseHelper.getDefaultSettings();
        String user_id = defaultArrayList.get(0).getUser_id();
        MCrypt mCrypt = new MCrypt(user_id, strDeviceId);
        String decrypted_response = null;
        byte[] decrypted_bytes = Base64.decode(response, Base64.DEFAULT);
        try {
            decrypted_response = new String(mCrypt.decrypt(mCrypt.bytesToHex(decrypted_bytes)), "UTF-8");
            LogMessage.d("decrypted : " + decrypted_response);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return decrypted_response;
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
