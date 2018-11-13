package specificstep.com.onus.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import specificstep.com.onus.Models.PlanModel;

/**
 * Created by ubuntu on 29/5/17.
 */

public class PlanTable {
    public static final String KEY_CIRCLE_NAME = "circle_name";
    public static final String KEY_COMPANY_NAME = "company_name";
    public static final String KEY_PRODUCT_ID = "product_id";
    public static final String KEY_PRODUCT_NAME = "product_name";
    public static final String KEY_PLANTYPE_NAME = "plantype_name";
    public static final String KEY_NAME = "name";
    public static final String KEY_BENIFIT = "Benifit";
    public static final String KEY_VALIDITY = "validity";
    public static final String KEY_PRICE = "price";

    private static final String DATABASE_NAME = "RechargeEngine_new";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "tbl_plans";

    private Context context;
    private SQLiteDatabase db;
    private OpenHelper openHelper;

    private static final String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + KEY_CIRCLE_NAME + " TEXT,"
            + KEY_COMPANY_NAME + " TEXT,"
            + KEY_PRODUCT_ID + " TEXT,"
            + KEY_PRODUCT_NAME + " TEXT,"
            + KEY_PLANTYPE_NAME + " TEXT,"
            + KEY_BENIFIT + " TEXT,"
            + KEY_VALIDITY + " TEXT,"
            + KEY_PRICE + " TEXT,"
            + KEY_NAME + " TEXT" + ")";

    public PlanTable(Context context) {
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

    public void insert(PlanModel model) {
        this.db = openHelper.getWritableDatabase();
        final ContentValues values = new ContentValues();

        values.put(KEY_CIRCLE_NAME, model.circle_name);
        values.put(KEY_COMPANY_NAME, model.company_name);
        values.put(KEY_PRODUCT_ID, model.product_id);
        values.put(KEY_PRODUCT_NAME, model.product_name);
        values.put(KEY_PLANTYPE_NAME, model.plantype_name);
        values.put(KEY_BENIFIT, model.benifit);
        values.put(KEY_VALIDITY, model.validity);
        values.put(KEY_PRICE, model.price);
        values.put(KEY_NAME, model.name);

        Log.d("DB", "Insert data : " + db.insert(TABLE_NAME, null, values));
        openHelper.close();
        db.close();
    }

    public void update(PlanModel model, String whereClause) {
        this.db = openHelper.getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(KEY_CIRCLE_NAME, model.circle_name);
        values.put(KEY_COMPANY_NAME, model.company_name);
        values.put(KEY_PRODUCT_ID, model.product_id);
        values.put(KEY_PRODUCT_NAME, model.product_name);
        values.put(KEY_PLANTYPE_NAME, model.plantype_name);
        values.put(KEY_BENIFIT, model.benifit);
        values.put(KEY_VALIDITY, model.validity);
        values.put(KEY_PRICE, model.price);
        values.put(KEY_NAME, model.name);
        Log.d("DB", "Update data : " + db.update(TABLE_NAME, values, whereClause, null));
        openHelper.close();
        db.close();
    }

    public void delete_All() {
        this.db = openHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        openHelper.close();
        db.close();
    }

    public void delete(String whereClause) {
        this.db = openHelper.getWritableDatabase();
        db.delete(TABLE_NAME, whereClause, null);
        openHelper.close();
        db.close();
    }

    //    select * from product order by item_id desc
    //    select * from product WHERE item_id BETWEEN 1 AND 10
    //    select * from product WHERE item_id BETWEEN 1 AND 10 order by item_id desc
    // select * from product WHERE item_id > 80
    // // select * from product WHERE KEY_BALANCE > 80 " order by " + KEY_FIRMNAME + " COLLATE NOCASE"
    // select * from product WHERE item_id BETWEEN 1 AND 80 order by id
    // HIGH - LOW select * from product WHERE item_id BETWEEN 1 AND 80 order by id DESC
    // LOW - HIGH = select * from product WHERE item_id BETWEEN 1 AND 80 order by id ASC
    public ArrayList<PlanModel> select_Data(String whereClause) {
        this.db = openHelper.getWritableDatabase();
        Log.d("DB", "Select Data Where Clause : " + "select * from " + TABLE_NAME + " where " + whereClause);
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + whereClause, null);
        return Load_DATA(cursor);
    }

    public ArrayList<PlanModel> select_Data() {
        this.db = openHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        return Load_DATA(cursor);
    }

    private ArrayList<PlanModel> Load_DATA(Cursor cursor) {
        ArrayList<PlanModel> models = new ArrayList<PlanModel>();
        if (cursor.moveToFirst()) {
            do {
                PlanModel model = new PlanModel();
                model.circle_name = cursor.getString(cursor.getColumnIndex(KEY_CIRCLE_NAME));
                model.company_name = cursor.getString(cursor.getColumnIndex(KEY_COMPANY_NAME));
                model.product_id = cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_ID));
                model.product_name = cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_NAME));
                model.plantype_name = cursor.getString(cursor.getColumnIndex(KEY_PLANTYPE_NAME));
                model.benifit = cursor.getString(cursor.getColumnIndex(KEY_BENIFIT));
                model.validity = cursor.getString(cursor.getColumnIndex(KEY_VALIDITY));
                model.price = cursor.getString(cursor.getColumnIndex(KEY_PRICE));
                model.name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                models.add(model);
            }
            while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        openHelper.close();
        db.close();
        return models;
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
