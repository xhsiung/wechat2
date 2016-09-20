package tw.com.bais.wechat;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.HashSet;
import java.util.Iterator;

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

    public static final String SPSetting = "SPSETTING";
    public static final String SPSettingkey = "wechatSetting";
    static int activeInstance = 0 ;
    static int unreadnum = 0 ;

    String authorityCP = "wechatcp.bais.com.tw";

    String filterAction = "tw.com.bais.wechat.WeChat";
    PowerManager.WakeLock wakeLock = null;
    JSONObject mSettings = null ;
    Socket mSocket = null ;

    public DBOperator dbOperator = null ;

    NotificationManager notificationManager= null;
    HashSet<String> channSet = null ;
    static String SID = "";

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
        channSet = new HashSet<String>();
        activeInstance++;

        if (DBOperator.mDB == null){
            dbOperator = new DBOperator(this);
        }

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
                SendLoopBack(eb.pack);
                break;
            case UPDATEDB:
                //SaveDB();
                break;
//            case QUERYDBDATE:
//                QueryDBDateSendBroadcast( eb.pack );
//                break;
            case NOTIFYSEND:
                NotifySend( eb.pack );
                break;
        }
    }


    private void Init(JSONObject obj) throws JSONException {
        //檢查儲存必要欄位
        //mSettings = checkFields(obj);
        //saveEvn( mSettings );
        //SaveEl();
        SaveConf( obj );

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

    public void SaveConf( JSONObject obj) throws JSONException {
        //檢查必要欄位
        mSettings = checkFields(obj);
        saveEvn( mSettings );
        SaveEl();
        Log.d(TAG , "EBusService SaveConf");
    }

    public void SaveEl() throws JSONException {
        if ( mSettings.getBoolean("hasSaveEl") ){
            releaseWakeLock();
        }else{
            acquireWakeLock();
        }
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

        Log.d(TAG , "EBusService checkFields");
        return obj;
    }

    public void saveEvn(JSONObject obj){
        //save Environment
        SharedPreferences.Editor editor = getSharedPreferences(SPSetting , Context.MODE_WORLD_WRITEABLE).edit();
        editor.putString(SPSettingkey , obj.toString());
        editor.commit();
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

    public String getInviteChann(String sid , String tid) throws JSONException {
        String max = sid.compareTo( tid ) > 0 ? sid : tid;
        String min = sid.compareTo( tid ) < 0 ? sid : tid;
        return max + "@" + min;
    }

    public HashSet<String> getInviteChann2(JSONObject obj) throws JSONException {
        JSONArray jarr = obj.getJSONArray("data");
        HashSet<String> arr = new HashSet<String>();
        for (int i=0; i < jarr.length() ; i++){
            String sid = jarr.getJSONObject(i).getString("sid");
            String tid = jarr.getJSONObject(i).getString("tid");
            String gid = jarr.getJSONObject(i).getString("gid");

            String max="";
            String min="";
            String newchann="";

            if (gid.isEmpty()){
                max = sid.compareTo( tid ) > 0 ? sid : tid;
                min = sid.compareTo( tid ) < 0 ? sid : tid;
                newchann = max + "@" + min ;
            }else{
                newchann =  gid ;
            }

            arr.add( newchann );
        }
        return arr;
    }

    private JSONObject QueryUnreadDBSendBroadcast( String action , int offset,int limit ,boolean hasSetUnread ) throws JSONException {
        JSONObject root = DBOperator.chatHistoryQueryUnread2( offset , limit);

        //show unread records
        if (hasSetUnread){
            unreadnum = root.getJSONArray("data").length() ;
            ShortcutBadger.with( getApplicationContext()).count( unreadnum );
        }else{
            ShortcutBadger.with( getApplicationContext()).count( 0 );
        }

        if (root.getJSONArray("data").length() == 0 ) return null  ;

        Intent intent = new Intent();
        intent.putExtra("action", action );
        intent.putExtra("data", root.toString() );
        intent.setAction(filterAction);
        sendBroadcast( intent );
        Log.d(TAG , "EBusService QueryUnreadDBSendBroadcast");
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
        Log.d(TAG , jobj.toString() );
        String channel = jobj.getString("channel") ;
        //if (existChan( channel )) return;

        jobj.remove("device");
        jobj.put("device", "mobile" );
        mSocket.emit("subscribe" , jobj.toString() );
        channSet.add( channel );
        Log.d(TAG , "Subscribe " + jobj.toString());
    }



    public void Subscribe() throws JSONException {
        //String channel = getDeviceID();
        //if (existChan(channel)) return;

        if ( getSID().isEmpty() ) return;
        Log.d(TAG , "EBusService channel :" + SID );

        String jstr = String.format("{\"channel\": \"%s\" , \"tid\": \"%s\" , \"key\": \"%s\" ,\"device\":\"mobile\" }" , getSID() , getSID(), mSettings.getString("key"));
        mSocket.emit("subscribe", jstr );
        channSet.add( getSID() );

        Log.d(TAG , "Subscribe " + jstr);
    }

    public void Watch() throws JSONException {
        //String jstr = String.format("{\"channel\": \"%s\" , \"sid\": \"%s\" , \"tid\": \"%s\" ,\"device\":\"mobile\" }" );
        //mSocket.emit("watch", jstr );
    }

    public void UnSubcribe(JSONObject jobj) throws JSONException {
        Log.d(TAG , "UnSubscribe " + jobj.toString());
        mSocket.emit("unsubscribe" , jobj.toString());
        channSet.remove( jobj.getString("channel"))  ;
    }

    //for user Send
    public void Send(JSONObject jobj) throws JSONException {
        if (! mSocket.connected()) return;

        //secret *********************************************************
        String cid = getSID() + System.currentTimeMillis();
        String sid = jobj.getString("sid");
        String tid = jobj.getString("tid");
        String gid = jobj.has("gid")? jobj.getString("gid"): "";

        JSONObject askData = new JSONObject();
        askData.put("action", "invite");
        askData.put("sid", sid );
        askData.put("tid", tid );
        askData.put("channel", tid );

        if (gid.isEmpty()){
            askData.put("gid", "" );
        }else{
            askData.put("gid", gid );
        }
        mSocket.emit("ask",  askData.toString());
        //*****************************************************************

        JSONObject xjobj = jobj.has("cid") ? jobj : jobj.put("cid", cid);

        Log.d(TAG , "Send " + xjobj.toString());
        mSocket.emit("send",  xjobj.toString());
    }

    //loopback
    public void SendLoopBack(JSONObject jobj) throws JSONException {
        if (! mSocket.connected()) return;

        Log.d(TAG , "SendLoopBack " + jobj.toString());
        mSocket.emit("loopback" , jobj.toString());
    }

    public void SendUnReadLoopBack(JSONObject jobj) throws JSONException {
        if (!mSocket.connected()) return;
        Log.d(TAG , "SendUnReadLoopBack " + jobj.toString());
        mSocket.emit("unreadloopback" , jobj.toString());
    }

    private void Connect(){
        if (! mSocket.connected() ){
            mSocket.once(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.once(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on(Socket.EVENT_RECONNECT, onReconnect);
            mSocket.on("mqmsg" , OnMqMsg );
            mSocket.on("mqunread", OnMqUnread);
            mSocket.on("asked",OnAsked);
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
            mSocket.off("asked",OnAsked);
            mSocket.disconnect();

        }
        Log.d(TAG , "EBusService disconect");
    }


    //notfication
    private void NotifySend(JSONObject pack) throws JSONException, ClassNotFoundException {
        JSONArray jarr = pack.getJSONArray("data");
        SharedPreferences pre = EBusService.this.getSharedPreferences(SPSetting, Context.MODE_WORLD_READABLE);
        String configure = pre.getString(SPSettingkey, "");
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

    public static String getSID(){
        if (SID.isEmpty()){
            SID = DBOperator.getContactsOwnerMid();
            if (SID.isEmpty()) return  "";
        }
        return SID;
    }


    //Sender
    private void SenderTask(JSONObject root ) throws JSONException {
        Log.d(TAG, "EBusService Enter SenderTask");

        if (root.has("device") && root.getString("device").contains("mobile")) {
            if (root.has("action") && root.getString("action").equals("send")) {
                Log.d(TAG , "OnMqMsg  SenderTask send " + root.toString());

                //auth
                if ( ! DBOperator.contactsExist( root ) ) {
                    Log.d(TAG , "OnMqMsg SenderTask  send not contactsExits");
                    return;
                }

                //sqlite readed
                DBOperator.chatHistoryDelInsert( root );
                QueryUnreadDBSendBroadcast("mqmsg" , 0 , 100 , true);

                if (WeChat.isActive()){
                    unreadnum = 0 ;
                    ShortcutBadger.with( getApplicationContext()).count( unreadnum );
                }

                SendLoopBack( root );
                Log.d(TAG, "EBusService SenderTask action:send ");
            }
        }


    }

    //Reciver
    private void ReciverTask(JSONObject root ) throws JSONException {
        Log.d(TAG, "EBusService Enter ReciverTask");

        if (root.has("device") && root.getString("device").contains("mobile")) {

            if (root.has("action") && root.getString("action").equals("send")) {
                Log.d(TAG , "OnMqMsg send " + root.toString());

                //auth
                if (!DBOperator.contactsExist(root) ) {
                    Log.d(TAG , "OnMqMsg send not contactsExits");
                    return;
                }

                //sqlite readed
                DBOperator.chatHistoryDelInsert( root );
                QueryUnreadDBSendBroadcast("mqmsg" , 0 , 100 , true);

                if (WeChat.isActive()){
                    unreadnum = 0 ;
                    ShortcutBadger.with( getApplicationContext()).count( unreadnum );
                }

                SendLoopBack( root );
                Log.d(TAG, "EBusService onMqMsg action send mobile ");
            }


            //invite
            if (root.has("action") && root.getString("action").equals("invite") ) {
                Log.d(TAG , root.toString() );

                //not contacts
                if (! DBOperator.contactsExist(root) ) {
                    //readed
                    sendPureBroadcast("invite", root);
                    Log.d(TAG, "EBusService sendPureBroadcast invite");
                }

                //write cloud DB
                SendLoopBack(root);
                Log.d(TAG , "EBusService onMqMsg action invite mobile");
            }


            //NOTIFYSEND
            if (root.has("action") && root.getString("action").equals("notify") ) {
                //DelInsertDB(root);
                //DBOperator.chatHistoryDelInsert( root );
                //SendLoopBack( root );
                //UpdateDB(root, 1);
                //DBOperator.chatHistoryUpdateDB( root , 1);

                //EBundle eb = new EBundle();
                //eb.action =  EBusService.NOTIFYSEND;
                //eb.pack = root;
                //EventBus.getDefault().post(eb);
                Log.d(TAG, "EBusService onMqMsg action notify mobile");
            }

        }
    }


    private Emitter.Listener OnMqMsg = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "EBusService OnMqMsg");
            try {
                if (args[0] == null ) return ;
                JSONObject obj = new JSONObject(args[0].toString());
                obj.remove("device");
                obj.put("device", "mobile");

                JSONObject root = new JSONObject();
                JSONArray jarr = new JSONArray();
                jarr.put( obj );
                root.put("data", jarr);
                root.put("length", jarr.length());
                root.remove("device");
                root.put("device", "mobile");
                root.put("action",obj.getString("action"));


                //global auth add channel
                if ( DBOperator.contactsExist(root) ) {
                    HashSet<String> channArr = getInviteChann2(root);
                    Iterator<String> it = channArr.iterator();
                    while (it.hasNext()) {
                        String chann = it.next();
                        if (!channSet.contains( chann)) {
                            JSONObject tmpchann = new JSONObject();
                            tmpchann.put("channel", chann);
                            Subscribe(tmpchann);
                            channSet.add(chann);
                        }
                    }
                }

                if (obj.getString("sid").equals( getSID() )){
                    //sender
                    root.put("who","sender");
                    SenderTask( root  );
                }else{
                    //reciver
                    root.put("who","reciver");
                    ReciverTask(root  );
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    //unread && reciever
    private Emitter.Listener OnMqUnread = new Emitter.Listener(){
        @Override
        public void call(Object... args) {
            Log.d(TAG , "EBusService OnMqUnread");
            if (args[0] == null ) return ;
            try {
                JSONObject root = new JSONObject(args[0].toString());
                root.put("who", "reciver");

                //global auth add channel
                if (DBOperator.contactsExist(root)) {
                    HashSet<String> channArr = getInviteChann2(root);
                    Iterator<String> it = channArr.iterator();
                    while (it.hasNext()) {
                        String chann = it.next();
                        if (!channSet.contains( chann )) {
                            JSONObject tmpchann = new JSONObject();
                            tmpchann.put("channel", chann);
                            Subscribe(tmpchann);
                            channSet.add(chann);
                        }
                    }
                }

                if (root.has("device") && root.getString("device").contains("mobile")) {
                    if (root.has("action") && root.getString("action").equals("send")) {
                        Log.d(TAG, "OnMqMsg send " + root.toString());

                        //auth
                        if (!DBOperator.contactsExist(root)) {
                            Log.d(TAG, "OnMqUnread send not contactsExits");
                            return;
                        }

                        //sqlite readed
                        DBOperator.chatHistoryDelInsert(root);
                        QueryUnreadDBSendBroadcast("mqmsg", 0, 100, true);

                        if (WeChat.isActive()) {
                            unreadnum = 0;
                            ShortcutBadger.with(getApplicationContext()).count(unreadnum);
                        }

                        SendUnReadLoopBack(root);
                        Log.d(TAG, "EBusService OnMqUnread action send mobile ");
                    }


                    //invite
                    if (root.has("action") && root.getString("action").equals("invite")) {
                        Log.d(TAG, root.toString());

                        //not contacts
                        if (!DBOperator.contactsExist(root)) {
                            //readed
                            sendPureBroadcast("invite", root);
                            Log.d(TAG, "EBusService OnMqUnread sendPureBroadcast invite");
                        }

                        //write cloud DB
                        SendUnReadLoopBack(root);
                        Log.d(TAG, "EBusService OnMqUnread action invite mobile");
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private Emitter.Listener OnAsked = new Emitter.Listener(){

        @Override
        public void call(Object... args) {
            Log.d(TAG , "EBusService OnAsked");
            if (args[0] == null ) return ;

            try {
                JSONObject obj = new JSONObject(args[0].toString());
                JSONObject root = new JSONObject();
                JSONArray jarr = new JSONArray();

                jarr.put( obj );
                root.put("data", jarr);
                root.put("length", jarr.length());
                root.remove("device");
                root.put("device", "mobile");
                root.put("action",obj.getString("action"));

                Log.d(TAG , root.toString() );
                //global auth add channel
                if (DBOperator.contactsExist(root)) {
                    HashSet<String> channArr = getInviteChann2(root);
                    Iterator<String> it = channArr.iterator();
                    while (it.hasNext()) {
                        String chann = it.next();
                        if (!channSet.contains( chann )) {
                            JSONObject tmpchann = new JSONObject();
                            tmpchann.put("channel", chann);
                            Log.d(TAG , "ASKED Subscirbe---->" + chann );
                            Subscribe(tmpchann);
                            channSet.add(chann);
                        }
                    }
                }


                Log.d(TAG , obj.toString() );
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
                channSet.clear();
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
                channSet.clear();
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
