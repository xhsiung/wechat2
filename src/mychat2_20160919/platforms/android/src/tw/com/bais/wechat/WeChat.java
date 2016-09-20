package tw.com.bais.wechat;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeChat extends CordovaPlugin{
    final String TAG = "WeChat2";
    static int activeInstance = 0 ;
    String filterAction = "tw.com.bais.wechat.WeChat";
    NetworkInfo mNetworkInfo = null;
    BroadcastReceiver receiver = null;
    CallbackContext  myCallbackContext= null;
    Activity context;
    CordovaWebView webView;

    JSONObject querydbdateParams;
    JSONObject registerParams;
    public static SQLiteDatabase mDB = null;
    public DBOperator dbOperator = null ;


    static boolean isActive(){
        return (activeInstance > 0);
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        activeInstance++ ;
        context = cordova.getActivity();

        this.webView = webView ;

        if (DBOperator.mDB == null){
            dbOperator = new DBOperator( context );
        }
    }


    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        initNetworkInfo();
        //network
        if (mNetworkInfo == null || !mNetworkInfo.isAvailable()) {
            Log.d(TAG , "WeChat2 execute mNetworkInfo error");
            return  false;
        }

        //start reciever
        if (action.equalsIgnoreCase("start")){
            Log.d(TAG , "start_test");
            //enable service
            sendService( context );

            if (this.myCallbackContext != null){
                callbackContext.error("listener already running");
                return  true;
            }
            this.myCallbackContext =  callbackContext ;
            startReceiver();

            PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
            //keep status callback
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);

            //send broadcast read data
            Intent intent = new Intent();
            intent.setAction("tw.com.bais.wechat.BootReceiver");
            context.sendBroadcast(intent);
            Log.d(TAG ,"start_test sendBroadcast");

            return true;
        }

        //stop reciever
        if (action.equalsIgnoreCase("stop")){
            Log.d(TAG , "stop reciever");
            stopReceiver();
            if (this.myCallbackContext != null) {
                PluginResult result = new PluginResult(PluginResult.Status.OK, new JSONObject());
                // release status callback in JS side
                result.setKeepCallback(false);
                this.myCallbackContext.sendPluginResult(result);
            }
            this.myCallbackContext = null ;
            callbackContext.success();
            return true;
        }

        //send broadcast
        if (action.equalsIgnoreCase("sendBroadcast")){
            Log.d(TAG, "sendBroadcast enter");
            //corodva exec ---> [arg0]
            JSONObject obj = args.getJSONObject(0);
            //sendBroadcast
            Intent intent = new Intent();
            intent.putExtra("data", obj.toString());
            intent.setAction( filterAction );
            cordova.getActivity().sendBroadcast(intent, null);

            callbackContext.success("sendBroadcast callback");
            return true;
        }

        if (action.equals("initConn")){
            Log.d(TAG , "WeChat initConn");
            JSONObject obj = args.getJSONObject(0);
            EBundle eb = new EBundle();
            eb.action = EBusService.INIT;
            eb.settings = obj ;
            sendService( context , eb);
            return true;
        }

        if (action.equals("saveconf")){
            Log.d(TAG , "WeChat saveconf");
            JSONObject obj = args.getJSONObject(0);
            EBundle eb = new EBundle();
            eb.action = EBusService.SAVECONF;
            eb.settings = obj ;
            sendService( context , eb);
            return true;
        }

        if (action.equals("connect")){
            return true;
        }

        if (action.equals("disconnect")){
            EBundle eb = new EBundle();
            eb.action =  EBusService.DISCONNECT;
            sendService( context , eb );
            return true;
        }

        if (action.equals("subscribe")){
            JSONObject obj = args.getJSONObject(0);
            EBundle eb = new EBundle();
            eb.action =  EBusService.SUBSCRIBE;
            eb.pack = obj;
            sendService( context , eb );
            return true;
        }

        if (action.equals("unsubscribe")){
            JSONObject obj = args.getJSONObject(0);
            EBundle eb = new EBundle();
            eb.action =  EBusService.UNSUBSCRIBE;
            eb.pack = obj;
            sendService( context , eb );
            return true;
        }

        if (action.equals("send")){
            JSONObject obj = args.getJSONObject(0);
            EBundle eb = new EBundle();
            eb.action =  EBusService.SEND;
            eb.pack = obj;
            sendService( context , eb );
            return true;
        }

        if (action.equals("notify")){
            JSONObject obj = args.getJSONObject(0);
            EBundle eb = new EBundle();
            eb.action =  EBusService.NOTIFYSEND;
            eb.pack = obj;
            sendService( context , eb );
            return true;
        }


        //return loopback save
        if (action.equals("loopback")){
            //{data:[{cid:"xxxx",sid:"",tid:""} ]}
            //JSONObject root = new JSONObject();

            JSONObject obj = args.getJSONObject(0);
            EBundle eb = new EBundle();
            eb.action = EBusService.LOOPBACK ;
            eb.pack = obj;
            sendService( context , eb);
            return true;
        }

        if (action.equals("querydbdate")){
            querydbdateParams = args.getJSONObject(0);
            WeChat.this.context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject obj = DBOperator.chatHistoryQueryDBDate( WeChat.this.querydbdateParams );
                        if (obj == null ){
                            callbackContext.error("error");
                        }else{
                            callbackContext.success( obj );
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
            return true;
        }

        //getDeviceID
        if (action.equals("deviceid")){
            callbackContext.success( getDeviceID() );
            return  true;
        }

        //register contacts
        if (action.equals("register")){
            Log.d(TAG , "WeChat register");
            registerParams = args.getJSONObject(0);
            final JSONObject xobj = DBOperator.chkContacts( registerParams );
            WeChat.this.context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if ( xobj == null){
                        callbackContext.error("error");
                        //return false;
                    }

                    try {
                        String  contactsAction = registerParams.getString("action");
                        if (contactsAction.equals("insert")){
                            if ( ! DBOperator.contactsIns( xobj ) ){
                                callbackContext.error("error");
                                //  return false;
                            }
                        }else if (contactsAction.equals("update")){
                            if ( ! DBOperator.contactsUpd( xobj ) ){
                                callbackContext.error("error");
                                //return false;
                            }
                        }else if (contactsAction.equals("delete")){
                            if ( ! DBOperator.contactsDel( xobj ) ){
                                callbackContext.error("error");
                                //return false;
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });

            return true;
        }

        if (action.equals("contacts")){
            JSONObject contactsSelObj = DBOperator.getContactsUser("all") ;
            if ( contactsSelObj == null ){
                callbackContext.error("error");
            }else{
                callbackContext.success( contactsSelObj);
            }

            return true;
        }

        //rereaded
        if (action.equals("rereaded")){
            Log.d(TAG , "WeChat rereaded");
            JSONObject obj = args.getJSONObject(0);
            DBOperator.chatHistoryReReaded( obj );
            return true;
        }

        //existOwner
        if (action.equals("existOwner")){
            if ( DBOperator.contactsExistOwner() ) {
                callbackContext.success( 1 );
            }else{
                callbackContext.success( 0 );
            }
            return true;
        }

        //unreadchat
        if (action.equals("unreadchat")){
            JSONObject jobj = DBOperator.chatLastQuery();
            if ( jobj == null ){
                callbackContext.error("error");
            }else{
                callbackContext.success( jobj);
            }
            return  true;
        }

        if (action.equals("ask")){
            JSONObject obj = args.getJSONObject(0);
            return true ;
        }

        return false;
    }

    //start BorodcastReceiver
    private void startReceiver(){
        IntentFilter intentFilter = new IntentFilter( filterAction );
        if (this.receiver == null){
            this.receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, "Got receiver");
                    final Bundle bundle = intent.getExtras();

                    if ( myCallbackContext != null && bundle != null ){
                        //mqmsg
                        if ( bundle.getString("action").equals("mqmsg")){
                            Log.d( TAG , "WeChat action mqmsg recieve");
                            try {
                                JSONObject  obj = new JSONObject( bundle.getString("data"));
                                PluginResult result = new PluginResult(PluginResult.Status.OK,  obj );
                                result.setKeepCallback(true);
                                myCallbackContext.sendPluginResult(result);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        //mqunread
                        if (bundle.getString("action").equals("mqunread")){
                            Log.d( TAG , "WeChat action mqunread recieve");
                            try {
                                JSONObject  obj = new JSONObject( bundle.getString("data"));
                                PluginResult result = new PluginResult(PluginResult.Status.OK, obj );
                                result.setKeepCallback(true);
                                myCallbackContext.sendPluginResult(result);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        //socket status
                        if (bundle.getString("action").equals("socket")) {
                            Log.d(TAG, "WeChat action system recieve");
                            WeChat.this.context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject obj = null;
                                    try {
                                        obj = new JSONObject( bundle.getString("data") );
                                        WeChat.this.webView.loadUrl("javascript: wechatOnConnectError("+ obj + ")");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        //system test
                        if (bundle.getString("action").equals("invite")) {
                            Log.d(TAG, "WeChat action invite recieve");
                            WeChat.this.context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String  jstr =  bundle.getString("data") ;
                                    JSONObject obj = null;
                                    try {
                                        obj = new JSONObject( jstr );
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    WeChat.this.webView.loadUrl("javascript: wechatInviteRecived("+ obj + ")");
                                }
                            });
                        }

                        //system test
                        if (bundle.getString("action").equals("system")) {
                            Log.d(TAG, "WeChat action system recieve");
                            WeChat.this.context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String  jstr =  bundle.getString("data") ;
                                    WeChat.this.webView.loadUrl("javascript: wechatRecieve('"+ jstr + "')");
                               }
                            });
                        }


                    }
                }
            };
            //register listener
            webView.getContext().registerReceiver(this.receiver , intentFilter);
        }
    }

    //stop BorodcastReceiver
    private void stopReceiver(){
        if (this.receiver != null){
            try{
                webView.getContext().unregisterReceiver(this.receiver);
                this.receiver = null ;
            }catch (Exception e){
                Log.d(TAG , "Error stopReceiver");
            }
        }
    }


    private void initNetworkInfo() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager)cordova.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
    }


    //start service
    public void sendService(Activity context){
        if ( ! EBusService.isActive() ){
            Intent intent = new Intent( context , EBusService.class);
            context.startService( intent );
        }
    }

    //send EBundle
    public void sendService(final Activity context , final EBundle eb){
        if (EBusService.isActive()){
            EventBus.getDefault().post(eb);
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Intent intent = new Intent( context , EBusService.class);
                        context.startService( intent );
                        Thread.sleep( 3000 );
                        EventBus.getDefault().post(eb);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public String getDeviceID(){
        try {
            return Settings.Secure.getString( context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        }catch (Exception e){
            return  "nulldeviceid";
        }
    }


    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        activeInstance = 0;
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        activeInstance++;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        stopReceiver();
        super.onDestroy();
    }
}
