package com.example.converter2_0.currency_logic.currencies

class CurrencyPrivat(currency_code: String, currency_value_buy: Double, currency_value_sell: Double){
    private var currency_code: String
    private var currency_value_buy: Double
    private var currency_value_sell: Double

    init{
        this.currency_code = currency_code
        this.currency_value_buy = currency_value_buy
        this.currency_value_sell = currency_value_sell
    }
    fun getCc(): String{
        return currency_code
    }
    fun getValueBuy(): Double{
        return currency_value_buy
    }
    fun getValueSell(): Double{
        return currency_value_sell
    }
}