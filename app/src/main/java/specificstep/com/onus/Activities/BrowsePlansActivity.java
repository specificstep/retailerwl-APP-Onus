package specificstep.com.onus.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import specificstep.com.onus.Database.PlanTypesTable;
import specificstep.com.onus.Fragments.OneFragment;
import specificstep.com.onus.GlobalClasses.Constants;
import specificstep.com.onus.Models.PlanTypesModel;
import specificstep.com.onus.R;
import specificstep.com.onus.utility.LogMessage;

/**
 * Created by ubuntu on 25/5/17.
 */

public class BrowsePlansActivity extends FragmentActivity {
    /* Broad cast message */
    public static final String ACTION_CHANGE_TAB = "specificstep.com.metroenterprise.CHANGE_TAB";

    /* Other class objects */
    private Context context;
    private PlanTypesTable planTypesTable;

    /* All local int and string variables */
    private String strOperator = "", strState = "", strCompanyId = "", strCircleId = "", strPlanType = "",
            strPlanTypeId = "";

    /* All ArrayList */
    private ArrayList<PlanTypesModel> planTypesModels = null;

    /* All Views */
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView txtBrowsePlans;

    private Context getContextInstance() {
        if (context == null) {
            context = BrowsePlansActivity.this;
            return context;
        } else {
            return context;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_browse_plans);

        context = BrowsePlansActivity.this;

        getBundleData();
        initControls();
    }

    private void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString(Constants.KEY_OPERATOR) != null) {
                if (!TextUtils.isEmpty(bundle.getString(Constants.KEY_OPERATOR, ""))) {
                    strOperator = bundle.getString(Constants.KEY_OPERATOR, "");
                }
            }
            if (bundle.getString(Constants.KEY_STATE) != null) {
                if (!TextUtils.isEmpty(bundle.getString(Constants.KEY_STATE, ""))) {
                    strState = bundle.getString(Constants.KEY_STATE, "");
                }
            }
            if (bundle.getString(Constants.KEY_CIRCLE_ID) != null) {
                if (!TextUtils.isEmpty(bundle.getString(Constants.KEY_CIRCLE_ID, ""))) {
                    strCircleId = bundle.getString(Constants.KEY_CIRCLE_ID, "");
                }
            }
            if (bundle.getString(Constants.KEY_COMPANY_ID) != null) {
                if (!TextUtils.isEmpty(bundle.getString(Constants.KEY_COMPANY_ID, ""))) {
                    strCompanyId = bundle.getString(Constants.KEY_COMPANY_ID, "");
                }
            }
        }
    }

    private void initControls() {
        planTypesTable = new PlanTypesTable(getContextInstance());
        planTypesModels = planTypesTable.select_Data();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        txtBrowsePlans = (TextView) findViewById(R.id.txt_BrowsePlans_Plans);

        LogMessage.d("Operator : " + strOperator);
        LogMessage.d("State : " + strState);
        if (!TextUtils.isEmpty(strOperator)) {
            txtBrowsePlans.setText(strOperator + " - " + strState);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        if (planTypesModels != null) {
            if (planTypesModels.size() > 0) {
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                for (int i = 0; i < planTypesModels.size(); i++) {
                    viewPagerAdapter.addFragment(new OneFragment(), planTypesModels.get(i).name);
                    if (i == 0) {
                        strPlanType = planTypesModels.get(i).name;
                        strPlanTypeId = planTypesModels.get(i).id;
                    }
                }
                viewPager.setAdapter(viewPagerAdapter);

                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        LogMessage.d("Page Selected : " + position);
                        strPlanType = planTypesModels.get(position).name;
                        strPlanTypeId = planTypesModels.get(position).id;
                        Intent intentChangeTab = new Intent(ACTION_CHANGE_TAB);
                        context.sendBroadcast(intentChangeTab);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
            }
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public String getStrPlanType() {
        return strPlanType;
    }

    public String getStrPlanTypeId() {
        return strPlanTypeId;
    }

    public String getStrCompanyId() {
        return strCompanyId;
    }

    public String getStrCircleId() {
        return strCircleId;
    }

    public void returnActivityResult(String strPlanRs, String productId, String productName) {
        LogMessage.d("Activity result : " + strPlanRs);
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.KEY_PLAN_RS, strPlanRs);
        returnIntent.putExtra(Constants.KEY_PRODUCT_ID, productId);
        returnIntent.putExtra(Constants.KEY_PRODUCT_NAME, productName);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
