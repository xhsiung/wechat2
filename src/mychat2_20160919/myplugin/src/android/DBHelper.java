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
    public static String TableName = "wechat";
    public static String cid = "cid";
    public static String channel = "channel";
    public static String action = "action";

    public static String sid = "sid";
    public static String tid = "tid";
    public static String corps = "corps";
    public static String category = "category";

    public static String data = "data";
    public static String status = "status" ;
    public static String found = "found" ;
    public static String modify = "modify" ;

    public static String CreateTable = "CREATE TABLE " +
            TableName + " (" +
            "_ID INTEGER PRIMARY KEY," +
            cid + " VARCHAR(30)," +
            channel + " VARCHAR(30)," +
            action + " VARCHAR(30)," +
            sid + " VARCHAR(20)," +
            tid + " VARCHAR(20)," +
            corps + " VARCHAR(20)," +
            category + " VARCHAR(20)," +
            data + " VARCHAR(255)," +
            status + " INTEGER," +
            found + " DATETIME," +
            modify + " DATETIME )" ;

    public static String DropTable = "DROP TABLE IF EXISTS " + TableName ;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG , "DBhelper drop && create");
        db.execSQL( DropTable );
        db.execSQL( CreateTable );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE " + TableName );
    }
}
