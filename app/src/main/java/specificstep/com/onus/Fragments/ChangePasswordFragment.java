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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
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
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import specificstep.com.onus.Activities.HomeActivity;
import specificstep.com.onus.Activities.LoginActivity;
import specificstep.com.onus.Activities.MainActivity;
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

/**
 * Created by ubuntu on 8/3/17.
 */

public class ChangePasswordFragment extends Fragment implements View.OnClickListener {

    private String password, oldPassword;
    private final int SUCCESS = 1, ERROR = 2;
    /* [START] - All View objects */
    // View class object for display fragment view
    private View view;
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
    // [END]

    private HomeActivity mainActivity() {
        return ((HomeActivity) getActivity());
    }

    private Context getContextInstance() {
        if (context == null) {
            context = ChangePasswordFragment.this.getActivity();
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
        view = inflater.inflate(R.layout.activity_change_password, null);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        context = ChangePasswordFragment.this.getActivity();
        // log = new LogMessage(ChangePasswordFragment.class.getSimpleName());

        initController();
        setListener();
        oldPasswordListener();
        newPasswordListener();
        confirmPasswordListener();
        return view;
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
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

    }

    private void initController() {
        constants = new Constants();
        sharedPreferences = context.getSharedPreferences(constants.SHAREEDPREFERENCE, Context.MODE_PRIVATE);

        connection = new CheckConnection();
        transparentProgressDialog = new TransparentProgressDialog(getContextInstance(), R.drawable.fotterloading);
        databaseHelper = new DatabaseHelper(getContextInstance());

        userArrayList = new ArrayList<User>();
        userArrayList = databaseHelper.getUserDetail();
        strOtp = userArrayList.get(0).getOtp_code();
        strDeviceId = userArrayList.get(0).getDevice_id();
        strUsername = userArrayList.get(0).getUser_name();
        strOldPassword = userArrayList.get(0).getPassword();
        strRememberMe = userArrayList.get(0).getRemember_me();
        strRegistrationDate = userArrayList.get(0).getReg_date();

        // Button
        btnChangePassword = (Button) view.findViewById(R.id.btn_ChangePassword);
        // Old password
        edtOldPassword = (EditText) view.findViewById(R.id.edt_ChangePassword_OldPassword);
        imgShowOldPassword = (ImageView) view.findViewById(R.id.img_ChangePassword_ShowOldPassword);
        imgHideOldPassword = (ImageView) view.findViewById(R.id.img_ChangePassword_HideOldPassword);
        imgLockOldPassword = (ImageView) view.findViewById(R.id.img_ChangePassword_LockedOldPassword);
        imgUnlockOldPassword = (ImageView) view.findViewById(R.id.img_ChangePassword_UnLockedOldPassword);
        // New password
        edtNewPassword = (EditText) view.findViewById(R.id.edt_ChangePassword_NewPassword);
        imgShowNewPassword = (ImageView) view.findViewById(R.id.img_ChangePassword_ShowNewPassword);
        imgHideNewPassword = (ImageView) view.findViewById(R.id.img_ChangePassword_HideNewPassword);
        imgLockNewPassword = (ImageView) view.findViewById(R.id.img_ChangePassword_LockedNewPassword);
        imgUnlockNewPassword = (ImageView) view.findViewById(R.id.img_ChangePassword_UnLockedNewPassword);
        // Confirm password
        edtConfirmPassword = (EditText) view.findViewById(R.id.edt_ChangePassword_ConfirmPassword);
        imgShowConfirmPassword = (ImageView) view.findViewById(R.id.img_ChangePassword_ShowConfirmPassword);
        imgHideConfirmPassword = (ImageView) view.findViewById(R.id.img_ChangePassword_HideConfirmPassword);
        imgLockConfirmPassword = (ImageView) view.findViewById(R.id.img_ChangePassword_LockedConfirmPassword);
        imgUnlockConfirmPassword = (ImageView) view.findViewById(R.id.img_ChangePassword_UnLockedConfirmPassword);

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

    private boolean changePasswordValidation() {
        String oldPassword = Utility.getString(edtOldPassword);
        String newPassword = Utility.getString(edtNewPassword);
        String confirmPassword = Utility.getString(edtConfirmPassword);
        // check empty
        if (oldPassword.length() == 0 || TextUtils.isEmpty(oldPassword)) {
            Utility.toast(getContextInstance(), "Enter old password");
            return false;
        } else if (newPassword.length() == 0 || TextUtils.isEmpty(newPassword)) {
            Utility.toast(getContextInstance(), "Enter new password");
            return false;
        } else if (confirmPassword.length() == 0 || TextUtils.isEmpty(confirmPassword)) {
            Utility.toast(getContextInstance(), "Enter confirm password");
            return false;
        } else if (!TextUtils.equals(newPassword, confirmPassword)) {
            Utility.toast(getContextInstance(), "Password not match");
            return false;
        } else if (!TextUtils.equals(strOldPassword, oldPassword)) {
            Utility.toast(getContextInstance(), "Old password is wrong");
            return false;
        }
        return true;
    }

    private void makeChangePassword() {
        if (changePasswordValidation()) {
            printMessage = "";
            if (!connection.isConnectingToInternet(getContextInstance())) {
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
                if (checkConnection.isConnectingToInternet(getContextInstance()) == true) {
                    password = Utility.getString(edtNewPassword);
                    oldPassword = Utility.getString(edtOldPassword);

                    showProgressDialog();

                    makeNativeChangePassword();
                } else {
                    Utility.toast(getContextInstance(), "Check your internet connection");
                }
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
                    String url = URL.changePassword;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "new_password",
                            "old_password",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            strUsername,
                            strDeviceId,
                            strOtp,
                            encodedNewPassword,
                            encodedOldPassword,
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
        LogMessage.i("Change Password Req Res : " + printMessage);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {

                String message = jsonObject.getString("msg");

                Utility.toast(getContextInstance(), message);

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
                Intent intent1 = new Intent(getContextInstance(), LoginActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                ChangePasswordFragment.this.getActivity().finish();
                sharedPreferences.edit().putBoolean(constants.LOGOUT, true).commit();
                // [END]

                /* [START] - Send notification after change password */
                changePasswordNotification();
                // [END]
            } else if (jsonObject.getString("status").equals("2") &&
                    jsonObject.getString("message").equalsIgnoreCase("Invalid Details")) {
                new AlertDialog.Builder(getContextInstance())
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
            Utility.toast(getContextInstance(), "Please check your internet access");
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

        new NotificationUtil(getContextInstance()).sendNotification(getResources().getString(R.string.app_name),
                "Your password has been changed successfully.", DateTime.getCurrentDateTime());
    }
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
