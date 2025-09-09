package com.vermouthx.stocker.views.windows

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.*
import com.vermouthx.stocker.enums.StockerQuoteColorPattern
import com.vermouthx.stocker.enums.StockerQuoteProvider
import com.vermouthx.stocker.settings.StockerSetting
import com.vermouthx.stocker.settings.StockerSettingState

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
                val codes = setting.aShareList
                codes.forEach { code ->
                    row(code) {
                        var w = wealthMap.getOrDefault(code, StockerSettingState.Wealth());
                        wealthMap.putIfAbsent(code, w)
                        label("Cost:")
                        textField().bindText(w::getCostStr, w::setCostStr)
                        label("Hold:")
                        textField().bindIntText(w::hold.toMutableProperty())

                        label("LPrice:")
                        textField().bindText(w::getLPriceStr, w::setLPriceStr)
                        label("HPrice:")
                        textField().bindText(w::getHPriceStr, w::setHPriceStr)
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
