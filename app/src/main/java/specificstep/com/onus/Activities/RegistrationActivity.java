package specificstep.com.onus.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import specificstep.com.onus.Encrypt.Encryption;
import specificstep.com.onus.GlobalClasses.CheckConnection;
import specificstep.com.onus.GlobalClasses.Constants;
import specificstep.com.onus.GlobalClasses.TransparentProgressDialog;
import specificstep.com.onus.GlobalClasses.URL;
import specificstep.com.onus.Models.LoginReq;
import specificstep.com.onus.R;
import specificstep.com.onus.utility.InternetUtil;
import specificstep.com.onus.utility.LogMessage;
import specificstep.com.onus.utility.Utility;

/**
 * Created by ubuntu on 12/1/17.
 */

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private final int SUCCESS_REGISTER_USER = 1, ERROR_REGISTER_USER = 2, ERROR = 3;
    private Context context;
    private EditText edtUsernameOrEmail;
    private Button btnRegisterApp ,btn_login;
    private String strUsernameOrEmail;
    final private int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 100;
    private String strDeviceId = "";
    private TransparentProgressDialog transparentProgressDialog;
    private String token;
    private Constants constants;
    private SharedPreferences sharedPreferences;
    String mac;
    String TAG = "RegistrationActivity ::";
    private String encryptStr;


    private Context getContextInstance() {
        if(context == null) {
            context = RegistrationActivity.this;
            return context;
        }
        else {
            return context;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        context = RegistrationActivity.this;
        /* [START] - Set actionbar title */
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#4F7D7B\">" + "\t Register App" + "</font>"));
        // [END]


        mac = getMacAddress(this);
        String icc_id = getICCID (this);
        String imei = getImei(this);

        /*Encrptdata("9429978568",
                "BSNLChannel","");*/




        initControls();
        setListener();
    }

    private void initControls() {
        /* [START] - Initialise class objects */
        constants = new Constants();
        sharedPreferences = getSharedPreferences(constants.SHAREEDPREFERENCE, Context.MODE_PRIVATE);
        transparentProgressDialog = new TransparentProgressDialog(this, R.drawable.fotterloading);
        // [END]

        /* [START] - Initialise control objects */
        edtUsernameOrEmail = (EditText) findViewById(R.id.edt_uname_or_email_act_reg);
        btnRegisterApp = (Button) findViewById(R.id.btn_reg_app_act_reg);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getLoginRequest();
            }
        });
        // [END]

        /* [START] - set input filter in username edit text */
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isWhitespace(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }

        };
        edtUsernameOrEmail.setFilters(new InputFilter[]{filter});
        // [END]

        // Get token from shared preference
        token = sharedPreferences.getString(constants.TOKEN, "");


        readPhoneState();
    }

    private void setListener() {
        // on click listener
        btnRegisterApp.setOnClickListener(this);
    }

    /**
     * Check phone state read permission is enable or not
     */
    public void readPhoneState() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

        } else {

        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnRegisterApp) {
            strUsernameOrEmail = edtUsernameOrEmail.getText().toString();
            if (strUsernameOrEmail == null ||strUsernameOrEmail.equals(null) || strUsernameOrEmail.equals("")) {
                Utility.toast(getContextInstance(), "Please enter Email id or Username or Mobile No");
            } else {
                try {
                    // Generate token
                    if (token == null || token.equals(null) || token.equals("")) {
                        token = FirebaseInstanceId.getInstance().getToken();
                    }
                    if (token == null || token.equals(null) || token.equals("")) {
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
                        if (checkConnection.isConnected(this) && token != null) {
                            showProgressDialog();
                            makeRegisterUser();
                            //makeLoginUser2();

                        } else {
                            Utility.toast(getContextInstance(), "Check your internet connection");
                        }
                    }
                }
                catch (Exception ex) {
                    LogMessage.e("Error while click on register button");
                    LogMessage.d("ERROR : " + ex.toString());
                    Utility.toast(getContextInstance(), "Check your internet connection");
                }
            }
        }
    }

    /* [START] - 2017_04_27 - Add native code for register user, and Remove volley code */
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
                            strUsernameOrEmail,
                            token,
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    parseRegisterUserResponse(response);
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
        LogMessage.i("Register User Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("0")) {
                myHandler.obtainMessage(SUCCESS_REGISTER_USER).sendToTarget();

            } else if (jsonObject.getString("status").equals("2")) {
                myHandler.obtainMessage(ERROR_REGISTER_USER, jsonObject.getString("msg")).sendToTarget();
            }
            else {
                LogMessage.d("Application Registration fail. Status = " + jsonObject.getString("status"));
                myHandler.obtainMessage(ERROR_REGISTER_USER, jsonObject.getString("msg")).sendToTarget();
            }
        }
        catch (JSONException ex) {
            LogMessage.e("Error in parse register user response");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
            myHandler.obtainMessage(ERROR, "Application Registration fail").sendToTarget();
        }
    }

    // Display register user error dialog
    private void displayRegisterUserErrorDialog(String message) {
        new AlertDialog.Builder(getContextInstance())
                .setTitle("Registration Error")
                .setCancelable(false)
                .setMessage(message)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    // handle register user messages
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == SUCCESS_REGISTER_USER) {
                dismissProgressDialog();
                Intent intent = new Intent(getContextInstance(), VerifyRegistrationActivity.class);
                intent.putExtra("uname", strUsernameOrEmail);
                intent.putExtra("device_id", strDeviceId);
                startActivity(intent);
            } else if (msg.what == ERROR_REGISTER_USER) {
                dismissProgressDialog();
                displayRegisterUserErrorDialog(msg.obj.toString());
            } else if (msg.what == ERROR) {
                dismissProgressDialog();
                Utility.toast(getContextInstance(), msg.obj.toString());
            }
        }
    };
    // [END]

    // private void makeJsonRegister() {}

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }

        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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



    //@kns.p
    private void makeLoginUser2() {
        // create new thread for user login
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    // set login user url
                    String url = "http://218.248.64.36:50505/APP_Server/APPServices/VersionCheck";
                    // Set parameters list in string array
                    String[] parameters = {
                            "msg",
                            "reqtype",
                            "key",
                            "trid",
                            "mac",
                            "version",
                            "lat",
                            "lang",
                            "icc_id",
                            "waid",
                            "pgwid",
                            "androidID"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            "VER",
                            "android",
                            "VER",
                            "644",
                            mac,
                            "1.97",
                            null,
                            null,
                            null,
                            "0",
                            null,
                            "651feb89bc7b7cf5"
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    Log.e("dasd", "response >>>: "+response );
                    //myHandler.obtainMessage(RESPONSE_LOGIN, response).sendToTarget();
                }
                catch (Exception ex) {
                    LogMessage.e("Error in register user");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    //myHandler.obtainMessage(ERROR_LOGIN, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }



    //=================================
    public static String getImei(Context context) {
        try {
            if (ContextCompat.checkSelfPermission(context, "android.permission.ACCESS_FINE_LOCATION") == 0) {
                return ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getMacAddress(Context context) {
        return ((WifiManager) context.getSystemService("wifi")).getConnectionInfo().getMacAddress();
    }

    public static String getICCID(Context context) {
        try {
            @SuppressLint("MissingPermission") String iccid = ((TelephonyManager) context.getSystemService("phone")).getSimSerialNumber();
            //Log.e(TAG, "iccid: " + iccid);
            return iccid;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getAndroidID(Context context) {
        try {
            String androidId = Settings.Secure.getString(context.getContentResolver(), "android_id");
           // Log.e(TAG, "androidId: " + androidId);
            return androidId;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public static int getRandomTransactionId(int min, int max) {
        return (Math.abs(new Random().nextInt()) % ((max - min) + 1)) + min;
    }

    public static String Encrptdata(String dataPart, String secretKey, String content) {
        String str = null;
        try {
            String appender = "1255106";
            String size = dataPart.length() + "";
            if (size.length() <= 1) {
                size = 0 + size;
            }
            str = size + dataPart + _encrypt(content, _encrypt(new StringBuffer(dataPart).reverse().toString() + appender, secretKey));
        } catch (Exception ex) {
            Log.e("", "Encrptdata: " +str);
          //  Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
        }
        return str;
    }

    public static String _encrypt(String message, String secretKey) throws Exception {
        SecretKey key = new SecretKeySpec(Arrays.copyOf(MessageDigest.getInstance("SHA-1").digest(secretKey.getBytes("utf-8")), 24), "DESede");
        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(1, key);
        return new String(Base64.encodeBase64(cipher.doFinal(message.getBytes("utf-8"))));
    }

    public static String toJson(Object object) throws JsonGenerationException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Writer strWriter = new StringWriter();
        mapper.writeValue(strWriter, object);
        return strWriter.toString();
    }

    private void getLoginRequest() {
        LoginReq loginRequest = new LoginReq();
        loginRequest.setMsg("VER");
        loginRequest.setReqtype("android");
        loginRequest.setSrc("9429978568");
        loginRequest.setKey("login");
        loginRequest.setTrid(""+getRandomTransactionId(100, 900));
        loginRequest.setMac(getMacAddress(this));
        loginRequest.setImei(getImei(this));
        loginRequest.setFcmid(FirebaseInstanceId.getInstance().getToken());

        String version = "20";
        loginRequest.setVersion(version);
        if (version != null) {
            loginRequest.setVersion(version);
        }
        loginRequest.setAndroidID(getAndroidID(this));
        //loginRequest.setIcc_id(getICCID(this));
       // loginRequest.setWaid("0");
        loginRequest.setMpin("7536");

        loginRequest.setLat(null);
        loginRequest.setLong1(null);
        loginRequest.setIcc_id("89918540400008404549");
        loginRequest.setWaid("0");
        loginRequest.setPgwid(null);
        try {
            String loginJson = this.toJson(loginRequest);
            if (loginJson != null) {
                Log.e(this.TAG, "*****loginRequest.toString() Data******" + loginJson);
                this.encryptStr = Encryption.Encrptdata
                        ("9429978568", "BSNLChannel", loginJson);
                Log.e(this.TAG, "*****Encypted Data******" + this.encryptStr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
       // if (DeviceInfoUtils.isNetworkAvailable(this)) {
           // getLoginResponse(this.encryptStr);
       // }
    }
    //==================================

}
