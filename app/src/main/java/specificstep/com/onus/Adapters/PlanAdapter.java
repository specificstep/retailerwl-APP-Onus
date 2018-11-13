package specificstep.com.onus.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import specificstep.com.onus.Models.PlanModel;
import specificstep.com.onus.R;

/**
 * Created by ubuntu on 29/5/17.
 */

public class PlanAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;
    private ArrayList<PlanModel> models = null;
    private Context context;

    public PlanAdapter(Context activity, ArrayList<PlanModel> _models) {
        inflater = LayoutInflater.from(activity.getApplicationContext());
        models = _models;
        this.context = activity;
    }

    private class RowHolder {
        private TextView txtDescription, txtPrice, txtValidity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final RowHolder rowHolder;
        if (convertView == null) {
            rowHolder = new RowHolder();
            convertView = inflater.inflate(R.layout.item_plan, null);
            rowHolder.txtDescription = (TextView) convertView.findViewById(R.id.txt_Item_Plan_Description);
            rowHolder.txtPrice = (TextView) convertView.findViewById(R.id.txt_Item_Plan_Price);
            rowHolder.txtValidity = (TextView) convertView.findViewById(R.id.txt_Item_Plan_Validity);
            convertView.setTag(rowHolder);
        } else {
            rowHolder = (RowHolder) convertView.getTag();
        }

        rowHolder.txtDescription.setText(models.get(position).benifit);
        rowHolder.txtPrice.setText(context.getResources().getString(R.string.Rs) + " " + models.get(position).price);
        rowHolder.txtValidity.setText("Validity : " + models.get(position).validity);

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

    public PlanModel getData(int position) {
        return models.get(position);
    }
}