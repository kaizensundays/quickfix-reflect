package com.kaizensundays.fusion.quickfix.messages;

import quickfix.field.MsgType;

/**
 * Created: Saturday 6/25/2022, 12:12 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
public class NewOrderSingle extends FixMessage {

    public Character side;
    public Double orderQty;
    public String symbol;
    public Long maturityMonthYear;
    public Long transactTime;

    public NewOrderSingle() {
        super(MsgType.ORDER_SINGLE);
    }

}
