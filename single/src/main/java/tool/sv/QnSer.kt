package tool.sv

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.Service.START_STICKY
import android.content.Intent
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.mastery.leaves.trace.R
import com.mastery.leaves.trace.ami.AllDataTool

class QnSer : Service() {
    private var mNotification: Notification? = null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        // 这个为demo类 创建前台服务，需要做一下差异化，不能直接就这样写了
        val channel = NotificationChannel(
            "Notification",
            "Notification Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        (getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            channel
        )
        mNotification = NotificationCompat.Builder(this, "Notification").setAutoCancel(false)
            // ic_24_transport 为一个24尺寸大小的图标，
            // 需要注意的时候每个项目如果复用图标的话需要将图片进行修改md5值
            // 同样用到地方用到的透明图标也需要使用md5进行修改
            .setContentText("").setSmallIcon(R.drawable.fel_rn).setOngoing(true)
            .setOnlyAlertOnce(true).setContentTitle("").setCategory(Notification.CATEGORY_CALL)
            .setCustomContentView(RemoteViews(packageName, R.layout.ss_vme)).build()
        AllDataTool.isOpenNotification = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        runCatching {
            // id需要修改
            startForeground(1000, mNotification)
        }
        return START_STICKY  // 必须用这个模式
    }

    override fun onDestroy() {
        AllDataTool.isOpenNotification = false
        super.onDestroy()
    }
}