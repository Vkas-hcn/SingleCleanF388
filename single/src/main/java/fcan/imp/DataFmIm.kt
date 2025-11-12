package fcan.imp

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import tool.sv.QnSer

class DataFmIm : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
        // 反射或者加点垃圾代码来实现
      openNotification(this)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

    }
    var isOpenNotification = false
    private var lastOpenTime = 0L

    fun openNotification(context: Context) {
        if (isOpenNotification && System.currentTimeMillis() - lastOpenTime < 60000 * 10) return
        lastOpenTime = System.currentTimeMillis()
        runCatching {
            ContextCompat.startForegroundService(
                context,
                Intent(context, QnSer::class.java)
            )
        }
    }
}