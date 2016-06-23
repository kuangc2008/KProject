package asynctask.com.kc.kgson;

import org.json.JSONObject;

/**
 * Created by kuangcheng01 on 2016/6/23.
 */
public interface IKJSonObject extends IKObject{

    public boolean fillByJsonObject(JSONObject jo);

    public boolean fillInJsonObject(JSONObject jo);
}
