package com.kaizensundays.fusion.quickfix.messages

/**
 * Created: Saturday 6/25/2022, 12:33 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
interface ObjectConverter<T, O> {

    fun fromObject(obj: O): T

    fun toObject(msg: T): O

}