package tw.com.bais.wechat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by xdna on 2016/5/5.
 */
public class DBHelper extends SQLiteOpenHelper {

    public String TAG = "WeChat";

    public static String chat_history = "CREATE TABLE " +
            DBOperator.chat_history + " ("  +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "cid TEXT," +
            "channel TEXT," +
            "action TEXT," +
            "sid TEXT," +

            "tid TEXT," +
            "gid TEXT," +
            "corps INTEGER," +
            "category TEXT," +
            "data TEXT," +

            "status INTEGER," +
            "created_time TEXT," +
            "updated_time TEXT," +
            "location_name TEXT,"+
            "location_address TEXT," +

            "location_phone TEXT," +
            "location_latitude INTEGER," +
            "location_longitude INTEGER," +
            "readed INTEGER)" ;
    public static String drop_chat_history = "DROP TABLE IF EXISTS " + DBOperator.chat_history ;

    public static String chat = "CREATE TABLE " +
            DBOperator.chat + " ("  +
            "sid TEXT PRIMARY KEY," +
            "tid TEXT," +
            "oid TEXT," +
            "channel TEXT," +
            "data TEXT," +
            "chat_name TEXT," +
            "last_message TEXT," +
            "last_created_time TEXT," +
            "unread INTEGER)" ;
    public static String drop_chat = "DROP TABLE IF EXISTS " + DBOperator.chat ;


    public static String contacts = "CREATE TABLE " +
            DBOperator.contacts + " ("  +
            "m_id TEXT PRIMARY KEY," +
            "custom_name TEXT," +
            "addressbook TEXT," +
            "mobile TEXT," +
            "corps INTEGER," +

            "islock INTEGER," +
            "isgroup INTEGER," +
            "picture_path TEXT,"+
            "created_time TEXT,"+
            "updated_time TEXT," +

            "contact_id TEXT," +
            "contact_key TEXT," +
            "status_msg TEXT)";
    public static String drop_contacts = "DROP TABLE IF EXISTS " + DBOperator.contacts ;

    public static String contacts_circle = "CREATE TABLE " +
            DBOperator.contacts_circle + " ("  +
            "gid TEXT PRIMARY KEY," +
            "name TEXT," +
            "created_time TEXT)";

    public static String drop_contacts_circle = "DROP TABLE IF EXISTS " + DBOperator.contacts_circle ;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG , "DBhelper drop && create");
        db.execSQL( drop_chat_history );
        db.execSQL( drop_chat );
        db.execSQL( drop_contacts );
        db.execSQL( drop_contacts_circle );

        db.execSQL( chat_history );
        db.execSQL( chat);
        db.execSQL( contacts );
        db.execSQL( contacts_circle );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( drop_chat_history );
        db.execSQL( drop_chat );
        db.execSQL( drop_contacts );
        db.execSQL( drop_contacts_circle );
    }
}
