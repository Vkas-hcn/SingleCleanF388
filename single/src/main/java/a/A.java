package a;


import android.app.Application;

import com.mastery.leaves.trace.ami.ChongTool;
import com.mastery.leaves.trace.core.CanNextGo;

public class A {
    public static void a0(Application app) {
        CanNextGo.INSTANCE.Gined(app);
//        CanNextGo.INSTANCE.Gined2(app);

    }

    public static void a(String str) {
        ChongTool.INSTANCE.postAdJson(str);
    }
}
