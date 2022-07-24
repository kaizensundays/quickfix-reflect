package com.kaizensundays.fusion.quickfix.messages;

import quickfix.field.MsgType;

/**
 * Created: Sunday 7/3/2022, 12:13 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
public class QuoteRequest extends FixMessage {

    public String quoteReqID;
    public String symbol;
    public Long transactTime;
    public NoRelatedSym[] noRelatedSym = {};

    public static class NoRelatedSym {
        public Integer quoteType;
        public NoLegs[] noLegs = {};

        public static class NoLegs {
            public String legSymbol;
            public Integer legProduct;

            public NoLegs() {
            }

            public NoLegs(String legSymbol, Integer legProduct) {
                this.legSymbol = legSymbol;
                this.legProduct = legProduct;
            }
        }

        public NoRelatedSym() {
        }

        public NoRelatedSym(Integer quoteType, NoLegs[] noLegs) {
            this.quoteType = quoteType;
            this.noLegs = noLegs;
        }
    }

    public QuoteRequest() {
        super(MsgType.QUOTE_REQUEST);
    }

}
