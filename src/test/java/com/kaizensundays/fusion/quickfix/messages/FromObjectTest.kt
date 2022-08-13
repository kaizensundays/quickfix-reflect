package com.kaizensundays.fusion.quickfix.messages

import com.kaizensundays.fusion.quickfix.setTransactTimeTag
import org.junit.Before
import org.junit.Test
import quickfix.Message
import quickfix.field.LegProduct
import quickfix.field.LegSymbol
import quickfix.field.MaturityMonthYear
import quickfix.field.MsgType
import quickfix.field.NoLegs
import quickfix.field.NoRelatedSym
import quickfix.field.OrderQty
import quickfix.field.QuoteReqID
import quickfix.field.QuoteType
import quickfix.field.SenderCompID
import quickfix.field.Side
import quickfix.field.Symbol
import quickfix.field.TargetCompID
import quickfix.field.TransactTime
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

    @Before
    override fun before() {
        super.before()
        val transactTimeConverter = TransactTimeConverter(dictionary)
        //fo.registerTagSetter("TransactTime", setTransactTimeTag)
        fo.register(transactTimeConverter)
    }

    @Test
    fun fieldCopyToNewOrderSingle() {

        val obj = newOrderSingles[0]
        val msg = Message()

        FixMessage::class.java.declaredFields.forEach { f ->
            fo.fieldCopyTo(f, obj, msg)
        }

        assertEquals(MsgType.ORDER_SINGLE, msg.getString(MsgType.FIELD))
        assertEquals(obj.senderCompID, msg.getString(SenderCompID.FIELD))
        assertEquals(obj.targetCompID, msg.getString(TargetCompID.FIELD))

        NewOrderSingle::class.java.declaredFields.forEach { f ->
            fo.fieldCopyTo(f, obj, msg)
        }

        assertEquals(obj.side, msg.getChar(Side.FIELD))
        assertEquals(obj.orderQty, msg.getDouble(OrderQty.FIELD))
        assertEquals(obj.symbol, msg.getString(Symbol.FIELD))

        assertEquals(obj.maturityMonthYear.toInt(), msg.getInt(MaturityMonthYear.FIELD))
        assertEquals("2022-07-03T17:11:03", msg.getUtcTimeStamp(TransactTime.FIELD).toString())
    }

    @Test
    fun fieldCopyToQuoteRequest() {

        val obj = quoteRequests[0]
        val msg = Message()

        FixMessage::class.java.declaredFields.forEach { f ->
            fo.fieldCopyTo(f, obj, msg)
        }

        assertEquals(MsgType.QUOTE_REQUEST, msg.getString(MsgType.FIELD))
        assertEquals(obj.senderCompID, msg.getString(SenderCompID.FIELD))
        assertEquals(obj.targetCompID, msg.getString(TargetCompID.FIELD))

        QuoteRequest::class.java.declaredFields.forEach { f ->
            fo.fieldCopyTo(f, obj, msg)
        }

        assertEquals(obj.quoteReqID, msg.getString(QuoteReqID.FIELD))
        assertEquals(obj.symbol, msg.getString(Symbol.FIELD))
        assertEquals("2022-07-03T17:11:03", msg.getUtcTimeStamp(TransactTime.FIELD).toString())

        assertEquals(obj.noRelatedSym.size, msg.getInt(NoRelatedSym.FIELD))

        val noRelatedSymGroups = msg.getGroups(NoRelatedSym.FIELD)
        obj.noRelatedSym.zip(noRelatedSymGroups).forEach { (noRelatedSym, group) ->
            assertEquals(noRelatedSym.quoteType, group.getInt(QuoteType.FIELD))
            assertEquals(noRelatedSym.noLegs.size, group.getInt(NoLegs.FIELD))

            val noLegsGroups = group.getGroups(NoLegs.FIELD)
            assertEquals(noRelatedSym.noLegs.size, noLegsGroups.size)

            noRelatedSym.noLegs.zip(noLegsGroups).forEach { (noLegs, group) ->
                assertEquals(noLegs.legSymbol, group.getString(LegSymbol.FIELD))
                assertEquals(noLegs.legProduct, group.getInt(LegProduct.FIELD))
            }
        }

    }

}