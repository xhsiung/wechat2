package tw.com.bais.wechat;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeChat extends CordovaPlugin{
    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        //return super.execute(action, args, callbackContext);

        if (action.equalsIgnoreCase("start")){
            JSONObject jobj = args.getJSONObject(0);
            callbackContext.success("my call back");
            //callbackContext.error("erorr");

            //PluginResult result = new PluginResult(PluginResult.Status.OK);
            //PluginResult result = new PluginResult(PluginResult.Status.ERROR);
            //PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
            //callbackContext.sendPluginResult( result);
            return true;
        }
        return false;
    }
}
