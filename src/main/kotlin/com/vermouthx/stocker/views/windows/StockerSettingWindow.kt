package com.vermouthx.stocker.views.windows

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.*
import com.vermouthx.stocker.enums.StockerMarketType
import com.vermouthx.stocker.enums.StockerQuoteColorPattern
import com.vermouthx.stocker.enums.StockerQuoteProvider
import com.vermouthx.stocker.settings.StockerSetting
import com.vermouthx.stocker.settings.StockerSettingState
import com.vermouthx.stocker.utils.StockerQuoteHttpUtil

class StockerSettingWindow : BoundConfigurable("Stocker") {

    private val setting = StockerSetting.instance

    private var colorPattern: StockerQuoteColorPattern = setting.quoteColorPattern
    private var quoteProviderTitle: String = setting.quoteProvider.title
    private var wealthMap: MutableMap<String, StockerSettingState.Wealth> = setting.wealthMap

    override fun createPanel(): DialogPanel {
        return panel {
            group("General") {
                row("Provider: ") {
                    comboBox(
                        StockerQuoteProvider.values()
                            .map { it.title }).bindItem(::quoteProviderTitle.toNullableProperty())
                }
            }

            group("Appearance") {
                buttonsGroup("Color Pattern: ") {
                    row {
                        radioButton("Red up and green down", StockerQuoteColorPattern.RED_UP_GREEN_DOWN)
                    }
                    row {
                        radioButton("Green up and red down", StockerQuoteColorPattern.GREEN_UP_RED_DOWN)
                    }
                    row {
                        radioButton("None", StockerQuoteColorPattern.NONE)
                    }
                }.bind(::colorPattern.toMutableProperty(), StockerQuoteColorPattern::class.java)
            }

            group("Wealth") {

                val aShareStockQuotes = StockerQuoteHttpUtil.get(StockerMarketType.AShare, setting.quoteProvider, setting.aShareList)

                aShareStockQuotes.forEach { stockQute ->
                    row(stockQute.name) {
                        val w = wealthMap.getOrDefault(stockQute.code, StockerSettingState.Wealth());
                        wealthMap.putIfAbsent(stockQute.code, w)
                        label("Cost:")
                        textField().bindText(w::getCostStr, w::setCostStr).columns(8)
                        label("Hold:")
                        textField().bindIntText(w::hold.toMutableProperty()).columns(8)

                        label("LPrice:")
                        textField().bindText(w::getLPriceStr, w::setLPriceStr).columns(8)
                        label("HPrice:")
                        textField().bindText(w::getHPriceStr, w::setHPriceStr).columns(8)
                    }
                }

            }

            onApply {
                setting.quoteProvider = setting.quoteProvider.fromTitle(quoteProviderTitle)
                setting.quoteColorPattern = colorPattern
                setting.wealthMap = wealthMap
            }
            onIsModified {
                quoteProviderTitle != setting.quoteProvider.title
                colorPattern != setting.quoteColorPattern
                setting.wealthMap.values != wealthMap.values
            }
            onReset {
                quoteProviderTitle = setting.quoteProvider.title
                colorPattern = setting.quoteColorPattern
                wealthMap = setting.wealthMap
            }
        }
    }

}
