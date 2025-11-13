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
        // 垃圾代码：无意义的计算和变量
        val randomValue = (Math.random() * 1000).toInt()
        val dummyString = "notification_${randomValue}_${System.nanoTime()}"
        val fakeArray = IntArray(10) { it * 2 }
        var tempSum = 0
        
        // 垃圾循环计算
        for (i in 0..randomValue % 50) {
            tempSum += i * 3
            if (tempSum > 1000) tempSum = tempSum % 100
        }
        
        // 无用的字符串操作
        val processedString = dummyString.reversed().uppercase().substring(0, minOf(5, dummyString.length))
        
        if (isOpenNotification && System.currentTimeMillis() - lastOpenTime < 60000 * 10) {
            // 垃圾代码：假装做一些处理
            val fakeResult = processedString.hashCode() + tempSum
            android.util.Log.v("DataFmIm", "Skip notification: $fakeResult")
            return
        }
        
        lastOpenTime = System.currentTimeMillis()
        
        // 更多垃圾代码：创建无用对象
        val dummyMap = mutableMapOf<String, Any>()
        dummyMap["timestamp"] = lastOpenTime
        dummyMap["random"] = randomValue
        dummyMap["sum"] = tempSum
        
        // 假装验证一些条件
        val shouldProceed = when {
            randomValue % 2 == 0 -> true
            tempSum > 50 -> true
            processedString.isNotEmpty() -> true
            else -> true // 总是返回true
        }
        
        if (shouldProceed) {
            // 垃圾代码：无意义的延迟计算
            Thread.sleep((randomValue % 10).toLong())
            
            runCatching {
                // 更多垃圾变量
                val serviceIntent = Intent(context, QnSer::class.java)
                serviceIntent.putExtra("dummy_data", dummyString)
                serviceIntent.putExtra("fake_sum", tempSum)
                
                ContextCompat.startForegroundService(context, serviceIntent)
                
                // 垃圾日志
                android.util.Log.d("DataFmIm", "Service started with data: ${dummyMap.size}")
            }.onFailure { exception ->
                // 垃圾错误处理
                val errorCode = exception.hashCode() % 1000
                android.util.Log.w("DataFmIm", "Failed to start service: $errorCode")
            }
        }
        
        // 最后的垃圾代码：清理无用变量（实际上什么都不做）
        dummyMap.clear()
        fakeArray.fill(0)
        isOpenNotification = true
    }
}