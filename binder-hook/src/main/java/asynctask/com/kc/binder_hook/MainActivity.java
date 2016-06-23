package asynctask.com.kc.binder_hook;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

/**
 * Created by kuangcheng01 on 2016/6/23.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            BinderHookHelper.hookClipboardService();
        } catch (Exception e) {
            e.printStackTrace();
        }

        EditText editText = new EditText(this);
        setContentView(editText);
    }
}
