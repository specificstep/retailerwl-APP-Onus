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
import specificstep.com.onus.Models.Color;
import specificstep.com.onus.Models.Recharge;
import specificstep.com.onus.R;

/**
 * Created by ubuntu on 19/1/17.
 */

public class SearchListAdapter extends BaseAdapter implements View.OnClickListener {

    ArrayList<Recharge> rechargeArrayList;
    Context context;
    DatabaseHelper databaseHelper;
    LayoutInflater inflater;
    TextView tv_order_id, tv_product_name, tv_mo_no, tv_date_time, tv_amount, tv_status, tv_company_name, tv_service_type, txtOperatorId,tvComplain;
    LinearLayout llOperatorId;
    ImageView iv_company_logo;
    ArrayList<Color> colorArrayList;
    String _color_name, color_value;


    private SubmitComplainClickListener submitComplainClickListener;

    public interface SubmitComplainClickListener {
        void onComplainClick(int position, String complainText);
    }

    public SearchListAdapter(Context activity, ArrayList<Recharge> rechargeArrayList, SubmitComplainClickListener submitComplainClickListener) {

        context = activity;
        this.rechargeArrayList = rechargeArrayList;
        this.submitComplainClickListener = submitComplainClickListener;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return rechargeArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return rechargeArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.adapter_search_trans, null);

        tv_order_id = (TextView) convertView.findViewById(R.id.tv_order_id_adapter_rec_trans);
        tv_product_name = (TextView) convertView.findViewById(R.id.tv_product_name_adapter_rec_trans);
        tv_mo_no = (TextView) convertView.findViewById(R.id.tv_mo_no_adapter_rec_trans);
        tv_amount = (TextView) convertView.findViewById(R.id.tv_amount_adapter_rec_trans);
        tv_date_time = (TextView) convertView.findViewById(R.id.tv_date_time_adapter_rec_trans);
        tv_product_name = (TextView) convertView.findViewById(R.id.tv_product_name_adapter_rec_trans);
        tv_status = (TextView) convertView.findViewById(R.id.tv_status_adapter_rec_trans);
        tv_company_name = (TextView) convertView.findViewById(R.id.tv_company_name_adapter_rec_trans);
        iv_company_logo = (ImageView) convertView.findViewById(R.id.iv_company_adapter_rec_trans);

        txtOperatorId = (TextView) convertView.findViewById(R.id.txt_Search_OperatorId);
        llOperatorId = (LinearLayout) convertView.findViewById(R.id.ll_Search_OperatorId);

        tvComplain = (TextView) convertView.findViewById(R.id.tv_complain_adapter_rec_trans);
        tvComplain.setTag(position);

        databaseHelper = new DatabaseHelper(context);
        Recharge recharge = rechargeArrayList.get(position);
        tv_order_id.setText(recharge.getClient_trans_id());
        tv_mo_no.setText(recharge.getMo_no());
        tv_amount.setText(recharge.getAmount());
        tv_date_time.setText(recharge.getTrans_date_time());
        tv_company_name.setText(recharge.getCompnay_name());

        /* [START] - Data proper not display ("Postpaid") (MAX - 10 character) */
        // tv_product_name.setText(recharge.getProduct_name());
        String productName = recharge.getProduct_name();
        try {
            if (productName.trim().length() > 10) {
                String subProductName = productName.substring(0, 10);
                tv_product_name.setText(" " + subProductName + "\n" + productName.substring(10, productName.length()));
            } else {
                tv_product_name.setText(" " + productName);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            tv_product_name.setText(" " + productName);
        }
        // [END]

        /* [START] - Display operator id (If data not found, hide this field) */
        if (recharge.getOperator_trans_id().trim().length() == 0
                || recharge.getOperator_trans_id() == null
                || recharge.getOperator_trans_id().trim().equalsIgnoreCase("null")) {
            llOperatorId.setVisibility(View.GONE);
            txtOperatorId.setText(" " + recharge.getOperator_trans_id());
        } else {
            llOperatorId.setVisibility(View.VISIBLE);
            txtOperatorId.setText(" " + recharge.getOperator_trans_id());
        }
        // [END]

        colorArrayList = databaseHelper.getAllColors();
        /*Set color of recharge status*/
        if (recharge.getRecharge_status().equalsIgnoreCase("success")) {
            for (int i = 0; i < colorArrayList.size(); i++) {
                _color_name = colorArrayList.get(i).getColor_name();
                if (_color_name.contains("success")) {
                    color_value = colorArrayList.get(i).getColo_value();
                    tv_status.setBackgroundColor(android.graphics.Color.parseColor(color_value));
                }
            }
            tv_status.setText(recharge.getRecharge_status());
            tvComplain.setVisibility(View.VISIBLE);
            tvComplain.setOnClickListener(this);
        } else if (recharge.getRecharge_status().equalsIgnoreCase("pending")) {
            for (int i = 0; i < colorArrayList.size(); i++) {
                _color_name = colorArrayList.get(i).getColor_name();
                if (_color_name.contains("pending")) {
                    color_value = colorArrayList.get(i).getColo_value();
                    tv_status.setBackgroundColor(android.graphics.Color.parseColor(color_value));
                }
            }
            tv_status.setText(recharge.getRecharge_status());
            tvComplain.setVisibility(View.VISIBLE);
        } else if (recharge.getRecharge_status().equalsIgnoreCase("failure")) {
            for (int i = 0; i < colorArrayList.size(); i++) {
                _color_name = colorArrayList.get(i).getColor_name();
                if (_color_name.contains("failure")) {
                    color_value = colorArrayList.get(i).getColo_value();
                    tv_status.setBackgroundColor(android.graphics.Color.parseColor(color_value));
                }
            }
            tv_status.setText(recharge.getRecharge_status());
            tvComplain.setVisibility(View.GONE);
        }
        /* [START] - recharge_status":"Credit" */
        else if (recharge.getRecharge_status().equalsIgnoreCase("credit")) {
            for (int i = 0; i < colorArrayList.size(); i++) {
                _color_name = colorArrayList.get(i).getColor_name();
                if (_color_name.contains("credit")) {
                    color_value = colorArrayList.get(i).getColo_value();
                    tv_status.setBackgroundColor(android.graphics.Color.parseColor(color_value));
                }
            }
            tv_status.setText(recharge.getRecharge_status());
            tvComplain.setVisibility(View.GONE);
        }
        // [END]

        String company_logo = databaseHelper.getCompanyLogo(recharge.getCompnay_name());
        if (company_logo.isEmpty()) {
            iv_company_logo.setImageResource(R.drawable.placeholder_icon);
        } else {
            Picasso.with(context).load(company_logo).placeholder(R.drawable.placeholder_icon).into(iv_company_logo);
        }

        return convertView;
    }


    // variable to track event time
    private long mLastClickTime = 0;

    @Override
    public void onClick(View v) {

        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();


        if (v.getId() == R.id.tv_complain_adapter_rec_trans) {

            // get prompts.xml view
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.complain_dialog, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            TextView tv_order_id = (TextView) promptsView.findViewById(R.id.tv_order_id_complain);
            TextView tv_product_name = (TextView) promptsView.findViewById(R.id.tv_product_name_complain);
            TextView tv_mo_no = (TextView) promptsView.findViewById(R.id.tv_mo_no_complain);
            TextView tv_amount = (TextView) promptsView.findViewById(R.id.tv_amount_complain);
            TextView tv_date_time = (TextView) promptsView.findViewById(R.id.tv_date_time_complain);
            TextView tv_status = (TextView) promptsView.findViewById(R.id.tv_status_complain);
            TextView tv_company_name = (TextView) promptsView.findViewById(R.id.tv_company_name_complain);
            ImageView iv_company_logo = (ImageView) promptsView.findViewById(R.id.iv_company_complain);

            EditText edt_report_complain = (EditText) promptsView.findViewById(R.id.edt_report_complain);

            txtOperatorId = (TextView) promptsView.findViewById(R.id.txt_operator_id_complain);
            llOperatorId = (LinearLayout) promptsView.findViewById(R.id.ll_operator_id_complain);

            Spinner spinner = (Spinner) promptsView.findViewById(R.id.spinner_complain);

            //Set the text color of the Spinner's selected view (not a drop down list view)
            spinner.setSelection(0, true);
            View view = spinner.getSelectedView();
            ((TextView) view).setTextColor(context.getResources().getColor(R.color.colorWhite));
            ((TextView) view).setTextSize(14);

            //Set the listener for when each option is clicked.
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //Change the selected item's text color
                    ((TextView) view).setTextColor(context.getResources().getColor(R.color.colorWhite));
                    ((TextView) view).setTextSize(14);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });


            Recharge recharge = rechargeArrayList.get((Integer) v.getTag());
            tv_order_id.setText(" " + recharge.getClient_trans_id());
            tv_mo_no.setText(" " + recharge.getMo_no());
            tv_amount.setText(recharge.getAmount());
            tv_date_time.setText(" " + recharge.getTrans_date_time());
            tv_company_name.setText(recharge.getCompnay_name());

            /* [START] - Data proper not display ("Postpaid") (MAX - 10 character) */
            // tv_product_name.setText(recharge.getProduct_name());
            String productName = recharge.getProduct_name();
            try {
                if (productName.trim().length() > 10) {
                    String subProductName = productName.substring(0, 10);
                    tv_product_name.setText(" " + subProductName + "\n" + productName.substring(10, productName.length()));
                } else {
                    tv_product_name.setText(" " + productName);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                tv_product_name.setText(" " + productName);
            }
            // [END]

            /* [START] - Display operator id (If data not found, hide this field) */
            if (recharge.getOperator_trans_id().trim().length() == 0
                    || recharge.getOperator_trans_id() == null
                    || recharge.getOperator_trans_id().trim().equalsIgnoreCase("null")) {
                llOperatorId.setVisibility(View.GONE);
                txtOperatorId.setText(" " + recharge.getOperator_trans_id());
            } else {
                llOperatorId.setVisibility(View.VISIBLE);
                txtOperatorId.setText(" " + recharge.getOperator_trans_id());
            }
            // [END]

            /*Set color of recharge status*/
            if (recharge.getRecharge_status().equalsIgnoreCase("success")) {
                for (int i = 0; i < colorArrayList.size(); i++) {
                    _color_name = colorArrayList.get(i).getColor_name();
                    if (_color_name.contains("success")) {
                        color_value = colorArrayList.get(i).getColo_value();
                        tv_status.setBackgroundColor(android.graphics.Color.parseColor(color_value));
                    }
                }
                tv_status.setText(recharge.getRecharge_status());
            } else if (recharge.getRecharge_status().equalsIgnoreCase("pending")) {
                for (int i = 0; i < colorArrayList.size(); i++) {
                    _color_name = colorArrayList.get(i).getColor_name();
                    if (_color_name.contains("pending")) {
                        color_value = colorArrayList.get(i).getColo_value();
                        tv_status.setBackgroundColor(android.graphics.Color.parseColor(color_value));
                    }
                }
                tv_status.setText(recharge.getRecharge_status());
            } else if (recharge.getRecharge_status().equalsIgnoreCase("failure")) {
                for (int i = 0; i < colorArrayList.size(); i++) {
                    _color_name = colorArrayList.get(i).getColor_name();
                    if (_color_name.contains("failure")) {
                        color_value = colorArrayList.get(i).getColo_value();
                        tv_status.setBackgroundColor(android.graphics.Color.parseColor(color_value));
                    }
                }
                tv_status.setText(recharge.getRecharge_status());
            }
            /* [START] - recharge_status":"Credit" */
            else if (recharge.getRecharge_status().equalsIgnoreCase("credit")) {
                for (int i = 0; i < colorArrayList.size(); i++) {
                    _color_name = colorArrayList.get(i).getColor_name();
                    if (_color_name.contains("credit")) {
                        color_value = colorArrayList.get(i).getColo_value();
                        tv_status.setBackgroundColor(android.graphics.Color.parseColor(color_value));
                    }
                }
                tv_status.setText(recharge.getRecharge_status());
            }
            // [END]

            String company_logo = databaseHelper.getCompanyLogo(recharge.getCompnay_name());
            if (company_logo.isEmpty()) {
                iv_company_logo.setImageResource(R.drawable.placeholder_icon);
            } else {
                Picasso.with(context).load(company_logo).placeholder(R.drawable.placeholder_icon).into(iv_company_logo);
            }

            // set dialog message
            alertDialogBuilder.setCancelable(false)
                    .setPositiveButton("Submit",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    submitComplainClickListener.onComplainClick((Integer) v.getTag(), edt_report_complain.getText().toString().trim());
                                    dialog.cancel();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show dialog
            if (!alertDialog.isShowing())
                alertDialog.show();
        }
    }
}
