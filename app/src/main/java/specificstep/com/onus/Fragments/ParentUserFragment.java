package specificstep.com.onus.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import specificstep.com.onus.Adapters.ParentUserAdapter;
import specificstep.com.onus.Database.DatabaseHelper;
import specificstep.com.onus.GlobalClasses.Constants;
import specificstep.com.onus.GlobalClasses.MCrypt;
import specificstep.com.onus.GlobalClasses.TransparentProgressDialog;
import specificstep.com.onus.GlobalClasses.URL;
import specificstep.com.onus.Models.Default;
import specificstep.com.onus.Models.ParentUserModel;
import specificstep.com.onus.Models.User;
import specificstep.com.onus.R;
import specificstep.com.onus.utility.InternetUtil;
import specificstep.com.onus.utility.LogMessage;
import specificstep.com.onus.utility.Utility;

public class ParentUserFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ParentUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ParentUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ParentUserFragment newInstance(String param1, String param2) {
        ParentUserFragment fragment = new ParentUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /* [START] - 2017_05_02 - All Variables and Objects */
    private final int SUCCESS = 1, ERROR = 2;
    private View view;
    private Context context;
    // Custom log message class
    // private LogMessage log;
    // All static variables class
    private Constants constants;
    // Custom progress bar
    private TransparentProgressDialog transparentProgressDialog;
    private AlertDialog alertDialog;
    // Database class
    private DatabaseHelper databaseHelper;
    // All array lists
    private ArrayList<User> userArrayList;
    // All string variables
    private String strMacAddress, strUserName, strOtpCode, strRegistrationDateTime;
    // Controls objects
    private ListView lstParentUserDetails;
    private LinearLayout llParentUserDetails;
    private TextView txtNoMoreData, txtFirmName, txtMobileNo, txtUserType, txtName;
    // [END]

    private Context getContextInstance() {
        if (context == null) {
            context = ParentUserFragment.this.getActivity();
            return context;
        } else {
            return context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_parent_user, container, false);
        context = ParentUserFragment.this.getActivity();

        initControls();

        showProgressDialog();
        makeGetParentUserDetails();

        return view;
    }

    private void initControls() {
        /* [START] - Initialise class objects */
        // log = new LogMessage(ParentUserFragment.class.getSimpleName());
        constants = new Constants();
        transparentProgressDialog = new TransparentProgressDialog(getContextInstance(), R.drawable.fotterloading);
        databaseHelper = new DatabaseHelper(getContextInstance());
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
        lstParentUserDetails = (ListView) view.findViewById(R.id.lst_ParentUser_ParentUserDetails);
        llParentUserDetails = (LinearLayout) view.findViewById(R.id.ll_ParentUser);
        txtNoMoreData = (TextView) view.findViewById(R.id.txt_ParentUser_NoMoreData);
        txtFirmName = (TextView) view.findViewById(R.id.txt_ParentUser_FirmName);
        txtName = (TextView) view.findViewById(R.id.txt_ParentUser_Name);
        txtMobileNo = (TextView) view.findViewById(R.id.txt_ParentUser_MobileNo);
        txtUserType = (TextView) view.findViewById(R.id.txt_ParentUser_UserType);
        // [END]
    }

    /* [START] - 2017_05_02 - Get parent user details */
    private void makeGetParentUserDetails() {
        // create new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.GET_PARENT_USER_DETAILS;
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
                            strMacAddress,
                            strOtpCode,
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    myHandler.obtainMessage(SUCCESS, response).sendToTarget();
                }
                catch (Exception ex) {
                    LogMessage.e("Error in get parent user details native method");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    // parse success response
    private void parseSuccessResponse(String response) {
        LogMessage.i("Parent User Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                llParentUserDetails.setVisibility(View.VISIBLE);
                String encrypted_response = jsonObject.getString("data");
                String message = jsonObject.getString("msg");
                LogMessage.d("Parent User : " + "Message : " + message);
                try {
                    String decrypted_response = decryptAPI(encrypted_response);
                    LogMessage.d("Parent User : " + "Response : " + decrypted_response);
                    loadData(decrypted_response);
                }
                catch (Exception ex) {
                    myHandler.obtainMessage(ERROR, "Parent user data not found.").sendToTarget();
                }
            } else {
                myHandler.obtainMessage(ERROR, jsonObject.getString("msg")).sendToTarget();
            }
        }
        catch (JSONException e) {
            LogMessage.d("Cashbook : " + "Error 4 : " + e.getMessage());
            Utility.toast(getContextInstance(), "No result found");
            e.printStackTrace();
        }
    }

    public void loadData(String response) {
        try {
            ArrayList<ParentUserModel> parentUserModels = new ArrayList<ParentUserModel>();
            JSONObject jsonObject1 = new JSONObject(response);
            JSONArray jsonArray = jsonObject1.getJSONArray("userdata");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                ParentUserModel parentUserModel = new ParentUserModel();
                parentUserModel.position = i + 1 + "";
                parentUserModel.firmName = object.getString("firm_name");
                parentUserModel.mobileNumber = object.getString("phone_no");
                parentUserModel.userType = object.getString("usertype");
                // add name -   first_name     last_name
                String fullName = " - ";
                try {
                    fullName = object.getString("first_name") + " " + object.getString("last_name");
                }
                catch (Exception ex) {
                    fullName = " - ";
                }
                parentUserModel.name = fullName;

                parentUserModels.add(parentUserModel);
            }
            if (parentUserModels != null) {
                if (parentUserModels.size() > 0) {
                    ParentUserAdapter parentUserAdapter = new ParentUserAdapter(getContextInstance(), parentUserModels);
                    lstParentUserDetails.setAdapter(parentUserAdapter);
                    lstParentUserDetails.setVisibility(View.VISIBLE);
                    txtNoMoreData.setVisibility(View.GONE);
                } else {
                    lstParentUserDetails.setVisibility(View.GONE);
                    txtNoMoreData.setVisibility(View.VISIBLE);
                }
            } else {
                lstParentUserDetails.setVisibility(View.GONE);
                txtNoMoreData.setVisibility(View.VISIBLE);
            }

            // display user login details
            JSONObject jsonObject = new JSONObject(response);
            String details = jsonObject.getString("details");
            JSONObject detailsObject = new JSONObject(details);

            txtFirmName.setText("Firm Name : " + detailsObject.getString("firm_name"));
            String fullName = " - ";
            try {
                fullName = detailsObject.getString("first_name") + " " + detailsObject.getString("last_name");
            }
            catch (Exception ex) {
                fullName = " - ";
            }
            txtName.setText("Name : " + fullName);
            txtMobileNo.setText("Mobile No : " + detailsObject.getString("phone_no"));
            txtUserType.setText("User Type : " + detailsObject.getString("usertype"));
        }
        catch (JSONException e) {
            e.printStackTrace();
            LogMessage.d("Cashbook : " + "Error : " + e.toString());
            new AlertDialog.Builder(getContextInstance())
                    .setTitle("Alert!")
                    .setCancelable(false)
                    .setMessage(getResources().getString(R.string.alert_servicer_down))
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
            lstParentUserDetails.setVisibility(View.GONE);
            txtNoMoreData.setVisibility(View.VISIBLE);
        }
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
    // [END]

    /* Method : decryptAPI Decrypt response of webservice */
    public String decryptAPI(String response) throws Exception {
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
