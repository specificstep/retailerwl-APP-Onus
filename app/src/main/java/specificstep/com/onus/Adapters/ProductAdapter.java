package specificstep.com.onus.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import specificstep.com.onus.Fragments.DTHRechargeFragment;
import specificstep.com.onus.Fragments.RechargeFragment;
import specificstep.com.onus.Models.Product;
import specificstep.com.onus.R;

/**
 * Created by ubuntu on 17/1/17.
 */

public class ProductAdapter extends BaseAdapter {
    Context context;
    ArrayList<Product> productArrayList;
    LayoutInflater inflater;
    FragmentManager fragmentManager;
    String fragment_name;
    String company_name;
    String company_logo;

    public ProductAdapter(Context activity, ArrayList<Product> companyArrayList, FragmentManager supportFragmentManager, String fragment_name, String company_name, String company_logo) {
        this.fragment_name = fragment_name;
        context = activity;
        this.productArrayList = companyArrayList;
        this.company_name = company_name;
        this.company_logo = company_logo;
        fragmentManager = supportFragmentManager;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return productArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return productArrayList.get(position);
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

        tv_company_name.setText(productArrayList.get(position).getProduct_name());
        String img_path = productArrayList.get(position).getProduct_logo();

        if (img_path != null && !img_path.isEmpty()) {
            Picasso.with(context)
                    .load(img_path)
                    .placeholder(R.drawable.placeholder_icon)
                    .into(iv_company_logo);
        } else {
            iv_company_logo.setImageResource(R.drawable.placeholder_icon);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("company_id", productArrayList.get(position).getCompany_id());
                bundle.putString("product_id", productArrayList.get(position).getId());
                bundle.putString("company_name", company_name);
                bundle.putString("company_image", company_logo);
                bundle.putString("product_name", productArrayList.get(position).getProduct_name());
                bundle.putString("product_image", productArrayList.get(position).getProduct_logo());

                if (fragment_name.equals("Mobile")) {
                    RechargeFragment rechargeFragment = new RechargeFragment();
                    rechargeFragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, rechargeFragment).addToBackStack(null).commit();
                } else if (fragment_name.equals("DTH")) {
                    DTHRechargeFragment rechargeFragment = new DTHRechargeFragment();
                    rechargeFragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, rechargeFragment).addToBackStack(null).commit();
                }
            }
        });
        return convertView;
    }
}
