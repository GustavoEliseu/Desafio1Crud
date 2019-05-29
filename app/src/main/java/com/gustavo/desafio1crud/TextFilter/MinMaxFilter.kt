package com.gustavo.desafio1crud.TextFilter

import android.text.InputFilter
import android.text.Spanned

/**
 * versão kotlin refeita a partir do código java de npatel
 * Created by npatel on 4/5/2016.
 */
//classe introduzida para controlar o input de números para nota de cada matéria
class MinMaxFilter : InputFilter {

    private var mIntMin: Int = 0
    private var mIntMax: Int = 0

    constructor(minValue: Int, maxValue: Int) {
        this.mIntMin = minValue
        this.mIntMax = maxValue
    }

    constructor(minValue: String, maxValue: String) {
        this.mIntMin = Integer.parseInt(minValue)
        this.mIntMax = Integer.parseInt(maxValue)
    }

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val input = Integer.parseInt(dest.toString() + source.toString())
            if (isInRange(mIntMin, mIntMax, input))
                return null
        } catch (nfe: NumberFormatException) {
        }

        return ""
    }

    private fun isInRange(a: Int, b: Int, c: Int): Boolean {
        return if (b > a) c >= a && c <= b else c >= b && c <= a
    }
}