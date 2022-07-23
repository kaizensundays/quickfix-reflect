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

    public static class NoRelatedSym {
        public Integer quoteType;
        public Integer noLegs;
        public NoLegs[] noLegsGroup;

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

        public NoRelatedSym(Integer quoteType, NoLegs[] noLegsGroup) {
            this.quoteType = quoteType;
            this.noLegsGroup = noLegsGroup;
        }
    }

    public NoRelatedSym[] noRelatedSymGroup = {};

    public QuoteRequest() {
        super(MsgType.QUOTE_REQUEST);
    }

}
