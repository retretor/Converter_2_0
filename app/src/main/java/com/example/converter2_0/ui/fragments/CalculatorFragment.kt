package com.example.converter2_0.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.converter2_0.R
import com.example.converter2_0.currency_logic.CurrenciesInterface
import com.example.converter2_0.currency_logic.currencies.CurrencyNBU
import kotlinx.coroutines.DelicateCoroutinesApi

class CalculatorFragment : Fragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {
    //UI
    private var currency_1_value: EditText? = null
    private var currency_2_value: TextView? = null
    private var choice_rates: Spinner? = null
    private var choice_currency_1: Spinner? = null
    private var choice_currency_2: Spinner? = null
    private var temp_spinner: Int = 0
    private var bankInfo: TextView? = null

    //Logic
    private var currenciesInterface = CurrenciesInterface()
    private var switcher: Boolean = false
    private var switcher_spinner: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        switcher =  currenciesInterface.start(context) != false
    }
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.convert_btn -> {
                convertCurrencies()
            }
            R.id.inverse_button -> {
                swapCurrencies()
            }
        }
    }
    private fun convertCurrencies() {
        currency_1_value = view?.findViewById(R.id.currency_1_value)
        currency_2_value = view?.findViewById(R.id.currency_2_value)
        choice_rates = view?.findViewById(R.id.choice_rates)
        choice_currency_1 = view?.findViewById(R.id.currency_1)
        choice_currency_2 = view?.findViewById(R.id.currency_2)
        bankInfo = view?.findViewById(R.id.bankInfo)

        if(currency_1_value?.text.toString().isEmpty()){
            Toast.makeText(activity, "Enter currency value!", Toast.LENGTH_SHORT).show()
            return
        }
        if (!switcher) {
            switcher = currenciesInterface.start(context) != false
            return
        }
        val cost: Double = getCostCurrency()
        setCurrency2Value(cost)
    }
    private fun getCostCurrency(): Double {
        val currency_1_string = choice_currency_1?.selectedItem.toString()
        val currency_2_string = choice_currency_2?.selectedItem.toString()
        val rates_string = choice_rates?.selectedItem.toString()
        var cost: Double = 1.0
        if(rates_string == "NBU") {
            cost = currenciesInterface.getCostNBU(currency_1_string, currency_2_string)
        } else {
            cost = currenciesInterface.getCostPrivat(currency_1_string, currency_2_string)
        }
        if (currency_1_string != "UAH" && currency_2_string != "UAH") {
            if (cost < 1) {
                bankInfo?.text = "Cross-course: " + String.format("%.2f", 1 / cost)
            }
            else {
                bankInfo?.text = "Cross-course: " + String.format("%.2f", cost)
            }
        } else {
            if (cost < 1) {
                bankInfo?.text = "Course-sell: " + String.format("%.2f", 1 / cost)
            }
            else {
                bankInfo?.text = "Course-buy: " + String.format("%.2f", cost)
            }
        }
        return cost
    }
    private fun setCurrency2Value(cost: Double) {
        val currency_1_value_string = currency_1_value?.text.toString()
        val currency_2_value_double = currency_1_value_string.toDouble() / cost
        currency_2_value?.text = String.format("%.2f", currency_2_value_double)
    }

    private fun swapCurrencies() {
        choice_currency_1 = view?.findViewById(R.id.currency_1)
        choice_currency_2 = view?.findViewById(R.id.currency_2)
        // swap the values of the two spinners
        temp_spinner = choice_currency_1!!.selectedItemPosition

        choice_currency_1?.setSelection(choice_currency_2!!.selectedItemPosition)
        choice_currency_2?.setSelection(temp_spinner)

        convertCurrencies()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_calculator, container, false)
        val convert_button: Button = view.findViewById(R.id.convert_btn)
        val inverse_button: ImageButton = view.findViewById(R.id.inverse_button)
        convert_button.setOnClickListener(this)
        inverse_button.setOnClickListener(this)

        //create arrayList_nbu = @string/string-array - nbu_rates
        val arrayList_nbu = arrayListOf<String>()
        val arrayList_privat = arrayListOf<String>()

        arrayList_nbu.addAll(resources.getStringArray(R.array.currency_codes))
        arrayList_privat.addAll(resources.getStringArray(R.array.currency_codes_privat))


        choice_rates = view.findViewById(R.id.choice_rates)
        val adapter_rates = ArrayAdapter(
            activity as Context,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.bank_names)
        )
        choice_rates?.adapter = adapter_rates
        //set the default size of text in the spinner
        choice_rates?.dropDownWidth = 400

        choice_currency_1 = view.findViewById(R.id.currency_1)
        choice_currency_2 = view.findViewById(R.id.currency_2)
        val adapter_currency_1 = ArrayAdapter(
            activity as Context,
            android.R.layout.simple_spinner_item,
            arrayList_nbu
        )
        val adapter_currency_2 = ArrayAdapter(
            activity as Context,
            android.R.layout.simple_spinner_item,
            arrayList_privat
        )
        choice_currency_1?.adapter = adapter_currency_1
        choice_currency_2?.adapter = adapter_currency_2

        choice_rates?.onItemSelectedListener = this
        choice_currency_1?.onItemSelectedListener = this
        choice_currency_2?.onItemSelectedListener = this

        return view
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
        //if the user selects in choice_rates nbu change the adapter of choice_currency_1 and choice_currency_2 to nbu_rates
        if (p0?.id == R.id.choice_rates) {
            if(choice_rates?.selectedItem.toString() == "NBU") {
                //save choice_currency_1 selected item text to new variable
                val temp_currency_1 = choice_currency_1?.selectedItem.toString()
                choice_currency_1?.adapter = ArrayAdapter(
                    activity as Context,
                    android.R.layout.simple_spinner_item,
                    resources.getStringArray(R.array.currency_codes)
                )
                //save choice_currency_2 selected item text to new variable
                val temp_currency_2 = choice_currency_2?.selectedItem.toString()
                choice_currency_2?.adapter = ArrayAdapter(
                    activity as Context,
                    android.R.layout.simple_spinner_item,
                    resources.getStringArray(R.array.currency_codes)
                )
                setSelectedItem(temp_currency_1, temp_currency_2)
            } else {
                //save choice_currency_1 selected item text to new variable
                val temp_currency_1 = choice_currency_1?.selectedItem.toString()
                choice_currency_1?.adapter = ArrayAdapter(
                    activity as Context,
                    android.R.layout.simple_spinner_item,
                    resources.getStringArray(R.array.currency_codes_privat)
                )
                //save choice_currency_2 selected item text to new variable
                val temp_currency_2 = choice_currency_2?.selectedItem.toString()
                choice_currency_2?.adapter = ArrayAdapter(
                    activity as Context,
                    android.R.layout.simple_spinner_item,
                    resources.getStringArray(R.array.currency_codes_privat)
                )
                setSelectedItem(temp_currency_1, temp_currency_2)
            }
        }
        else {
            if (switcher_spinner == 4) {
                convertCurrencies()
            } else {
                switcher_spinner++
            }
        }
    }

    private fun setSelectedItem(tempCurrency1: String, tempCurrency2: String) {
        for (i in 0 until choice_currency_1?.count!!) {
            if (choice_currency_1?.getItemAtPosition(i).toString() == tempCurrency1) {
                choice_currency_1?.setSelection(i)
            }
        }
        for (i in 0 until choice_currency_2?.count!!) {
            if (choice_currency_2?.getItemAtPosition(i).toString() == tempCurrency2) {
                choice_currency_2?.setSelection(i)
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}