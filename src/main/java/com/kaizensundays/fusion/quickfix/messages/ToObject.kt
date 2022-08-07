package com.kaizensundays.fusion.quickfix.messages

import quickfix.Message

/**
 * Created: Saturday 7/30/2022, 12:57 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class ToObject(private val dictionary: FixDictionary) {

    fun toObject(msg: Message): FixMessage {
        return QuoteRequest()
    }

}