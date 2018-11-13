package specificstep.com.onus.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import specificstep.com.onus.GlobalClasses.Constants;
import specificstep.com.onus.Models.NotificationModel;

/**
 * Created by ubuntu on 14/4/17.
 */

public class NotificationTable {

    /* [START] - Notification table name and field */

    private static final String KEY_NOTIFY_ID = "notifyId";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_TITLE = "title";
    private static final String KEY_RECEIVE_DATETIME = "receive_dateTime";
    private static final String KEY_SAVE_TIME = "save_dateTime";
    private static final String KEY_READ_DATETIME = "read_dateTime";
    private static final String KEY_READ_FLAG = "readFlag";
    // Contacts Table Columns names

    private static final String DATABASE_NAME = "RechargeEngine_new";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "tbl_notify";

    private Context context;
    private SQLiteDatabase db;
    private OpenHelper openHelper;

    private static final String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + KEY_NOTIFY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_MESSAGE + " TEXT,"
            + KEY_TITLE + " TEXT,"
            + KEY_RECEIVE_DATETIME + " TEXT,"
            + KEY_SAVE_TIME + " TEXT,"
            + KEY_READ_DATETIME + " TEXT,"
            // + KEY_DELETE_FLAG + " TEXT,"
            + KEY_READ_FLAG + " TEXT" + ")";

    public NotificationTable(Context context) {
        this.context = context;
        openHelper = new OpenHelper(this.context);
        createTable();
    }

    public void createTable() {
        this.db = openHelper.getWritableDatabase();
        try {
            // Log.d("DB", "Create table : " + createTable);
            db.execSQL(createTable);
            openHelper.close();
            db.close();
        }
        catch (Exception e) {
        }
    }

    /* [START] - Get last notification id */
    public String getLastNotificationId() {
        String lastRecordId = "0";
        // SELECT * FROM tbl_notify ORDER BY notifyId DESC LIMIT 1;
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + KEY_NOTIFY_ID + " DESC LIMIT 1";
        SQLiteDatabase db = openHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and get last record id
        if (cursor.moveToFirst()) {
            do {
                lastRecordId = cursor.getString(cursor.getColumnIndex(KEY_NOTIFY_ID));
            }
            while (cursor.moveToNext());
        }
        db.close();
        Log.d("Database", "Last record Id : " + lastRecordId);
        return lastRecordId;
    }
    // [END]

    public ArrayList<NotificationModel> getNotificationData(String notificationId) {
        ArrayList<NotificationModel> stateArrayList = new ArrayList<NotificationModel>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_NOTIFY_ID + " ='" + notificationId + "'";

        SQLiteDatabase db = openHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                NotificationModel notificationModel = new NotificationModel();
                notificationModel.id = cursor.getString(cursor.getColumnIndex(KEY_NOTIFY_ID));
                notificationModel.title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                notificationModel.message = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE));
                notificationModel.receiveDateTime = cursor.getString(cursor.getColumnIndex(KEY_RECEIVE_DATETIME));
                notificationModel.saveDateTime = cursor.getString(cursor.getColumnIndex(KEY_SAVE_TIME));
                notificationModel.readFlag = cursor.getString(cursor.getColumnIndex(KEY_READ_FLAG));
                notificationModel.readDateTime = cursor.getString(cursor.getColumnIndex(KEY_READ_DATETIME));
//                notificationModel.deleteFlag = cursor.getString(cursor.getColumnIndex(KEY_DELETE_FLAG));
                stateArrayList.add(notificationModel);
            }
            while (cursor.moveToNext());
        }
        db.close();
        return stateArrayList;
    }

    public void updateNotification(NotificationModel model, String notificationId) {
        String whereClause = KEY_NOTIFY_ID + "='" + notificationId + "'";
        SQLiteDatabase db = openHelper.getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(KEY_NOTIFY_ID, model.id);
        values.put(KEY_MESSAGE, model.message);
        values.put(KEY_TITLE, model.title);
        values.put(KEY_RECEIVE_DATETIME, model.receiveDateTime);
        values.put(KEY_SAVE_TIME, model.saveDateTime);
        values.put(KEY_READ_FLAG, "1");
        values.put(KEY_READ_DATETIME, model.readDateTime);
//        values.put(KEY_DELETE_FLAG, model.deleteFlag);
        Log.d("Database", "Update Notification data : " + db.update(TABLE_NAME, values, whereClause, null));
        db.close();
    }

    public int getAllNotificationRecordCounter() {
        int numberOfRow = 0;
        // Select last record id
        // SELECT * FROM tbl_notify ORDER BY id DESC LIMIT 1
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = openHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // get number of row in table
        numberOfRow = cursor.getCount();
        db.close();
//        Log.d("Database", "numberOfRow : " + numberOfRow);
        return numberOfRow;
    }

    /**
     * Add notification data
     *
     * @param model NotificationModel as param
     */
    public void addNotificationData(NotificationModel model) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = openHelper.getWritableDatabase();
        // values.put(KEY_NOTIFY_ID, model.id);
        values.put(KEY_MESSAGE, model.message);
        values.put(KEY_TITLE, model.title);
        values.put(KEY_RECEIVE_DATETIME, model.receiveDateTime);
        values.put(KEY_SAVE_TIME, model.saveDateTime);
        values.put(KEY_READ_FLAG, model.readFlag);
        values.put(KEY_READ_DATETIME, model.readDateTime);
        // values.put(KEY_DELETE_FLAG, model.deleteFlag);
        long count = db.insert(TABLE_NAME, null, values);
        Log.d("Database", "Insert : " + count);

        db.close(); // Closing database connection

        // delete last record
        ArrayList<NotificationModel> notificationModels = getLastNotificationData();
        if (notificationModels.size() > 0) {
            NotificationModel notificationModel = notificationModels.get(0);
            int lastId = 1;
            try {
                lastId = Integer.parseInt(notificationModel.id);
                Log.d("Database", "Last id : " + lastId);
            }
            catch (Exception ex) {
                Log.d("Database", "Error while parse id");
                ex.printStackTrace();
                lastId = 1;
            }
            if (lastId > 100) {
                NotificationModel deleteNotification = getFirst_1_NotificationData().get(0);
                String deleteRecord = deleteNotification.id;
                String whereClause = KEY_NOTIFY_ID + " ='" + deleteRecord + "'";
                deleteNotification(whereClause);
            }
        }

        Constants.TOTAL_UNREAD_NOTIFICATION = getNumberOfNotificationRecord() + "";
    }

    public ArrayList<NotificationModel> getFirst_1_NotificationData() {
        ArrayList<NotificationModel> stateArrayList = new ArrayList<NotificationModel>();
        // Select last record
        // SELECT * FROM tbl_notify ORDER BY id ASC LIMIT 1
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + KEY_NOTIFY_ID + " ASC LIMIT 1";

        SQLiteDatabase db = openHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through single rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                NotificationModel notificationModel = new NotificationModel();
                notificationModel.id = cursor.getString(cursor.getColumnIndex(KEY_NOTIFY_ID));
                notificationModel.title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                notificationModel.message = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE));
                notificationModel.receiveDateTime = cursor.getString(cursor.getColumnIndex(KEY_RECEIVE_DATETIME));
                notificationModel.saveDateTime = cursor.getString(cursor.getColumnIndex(KEY_SAVE_TIME));
                notificationModel.readFlag = cursor.getString(cursor.getColumnIndex(KEY_READ_FLAG));
                notificationModel.readDateTime = cursor.getString(cursor.getColumnIndex(KEY_READ_DATETIME));
//                notificationModel.deleteFlag = cursor.getString(cursor.getColumnIndex(KEY_DELETE_FLAG));
                stateArrayList.add(notificationModel);
            }
            while (cursor.moveToNext());
        }
        db.close();
        return stateArrayList;
    }

    public ArrayList<NotificationModel> getLastNotificationData() {
        ArrayList<NotificationModel> stateArrayList = new ArrayList<NotificationModel>();
        // Select last record
        // SELECT * FROM tbl_notify ORDER BY id DESC LIMIT 1
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + KEY_NOTIFY_ID + " DESC LIMIT 1";

        SQLiteDatabase db = openHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through single rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                NotificationModel notificationModel = new NotificationModel();
                notificationModel.id = cursor.getString(cursor.getColumnIndex(KEY_NOTIFY_ID));
                notificationModel.title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                notificationModel.message = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE));
                notificationModel.receiveDateTime = cursor.getString(cursor.getColumnIndex(KEY_RECEIVE_DATETIME));
                notificationModel.saveDateTime = cursor.getString(cursor.getColumnIndex(KEY_SAVE_TIME));
                notificationModel.readFlag = cursor.getString(cursor.getColumnIndex(KEY_READ_FLAG));
                notificationModel.readDateTime = cursor.getString(cursor.getColumnIndex(KEY_READ_DATETIME));
//                notificationModel.deleteFlag = cursor.getString(cursor.getColumnIndex(KEY_DELETE_FLAG));
                stateArrayList.add(notificationModel);
            }
            while (cursor.moveToNext());
        }
        db.close();
        return stateArrayList;
    }

    public ArrayList<NotificationModel> getNotificationData_OrderBy() {
        ArrayList<NotificationModel> stateArrayList = new ArrayList<NotificationModel>();
        // Select All Query
        // select * from tbl_notify order by readFlag
        // select * from tbl_notify order by readFlag, read_dateTime
        // String selectQuery = "SELECT * FROM " + TABLE_NOTIFY + " ORDER BY " + KEY_READ_FLAG;
        // select * from tbl_notify order by readFlag, read_dateTime DESC
        // String selectQuery = "SELECT * FROM " + TABLE_NOTIFY + " ORDER BY " + KEY_READ_FLAG + " , " + KEY_READ_DATETIME;
        // String selectQuery = "SELECT * FROM " + TABLE_NOTIFY + " ORDER BY " + KEY_READ_FLAG + " , " + KEY_READ_DATETIME + " DESC";
        // String selectQuery = "SELECT * FROM " + TABLE_NOTIFY + " ORDER BY " + KEY_READ_FLAG + " , " + KEY_READ_DATETIME;
        // select * from tbl_notify order by readFlag,  receive_dateTime DESC
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + KEY_READ_FLAG + " , " + KEY_RECEIVE_DATETIME + " DESC";

        SQLiteDatabase db = openHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                NotificationModel notificationModel = new NotificationModel();
                notificationModel.id = cursor.getString(cursor.getColumnIndex(KEY_NOTIFY_ID));
                notificationModel.title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                notificationModel.message = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE));
                notificationModel.receiveDateTime = cursor.getString(cursor.getColumnIndex(KEY_RECEIVE_DATETIME));
                notificationModel.saveDateTime = cursor.getString(cursor.getColumnIndex(KEY_SAVE_TIME));
                notificationModel.readFlag = cursor.getString(cursor.getColumnIndex(KEY_READ_FLAG));
                notificationModel.readDateTime = cursor.getString(cursor.getColumnIndex(KEY_READ_DATETIME));
//                notificationModel.deleteFlag = cursor.getString(cursor.getColumnIndex(KEY_DELETE_FLAG));
                stateArrayList.add(notificationModel);
            }
            while (cursor.moveToNext());
        }
        db.close();

        return stateArrayList;
    }

    public int getNumberOfNotificationRecord() {
        int numberOfRow = 0;
        // Select last record id
        // SELECT * FROM tbl_notify ORDER BY id DESC LIMIT 1
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_READ_FLAG + " ='0'";
        SQLiteDatabase db = openHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // get number of row in table
        numberOfRow = cursor.getCount();
        db.close();
//        Log.d("Database", "numberOfRow : " + numberOfRow);
        return numberOfRow;
    }

    public void deleteNotification(String whereClause) {
        Log.d("Database", "Delete : " + whereClause);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.delete(TABLE_NAME, whereClause, null);
        db.close();
    }

    private static class OpenHelper extends SQLiteOpenHelper {
        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                Log.d("DB", "Create table : " + createTable);
                db.execSQL(createTable);
            }
            catch (Exception e) {
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("DB", "Upgrading database, this will drop login tables and recreate.");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
