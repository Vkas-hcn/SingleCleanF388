package b;


import com.mastery.leaves.trace.ami.ChongTool;
import com.mastery.leaves.trace.core.CanNextGo;

public class B {
    public static void b(Boolean canRetry, String name, String key1, String keyValue1) {
       ChongTool.INSTANCE.postPointFun(canRetry, name, key1, keyValue1);
    }
}
