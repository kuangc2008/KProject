package asynctask.com.kc.kgson.source;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import asynctask.com.kc.kgson.util.FieldDescription;

/**
 * Created by kuangcheng01 on 2016/6/23.
 */
public class JsonDataSource implements IMapDataSource {

    private JSONObject jo;

    public JsonDataSource(JSONObject jo) {
        this.jo = jo;
    }

    @Override
    public Set<String> getKeys() {
        Iterator<?> it = jo.keys();
        if (it != null) {
            Set<String> set = new HashSet<>();
            while (it.hasNext()) {
                Object key = it.next();
                if (key!=null) {
                    set.add(key.toString());
                }
            }
            return set;
        }
        return null;
    }

    @Override
    public Object getObject(String key) {
        return jo.opt(key);
    }

    @Override
    public Object getObjectByType(String key, Type type) {
        Object value = getObject(key);
        if (value != null) {
            FieldDescription desc = new FieldDescription(type);

        }



        return null;
    }

    @Override
    public void set(String key, Object value) {
        try {
            jo.putOpt(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
