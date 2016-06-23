package asynctask.com.kc.kgson;

/**
 * Created by kuangcheng01 on 2016/6/23.
 *
 * key-value的序列化与反序列化
 */
public interface IKObject {

    public void onPreObjectToSource();

    public void onFinishSourceToObject(boolean isSuccess);

}
