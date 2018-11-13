package specificstep.com.onus.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import specificstep.com.onus.Models.Color;
import specificstep.com.onus.Models.Company;
import specificstep.com.onus.Models.Default;
import specificstep.com.onus.Models.Product;
import specificstep.com.onus.Models.State;
import specificstep.com.onus.Models.User;

/**
 * Created by ubuntu on 13/1/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 9;

    Context context;

    private static final String DATABASE_NAME = "RechargeEngine";

    private static final String TABLE_DEFAULT_SETTINGS = "default_settings";

    private static final String KEY_ID = "id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_STATE_ID = "state_id";
    private static final String KEY_STATE_NAME = "state_name";

    private static final String TABLE_COMPANY = "company";

    private static final String KEY_COMPANY_ITEM_ID = "item_id";
    private static final String KEY_COMPANY_NAME = "company_name";
    private static final String KEY_LOGO = "logo";
    private static final String KEY_SERVICE_TYPE = "service_type";

    private static final String TABLE_PRODUCT = "product";

    private static final String KEY_PRODUCT_ITEM_ID = "item_id";
    private static final String KEY_PRODUCT_NAME = "product_name";
    private static final String KEY_COMPANY_ID = "company_id";
    private static final String KEY_PRODUCT_LOGO = "product_logo";

    private static final String TABLE_STATE = "state";

    private static final String KEY_CIRCLE_ID = "circle_id";
    private static final String KEY_CIRCLE_NAME = "circle_name";

    private static final String TABLE_USER = "user";

    private static final String KEY_OTP_CODE = "otp_code";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PWD = "password";
    private static final String KEY_REMEMBER_ME = "remember_me";
    private static final String KEY_REG_DATE = "reg_date";

    private static final String TABLE_STATUS_COLOR = "status_color";

    private static final String KEY_COLOR_NAME = "name";
    private static final String KEY_COLOR_VALUE = "value";

    /* [START] - Notification table name and field */
//    private static final String TABLE_NOTIFY = "tbl_notify";
//    private static final String TABLE_NOTIFY_1 = "tbl_notify_1";

//    private static final String KEY_NOTIFY_ID = "notifyId";
//    private static final String KEY_MESSAGE = "message";
//    private static final String KEY_TITLE = "title";
//    private static final String KEY_RECEIVE_DATETIME = "receive_dateTime";
//    private static final String KEY_SAVE_TIME = "save_dateTime";
//    private static final String KEY_READ_DATETIME = "read_dateTime";
//    private static final String KEY_READ_FLAG = "readFlag";
//    private static final String KEY_DELETE_FLAG = "deleteFlag";
    // [END]

    public DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_DEFAULT_SETTINGS = "CREATE TABLE " + TABLE_DEFAULT_SETTINGS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER_ID + " TEXT," + KEY_STATE_ID + " TEXT," +
                KEY_STATE_NAME + " TEXT" + ")";

        String CREATE_TABLE_COMPANY = "CREATE TABLE " + TABLE_COMPANY + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," + KEY_COMPANY_ITEM_ID + " TEXT," + KEY_COMPANY_NAME + " TEXT," +
                KEY_LOGO + " TEXT, " + KEY_SERVICE_TYPE + " TEXT" + ")";

        String CREATE_TABLE_PRODUCT = "CREATE TABLE " + TABLE_PRODUCT + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," + KEY_PRODUCT_ITEM_ID + " TEXT," + KEY_PRODUCT_NAME + " TEXT," +
                KEY_COMPANY_ID + " TEXT," + KEY_PRODUCT_LOGO + " TEXT," + KEY_SERVICE_TYPE + " TEXT" + ")";

        String CREATE_TABLE_STATE = "CREATE TABLE " + TABLE_STATE + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," + KEY_CIRCLE_ID + " TEXT," + KEY_CIRCLE_NAME + " TEXT" + ")";

        String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER_ID + " TEXT," + KEY_OTP_CODE + " TEXT," +
                KEY_USER_NAME + " TEXT," + KEY_DEVICE_ID + " TEXT," + KEY_NAME + " TEXT," + KEY_PWD + " TEXT," + KEY_REMEMBER_ME + " TEXT," +
                KEY_REG_DATE + " TEXT" + ")";

        String CREATE_TABLE_STATUS_COLOR = "CREATE TABLE " + TABLE_STATUS_COLOR + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," + KEY_COLOR_NAME + " TEXT," + KEY_COLOR_VALUE + " TEXT" + ")";

        /* [START] - create Notification table */
        // Create notification table
        // + KEY_NOTIFY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//        String CREATE_TABLE_NOTIFICATION = "CREATE TABLE " + TABLE_NOTIFY + "("
//                + KEY_NOTIFY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + KEY_MESSAGE + " TEXT,"
//                + KEY_TITLE + " TEXT,"
//                + KEY_RECEIVE_DATETIME + " TEXT,"
//                + KEY_SAVE_TIME + " TEXT,"
//                + KEY_READ_DATETIME + " TEXT,"
//                // + KEY_DELETE_FLAG + " TEXT,"
//                + KEY_READ_FLAG + " TEXT" + ")";
        // [END]

//        String CREATE_TABLE_NOTIFICATION_1 = "CREATE TABLE " + TABLE_NOTIFY_1 + "("
//                + KEY_NOTIFY_ID + " TEXT,"
//                + KEY_MESSAGE + " TEXT,"
//                + KEY_TITLE + " TEXT,"
//                + KEY_RECEIVE_DATETIME + " TEXT,"
//                + KEY_SAVE_TIME + " TEXT,"
//                // + KEY_DELETE_FLAG + " TEXT,"
//                + KEY_READ_FLAG + " TEXT" + ")";

        db.execSQL(CREATE_TABLE_DEFAULT_SETTINGS);
        db.execSQL(CREATE_TABLE_COMPANY);
        db.execSQL(CREATE_TABLE_PRODUCT);
        db.execSQL(CREATE_TABLE_STATE);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_STATUS_COLOR);
//        db.execSQL(CREATE_TABLE_NOTIFICATION);
//        db.execSQL(CREATE_TABLE_NOTIFICATION_1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEFAULT_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPANY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATUS_COLOR);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFY);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFY_1);
        onCreate(db);
    }

    public void addColors(ArrayList<Color> colorArrayList) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (int i = 0; i < colorArrayList.size(); i++) {

            values.put(KEY_COLOR_NAME, colorArrayList.get(i).getColor_name());
            values.put(KEY_COLOR_VALUE, colorArrayList.get(i).getColo_value());

            db.insert(TABLE_STATUS_COLOR, null, values);
        }

        db.close(); // Closing database connection
    }

//    /**
//     * Add notification data
//     *
//     * @param model NotificationModel as param
//     */
//    public void addNotificationData(NotificationModel model) {
//        ContentValues values = new ContentValues();
//        SQLiteDatabase db = this.getWritableDatabase();
//        // values.put(KEY_NOTIFY_ID, model.id);
//        values.put(KEY_MESSAGE, model.message);
//        values.put(KEY_TITLE, model.title);
//        values.put(KEY_RECEIVE_DATETIME, model.receiveDateTime);
//        values.put(KEY_SAVE_TIME, model.saveDateTime);
//        values.put(KEY_READ_FLAG, model.readFlag);
//        values.put(KEY_READ_DATETIME, model.readDateTime);
//        // values.put(KEY_DELETE_FLAG, model.deleteFlag);
//        long count = db.insert(TABLE_NOTIFY, null, values);
//        Log.d("Database", "Insert : " + count);
//
//        db.close(); // Closing database connection
//
//        // delete last record
//        ArrayList<NotificationModel> notificationModels = getLastNotificationData();
//        if (notificationModels.size() > 0) {
//            NotificationModel notificationModel = notificationModels.get(0);
//            int lastId = 1;
//            try {
//                lastId = Integer.parseInt(notificationModel.id);
//                Log.d("Database", "Last id : " + lastId);
//            }
//            catch (Exception ex) {
//                Log.d("Database", "Error while parse id");
//                ex.printStackTrace();
//                lastId = 1;
//            }
//            if (lastId > 100) {
//                NotificationModel deleteNotification = getFirst_1_NotificationData().get(0);
//                String deleteRecord = deleteNotification.id;
//                String whereClause = KEY_NOTIFY_ID + " ='" + deleteRecord + "'";
//                deleteNotification(whereClause);
//            }
//        }
//
//        Constants.TOTAL_UNREAD_NOTIFICATION = getNumberOfNotificationRecord() + "";
//
//        // Backup database file logic for testing
////        if (count == 1000) {
////            Log.d("Backup", "Counter : " + count);
////            backupDatabase(count);
////        }
////        else if (count == 2000) {
////            Log.d("Backup", "Counter : " + count);
////            backupDatabase(count);
////        } else if (count == 3000) {
////            Log.d("Backup", "Counter : " + count);
////            backupDatabase(count);
////        } else if (count == 4000) {
////            Log.d("Backup", "Counter : " + count);
////            backupDatabase(count);
////        } else if (count == 5000) {
////            Log.d("Backup", "Counter : " + count);
////            backupDatabase(count);
////        }
//    }

//    public void addNotificationData_1(NotificationModel model) {
//
//        ContentValues values = new ContentValues();
//
//        int totalRecord = getNumberOfNotificationRecord();
//        String lastId = getLastNotificationId();
//        int setId = 0;
//        if(totalRecord == 200) {
//            if (TextUtils.equals(lastId, "100")) {
//                delete_101To200_Notification();
//                setId = Integer.parseInt(lastId) + 1;
//            } else if (TextUtils.equals(lastId, "200")) {
//                delete_1To100_Notification();
//                if(TextUtils.equals(lastId, "200"))
//                    setId = 1;
//                else
//                    setId = Integer.parseInt(lastId) + 1;
//            }
//        }
//        else {
//            setId = Integer.parseInt(lastId) + 1;
//        }
//        model.id = setId + "";
//        SQLiteDatabase db = this.getWritableDatabase();
//        values.put(KEY_NOTIFY_ID, model.id);
//        values.put(KEY_MESSAGE, model.message);
//        values.put(KEY_TITLE, model.title);
//        values.put(KEY_RECEIVE_DATETIME, model.receiveDateTime);
//        values.put(KEY_SAVE_TIME, model.saveDateTime);
//        values.put(KEY_READ_FLAG, model.readFlag);
////        values.put(KEY_DELETE_FLAG, model.deleteFlag);
//        long count = db.insert(TABLE_NOTIFY_1, null, values);
//        Log.d("Database", "Insert : " + count);
//
//        db.close(); // Closing database connection
//    }

//    public void updateNotification(NotificationModel model, String notificationId) {
//        String whereClause = KEY_NOTIFY_ID + "='" + notificationId + "'";
//        SQLiteDatabase db = this.getWritableDatabase();
//        final ContentValues values = new ContentValues();
//        values.put(KEY_NOTIFY_ID, model.id);
//        values.put(KEY_MESSAGE, model.message);
//        values.put(KEY_TITLE, model.title);
//        values.put(KEY_RECEIVE_DATETIME, model.receiveDateTime);
//        values.put(KEY_SAVE_TIME, model.saveDateTime);
//        values.put(KEY_READ_FLAG, "1");
//        values.put(KEY_READ_DATETIME, model.readDateTime);
////        values.put(KEY_DELETE_FLAG, model.deleteFlag);
//        Log.d("Database", "Update Notification data : " + db.update(TABLE_NOTIFY, values, whereClause, null));
//        db.close();
////        backupDatabase(1);
//    }

//    public void deleteOldNotification() {
//        ArrayList<NotificationModel> notificationModels = getFirst_100_NotificationData();
//        String whereClause = "";
//        if (notificationModels.size() > 0) {
//            for (int i = 0; i < notificationModels.size(); i++) {
//                NotificationModel notificationModel = notificationModels.get(i);
//                if (TextUtils.equals(notificationModel.deleteFlag, "1")) {
//                    whereClause = KEY_NOTIFY_ID + " ='" + notificationModel.id + "'";
//                    Log.d("Database", "Delete : " + whereClause);
//                    SQLiteDatabase db = this.getWritableDatabase();
//                    db.delete(TABLE_NOTIFY, whereClause, null);
//                    db.close();
//                }
//            }
//        }
//    }

//    public void delete_1To100_Notification() {
//        // delete from Table where id > 79 and id < 296
//        String whereClause = KEY_NOTIFY_ID + " > 0 AND " + KEY_NOTIFY_ID + " < 101";
//        Log.d("Database", "Delete : " + whereClause);
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_NOTIFY_1, whereClause, null);
//        db.close();
//    }

//    public void delete_101To200_Notification() {
//        String whereClause = KEY_NOTIFY_ID + " > 100 AND " + KEY_NOTIFY_ID + " < 201";
//        Log.d("Database", "Delete : " + whereClause);
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_NOTIFY_1, whereClause, null);
//        db.close();
//    }

//    public void deleteNotification(String whereClause) {
//        Log.d("Database", "Delete : " + whereClause);
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_NOTIFY, whereClause, null);
//        db.close();
//    }

//    public ArrayList<NotificationModel> getNotificationData(String notificationId) {
//        ArrayList<NotificationModel> stateArrayList = new ArrayList<NotificationModel>();
//        // Select All Query
//        String selectQuery = "SELECT * FROM " + TABLE_NOTIFY + " WHERE " + KEY_NOTIFY_ID + " ='" + notificationId + "'";
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                NotificationModel notificationModel = new NotificationModel();
//                notificationModel.id = cursor.getString(cursor.getColumnIndex(KEY_NOTIFY_ID));
//                notificationModel.title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
//                notificationModel.message = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE));
//                notificationModel.receiveDateTime = cursor.getString(cursor.getColumnIndex(KEY_RECEIVE_DATETIME));
//                notificationModel.saveDateTime = cursor.getString(cursor.getColumnIndex(KEY_SAVE_TIME));
//                notificationModel.readFlag = cursor.getString(cursor.getColumnIndex(KEY_READ_FLAG));
//                notificationModel.readDateTime = cursor.getString(cursor.getColumnIndex(KEY_READ_DATETIME));
////                notificationModel.deleteFlag = cursor.getString(cursor.getColumnIndex(KEY_DELETE_FLAG));
//                stateArrayList.add(notificationModel);
//            }
//            while (cursor.moveToNext());
//        }
//        db.close();
//        return stateArrayList;
//    }

//    public ArrayList<NotificationModel> getNotificationData() {
//        ArrayList<NotificationModel> stateArrayList = new ArrayList<NotificationModel>();
//        // Select All Query
//        String selectQuery = "SELECT * FROM " + TABLE_NOTIFY;
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                NotificationModel notificationModel = new NotificationModel();
//                notificationModel.id = cursor.getString(cursor.getColumnIndex(KEY_NOTIFY_ID));
//                notificationModel.title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
//                notificationModel.message = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE));
//                notificationModel.receiveDateTime = cursor.getString(cursor.getColumnIndex(KEY_RECEIVE_DATETIME));
//                notificationModel.saveDateTime = cursor.getString(cursor.getColumnIndex(KEY_SAVE_TIME));
//                notificationModel.readFlag = cursor.getString(cursor.getColumnIndex(KEY_READ_FLAG));
//                notificationModel.readDateTime = cursor.getString(cursor.getColumnIndex(KEY_READ_DATETIME));
////                notificationModel.deleteFlag = cursor.getString(cursor.getColumnIndex(KEY_DELETE_FLAG));
//                stateArrayList.add(notificationModel);
//            }
//            while (cursor.moveToNext());
//        }
//        db.close();
//        return stateArrayList;
//    }

//    public ArrayList<NotificationModel> getNotificationData_OrderBy() {
//        ArrayList<NotificationModel> stateArrayList = new ArrayList<NotificationModel>();
//        // Select All Query
//        // select * from tbl_notify order by readFlag
//        // select * from tbl_notify order by readFlag, read_dateTime
//        // String selectQuery = "SELECT * FROM " + TABLE_NOTIFY + " ORDER BY " + KEY_READ_FLAG;
//        // select * from tbl_notify order by readFlag, read_dateTime DESC
//        // String selectQuery = "SELECT * FROM " + TABLE_NOTIFY + " ORDER BY " + KEY_READ_FLAG + " , " + KEY_READ_DATETIME;
//        // String selectQuery = "SELECT * FROM " + TABLE_NOTIFY + " ORDER BY " + KEY_READ_FLAG + " , " + KEY_READ_DATETIME + " DESC";
//        // String selectQuery = "SELECT * FROM " + TABLE_NOTIFY + " ORDER BY " + KEY_READ_FLAG + " , " + KEY_READ_DATETIME;
//        // select * from tbl_notify order by readFlag,  receive_dateTime DESC
//        String selectQuery = "SELECT * FROM " + TABLE_NOTIFY + " ORDER BY " + KEY_READ_FLAG + " , " + KEY_RECEIVE_DATETIME + " DESC";
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                NotificationModel notificationModel = new NotificationModel();
//                notificationModel.id = cursor.getString(cursor.getColumnIndex(KEY_NOTIFY_ID));
//                notificationModel.title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
//                notificationModel.message = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE));
//                notificationModel.receiveDateTime = cursor.getString(cursor.getColumnIndex(KEY_RECEIVE_DATETIME));
//                notificationModel.saveDateTime = cursor.getString(cursor.getColumnIndex(KEY_SAVE_TIME));
//                notificationModel.readFlag = cursor.getString(cursor.getColumnIndex(KEY_READ_FLAG));
//                notificationModel.readDateTime = cursor.getString(cursor.getColumnIndex(KEY_READ_DATETIME));
////                notificationModel.deleteFlag = cursor.getString(cursor.getColumnIndex(KEY_DELETE_FLAG));
//                stateArrayList.add(notificationModel);
//            }
//            while (cursor.moveToNext());
//        }
//        db.close();
//
//        return stateArrayList;
//    }

//    public ArrayList<NotificationModel> getLastNotificationData() {
//        ArrayList<NotificationModel> stateArrayList = new ArrayList<NotificationModel>();
//        // Select last record
//        // SELECT * FROM tbl_notify ORDER BY id DESC LIMIT 1
//        String selectQuery = "SELECT * FROM " + TABLE_NOTIFY + " ORDER BY " + KEY_NOTIFY_ID + " DESC LIMIT 1";
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        // looping through single rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                NotificationModel notificationModel = new NotificationModel();
//                notificationModel.id = cursor.getString(cursor.getColumnIndex(KEY_NOTIFY_ID));
//                notificationModel.title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
//                notificationModel.message = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE));
//                notificationModel.receiveDateTime = cursor.getString(cursor.getColumnIndex(KEY_RECEIVE_DATETIME));
//                notificationModel.saveDateTime = cursor.getString(cursor.getColumnIndex(KEY_SAVE_TIME));
//                notificationModel.readFlag = cursor.getString(cursor.getColumnIndex(KEY_READ_FLAG));
//                notificationModel.readDateTime = cursor.getString(cursor.getColumnIndex(KEY_READ_DATETIME));
////                notificationModel.deleteFlag = cursor.getString(cursor.getColumnIndex(KEY_DELETE_FLAG));
//                stateArrayList.add(notificationModel);
//            }
//            while (cursor.moveToNext());
//        }
//        db.close();
//        return stateArrayList;
//    }

//    public ArrayList<NotificationModel> getFirst_1_NotificationData() {
//        ArrayList<NotificationModel> stateArrayList = new ArrayList<NotificationModel>();
//        // Select last record
//        // SELECT * FROM tbl_notify ORDER BY id ASC LIMIT 1
//        String selectQuery = "SELECT * FROM " + TABLE_NOTIFY + " ORDER BY " + KEY_NOTIFY_ID + " ASC LIMIT 1";
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        // looping through single rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                NotificationModel notificationModel = new NotificationModel();
//                notificationModel.id = cursor.getString(cursor.getColumnIndex(KEY_NOTIFY_ID));
//                notificationModel.title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
//                notificationModel.message = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE));
//                notificationModel.receiveDateTime = cursor.getString(cursor.getColumnIndex(KEY_RECEIVE_DATETIME));
//                notificationModel.saveDateTime = cursor.getString(cursor.getColumnIndex(KEY_SAVE_TIME));
//                notificationModel.readFlag = cursor.getString(cursor.getColumnIndex(KEY_READ_FLAG));
//                notificationModel.readDateTime = cursor.getString(cursor.getColumnIndex(KEY_READ_DATETIME));
////                notificationModel.deleteFlag = cursor.getString(cursor.getColumnIndex(KEY_DELETE_FLAG));
//                stateArrayList.add(notificationModel);
//            }
//            while (cursor.moveToNext());
//        }
//        db.close();
//        return stateArrayList;
//    }

//    public String getLastNotificationId() {
//        String lastNotificationId = "0";
//        // Select last record id
//        String selectQuery = "SELECT * FROM " + TABLE_NOTIFY_1;
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        // looping through single rows and adding las notification variable
//        if (cursor.moveToFirst()) {
//            try {
//                cursor.moveToPosition(cursor.getCount() - 1);
//                lastNotificationId = cursor.getString(cursor.getColumnIndex(KEY_NOTIFY_ID));
//            }
//            catch (Exception ex) {
//                Log.d("Database", "getLastNotificationId - Error : " + ex.toString());
//                lastNotificationId = "0";
//            }
//        }
//        db.close();
//        Log.d("Database", "lastNotificationId : " + lastNotificationId);
//        return lastNotificationId;
//    }

//    public int getNumberOfNotificationRecord() {
//        int numberOfRow = 0;
//        // Select last record id
//        // SELECT * FROM tbl_notify ORDER BY id DESC LIMIT 1
//        String selectQuery = "SELECT * FROM " + TABLE_NOTIFY + " WHERE " + KEY_READ_FLAG + " ='0'";
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        // get number of row in table
//        numberOfRow = cursor.getCount();
//        db.close();
////        Log.d("Database", "numberOfRow : " + numberOfRow);
//        return numberOfRow;
//    }

//    public int getAllNotificationRecordCounter() {
//        int numberOfRow = 0;
//        // Select last record id
//        // SELECT * FROM tbl_notify ORDER BY id DESC LIMIT 1
//        String selectQuery = "SELECT * FROM " + TABLE_NOTIFY;
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        // get number of row in table
//        numberOfRow = cursor.getCount();
//        db.close();
////        Log.d("Database", "numberOfRow : " + numberOfRow);
//        return numberOfRow;
//    }

    /* [START] - Get last notification id */
//    public String getLastNotificationId() {
//        String lastRecordId = "0";
//        // SELECT * FROM tbl_notify ORDER BY notifyId DESC LIMIT 1;
//        String selectQuery = "SELECT * FROM " + TABLE_NOTIFY + " ORDER BY " + KEY_NOTIFY_ID + " DESC LIMIT 1";
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        // looping through all rows and get last record id
//        if (cursor.moveToFirst()) {
//            do {
//                lastRecordId = cursor.getString(cursor.getColumnIndex(KEY_NOTIFY_ID));
//            }
//            while (cursor.moveToNext());
//        }
//        db.close();
//        Log.d("Database", "Last record Id : " + lastRecordId);
//        return lastRecordId;
//    }
    // [END]

    public void addDefaultSettings(String user_id, String state_id, String state_name) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user_id);
        values.put(KEY_STATE_ID, state_id);
        values.put(KEY_STATE_NAME, state_name);
        db.insert(TABLE_DEFAULT_SETTINGS, null, values);

        db.close(); // Closing database connection
    }

    public void addUserDetails(String user_id, String otp_code, String user_name, String device_id, String name, String remember_me) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_USER_ID, user_id);
        values.put(KEY_OTP_CODE, otp_code);
        values.put(KEY_USER_NAME, user_name);
        values.put(KEY_DEVICE_ID, device_id);
        values.put(KEY_NAME, name);
        values.put(KEY_REMEMBER_ME, remember_me);
        db.insert(TABLE_USER, null, values);

        db.close(); // Closing database connection
    }

    public void addCompanysDetails(ArrayList<Company> companyArrayList) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < companyArrayList.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_COMPANY_ITEM_ID, companyArrayList.get(i).getId());
            values.put(KEY_COMPANY_NAME, companyArrayList.get(i).getCompany_name());
            values.put(KEY_LOGO, companyArrayList.get(i).getLogo());
            values.put(KEY_SERVICE_TYPE, companyArrayList.get(i).getService_type());
            db.insert(TABLE_COMPANY, null, values);

        }
        db.close(); // Closing database connection
    }

    public void addProductsDetails(ArrayList<Product> productArrayList) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < productArrayList.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_PRODUCT_ITEM_ID, productArrayList.get(i).getId());
            values.put(KEY_PRODUCT_NAME, productArrayList.get(i).getProduct_name());
            values.put(KEY_COMPANY_ID, productArrayList.get(i).getCompany_id());
            values.put(KEY_PRODUCT_LOGO, productArrayList.get(i).getProduct_logo());
            values.put(KEY_SERVICE_TYPE, productArrayList.get(i).getService_type());
            db.insert(TABLE_PRODUCT, null, values);
        }
        db.close(); // Closing database connection
    }

    public void addStatesDetails(ArrayList<State> stateArrayList) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < stateArrayList.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_CIRCLE_ID, stateArrayList.get(i).getCircle_id());
            values.put(KEY_CIRCLE_NAME, stateArrayList.get(i).getCircle_name());
            db.insert(TABLE_STATE, null, values);

        }
        db.close(); // Closing database connection
    }

    public void truncateUpdateData() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_STATE);
        db.execSQL("DELETE FROM " + TABLE_PRODUCT);
        db.execSQL("DELETE FROM " + TABLE_COMPANY);

        db.close();
    }

    public int getCount(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount = db.rawQuery("select count(*) from " + table, null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        mCount.close();
        db.close();
        return count;
    }

    public boolean checkEmpty() {
        if (getCount(TABLE_STATE) == 0 || getCount(TABLE_PRODUCT) == 0 || getCount(TABLE_COMPANY) == 0) {
            return false;
        }
        return true;
    }

    public void deleteDefaultSettings() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_DEFAULT_SETTINGS);
        db.close();
    }

//    public void deleteNotificationData() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("DELETE FROM " + TABLE_NOTIFY);
//        db.close();
//    }

    public void deleteCompanyDetail(String service_type) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COMPANY,
                KEY_SERVICE_TYPE + "=?",
                new String[]{service_type});
        db.close();
    }

    public void deleteProductDetail(String service_type) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_PRODUCT,
                KEY_SERVICE_TYPE + "=?",
                new String[]{service_type});
        db.close();
    }

    public void deleteStateDetail() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_STATE);
        db.close();
    }

    public void deleteStatusColor() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_STATUS_COLOR);
        db.close();
    }

    public void deleteUsersDetail() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_USER);
        db.close();
    }

    public ArrayList<Company> getCompanyDetails(String Service_type) {
        ArrayList<Company> companyArrayList = new ArrayList<Company>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.query(TABLE_COMPANY,
                    new String[]{KEY_SERVICE_TYPE, KEY_COMPANY_NAME, KEY_LOGO, KEY_COMPANY_ITEM_ID},
                    KEY_SERVICE_TYPE + "=?",
                    new String[]{Service_type}, null, null, null, null);

            // looping through all rows and adding to list
            if (cursor != null) {
                cursor.moveToFirst();
                do {
                    Company company = new Company();
                    company.setId(cursor.getString(3));
                    company.setCompany_name(cursor.getString(1));
                    company.setLogo(cursor.getString(2));
                    company.setService_type(cursor.getString(0));
                    companyArrayList.add(company);
                }
                while (cursor.moveToNext());
            }
            db.close();
        }
        catch (Exception ex) {

        }
        return companyArrayList;

    }



    //@kns.p get company name from number tracer
    public String getCompanyName(String company_id) {
       String company_name = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.query(TABLE_COMPANY,
                    new String[]{KEY_COMPANY_NAME},
                    KEY_COMPANY_ITEM_ID + "=?",
                    new String[]{company_id}, null, null, null, null);

            // looping through all rows and adding to list
            if (cursor != null) {
                cursor.moveToFirst();
                company_name = cursor.getString(0);

            }
            db.close();
        }
        catch (Exception ex) {

            Log.e("", "getCompanyName: "+ex.toString() );
        }
        return company_name;

    }


    //@kns.p get company name from number tracer
    public String getProductName(String company_id) {
        String product_name = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.query(TABLE_PRODUCT,
                    new String[]{KEY_PRODUCT_NAME},
                    KEY_COMPANY_ITEM_ID + "=?",
                    new String[]{company_id},
                    null,
                    null,
                    null,
                    null);

            // looping through all rows and adding to list
            if (cursor != null) {
                cursor.moveToFirst();
                product_name = cursor.getString(0);
            }
            db.close();
        }
        catch (Exception ex) {
            Log.e("", "getCompanyName: "+ex.toString() );
        }
        return product_name;
    }


    public ArrayList<String> getAllplans() {

        ArrayList<String> textColorArrayList = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCT ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                textColorArrayList.add(cursor.getString(0));
            }
            while (cursor.moveToNext());
        }

        db.close();
        return textColorArrayList;
    }

    public String getProductID(String company_id) {
        String product_name = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.query(TABLE_PRODUCT,
                    new String[]{KEY_PRODUCT_ITEM_ID},
                    KEY_COMPANY_ITEM_ID + "=?",
                    new String[]{company_id}, null, null, null, null);

            // looping through all rows and adding to list
            if (cursor != null) {
                cursor.moveToFirst();
                product_name = cursor.getString(0);

            }
            db.close();
        }
        catch (Exception ex) {

            Log.e("", "getProductId: "+ex.toString() );
        }
        return product_name;

    }
    //====================================================


    public ArrayList<Product> getProductDetails(String company_id) {
        ArrayList<Product> productArrayList = new ArrayList<Product>();
        // Select All Query
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCT, new String[]{KEY_PRODUCT_ITEM_ID, KEY_PRODUCT_NAME, KEY_COMPANY_ID, KEY_PRODUCT_LOGO}, KEY_COMPANY_ID + "=?",
                new String[]{company_id}, null, null, null, null);


        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(cursor.getString(0));
                product.setProduct_name(cursor.getString(1));
                product.setCompany_id(cursor.getString(2));
                product.setProduct_logo(cursor.getString(3));
                productArrayList.add(product);
            }
            while (cursor.moveToNext());
        }
        db.close();
        return productArrayList;
    }

    public ArrayList<State> getStateDetails() {
        ArrayList<State> stateArrayList = new ArrayList<State>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_STATE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                State state = new State();
                state.setCircle_id(cursor.getString(1));
                state.setCircle_name(cursor.getString(2));
                stateArrayList.add(state);
            }
            while (cursor.moveToNext());
        }
        db.close();
        return stateArrayList;
    }

    public ArrayList<Default> getDefaultSettings() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ArrayList<Default> defaultArrayList = new ArrayList<Default>();
        Cursor cursor = sqLiteDatabase.query(TABLE_DEFAULT_SETTINGS, new String[]{KEY_ID,
                        KEY_USER_ID, KEY_STATE_ID, KEY_STATE_NAME}, null,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            Default aDefault = new Default();
            aDefault.setUser_id(cursor.getString(1));
            aDefault.setState_id(cursor.getString(2));
            aDefault.setState_name(cursor.getString(3));
            defaultArrayList.add(aDefault);
        }
        sqLiteDatabase.close();
        return defaultArrayList;
    }

    public String getCircleID(String circle_name) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.query(TABLE_STATE, new String[]{KEY_CIRCLE_ID}, KEY_CIRCLE_NAME + "=?",
                new String[]{circle_name}, null, null, null, null);

        String circle_id = null;
        if (cursor != null) {
            cursor.moveToFirst();
            circle_id = cursor.getString(0);
        }
        sqLiteDatabase.close();
        return circle_id;
    }

    public ArrayList<Color> getAllColors() {

        ArrayList<Color> textColorArrayList = new ArrayList<Color>();
        String selectQuery = "SELECT  * FROM " + TABLE_STATUS_COLOR;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Color textColor = new Color();
                textColor.setColor_name(cursor.getString(1));
                textColor.setColo_value(cursor.getString(2));
                textColorArrayList.add(textColor);
            }
            while (cursor.moveToNext());
        }

        db.close();
        return textColorArrayList;
    }

    public String getCompanyLogo(String company_name) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.query(TABLE_COMPANY, new String[]{KEY_LOGO}, KEY_COMPANY_NAME + "=?",
                new String[]{company_name}, null, null, null, null);

        String company_logo = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            company_logo = cursor.getString(0);

        }
        sqLiteDatabase.close();
        return company_logo;
    }

    public ArrayList<User> getUserDetail() {

        ArrayList<User> userArrayList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String select_query = "SELECT * FROM " + TABLE_USER;
        Cursor cursor = sqLiteDatabase.rawQuery(select_query, null);
        if (cursor != null) {
            cursor.moveToFirst();
            User user = new User();
            user.setOtp_code(cursor.getString(2));
            user.setUser_name(cursor.getString(3));
            user.setDevice_id(cursor.getString(4));
            user.setName(cursor.getString(5));
            user.setPassword(cursor.getString(6));
            user.setRemember_me(cursor.getString(7));
            user.setReg_date(cursor.getString(8));
            userArrayList.add(user);
        }
        sqLiteDatabase.close();
        return userArrayList;
    }

    public int updateUserDetails(String uname, String pwd, String remember_me_status, String reg_date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PWD, pwd);
        values.put(KEY_REMEMBER_ME, remember_me_status);
        values.put(KEY_REG_DATE, reg_date);

        // updating row
        return db.update(TABLE_USER, values, KEY_USER_NAME + " = ?",
                new String[]{String.valueOf(uname)});

    }

    // Backup DB. Require to add permission
//    public void backupDatabase() {
//        File sd = Environment.getExternalStorageDirectory();
//        File data = Environment.getDataDirectory();
//        if (sd.canWrite()) {
//            // String currentDBPath = Constants.DB_Path;
//             String currentDBPath = "/data/" + context.getPackageName() + "/databases/" + DATABASE_NAME;
//             Log.d("FILE", "DB current path : " + currentDBPath);
////            String currentDBPath = context.getDatabasePath(DATABASE_NAME) + "";
//            Log.d("FILE", "DB path : " + context.getDatabasePath(DATABASE_NAME).getPath());
//
//            String backupDBPath = "DB_" + DATABASE_NAME + "_" + DateTime.getDate() + ".db";
//            File currentDB = new File(data, currentDBPath);
//            File backupDB = new File(sd, backupDBPath);
//             if (currentDB.exists()) {
//                try {
//                    int status = FileUtility.copyFile(currentDB, backupDB);
//                    if (status == 1) {
//                        Log.d("FILE", "File write");
//                    } else {
//                        Log.d("FILE", "File not write");
//                    }
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                Log.d("FILE", "File not found");
//            }
//        }
//    }
}
