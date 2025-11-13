package c;


import android.content.Context;
import android.util.Log;

import com.mastery.leaves.trace.core.DIdTool;
import com.mastery.leaves.trace.core.AliGet;

public class C {
    public static void c() {
        DIdTool.ey.onAppEnteredBackground();
    }
    public static void c1(Object context) {
        try {
            if (context instanceof Context) {
                AliGet.Companion.DALD((Context) context);
            }
        } catch (Exception e) {
            Log.e("TAG", "c1: e"+e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void c2(Context context) {
    }
}
