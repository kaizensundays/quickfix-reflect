package com.kaizensundays.fusion.quickfix.messages

import org.junit.Test
import quickfix.field.BeginString
import quickfix.field.MsgType
import quickfix.field.QuoteType
import quickfix.field.SenderCompID
import quickfix.field.Side
import quickfix.field.TargetCompID
import kotlin.test.assertEquals

/**
 * Created: Saturday 7/30/2022, 12:58 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class ToObjectTest : GenericFixMessageConverterTestSupport() {

    val to = ToObject(dictionary)

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

    private fun setObjectFields(objs: Array<NewOrderSingle>) {

        objs.forEachIndexed { i, _ ->

            val msg = converter.fromObject(objs[i])

            val obj = NewOrderSingle()

            FixMessage::class.java.declaredFields.forEach { f ->
                to.set(msg.header, FixMessage::class.java, obj)
            }

            with(msg) {
                assertEquals(header.getString(BeginString.FIELD), obj.beginString)
                assertEquals(header.getString(MsgType.FIELD), obj.msgType)
                assertEquals(header.getString(SenderCompID.FIELD), obj.senderCompID)
                assertEquals(header.getString(TargetCompID.FIELD), obj.targetCompID)
            }
        }

    }

    private fun setObjectFields(objs: Array<QuoteRequest>) {

        objs.forEachIndexed { i, _ ->

            val msg = converter.fromObject(objs[i])

            val obj = QuoteRequest()

            FixMessage::class.java.declaredFields.forEach { f ->
                to.set(msg.header, FixMessage::class.java, obj)
            }

            with(msg) {
                assertEquals(header.getString(BeginString.FIELD), obj.beginString)
                assertEquals(header.getString(MsgType.FIELD), obj.msgType)
                assertEquals(header.getString(SenderCompID.FIELD), obj.senderCompID)
                assertEquals(header.getString(TargetCompID.FIELD), obj.targetCompID)
            }
        }
    }


    @Test
    fun setObjectFields() {
        setObjectFields(newOrderSingles)
        setObjectFields(quoteRequests)
    }

}