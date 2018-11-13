package specificstep.com.onus.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import specificstep.com.onus.Database.DatabaseHelper;
import specificstep.com.onus.GlobalClasses.CheckConnection;
import specificstep.com.onus.GlobalClasses.Constants;
import specificstep.com.onus.GlobalClasses.MCrypt;
import specificstep.com.onus.GlobalClasses.TransparentProgressDialog;
import specificstep.com.onus.GlobalClasses.URL;
import specificstep.com.onus.Models.DateTime;
import specificstep.com.onus.Models.Default;
import specificstep.com.onus.Models.User;
import specificstep.com.onus.R;
import specificstep.com.onus.utility.InternetUtil;
import specificstep.com.onus.utility.LogMessage;
import specificstep.com.onus.utility.NotificationUtil;
import specificstep.com.onus.utility.Utility;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_OTP = "key_otp";
    public static final String EXTRA_PASSWORD = "key_password";

    private String password, oldPassword;
    private final int SUCCESS = 1, ERROR = 2;
    /* [START] - All View objects */
    // View class object for display fragment view
    // [END]

    /* [START] - Other class objects */
    private Context context;
    // Custom log message class
    // private LogMessage log;
    private CheckConnection connection;
    private TransparentProgressDialog transparentProgressDialog;
    // Database class
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    // All static variables class
    private Constants constants;
    // [END]

    /* [START] - Controls objects */
    // Old Password
    private EditText edtOldPassword;
    private ImageView imgShowOldPassword, imgHideOldPassword, imgLockOldPassword, imgUnlockOldPassword;
    // New Password
    private EditText edtNewPassword;
    private ImageView imgShowNewPassword, imgHideNewPassword, imgLockNewPassword, imgUnlockNewPassword;
    // Confirm Password
    private EditText edtConfirmPassword;
    private ImageView imgShowConfirmPassword, imgHideConfirmPassword, imgLockConfirmPassword, imgUnlockConfirmPassword;
    // Change password button
    private Button btnChangePassword;
    // [END]

    /* [START] - Variables */
    private ArrayList<User> userArrayList;
    private String encodedNewPassword, encodedOldPassword, strDeviceId, strOtp, strUsername, strOldPassword, strRegistrationDate;
    private String printMessage = "", strRememberMe = "";
    private int wrongPasswordCounter = 0;
    boolean isConfirmPasswordShow = false, isOldPasswordShow = false, isNewPasswordShow = false;
    String strAppOtp,strPassword;
    // [END]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        context = ForgotPasswordActivity.this;
        // log = new LogMessage(ChangePasswordFragment.class.getSimpleName());

        try {

            strAppOtp = getIntent().getStringExtra(EXTRA_OTP);
            strPassword = getIntent().getStringExtra(EXTRA_PASSWORD);

        } catch (Exception e) {
            e.toString();
        }

        initController();
        setListener();
        oldPasswordListener();
        newPasswordListener();
        confirmPasswordListener();

    }

    private void initController() {
        constants = new Constants();
        sharedPreferences = getSharedPreferences(constants.SHAREEDPREFERENCE, Context.MODE_PRIVATE);

        connection = new CheckConnection();
        transparentProgressDialog = new TransparentProgressDialog(getApplicationContext(), R.drawable.fotterloading);
        databaseHelper = new DatabaseHelper(getApplicationContext());

        userArrayList = new ArrayList<User>();
        userArrayList = databaseHelper.getUserDetail();
        strOtp = userArrayList.get(0).getOtp_code();
        strDeviceId = userArrayList.get(0).getDevice_id();
        strUsername = userArrayList.get(0).getUser_name();
        strOldPassword = userArrayList.get(0).getPassword();
        strRememberMe = userArrayList.get(0).getRemember_me();
        strRegistrationDate = userArrayList.get(0).getReg_date();

        // Button
        btnChangePassword = (Button) findViewById(R.id.btn_ChangePassword);
        // Old password
        edtOldPassword = (EditText) findViewById(R.id.edt_ChangePassword_OldPassword);
        imgShowOldPassword = (ImageView) findViewById(R.id.img_ChangePassword_ShowOldPassword);
        imgHideOldPassword = (ImageView) findViewById(R.id.img_ChangePassword_HideOldPassword);
        imgLockOldPassword = (ImageView) findViewById(R.id.img_ChangePassword_LockedOldPassword);
        imgUnlockOldPassword = (ImageView) findViewById(R.id.img_ChangePassword_UnLockedOldPassword);
        // New password
        edtNewPassword = (EditText) findViewById(R.id.edt_ChangePassword_NewPassword);
        imgShowNewPassword = (ImageView) findViewById(R.id.img_ChangePassword_ShowNewPassword);
        imgHideNewPassword = (ImageView) findViewById(R.id.img_ChangePassword_HideNewPassword);
        imgLockNewPassword = (ImageView) findViewById(R.id.img_ChangePassword_LockedNewPassword);
        imgUnlockNewPassword = (ImageView) findViewById(R.id.img_ChangePassword_UnLockedNewPassword);
        // Confirm password
        edtConfirmPassword = (EditText) findViewById(R.id.edt_ChangePassword_ConfirmPassword);
        imgShowConfirmPassword = (ImageView) findViewById(R.id.img_ChangePassword_ShowConfirmPassword);
        imgHideConfirmPassword = (ImageView) findViewById(R.id.img_ChangePassword_HideConfirmPassword);
        imgLockConfirmPassword = (ImageView) findViewById(R.id.img_ChangePassword_LockedConfirmPassword);
        imgUnlockConfirmPassword = (ImageView) findViewById(R.id.img_ChangePassword_UnLockedConfirmPassword);

        /* [START] Set old password if user select remember password in login screen */
        if (strRememberMe.equals("1") && strOldPassword != null) {
            edtOldPassword.setText(strOldPassword);
            imgHideOldPassword.setVisibility(View.VISIBLE);
            LogMessage.d("Old password : " + strOldPassword);
        } else if (strRememberMe.equals("0")) {
            edtOldPassword.setText("");
            imgHideOldPassword.setVisibility(View.GONE);
        }
        // [END]
    }

    private void setListener() {
        btnChangePassword.setOnClickListener(this);
        // Old password
        imgShowOldPassword.setOnClickListener(this);
        imgHideOldPassword.setOnClickListener(this);
        // New password
        imgShowNewPassword.setOnClickListener(this);
        imgHideNewPassword.setOnClickListener(this);
        // Confirm password
        imgShowConfirmPassword.setOnClickListener(this);
        imgHideConfirmPassword.setOnClickListener(this);
    }

    private void oldPasswordListener() {
        /*
         * Add  and remove show password icon for Old password
         * while user insert or removes text
         * in edittext of password
         * */
        edtOldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    imgUnlockOldPassword.setVisibility(View.GONE);
                    imgLockOldPassword.setVisibility(View.VISIBLE);
                    imgShowOldPassword.setVisibility(View.GONE);
                    imgHideOldPassword.setVisibility(View.GONE);
                } else {
                    imgUnlockOldPassword.setVisibility(View.VISIBLE);
                    imgLockOldPassword.setVisibility(View.GONE);
                    if (isOldPasswordShow) {
                        imgShowOldPassword.setVisibility(View.VISIBLE);
                        imgHideOldPassword.setVisibility(View.GONE);
                    } else {
                        imgShowOldPassword.setVisibility(View.GONE);
                        imgHideOldPassword.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void newPasswordListener() {
        /*
         * Add  and remove show password icon for New password
         * while user insert or removes text
         * in edittext of password
         * */
        edtNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    imgUnlockNewPassword.setVisibility(View.GONE);
                    imgLockNewPassword.setVisibility(View.VISIBLE);
                    imgShowNewPassword.setVisibility(View.GONE);
                    imgHideNewPassword.setVisibility(View.GONE);
                } else {
                    imgUnlockNewPassword.setVisibility(View.VISIBLE);
                    imgLockNewPassword.setVisibility(View.GONE);
                    if (isNewPasswordShow) {
                        imgShowNewPassword.setVisibility(View.VISIBLE);
                        imgHideNewPassword.setVisibility(View.GONE);
                    } else {
                        imgShowNewPassword.setVisibility(View.GONE);
                        imgHideNewPassword.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void confirmPasswordListener() {
        /*
         * Add  and remove show password icon for Confirm password
         * while user insert or removes text
         * in edittext of password
         * */
        edtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    imgUnlockConfirmPassword.setVisibility(View.GONE);
                    imgLockConfirmPassword.setVisibility(View.VISIBLE);
                    imgShowConfirmPassword.setVisibility(View.GONE);
                    imgHideConfirmPassword.setVisibility(View.GONE);
                } else {
                    imgUnlockConfirmPassword.setVisibility(View.VISIBLE);
                    imgLockConfirmPassword.setVisibility(View.GONE);
                    if (isConfirmPasswordShow) {
                        imgShowConfirmPassword.setVisibility(View.VISIBLE);
                        imgHideConfirmPassword.setVisibility(View.GONE);
                    } else {
                        imgShowConfirmPassword.setVisibility(View.GONE);
                        imgHideConfirmPassword.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnChangePassword) {
            makeChangePassword();
        }
        // Old password
        else if (v == imgShowOldPassword) {
            isOldPasswordShow = false;
            edtOldPassword.setTransformationMethod(new PasswordTransformationMethod());
        } else if (v == imgHideOldPassword) {
            isOldPasswordShow = true;
            edtOldPassword.setTransformationMethod(null);
        }
        // New password
        else if (v == imgShowNewPassword) {
            isNewPasswordShow = false;
            edtNewPassword.setTransformationMethod(new PasswordTransformationMethod());
        } else if (v == imgHideNewPassword) {
            isNewPasswordShow = true;
            edtNewPassword.setTransformationMethod(null);
        }
        // Confirm password
        else if (v == imgShowConfirmPassword) {
            isConfirmPasswordShow = false;
            edtConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
        } else if (v == imgHideConfirmPassword) {
            isConfirmPasswordShow = true;
            edtConfirmPassword.setTransformationMethod(null);
        }
    }

    private boolean checkPassword() {
        boolean status = false;
        if(TextUtils.isEmpty(edtNewPassword.getText())) {
            Toast.makeText(getApplicationContext(),"Please Enter New Password.",Toast.LENGTH_LONG).show();
            status = false;
        } else if(TextUtils.isEmpty(edtConfirmPassword.getText())) {
            Toast.makeText(getApplicationContext(),"Please Enter Confirm Password.",Toast.LENGTH_LONG).show();
            status = false;
        } else if(!edtNewPassword.getText().toString().equals(edtConfirmPassword.getText().toString())){
            Toast.makeText(getApplicationContext(),"New Password and Confirm Password does not match!",Toast.LENGTH_LONG).show();
            status = false;
        } else {
            status = true;
        }
        return status;
    }

    private boolean checkValidation(String new_pass) {
        //Password validation as below if you receive 1 in password
        //Password Must Contain
        //Special Character,
        // Uppercase & Lowercase Latter,
        // Number,
        // Minimum 8 Character.
        if(new_pass.length()>=8 && isValidPassword(new_pass)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public void callForgotChangePassword() {
        printMessage = "";
        if (!connection.isConnectingToInternet(getApplicationContext())) {
            new AlertDialog.Builder(getApplicationContext())
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
            if (checkConnection.isConnectingToInternet(getApplicationContext()) == true) {
                password = Utility.getString(edtNewPassword);
                oldPassword = Utility.getString(edtConfirmPassword);

                showProgressDialog();

                makeNativeChangePassword();
            } else {
                Utility.toast(getApplicationContext(), "Check your internet connection");
            }
        }
    }

    private void makeChangePassword() {

        if(checkPassword()) {
            /** password == 1 -> check validation
             * */
            if(strPassword.equalsIgnoreCase("1")) {
                String new_pass = edtNewPassword.getText().toString();

                if (checkValidation(new_pass)) {
                    //api calls
                    //presenter.onChangePasswordButtonClicked(getActivity(),forgot_otp,newPasswordEditText.getText().toString(),confirmPasswordEditText.getText().toString());
                    callForgotChangePassword();
                } else {
                    Toast.makeText(getApplicationContext(), "password must contain " +
                            "Special Character , Uppercase & Lowercase Latter and Number with minimum 8 characters", Toast.LENGTH_LONG).show();
                }

            }
            else {
                //api calls
                //presenter.onChangePasswordButtonClicked(getActivity(),forgot_otp,newPasswordEditText.getText().toString(),confirmPasswordEditText.getText().toString());
                callForgotChangePassword();
            }
        }



    }

    /* [START] - 2017_04_28 - Add native code for cash book, and Remove volley code */
    private void makeNativeChangePassword() {
        // create new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // ----------------------- Get change password data
                    ArrayList<Default> defaultArrayList;
                    defaultArrayList = databaseHelper.getDefaultSettings();
                    String user_id = defaultArrayList.get(0).getUser_id();
                    LogMessage.d("device_id : " + strDeviceId);
                    MCrypt mCrypt = new MCrypt(user_id, strDeviceId);
                    try {
                        byte[] encrypted_bytes = mCrypt.encrypt(password);
                        byte[] encrypted_bytes_oldPassword = mCrypt.encrypt(oldPassword);
                        encodedNewPassword = Base64.encodeToString(encrypted_bytes, Base64.DEFAULT);
                        encodedOldPassword = Base64.encodeToString(encrypted_bytes_oldPassword, Base64.DEFAULT);
                        LogMessage.d("Old Password" + " : " + oldPassword + " = " + encodedOldPassword);
                        LogMessage.d("New Password" + " : " + password + " = " + encodedNewPassword);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        myHandler.obtainMessage(ERROR, "Error in password change").sendToTarget();
                    }
                    // -------------------------
                    // set cashBook url
                    String url = URL.GET_FORGOT_CHANGE_PASSWORD;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "app",
                            "forgot_otp",
                            "new_pass",
                            "confirm_pass"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            strUsername,
                            strDeviceId,
                            strOtp,
                            Constants.APP_VERSION,
                            strAppOtp,
                            encodedNewPassword,
                            encodedOldPassword
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
        LogMessage.i("Change Password Req Res : " + printMessage);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {

                String message = jsonObject.getString("msg");

                Utility.toast(getApplicationContext(), message);

                /* [START] Set new password if user remember password */
                if (strRememberMe.equals("1")) {
                    databaseHelper.updateUserDetails(strUsername, password, "1", "");
                    LogMessage.d("New password : " + password + " saved");
                } else if (strRememberMe.equals("0")) {
                    databaseHelper.updateUserDetails(strUsername, password, "0", "");
                    LogMessage.d("New password : " + password + " not saved");
                }
                // [END]

                /* [START] - Open login screen after password change success */
                sharedPreferences.edit().clear().commit();
                Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                ForgotPasswordActivity.this.finish();
                sharedPreferences.edit().putBoolean(constants.LOGOUT, true).commit();
                // [END]

                /* [START] - Send notification after change password */
                changePasswordNotification();
                // [END]
            } else if (jsonObject.getString("status").equals("2")) {
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("Error in change password..")
                        .setCancelable(false)
                        .setMessage(jsonObject.getString("msg"))
                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
//                if (wrongPasswordCounter >= 3) {
//                                    /*redirect on registration after 3 wrong attempts*/
//                    Utility.toast(context, "You have attempted more than 3 times");
//                    Intent i = new Intent(context, RegistrationActivity.class);
//                    startActivity(i);
//                }
            } else {
                myHandler.obtainMessage(ERROR, jsonObject.getString("msg")).sendToTarget();
            }
        }
        catch (JSONException e) {
            LogMessage.e("Error in password change");
            LogMessage.e("Error : " + e.getMessage());
            e.printStackTrace();
            Utility.toast(getApplicationContext(), "Please check your internet access");
        }
    }

    private AlertDialog alertDialog;

    // display error in dialog
    private void displayErrorDialog(String message) {
        /* [START] - 2017_05_01 - Close all alert dialog logic */
        try {
            alertDialog = new AlertDialog.Builder(context).create();
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
                Utility.toast(context, message);
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

    /* [START] - Change password notification */
    public void changePasswordNotification() {

        new NotificationUtil(getApplicationContext()).sendNotification(getResources().getString(R.string.app_name),
                "Your password has been changed successfully.", DateTime.getCurrentDateTime());
    }
    // [END]

    // show progress dialog
    private void showProgressDialog() {
        try {
            if (transparentProgressDialog == null) {
                transparentProgressDialog = new TransparentProgressDialog(getApplicationContext(), R.drawable.fotterloading);
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
    public void onBackPressed() {
        super.onBackPressed();
        ForgotPasswordActivity.this.finish();
    }
}

