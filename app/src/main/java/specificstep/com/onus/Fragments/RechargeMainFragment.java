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
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import specificstep.com.onus.Activities.HomeActivity;
import specificstep.com.onus.Activities.MainActivity;
import specificstep.com.onus.Database.DatabaseHelper;
import specificstep.com.onus.GlobalClasses.Constants;
import specificstep.com.onus.GlobalClasses.MCrypt;
import specificstep.com.onus.GlobalClasses.URL;
import specificstep.com.onus.Models.Default;
import specificstep.com.onus.Models.Product;
import specificstep.com.onus.Models.State;
import specificstep.com.onus.Models.User;
import specificstep.com.onus.R;
import specificstep.com.onus.utility.InternetUtil;
import specificstep.com.onus.utility.LogMessage;
import specificstep.com.onus.utility.Utility;

/**
 * Created by ubuntu on 16/1/17.
 */

public class RechargeMainFragment extends Fragment {

    private final int SUCCESS_BALANCE = 1, ERROR = 2, SUCCESS_MOBILE_PRODUCT = 3, SUCCESS_DTH_PRODUCT = 4,
            SUCCESS_STATE = 5;
    private Context context;
    // private LogMessage log;
    View view;
    TabLayout tabLayout;
    ViewPager viewPager;
    Adapter adapter;
    String PREF_KEY_CURRENT_TAB = "PREF_KEY_CURRENT_TAB";

    String balance;
    ArrayList<User> userArrayList;
    DatabaseHelper databaseHelper;
    SharedPreferences sharedPreferences;
    Constants constants;
    private int position = 0;
    ArrayList<Product> productArrayList;
    ArrayList<State> stateArrayList;

    // String[] language = {"C","C++","Java",".NET","iPhone","Android","ASP.NET","PHP"};

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recharge_main, null);

        /* [START] - 2017_04_18 set title bar as Mobile recharge */
        mainActivity().getSupportActionBar().setTitle("Recharge");
        // [END]

        context = RechargeMainFragment.this.getActivity();

        // initialise controls and variables
        initControls();
        // set listener
        setListener();

        // Display bottom bar in add balance
        mainActivity().displayRechargeBottomBar(true);

        // [START] - set notification counter, display unread message counter on notification icon
//        mainActivity().setNotificationCounter();
        // [END]

        return view;
    }

    private void initControls() {

        // log = new LogMessage(RechargeMainFragment.class.getSimpleName());
        // [START] - bind view
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        // [END]

        // [START] - initialise objects
        constants = new Constants();
        databaseHelper = new DatabaseHelper(context);
        userArrayList = new ArrayList<User>();
        adapter = new Adapter(getChildFragmentManager());
        productArrayList = new ArrayList<Product>();
        stateArrayList = new ArrayList<State>();
        sharedPreferences = context.getSharedPreferences(constants.SHAREEDPREFERENCE, Context.MODE_PRIVATE);
        // [END]

        // [START] - get user data from database
        userArrayList = databaseHelper.getUserDetail();
        // [END]

        // [START] - Setup view pager
        setupViewPager(viewPager);
        // [END]

        // [START] - set current view pager
        tabLayout.setupWithViewPager(viewPager);
        position = sharedPreferences.getInt(constants.SELECTED_TAB, 0);
        viewPager.setCurrentItem(position);
        // [END]

        // [START] - Get mobile product, DTH and state data from server
        LogMessage.i("Fetch Data : " + sharedPreferences.getString("fetch_data", ""));
        // Check fetch_data value from shared preference, if value is "1" then get data from server
        if (TextUtils.equals(sharedPreferences.getString("fetch_data", ""), "1")) {
            // get product data
            makeMobileProduct();
            // get DTH data
            makeDTHProduct();
            // get State data
            makeState();
        }
        // after getting mobile, DTH and state data change fetch_data value as "0"
        sharedPreferences.edit().putString("fetch_data", "0").commit();
        // [END]
    }

    private void setListener() {
        // [START] - Add tab selected listener to change tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // store current tab position in integer variable
                position = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        // [END]
    }

    private MenuItem menuItem;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // [START] - set option menu
        // Clear menu
        menu.clear();
        // Set menu
        inflater.inflate(R.menu.menu_main_activity, menu);
        menuItem = menu.findItem(R.id.action_balance_menu_main);
        // get current balance and display in action bar
        makeBalance();
        // [END]
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        // [START] - Set view pager
        // Add mobile recharge fragment
        adapter.addFragment(new MobileRecharge(), "Mobile Recharge");
        // Add DTH recharge fragment
        adapter.addFragment(new DTHRecharge(), "DTH Recharge");
        // set adapter in view pager
        viewPager.setAdapter(adapter);
        // [END]
    }

    /***
     * Adapter for view pager
     */
    static class Adapter extends FragmentStatePagerAdapter {
        // Create variables for fragment list and fragment title list.
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        /**
         * This method is use to add fragments in FragmentStatePagerAdapter
         *
         * @param fragment Object of fragment
         * @param title    Title of fragment
         */
        public void addFragment(Fragment fragment, String title) {
            // Add fragment object in Fragment type list
            mFragments.add(fragment);
            // Add fragment title in String type list
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        position = 0;
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    return true;
                }

                return false;
            }
        });
    }

    /* [START] - 2017_04_28 - Add native code for update balance, and Remove volley code */
    private void makeBalance() {
        // create new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.balance;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            userArrayList.get(0).getUser_name(),
                            userArrayList.get(0).getDevice_id(),
                            userArrayList.get(0).getOtp_code(),
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    myHandler.obtainMessage(SUCCESS_BALANCE, response).sendToTarget();
                }
                catch (Exception ex) {
                    LogMessage.e("Error in Cash book native method");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    // myHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    // parse success response
    private void parseBalanceResponse(String response) {
        LogMessage.i("balance Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {

                String encrypted_response = jsonObject.getString("data");
                String decrypted_data = decryptAPI(encrypted_response);
                JSONObject object = new JSONObject(decrypted_data);
                balance = object.getString("balance");
                menuItem.setTitle("\u20B9  " + balance);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void makeMobileProduct() {
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
        LogMessage.i("Product List Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String encrypted_string = jsonObject.getString("data");
                decryptAPI(encrypted_string);
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
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // DTH Product
    private void makeDTHProduct() {
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
        LogMessage.i("Product List Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String encrypted_string = jsonObject.getString("data");
                decryptAPI(encrypted_string);
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
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
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

    // get State data
    private void makeState() {
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
        LogMessage.i("State List Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String encrypted_string = jsonObject.getString("data");
                decryptAPI(encrypted_string);
                JSONObject jsonObject1 = new JSONObject(decryptAPI(encrypted_string));
                JSONArray jsonArray = jsonObject1.getJSONArray("state");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    State state = new State();
                    state.setCircle_id(jsonObject2.getString("circle_id"));
                    state.setCircle_name(jsonObject2.getString("circle_name"));
                    stateArrayList.add(state);
                }
                databaseHelper.deleteStateDetail();
                databaseHelper.addStatesDetails(stateArrayList);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // handle thread messages
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == SUCCESS_BALANCE) {
                parseBalanceResponse(msg.obj.toString());
            } else if (msg.what == SUCCESS_MOBILE_PRODUCT) {
                parseMobileProductResponse(msg.obj.toString());
            } else if (msg.what == SUCCESS_DTH_PRODUCT) {
                parseDTHProductResponse(msg.obj.toString());
            } else if (msg.what == SUCCESS_STATE) {
                parseStateResponse(msg.obj.toString());
            } else if (msg.what == ERROR) {
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

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.edit().putInt(constants.SELECTED_TAB, position).commit();
    }
}