package specificstep.com.onus.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

import specificstep.com.onus.Activities.HomeActivity;
import specificstep.com.onus.Adapters.ProductAdapter;
import specificstep.com.onus.Database.DatabaseHelper;
import specificstep.com.onus.Models.Product;
import specificstep.com.onus.R;

/**
 * Created by ubuntu on 16/1/17.
 */

public class ProductFragment extends Fragment {

    private View view;
    /* Other class objects */
    private DatabaseHelper databaseHelper;

    /* All local int and string and boolean variables */
    private String strCompanyId, strCompanyName, strCompanyLogo, strFragmentName;

    /* All ArrayList */
    private ArrayList<Product> productArrayList;

    /* All Adapters */
    private ProductAdapter productAdapter;

    /* All Views */
    private GridView gridViewMobileRecharge;
    private TextView txtProductName;

    private HomeActivity mainActivity() {
        return ((HomeActivity) getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mobile_recharge, null);

        productArrayList = new ArrayList<Product>();

        Bundle bundle = getArguments();
        strCompanyId = bundle.getString("company_id");
        strFragmentName = bundle.getString("fragment_name");
        strCompanyName = bundle.getString("company_name");
        strCompanyLogo = bundle.getString("company_image");

        /* [START] - 2017_04_18 set title bar as Mobile recharge */
        mainActivity().getSupportActionBar().setTitle(strFragmentName + " Recharge");
        // [END]

        databaseHelper = new DatabaseHelper(getActivity());
        productArrayList = databaseHelper.getProductDetails(strCompanyId);

        // Display bottom bar in add balance
        mainActivity().displayRechargeBottomBar(true);

        init();
        return view;
    }

    private void init() {
        gridViewMobileRecharge = (GridView) view.findViewById(R.id.grid_mobile_rechrge);
        txtProductName = (TextView) view.findViewById(R.id.tv_product_name);
        txtProductName.setVisibility(View.VISIBLE);
        txtProductName.setText(strCompanyName);
        productAdapter = new ProductAdapter(getActivity(),
                productArrayList,
                getActivity().getSupportFragmentManager(),
                strFragmentName,
                strCompanyName,
                strCompanyLogo);
        gridViewMobileRecharge.setAdapter(productAdapter);
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
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }
}
