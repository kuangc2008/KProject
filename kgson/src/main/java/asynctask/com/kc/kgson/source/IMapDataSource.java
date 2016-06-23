package asynctask.com.kc.kgson.source;

import java.lang.reflect.Type;
import java.util.Set;

/**
 * 定义一个类Map的数据类型，有key-value特性的数据源
 */
public interface IMapDataSource {

    public Set<String> getKeys();

    public Object getObject(String key);

    public Object getObjectByType(String key, Type type);

    public void set(String key, Object value);
}
