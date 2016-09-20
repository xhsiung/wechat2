package tw.com.bais.wechat;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import me.leolin.shortcutbadger.ShortcutBadger;

public class EBusService extends Service {
    final String TAG = "WeChat2";
    static final int INIT = 0x00;
    static final int SAVECONF = 0x01;
    static final int CONNECT = 0x02;
    static final int DISCONNECT = 0x03;
    static final int RECONNECT = 0x04;
    static final int SUBSCRIBE = 0x05;
    static final int UNSUBSCRIBE = 0x06;
    static final int SEND = 0x07;
    static final int LOOPBACK = 0x08;
    static final int UPDATEDB = 0x09;
    static final int QUERYDBDATE = 0x0A;
    static final int NOTIFYSEND = 0x0B;

    static final String SPSetting = "SPSETTING";
    static final String SPSettingkey = "wechatSetting";
    static int activeInstance = 0 ;
    static int unreadnum = 0 ;

    String authorityCP = "wechatcp.bais.com.tw";

    String filterAction = "tw.com.bais.wechat.WeChat";
    PowerManager.WakeLock wakeLock = null;
    JSONObject mSettings = null ;
    Socket mSocket = null ;
    ContentResolver cr = null;
    NotificationManager notificationManager= null;
    HashMap<String,String> channMap = null ;


    static boolean  isActive(){
        return (activeInstance > 0 );
    }

    public EBusService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        channMap  = new HashMap<String,String>();

        activeInstance++;
        cr = getContentResolver();
        EventBus.getDefault().register(this);
        Log.d(TAG , "EBusService start");
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EBundle eb) throws JSONException, ClassNotFoundException {
        Log.d(TAG, "EBusService ThreadMode.MAIN");
        switch (eb.action){
            case INIT:
                Init( eb.settings );
                break;
            case SAVECONF:
                SaveConf( eb.settings );
                break;
            case CONNECT:
                Connect();
                break;
            case RECONNECT:
                break;
            case DISCONNECT:
                DisConnect();
                break;
            case SUBSCRIBE:
                Subscribe( eb.pack );
                break;
            case UNSUBSCRIBE:
                UnSubcribe( eb.pack );
                break;
            case SEND:
                Send( eb.pack );
                break;
            case LOOPBACK:
                //SendLoopBack(eb.pack);
                break;
            case UPDATEDB:
                //SaveDB();
                break;
            case QUERYDBDATE:
                QueryDBDateSendBroadcast( eb.pack );
                break;
            case NOTIFYSEND:
                NotifySend( eb.pack );
                break;
        }
    }


    private void Init(JSONObject obj) throws JSONException {
        //檢查儲存必要欄位
        SaveConf( obj );

        //SaveEl
        SaveEl();

        try {
            //socket連線網址
            String url = "http://" + mSettings.getString("serverip") + ":" + mSettings.getString("port") ;
            if (mSocket == null)
                mSocket = IO.socket( url );
            Connect();
            Subscribe();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Log.d(TAG , "EBusService Init");
    }


    public void SaveEl() throws JSONException {
        if ( mSettings.getBoolean("hasSaveEl") ){
            releaseWakeLock();
        }else{
            acquireWakeLock();
        }
    }

    public void SaveConf( JSONObject obj) throws JSONException {
        //檢查必要欄位
        mSettings = checkFields(obj);
        SaveEl();
        Log.d(TAG , "EBusService SaveConf");
    }

    //check
    private JSONObject checkFields(JSONObject obj) throws JSONException {
        String serverip = obj.has("serverip")  ? obj.getString("serverip") : "0.0.0.0";
        obj.remove("serverip");
        obj.put("serverip",serverip);

        int port = obj.has("port") ? obj.getInt("port") : 3000 ;
        obj.remove("port");
        obj.put("port",port);

        String notifyTarget = obj.has("notifyTarget") ? obj.getString("notifyTarget") : "axsoho.com.MainActivity";
        obj.remove("notifyTarget");
        obj.put("notifyTarget",notifyTarget);

        String notifyTicker = obj.has("notifyTicker") ? obj.getString("notifyTicker") : "message";
        obj.remove("notifyTicker");
        obj.put("notifyTicker",notifyTicker);

        String notifyTitle = obj.has("notifyTitle") ? obj.getString("notifyTitle") : "";
        obj.remove("notifyTitle");
        obj.put("notifyTitle",notifyTitle);

        Boolean hasVibrate = obj.has("hasVibrate") ? obj.getBoolean("hasVibrate") : false;
        obj.remove("hasVibrate");
        obj.put("hasVibrate",hasVibrate);

        Boolean hasSound = obj.has("hasSound") ? obj.getBoolean("hasSound") : false;
        obj.remove("hasSound");
        obj.put("hasSound",hasSound);

        Boolean hasSaveEl = obj.has("hasSaveEl") ? obj.getBoolean("hasSaveEl") : true;
        obj.remove("hasSaveEl");
        obj.put("hasSaveEl",hasSaveEl);


        String key = obj.has("key") ? obj.getString("key") : "1qaz2wsx3edc4rfv5tgb6yhn7ujm8ik";
        obj.remove("key");
        obj.put("key",key);

        //save Environment
        SharedPreferences.Editor editor = getSharedPreferences(SPSetting , Context.MODE_WORLD_WRITEABLE).edit();
        editor.putString(SPSettingkey , obj.toString());
        editor.commit();

        Log.d(TAG , "EBusService checkFields");
        return obj;
    }

    private String getGAccount(){
        try {
            AccountManager accountManager = (AccountManager)getSystemService(ACCOUNT_SERVICE);
            Account[] accounts = accountManager.getAccountsByType("com.google");
            return  accounts[0].name ;
        }catch (Exception e) {
            return "none";
        }
    }


    private void DelInsertDB(JSONObject obj) throws JSONException {
        JSONArray jarr = obj.getJSONArray("data");
        for (int i=0 ; i < jarr.length(); i++){
            ContentValues cv = new ContentValues();
            String cid = jarr.getJSONObject(i).getString("cid") ;
            //delete
            DeleteDB(cid);

            cv.put("cid" , cid );
            cv.put("sid" , jarr.getJSONObject(i).getString("sid"));
            cv.put("tid" , jarr.getJSONObject(i).getString("tid"));
            cv.put("channel" , jarr.getJSONObject(i).getString("channel"));
            cv.put("action" , jarr.getJSONObject(i).getString("action"));
            cv.put("corps" , jarr.getJSONObject(i).getString("corps"));
            cv.put("category" , jarr.getJSONObject(i).getString("category"));
            cv.put("data" , jarr.getJSONObject(i).getString("data"));
            cv.put("status" , 0 );
            cv.put("found" , jarr.getJSONObject(i).getString("found"));
            cv.put("modify" , jarr.getJSONObject(i).getString("modify"));

            cr.insert(Uri.parse("content://" + authorityCP ) , cv);

        }
        Log.d(TAG, "EBusService InsertDB ok");
    }

    private void UpdateDB(JSONObject obj ,int status) throws JSONException {
        //update
        JSONArray jarr = obj.getJSONArray("data");
        for (int i=0 ; i < jarr.length(); i++){
            ContentValues cv = new ContentValues();
            String cid =  jarr.getJSONObject(i).getString("cid") ;
            cv.put("sid" , jarr.getJSONObject(i).getString("sid"));
            cv.put("tid" , jarr.getJSONObject(i).getString("tid"));
            cv.put("channel" , jarr.getJSONObject(i).getString("channel"));
            cv.put("action" , jarr.getJSONObject(i).getString("action"));
            cv.put("corps" , jarr.getJSONObject(i).getString("corps"));
            cv.put("category" , jarr.getJSONObject(i).getString("category"));
            cv.put("data" , jarr.getJSONObject(i).getString("data"));
            cv.put("status" , status );
            cv.put("found" , jarr.getJSONObject(i).getString("found"));
            cv.put("modify" , jarr.getJSONObject(i).getString("modify"));

            cr.update(Uri.parse("content://"+ authorityCP),cv ,"cid=?" , new String[]{ cid });
        }
        Log.d(TAG ,"EBusService UpdateDB ok");
    }

    private void DeleteDB(String cid){
        cr.delete( Uri.parse("content://"+authorityCP) , "cid=?", new String[]{ cid } );
        Log.d(TAG ,"EBusService DeleteDB cid " + cid);
    }

    private JSONObject QueryUnreadDBSendBroadcast( String action ,String sort , int limit ,boolean hasSetUnread ) throws JSONException {
        JSONObject root = new JSONObject();
        JSONArray jarr = new JSONArray();
        Cursor cursor = cr.query( Uri.parse("content://" + authorityCP ),null ,"action='send' and status=?" ,new String[]{ "0" }, "modify "+ sort + " limit 0," + limit);

        cursor.moveToFirst();
        while(! cursor.isAfterLast()){
            //cid   channel          action    action     tid
            //a1c32840-1599-11e6-a92a-85d208eeb757|14cd3aeea632a005|send|user00|14cd3aeea632a005|class01|general|mymessage|1|2016-05-09T11:53:46.947Z|2016-05-09T11:53:46.947Z
            String jstr = String.format("{ 'cid':'%s', 'channel':'%s', 'action': '%s', 'sid':'%s', 'tid':'%s', 'corps':'%s', 'category':'%s', 'data':'%s', 'status': %s , 'found':'%s', 'modify':'%s' }",
                    cursor.getString(1),cursor.getString(2), cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10),cursor.getString(10) );
            JSONObject  row = new JSONObject( jstr );

            jarr.put( row );
            cursor.moveToNext();
        }
        root.put("data", jarr);
        root.put("length", jarr.length() );

        //show unread records
        if (hasSetUnread){
            unreadnum = jarr.length() ;
            ShortcutBadger.with( getApplicationContext()).count( unreadnum );
        }else{
            ShortcutBadger.with( getApplicationContext()).count( 0 );
        }

        if (jarr.length() == 0 ) return null  ;

        Intent intent = new Intent();
        intent.putExtra("action", action );
        intent.putExtra("data", root.toString() );
        intent.setAction(filterAction);
        sendBroadcast( intent );
        Log.d(TAG , "EBusService QueryUnreadDBSendBroadcast");
        return  root;
    }

    private JSONObject QueryDBDateSendBroadcast(JSONObject obj ) throws JSONException {
        String starttime = obj.has("starttime") ? obj.getString("starttime") : "2016-01-01T01:01:00.000Z";
        String endtime = obj.has("endtime") ? obj.getString("endtime"): "2016-01-01T01:01:00.000Z";
        String sort = obj.has("sort") ?  obj.getString("sort"): "ASC";
        String corps = obj.has("corps") ?  obj.getString("corps"): "all";
        int limit = obj.has("limit") ?  obj.getInt("limit"): 50 ;

        JSONObject root = new JSONObject();
        JSONArray jarr = new JSONArray();

        Cursor cursor = null ;
        if ( corps.equalsIgnoreCase("all")) {
            cursor = cr.query(Uri.parse("content://" + authorityCP), null, "action='send' and modify >=? and modify <=? ", new String[]{starttime, endtime}, "modify " + sort);
        }else{
            cursor = cr.query(Uri.parse("content://" + authorityCP), null, "action='send' and modify >=? and modify <=?  and corps=? ", new String[]{starttime, endtime,corps }, "modify " + sort + " limit 0,"+limit);
        }

        cursor.moveToFirst();
        while(! cursor.isAfterLast()) {
            String jstr = String.format("{ 'cid':'%s', 'channel':'%s', 'action': '%s', 'sid':'%s', 'tid':'%s', 'corps':'%s', 'category':'%s', 'data':'%s', 'status': %s , 'found':'%s', 'modify':'%s' }",
                    cursor.getString(1),cursor.getString(2), cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10),cursor.getString(10) );

            JSONObject  row = new JSONObject( jstr );
            jarr.put( row );
            cursor.moveToNext();
        }
        root.put("data", jarr);
        root.put("length", jarr.length() );

        Intent intent = new Intent();
        intent.putExtra("action", "querydbdate");
        intent.putExtra("data", root.toString() );
        intent.setAction(filterAction);
        sendBroadcast( intent );
        Log.d(TAG ,"QueryDBDateSendBroadcast " + root.toString() );
        return  root;
    }

    private void sendPureBroadcast( String action , JSONObject  obj) throws JSONException {
        Intent intent = new Intent();
        intent.putExtra("action",  action );
        intent.putExtra("data", obj.toString() );
        intent.setAction(filterAction);
        sendBroadcast( intent );
        Log.d(TAG ,"sendPureBroadcast " + obj.toString() );
    }

    public void Subscribe(JSONObject jobj) throws JSONException {
        String channel = jobj.getString("channel") ;
        if (existChan( channel )) return;

        jobj.remove("device");
        jobj.put("device", "mobile" );
        mSocket.emit("subscribe" , jobj.toString() );
        channMap.put( channel , mSocket.id());
        Log.d(TAG , "Subscribe " + jobj.toString());
    }

    public void Subscribe() throws JSONException {
        String channel = getDeviceID();
        if (existChan(channel)) return;

        String jstr = String.format("{\"channel\": \"%s\" , \"tid\": \"%s\" , \"key\": \"%s\" ,\"device\":\"mobile\" }" , channel , channel, mSettings.getString("key"));
        mSocket.emit("subscribe", jstr );
        channMap.put( channel, mSocket.id());

        Log.d(TAG , "Subscribe " + jstr);
    }

    public void UnSubcribe(JSONObject jobj) throws JSONException {
        Log.d(TAG , "UnSubscribe " + jobj.toString());
        mSocket.emit("unsubscribe" , jobj.toString());
        channMap.remove( jobj.getString("channel") );
    }

    //for user Send
    public void Send(JSONObject jobj){
        if (! mSocket.connected()) return;
        Log.d(TAG , "Send " + jobj.toString());
        mSocket.emit("send", jobj.toString());
    }

    //loopback
    public void SendLoopBack(JSONObject jobj) throws JSONException {
        if (! mSocket.connected()) return;
        Log.d(TAG , "SendLoopBack " + jobj.toString());
        mSocket.emit("loopback" , jobj.toString());

    }

    private void Connect(){
        if (! mSocket.connected() ){
            mSocket.once(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.once(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on(Socket.EVENT_RECONNECT, onReconnect);
            mSocket.on("mqmsg" , OnMqMsg );
            mSocket.on("mqunread", OnMqUnread);
            mSocket.connect();
        }
        Log.d(TAG , "EBusService connect");
    }

    private void DisConnect(){
        if ( mSocket.connected()){
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.off(Socket.EVENT_RECONNECT, onReconnect);
            mSocket.off("mqmsg" , OnMqMsg );
            mSocket.off("mqunread", OnMqUnread);
            mSocket.disconnect();
        }
        Log.d(TAG , "EBusService disconect");
    }


    //notfication
    private void NotifySend(JSONObject pack) throws JSONException, ClassNotFoundException {
        JSONArray jarr = pack.getJSONArray("data");
        SharedPreferences pre = EBusService.this.getSharedPreferences(SPSetting, Context.MODE_WORLD_READABLE);
        String configure = pre.getString("wechatSetting", "");
        if (configure.isEmpty()) return;

        mSettings = new JSONObject(configure);
        String target = mSettings.getString("notifyTarget");
        Intent intent = new Intent( tw.com.bais.wechat.EBusService.this , Class.forName(target));

        PendingIntent pIntent = PendingIntent.getActivity(tw.com.bais.wechat.EBusService.this, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(EBusService.this).setSmallIcon(getResources().getIdentifier("icon", "drawable", getPackageName()));

        mBuilder.setAutoCancel(true);

        String notifyTicker = "";
        if (mSettings.has("notifyTicker")){
            notifyTicker = mSettings.getString("notifyTicker");
            mBuilder.setTicker(notifyTicker);
        }else{
            mBuilder.setTicker("message");
        }

        String notifyTitle = "" ;
        if (mSettings.has("notifyTitle")) {
            notifyTitle = mSettings.getString("notifyTitle");
        }

        if ( mSettings.getBoolean("hasSound")){
            mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        }

        if ( mSettings.getBoolean("hasVibrate")){
            long[] vibtimes = {0 ,100 ,200 ,300};
            mBuilder.setVibrate(vibtimes);
        }


        for (int i=0 ; i < jarr.length(); i++) {
            String data = jarr.getJSONObject(i).getString("data") ;

            if (notifyTitle.isEmpty()){
                if (data.indexOf(":") > 0){
                    String[] whoSay = data.split(":",2);
                    mBuilder.setContentTitle( whoSay[0] + ":");
                    mBuilder.setContentText( whoSay[1]);
                }else{
                    mBuilder.setContentTitle("none");
                    mBuilder.setContentText( data );
                }

            }else{
                mBuilder.setContentTitle( notifyTitle );
                mBuilder.setContentText( data );
            }

            mBuilder.setContentIntent(pIntent);
            notificationManager =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, mBuilder.build());
        }

        Log.d(TAG , "EBusService NotifySend");
    }


    private Emitter.Listener OnMqMsg = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "EBusService OnMqMsg");
            try {
                if (args[0] == null ) return ;
                JSONObject obj = new JSONObject(args[0].toString());
                JSONObject root = new JSONObject();
                JSONArray jarr = new JSONArray();
                jarr.put( obj );
                root.put("data", jarr);
                root.put("length", jarr.length());
                root.put("device", "mobile");

                if (obj.has("device") && obj.getString("device").equals("mobile")) {
                    if (obj.has("action") && obj.getString("action").equals("send")) {
                        Log.d(TAG , "OnMqMsg send " + root.toString());

                        DelInsertDB(root);
                        QueryUnreadDBSendBroadcast("mqmsg" , "ASC" , 100 , true);

                        if (WeChat.isActive()){
                            SendLoopBack( root );
                            UpdateDB(root, 1);
                            unreadnum = 0 ;
                            ShortcutBadger.with( getApplicationContext()).count( unreadnum );
                        }
                        Log.d(TAG, "EBusService onMqMsg action send mobile ");
                    }

                    //NOTIFYSEND
                    if (obj.has("action") && obj.getString("action").equals("notify") ) {
                        DelInsertDB(root);
                        SendLoopBack( root );
                        UpdateDB(root, 1);

                        EBundle eb = new EBundle();
                        eb.action =  EBusService.NOTIFYSEND;
                        eb.pack = root;
                        EventBus.getDefault().post(eb);
                        Log.d(TAG, "EBusService onMqMsg action notify mobile");
                    }

                    //system test
                    if (obj.has("action") && obj.getString("action").equals("system") ) {
                        DelInsertDB(root);
                        SendLoopBack( root );
                        UpdateDB(root, 1);
                        DelInsertDB(root);
                        sendPureBroadcast("system",root);
                        Log.d(TAG, "EBusService onMqMsg action system mobile");
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    //unread
    private Emitter.Listener OnMqUnread = new Emitter.Listener(){
        @Override
        public void call(Object... args) {
            Log.d(TAG , "EBusService OnMqUnread");
            if (args[0] == null ) return ;
            try {
                JSONObject root = new JSONObject(args[0].toString());
                Log.d(TAG , "OnMqUnread " + root.toString() );

                if (root.has("device") && root.getString("device").equals("mobile")) {

                    //filter
                    if ( ! root.getString("tid").equals( getDeviceID())) return;

                    //send
                    if (root.getString("action").equals("send")){
                        DelInsertDB(root);
                        QueryUnreadDBSendBroadcast("mqunread", "ASC" , 100 , true);

                        if (WeChat.isActive()){
                            SendLoopBack( root );
                            UpdateDB(root, 1);
                            unreadnum = 0 ;
                            ShortcutBadger.with( getApplicationContext()).count( unreadnum );
                        }

                        Log.d(TAG , "OnMqUnread unread aciton send WeChat Active");
                    }


                    //notify
                    if (root.getString("action").equals("notify")){
                        DelInsertDB(root);
                        SendLoopBack( root );
                        UpdateDB(root, 1);

                        EBundle eb = new EBundle();
                        eb.action =  EBusService.NOTIFYSEND;
                        eb.pack = root;
                        EventBus.getDefault().post(eb);
                        Log.d(TAG , "OnMqUnread unread aciton notify");
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    private Emitter.Listener onReconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "onReconnect");
            try {
                channMap = null ;
                Subscribe();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject root = new JSONObject();
            try {
                channMap = null ;
                root.put("success", false);
                root.put("msg", "connectError");
                sendPureBroadcast("socket", root );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "onConnectError");
        }
    };

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        mSocket.disconnect();
        mSocket.close();
        this.stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.close();
        EventBus.getDefault().unregister(this);
    }

    //check channel exist
    public boolean existChan(String channel){
        if (channMap.keySet() == null ) return false ;
        for (String Key : channMap.keySet()) {
            if ( Key.equals( channel)){
                return true;
            }
        }
        return false;
    }

    public String getDeviceID(){
        try {
            return Settings.Secure.getString( this.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        }catch (Exception e){
            return  "nulldeviceid";
        }
    }

    private void acquireWakeLock()
    {
        if (null == wakeLock)
        {
            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "PostLocationService");
            if (null != wakeLock)
            {
                wakeLock.acquire();
            }
        }
    }

    private void releaseWakeLock()
    {
        if (null != wakeLock)
        {
            wakeLock.release();
            wakeLock = null;
        }
    }
}
