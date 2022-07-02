package com.kaizensundays.fusion.quickfix.messages

import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

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

}