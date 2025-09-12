package com.vermouthx.stocker.views.dialogs

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.AlignY
import com.intellij.ui.dsl.builder.panel
import com.vermouthx.stocker.enums.StockerMarketType
import java.awt.BorderLayout
import java.net.URL
import javax.imageio.ImageIO
import javax.swing.*


class StockerKImageDialog(val project: Project?, val market: StockerMarketType, val code: String) :
    DialogWrapper(project) {

    private val tabMap: MutableMap<StockShowType, JPanel> = mutableMapOf()

    init {
        title = "K"
        init()
    }

    override fun createCenterPanel(): DialogPanel {

        val tabbedPane = JBTabbedPane()
        for (type in StockShowType.entries) {
            tabbedPane.add(type.desc, createTabContent(type))
        }

        for (type in StockShowType.entries) {
            tabMap[type]?.let { pane ->
                renderTabPane(pane, imageUrl(type.type))
            }
        }

        tabbedPane.selectedIndex = 0
        return panel {
            row {
                cell(tabbedPane).align(AlignX.FILL)
            }
        }.withPreferredWidth(300)
    }


    private fun createTabContent(type: StockShowType): JComponent {
        val pane = JPanel(BorderLayout())
        tabMap[type] = pane
        return panel {
            row {
                cell(pane).align(AlignX.FILL).align(AlignY.FILL)
            }
        }
    }

    private fun renderTabPane(pane: JPanel, imageUrl: String) {
        println("imageUrl:$imageUrl")
        val jLabel = JLabel("加载中...")
        try {
            val url = URL(imageUrl)
            val image = ImageIO.read(url)
            jLabel.setIcon(ImageIcon(image))
            jLabel.setText("");
        } catch (e: Exception) {
            println("网络图像加载失败：" + e.message)
            jLabel.setText("无法加载图像，请检查网络连接。")
        }
        pane.add(jLabel, BorderLayout.CENTER)
    }

    override fun createActions(): Array<Action> {
        return emptyArray()
    }

    fun imageUrl(type: String): String {
        var url = "http://image.sinajs.cn/newchart"
        return when (market) {
            StockerMarketType.AShare ->
                // 沪深股
                // 分时线图  http://image.sinajs.cn/newchart/min/n/sh600519.gif
                // 日K线图  http://image.sinajs.cn/newchart/daily/n/sh600519.gif
                // 周K线图  http://image.sinajs.cn/newchart/weekly/n/sh600519.gif
                // 月K线图  http://image.sinajs.cn/newchart/monthly/n/sh600519.gif
                String.format("%s/%s/n/%s.gif?%s", url, type, code.lowercase(), System.currentTimeMillis())

            // 非A不实现
            // 可参考：https://github.com/huage2580/leeks/blob/master/src/main/java/utils/PopupsUiUtil.java
            else -> url
        }
    }


    enum class StockShowType(val type: String, val desc: String) {

        /**
         * 分时线图
         */
        min("min", "分时线图"),

        /**
         * 日K线图
         */
        daily("daily", "日K线图"),

        /**
         * 周K线图
         */
        weekly("weekly", "周K线图"),

        /**
         * 月K线图
         */
        monthly("monthly", "月K线图")
    }

}
