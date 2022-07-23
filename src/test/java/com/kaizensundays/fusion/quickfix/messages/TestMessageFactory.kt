package com.kaizensundays.fusion.quickfix.messages

import com.kaizensundays.fusion.quickfix.toEpochMilli
import quickfix.FixVersions
import java.time.LocalDateTime
import java.util.*

/**
 * Created: Saturday 6/25/2022, 12:35 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class TestMessageFactory {

    fun newOrderSingle(side: Char, orderQty: Double, symbol: String): NewOrderSingle {
        val obj = NewOrderSingle()
        obj.beginString = FixVersions.BEGINSTRING_FIX44
        obj.senderCompID = "IB"
        obj.targetCompID = "CBOE"

        obj.side = side
        obj.orderQty = orderQty
        obj.symbol = symbol
        obj.maturityMonthYear = 20220715
        obj.transactTime = toEpochMilli(LocalDateTime.of(2022, 7, 3, 17, 11, 3))
        return obj;
    }

    fun quoteRequest(symbol: String, noRelatedSymGroup: Array<QuoteRequest.NoRelatedSym>, instrumentLeg: Array<InstrumentLeg> = emptyArray() ): QuoteRequest {
        val obj = QuoteRequest()
        obj.beginString = FixVersions.BEGINSTRING_FIX44
        obj.senderCompID = "IB"
        obj.targetCompID = "CBOE"

        obj.quoteReqID = UUID.randomUUID().toString()
        obj.symbol = symbol
        obj.noRelatedSym = noRelatedSymGroup.size
        obj.noRelatedSymGroup = noRelatedSymGroup
        //obj.instrumentLeg = instrumentLeg

        obj.transactTime = toEpochMilli(LocalDateTime.of(2022, 7, 3, 17, 11, 3))
        return obj;
    }

}