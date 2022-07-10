package com.example.converter2_0.currency_logic.responses

import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONArray
import java.net.URL
import java.util.*

class ResponsePrivat {
    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)

    private val url: String = "https://api.privatbank.ua/p24api/exchange_rates?json&date=$day.$month.$year"

    var ccs = mutableListOf<String>()
    var values_buy = mutableListOf<Double>()
    var values_sale = mutableListOf<Double>()


    @DelicateCoroutinesApi
    fun getResponse() {
        CoroutineScope(Dispatchers.IO).launch {
            async { getResponseCoroutine() }.await()
        }
    }

    suspend fun getResponseCoroutine() {
        var response = URL(url).readText()
        response = response.substring(response.indexOf("["), response.indexOf("]") + 1)
        val jsonArray2 = JSONArray(response)
        for (i in 1 until jsonArray2.length()) {
            val jsonObject = jsonArray2.getJSONObject(i)
            if(jsonObject.has("saleRate") && jsonObject.has("purchaseRate"))
            {
                values_buy.add(jsonObject.getDouble("purchaseRate"))
                values_sale.add(jsonObject.getDouble("saleRate"))
                ccs.add(jsonObject.getString("currency"))
            }
        }
    }
}