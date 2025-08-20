package com.vermouthx.stocker.settings

import com.vermouthx.stocker.enums.StockerQuoteColorPattern
import com.vermouthx.stocker.enums.StockerQuoteProvider

class StockerSettingState {
    var version: String = ""
    var refreshInterval: Long = 5
    var quoteProvider: StockerQuoteProvider = StockerQuoteProvider.SINA
    var quoteColorPattern: StockerQuoteColorPattern = StockerQuoteColorPattern.RED_UP_GREEN_DOWN
    var aShareList: MutableList<String> = mutableListOf()
    var hkStocksList: MutableList<String> = mutableListOf()
    var usStocksList: MutableList<String> = mutableListOf()
    var cryptoList: MutableList<String> = mutableListOf()

    var wealthMap: MutableMap<String, Wealth> = mutableMapOf()


    class Wealth {
        var cost: Double = .0
        var hold: Int = 0

        fun have(): Boolean {
            return cost > .0 && hold > 0
        }

        fun income(current: Double): Double {
            return (current - cost) * hold
        }

        fun getCostStr(): String {
            return cost.toString()
        }

        fun setCostStr(costStr: String) {
            cost = costStr.toDouble()
        }

    }

}