package com.example.converter2_0.currency_logic.currencies

import android.util.Log

class CurrencyNBU(currency_code: String, currency_value: Double) {
    private var currency_code: String
    private var currency_value: Double

    init {
        this.currency_code = currency_code
        this.currency_value = currency_value
    }
    fun getCc(): String {
        return currency_code
    }
    fun getValue(): Double {
        return currency_value
    }
}