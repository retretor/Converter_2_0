package com.example.converter2_0.currency_logic.responses

import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONArray
import java.net.URL

class ResponseNBU {
    private val url: String = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchangenew?json"

    var ccs = mutableListOf<String>()
    var values = mutableListOf<Double>()

    @DelicateCoroutinesApi
    fun getResponse() {
        CoroutineScope(Dispatchers.IO).launch {
            async { getResponseCoroutine() }.await()
        }
    }

    suspend fun getResponseCoroutine () {
        val response = URL(url).readText()
        val jsonArray = JSONArray(response)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            values.add(jsonObject.getDouble("rate"))
            ccs.add(jsonObject.getString("cc"))
        }
    }
}