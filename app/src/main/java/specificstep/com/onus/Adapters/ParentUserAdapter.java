package specificstep.com.onus.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import specificstep.com.onus.Models.ParentUserModel;
import specificstep.com.onus.R;

/**
 * Created by ubuntu on 2/5/17.
 */

public class ParentUserAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;
    private ArrayList<ParentUserModel> models = null;

    public ParentUserAdapter(Context activity, ArrayList<ParentUserModel> _models) {
        inflater = LayoutInflater.from(activity.getApplicationContext());
        models = _models;
    }

    private class RowHolder {
        private TextView txtFirmName, txtMobileNumber, txtUserType, txtName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final RowHolder rowHolder;
        if (convertView == null) {
            rowHolder = new RowHolder();
            convertView = inflater.inflate(R.layout.item_parent_user, null);
            rowHolder.txtFirmName = (TextView) convertView.findViewById(R.id.txt_Item_ParentUser_FirmName);
            rowHolder.txtMobileNumber = (TextView) convertView.findViewById(R.id.txt_Item_ParentUser_MobileNumber);
            rowHolder.txtUserType = (TextView) convertView.findViewById(R.id.txt_Item_ParentUser_UserType);
            rowHolder.txtName = (TextView) convertView.findViewById(R.id.txt_Item_ParentUser_Name);
            convertView.setTag(rowHolder);
        } else {
            rowHolder = (RowHolder) convertView.getTag();
        }

        rowHolder.txtFirmName.setText("Firm Name : " + models.get(position).firmName);
        rowHolder.txtMobileNumber.setText("Mobile No : " + models.get(position).mobileNumber);
        rowHolder.txtUserType.setText("User Type : " + models.get(position).userType);
        rowHolder.txtName.setText("Name : " + models.get(position).name);

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

    public ParentUserModel getData(int position) {
        return models.get(position);
    }
}