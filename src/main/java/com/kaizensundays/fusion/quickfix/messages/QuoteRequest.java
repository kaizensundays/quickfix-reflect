package com.kaizensundays.fusion.quickfix.messages;

import org.jetbrains.annotations.NotNull;
import quickfix.Group;
import quickfix.field.MsgType;

import java.util.List;

/**
 * Created: Sunday 7/3/2022, 12:13 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
public class QuoteRequest extends FixMessage {

    public String quoteReqID;
    public String symbol;
    public Long transactTime;
    public List<NoRelatedSym> noRelatedSym;

    public static class NoRelatedSym implements FixGroup {
        @NotNull
        @Override
        public Group createQuickFixGroup() {
            return new quickfix.fix44.QuoteRequest.NoRelatedSym();
        }

        @NotNull
        @Override
        public FixGroup createGroup() {
            return new NoRelatedSym();
        }

        public Integer quoteType;
        public List<NoLegs> noLegs;

        public static class NoLegs implements FixGroup {
            @NotNull
            @Override
            public Group createQuickFixGroup() {
                return new quickfix.fix44.QuoteRequest.NoRelatedSym.NoLegs();
            }

            @NotNull
            @Override
            public FixGroup createGroup() {
                return new NoLegs();
            }

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

        public NoRelatedSym(Integer quoteType, List<NoLegs> noLegs) {
            this.quoteType = quoteType;
            this.noLegs = noLegs;
        }
    }

    public QuoteRequest() {
        super(MsgType.QUOTE_REQUEST);
    }

}
