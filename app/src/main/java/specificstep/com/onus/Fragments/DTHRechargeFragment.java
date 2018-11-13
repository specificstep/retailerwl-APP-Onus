package specificstep.com.onus.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import specificstep.com.onus.Activities.HomeActivity;
import specificstep.com.onus.Database.DatabaseHelper;
import specificstep.com.onus.GlobalClasses.CheckConnection;
import specificstep.com.onus.GlobalClasses.Constants;
import specificstep.com.onus.GlobalClasses.MCrypt;
import specificstep.com.onus.GlobalClasses.TransparentProgressDialog;
import specificstep.com.onus.GlobalClasses.URL;
import specificstep.com.onus.Models.Default;
import specificstep.com.onus.Models.Product;
import specificstep.com.onus.Models.State;
import specificstep.com.onus.Models.User;
import specificstep.com.onus.R;
import specificstep.com.onus.utility.InternetUtil;
import specificstep.com.onus.utility.LogMessage;
import specificstep.com.onus.utility.NotificationUtil;
import specificstep.com.onus.utility.Utility;

/**
 * Created by ubuntu on 16/1/17.
 */

public class DTHRechargeFragment extends Fragment implements View.OnClickListener {
    private View view;
    /* Other class objects */
    private Context context;
    private DatabaseHelper databaseHelper;
    private Dialog dialog, dialog_success;
    private TransparentProgressDialog transparentProgressDialog;
    private CheckConnection connection;
    private SharedPreferences sharedPreferences;
    private Constants constants;
    private AlertDialog alertDialog, alertDialog_Permission;

    /* All local int and string variables */
    private final String ACTIONBAR_TITLE = "DTH Recharge";
    private final int SUCCESS_RECHARGE = 1, ERROR_RECHARGE = 2, ERROR = 3, SUCCESS_STATE = 4, SUCCESS_NAME = 5;
    private String strMobileNumber, strAmount, strCircle, strProductId, strCompanyId,
            strUserName, strMacAddress, strOtpCode, strCircleId, strCompanyName, strProductName;
    private int PICK_CONTACT = 500;
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private String isCreditStatus = "0", name = "";

    /* All ArrayList */
    private ArrayList<State> stateArrayList;
    private ArrayList<User> userArrayList;
    private ArrayList<Default> defaultArrayList;

    /* Adapter object */
    private ArrayAdapter<String> adapterCircleName;

    /* All Views */
    private Button btnProceed;
    private TextView txtChangeCompany, txtChangeProduct, txtCompanyName, txtProductName;
    private EditText edtMobileNumber, edtAmount, edtName;
    private Spinner spiCircle;
    private CheckBox chkIsCredit;
    private ImageView imgAllContacts;
    private LinearLayout llIsCreditView, llNameView, llCircleContainer;

    private Context getContextInstance() {
        if (context == null) {
            context = DTHRechargeFragment.this.getActivity();
            return context;
        } else {
            return context;
        }
    }

    private HomeActivity mainActivity() {
        return ((HomeActivity) getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dth_recharge, null);

        context = DTHRechargeFragment.this.getActivity();

        /* [START] - 2017_04_18 set title bar as DTH recharge */
        mainActivity().getSupportActionBar().setTitle(ACTIONBAR_TITLE);
        // [END]

        databaseHelper = new DatabaseHelper(getActivity());
        constants = new Constants();
        sharedPreferences = getActivity().getSharedPreferences(constants.SHAREEDPREFERENCE, Context.MODE_PRIVATE);
        dialog = new Dialog(getActivity());
        connection = new CheckConnection();
        dialog_success = new Dialog(getActivity());

        stateArrayList = new ArrayList<State>();
        userArrayList = new ArrayList<User>();
        defaultArrayList = new ArrayList<Default>();

        stateArrayList = databaseHelper.getStateDetails();
        userArrayList = databaseHelper.getUserDetail();
        defaultArrayList = databaseHelper.getDefaultSettings();
        transparentProgressDialog = new TransparentProgressDialog(getActivity(), R.drawable.fotterloading);

        Bundle bundle = getArguments();
        strCompanyId = bundle.getString("company_id");
        strProductId = bundle.getString("product_id");
        strCompanyName = bundle.getString("company_name");
        strProductName = bundle.getString("product_name");

        strOtpCode = userArrayList.get(0).getOtp_code();
        strUserName = userArrayList.get(0).getUser_name();
        strMacAddress = userArrayList.get(0).getDevice_id();

        init();

        // Display bottom bar in add balance
        mainActivity().displayRechargeBottomBar(false);

        return view;
    }

    private void init() {
        llIsCreditView = (LinearLayout) view.findViewById(R.id.ll_DTHRecharge_IsCreditView);
        llNameView = (LinearLayout) view.findViewById(R.id.ll_DTHRecharge_NameView);
        llCircleContainer = (LinearLayout) view.findViewById(R.id.circle_container);
        chkIsCredit = (CheckBox) view.findViewById(R.id.chk_DTHRecharge_IsCredit);
        edtName = (EditText) view.findViewById(R.id.edt_DTHRecharge_Name);
        edtMobileNumber = (EditText) view.findViewById(R.id.edt_mo_no_fragment_recharge);
        edtAmount = (EditText) view.findViewById(R.id.edt_amt_fragment_recharge);
        btnProceed = (Button) view.findViewById(R.id.btn_proceed_fragment_recharge);
        spiCircle = (Spinner) view.findViewById(R.id.sp_circle_fragment_recharge);
        txtCompanyName = (TextView) view.findViewById(R.id.tv_company_name_fragment_DTH_recharge);
        txtProductName = (TextView) view.findViewById(R.id.tv_product_name_fragment_DTH_recharge);
        txtChangeCompany = (TextView) view.findViewById(R.id.tv_change_company);
        txtChangeProduct = (TextView) view.findViewById(R.id.tv_change_product);
        /* [START] - 2017_05_31 - Add contact image in mobile recharge screen. */
        imgAllContacts = (ImageView) view.findViewById(R.id.img_DTHRecharge_AllContacts);
        // [END]

        /* [START] - Image View onClickListener */
        imgAllContacts.setOnClickListener(this);
        // [END]
        txtChangeCompany.setOnClickListener(this);
        txtChangeProduct.setOnClickListener(this);

        txtCompanyName.setText(strCompanyName);
        txtProductName.setText(strProductName);

        /* set previous entries of mobile no and mount if user had clicked on change company or change product */
        if (sharedPreferences.getBoolean(constants.isClicked, false) == true) {
            edtMobileNumber.setText(sharedPreferences.getString(constants.MOBILENUMBER, ""));
            edtAmount.setText(sharedPreferences.getString(constants.AMOUNT, ""));
        }
        /* get State name */
        ArrayList<String> circle_array = new ArrayList<String>();
        for (int i = 0; i < stateArrayList.size(); i++) {
            circle_array.add(stateArrayList.get(i).getCircle_name());
        }

        spiCircle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        /* get default value of state spinner when user insert smart no */
        edtMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                strMobileNumber = edtMobileNumber.getText().toString();
                if (!strMobileNumber.isEmpty()) {
                    CheckConnection checkConnection = new CheckConnection();
                    if (checkConnection.isConnectingToInternet(getActivity()) == true) {
                        makeNativeDefaultState();
                        // makeGetName();
                    } else {
                        LogMessage.d("Internet connection not found");
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        /* Set on item selected listener in circle spinner */
        adapterCircleName = new ArrayAdapter<String>(getActivity(), R.layout.adapter_spinner, circle_array);
        spiCircle.setAdapter(adapterCircleName);
        // set default value of state spinner
        for (int i = 0; i < adapterCircleName.getCount(); i++) {
            if (defaultArrayList.get(0).getState_name().trim().equals(adapterCircleName.getItem(i).toString())) {
                spiCircle.setSelection(i);
                break;
            }
        }
        btnProceed.setOnClickListener(this);

        /* [START] - set Circle visibility */
        String circle_visibility = sharedPreferences.getString(constants.PREF_IS_CIRCLE_VISIBILITY, "0");
        if (circle_visibility.compareTo("0") == 0) {
            llCircleContainer.setVisibility(View.GONE);
        }
        // [END]
        /* [START] - 2017_05_30 - Set is credit visibility */
        String isCreditVisibility = sharedPreferences.getString(constants.PREF_IS_CREDIT_STATUS, "0");
        if (TextUtils.equals(isCreditVisibility, "0")) {
            llIsCreditView.setVisibility(View.GONE);
        }
        // Set name visibility
        String nameVisibility = sharedPreferences.getString(constants.PREF_NAME_STATUS, "0");
        if (TextUtils.equals(nameVisibility, "0")) {
            llNameView.setVisibility(View.GONE);
        }
        // [END]
    }

    @Override
    public void onClick(View v) {
        strMobileNumber = edtMobileNumber.getText().toString();
        strAmount = edtAmount.getText().toString();
        strCircle = spiCircle.getSelectedItem().toString();
        strCircleId = databaseHelper.getCircleID(strCircle);
        switch (v.getId()) {
            case R.id.btn_proceed_fragment_recharge:
                if (!TextUtils.equals(edtAmount.getText().toString().trim(), "0")) {
                    showConfirmationDialog();
                } else {
                    Utility.toast(getContextInstance(), "Please enter amount more than 0");
                }
                break;
            case R.id.btn_cancel_confirm_dialog:
                dialog.dismiss();
                break;
            case R.id.btn_confirm_confirm_dialog:
                CheckConnection checkConnection = new CheckConnection();
                if (checkConnection.isConnectingToInternet(getActivity()) == true) {
                    showProgressDialog();
                    // make dth recharge using native code
                    makeNativeRecharge();
                } else {
                    Utility.toast(getContextInstance(), "Check your internet connection");
                }
                dialog.dismiss();
                break;
            case R.id.tv_change_company:
                sharedPreferences.edit().putBoolean(constants.isClicked, true).commit();
                RechargeMainFragment rechargeMainFragment = new RechargeMainFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, rechargeMainFragment).addToBackStack(null).commit();
                break;
            case R.id.tv_change_product:
                /* [START] - 2017_05_02 - If company have only one products than by pressing on change product move to company */
                ArrayList<Product> selectedCompanyProductArrayList = new ArrayList<Product>();
                selectedCompanyProductArrayList = databaseHelper.getProductDetails(strCompanyId);
                if (selectedCompanyProductArrayList.size() == 1) {
                    sharedPreferences.edit().putBoolean(constants.isClicked, true).commit();
                    RechargeMainFragment rechargeMainFragment_1 = new RechargeMainFragment();
                    FragmentManager fragmentManager_1 = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction_1 = fragmentManager_1.beginTransaction();
                    fragmentTransaction_1.replace(R.id.container, rechargeMainFragment_1).addToBackStack(null).commit();
                } else {
                    sharedPreferences.edit().putBoolean(constants.isClicked, true).commit();
                    Bundle bundle = new Bundle();
                    bundle.putString("fragment_name", "DTH");
                    bundle.putString("company_id", strCompanyId);
                    bundle.putString("company_name", strCompanyName);
                    ProductFragment productFragment = new ProductFragment();
                    productFragment.setArguments(bundle);
                    FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
                    fragmentTransaction1.replace(R.id.container, productFragment).addToBackStack(null).commit();
                }
                // [END]
                break;
            case R.id.img_DTHRecharge_AllContacts:
                /* [START] - 2017_05_30 - Display contact application and select contact from them and display selected number. */
                if (Build.VERSION.SDK_INT >= 23) {
                    LogMessage.d("Device is marshmallow");
                    readContactPermission();
                } else {
                    LogMessage.d("Device is not marshmallow");
                    showContacts();
                }
                // [END]
                break;
        }

    }

    private void readContactPermission() {
        LogMessage.i("Checking permission.");
        // BEGIN_INCLUDE(READ_CONTACTS)
        // Check if the READ_CONTACTS permission is already available.
        if (ActivityCompat.checkSelfPermission(getContextInstance(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Phone state permission has not been granted.
            requestReadContactPermission();
        } else {
            // Read SMS permissions is already available, show the camera preview.
            LogMessage.i("Read contact permission has already been granted.");
            showContacts();
        }
        // END_INCLUDE(READ_PHONE_STATE)
    }

    /**
     * Requests the Read phone state permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestReadContactPermission() {
        LogMessage.i("Read phone state permission has NOT been granted. Requesting permission.");
        // BEGIN_INCLUDE(READ_PHONE_STATE)
        if (ActivityCompat.shouldShowRequestPermissionRationale(DTHRechargeFragment.this.getActivity(),
                Manifest.permission.READ_CONTACTS)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            LogMessage.i("Displaying READ_CONTACTS permission rationale to provide additional context.");
            // Force fully user to grand permission
            ActivityCompat.requestPermissions(DTHRechargeFragment.this.getActivity(), new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            // READ_CONTACTS permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(DTHRechargeFragment.this.getActivity(), new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        // END_INCLUDE(READ_PHONE_STATE)
    }

    private void showContacts() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            LogMessage.i("Received response for Read SMS permission request.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Read SMS permission has been granted
                LogMessage.i("Read SMS permission has now been granted.");
                // Ask user for grand READ_PHONE_STATE permission.
                readContactPermission();
            } else {
                LogMessage.i("Read SMS permission was NOT granted.");
                Utility.toast(context, "Until you grant the permission, we canot display the names");
                // again force fully prompt to user for grand permission.
                readContactPermission();
            }
            // END_INCLUDE(permission_result)
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    edtMobileNumber.setText("");
                    edtName.setText("");
                    Uri contactData = data.getData();
                    try {
                        String id = contactData.getLastPathSegment();
                        Cursor phoneCursor = getContextInstance().getContentResolver()
                                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                        new String[]{id},
                                        null);

                        final ArrayList<String> phonesList = new ArrayList<String>();
                        final ArrayList<String> phonesType = new ArrayList<String>();
                        while (phoneCursor.moveToNext()) {
                            // This would allow you get several phone numbers
                            // if the phone numbers were stored in an array
                            String phone = phoneCursor.getString(phoneCursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
                            String type = phoneCursor.getString(phoneCursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                            phonesList.add(phone);
                            phonesType.add(new Utility().getContactTypeName(type));
                        }
                        phoneCursor.close();

                        if (phonesList.size() == 0) {
                            displayPermissionError("Failed to get contact number.\nMake sure contact permission granted.");
                        } else if (phonesList.size() == 1) {
                            LogMessage.i("Contact No. : " + phonesList.get(0));
                            try {
                                String contactNumber = new Utility().formattedDTHNumber(phonesList.get(0));
                                if (contactNumber.trim().length() == 0) {
                                    Utility.toast(getContextInstance(), "Failed to get contact number.");
                                } else {
                                    edtMobileNumber.setText(contactNumber);
                                }
                            }
                            catch (Exception ex) {
                                LogMessage.e("Error in parse contact number.");
                                LogMessage.e("Error : " + ex.getMessage());
                                ex.printStackTrace();
                                Utility.toast(getContextInstance(), "Failed to get contact number.");
                                edtMobileNumber.setText("");
                                edtName.setText("");
                            }
                        } else {
                            final String[] allContactDetails = new String[phonesList.size()];
                            for (int i = 0; i < phonesList.size(); i++) {
                                allContactDetails[i] = phonesType.get(i) + " : " + phonesList.get(i);
                            }
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getContextInstance());
                            dialog.setTitle("Choose phone");
                            ((AlertDialog.Builder) dialog).setItems(allContactDetails,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            LogMessage.i("Selected number : " + allContactDetails[which]);
                                            String contactNumber = new Utility().formattedDTHNumber(allContactDetails[which]);
                                            if (contactNumber.trim().length() == 0) {
                                                Utility.toast(getContextInstance(), "Failed to get contact number.");
                                            } else {
                                                edtMobileNumber.setText(contactNumber);
                                            }
                                        }
                                    }).create();
                            dialog.show();
                        }
                    }
                    catch (Exception e) {
                        LogMessage.i("Failed to get phone data : " + e.getMessage());
                        e.printStackTrace();
                        Utility.toast(getContextInstance(), "Failed to get contact number.");
                    }
                }
            }
        }
    }

    private void displayPermissionError(String message) {
        try {
            alertDialog_Permission = new AlertDialog.Builder(getContextInstance()).create();
            alertDialog_Permission.setTitle("Contact permission request");
            alertDialog_Permission.setCancelable(true);
            alertDialog_Permission.setMessage(message);
            alertDialog_Permission.setButton("SETTINGS", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog_Permission.dismiss();
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getContextInstance().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                    catch (Exception ex) {
                        LogMessage.e("Error in open setting screen.");
                        LogMessage.e("Error : " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            });
            alertDialog_Permission.show();
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
    }

    /* [START] - 2017_04_24 - Add native code for add balance, and Remove volley code */
    /* [START] - 2017_05_31 - Get name using dth or mobile number from server and fill in name edit text */
    private void makeGetName() {
        // create new thread for get name by mobile number
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.number_tracer;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "mobile",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            strUserName,
                            strMacAddress,
                            strOtpCode,
                            strMobileNumber,
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    myHandler.obtainMessage(SUCCESS_NAME, response).sendToTarget();
                }
                catch (Exception ex) {
                    LogMessage.e("Error in get name native method");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    // parse name response
    private void parseNameResponse(String response) {
        LogMessage.i("Name Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("0")) {
            } else if (jsonObject.getString("status").equals("1")) {
                String encrypted_string = jsonObject.getString("data");
                JSONObject object = new JSONObject(decryptAPI(encrypted_string));
                String strName = object.getString("circle_id");
                edtName.setText(strName.trim());
            }
        }
        catch (JSONException e) {
            LogMessage.e("Error while parsing name response");
            LogMessage.e("Error : " + e.getMessage());
            e.printStackTrace();
        }
    }
    // [END]

    private void makeNativeDefaultState() {
        // create new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.number_tracer;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "mobile",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            strUserName,
                            strMacAddress,
                            strOtpCode,
                            strMobileNumber,
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    myHandler.obtainMessage(SUCCESS_STATE, response).sendToTarget();
                }
                catch (Exception ex) {
                    LogMessage.e("Error in get state native method");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    // parse success response
    private void parseStateResponse(String response) {
        LogMessage.i("Default Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("0")) {

            } else if (jsonObject.getString("status").equals("1")) {
                String encrypted_string = jsonObject.getString("data");
                JSONObject object = new JSONObject(decryptAPI(encrypted_string));
                String strDefaultCircleId = object.getString("circle_id");
                String strDefaultCircleName = "";
                for (int i = 0; i < stateArrayList.size(); i++) {
                    if (strDefaultCircleId.equals(stateArrayList.get(i).getCircle_id())) {
                        strDefaultCircleName = stateArrayList.get(i).getCircle_name();
                    }
                }
                for (int i = 0; i < adapterCircleName.getCount(); i++) {
                    if (strDefaultCircleName.trim().equals(adapterCircleName.getItem(i).toString())) {
                        spiCircle.setSelection(i);
                        break;
                    }
                }
            }
        }
        catch (JSONException e) {
            LogMessage.e("Error while parsing state response");
            LogMessage.e("Error : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void makeNativeRecharge() {
        /* [START] - 2017_05_30 - Add isCredit parameter recharge URL
         * Value = 1 or 0
         * If is credit check box is checked then pass 1
         * if is credit check box is un-checked then pass 0 */
        if (chkIsCredit.isChecked())
            isCreditStatus = "1";
        // get entered name from edit text
        if (!TextUtils.isEmpty(edtName.getText().toString()))
            name = edtName.getText().toString().trim();
        // [END]

        // create new thread for recharge
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set recharge url
                    String url = URL.recharge;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "amount",
                            "product",
                            "circle_id",
                            "mobile",
                            "operator",
                            "service_id",
                            "app",
                            "isCredit"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            strUserName,
                            strMacAddress,
                            strOtpCode,
                            strAmount,
                            strProductId,
                            strCircleId,
                            strMobileNumber,
                            strCompanyId,
                            "2",
                            Constants.APP_VERSION,
                            isCreditStatus
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    parseRechargeResponse(response);
                }
                catch (Exception ex) {
                    LogMessage.e("Error in get recharge native method");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR_RECHARGE, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    // method for parse recharge response
    private void parseRechargeResponse(String response) {
        LogMessage.i("Recharge Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("0")) {
                myHandler.obtainMessage(ERROR_RECHARGE, jsonObject.getString("msg")).sendToTarget();
            } else if (jsonObject.getString("status").equals("1")) {
                String encrypted_string = jsonObject.getString("data");
                String decryptedString = decryptAPI(encrypted_string);
                LogMessage.i("Response : " + encrypted_string);
                LogMessage.i("Decrypt : " + decryptedString);
                myHandler.obtainMessage(SUCCESS_RECHARGE, decryptedString).sendToTarget();
            } else {
                myHandler.obtainMessage(ERROR_RECHARGE, jsonObject.getString("msg")).sendToTarget();
            }
        }
        catch (JSONException ex) {
            LogMessage.e("Error in parse recharge response");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
            myHandler.obtainMessage(ERROR, "Recharge Error...").sendToTarget();
        }
    }

    // handle recharge messages
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == SUCCESS_RECHARGE) {
                dismissProgressDialog();
                parseSuccessRechargeResponse(msg.obj.toString());
            } else if (msg.what == ERROR_RECHARGE) {
                dismissProgressDialog();
                displayRechargeErrorDialog(msg.obj.toString());
            } else if (msg.what == SUCCESS_STATE) {
                dismissProgressDialog();
                parseStateResponse(msg.obj.toString());
            } else if (msg.what == ERROR) {
                dismissProgressDialog();
                Utility.toast(getContextInstance(), msg.obj.toString());
            } else if (msg.what == SUCCESS_NAME) {
                dismissProgressDialog();
                parseNameResponse(msg.obj.toString());
            }
        }
    };

    private void displayRechargeErrorDialog(String message) {
        /* [START] - 2017_05_01 - Close all alert dialog logic */
        try {
            alertDialog = new AlertDialog.Builder(getContextInstance()).create();
            alertDialog.setTitle("Recharge Failed");
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

    // Parse success response and display dialog and send notification
    private void parseSuccessRechargeResponse(String response) {
        try {
            JSONObject object = new JSONObject(response);
            String recharge_id = object.getString("rechargeid");
            String recharge_status = object.getString("recharge_status");
            String operator_id = object.getString("operatorid");
            String date_time = object.getString("datetime");
            String mo_no = object.getString("mobile");
            String company_id = object.getString("company");
            String product_id = object.getString("product");
            String service = object.getString("service");
            String amount = object.getString("amount");
            String margin = object.getString("margin");
            String balance = object.getString("balance");

            /* [START] - 2017_04_24 - Add RS symbol with amount & Add .00 after amount*/
            // Decimal format
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator('.');
            DecimalFormat format = new DecimalFormat("0.#");
            format.setDecimalFormatSymbols(symbols);
            // Add RS symbol in credit and debit amount
            try {
                if (!TextUtils.equals(amount, "0")) {
                    amount = " " + getContextInstance().getResources().getString(R.string.Rs) + "  " + format.parse(amount).floatValue();
                }
            }
            catch (Exception ex) {
                LogMessage.e("Error in decimal number");
                LogMessage.e("Error : " + ex.getMessage());
                ex.printStackTrace();
                amount = " " + getContextInstance().getResources().getString(R.string.Rs) + "  " + object.getString("amount");
            }
            // [END]

            // Show recharge success dialog
            showSuccessDialog(recharge_id, recharge_status, company_id, product_id, amount, mo_no, date_time);
            /* [START] - 2017_04_18 - change notification message */
            String notificationMessage = "";
            notificationMessage += recharge_id + "\n";
            notificationMessage += "Recharge Status : " + recharge_status + "\n";
            // notificationMessage += "Date Time : " + date_time + "\n";
            notificationMessage += "Smart Number : " + mo_no + "\n";
            notificationMessage += "Company : " + strCompanyName + "\n";
            notificationMessage += "Product : " + strProductName + "\n";
            notificationMessage += "Amount : " + amount;
            // sendNotification(recharge_id, date_time);
            sendNotification(notificationMessage, date_time);
            // [END]
        }
        catch (Exception ex) {
            LogMessage.e("Error in parse success message");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
            Utility.toast(getContextInstance(), "Recharge Error...");
        }
    }
    // [END]

    /*Method : decryptAPI Decrypt response of webservice*/
    public String decryptAPI(String response) {
        String user_id = defaultArrayList.get(0).getUser_id();
        MCrypt mCrypt = new MCrypt(user_id, strMacAddress);
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

    /*Method : showConfirmationDialog show dialog for confirmation of recharge details*/
    public void showConfirmationDialog() {
        dialog.setContentView(R.layout.dialog_confirmation);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        dialog.setTitle("Recharge Confirmation");
        TextView tv_company_name = (TextView) dialog.findViewById(R.id.tv_company_name_confirm_dialog);
        TextView tv_product_name = (TextView) dialog.findViewById(R.id.tv_product_name_confirm_dialog);
        TextView tv_amount = (TextView) dialog.findViewById(R.id.tv_amount_confirm_dialog);
        TextView tv_state = (TextView) dialog.findViewById(R.id.tv_state_confirm_dialog);
        TextView tv_mo_no = (TextView) dialog.findViewById(R.id.tv_mo_no_confirm_dialog);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel_confirm_dialog);
        Button btn_confirm = (Button) dialog.findViewById(R.id.btn_confirm_confirm_dialog);
        btn_confirm.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        TextView mobile_label = (TextView) dialog.findViewById(R.id.ev_mo_no_confirm_dialog);
        mobile_label.setText("Smart Number : ");

        LinearLayout circle_confirm_container = (LinearLayout) dialog.findViewById(R.id.circle_confirm_container);
        String circle_visibility = sharedPreferences.getString("circle_visibility", "0");
        if (circle_visibility.compareTo("0") == 0) {
            circle_confirm_container.setVisibility(View.GONE);
        }

        tv_company_name.setText(strCompanyName);
        tv_product_name.setText(strProductName);
        tv_amount.setText(strAmount);
        tv_state.setText(strCircle);
        tv_mo_no.setText(strMobileNumber);
        if (strMobileNumber.isEmpty()) {
            Utility.toast(getContextInstance(), "Enter Smart no.");
        } else if (strAmount.isEmpty()) {
            Utility.toast(getContextInstance(), "Enter Amount.");
        } else if (connection.isConnectingToInternet(getActivity()) == true) {
            dialog.show();
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Connection Error")
                    .setCancelable(false)
                    .setMessage("Please make sure your device is connected to internet")
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    /*Method : showSuccessDialog show dialog for successfully recharge */
    public void showSuccessDialog(String recharge_id, String recharge_status, String company_id, String product_id, String amount, String mo_no, String date_time) {
        dialog_success.setContentView(R.layout.dialog_success);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        TextView tv_recharge_id = (TextView) dialog_success.findViewById(R.id.tv_recharge_id);
        TextView tv_date_time = (TextView) dialog_success.findViewById(R.id.tv_date_time);
        TextView tv_recharge_status = (TextView) dialog_success.findViewById(R.id.tv_recharge_status);
        TextView tv_mo_no = (TextView) dialog_success.findViewById(R.id.tv_mo_no);
        TextView tv_company = (TextView) dialog_success.findViewById(R.id.tv_company);
        TextView tv_amount = (TextView) dialog_success.findViewById(R.id.tv_amount);
        TextView tv_product = (TextView) dialog_success.findViewById(R.id.tv_product);
        Button btn_ok = (Button) dialog_success.findViewById(R.id.btn_ok);
        tv_recharge_id.setText(recharge_id);
        tv_recharge_status.setText(recharge_status);
        tv_company.setText(strCompanyName);
        tv_product.setText(strProductName);
        tv_amount.setText(amount);
        tv_mo_no.setText(mo_no);
        tv_date_time.setText(date_time);

        TextView success_number = (TextView) dialog_success.findViewById(R.id.success_number);
        success_number.setText("Smart Number :");

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_success.dismiss();
                edtMobileNumber.setText("");
                edtAmount.setText("");
                edtName.setText("");
                chkIsCredit.setChecked(false);
            }
        });
        dialog_success.show();
    }

    /*Method : sendNotification send notification when recharge completed*/
    public void sendNotification(String recharge_id, String date_time) {
        new NotificationUtil(getContextInstance())
                .sendNotification("Recharge completed", "Recharge Id: " + recharge_id, date_time);
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
