/*
 * @(#)DataPojo.java 14/4/2008
 *
 * Copyright 2008 GigaSpaces Technologies Inc.
 */
package com.gigaspaces.metrics.test;

import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceIndex;
import com.gigaspaces.annotation.pojo.SpaceRouting;
import com.gigaspaces.metadata.index.SpaceIndexType;

import java.io.Serializable;

/**
 * POJO implementation for {@link ITrade} interface.
 *
 * @author hanang
 *         created on 14/4/2008
 * @version 6.5
 **/
public class TradePojo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Object's secondary key field.
     * This field is a regular field concerning the space, in metric it is
     * used to filter by thread context.
     */
    private Integer tradeId;

    /**
     * Object's space index.
     */
    private TradeStatus tradeStatus;

    private Float price;

    private Long timestamp;

    /**
     * Object's payload field
     */
    private byte[] payload;

    private Integer quantity;

    public TradePojo() {
    }


    @SpaceId(autoGenerate = false)
    public Integer getTradeId() {
        return tradeId;
    }

    public void setTradeId(Integer tradeId) {
        this.tradeId = tradeId;
    }

    public TradeStatus getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(TradeStatus tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return TradeUtil.toString(this);
    }


    public enum TradeStatus {
        NEW,
        VALIDATED,
        PROCESSED;
    }
}
