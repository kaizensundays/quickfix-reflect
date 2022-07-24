package com.kaizensundays.fusion.quickfix.messages

import org.junit.Test
import quickfix.field.BeginString
import quickfix.field.LegProduct
import quickfix.field.LegSymbol
import quickfix.field.MsgType
import quickfix.field.NoLegs
import quickfix.field.NoRelatedSym
import quickfix.field.QuoteType
import quickfix.field.SenderCompID
import quickfix.field.Symbol
import quickfix.field.TargetCompID
import quickfix.field.TransactTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created: Sunday 7/3/2022, 12:20 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class QuoteRequestConverterTest : GenericFixMessageConverterTestSupport() {

    val objs = arrayOf(
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
    fun hasComponent() {
        assertTrue(dictionary.hasComponent("instrumentLeg"))
    }

    @Test
    fun fromObject() {

        objs.forEach { obj ->
            val msg = converter.fromObject(obj)

            with(msg) {
                assertEquals(obj.beginString, header.getString(BeginString.FIELD))
                assertEquals(obj.msgType, header.getString(MsgType.FIELD))
                assertEquals(obj.senderCompID, header.getString(SenderCompID.FIELD))
                assertEquals(obj.targetCompID, header.getString(TargetCompID.FIELD))

                assertEquals(obj.symbol, getString(Symbol.FIELD))

                assertEquals("2022-07-03T17:11:03", getUtcTimeStamp(TransactTime.FIELD).toString())

                assertEquals(obj.noRelatedSym.size, getInt(NoRelatedSym.FIELD))

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

    }

    @Test
    fun toObject() {

        objs.forEachIndexed { i, _ ->
            val msg = converter.fromObject(objs[i])

            val obj = converter.toObject(msg)

            assertTrue(obj is QuoteRequest)

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

}