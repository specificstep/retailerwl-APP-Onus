package specificstep.com.onus.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPrefs {
    private SharedPreferences myPrefs;
    private SharedPreferences.Editor prefEditor;

    public MyPrefs(Context context, String prefsName) {
        myPrefs = context.getSharedPreferences(prefsName, 0);
    }

    public void saveString(String key, String value) {
        prefEditor = myPrefs.edit();
        prefEditor.putString(key, value);
        prefEditor.commit();
    }

    public String retriveString(String key, String value) {
        return myPrefs.getString(key, value);
    }

    public void clearAllData() {
        prefEditor = myPrefs.edit();
        prefEditor.clear();
        prefEditor.commit();
    }
}
