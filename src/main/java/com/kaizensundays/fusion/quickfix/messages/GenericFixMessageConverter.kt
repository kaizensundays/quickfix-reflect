package com.kaizensundays.fusion.quickfix.messages

import quickfix.FieldMap
import quickfix.Message
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.function.Supplier

/**
 * Created: Saturday 6/25/2022, 12:32 PM Eastern Time
 *
 * @author Sergey Chuykov
 */

typealias SetTag = FieldMap.(Int, Field, Any) -> Unit
typealias SetField = Any.(Field, Int, FieldMap) -> Unit

fun String.firstCharToUpper() = replaceFirstChar { c -> c.uppercase() }

fun Field.isList() = this.type.equals(List::class.java)

fun Field.isFinal() = Modifier.isFinal(this.modifiers)


class GenericFixMessageConverter(private val dictionary: FixDictionary) : ObjectConverter<Message, FixMessage> {

    private val fo = FromObject(dictionary)
    private val to = ToObject(dictionary)

    fun register(converter: TagConverter) {
        fo.register(converter)
        to.register(converter)
    }

    fun register(factory: Supplier<FixMessage>) {
        to.register(factory)
    }

    override fun fromObject(obj: FixMessage): Message {

        return fo.fromObject(obj)
    }

    override fun toObject(msg: Message): FixMessage {

        return to.toObject(msg)
    }

}