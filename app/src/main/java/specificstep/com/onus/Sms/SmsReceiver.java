package specificstep.com.onus.Sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import specificstep.com.onus.GlobalClasses.Constants;

/**
 * Created by programmer044 on 20/02/17.
 */

public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Constants.IS_RECEIVE_MESSAGE) {
            try {
                Constants constants = new Constants();
                Bundle data = intent.getExtras();
                if (data != null) {

                    Object[] pdus = (Object[]) data.get("pdus");

                    for (int i = 0; i < pdus.length; i++) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

                        String sender = smsMessage.getDisplayOriginatingAddress();
                        Log.d("Receiver", "sender : " + sender);

                        String[] separated = sender.split("-");
                        Log.d("Receiver", "separated : " + separated);
                        Log.d("Receiver", "smsMessage : " + smsMessage.getMessageBody());
                        Log.d("Receiver", "separated.length : " + separated.length);

                        try {
                            if (separated.length > 1) {
                                if (separated[1].length() == 6) {
                                    if (separated[1].compareTo(constants.SENDER_ID) == 0) {
                                        Log.d("Receiver", "separated[0] : " + separated[0]);
                                        Log.d("Receiver", "separated[1] : " + separated[1]);
                                        String messageBody = smsMessage.getMessageBody();
                                        Log.d("Receiver", "messageBody : " + messageBody);
                                        if (!mListener.equals(null))
                                            mListener.messageReceived(messageBody);
                                    }
                                }
                            }
                        }
                        catch (Exception ex) {
                            Log.d("Receiver", "Error in message receiver 1 : " + ex.toString());
                            ex.printStackTrace();
                        }
                    }
                }
            }
            catch (Exception ex) {
                Log.d("Receiver", "Error in message receiver 2 : " + ex.toString());
                ex.printStackTrace();
            }
        }
    }

    public static void bindListener(SmsListener listener) {
        try {
            mListener = listener;
        }
        catch (Exception ex) {

        }
    }
}
