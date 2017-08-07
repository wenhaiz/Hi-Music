package com.example.wenhai.listenall.utils

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object OkHttpUtil {
    const val TAG = "OkHttpUtil"

    @JvmStatic
    var client: OkHttpClient? = null

    @JvmStatic
    fun getHttpClient(): OkHttpClient {
        if (client == null) {
            synchronized(OkHttpUtil::class.java) {
                if (client == null) {
                    client = OkHttpClient.Builder()
                            .cookieJar(object : CookieJar {
                                //cookie policy
                                override fun saveFromResponse(url: HttpUrl?, cookies: MutableList<Cookie>?) {
                                    LogUtil.d(TAG, "cookie for $url")
                                    if (cookies != null) {

                                        for (cookie in cookies) {
                                            LogUtil.d(TAG, "cookieName:${cookie.name()},cookieValue:${cookie.value()}")
                                        }
                                    }
                                }

                                override fun loadForRequest(url: HttpUrl?): MutableList<Cookie> {
                                    return ArrayList()
                                }

                            })
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .build()
                }
            }
        }
        return client as OkHttpClient
    }

    @JvmStatic
    fun getForXiami(url: String, callback: JsonCallback) {
        //start request
        LogUtil.d(TAG, url)
        callback.onStart()
        val request = Request.Builder()
                .url(url)
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .addHeader("Connection", "keep-alive")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("Referer", "http://m.xiami.com/")
                .addHeader("Host", "api.xiami.com")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36")
                .get()
                .build()
        val client = getHttpClient()
        LogUtil.d(TAG, "client:${client.hashCode()}")
        val newCall = client.newCall(request)

        newCall.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                if (e != null) {
                    callback.onFailure(e.localizedMessage)
                }
            }

            override fun onResponse(call: Call?, response: Response?) {
                if (response == null) {
                    callback.onFailure("response == null")
                } else {
                    val body = response.body() !!.string()
                    LogUtil.d("response:", body)
                    if (body.startsWith("jsonp")) {//xiami
                        val realJsonStr = body.substring(body.indexOf("(") + 1, body.lastIndexOf(")"))
                        callback.onResponse(JSONObject(realJsonStr).getJSONObject("data"))
                    } else {
                        callback.onFailure("response body:$body")
                    }
                }
            }

        })
    }
}

interface JsonCallback {
    fun onStart()
    fun onResponse(data: JSONObject)
    fun onFailure(msg: String)
}