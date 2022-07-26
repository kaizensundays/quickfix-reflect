package com.kaizensundays.fusion.quickfix.messages

import org.junit.Before
import org.junit.Test
import quickfix.field.BeginString
import quickfix.field.LegProduct
import quickfix.field.LegSymbol
import quickfix.field.MaturityMonthYear
import quickfix.field.MsgType
import quickfix.field.NoLegs
import quickfix.field.NoRelatedSym
import quickfix.field.OrderQty
import quickfix.field.QuoteType
import quickfix.field.SenderCompID
import quickfix.field.Side
import quickfix.field.Symbol
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

    @Before
    override fun before() {
        super.before()
        val transactTimeConverter = TransactTimeConverter(dictionary)
        converter.register(transactTimeConverter)
        to.register(transactTimeConverter)
        to.register { QuoteRequest() }
    }

    @Test
    fun findFixGroups() {

        var map = to.findFixGroups(NewOrderSingle::class.java)

        assertEquals(0, map.size)
        assertEquals(sortedSetOf(), map.keys.toSortedSet())

        map = to.findFixGroups(QuoteRequest::class.java)

        assertEquals(2, map.size)
        assertEquals(
            sortedSetOf(
                "com.kaizensundays.fusion.quickfix.messages.QuoteRequest.NoRelatedSym",
                "com.kaizensundays.fusion.quickfix.messages.QuoteRequest.NoRelatedSym.NoLegs"
            ), map.keys.toSortedSet()
        )
    }

    private fun setObjectFields(objs: Array<NewOrderSingle>) {

        objs.forEachIndexed { i, _ ->

            val msg = converter.fromObject(objs[i])

            val obj = NewOrderSingle()

            to.set(msg.header, FixMessage::class.java, obj)
            to.set(msg, NewOrderSingle::class.java, obj)

            with(msg) {
                assertEquals(header.getString(BeginString.FIELD), obj.beginString)
                assertEquals(header.getString(MsgType.FIELD), obj.msgType)
                assertEquals(header.getString(SenderCompID.FIELD), obj.senderCompID)
                assertEquals(header.getString(TargetCompID.FIELD), obj.targetCompID)

                assertEquals(getChar(Side.FIELD), obj.side)
                assertEquals(getDouble(OrderQty.FIELD), obj.orderQty)
                assertEquals(getString(Symbol.FIELD), obj.symbol)
                assertEquals(getInt(MaturityMonthYear.FIELD), obj.maturityMonthYear)
                assertEquals(1656868263000, obj.transactTime)
            }
        }

    }

    private fun setObjectFields(objs: Array<QuoteRequest>) {

        objs.forEachIndexed { i, _ ->

            val msg = converter.fromObject(objs[i])

            val obj = QuoteRequest()

            to.set(msg.header, FixMessage::class.java, obj)

            to.set(msg, QuoteRequest::class.java, obj)

            with(msg) {
                assertEquals(header.getString(BeginString.FIELD), obj.beginString)
                assertEquals(header.getString(MsgType.FIELD), obj.msgType)
                assertEquals(header.getString(SenderCompID.FIELD), obj.senderCompID)
                assertEquals(header.getString(TargetCompID.FIELD), obj.targetCompID)

                assertEquals(getString(Symbol.FIELD), obj.symbol)
                assertEquals(1656868263000, obj.transactTime)

                assertEquals(getInt(NoRelatedSym.FIELD), obj.noRelatedSym.size)

                val noRelatedSymGroups = msg.getGroups(NoRelatedSym.FIELD)
                assertEquals(noRelatedSymGroups.size, obj.noRelatedSym.size)

                noRelatedSymGroups.zip(obj.noRelatedSym).forEach { (group, noRelatedSym) ->
                    assertEquals(group.getInt(QuoteType.FIELD), noRelatedSym.quoteType)
                    assertEquals(group.getInt(NoLegs.FIELD), noRelatedSym.noLegs.size)

                    val noLegsGroup = group.getGroups(NoLegs.FIELD)
                    assertEquals(noLegsGroup.size, noRelatedSym.noLegs.size)

                    noLegsGroup.zip(noRelatedSym.noLegs).forEach { (group, noLegs) ->
                        assertEquals(group.getString(LegSymbol.FIELD), noLegs.legSymbol)
                        assertEquals(group.getInt(LegProduct.FIELD), noLegs.legProduct)
                    }
                }
            }
        }
    }


    @Test
    fun setObjectFields() {
        setObjectFields(newOrderSingles)
        setObjectFields(quoteRequests)
    }

}