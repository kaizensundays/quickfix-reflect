package com.kaizensundays.fusion.quickfix.messages

import org.junit.Test
import quickfix.Message
import quickfix.field.MsgType
import quickfix.field.QuoteType
import quickfix.field.SenderCompID
import quickfix.field.Side
import quickfix.field.TargetCompID
import kotlin.test.assertEquals

/**
 * Created: Saturday 7/30/2022, 12:55 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class FromObjectTest : GenericFixMessageConverterTestSupport() {

    val fo = FromObject(dictionary)

    val newOrderSingles = arrayOf(
        factory.newOrderSingle(Side.BUY, 100.0, "ABNB"),
        factory.newOrderSingle(Side.SELL, 300.0, "AMZN"),
        factory.newOrderSingle(Side.BUY, 700.0, "UBER"),
    )

    val quoteRequests = arrayOf(
        factory.quoteRequest(
            "ABNB",
            listOf(
                QuoteRequest.NoRelatedSym(
                    QuoteType.INDICATIVE, listOf(
                        QuoteRequest.NoRelatedSym.NoLegs("ABNB.1", 1),
                        QuoteRequest.NoRelatedSym.NoLegs("ABNB.2", 3),
                    )
                )
            )
        ),
        factory.quoteRequest(
            "AMZN",
            listOf(
                QuoteRequest.NoRelatedSym(
                    QuoteType.INDICATIVE,
                    listOf(
                        QuoteRequest.NoRelatedSym.NoLegs("AMZN.1", 1),
                        QuoteRequest.NoRelatedSym.NoLegs("AMZN.2", 3),
                    )
                )
            )
        ),
        factory.quoteRequest(
            "UBER",
            listOf(
                QuoteRequest.NoRelatedSym(
                    QuoteType.INDICATIVE,
                    listOf(
                        QuoteRequest.NoRelatedSym.NoLegs("UBER.1", 1),
                        QuoteRequest.NoRelatedSym.NoLegs("UBER.2", 3),
                    )
                )
            )
        ),
    )

    @Test
    fun fieldCopyTo() {

        val msg = Message()

        FixMessage::class.java.declaredFields.forEach { f ->
            fo.fieldCopyTo(f, newOrderSingles[0], msg)
        }

        assertEquals(MsgType.ORDER_SINGLE, msg.getString(MsgType.FIELD))
        assertEquals("IB", msg.getString(SenderCompID.FIELD))
        assertEquals("CBOE", msg.getString(TargetCompID.FIELD))
    }

    @Test
    fun walkNewOrderSingle() {

        val names = mutableListOf<String>()

        fo.walk(newOrderSingles[0]) { field ->
            names.add(field.name)
        }

        assertEquals("[msgType, beginString, senderCompID, targetCompID, side, orderQty, symbol, maturityMonthYear, transactTime]", names.toString())
    }

    @Test
    fun walkQuoteRequest() {

        val names = mutableListOf<String>()

        fo.walk(quoteRequests[0]) { field ->
            names.add(field.name)
        }

        assertEquals("[msgType, beginString, senderCompID, targetCompID, quoteReqID, symbol, transactTime, quoteType, legSymbol, legProduct, legSymbol, legProduct]", names.toString())
    }

}