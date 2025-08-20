package com.vermouthx.stocker.listeners;

import com.intellij.util.messages.Topic;
import com.vermouthx.stocker.entities.StockerQuote;
import com.vermouthx.stocker.settings.StockerSettingState;

import java.util.List;
import java.util.Map;

public interface StockerQuoteUpdateNotifier {
    Topic<StockerQuoteUpdateNotifier> STOCK_ALL_QUOTE_UPDATE_TOPIC = Topic.create("StockAllQuoteUpdateTopic", StockerQuoteUpdateNotifier.class);
    Topic<StockerQuoteUpdateNotifier> STOCK_CN_QUOTE_UPDATE_TOPIC = Topic.create("StockCNQuoteUpdateTopic", StockerQuoteUpdateNotifier.class);
    Topic<StockerQuoteUpdateNotifier> STOCK_HK_QUOTE_UPDATE_TOPIC = Topic.create("StockHKQuoteUpdateTopic", StockerQuoteUpdateNotifier.class);
    Topic<StockerQuoteUpdateNotifier> STOCK_US_QUOTE_UPDATE_TOPIC = Topic.create("StockUSQuoteUpdateTopic", StockerQuoteUpdateNotifier.class);
    Topic<StockerQuoteUpdateNotifier> CRYPTO_QUOTE_UPDATE_TOPIC = Topic.create("CryptoQuoteUpdateTopic", StockerQuoteUpdateNotifier.class);

    void syncQuotes(List<StockerQuote> quotes, int size, Map<String, StockerSettingState.Wealth> wealthMap);

    void syncIndices(List<StockerQuote> indices);
}
