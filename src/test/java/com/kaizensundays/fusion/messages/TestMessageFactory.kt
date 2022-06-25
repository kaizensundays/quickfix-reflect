package com.kaizensundays.fusion.messages

import com.kaizensundays.fusion.quickfix.messages.NewOrderSingle

/**
 * Created: Saturday 6/25/2022, 12:35 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class TestMessageFactory {

    fun newOrderSingle(side: Char, orderQty: Double, symbol: String): NewOrderSingle {
        val obj = NewOrderSingle()
        obj.senderCompID = "IB"
        obj.targetCompID = "CBOE"
        obj.side = side
        obj.orderQty = orderQty
        obj.symbol = symbol
        return obj;
    }

}