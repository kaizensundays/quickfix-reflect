package com.kaizensundays.fusion.quickfix.messages

import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created: Saturday 7/2/2022, 12:48 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class FixDictionaryTest {

    val dictionary = FixDictionary("FIX44.xml")

    @Before
    fun before() {
        dictionary.init()
    }

    @Test
    fun map() {

        assertEquals(916, dictionary.nameToFieldMap().size)

        assertEquals(916, dictionary.tagToFieldMap().size)
    }

    @Test
    fun hasComponent() {
        assertTrue(dictionary.hasComponent("instrumentLeg"))
    }

    @Test
    fun getField() {

        val data = listOf(35, 54, 60, 200)
            .zip(
                listOf(
                    "MsgType" to "STRING",
                    "Side" to "CHAR",
                    "TransactTime" to "UTCTIMESTAMP",
                    "MaturityMonthYear" to "MONTHYEAR",
                )
            )

        data.forEach { (tag, field) ->

            val f = dictionary.getField(tag)
            assertEquals(field.first, f?.name)
            assertEquals(field.second, f?.type)
        }

    }

/*
    fun message(msgType: String): Message {
        val msg = Message()
        msg.header.setString(MsgType.FIELD, msgType)
        return msg
    }

    @Test
    fun getGroupTags() {

        assertEquals(emptySet(), dictionary.getGroupTags(Message()).toSortedSet())
        assertEquals(emptySet(), dictionary.getGroupTags(message("")).toSortedSet())
        assertEquals(emptySet(), dictionary.getGroupTags(message("NoSuchMsgType")).toSortedSet())

        assertEquals(sortedSetOf(78, 386, 711), dictionary.getGroupTags(message(MsgType.ORDER_SINGLE)).toSortedSet())
        assertEquals(sortedSetOf(78, 386, 555, 711), dictionary.getGroupTags(message(MsgType.NEW_ORDER_MULTILEG)).toSortedSet())
        assertEquals(sortedSetOf(146), dictionary.getGroupTags(message(MsgType.QUOTE_REQUEST)).toSortedSet())
    }
*/

/*
    @Test
    fun javaType() {

        // ToDo: create map<field.name, setter>, check if field type is correct
        // TransactTime -> LocalDateTime -> Long
        // convertLocalDateTimeToLong(ldt: LocalDateTime)

        NewOrderSingle::class.java.declaredFields.forEach { field ->
            println(field.name)
            val className = "quickfix.field." + field.name.firstCharToUpper()
            println("className=$className")
            val type = Class.forName(className)

            val paramClass = type.constructors.filter { c -> c.parameterCount == 1 }
                .first().parameterTypes.first()

            println("paramClass=$paramClass")
        }

    }
*/

}