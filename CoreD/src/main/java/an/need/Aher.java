package an.need;


import android.os.Handler;
import android.os.Message;

import an.nee.dc;


/**
 * Date：2025/7/28
 * Describe:
 */
// todo 重命名
public class Aher extends Handler {
    @Override
    public void handleMessage(Message message) {
        dc.dsgl(message.what);
    }
}
