package com.kaizensundays.fusion.quickfix.messages

/**
 * Created: Friday 8/19/2022, 8:26 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class Some(private val dictionary: FixDictionary) {

    fun fixType(tag: Int): String {
        val field = dictionary.tagToFieldMap()[tag]
        return if (field != null) field.type else "?"
    }

}