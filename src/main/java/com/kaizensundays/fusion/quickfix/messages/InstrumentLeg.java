package com.kaizensundays.fusion.quickfix.messages;

/**
 * Created: Sunday 7/3/2022, 12:33 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
public class InstrumentLeg {

    public String legSymbol;
    public Integer legProduct;

    public InstrumentLeg() {
    }

    public InstrumentLeg(String legSymbol, Integer legProduct) {
        this.legSymbol = legSymbol;
        this.legProduct = legProduct;
    }
}
