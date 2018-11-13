package specificstep.com.onus.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import specificstep.com.onus.Database.DatabaseHelper;
import specificstep.com.onus.Models.Complain;
import specificstep.com.onus.Models.Recharge;
import specificstep.com.onus.R;

/**
 * Created by ubuntu on 19/1/17.
 */

public class ComplainListAdapter extends BaseAdapter {

    Context context;

    private ArrayList<Complain> complainArrayList;

    private DatabaseHelper databaseHelper;

    private TextView tv_complain_type,tv_company_name,tv_complain_id,tv_amount,tv_mo_no,tv_complain_status,tv_description,tv_date_time;
    private ImageView iv_company;

    LayoutInflater inflater;

    public ComplainListAdapter(Context activity, ArrayList<Complain> rechargeArrayList) {

        context = activity;
        this.complainArrayList = rechargeArrayList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return complainArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return complainArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.adapter_complain, null);


        tv_company_name = (TextView) convertView.findViewById(R.id.tv_company_name_adapter_complain);

        tv_complain_id = (TextView) convertView.findViewById(R.id.tv_complain_id_adapter_complain);
        tv_amount = (TextView) convertView.findViewById(R.id.tv_amount_adapter_complain);
        tv_mo_no = (TextView) convertView.findViewById(R.id.tv_mo_no_adapter_complain);
        tv_complain_status = (TextView) convertView.findViewById(R.id.tv_complain_status_adapter_complain);
        tv_description = (TextView) convertView.findViewById(R.id.tv_description_adapter_complain);
        tv_date_time = (TextView) convertView.findViewById(R.id.tv_date_time_adapter_complain);

        tv_complain_type = (TextView) convertView.findViewById(R.id.tv_complain_type_adapter_complain);


        iv_company = (ImageView) convertView.findViewById(R.id.iv_company_adapter_complain);

        Complain complain = complainArrayList.get(position);

        tv_company_name.setText(complain.getCompnay_name());

        tv_complain_type.setText(complain.getComplain_type());

        tv_complain_id.setText(complain.getComplain_id());
        tv_amount.setText(complain.getAmount());
        tv_mo_no.setText(complain.getMo_no());
        tv_complain_status.setText(complain.getComplain_status());
        if(complain.getComplain_status().equalsIgnoreCase("solved")){
            tv_complain_status.setBackgroundResource(R.color.colorGreen);

        } else if (complain.getComplain_status().equalsIgnoreCase("pending")) {
            tv_complain_status.setBackgroundResource(R.color.colorPending);
        }
        else{
            tv_complain_status.setBackgroundResource(R.color.colorDefault);
        }

        if(complain.getDescription()!=null && complain.getDescription().length()>0){
            tv_description.setText(complain.getDescription());
        }
        else{
            tv_description.setText("-");
        }

        tv_date_time.setText(complain.getTrans_date_time());

        databaseHelper = new DatabaseHelper(context);
        if(complain.getCompnay_name() != null && complain.getCompnay_name().length() > 0){
            String company_logo = databaseHelper.getCompanyLogo(complain.getCompnay_name());
            if(company_logo!=null){
                Picasso.with(context).load(company_logo).placeholder(R.drawable.placeholder_icon).into(iv_company);

            }
            else{
                iv_company.setImageResource(R.drawable.placeholder_icon);

            }
            /*if (company_logo.isEmpty()) {
            } else {
            }*/
        }else{
            iv_company.setImageResource(R.drawable.placeholder_icon);
        }

        return convertView;
    }
}
