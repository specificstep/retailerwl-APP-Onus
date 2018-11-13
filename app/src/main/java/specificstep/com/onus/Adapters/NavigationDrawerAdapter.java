package specificstep.com.onus.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import specificstep.com.onus.GlobalClasses.Constants;
import specificstep.com.onus.R;

/**
 * Created by ubuntu on 5/4/17.
 */

public class NavigationDrawerAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;
    private ArrayList<String> models = null;

    public NavigationDrawerAdapter(Context activity, ArrayList<String> _models) {
        inflater = LayoutInflater.from(activity.getApplicationContext());
        models = _models;
    }

    private class RowHolder {
        private TextView txtTitle, txtMessage;
        private ImageView imgMenuImage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final RowHolder rowHolder;
        if (convertView == null) {
            rowHolder = new RowHolder();
            convertView = inflater.inflate(R.layout.item_menu, null);
            rowHolder.txtTitle = (TextView) convertView.findViewById(R.id.txt_Item_NavigationMenu_Title);
            rowHolder.txtMessage = (TextView) convertView.findViewById(R.id.txt_Item_NavigationMenu_Message);
            rowHolder.imgMenuImage = (ImageView) convertView.findViewById(R.id.img_Item_NavigationViewMenu_MenuImage);
            convertView.setTag(rowHolder);
        } else {
            rowHolder = (RowHolder) convertView.getTag();
        }
        rowHolder.txtTitle.setText(models.get(position));

        if (TextUtils.equals(models.get(position), "Home")) {
            rowHolder.imgMenuImage.setImageResource(R.drawable.ic_home);
        } else if (TextUtils.equals(models.get(position), "Recharge")) {
            rowHolder.imgMenuImage.setImageResource(R.drawable.ic_menu_recharge);
        } else if (TextUtils.equals(models.get(position), "Transaction Search")) {
            rowHolder.imgMenuImage.setImageResource(R.drawable.ic_menu_trans_search);
        } else if (TextUtils.equals(models.get(position), "Recent Transaction")) {
            rowHolder.imgMenuImage.setImageResource(R.drawable.ic_menu_rec_trans);
        } else if (TextUtils.equals(models.get(position), "Cash Book")) {
            rowHolder.imgMenuImage.setImageResource(R.drawable.ic_menu_trans_search);
        } else if (TextUtils.equals(models.get(position), "Update Data")) {
            rowHolder.imgMenuImage.setImageResource(R.drawable.ic_update_button);
        } else if (TextUtils.equals(models.get(position), "Change Password")) {
            rowHolder.imgMenuImage.setImageResource(R.drawable.ic_menu_change_password);
        } else if (TextUtils.equals(models.get(position), "Notification")) {
            rowHolder.imgMenuImage.setImageResource(R.drawable.ic_notifications_black_24dp);
        } else if (TextUtils.equals(models.get(position), "Parent User")) {
            rowHolder.imgMenuImage.setImageResource(R.drawable.ic_person);
        } else if (TextUtils.equals(models.get(position), "Log Out")) {
            rowHolder.imgMenuImage.setImageResource(R.drawable.ic_menu_logout);
        } else {
            rowHolder.imgMenuImage.setImageResource(R.drawable.ic_menu_logout);
        }
        // display notification counter in drawer
        if (TextUtils.equals(models.get(position), "Notification")) {
            int totalUnreadNotification = 0;
            try {
                totalUnreadNotification = Integer.parseInt(Constants.TOTAL_UNREAD_NOTIFICATION);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                Log.d("Notification Adapter", "Notification : " + "Error : " + ex.toString());
                totalUnreadNotification = 0;
            }
            if (totalUnreadNotification > 0) {
                rowHolder.txtMessage.setVisibility(View.VISIBLE);
                rowHolder.txtMessage.setText(Constants.TOTAL_UNREAD_NOTIFICATION);
                // reset custom navigation menu
            } else {
                rowHolder.txtMessage.setVisibility(View.GONE);
            }
        } else {
            rowHolder.txtMessage.setVisibility(View.GONE);
        }
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

    public String getData(int position) {
        return models.get(position);
    }
}

