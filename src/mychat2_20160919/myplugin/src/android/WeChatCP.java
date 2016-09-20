package tw.com.bais.wechat;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class WeChatCP extends ContentProvider {
    public String TAG = "WeChat2";
    String authority = "wechatcp.bais.com.tw";
    private String DbName = "wechat";
    private String TbName = "wechat";
    private SQLiteDatabase db;
    private DBHelper helper;

    public WeChatCP() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        //throw new UnsupportedOperationException("Not yet implemented");
        if (uri.getAuthority().equals(authority)){
            db = helper.getWritableDatabase();
            db.delete(TbName,selection ,selectionArgs);
            return 0;
        }
        return -1;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        //throw new UnsupportedOperationException("Not yet implemented");
        if (uri.getAuthority().equals(authority)){
            db = helper.getWritableDatabase();
            db.insert( TbName , null ,values);
            Log.d(TAG , "WeChatCP insert db ok");
        }
        return null;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        helper = new DBHelper(getContext(), DbName , null , 1);
        Log.d(TAG , "WeChatCP  onCreate");
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        //throw new UnsupportedOperationException("Not yet implemented");
        if (uri.getAuthority().equals(authority)){
            db = helper.getReadableDatabase();
            Log.d(TAG, "WeChatCP query db ok");
            return  db.query(TbName , null , selection,selectionArgs,null , null , sortOrder);
        }
        return  null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        //throw new UnsupportedOperationException("Not yet implemented");

        if (uri.getAuthority().equals(authority)){
            db = helper.getWritableDatabase();
            db.update(TbName,values , selection , selectionArgs );
            Log.d(TAG , "WeChatCP update db ok");
        }
        return 0;
    }
}
