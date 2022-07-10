package com.kaizensundays.fusion.quickfix.messages;

import quickfix.field.MsgType;

/**
 * Created: Sunday 7/3/2022, 12:13 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
public class QuoteRequest extends FixMessage {

    public String quoteReqID;
    public Integer noRelatedSym;
    public String symbol;
    public Long transactTime;

    public InstrumentLeg[] instrumentLeg = {};

    public QuoteRequest() {
        super(MsgType.QUOTE_REQUEST);
    }

}
