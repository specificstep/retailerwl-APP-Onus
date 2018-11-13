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
import java.util.Arrays;
import java.util.List;

import specificstep.com.onus.Models.NotificationModel;
import specificstep.com.onus.R;

/**
 * Created by ubuntu on 14/3/17.
 */

public class NotificationAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;
    private ArrayList<NotificationModel> models = null;
    // private Context context;

    public NotificationAdapter(Context activity, ArrayList<NotificationModel> _models) {
        // context = activity;
        inflater = LayoutInflater.from(activity.getApplicationContext());
        models = _models;
    }

    private class RowHolder {
        private TextView txtId, txtMessage, txtTitle, txtDateTime, txtReadDateTime;
        private ImageView imgTick;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final RowHolder rowHolder;
        if (convertView == null) {
            rowHolder = new RowHolder();
            convertView = inflater.inflate(R.layout.adapter_notification, null);
            rowHolder.txtId = (TextView) convertView.findViewById(R.id.txt_Item_Notification_Id);
            rowHolder.txtMessage = (TextView) convertView.findViewById(R.id.txt_Item_Notification_Message);
            rowHolder.txtTitle = (TextView) convertView.findViewById(R.id.txt_Item_Notification_Title);
            rowHolder.txtDateTime = (TextView) convertView.findViewById(R.id.txt_Item_Notification_DateTime);
            rowHolder.txtReadDateTime = (TextView) convertView.findViewById(R.id.txt_Item_Notification_ReadDateTime);
            rowHolder.imgTick = (ImageView) convertView.findViewById(R.id.img_Notification_MessageRead);
            convertView.setTag(rowHolder);
        } else {
            rowHolder = (RowHolder) convertView.getTag();
        }

        rowHolder.txtId.setText(models.get(position).id);
        rowHolder.txtTitle.setText(models.get(position).title);
        if (TextUtils.equals(models.get(position).readFlag, "1")) {
            rowHolder.imgTick.setVisibility(View.VISIBLE);
            rowHolder.txtReadDateTime.setVisibility(View.VISIBLE);
            rowHolder.txtReadDateTime.setText("Read : " + models.get(position).readDateTime);
        } else {
            rowHolder.imgTick.setVisibility(View.GONE);
            rowHolder.txtReadDateTime.setVisibility(View.GONE);
        }

        /* [START] - Display message in proper format
            recharge notification message = 9925049355 - Success/Failure/Credit, Aircel, Flexi, 123465789798987, 2017-03-27 06:00:00
            Simple message = App installed successfully and shortcut created.
         */
        String formattedMessage = "";
        String originalMessage = models.get(position).message;
        String notificationDate = "";
        try {
            // Check message contains mobile number or not
            if (originalMessage.contains("-")) {
                formattedMessage = "Number : " + originalMessage.substring(0, originalMessage.indexOf("-")) + "\n";
                originalMessage = originalMessage.substring(originalMessage.indexOf("-") + 1, originalMessage.length());
                // Check message contains comma separated value or not
                if (originalMessage.contains(",")) {
                    // Convert comma separated value in list
                    List<String> notificationMessageItems = Arrays.asList(originalMessage.split("\\s*,\\s*"));
                    // check list contains all value or not
                    if (notificationMessageItems.size() == 5) {
                        // get all value from list
                        formattedMessage += "Transaction Id : " + notificationMessageItems.get(3) + "\n";
                        formattedMessage += "Status : " + notificationMessageItems.get(0) + "\n";
                        formattedMessage += "Company : " + notificationMessageItems.get(1) + "\n";
                        formattedMessage += "Product : " + notificationMessageItems.get(2);
                        // formattedMessage += "Date Time : " + notificationMessageItems.get(4);
                        notificationDate = notificationMessageItems.get(4);
                    } else {
                        // formattedMessage += "Message : " + originalMessage;
                        formattedMessage += originalMessage;
                    }
                } else {
                    // formattedMessage += "Message : " + originalMessage;
                    formattedMessage += originalMessage;
                }
            } else {
                // formattedMessage = "Message : " + originalMessage;
                formattedMessage = originalMessage;
            }
        }
        catch (Exception ex) {
            Log.d("Notification Adapter", "Error while format notification message");
            Log.d("Notification Adapter", "Error : " + ex.getMessage());
            ex.printStackTrace();
            formattedMessage = "Message : " + originalMessage;
            notificationDate = "";
        }
        // [END]

        rowHolder.txtMessage.setText(formattedMessage);
        if (TextUtils.isEmpty(notificationDate))
            rowHolder.txtDateTime.setText("Date Time : " + models.get(position).receiveDateTime);
        else
            rowHolder.txtDateTime.setText("Date Time : " + notificationDate);

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

    public NotificationModel getData(int position) {
        return models.get(position);
    }
}
