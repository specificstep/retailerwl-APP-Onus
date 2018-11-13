package specificstep.com.onus.GlobalClasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by ubuntu on 12/1/17.
 */

public class SMSReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("Receiver", "OnReceive ++ >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        Bundle bndl = intent.getExtras();
        SmsMessage[] msg = null;
        String str = "";
        //   SmsDatabaeHelper db = new SmsDatabaeHelper(context);

        if (null != bndl) {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bndl.get("pdus");
            msg = new SmsMessage[pdus.length];
            for (int i = 0; i < msg.length; i++) {
                msg[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                str += "SMS From " + msg[i].getOriginatingAddress();
                str += " :\r\n";
                str += msg[i].getMessageBody().toString();
                str += "\n";

                Log.e("Full sms...", " " + str);

                Log.d("Receiver", "msg[i].getMessageBody() : " + msg[i].getMessageBody());
                Log.d("Receiver", "msg.length : " + msg.length);

                Log.d("Receiver", "msg[0] : " + msg[0]);
                Log.d("Receiver", "msg[1] : " + msg[1]);

                String CurrentString = msg[i].getMessageBody().toString();
                String[] separated = CurrentString.split(":");

                //  String title_msg=msg[i].getOriginatingAddress();

                //CommanClass.AUTO_OTP= separated[1].trim();

                Log.e("Incomeing msg...", " " + separated[1]);

                // db.addRecord(new SmsInfo(msg[i].getOriginatingAddress(), msg[i].getMessageBody().toString()));
            }
        }
    }
}
