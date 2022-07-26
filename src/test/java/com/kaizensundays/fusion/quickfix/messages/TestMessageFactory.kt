package com.kaizensundays.fusion.quickfix.messages

import quickfix.FixVersions
import java.time.LocalDateTime
import java.util.*

/**
 * Created: Saturday 6/25/2022, 12:35 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class TestMessageFactory(dictionary: FixDictionary) {

    private val ttc = TransactTimeConverter(dictionary)

    fun newOrderSingle(side: Char, orderQty: Double, symbol: String): NewOrderSingle {
        val obj = NewOrderSingle()
        obj.beginString = FixVersions.BEGINSTRING_FIX44
        obj.senderCompID = "IB"
        obj.targetCompID = "CBOE"

        obj.side = side
        obj.orderQty = orderQty
        obj.symbol = symbol
        obj.maturityMonthYear = 20220715
        obj.transactTime = ttc.toEpochMilli(LocalDateTime.of(2022, 7, 3, 17, 11, 3))
        return obj;
    }

    fun quoteRequest(symbol: String, noRelatedSym: List<QuoteRequest.NoRelatedSym>): QuoteRequest {
        val obj = QuoteRequest()
        obj.beginString = FixVersions.BEGINSTRING_FIX44
        obj.senderCompID = "IB"
        obj.targetCompID = "CBOE"

        obj.quoteReqID = UUID.randomUUID().toString()
        obj.symbol = symbol
        obj.noRelatedSym = noRelatedSym

        obj.transactTime = ttc.toEpochMilli(LocalDateTime.of(2022, 7, 3, 17, 11, 3))
        return obj;
    }

}