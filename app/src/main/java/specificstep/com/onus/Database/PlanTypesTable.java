package specificstep.com.onus.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import specificstep.com.onus.Models.PlanTypesModel;

/**
 * Created by ubuntu on 29/5/17.
 */

public class PlanTypesTable {
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    private static final String DATABASE_NAME = "RechargeEngine_new";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "tbl_plantypes";

    private Context context;
    private SQLiteDatabase db;
    private OpenHelper openHelper;

    private static final String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + KEY_ID + " TEXT,"
            + KEY_NAME + " TEXT" + ")";

    public PlanTypesTable(Context context) {
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

    public void insert(PlanTypesModel model)
    {
        this.db = openHelper.getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(KEY_ID, model.id);
        values.put(KEY_NAME, model.name);
        Log.d("DB","Insert data : " + db.insert(TABLE_NAME, null, values));
        openHelper.close();
        db.close();
    }

    public void update(PlanTypesModel model, String whereClause)
    {
        this.db = openHelper.getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(KEY_ID, model.id);
        values.put(KEY_NAME, model.name);
        Log.d("DB","Update data : " + db.update(TABLE_NAME, values, whereClause, null));
        openHelper.close();
        db.close();
    }

    public void delete_All()
    {
        this.db = openHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
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
    public ArrayList<PlanTypesModel> select_Data(String whereClause)
    {
        this.db = openHelper.getWritableDatabase();
        Log.d("DB", "Select Data Where Clause : " + "select * from " + TABLE_NAME + " where " + whereClause);
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + whereClause, null);
        return Load_DATA(cursor);
    }

    public ArrayList<PlanTypesModel> select_Data()
    {
        this.db = openHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        return Load_DATA(cursor);
    }

    private ArrayList<PlanTypesModel> Load_DATA(Cursor cursor)
    {
        ArrayList<PlanTypesModel> models = new ArrayList<PlanTypesModel>();
        if (cursor.moveToFirst())
        {
            do
            {
                PlanTypesModel model = new PlanTypesModel();
                model.id= cursor.getString(cursor.getColumnIndex(KEY_ID));
                model.name= cursor.getString(cursor.getColumnIndex(KEY_NAME));
                models.add(model);
            }
            while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed())
        {
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
