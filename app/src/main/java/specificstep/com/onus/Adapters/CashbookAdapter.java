package specificstep.com.onus.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import specificstep.com.onus.Models.CashbookModel;
import specificstep.com.onus.R;

/**
 * Created by ubuntu on 16/3/17.
 */

public class CashbookAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;
    private ArrayList<CashbookModel> models = null;
    private Context context;

    public CashbookAdapter(Context activity, ArrayList<CashbookModel> _models) {
        context = activity;
        inflater = LayoutInflater.from(activity.getApplicationContext());
        models = _models;
    }

    private class RowHolder {
        private TextView txtPaymentId, txtFrom, txtTo, txtUserType, txtAmount, txtRemarks, txtDateTime;
        // txtBalance
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final RowHolder rowHolder;
        if (convertView == null) {
            rowHolder = new RowHolder();
            convertView = inflater.inflate(R.layout.adapter_cashbook, null);
            rowHolder.txtPaymentId = (TextView) convertView.findViewById(R.id.txt_Adapter_CashBook_PaymentId);
            rowHolder.txtAmount = (TextView) convertView.findViewById(R.id.txt_Adapter_CashBook_Amount);
            // rowHolder.txtBalance = (TextView) convertView.findViewById(R.id.txt_Adapter_CashBook_Balance);
            rowHolder.txtDateTime = (TextView) convertView.findViewById(R.id.txt_Adapter_CashBook_DateTime);
            rowHolder.txtFrom = (TextView) convertView.findViewById(R.id.txt_Adapter_CashBook_PaymentFrom);
            rowHolder.txtTo = (TextView) convertView.findViewById(R.id.txt_Adapter_CashBook_PaymentTo);
            rowHolder.txtRemarks = (TextView) convertView.findViewById(R.id.txt_Adapter_CashBook_Remarks);
            rowHolder.txtUserType = (TextView) convertView.findViewById(R.id.txt_Adapter_CashBook_UserType);
            convertView.setTag(rowHolder);
        } else {
            rowHolder = (RowHolder) convertView.getTag();
        }
        rowHolder.txtPaymentId.setText(models.get(position).paymentId.trim());
        // rowHolder.txtBalance.setText(models.get(position).balance);
        rowHolder.txtDateTime.setText(models.get(position).dateTime.trim());
        rowHolder.txtFrom.setText(models.get(position).paymentFrom.trim());
        rowHolder.txtTo.setText(models.get(position).paymentTo.trim());
        rowHolder.txtRemarks.setText(models.get(position).remarks.trim());
        rowHolder.txtUserType.setText(models.get(position).userType.trim());

        /* [START] - 2017_04_24 - Add RS symbol with amount & Add .00 after amount*/
        String amount = models.get(position).amount;
        // Decimal format
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat format = new DecimalFormat("0.#");
        format.setDecimalFormatSymbols(symbols);
        // Add RS symbol in credit and debit amount
        try {
            if (!TextUtils.equals(amount, "0")) {
                amount = context.getResources().getString(R.string.Rs) + "  " + format.parse(amount).floatValue();
            }
        }
        catch (Exception ex) {
            Log.e("Cash Adapter" ,"Error in decimal number");
            Log.e("Cash Adapter" ,"Error : " + ex.getMessage());
            ex.printStackTrace();
            amount = models.get(position).amount;
        }
        // set amount in text view
        rowHolder.txtAmount.setText(amount);
        // [END]

        return convertView;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return models.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return models.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public CashbookModel getData(int position) {
        return models.get(position);
    }
}
