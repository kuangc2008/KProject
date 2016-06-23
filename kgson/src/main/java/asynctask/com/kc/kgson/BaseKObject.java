package asynctask.com.kc.kgson;

import org.json.JSONObject;

/**
 * Created by kuangcheng01 on 2016/6/23.
 */
public class BaseKObject implements IKJSonObject{
    @Override
    public boolean fillByJsonObject(JSONObject jo) {
        return false;
    }

    @Override
    public boolean fillInJsonObject(JSONObject jo) {
        return false;
    }

    @Override
    public void onPreObjectToSource() {

    }

    @Override
    public void onFinishSourceToObject(boolean isSuccess) {

    }
}
