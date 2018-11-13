package specificstep.com.onus.Activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import specificstep.com.onus.Adapters.NavigationDrawerAdapter;
import specificstep.com.onus.Database.DatabaseHelper;
import specificstep.com.onus.Database.NotificationTable;
import specificstep.com.onus.Fragments.AccountLedgerFragment;
import specificstep.com.onus.Fragments.BrowsePlansFragment;
import specificstep.com.onus.Fragments.CashbookFragment;
import specificstep.com.onus.Fragments.ChangePasswordFragment;
import specificstep.com.onus.Fragments.ComplainReportFragment;
import specificstep.com.onus.Fragments.NotificationFragment;
import specificstep.com.onus.Fragments.ParentUserFragment;
import specificstep.com.onus.Fragments.RecentTransactionFragment;
import specificstep.com.onus.Fragments.RechargeFragment;
import specificstep.com.onus.Fragments.RechargeMainFragment;
import specificstep.com.onus.Fragments.TransSearchFragment;
import specificstep.com.onus.Fragments.UpdateData;
import specificstep.com.onus.GlobalClasses.Constants;
import specificstep.com.onus.GlobalClasses.MCrypt;
import specificstep.com.onus.GlobalClasses.URL;
import specificstep.com.onus.Models.Default;
import specificstep.com.onus.Models.User;
import specificstep.com.onus.R;
import specificstep.com.onus.utility.InternetUtil;
import specificstep.com.onus.utility.LogMessage;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private final int SUCCESS_BALANCE = 1, ERROR = 2;
    /* [START] - Other class objects */
    private Constants constants;
    private Context context;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper databaseHelper;
    // [END]

    /* [START] - Controls objects */
    private ActionBarDrawerToggle toggle;
    private TextView tv_balance, tv_email, tv_name;
    // Default bottom bar
    private LinearLayout llDefault, llUpdate, llChangePassword, llNotification, llLogout;
    private TextView txtDefaultTotalNotification;
    // Notification bottom bar
    private LinearLayout llNotificationBottom, llNotificationRecharge, llNotificationRecentTransaction, llNotificationTransactionSearch;
    // Cashbook bottom bar
    private LinearLayout llCashbookBottom, llCashbookRecharge, llCashbookRecentTrasaction, llCashbookNotification;
    private TextView txtCashbookTotalNotification;
    // Change password bottom bar
    private LinearLayout llChangePasswordBottom, llChangePasswordRecharge, llChangePasswordRecentTransaction, llChangePasswordNotification;
    private TextView txtChangePasswordTotalNotification;
    // Recharge bottom bar
    private LinearLayout llRechargeBottom, llRechargeRecentTransaction, llRechargeTransaction, llRechargeNotification;
    private TextView txtRechargeTotalNotification;
    // Transaction search
    private LinearLayout llTransactionSearchBottom, llTransactionSearchRecharge, llTransactionSearchRecentTransaction, llTransactionSearchNotification;
    private TextView txtTransactionSearchTotalNotification;
    // Recent transaction
    private LinearLayout llRecentTransactionBottom, llRecentTransactionRecharge, llRecentTransactionTransactionSearch, llRecentTransactionNotification;
    private TextView txtRecentTransactionTotalNotification;
    // [END]

    /* [START] - Variables */
    private String balance;
    private int position;
    private ArrayList<User> userArrayList;
    // Screen Number
    private final int NOTIFICATION = 8, CHANGE_PASSWORD = 7, UPDATE_DATA = 6, ACCOUNT_LEGER = 5 , CASH_BOOK = 4, COMPLAIN_REPORT = 3, TRANSACTION_SEARCH = 2, RECENT_TRANSACTION = 1, RECHARGE = 0;
    // [END]

    // Notification receiver
    private BroadcastReceiver notificationReceiver = null;
    public static final String ACTION_REFRESH_HOMEACTIVITY = "specificstep.com.metroenterprise.REFRESH_NOTIFICATION";

    // Navigation view
    private NavigationView navigationView = null;
    private DrawerLayout drawer = null;

    // Custom navigation menu
    private ListView lstNavigation;

    // all menu names
    private final String MENU_HOME = "Home";
    private final String MENU_RECHARGE = "Recharge";
    private final String MENU_RECENT_TRANSACTION = "Recent Transaction";
    private final String MENU_TRANSACTION_SEARCH = "Transaction Search";
    private final String MENU_COMPLAIN_REPORT = "Complain Report";
    private final String MENU_CASH_BOOK = "Cash Book";
    private final String MENU_ACCOUNT_LEDGER = "Account Ledger";
    private final String MENU_UPDATE_DATA = "Update Data";
    private final String MENU_CHANGE_PASSWORD = "Change Password";
    private final String MENU_NOTIFICATION = "Notification";
    private final String MENU_PARENT_USER = "Parent User";
    private final String MENU_LOGOUT = "Log Out";


    private Context getContextInstance() {
        if (context == null) {
            context = HomeActivity.this;
            return context;
        } else {
            return context;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = HomeActivity.this;

        initControls();
        getBundleData();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerStateChanged(int newState) {
                makeBalance();
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        setCustomNavigation();
        makeBalance();

        tv_name = (TextView) header.findViewById(R.id.tv_header_name);
        tv_email = (TextView) header.findViewById(R.id.tv_header_email);
        tv_balance = (TextView) header.findViewById(R.id.tv_header_balance);

        initBottomNavigation();

        /* Set header content of navigation drawer */
        tv_email.setText(userArrayList.get(0).getUser_name());
        tv_name.setText(userArrayList.get(0).getName());

        /* Set Current Fragment according to selected item from MainActivity */
        if (databaseHelper.checkEmpty() == false) {
            position = 6;
            displayBottomNavigationDynamic(UPDATE_DATA);
        }
        if (position == 0) {
            //openFragment(MENU_RECHARGE, new RechargeMainFragment());
            openFragment("Mobile Recharge", new RechargeFragment()); //skip above and direct open this fragment
            displayBottomNavigationDynamic(RECHARGE);
        } else if (position == 1) {
            // openFragment(MENU_RECENT_TRANSACTION, new RecentTransactionFragment());
            openRecentTransactionFragment();
            displayBottomNavigationDynamic(RECENT_TRANSACTION);
        } else if (position == 2) {
            openFragment(MENU_TRANSACTION_SEARCH, new TransSearchFragment());
            displayBottomNavigationDynamic(TRANSACTION_SEARCH);
        } else if (position == 3) {
            openFragment(MENU_COMPLAIN_REPORT, new ComplainReportFragment());
            displayBottomNavigationDynamic(COMPLAIN_REPORT);
        } else if (position == 4) {
            openFragment(MENU_CASH_BOOK, new CashbookFragment());
            displayBottomNavigationDynamic(CASH_BOOK);
        } else if (position == 5) {
          //  Toast.makeText(HomeActivity.this,"coming soon!",Toast.LENGTH_LONG).show();
            openFragment(MENU_ACCOUNT_LEDGER, new AccountLedgerFragment());
            displayBottomNavigationDynamic(ACCOUNT_LEGER);
        } else if (position == 6) {
            openFragment(MENU_UPDATE_DATA, new UpdateData());
            displayBottomNavigationDynamic(UPDATE_DATA);
        } else if (position == 7) {
            openFragment(MENU_CHANGE_PASSWORD, new ChangePasswordFragment());
            displayBottomNavigationDynamic(CHANGE_PASSWORD);
        } else if (position == 8) {
            openFragment(MENU_NOTIFICATION, new NotificationFragment());
            displayBottomNavigationDynamic(NOTIFICATION);
        }
        // Display number of unread message
        setNotificationCounter();
        // start notification update timer
        // countDownTimer.start();
        getNotificationIntentData();
        checkForUpdateData();
    }

    private void checkForUpdateData() {
        Intent intent = getIntent();
        if (intent.getStringExtra(constants.KEY_REQUIRE_UPDATE) != null
                && !TextUtils.isEmpty(intent.getStringExtra(constants.KEY_REQUIRE_UPDATE))) {
            String requireUpdate = intent.getStringExtra(constants.KEY_REQUIRE_UPDATE);
            LogMessage.d("Require update : " + requireUpdate);

            // Open update fragment
            if (TextUtils.equals(requireUpdate, "1")) {
                FragmentTransaction fragment = getSupportFragmentManager().beginTransaction();
                UpdateData updateData1 = new UpdateData();
                Bundle bundle = new Bundle();
                bundle.putString(constants.KEY_REQUIRE_UPDATE, requireUpdate);
                updateData1.setArguments(bundle);
                fragment.replace(R.id.container, updateData1);
                fragment.commit();
                displayBottomNavigationDynamic(UPDATE_DATA);
            }
        }
    }

    private void initBottomNavigation() {
        /* [START] - Bottom navigation menu */
        // Default Bottom navigation controls
        llDefault = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_Default);
        llLogout = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_Default_Logout);
        llChangePassword = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_Default_ChangePassword);
        llNotification = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_Default_Notification);
        llUpdate = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_Default_Update);
        // Unread notification counter
        txtDefaultTotalNotification = (TextView) findViewById(R.id.txt_Home_BottomNavigation_Default_TotalNotification);
        llLogout.setOnClickListener(this);
        llChangePassword.setOnClickListener(this);
        llNotification.setOnClickListener(this);
        llUpdate.setOnClickListener(this);

        // Notification Bottom navigation controls
        llNotificationBottom = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_Notification);
        llNotificationRecharge = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_Notification_Recharge);
        llNotificationRecentTransaction = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_Notification_RecentTransaction);
        llNotificationTransactionSearch = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_Notification_TransactionSearch);
        llNotificationRecharge.setOnClickListener(this);
        llNotificationRecentTransaction.setOnClickListener(this);
        llNotificationTransactionSearch.setOnClickListener(this);

        // Cashbook Bottom navigation controls
        llCashbookBottom = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_Cashbook);
        llCashbookRecharge = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_Cashbook_Recharge);
        llCashbookRecentTrasaction = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_Cashbook_RecentTransaction);
        llCashbookNotification = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_Cashbook_Notification);
        // Unread notification counter
        txtCashbookTotalNotification = (TextView) findViewById(R.id.txt_Home_BottomNavigation_Cashbook_TotalNotification);
        llCashbookRecharge.setOnClickListener(this);
        llCashbookRecentTrasaction.setOnClickListener(this);
        llCashbookNotification.setOnClickListener(this);

        // Change password Bottom navigation controls
        llChangePasswordBottom = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_ChangePassword);
        llChangePasswordRecharge = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_ChangePassword_Recharge);
        llChangePasswordRecentTransaction = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_ChangePassword_RecentTrasaction);
        llChangePasswordNotification = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_ChangePassword_Notification);
        // Unread notification counter
        txtChangePasswordTotalNotification = (TextView) findViewById(R.id.txt_Home_BottomNavigation_ChangePassword_TotalNotification);
        llChangePasswordRecharge.setOnClickListener(this);
        llChangePasswordRecentTransaction.setOnClickListener(this);
        llChangePasswordNotification.setOnClickListener(this);

        // Recharge Bottom navigation controls
        llRechargeBottom = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_Recharge);
        llRechargeTransaction = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_Recharge_TransactionSearch);
        llRechargeRecentTransaction = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_Recharge_RecentTrasaction);
        llRechargeNotification = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_Recharge_Notification);
        // Unread notification counter
        txtRechargeTotalNotification = (TextView) findViewById(R.id.txt_Home_BottomNavigation_Recharge_TotalNotification);
        llRechargeTransaction.setOnClickListener(this);
        llRechargeRecentTransaction.setOnClickListener(this);
        llRechargeNotification.setOnClickListener(this);

        // Transaction search Bottom navigation controls
        llTransactionSearchBottom = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_TransactionSearch);
        llTransactionSearchRecharge = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_TransactionSearch_Recharge);
        llTransactionSearchRecentTransaction = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_TransactionSearch_RecentTransaction);
        llTransactionSearchNotification = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_TransactionSearch_Notification);
        // Unread notification counter
        txtTransactionSearchTotalNotification = (TextView) findViewById(R.id.txt_Home_BottomNavigation_TransactionSearch_TotalNotification);
        llTransactionSearchRecharge.setOnClickListener(this);
        llTransactionSearchRecentTransaction.setOnClickListener(this);
        llTransactionSearchNotification.setOnClickListener(this);

        // Recent transaction Bottom navigation controls
        llRecentTransactionBottom = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_RecentTransaction);
        llRecentTransactionRecharge = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_RecentTransaction_Recharge);
        llRecentTransactionTransactionSearch = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_RecentTransaction_TransactionSearch);
        llRecentTransactionNotification = (LinearLayout) findViewById(R.id.ll_Home_BottomNavigation_RecentTransaction_Notification);
        // Unread notification counter
        txtRecentTransactionTotalNotification = (TextView) findViewById(R.id.txt_Home_BottomNavigation_RecentTransaction_TotalNotification);
        llRecentTransactionRecharge.setOnClickListener(this);
        llRecentTransactionTransactionSearch.setOnClickListener(this);
        llRecentTransactionNotification.setOnClickListener(this);
        // [END]
    }

    /* [START] -  Change bottom navigation dynamic */
    private void displayNotificationBottomBar() {
        llDefault.setVisibility(View.GONE);
        llNotificationBottom.setVisibility(View.VISIBLE);
        llCashbookBottom.setVisibility(View.GONE);
        llChangePasswordBottom.setVisibility(View.GONE);
        llRechargeBottom.setVisibility(View.GONE);
        llTransactionSearchBottom.setVisibility(View.GONE);
        llRecentTransactionBottom.setVisibility(View.GONE);
    }

    private void displayDefaultBottomBar() {
        llDefault.setVisibility(View.VISIBLE);
        llNotificationBottom.setVisibility(View.GONE);
        llCashbookBottom.setVisibility(View.GONE);
        llChangePasswordBottom.setVisibility(View.GONE);
        llRechargeBottom.setVisibility(View.GONE);
        llTransactionSearchBottom.setVisibility(View.GONE);
        llRecentTransactionBottom.setVisibility(View.GONE);
    }

    private void displayCashbookBottomBar() {
        llDefault.setVisibility(View.GONE);
        llNotificationBottom.setVisibility(View.GONE);
        llCashbookBottom.setVisibility(View.VISIBLE);
        llChangePasswordBottom.setVisibility(View.GONE);
        llRechargeBottom.setVisibility(View.GONE);
        llTransactionSearchBottom.setVisibility(View.GONE);
        llRecentTransactionBottom.setVisibility(View.GONE);
    }

    private void displayChangePasswordBottomBar() {
        llDefault.setVisibility(View.GONE);
        llNotificationBottom.setVisibility(View.GONE);
        llCashbookBottom.setVisibility(View.GONE);
        llChangePasswordBottom.setVisibility(View.VISIBLE);
        llRechargeBottom.setVisibility(View.GONE);
        llTransactionSearchBottom.setVisibility(View.GONE);
        llRecentTransactionBottom.setVisibility(View.GONE);
    }

    private void displayUpdateDataBottomBar() {
        llDefault.setVisibility(View.GONE);
        llNotificationBottom.setVisibility(View.GONE);
        llCashbookBottom.setVisibility(View.GONE);
        llChangePasswordBottom.setVisibility(View.GONE);
        llRechargeBottom.setVisibility(View.GONE);
        llTransactionSearchBottom.setVisibility(View.GONE);
        llRecentTransactionBottom.setVisibility(View.GONE);
    }

    private void displayRechargeBottomBar() {
        llDefault.setVisibility(View.GONE);
        llNotificationBottom.setVisibility(View.GONE);
        llCashbookBottom.setVisibility(View.GONE);
        llChangePasswordBottom.setVisibility(View.GONE);
        llRechargeBottom.setVisibility(View.VISIBLE);
        llTransactionSearchBottom.setVisibility(View.GONE);
        llRecentTransactionBottom.setVisibility(View.GONE);
    }

    public void displayRechargeBottomBar(boolean displayBottom) {
        llDefault.setVisibility(View.GONE);
        llNotificationBottom.setVisibility(View.GONE);
        llCashbookBottom.setVisibility(View.GONE);
        llChangePasswordBottom.setVisibility(View.GONE);
        llTransactionSearchBottom.setVisibility(View.GONE);
        llRecentTransactionBottom.setVisibility(View.GONE);
        if (displayBottom) {
            llRechargeBottom.setVisibility(View.VISIBLE);
        } else {
            llRechargeBottom.setVisibility(View.GONE);
        }
    }

    private void displayTransactionSearch() {
        llDefault.setVisibility(View.GONE);
        llNotificationBottom.setVisibility(View.GONE);
        llCashbookBottom.setVisibility(View.GONE);
        llChangePasswordBottom.setVisibility(View.GONE);
        llRechargeBottom.setVisibility(View.GONE);
        llTransactionSearchBottom.setVisibility(View.VISIBLE);
        llRecentTransactionBottom.setVisibility(View.GONE);
    }

    private void displayRecentTransactionSearch() {
        llDefault.setVisibility(View.GONE);
        llNotificationBottom.setVisibility(View.GONE);
        llCashbookBottom.setVisibility(View.GONE);
        llChangePasswordBottom.setVisibility(View.GONE);
        llRechargeBottom.setVisibility(View.GONE);
        llTransactionSearchBottom.setVisibility(View.GONE);
        llRecentTransactionBottom.setVisibility(View.VISIBLE);
    }

    // [START] - Remove bottom navigation and drawer in update screen. */
    private void displayBottomNavigationDynamic(int fragmentNumber) {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        if (fragmentNumber == NOTIFICATION) {
            displayNotificationBottomBar();
        } else if (fragmentNumber == CASH_BOOK) {
            displayCashbookBottomBar();
        } else if (fragmentNumber == CHANGE_PASSWORD) {
            displayChangePasswordBottomBar();
        } else if (fragmentNumber == UPDATE_DATA) {
            displayUpdateDataBottomBar();
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else if (fragmentNumber == RECHARGE) {
            displayRechargeBottomBar();
        } else if (fragmentNumber == TRANSACTION_SEARCH) {
            displayTransactionSearch();
        } else if (fragmentNumber == RECENT_TRANSACTION) {
            displayRecentTransactionSearch();
        } else {
            displayDefaultBottomBar();
        }
    }
    // [END]

    // set custom notification
    private void setCustomNavigation() {
        try {
            lstNavigation = (ListView) findViewById(R.id.lst_NavigationView);
            ArrayList<String> stringArrayList = new ArrayList<String>();
            // All menu items name
            stringArrayList.add(MENU_HOME);
            stringArrayList.add(MENU_RECHARGE);
            stringArrayList.add(MENU_RECENT_TRANSACTION);
            stringArrayList.add(MENU_TRANSACTION_SEARCH);
            stringArrayList.add(MENU_COMPLAIN_REPORT);
            stringArrayList.add(MENU_CASH_BOOK);
            stringArrayList.add(MENU_ACCOUNT_LEDGER);
            stringArrayList.add(MENU_UPDATE_DATA);
            stringArrayList.add(MENU_CHANGE_PASSWORD);
            stringArrayList.add(MENU_NOTIFICATION);
            stringArrayList.add(MENU_PARENT_USER);
            stringArrayList.add(MENU_LOGOUT);
            final NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(getContextInstance(), stringArrayList);
            lstNavigation.setAdapter(adapter);

            lstNavigation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedMenuName = adapter.getData(position);
                    // Handle navigation view item clicks here.
                    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                    setSupportActionBar(toolbar);

                    if (TextUtils.equals(selectedMenuName, MENU_HOME)) {
                        Intent intent = new Intent(getContextInstance(), MainActivity.class);
                        startActivity(intent);
                    } else if (TextUtils.equals(selectedMenuName, MENU_RECHARGE)) {
                        //openFragment(MENU_RECHARGE, new RechargeMainFragment());
                        sharedPreferences.edit().putString(constants.MOBILENUMBER, "").commit();
                        sharedPreferences.edit().putString(constants.AMOUNT, "").commit();

                        openFragment("Mobile Recharge", new RechargeFragment()); //skip above and direct open this fragment
                        displayBottomNavigationDynamic(RECHARGE);
                    } else if (TextUtils.equals(selectedMenuName, MENU_RECENT_TRANSACTION)) {
                        openFragment(MENU_RECENT_TRANSACTION, new RecentTransactionFragment());
                        displayBottomNavigationDynamic(RECENT_TRANSACTION);
                    }else if (TextUtils.equals(selectedMenuName, MENU_TRANSACTION_SEARCH)) {
                        openFragment(MENU_TRANSACTION_SEARCH, new TransSearchFragment());
                        displayBottomNavigationDynamic(TRANSACTION_SEARCH);
                    } else if (TextUtils.equals(selectedMenuName, MENU_COMPLAIN_REPORT)) {
                        openFragment(MENU_COMPLAIN_REPORT, new ComplainReportFragment());
                        displayBottomNavigationDynamic(COMPLAIN_REPORT);
                    }  else if (TextUtils.equals(selectedMenuName, MENU_CASH_BOOK)) {
                        openFragment(MENU_CASH_BOOK, new CashbookFragment());
                        displayBottomNavigationDynamic(CASH_BOOK);
                    }else if (TextUtils.equals(selectedMenuName, MENU_ACCOUNT_LEDGER)) {
                        //Toast.makeText(HomeActivity.this,"coming soon!",Toast.LENGTH_LONG).show();

                        openFragment(MENU_ACCOUNT_LEDGER, new AccountLedgerFragment());
                        displayBottomNavigationDynamic(ACCOUNT_LEGER);
                    } else if (TextUtils.equals(selectedMenuName, MENU_UPDATE_DATA)) {
                        openFragment(MENU_UPDATE_DATA, new UpdateData());
                        displayBottomNavigationDynamic(UPDATE_DATA);
                    } else if (TextUtils.equals(selectedMenuName, MENU_CHANGE_PASSWORD)) {
                        openFragment(MENU_CHANGE_PASSWORD, new ChangePasswordFragment());
                        displayBottomNavigationDynamic(CHANGE_PASSWORD);
                    } else if (TextUtils.equals(selectedMenuName, MENU_NOTIFICATION)) {
                        openFragment(MENU_NOTIFICATION, new NotificationFragment());
                        displayBottomNavigationDynamic(NOTIFICATION);
                    } else if (TextUtils.equals(selectedMenuName, MENU_PARENT_USER)) {
                        ParentUserFragment parentUserFragment = ParentUserFragment.newInstance("1", "2");
                        openFragment(MENU_PARENT_USER, parentUserFragment);
                        displayBottomNavigationDynamic(RECHARGE);
                    } else if (TextUtils.equals(selectedMenuName, MENU_LOGOUT)) {
                        sharedPreferences.edit().clear().commit();
                        Intent intent1 = new Intent(getContextInstance(), LoginActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent1);
                        finish();
                        sharedPreferences.edit().putBoolean(constants.LOGOUT, true).commit();
                    }
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                }
            });
        }
        catch (Exception ex) {
            LogMessage.e("Error in Custom navigation menu.");
            LogMessage.d("Error : " + ex.toString());
            ex.printStackTrace();
        }
    }

    private void initControls() {
        /* [START] - Initialise class objects */
        constants = new Constants();
        sharedPreferences = getSharedPreferences(constants.SHAREEDPREFERENCE, Context.MODE_PRIVATE);
        databaseHelper = new DatabaseHelper(getContextInstance());
        // [END]
        /* [START] - get user data from database and store into array list */
        userArrayList = databaseHelper.getUserDetail();
        // [END]
        registerNotificationReceiver();
    }

    private void registerNotificationReceiver() {
        /* [START] - Create custom notification for receiver notification data */
        try {
            if (notificationReceiver == null) {
                // Add notification filter
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(ACTION_REFRESH_HOMEACTIVITY);
                // Create notification object
                notificationReceiver = new CheckNotification();
                // Register receiver
                HomeActivity.this.registerReceiver(notificationReceiver, intentFilter);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            LogMessage.e("Error in register receiver");
            LogMessage.e("Error : " + ex.getMessage());
        }
        // [END]
    }

    private void unregisterNotificationReceiver() {
        try {
            if (notificationReceiver != null) {
                HomeActivity.this.unregisterReceiver(notificationReceiver);
                notificationReceiver = null;
            }
        }
        catch (Exception ex) {
            LogMessage.e("Error in un register receiver");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void getBundleData() {
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        LogMessage.d("position : " + String.valueOf(position));
    }

    private void getNotificationIntentData() {
        /* [START] - Open notification fragment if user press on notification */
        LogMessage.d("getNotificationIntentData() call");
        Bundle bundle = getIntent().getExtras();
        String screenNo = "-1";
        String notificationId = "-1";
        if (bundle != null) {
            if (bundle.getString(Constants.KEY_SCREEN_NO) != null) {
                screenNo = bundle.getString(Constants.KEY_SCREEN_NO, "-1");
                LogMessage.d("Screen No : " + screenNo);
            }
            if (bundle.getString(Constants.KEY_NOTIFICATION_ID) != null) {
                notificationId = bundle.getString(Constants.KEY_NOTIFICATION_ID, "");
                LogMessage.d("Notification No : " + notificationId);
            }
        }
        if (!TextUtils.equals(screenNo, "-1") && !TextUtils.equals(notificationId, "-1")) {
            // openFragment(MENU_NOTIFICATION, new NotificationFragment());
            openNotificationFragment(MENU_NOTIFICATION, notificationId);
        }
        // [END]
    }

    private void openFragment(String title, Fragment fragment) {
        getSupportActionBar().setTitle(title);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // fragmentTransaction.add(R.id.container, fragment).commit();
        fragmentTransaction.replace(R.id.container, fragment).commit();
    }

    public void openRecentTransactionFragment() {
        openFragment(MENU_RECENT_TRANSACTION, new RecentTransactionFragment());
    }

    private void openNotificationFragment(String title, String notificationId) {
        getSupportActionBar().setTitle(title);

        NotificationFragment notificationFragment = new NotificationFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_NOTIFICATION_ID, notificationId);
        notificationFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, notificationFragment).commit();

        displayBottomNavigationDynamic(NOTIFICATION);
    }

    public void setNotificationCounter() {
        int totalNotification = 0;
        try {
            Constants.TOTAL_UNREAD_NOTIFICATION = new NotificationTable(getContextInstance()).getNumberOfNotificationRecord() + "";
            totalNotification = Integer.parseInt(Constants.TOTAL_UNREAD_NOTIFICATION);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            LogMessage.d("Notification : " + "Error : " + ex.toString());
            totalNotification = 0;
        }
        if (totalNotification > 0) {
            txtDefaultTotalNotification.setVisibility(View.VISIBLE);
            txtCashbookTotalNotification.setVisibility(View.VISIBLE);
            txtChangePasswordTotalNotification.setVisibility(View.VISIBLE);
            txtRechargeTotalNotification.setVisibility(View.VISIBLE);
            txtTransactionSearchTotalNotification.setVisibility(View.VISIBLE);
            txtRecentTransactionTotalNotification.setVisibility(View.VISIBLE);
            txtDefaultTotalNotification.setText(Constants.TOTAL_UNREAD_NOTIFICATION);
            txtCashbookTotalNotification.setText(Constants.TOTAL_UNREAD_NOTIFICATION);
            txtChangePasswordTotalNotification.setText(Constants.TOTAL_UNREAD_NOTIFICATION);
            txtRechargeTotalNotification.setText(Constants.TOTAL_UNREAD_NOTIFICATION);
            txtTransactionSearchTotalNotification.setText(Constants.TOTAL_UNREAD_NOTIFICATION);
            txtRecentTransactionTotalNotification.setText(Constants.TOTAL_UNREAD_NOTIFICATION);
        } else {
            txtDefaultTotalNotification.setVisibility(View.GONE);
            txtCashbookTotalNotification.setVisibility(View.GONE);
            txtChangePasswordTotalNotification.setVisibility(View.GONE);
            txtRechargeTotalNotification.setVisibility(View.GONE);
            txtTransactionSearchTotalNotification.setVisibility(View.GONE);
            txtRecentTransactionTotalNotification.setVisibility(View.GONE);
        }
        // reset custom navigation menu
        try {
            setCustomNavigation();
        }
        catch (Exception ex) {
            LogMessage.d("Custom navigation menu error : " + ex.toString());
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
            sharedPreferences.edit().putBoolean(constants.isClicked, false).commit();
            getSupportFragmentManager().popBackStackImmediate();
            getSupportFragmentManager().beginTransaction().commit();
        } else {
            super.onBackPressed();
            unregisterNotificationReceiver();
            HomeActivity.this.finish();
            Intent intent = new Intent(getContextInstance(), MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
//        if (fragment instanceof RechargeMainFragment)
//            getMenuInflater().inflate(R.menu.menu_common, menu);
//        else if (fragment instanceof TransSearchFragment) {
//            menu.clear();
//            getMenuInflater().inflate(R.menu.menu_common, menu);
//        } else if (fragment instanceof RecentTransactionFragment)
//            menu.clear();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        MenuItem menuItem;
//        menuItem = menu.findItem(R.id.action_balance);
//        if (balance != null) {
//            menuItem.setTitle("Balance : " + balance);
//        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.action_logout) {
//            sharedPreferences.edit().clear().commit();
//            finish();
//            Intent intent1 = new Intent(HomeActivity.this, LoginActivity.class);
//            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            //intent1.putExtra("device_id", device_id);
//            startActivity(intent1);
//            sharedPreferences.edit().putBoolean(constants.LOGOUT, true).commit();
//        }
        return toggle.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String title = "";

        if (id == R.id.nav_Home) {
            Intent intent = new Intent(getContextInstance(), MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_recharge) {
            title = MENU_RECHARGE;
            getSupportActionBar().setTitle(title);
            RechargeMainFragment rechargeFragment = new RechargeMainFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, rechargeFragment).commit();

            displayBottomNavigationDynamic(RECHARGE);
        } else if (id == R.id.nav_rec_trans) {
            title = MENU_RECENT_TRANSACTION;
            getSupportActionBar().setTitle(title);
            RecentTransactionFragment recentTransactionFragment = new RecentTransactionFragment();
            fragmentTransaction(recentTransactionFragment);

            displayBottomNavigationDynamic(RECENT_TRANSACTION);
        } else if (id == R.id.nav_trans_search) {
            title = MENU_TRANSACTION_SEARCH;
            getSupportActionBar().setTitle(title);
            TransSearchFragment transSearchFragment = new TransSearchFragment();
            fragmentTransaction(transSearchFragment);

            displayBottomNavigationDynamic(TRANSACTION_SEARCH);
        }else if (id == R.id.nav_complain_report) {
            title = MENU_COMPLAIN_REPORT;
            getSupportActionBar().setTitle(title);
            ComplainReportFragment complainReportFragment = new ComplainReportFragment();
            fragmentTransaction(complainReportFragment);

            displayBottomNavigationDynamic(COMPLAIN_REPORT);
        }else if (id == R.id.nav_Cashbook) {
            title = MENU_CASH_BOOK;
            getSupportActionBar().setTitle(title);
            CashbookFragment cashbookFragment = new CashbookFragment();
            fragmentTransaction(cashbookFragment);

            displayBottomNavigationDynamic(CASH_BOOK);
        }else if (id == R.id.nav_account_ledger) {
            //Toast.makeText(HomeActivity.this,"coming soon!",Toast.LENGTH_LONG).show();

            title = MENU_ACCOUNT_LEDGER;
            getSupportActionBar().setTitle(title);
            AccountLedgerFragment accountLedgerFragment = new AccountLedgerFragment();
            fragmentTransaction(accountLedgerFragment);

            displayBottomNavigationDynamic(ACCOUNT_LEGER);
        }else if (id == R.id.nav_update_data) {
            title = MENU_UPDATE_DATA;
            getSupportActionBar().setTitle(title);
            UpdateData updateDataFragment = new UpdateData();
            fragmentTransaction(updateDataFragment);

            displayBottomNavigationDynamic(UPDATE_DATA);
        }else if (id == R.id.nav_ChangePassword) {
            title = MENU_CHANGE_PASSWORD;
            getSupportActionBar().setTitle(title);
            ChangePasswordFragment changePasswordActivity = new ChangePasswordFragment();
            fragmentTransaction(changePasswordActivity);

            displayBottomNavigationDynamic(CHANGE_PASSWORD);
        } else if (id == R.id.nav_Notification) {
            title = MENU_NOTIFICATION;
            getSupportActionBar().setTitle(title);
            NotificationFragment notificationFragment = new NotificationFragment();
            fragmentTransaction(notificationFragment);

            displayBottomNavigationDynamic(NOTIFICATION);
        } else if (id == R.id.nav_logout) {
            sharedPreferences.edit().clear().commit();
            Intent intent1 = new Intent(getContextInstance(), LoginActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent1);
            finish();
            sharedPreferences.edit().putBoolean(constants.LOGOUT, true).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void fragmentTransaction(Fragment fragmentname) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, fragmentname).commit();
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
        LogMessage.i("Balance Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {

                String encrypted_response = jsonObject.getString("data");
                String decrypted_data = decryptAPI(encrypted_response);
                JSONObject object = new JSONObject(decrypted_data);
                balance = object.getString("balance");
                tv_balance.setText(getResources().getString(R.string.Rs) + "  " + balance);
            } else {
                LogMessage.d("Balance response not found. Status = " + jsonObject.getString("status"));
            }
        }
        catch (JSONException e) {
            LogMessage.e("Error while get balance");
            LogMessage.e("Error : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // display error in dialog
    private void displayErrorDialog(String message) {
        new AlertDialog.Builder(getContextInstance())
                .setTitle("Info!")
                .setCancelable(false)
                .setMessage(message)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    // handle thread messages
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == SUCCESS_BALANCE) {
                parseBalanceResponse(msg.obj.toString());
            } else if (msg.what == ERROR) {
                displayErrorDialog(msg.obj.toString());
            }
        }
    };
    // [END]

    /*
        Method Name : decryptAPI
        Decrypt response of webservice */
    public String decryptAPI(String response) {
        ArrayList<Default> defaultArrayList;
        defaultArrayList = databaseHelper.getDefaultSettings();
        String user_id = defaultArrayList.get(0).getUser_id();
        MCrypt mCrypt = new MCrypt(user_id, userArrayList.get(0).getDevice_id());
        String decrypted_response = null;
        byte[] decrypted_bytes = Base64.decode(response, Base64.DEFAULT);
        try {
            decrypted_response = new String(mCrypt.decrypt(mCrypt.bytesToHex(decrypted_bytes)), "UTF-8");
            LogMessage.d("decrypted_balance : " + decrypted_response);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return decrypted_response;
    }

    @Override
    public void onClick(View v) {
        String title = "";
        /* [START] - Bottom Notification listener */
        // Default bottom navigation
        if (v == llChangePassword) {
            openFragment(MENU_CHANGE_PASSWORD, new ChangePasswordFragment());
            displayBottomNavigationDynamic(CHANGE_PASSWORD);
        } else if (v == llLogout) {
            sharedPreferences.edit().clear().commit();
            Intent intent1 = new Intent(getContextInstance(), LoginActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent1);
            finish();
            sharedPreferences.edit().putBoolean(constants.LOGOUT, true).commit();
        } else if (v == llNotification) {
            openFragment(MENU_NOTIFICATION, new NotificationFragment());
            displayBottomNavigationDynamic(NOTIFICATION);
        } else if (v == llUpdate) {
            openFragment(MENU_UPDATE_DATA, new UpdateData());
            displayBottomNavigationDynamic(UPDATE_DATA);
        }
        // Notification bottom navigation listener
        else if (v == llNotificationRecharge) {
            openFragment(MENU_RECHARGE, new RechargeMainFragment());
            displayBottomNavigationDynamic(RECHARGE);
        } else if (v == llNotificationRecentTransaction) {
            openFragment(MENU_RECENT_TRANSACTION, new RecentTransactionFragment());
            displayBottomNavigationDynamic(RECENT_TRANSACTION);
        } else if (v == llNotificationTransactionSearch) {
            openFragment(MENU_TRANSACTION_SEARCH, new TransSearchFragment());
            displayBottomNavigationDynamic(TRANSACTION_SEARCH);
        }
        // Cashbook bottom navigation listener
        else if (v == llCashbookRecharge) {
            openFragment(MENU_RECHARGE, new RechargeMainFragment());
            displayBottomNavigationDynamic(RECHARGE);
        } else if (v == llCashbookRecentTrasaction) {
            openFragment(MENU_RECENT_TRANSACTION, new RecentTransactionFragment());
            displayBottomNavigationDynamic(RECENT_TRANSACTION);
        } else if (v == llCashbookNotification) {
            openFragment(MENU_NOTIFICATION, new NotificationFragment());
            displayBottomNavigationDynamic(NOTIFICATION);
        }
        // Change password bottom navigation listener
        else if (v == llChangePasswordRecharge) {
            openFragment(MENU_RECHARGE, new RechargeMainFragment());
            displayBottomNavigationDynamic(RECHARGE);
        } else if (v == llChangePasswordRecentTransaction) {
            openFragment(MENU_RECENT_TRANSACTION, new RecentTransactionFragment());
            displayBottomNavigationDynamic(RECENT_TRANSACTION);
        } else if (v == llChangePasswordNotification) {
            openFragment(MENU_NOTIFICATION, new NotificationFragment());
            displayBottomNavigationDynamic(NOTIFICATION);
        }
        // Recharge bottom navigation listener
        else if (v == llRechargeTransaction) {
            openFragment(MENU_TRANSACTION_SEARCH, new TransSearchFragment());
            displayBottomNavigationDynamic(TRANSACTION_SEARCH);
        } else if (v == llRechargeRecentTransaction) {
            openFragment(MENU_RECENT_TRANSACTION, new RecentTransactionFragment());
            displayBottomNavigationDynamic(RECENT_TRANSACTION);
        } else if (v == llRechargeNotification) {
            openFragment(MENU_NOTIFICATION, new NotificationFragment());
            displayBottomNavigationDynamic(NOTIFICATION);
        }
        // Transaction search bottom navigation listener
        else if (v == llTransactionSearchRecharge) {
            openFragment(MENU_RECHARGE, new RechargeMainFragment());
            displayBottomNavigationDynamic(RECHARGE);
        } else if (v == llTransactionSearchRecentTransaction) {
            openFragment(MENU_RECENT_TRANSACTION, new RecentTransactionFragment());
            displayBottomNavigationDynamic(RECENT_TRANSACTION);
        } else if (v == llTransactionSearchNotification) {
            openFragment(MENU_NOTIFICATION, new NotificationFragment());
            displayBottomNavigationDynamic(NOTIFICATION);
        }
        // Recent transaction bottom navigation listener
        else if (v == llRecentTransactionRecharge) {
            openFragment(MENU_RECHARGE, new RechargeMainFragment());
            displayBottomNavigationDynamic(RECHARGE);
        } else if (v == llRecentTransactionTransactionSearch) {
            openFragment(MENU_TRANSACTION_SEARCH, new TransSearchFragment());
            displayBottomNavigationDynamic(TRANSACTION_SEARCH);
        } else if (v == llRecentTransactionNotification) {
            openFragment(MENU_NOTIFICATION, new NotificationFragment());
            displayBottomNavigationDynamic(NOTIFICATION);
        }
        // [END]
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNotificationReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterNotificationReceiver();
    }

    /* [START] - Custom check notification data class */
    private class CheckNotification extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogMessage.d("Receiver action : " + action);
            if (action.equals(ACTION_REFRESH_HOMEACTIVITY)) {
                LogMessage.i("Receiver call ACTION_REFRESH_HOMEACTIVITY");
                try {
                    setNotificationCounter();
                    // setCustomNavigation();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    LogMessage.e("Error in receiver ACTION_REFRESH_HOMEACTIVITY");
                    LogMessage.e("Error : " + ex.getMessage());
                }
            }
        }
    }
    // [END]
}
