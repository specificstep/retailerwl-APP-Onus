package specificstep.com.onus.Fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import specificstep.com.onus.Activities.BrowsePlansActivity;
import specificstep.com.onus.Adapters.PlanAdapter;
import specificstep.com.onus.Database.PlanTable;
import specificstep.com.onus.GlobalClasses.TransparentProgressDialog;
import specificstep.com.onus.GlobalClasses.URL;
import specificstep.com.onus.Models.PlanModel;
import specificstep.com.onus.R;
import specificstep.com.onus.utility.InternetUtil;
import specificstep.com.onus.utility.LogMessage;
import specificstep.com.onus.utility.Utility;

/**
 * Created by ubuntu on 25/5/17.
 */

public class OneFragment extends Fragment {

    private final int SUCCESS_PLANS = 1, ERROR = 3;
    /* [START] - All json key names */
    private final String KEY_CIRCLE_NAME = "circle_name";
    private final String KEY_COMPANY_NAME = "company_name";
    private final String KEY_PRODUCT_ID = "product_id";
    private final String KEY_PRODUCT_NAME = "product_name";
    private final String KEY_PLANTYPE_NAME = "plantype_name";
    private final String KEY_NAME = "name";
    private final String KEY_BENIFIT = "Benifit";
    private final String KEY_VALIDITY = "validity";
    private final String KEY_PRICE = "price";
    // [END]
    private View view;
    private Context context;
    // private LogMessage log;
    private TransparentProgressDialog transparentProgressDialog;
    private TextView txtNoMoreData;
    private ListView lstPlans;
    private PlanAdapter planAdapter;
    private ArrayList<PlanModel> planModels;
    private BroadcastReceiver changeTabReceiver = null;

    private Context getContextInstance() {
        if (context == null) {
            context = OneFragment.this.getActivity();
            return context;
        } else {
            return context;
        }
    }

    private BrowsePlansActivity mainActivity() {
        return ((BrowsePlansActivity) getActivity());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_one, null);

        context = OneFragment.this.getActivity();
        // log = new LogMessage(OneFragment.class.getSimpleName());

        initControls();
        setListener();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerChangeTabReceiver();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterChangeTabReceiver();
    }

    private void initControls() {
        // init progress bar
        transparentProgressDialog = new TransparentProgressDialog(getContextInstance(), R.drawable.fotterloading);

        txtNoMoreData = (TextView) view.findViewById(R.id.txt_Plan_NoMoreData);
        lstPlans = (ListView) view.findViewById(R.id.lst_Plan_Plans);

        planModels = new ArrayList<PlanModel>();
        planAdapter = new PlanAdapter(getContextInstance(), planModels);
        lstPlans.setAdapter(planAdapter);

        updatePlan();
    }

    private void setListener() {
        lstPlans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlanModel planModel = planAdapter.getData(position);
                String returnData = planModel.price + "," + planModel.product_id + "," + planModel.product_name;
                LogMessage.d(returnData);
                String prise = planModel.price;
                try {
                    if (prise.contains(".")) {
                        prise = prise.substring(0, prise.indexOf("."));
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    prise = planModel.price;
                }
                mainActivity().returnActivityResult(prise, planModel.product_id, planModel.product_name);
            }
        });
    }

    private void updatePlan() {
        String planType = mainActivity().getStrPlanType();
        String planTypeId = mainActivity().getStrPlanTypeId();
        LogMessage.d("Plan - " + planType + " - " + planTypeId);

        PlanTable planTable = new PlanTable(getContextInstance());
        ArrayList<PlanModel> models = planTable.select_Data(PlanTable.KEY_PLANTYPE_NAME + "='" + planType + "'");
        if (models.size() > 0) {
            planModels.clear();
            for (int i = 0; i < models.size(); i++) {
                planModels.add(models.get(i));
            }
            // if set plan data from selected data
            planAdapter.notifyDataSetChanged();
            setListViewVisibility(true);
        } else {
            // if plan model is null then get plan data from server
            showProgressDialog();
            getNativeGetPlanType();
        }
    }

    /* [START] - 2017_05_29 - Add native code for get plan data */
    private void getNativeGetPlanType() {
        // create new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.GET_PLANS;
                    // Set parameters list in string array
                    String[] parameters = {
                            "company_id",
                            "circle_id",
                            "name",
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            mainActivity().getStrCompanyId(),
                            mainActivity().getStrCircleId(),
                            mainActivity().getStrPlanTypeId(),
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    myHandler.obtainMessage(SUCCESS_PLANS, response).sendToTarget();
                }
                catch (Exception ex) {
                    LogMessage.e("Error in get plan data native method");
                    LogMessage.e("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    private void parseGetPlanResponse(String response) {
        LogMessage.i("Get plan type response : " + response);
        try {
            PlanTable planTable = new PlanTable(getContextInstance());
            planTable.delete(PlanTable.KEY_PLANTYPE_NAME + "='" + mainActivity().getStrPlanType() + "'");
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                // insert plan data into database.
                PlanModel planModel = new PlanModel();
                planModel.circle_name = jsonObject.getString(KEY_CIRCLE_NAME);
                planModel.company_name = jsonObject.getString(KEY_COMPANY_NAME);
                planModel.product_id = jsonObject.getString(KEY_PRODUCT_ID);
                planModel.product_name = jsonObject.getString(KEY_PRODUCT_NAME);
                planModel.plantype_name = jsonObject.getString(KEY_PLANTYPE_NAME);
                planModel.name = jsonObject.getString(KEY_NAME);
                planModel.benifit = jsonObject.getString(KEY_BENIFIT);
                planModel.validity = jsonObject.getString(KEY_VALIDITY);
                planModel.price = jsonObject.getString(KEY_PRICE);
                planTable.insert(planModel);
            }
            ArrayList<PlanModel> models = planTable.select_Data(PlanTable.KEY_PLANTYPE_NAME + "='" + mainActivity().getStrPlanType() + "'");
            if (models.size() > 0) {
                planModels.clear();
                for (int i = 0; i < models.size(); i++) {
                    planModels.add(models.get(i));
                }
                // if set plan data from selected data
                planAdapter.notifyDataSetChanged();
                setListViewVisibility(true);
            } else {
                // if plan model is null then get plan data from server
                // Utility.toast(getContextInstance(), "Not any plan found.");
                setListViewVisibility(false);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            myHandler.obtainMessage(ERROR, "Not any plan found.").sendToTarget();
        }
    }

    // handle recharge messages
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == SUCCESS_PLANS) {
                dismissProgressDialog();
                parseGetPlanResponse(msg.obj.toString());
            } else if (msg.what == ERROR) {
                dismissProgressDialog();
                setListViewVisibility(false);
                // displayPlanErrorDialog(msg.obj.toString());
                Utility.toast(getContextInstance(), msg.obj.toString());
            }
        }
    };

    private AlertDialog alertDialog;

    private void displayPlanErrorDialog(String message) {
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
    // [END]

    private void setListViewVisibility(boolean visibility) {
        if (visibility) {
            txtNoMoreData.setVisibility(View.GONE);
            lstPlans.setVisibility(View.VISIBLE);
        } else {
            txtNoMoreData.setVisibility(View.VISIBLE);
            lstPlans.setVisibility(View.GONE);
        }
    }

    private void registerChangeTabReceiver() {
        /* [START] - Create custom notification for receiver notification data */
        try {
            if (changeTabReceiver == null) {
                // Add notification filter
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(BrowsePlansActivity.ACTION_CHANGE_TAB);
                // Create notification object
                changeTabReceiver = new CheckChangeTab();
                // Register receiver
                OneFragment.this.getActivity().registerReceiver(changeTabReceiver, intentFilter);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            LogMessage.e("Error in register receiver");
            LogMessage.e("Error : " + ex.getMessage());
        }
        // [END]
    }

    private void unregisterChangeTabReceiver() {
        try {
            if (changeTabReceiver != null) {
                OneFragment.this.getActivity().unregisterReceiver(changeTabReceiver);
                changeTabReceiver = null;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            LogMessage.e("Error in register receiver");
            LogMessage.e("Error : " + ex.getMessage());
        }
    }

    /* [START] - Custom check notification data class */
    private class CheckChangeTab extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogMessage.d("Receiver action : " + action);
            if (action.equals(BrowsePlansActivity.ACTION_CHANGE_TAB)) {
                LogMessage.i("Receiver call BrowsePlansActivity.ACTION_CHANGE_TAB");
                try {
                    updatePlan();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    LogMessage.e("Error in BrowsePlansActivity.ACTION_CHANGE_TAB");
                    LogMessage.e("Error : " + ex.getMessage());
                }
            }
        }
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
