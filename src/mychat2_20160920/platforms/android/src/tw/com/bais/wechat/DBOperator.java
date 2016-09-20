package tw.com.bais.wechat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by xdna on 2016/9/9.
 */
public class DBOperator {
    final static String TAG = "WeChat2";
    public static String DBName = "wechat";
    public static String chat_history = "chat_history";
    public static String chat = "chat";
    public static String contacts = "contacts";
    public static String contacts_circle = "contacts_circle";
    public static String owner_id = "";
    public DBHelper dbHelper = null ;
    public static SQLiteDatabase mDB = null;

    public DBOperator(Context context){
        dbHelper = new DBHelper( context , DBName ,null ,1);
        mDB = dbHelper.getWritableDatabase();
    }

    public static String getContactsOwnerMid(){
        String sql = "SELECT * from contacts WHERE corps = -1";
        Cursor cursor = mDB.rawQuery( sql , null );

        if (cursor.getCount() == 0){
            return "";
        }else{
            cursor.moveToFirst();
            String m_id = cursor.getString(0);
            Log.d(TAG , "getContactsOwnerMid" +  m_id);
            owner_id = m_id ;
            return m_id ;
        }
    }


    public static boolean contactsExist(JSONObject obj ) throws JSONException {
        if ( mDB != null){
            JSONArray jarr = obj.getJSONArray("data");
            String tmpid = "";
            boolean isExist = false;

            String sid = jarr.getJSONObject(0).getString("sid");
            String gid = jarr.getJSONObject(0).getString("gid");

            tmpid = gid.isEmpty() ? sid : gid ;

            String sql = "SELECT * FROM contacts WHERE m_id = ? AND islock = 0";
            Cursor cursor = mDB.rawQuery( sql , new String[]{ tmpid });
            isExist = (cursor.getCount() > 0) ? true : false ;

            return isExist;

        }
        return false;
    }

    public static boolean contactsExistOwner() {
        if (  mDB != null){
            String sql = "SELECT * FROM contacts WHERE corps = -1";
            Cursor cursor = mDB.rawQuery( sql , null );
            if (cursor.getCount() > 0 ) return  true ;
        }
        return false;
    }

    public static boolean contactsIns (JSONObject obj) throws JSONException {
        Log.d(TAG , "enter contactsIns ");
        if ( mDB != null){

            //delete
            if ( obj.getInt("corps") == -1) {
                owner_id = obj.getString("m_id") ;
                String delSql = "DELETE FROM contacts WHERE  corps = -1 ";
                mDB.execSQL( delSql );
            }else{
                String delSql = "DELETE FROM contacts WHERE m_id = ? ";
                mDB.execSQL( delSql , new String[]{ obj.getString("m_id") } );
            }

            ContentValues cv = new ContentValues();
            cv.put("m_id",obj.getString("m_id"));
            cv.put("custom_name",obj.getString("custom_name"));
            cv.put("addressbook",obj.getString("addressbook"));
            cv.put("mobile",obj.getString("mobile"));
            cv.put("corps",obj.getInt("corps"));
            cv.put("islock",obj.getInt("islock"));
            cv.put("isgroup",obj.getInt("isgroup"));
            cv.put("picture_path",obj.getString("picture_path"));
            cv.put("created_time",obj.getString("created_time"));
            cv.put("updated_time",obj.getString("updated_time"));
            cv.put("contact_id",obj.getString("contact_id"));
            cv.put("contact_key",obj.getString("contact_key"));
            cv.put("status_msg",obj.getString("status_msg"));
            cv.put("ulast_updated_time",obj.getString("ulast_updated_time"));
            cv.put("glast_updated_time",obj.getString("glast_updated_time"));
            //cv.put("gid",obj.getString("gid"));

            mDB.insert(contacts, null, cv);
            Log.d(TAG , "contactsIns success");
            return  true;
        }
        Log.d(TAG , "enter contactsIns false");
        return  false;
    }

    public static boolean contactsUpd(JSONObject obj) throws JSONException {
        if ( mDB != null) {
            ContentValues cv = new ContentValues();
            String m_id = obj.getString("m_id");
            cv.put("m_id", m_id);
            cv.put("custom_name",obj.getString("custom_name"));
            cv.put("addressbook",obj.getString("addressbook"));
            cv.put("mobile",obj.getString("mobile"));
            cv.put("corps",obj.getInt("corps"));
            cv.put("islock",obj.getInt("islock"));
            cv.put("isgroup",obj.getInt("isgroup"));
            cv.put("picture_path",obj.getString("picture_path"));
            cv.put("created_time",obj.getString("created_time"));
            cv.put("updated_time",obj.getString("updated_time"));
            cv.put("contact_id",obj.getString("contact_id"));
            cv.put("contact_key",obj.getString("contact_key"));
            cv.put("status_msg",obj.getString("status_msg"));
            cv.put("ulast_updated_time",obj.getString("ulast_updated_time"));
            cv.put("glast_updated_time",obj.getString("glast_updated_time"));
            //cv.put("gid",obj.getString("gid"));

            mDB.update(contacts, cv, "m_id=?", new String[]{m_id});
            Log.d(TAG, "contactsUpdate success");
            return true;
        }
        return false;
    }

    public static boolean contactsDel(JSONObject obj) throws JSONException {
        if ( mDB != null) {
            String sql = "DELETE FROM contacts WHERE m_id=?";
            mDB.execSQL(sql , new String[]{ obj.getString("m_id") });
            Log.d(TAG , "contactsDelete success");
            return true;
        }
        return false;
    }

    public static JSONObject getContactsUser(String id) throws JSONException {
        if ( mDB != null) {
            Log.d(TAG , "getContactsUser ------->0");
            JSONObject root = new JSONObject();
            JSONArray jarr = new JSONArray();
            String sql="";
            Cursor cursor = null ;

            if (id.equals("all")){
                sql = "SELECT * FROM contacts ORDER BY corps ASC";
                cursor = mDB.rawQuery( sql , null );
            }else{
                Log.d(TAG , "id:" + id );
                sql = "SELECT * FROM contacts WHERE m_id = ? ";
                cursor = mDB.rawQuery( sql , new String[]{ id } );
            }
            Log.d(TAG , "getContactsUser ------->1");
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String m_id = cursor.getString( cursor.getColumnIndex("m_id"));
                String custom_name = cursor.getString( cursor.getColumnIndex("custom_name"));
                String addressbook = cursor.getString( cursor.getColumnIndex("addressbook"));
                String mobile = cursor.getString( cursor.getColumnIndex("mobile"));
                int corps = cursor.getInt( cursor.getColumnIndex("corps"));

                int islock = cursor.getInt( cursor.getColumnIndex("islock"));
                int isgroup = cursor.getInt( cursor.getColumnIndex("isgroup"));
                String picture_path = cursor.getString(cursor.getColumnIndex("picture_path"));
                String created_time = cursor.getString( cursor.getColumnIndex("created_time"));
                String updated_time = cursor.getString( cursor.getColumnIndex("updated_time"));

                String contact_id = cursor.getString(cursor.getColumnIndex("contact_id"));
                String contact_key = cursor.getString( cursor.getColumnIndex("contact_key"));
                String status_msg = cursor.getString( cursor.getColumnIndex("status_msg"));
                String ulast_updated_time = cursor.getString( cursor.getColumnIndex("ulast_updated_time"));
                String glast_updated_time = cursor.getString( cursor.getColumnIndex("glast_updated_time"));



                String jstr = "{'m_id':'%s','custom_name':'%s', 'addressbook':'%s','mobile':'%s', 'corps':%s," +
                        "'islock':%s,'isgroup':%s, 'picture_path':'%s', 'created_time':'%s', 'updated_time': '%s'," +
                        "'contact_id':'%s', 'contact_key':'%s' , 'status_msg':'%s' , 'ulast_updated_time':'%s' , 'glast_updated_time':'%s'}";
                String jstrfmt = String.format( jstr , m_id, custom_name, addressbook, mobile ,  corps,
                        islock , isgroup , picture_path,  created_time, updated_time,
                        contact_id , contact_key, status_msg , ulast_updated_time , glast_updated_time );
                JSONObject row = new JSONObject(jstrfmt);
                jarr.put(row);
                cursor.moveToNext();
                Log.d(TAG , "getContactsUser ------->3");
            }
            root.put("data", jarr);
            root.put("length", jarr.length());
            Log.d(TAG , root.toString() ) ;
            return root;
        }
        return null;
    }


    public static boolean chatHistoryReReaded(JSONObject obj) throws JSONException {
        if ( mDB != null){
            Log.d(TAG , obj.toString() );
            JSONArray jarr = obj.getJSONArray("data");
            for (int i=0 ; i < jarr.length() ; i++){
                String cid = jarr.getJSONObject(i).getString("cid");
                String sql = "UPDATE " + chat_history + " SET status = 1 WHERE cid = ?";
                mDB.execSQL( sql , new String[]{ cid});
            }
            return true;
        }
        return false;
    }


    public static boolean chatHistoryDelInsert( JSONObject obj ) throws JSONException {
        if ( mDB != null) {
            JSONArray jarr = obj.getJSONArray("data");
            for (int i = 0; i < jarr.length(); i++) {
                ContentValues cv = new ContentValues();
                String cid = jarr.getJSONObject(i).getString("cid");
                //delete
                chatHistoryDeleteDB(cid);

                cv.put("cid", cid);
                cv.put("channel", jarr.getJSONObject(i).getString("channel"));
                cv.put("action", jarr.getJSONObject(i).getString("action"));
                cv.put("sid", jarr.getJSONObject(i).getString("sid"));
                cv.put("tid", jarr.getJSONObject(i).getString("tid"));
                cv.put("gid", jarr.getJSONObject(i).getString("gid"));
                cv.put("corps", jarr.getJSONObject(i).getInt("corps"));
                cv.put("category", jarr.getJSONObject(i).getString("category"));
                cv.put("data", jarr.getJSONObject(i).getString("data"));
                cv.put("status", 0);
                cv.put("created_time", jarr.getJSONObject(i).getString("created_time"));
                cv.put("updated_time", jarr.getJSONObject(i).getString("updated_time"));
                cv.put("location_name", jarr.getJSONObject(i).getString("location_name"));
                cv.put("location_address", jarr.getJSONObject(i).getString("location_address"));
                cv.put("location_phone", jarr.getJSONObject(i).getString("location_phone"));
                cv.put("location_latitude", jarr.getJSONObject(i).getInt("location_latitude"));
                cv.put("location_longitude", jarr.getJSONObject(i).getInt("location_longitude"));
                cv.put("readed", 0 );

                //cr.insert(Uri.parse("content://" + authorityCP ) , cv);
                mDB.insert(chat_history, null, cv);

            }
            Log.d(TAG, "EBusService InsertDB ok");
            return true;
        }
        return false;
    }

    public static void chatHistoryUpdateDB(JSONObject obj ,int status) throws JSONException {
        if ( mDB != null) {
            //update
            JSONArray jarr = obj.getJSONArray("data");
            for (int i = 0; i < jarr.length(); i++) {
                ContentValues cv = new ContentValues();
                String cid = jarr.getJSONObject(i).getString("cid");
                cv.put("cid", cid);
                cv.put("channel", jarr.getJSONObject(i).getString("channel"));
                cv.put("action", jarr.getJSONObject(i).getString("action"));
                cv.put("sid", jarr.getJSONObject(i).getString("sid"));
                cv.put("tid", jarr.getJSONObject(i).getString("tid"));
                cv.put("gid", jarr.getJSONObject(i).getString("gid"));
                cv.put("corps", jarr.getJSONObject(i).getInt("corps"));
                cv.put("category", jarr.getJSONObject(i).getString("category"));
                cv.put("data", jarr.getJSONObject(i).getString("data"));
                cv.put("status", 0);
                cv.put("created_time", jarr.getJSONObject(i).getString("created_time"));
                cv.put("updated_time", jarr.getJSONObject(i).getString("updated_time"));
                cv.put("location_time", jarr.getJSONObject(i).getString("location_time"));
                cv.put("location_address", jarr.getJSONObject(i).getString("location_address"));
                cv.put("location_phone", jarr.getJSONObject(i).getString("location_phone"));
                cv.put("location_latitude", jarr.getJSONObject(i).getString("location_latitude"));
                cv.put("location_longitude", jarr.getJSONObject(i).getString("location_longitude"));
                cv.put("readed", 0 );

                //cr.update(Uri.parse("content://"+ authorityCP),cv ,"cid=?" , new String[]{ cid });
                mDB.update(chat_history, cv, "cid=?", new String[]{cid});
            }
            Log.d(TAG, "EBusService UpdateDB ok");
        }
    }

    public static void  chatHistoryDeleteDB(String cid) throws JSONException {
        if ( mDB != null) {
            mDB.delete(chat_history, "cid=?", new String[]{cid});
            Log.d(TAG, "EBusService DeleteDB chat_history cid " + cid);
        }
    }

    public static void  chatDeleteDB(String sid) throws JSONException {
        if ( mDB != null) {
            mDB.delete(chat_history, "cid=?", new String[]{sid});
            Log.d(TAG, "EBusService DeleteDB chat sid " + sid);
        }
    }


    //test group
    public static JSONObject chatHistoryQueryUnread2(int offset,int limit) throws JSONException {
        if ( mDB != null ){
            String  nowdatetime = String.valueOf( System.currentTimeMillis() );
            Log.d(TAG , "chatHistoryQueryUnread2-------->nowdatetime: " + nowdatetime );

            JSONObject root = new JSONObject();
            JSONArray jarr = new JSONArray();
            String sql = "SELECT * FROM chat_history WHERE action='send' AND status=0  ORDER BY channel ASC, updated_time DESC limit ?,?";
            Cursor cursor = mDB.rawQuery( sql , new String[]{  String.valueOf(offset) ,  String.valueOf( limit ) } );


//            String sql = "SELECT * FROM chat_history WHERE action='send' AND channel = '%s' AND updated_time <= '%s' ORDER BY updated_time DESC LIMIT %s,%s";
//            String sqlfmt = String.format(sql, channel , nowdatetime , offset , limit);
//            Cursor cursor = mDB.rawQuery( sqlfmt , null );

            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                String cid = cursor.getString( cursor.getColumnIndex("cid")) ;
                String channel = cursor.getString( cursor.getColumnIndex("channel")) ;
                String action = cursor.getString( cursor.getColumnIndex("action")) ;
                String sid = cursor.getString( cursor.getColumnIndex("sid")) ;
                String tid = cursor.getString( cursor.getColumnIndex("tid")) ;

                String gid = cursor.getString( cursor.getColumnIndex("gid")) ;
                String category = cursor.getString( cursor.getColumnIndex("category")) ;
                int corps = cursor.getInt( cursor.getColumnIndex("corps")) ;
                String data = cursor.getString( cursor.getColumnIndex("data")) ;
                String status = cursor.getString( cursor.getColumnIndex("status")) ;

                String created_time = cursor.getString( cursor.getColumnIndex("created_time")) ;
                String updated_time = cursor.getString( cursor.getColumnIndex("updated_time")) ;
                String location_name = cursor.getString( cursor.getColumnIndex("location_name")) ;
                String location_address = cursor.getString( cursor.getColumnIndex("location_address")) ;
                String location_phone = cursor.getString( cursor.getColumnIndex("location_phone")) ;

                int location_latitude = cursor.getInt( cursor.getColumnIndex("location_latitude")) ;
                int location_longitude = cursor.getInt( cursor.getColumnIndex("location_longitude")) ;
                int readed = cursor.getInt( cursor.getColumnIndex("readed")) ;

                String jstr = "{ 'cid':'%s', 'channel':'%s', 'action':'%s', 'sid':'%s', 'tid':'%s'," +
                        "'gid':'%s','corps':'%s',  'category':'%s',     'data':'%s',      'status': %s ," +
                        "'created_time':'%s', 'updated_time':'%s' ,'location_name':'%s','location_address':'%s', 'location_phone':'%s'," +
                        "'location_latitude':%s , 'location_longitude':%s ,'readed': %s}";
                String jstrfmt = String.format( jstr , cid , channel, action, sid, tid,
                        gid,category,corps,data,status,
                        created_time,updated_time,location_name,location_address,location_phone,
                        location_latitude,location_longitude,readed );
                JSONObject row = new JSONObject(jstrfmt);
                jarr.put(row);
                cursor.moveToNext();
            }
            //reverse
            JSONArray reverseArr = new JSONArray();
            for (int i= (jarr.length()-1); i >= 0 ;i--){
                reverseArr.put( jarr.getJSONObject(i) );
            }

            root.put("data", reverseArr);
            root.put("length", jarr.length());

            return root;
        }
        return null;
    }

     public static JSONObject chatLastQuery() throws JSONException {
        if ( mDB != null ){
            JSONObject root = new JSONObject();
            JSONArray jarr = new JSONArray();
              //delete chat
            mDB.delete("chat",null,null);

            String sql = "SELECT * FROM chat_history WHERE action='send' AND status = 0  GROUP BY sid ";
            Cursor cursor = mDB.rawQuery(sql, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                String channel = cursor.getString(cursor.getColumnIndex("channel"));
                String sid = cursor.getString(cursor.getColumnIndex("sid"));
                String tid = cursor.getString( cursor.getColumnIndex("tid"));
                String data = cursor.getString( cursor.getColumnIndex("data"));
                String updated_time = cursor.getString( cursor.getColumnIndex("updated_time"));

                if ( tid == EBusService.getSID() ) continue;

                String sqlunread = "SELECT * FROM chat_history WHERE action='send' AND status=0 AND sid = ? ";
                int unread = mDB.rawQuery(sqlunread , new String[]{ sid }).getCount() ;
//                Log.d(TAG , "XXXXXXXXXXXXXXXXXXXXXXX");
//                Log.d(TAG , "sid:" + sid );
//                Log.d(TAG , "tid:" + tid );
//                Log.d(TAG , "data:" + data );
//                Log.d(TAG , "updated_time:" + updated_time );
//                Log.d(TAG , "unread " + unread );
                //Log.d(TAG , "my cutom_name:" + custom_name );

                JSONObject user = getContactsUser( sid );
                String custom_name = user.getJSONArray("data").getJSONObject(0).getString("custom_name");

                //insert
                ContentValues cv = new ContentValues();
                cv.put("sid" , sid);
                cv.put("tid" , tid);
                cv.put("oid" , "");
                cv.put("channel" , channel);
                cv.put("data" , "");
                cv.put("chat_name" , custom_name);
                cv.put("last_message" , data);
                cv.put("last_created_time" , updated_time);
                cv.put("unread" , unread);
                mDB.insert( chat , null, cv);
                Log.d(TAG , "chatIns success");

                //json data
                String jstr = "{ 'sid':'%s',  'tid':'%s',  'oid':'%s', 'channel':'%s', 'data':'%s'," +
                        "'chat_name':'%s','last_message':'%s',  'last_created_time':'%s', 'unread':%s }" ;
                String jstrfmt = String.format( jstr , sid , tid ,  "", channel ,  "" ,
                        custom_name, data, updated_time, unread );
                JSONObject row = new JSONObject(jstrfmt);
                jarr.put(row);

                cursor.moveToNext();
            }

            root.put("data", jarr);
            root.put("length", jarr.length());
            return root ;
        }
        return null;
    }

    public static JSONObject getLastChat() throws JSONException {
        if ( mDB != null ){
            chatLastQuery();

            JSONObject root = new JSONObject();
            JSONArray jarr = new JSONArray();
            String sql = "SELECT * FROM chat";
            Cursor cursor = mDB.rawQuery(sql, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){

                String sid = cursor.getString(cursor.getColumnIndex("sid"));
                String tid = cursor.getString( cursor.getColumnIndex("tid"));
                String oid = cursor.getString(cursor.getColumnIndex("oid"));
                String channel = cursor.getString(cursor.getColumnIndex("channel"));
                String data = cursor.getString( cursor.getColumnIndex("data"));
                String chat_name = cursor.getString( cursor.getColumnIndex("chat_name"));
                String last_message = cursor.getString( cursor.getColumnIndex("last_message"));
                String last_created_time = cursor.getString( cursor.getColumnIndex("last_created_time"));
                int unread = cursor.getInt( cursor.getColumnIndex("unread"));

                //json data
                String jstr = "{ 'sid':'%s',  'tid':'%s',  'oid':'%s', 'channel':'%s', 'data':'%s'," +
                        "'chat_name':'%s','last_message':'%s',  'last_created_time':'%s', 'unread':%s }" ;
                String jstrfmt = String.format( jstr , sid , tid ,  "", channel ,  "" ,
                        chat_name, last_message, last_created_time, unread );
                JSONObject row = new JSONObject(jstrfmt);
                jarr.put(row);
                cursor.moveToNext();
            }
            root.put("data", jarr);
            root.put("length", jarr.length());
            Log.d(TAG , "myroot "+ root.toString());
            return root;
        }
        return null;
    }

    public static JSONObject chatHistoryQueryDBDate(JSONObject obj) throws JSONException {
        if ( mDB != null) {
            String  channel = obj.getString("channel");
            String  nowdatetime = String.valueOf( System.currentTimeMillis() );

            int offset = obj.has("offset") ?  obj.getInt("offset"): 0 ;
            int limit = obj.has("limit") ?  obj.getInt("limit"): 50 ;

            JSONObject root = new JSONObject();
            JSONArray jarr = new JSONArray();


            String sql = "SELECT * FROM chat_history WHERE action='send' AND channel = '%s' AND updated_time <= '%s' ORDER BY updated_time DESC LIMIT %s,%s";
            String sqlfmt = String.format(sql, channel , nowdatetime , offset , limit);
            Cursor cursor = mDB.rawQuery( sqlfmt , null );

            cursor.moveToFirst();
            while(! cursor.isAfterLast()) {
                String xcid = cursor.getString( cursor.getColumnIndex("cid") );
                String xchannel = cursor.getString( cursor.getColumnIndex("channel") );
                String xaction = cursor.getString( cursor.getColumnIndex("action") );
                String xsid = cursor.getString( cursor.getColumnIndex("sid") );
                String xtid = cursor.getString( cursor.getColumnIndex("tid") );

                String xgid = cursor.getString( cursor.getColumnIndex("gid") );
                String xcorps = cursor.getString( cursor.getColumnIndex("corps") );
                String xcategory = cursor.getString( cursor.getColumnIndex("category") );
                String xdata = cursor.getString( cursor.getColumnIndex("data") );
                String xstatus = cursor.getString( cursor.getColumnIndex("status") );

                String xcreated_time = cursor.getString( cursor.getColumnIndex("created_time") );
                String xupdated_time = cursor.getString( cursor.getColumnIndex("updated_time") );
                String xlocation_name = cursor.getString( cursor.getColumnIndex("location_name") );
                String xlocation_address = cursor.getString( cursor.getColumnIndex("location_address") );
                String xlocation_phone = cursor.getString( cursor.getColumnIndex("location_phone") );

                int xlocation_latitude = cursor.getInt( cursor.getColumnIndex("location_latitude") );
                int xlocation_longitude = cursor.getInt( cursor.getColumnIndex("location_longitude") );
                int xreaded = cursor.getInt( cursor.getColumnIndex("readed") );


                String jstr = "{ 'cid':'%s', 'channel':'%s', 'action': '%s', 'sid':'%s', 'tid':'%s'," +
                            "'serial':'%s',  'corps':'%s', 'category':'%s', 'data':'%s', 'status': %s , " +
                            "'create_time':'%s',  'update_time':'%s','location_name':'%s','location_address':'%s','location_phone':'%s'," +
                            "'location_latitude':%s,'location_longitude':%s , 'readed':%s }";
                String jstrfmt = String.format( jstr, xcid , xchannel,  xaction, xsid , xtid ,
                            xgid , xcorps, xcategory , xdata , xstatus,
                            xcreated_time, xupdated_time , xlocation_name , xlocation_address , xlocation_phone,
                            xlocation_latitude, xlocation_longitude ,xreaded );

                JSONObject  row = new JSONObject( jstrfmt );
                jarr.put( row );
                cursor.moveToNext();
            }

            //reverse
            JSONArray reverseArr = new JSONArray();
            for (int i= (jarr.length()-1); i >= 0 ;i--){
                    reverseArr.put( jarr.getJSONObject(i) );
            }
            root.put("data", reverseArr );
            root.put("length", jarr.length());

            return root ;
        }
        return null;
    }



    //checkContacsData
    public static JSONObject chkContacts(JSONObject obj) throws JSONException {
        String action = obj.has("action") ? obj.getString("action") : "";
        obj.remove("action");
        obj.put("action",action);

        String m_id = obj.has("m_id") ? obj.getString("m_id") : "";
        obj.remove("m_id");
        obj.put("m_id",m_id);

        String custom_name = obj.has("custom_name") ? obj.getString("custom_name") : "";
        obj.remove("custom_name");
        obj.put("custom_name",custom_name);

        String addressbook = obj.has("addressbook") ? obj.getString("addressbook") : "";
        obj.remove("addressbook");
        obj.put("addressbook",addressbook);

        String mobile = obj.has("mobile") ? obj.getString("mobile") : "";
        obj.remove("mobile");
        obj.put("mobile",mobile);

        int corps = obj.has("corps") ? obj.getInt("corps") : 0 ;
        obj.remove("corps");
        obj.put("corps",corps);

        int islock = obj.has("islock") ? obj.getInt("islock") : 0 ;
        obj.remove("islock");
        obj.put("islock",islock);

        int isgroup = obj.has("isgroup") ? obj.getInt("isgroup") : 0 ;
        obj.remove("isgroup");
        obj.put("isgroup",isgroup);

        String picture_path = obj.has("picture_path") ? obj.getString("picture_path") : "";
        obj.remove("picture_path");
        obj.put("picture_path",picture_path);


        String created_time = String.valueOf( System.currentTimeMillis());
        obj.remove("created_time");
        obj.put("created_time",created_time);

        String updated_time = obj.has("updated_time") ? obj.getString("updated_time") : "";
        obj.remove("updated_time");
        obj.put("updated_time", updated_time );

        String contact_id = obj.has("contact_id") ? obj.getString("contact_id") : "";
        obj.remove("contact_id");
        obj.put("contact_id",contact_id);

        String contact_key = obj.has("contact_key") ? obj.getString("contact_key") : "";
        obj.remove("contact_key");
        obj.put("contact_key",contact_key);

        String status_msg = obj.has("status_msg") ? obj.getString("status_msg") : "";
        obj.remove("status_msg");
        obj.put("status_msg",status_msg);


        String ulast_updated_time = obj.has("ulast_updated_time") ? obj.getString("status_msg") : "";
        obj.remove("ulast_updated_time");
        obj.put("ulast_updated_time",ulast_updated_time);

        String glast_updated_time = obj.has("glast_updated_time") ? obj.getString("glast_updated_time") : "";
        obj.remove("glast_updated_time");
        obj.put("glast_updated_time",glast_updated_time);

        Log.d(TAG , "m_id is " + m_id );
        Log.d(TAG , "custom_name is " + custom_name );
        if (m_id.isEmpty() || action.isEmpty() ) return null ;
        return obj;
    }

}
