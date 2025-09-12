package com.vermouthx.stocker.listeners;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.vermouthx.stocker.entities.StockerQuote;
import com.vermouthx.stocker.settings.StockerSettingState;
import com.vermouthx.stocker.utils.StockerTableModelUtil;
import com.vermouthx.stocker.views.StockerTableView;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Map;

public class StockerQuoteUpdateListener implements StockerQuoteUpdateNotifier {
    private final StockerTableView myTableView;

    private final String NOTIFICATION_GROUP_ID = "Stocker";

    public StockerQuoteUpdateListener(StockerTableView myTableView) {
        this.myTableView = myTableView;
    }

    @Override
    public void syncQuotes(List<StockerQuote> quotes, int size, Map<String, StockerSettingState.Wealth> wealthMap) {
        DefaultTableModel tableModel = myTableView.getTableModel();
        quotes.forEach(quote -> {
            synchronized (myTableView.getTableModel()) {
                StockerSettingState.Wealth wealth = wealthMap.getOrDefault(quote.getCode(), new StockerSettingState.Wealth());
                int rowIndex = StockerTableModelUtil.existAt(tableModel, quote.getCode());
                if (rowIndex != -1) {
                    if (!tableModel.getValueAt(rowIndex, 1).equals(quote.getName())) {
                        tableModel.setValueAt(quote.getName(), rowIndex, 1);
                        tableModel.fireTableCellUpdated(rowIndex, 1);
                    }
                    if (!tableModel.getValueAt(rowIndex, 2).equals(quote.getCurrent())) {
                        tableModel.setValueAt(quote.getCurrent(), rowIndex, 2);
                        tableModel.fireTableCellUpdated(rowIndex, 2);

                        // 破位价
                        if (wealth.breakPrice(quote.getCurrent())) {
                            var content = quote.getName() + ", current： " + quote.getCurrent();
                            var notification = NotificationGroupManager.getInstance().getNotificationGroup(NOTIFICATION_GROUP_ID)
                                    .createNotification("Break price", content, NotificationType.WARNING);
                            notification.notify(null);
                        }

                    }
                    if (!tableModel.getValueAt(rowIndex, 3).equals(quote.getPercentage())) {
                        tableModel.setValueAt(quote.getPercentage() + "%", rowIndex, 3);
                        tableModel.fireTableCellUpdated(rowIndex, 3);
                    }

                    if (wealth.have()) {
                        if (!tableModel.getValueAt(rowIndex, 4).equals(wealth.getCost())) {
                            tableModel.setValueAt(wealth.getCost(), rowIndex, 4);
                            tableModel.fireTableCellUpdated(rowIndex, 4);
                        }
                        if (!tableModel.getValueAt(rowIndex, 5).equals(wealth.getHold())) {
                            tableModel.setValueAt(wealth.getHold(), rowIndex, 5);
                            tableModel.fireTableCellUpdated(rowIndex, 5);
                        }
                        if (!tableModel.getValueAt(rowIndex, 6).equals(wealth.tIncome(quote.getChange()))) {
                            tableModel.setValueAt(wealth.tIncome(quote.getChange()), rowIndex, 6);
                            tableModel.fireTableCellUpdated(rowIndex, 6);
                        }
                        if (!tableModel.getValueAt(rowIndex, 7).equals(wealth.income(quote.getCurrent()))) {
                            tableModel.setValueAt(wealth.income(quote.getCurrent()), rowIndex, 7);
                            tableModel.fireTableCellUpdated(rowIndex, 7);
                        }

                    }
                } else {
                    if (quotes.size() == size) {
                        if (wealth.have()) {
                            tableModel.addRow(new Object[]{quote.getCode(), quote.getName(), quote.getCurrent(), quote.getPercentage() + "%", wealth.getCost(), wealth.getHold(), wealth.tIncome(quote.getChange()), wealth.income(quote.getCurrent())});
                        } else {
                            tableModel.addRow(new Object[]{quote.getCode(), quote.getName(), quote.getCurrent(), quote.getPercentage() + "%"});
                        }
                    }
                }
            }
        });
    }

    @Override
    public void syncIndices(List<StockerQuote> indices) {
        synchronized (myTableView) {
            myTableView.syncIndices(indices);
        }
    }

}
