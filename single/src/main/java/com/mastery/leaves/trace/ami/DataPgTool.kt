package com.mastery.leaves.trace.ami


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Base64
import android.util.Log
import com.mastery.leaves.trace.ami.ChongTool.postPointFun
import com.mastery.leaves.trace.core.CanNextGo
import com.mastery.leaves.trace.data.KaiBe.adminUrl
import com.mastery.leaves.trace.data.KaiBe.upUrl
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class DataPgTool private constructor() {

    sealed class RequestResult {
        data class Success(val data: String) : RequestResult()
        data class Error(val message: String) : RequestResult()
    }

    companion object {
        val instance: DataPgTool by lazy { DataPgTool() }
    }

    fun showAppVersion(): String {
        return AllDataTool.getMainUser.packageManager
            .getPackageInfo(AllDataTool.getMainUser.packageName, 0).versionName
            ?: ""
    }

    @SuppressLint("HardwareIds")
    private fun buildAdminPayload(): JSONObject {
        return JSONObject().apply {
            put("jZlDvhUcps", "com.smooths.woclean")
            put("KeIUaug", AllDataTool.idState)
            put("pjJpjYRnZ", AllDataTool.refState)
            put("kRGj", showAppVersion())
            put("ZqdJZjSlTJ", AllDataTool.rone)
            put("nHoSrmc", AllDataTool.rtow)
            put("VMaOICHk", fetchInstaller(AllDataTool.getMainUser))

        }
    }

    private fun fetchInstaller(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            runCatching {
                context.packageManager.getInstallSourceInfo(context.packageName)?.let { info ->
                    info.initiatingPackageName ?: info.originatingPackageName
                }
            }.getOrNull() ?: ""
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getInstallerPackageName(context.packageName) ?: ""
        }
    }

    private val client = HttpClient(Android) {
        engine {
            connectTimeout = 60_000
            socketTimeout = 60_000
        }
        expectSuccess = false
    }

    fun postAdminData(callback: (RequestResult) -> Unit) {
        val payload = buildAdminPayload()
        CanNextGo.showLog("postAdminData=$payload")
        val jsonBodyString = payload.toString()
        val datetime = System.currentTimeMillis().toString()
        val xorEncryptedString = jxData(jsonBodyString, datetime)
        val base64EncodedString = Base64.encodeToString(
            xorEncryptedString.toByteArray(StandardCharsets.UTF_8),
            Base64.NO_WRAP
        )
        ChongTool.postPointFun(false, "config_R")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: HttpResponse = client.post(String.adminUrl) {
                    header("datetime", datetime)
                    contentType(ContentType.Application.Json)
                    setBody(base64EncodedString)
                }

                if (response.status.value != 200) {
                    withContext(Dispatchers.Main) {
                        callback(RequestResult.Error("Unexpected code ${response.status.value}"))
                        ChongTool.ConfigG(true, response.status.value.toString())
                    }
                    return@launch
                }

                try {
                    val timestampResponse = response.headers["datetime"]
                        ?: throw IllegalArgumentException("Timestamp missing in headers")

                    val responseBody = response.bodyAsText()
                    val decodedBytes = Base64.decode(responseBody, Base64.DEFAULT)
                    val decodedString = String(decodedBytes, Charsets.UTF_8)
                    val finalData = jxData(decodedString, timestampResponse)
                    val jsonResponse = JSONObject(finalData)
                    val stringData = parseAdminRefData(jsonResponse.toString())
                    val jsonData = if (stringData.isNotEmpty()) {
                        JSONObject(stringData)
                    } else {
                        jsonResponse
                    }
                    CanNextGo.showLog("onResponse-adminData: ${stringData}")
                    ChongTool.initFb(jsonData)
                    ChongTool.ConfigG(ChongTool.getAUTool(jsonData), "200")
                    isCanSave(jsonData)
                    withContext(Dispatchers.Main) {
                        callback(RequestResult.Success(jsonData.toString()))
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        callback(RequestResult.Error("Decryption failed: ${e.message}"))
                        postPointFun(true, "cf_fail")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback(RequestResult.Error("Request failed: ${e.message}"))
                }
            }
        }
    }

    fun isCanSave(jsonData: JSONObject) {
        val currentDataState = AllDataTool.dataState
        if (currentDataState.isNotEmpty()) {
            try {
                val currentJson = JSONObject(currentDataState)
                if (ChongTool.getAUTool(currentJson) && !ChongTool.getAUTool(jsonData)) {
                    return
                }
            } catch (e: Exception) {
                // 如果 dataState 不是有效的 JSON，忽略错误继续保存
            }
        }
        AllDataTool.dataState = jsonData.toString()
    }

    private fun jxData(text: String, datetime: String): String {
        val cycleKey = datetime.toCharArray()
        val keyLength = cycleKey.size
        return text.mapIndexed { index, char ->
            char.code.xor(cycleKey[index % keyLength].code).toChar()
        }.joinToString("")
    }

    private fun parseAdminRefData(jsonString: String): String {
        return try {
            JSONObject(jsonString).getJSONObject("CjuWay").getString("conf")
        } catch (e: Exception) {
            ""
        }
    }

    fun postPutData(body: Any, callback: (RequestResult) -> Unit) {
        val jsonBodyString = JSONObject(body.toString()).toString()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: HttpResponse = client.post(String.upUrl) {
                    contentType(ContentType.Application.Json)
                    setBody(jsonBodyString)
                }

                val responseData = response.bodyAsText()

                withContext(Dispatchers.Main) {
                    if (response.status.value !in 200..299) {
                        callback(RequestResult.Error("Unexpected code ${response.status.value}"))
                    } else {
                        callback(RequestResult.Success(responseData))
                    }
                }
            } catch (e: Exception) {
                CanNextGo.showLog("tba-Error: ${e.message}")
                withContext(Dispatchers.Main) {
                    callback(RequestResult.Error(e.message ?: "Unknown error"))
                }
            }
        }
    }
}
