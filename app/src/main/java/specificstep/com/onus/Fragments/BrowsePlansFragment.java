package specificstep.com.onus.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import specificstep.com.onus.R;
import specificstep.com.onus.utility.LogMessage;

/**
 * Created by ubuntu on 25/5/17.
 */

public class BrowsePlansFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_OPERATOR = "operator";
    private static final String ARG_STATE = "state";

    private String strOperator = "";
    private String strState = "";

    public BrowsePlansFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param operator Name of mobile operator.
     * @param state    Name of state.
     * @return A new instance of fragment BrowsePlansFragment.
     */
    public static BrowsePlansFragment newInstance(String operator, String state) {
        BrowsePlansFragment fragment = new BrowsePlansFragment();
        Bundle args = new Bundle();
        args.putString(ARG_OPERATOR, operator);
        args.putString(ARG_STATE, state);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            strOperator = getArguments().getString(ARG_OPERATOR);
            strState = getArguments().getString(ARG_STATE);
        }
    }

    /* [START] - All plans static name */
    private final String PLAN_BEST_OFFER = "Best Offer";
    private final String PLAN_FULL_TALKTIME = "Full Talktime";
    private final String PLAN_3G4G_DATA = "3G/4G Data";
    private final String PLAN_2G_DATA = "2G Data";
    private final String PLAN_TOP_UP = "Top Up";
    private final String PLAN_SPECIAL_RECHARGE = "Special Recharge";
    private final String PLAN_ROAMING = "Roaming";
    // [END]

    private View view;
    private Context context;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView txtBrowsePlans;
    // private LogMessage log;
    private ArrayList<String> allPlanTitle;

    private Context getContextInstance() {
        if (context == null) {
            context = BrowsePlansFragment.this.getActivity();
            return context;
        } else {
            return context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_browse_plans, null);
        context = BrowsePlansFragment.this.getActivity();
        // log = new LogMessage(BrowsePlansFragment.class.getSimpleName());

        initControls();

        return view;
    }

    private void initControls() {
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        txtBrowsePlans = (TextView) view.findViewById(R.id.txt_BrowsePlans_Plans);

        LogMessage.d("Operator : " + strOperator);
        LogMessage.d("State : " + strState);

        if (!TextUtils.isEmpty(strOperator)) {
            // txtBrowsePlans.setText(strOperator + " - " + strState);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        /* [START] - Set all plans in array list */
        allPlanTitle = new ArrayList<String>();
        allPlanTitle.add(PLAN_BEST_OFFER);
        allPlanTitle.add(PLAN_FULL_TALKTIME);
        allPlanTitle.add(PLAN_3G4G_DATA);
        allPlanTitle.add(PLAN_2G_DATA);
        allPlanTitle.add(PLAN_TOP_UP);
        allPlanTitle.add(PLAN_SPECIAL_RECHARGE);
        allPlanTitle.add(PLAN_ROAMING);
        // [END]

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(BrowsePlansFragment.this.getActivity().getSupportFragmentManager());
        for (int i = 0; i < allPlanTitle.size(); i++) {
            viewPagerAdapter.addFragment(new OneFragment(), allPlanTitle.get(i));
        }
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                LogMessage.d("Page Selected : " + position);
                String selectedPalnName = allPlanTitle.get(position);
                if(TextUtils.equals(selectedPalnName, PLAN_BEST_OFFER)) {
                }
                else if(TextUtils.equals(selectedPalnName, PLAN_ROAMING)) {
                }
                else if(TextUtils.equals(selectedPalnName, PLAN_FULL_TALKTIME)) {
                }
                else if(TextUtils.equals(selectedPalnName, PLAN_3G4G_DATA)) {
                }
                else if(TextUtils.equals(selectedPalnName, PLAN_2G_DATA)) {
                }
                else if(TextUtils.equals(selectedPalnName, PLAN_TOP_UP)) {
                }
                else if(TextUtils.equals(selectedPalnName, PLAN_SPECIAL_RECHARGE)) {
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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
}
