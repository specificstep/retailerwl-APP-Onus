package specificstep.com.onus.utility;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ubuntu on 9/3/17.
 */

public class Utility {

    private static int time;

    public static String getString(EditText edt) {
        return edt.getText().toString().trim();
    }

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void amountToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(20);
        toast.show();
    }

    public String getContactTypeName(String typeIndex) {
        String typeName = "";
        if (TextUtils.equals(typeIndex, "0")) {
            typeName = "Custom";
        } else if (TextUtils.equals(typeIndex, "1")) {
            typeName = "Home";
        } else if (TextUtils.equals(typeIndex, "2")) {
            typeName = "Mobile";
        } else if (TextUtils.equals(typeIndex, "3")) {
            typeName = "Work";
        } else if (TextUtils.equals(typeIndex, "4")) {
            typeName = "Fax Work";
        } else if (TextUtils.equals(typeIndex, "5")) {
            typeName = "Fax Home";
        } else if (TextUtils.equals(typeIndex, "6")) {
            typeName = "Pager";
        } else if (TextUtils.equals(typeIndex, "7")) {
            typeName = "Other";
        } else if (TextUtils.equals(typeIndex, "8")) {
            typeName = "Call Back";
        } else if (TextUtils.equals(typeIndex, "9")) {
            typeName = "Car";
        } else if (TextUtils.equals(typeIndex, "10")) {
            typeName = "Company Main";
        } else if (TextUtils.equals(typeIndex, "11")) {
            typeName = "ISDN";
        } else if (TextUtils.equals(typeIndex, "12")) {
            typeName = "Main";
        } else if (TextUtils.equals(typeIndex, "13")) {
            typeName = "Other Fax";
        } else if (TextUtils.equals(typeIndex, "14")) {
            typeName = "Radio";
        } else if (TextUtils.equals(typeIndex, "15")) {
            typeName = "Telex";
        } else if (TextUtils.equals(typeIndex, "16")) {
            typeName = "TTY TDD";
        } else if (TextUtils.equals(typeIndex, "17")) {
            typeName = "Work Mobile";
        } else if (TextUtils.equals(typeIndex, "18")) {
            typeName = "Work Pager";
        } else if (TextUtils.equals(typeIndex, "19")) {
            typeName = "Assistant";
        } else if (TextUtils.equals(typeIndex, "20")) {
            typeName = "MMS";
        } else {
            typeName = "Other";
        }
        return typeName;
    }

    public String formattedContactNumber(String mobileNumber) {
        String contactNumber = mobileNumber;
        String myNumbers = "";
        String finalNumber = "";
        try {
            for (int i = 0; i < contactNumber.length(); i++) {
                if (Character.isDigit(contactNumber.charAt(i))) {
                    myNumbers += contactNumber.charAt(i);
                }
            }
            if (myNumbers.length() > 0) {
                finalNumber = myNumbers;
                LogMessage.d("Number length : " + finalNumber.length());
                if (finalNumber.length() > 10) {
                    finalNumber = myNumbers.substring((myNumbers.length() - 10), myNumbers.length());
                }
            }
        }
        catch (Exception ex) {
            LogMessage.e("Error in parse contact number.");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
            finalNumber = "";
        }
        return finalNumber;
    }

    public String formattedDTHNumber(String dthNumber) {
        String contactNumber = dthNumber;
        String myNumbers = "";
        String finalNumber = "";
        try {
            for (int i = 0; i < contactNumber.length(); i++) {
                if (Character.isDigit(contactNumber.charAt(i))) {
                    myNumbers += contactNumber.charAt(i);
                }
            }
            if (myNumbers.length() > 0) {
                finalNumber = myNumbers;
            }
        }
        catch (Exception ex) {
            LogMessage.e("Error in parse contact number.");
            LogMessage.e("Error : " + ex.getMessage());
            ex.printStackTrace();
            finalNumber = "";
        }
        return finalNumber;
    }
}
