package com.example.converter2_0.currency_logic

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import com.example.converter2_0.currency_logic.currencies.CurrencyNBU
import com.example.converter2_0.currency_logic.currencies.CurrencyPrivat
import com.example.converter2_0.currency_logic.responses.ResponseNBU
import com.example.converter2_0.currency_logic.responses.ResponsePrivat
import kotlinx.coroutines.*

class CurrenciesInterface {
    //Currencies
    private var currenciesNBU = mutableListOf<CurrencyNBU>()
    private var currenciesPrivat = mutableListOf<CurrencyPrivat>()
    //Responses
    private var responseNBU: ResponseNBU? = null
    private var responsePrivat: ResponsePrivat? = null
    //Internet connection
    private var internet_connection: Boolean = false
    private var switcher: Boolean = false


    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("UnsafeOptInUsageWarning")
    fun start(context: Context?): Boolean {
        if (switcher) {
            return true
        }
        responseNBU = ResponseNBU()
        responsePrivat = ResponsePrivat()
        if(isOnline(context!!)) {
            switcher = true
        } else {
            Toast.makeText(context, "No connection to the Internet", Toast.LENGTH_LONG).show()
            return false
        }
        initCurrencies()
        return true
    }


    @DelicateCoroutinesApi
    fun initCurrencies() {
        CoroutineScope(Dispatchers.IO).launch {
            async { initializeNBU()
                    //initializePrivat()
            }.await()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("UnsafeOptInUsageWarning")
    private suspend fun initializeCurrencies() {
        initializeNBU()
        //initializePrivat()
    }


    @DelicateCoroutinesApi
    private suspend fun initializeNBU() {
        responseNBU!!.getResponse()
        delay(2000)
        for (i in 0 until responseNBU!!.ccs.size) {
            currenciesNBU.add(CurrencyNBU(responseNBU!!.ccs[i], responseNBU!!.values[i]))
        }
    }
    @DelicateCoroutinesApi
    private suspend fun initializePrivat() {
        responsePrivat!!.getResponse()
        delay(2000)
        for (i in 0 until responsePrivat!!.ccs.size) {
            currenciesPrivat.add(CurrencyPrivat(responsePrivat!!.ccs[i], responsePrivat!!.values_buy[i], responsePrivat!!.values_sale[i]))
        }
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun getCostNBU(currency_1_string: String, currency_2_string: String): Double {
        if (currency_1_string == currency_2_string)
            return 1.0

        return if (currency_1_string == "UAH") {
            val cost = getCurrencyValueNBU(currency_2_string)
            cost
        }
        else if(currency_2_string == "UAH") {
            val cost = 1 / getCurrencyValueNBU(currency_1_string)
            cost
        }
        else {
            val cost = getCurrencyValueNBU(currency_2_string) / getCurrencyValueNBU(currency_1_string)
            cost
        }
    }
    private fun getCurrencyValueNBU(currency_сс: String): Double {
        var currency_value: Double = 0.0
        for (i in 0 until currenciesNBU.size) {
            if (currenciesNBU[i].getCc() == currency_сс) {
                currency_value = currenciesNBU[i].getValue()
            }
        }
        return currency_value
    }

    fun getCostPrivat(currency_1_string: String, currency_2_string: String): Double {
        if (currency_1_string == currency_2_string)
            return 1.0

        return if (currency_1_string == "UAH") {
            val cost = getValueBuyPriv(currency_2_string)
            cost
        }
        else if(currency_2_string == "UAH") {
            val cost = 1 / getValueSalePriv(currency_1_string)
            cost
        }
        else {
            val cost = getValueBuyPriv(currency_2_string) / getValueSalePriv(currency_1_string)
            cost
        }
    }
    private fun getValueBuyPriv(currency_сс: String): Double {
        var currency_value: Double = 0.0
        for (i in 0 until currenciesPrivat.size) {
            if (currenciesPrivat[i].getCc() == currency_сс) {
                currency_value = currenciesPrivat[i].getValueBuy()
            }
        }
        return currency_value
    }
    private fun getValueSalePriv(currency_сс: String): Double {
        var currency_value: Double = 0.0
        for (i in 0 until currenciesPrivat.size) {
            if (currenciesPrivat[i].getCc() == currency_сс) {
                currency_value = currenciesPrivat[i].getValueSell()
            }
        }
        return currency_value
    }
}