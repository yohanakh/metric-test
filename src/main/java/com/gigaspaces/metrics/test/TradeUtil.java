package com.gigaspaces.metrics.test;

public class TradeUtil {
    static String toString(TradePojo trade) {

        String toString = "object name = " + trade.getClass().getSimpleName() +
                "\n tradeId = " + trade.getTradeId() +
                "\n tradeStatus = " + trade.getTradeStatus() +
                "\n price = " + trade.getPrice() +
                "\n timestamp = " + trade.getTimestamp() +
                "\n payload = " + (trade.getPayload() == null ? "no payload" : trade.getPayload().length + "\n") +
                "\n quantity = " + trade.getQuantity();

        return toString;
    }


    public static TradePojo newInstanceByPrimaryKey(Integer primaryKey, int payLoad) {
        TradePojo trade = new TradePojo();

        trade.setTradeId(primaryKey);
        trade.setTradeStatus(TradePojo.TradeStatus.NEW);
        trade.setPrice(1000.0F);
        trade.setTimestamp(System.currentTimeMillis());
        trade.setQuantity(10);

        trade.setPayload(new byte[payLoad]);
        return trade;
    }
}
