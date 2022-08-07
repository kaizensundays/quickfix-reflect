package com.kaizensundays.fusion.quickfix.messages

import org.junit.Before
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created: Sunday 7/3/2022, 12:21 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
abstract class GenericFixMessageConverterTestSupport {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    val factory = TestMessageFactory()

    val dictionary = FixDictionary("FIX44.xml")

    val converter = GenericFixMessageConverter(dictionary)

    @Before
    open fun before() {
        dictionary.init()
    }

}