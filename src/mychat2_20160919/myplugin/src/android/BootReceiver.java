package tw.com.bais.wechat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class BootReceiver extends BroadcastReceiver {
    final String TAG = "WeChat2";

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String SPSetting = "SPSETTING";

        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") ||
                intent.getAction().equals("android.intent.action.USER_PRESENT") ||
                intent.getAction().equals("tw.com.bais.wechat.BootReceiver") ){

            Log.d(TAG , "BootReceiver on Boot_COMPLETED ");
            try {
                SharedPreferences pre = context.getSharedPreferences(SPSetting, Context.MODE_WORLD_READABLE);
                String configure = pre.getString("wechatSetting", "");
                if (configure.isEmpty()) {
                    Log.d(TAG , "BootReceiver configure.isEmpty");
                    return;
                }

                JSONObject obj = new JSONObject(configure);
                //if (obj.toString() == null || !obj.has("serverip") || !obj.has("port") || !obj.has("notifyTarget") ) {
                if ( obj == null ){
                    Log.d(TAG , "BootReceiver obj.toString maybe null");
                    return;
                }

                EBundle eb = new EBundle();
                eb.action = EBusService.INIT;
                eb.settings = obj;

                sendService(context, eb);
                Log.d(TAG, "BootReceive END");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    //send EBundle
    public void sendService(final Context context , final EBundle eb){
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
}
