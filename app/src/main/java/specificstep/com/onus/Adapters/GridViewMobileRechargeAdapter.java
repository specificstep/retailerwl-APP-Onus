package specificstep.com.onus.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import specificstep.com.onus.Database.DatabaseHelper;
import specificstep.com.onus.Fragments.DTHRechargeFragment;
import specificstep.com.onus.Fragments.ProductFragment;
import specificstep.com.onus.Fragments.RechargeFragment;
import specificstep.com.onus.Models.Company;
import specificstep.com.onus.Models.Product;
import specificstep.com.onus.R;
import specificstep.com.onus.utility.LogMessage;

/**
 * Created by ubuntu on 16/1/17.
 */

public class GridViewMobileRechargeAdapter extends BaseAdapter {
    Context context;
    ArrayList<Company> companyArrayList;
    LayoutInflater inflater;
    FragmentManager fragmentManager;
    String str_fragment_name;
    private DatabaseHelper databaseHelper;
    private ArrayList<Product> productArrayList;
    // private LogMessage log;

    public GridViewMobileRechargeAdapter(Context activity, ArrayList<Company> companyArrayList, FragmentManager supportFragmentManager, String fragment_name) {
        context = activity;
        this.companyArrayList = companyArrayList;
        fragmentManager = supportFragmentManager;
        str_fragment_name = fragment_name;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        databaseHelper = new DatabaseHelper(context);
        productArrayList = new ArrayList<Product>();
        // log = new LogMessage(GridViewMobileRechargeAdapter.class.getSimpleName());
    }

    @Override
    public int getCount() {
        return companyArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return companyArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.adapter_grid_mobile_recharge, null);
        ImageView iv_company_logo = (ImageView) convertView.findViewById(R.id.iv_company_logo_adapter_mo_recharge);
        TextView tv_company_name = (TextView) convertView.findViewById(R.id.tv_compnay_name_adapter_mo_recharge);

        tv_company_name.setText(companyArrayList.get(position).getCompany_name());
        String img_path = companyArrayList.get(position).getLogo();

        if (img_path != null && !img_path.isEmpty()) {
            Picasso.with(context)
                    .load(img_path)
                    .placeholder(R.drawable.placeholder_icon)
                    .error(R.drawable.placeholder_icon)
                    .into(iv_company_logo);
        } else {
            iv_company_logo.setImageResource(R.drawable.placeholder_icon);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    /* [START] - 2017_05_02 - If company have more than one products than  display products, if company have only one products than direct display to recharge form. */
                // if company have one product open recharge screen
                String companyId = companyArrayList.get(position).getId();
                productArrayList = databaseHelper.getProductDetails(companyId);
                if (productArrayList.size() == 1) {
                    LogMessage.d(companyArrayList.get(position).getCompany_name() + " - Have 1 product");
                    Bundle bundle = new Bundle();
                    bundle.putString("company_id", productArrayList.get(0).getCompany_id());
                    bundle.putString("product_id", productArrayList.get(0).getId());
                    bundle.putString("company_name", companyArrayList.get(position).getCompany_name());
                    bundle.putString("company_image", companyArrayList.get(position).getLogo());
                    bundle.putString("product_name", productArrayList.get(0).getProduct_name());
                    bundle.putString("product_image", productArrayList.get(0).getProduct_logo());

                    //redirect it directly on Recharge fragment
                    if (str_fragment_name.equals("Mobile")) {
                        RechargeFragment rechargeFragment = new RechargeFragment();
                        rechargeFragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container, rechargeFragment).addToBackStack(null).commit();
                    } else if (str_fragment_name.equals("DTH")) {
                        DTHRechargeFragment rechargeFragment = new DTHRechargeFragment();
                        rechargeFragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container, rechargeFragment).addToBackStack(null).commit();
                    }
                }
                // if company have more than one product then open product screen
                else if (productArrayList.size() > 1) {
                    LogMessage.d(companyArrayList.get(position).getCompany_name() + " - Have more than 1 product");
                    Bundle bundle = new Bundle();
                    bundle.putString("company_id", companyArrayList.get(position).getId());
                    bundle.putString("company_name", companyArrayList.get(position).getCompany_name());
                    bundle.putString("company_image", companyArrayList.get(position).getLogo());
                    bundle.putString("fragment_name", str_fragment_name);
                    ProductFragment productFragment = new ProductFragment();
                    productFragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, productFragment).commit();
                }
                // [END]
            }
        });

        // [END]
        return convertView;
    }
}
