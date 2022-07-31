package com.kaizensundays.fusion.quickfix.messages

import com.kaizensundays.fusion.quickfix.toLocalDateTime
import quickfix.FieldMap
import quickfix.Group
import quickfix.Message
import java.lang.reflect.Field


/**
 * Created: Saturday 7/30/2022, 12:55 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class FromObject(private val dictionary: FixDictionary) {

    private inline fun Any.getValue(field: Field, set: (Any) -> Unit) {
        val value = field.get(this)
        if (value != null) {
            set(value)
        }
    }

    private val setCharTag: SetTag = { tag, field, obj ->
        obj.getValue(field) { value -> this.setChar(tag, value as Char) }
    }

    private val setStringTag: SetTag = { tag, field, obj ->
        obj.getValue(field) { value -> this.setString(tag, value as String) }
    }

    private val setIntTag: SetTag = { tag, field, obj ->
        obj.getValue(field) { value -> this.setInt(tag, value as Int) }
    }

    fun fixType(tag: Int): String {
        val field = dictionary.tagToFieldMap()[tag]
        return if (field != null) field.type else "?"
    }

    val setLongTag: SetTag = { tag, field, obj ->
        obj.getValue(field) { value ->
            when (fixType(tag)) {
                "UTCTIMESTAMP" -> {
                    val timestamp = value as Long
                    this.setUtcTimeStamp(tag, toLocalDateTime(timestamp))
                }
                "MONTHYEAR" -> {
                    this.setInt(tag, (value as Long).toInt())
                }
                "INT" -> {
                    this.setInt(tag, (value as Long).toInt())
                }
            }
        }
    }

    val setDoubleTag: SetTag = { tag, field, obj ->
        obj.getValue(field) { value -> this.setDouble(tag, value as Double) }
    }

    private val setTagMap: Map<Class<*>, FieldMap.(tag: Int, field: Field, obj: Any) -> Unit> = mapOf(
        Character::class.java to setCharTag,
        String::class.java to setStringTag,
        Integer::class.java to setIntTag,
        java.lang.Long::class.java to setLongTag,
        java.lang.Double::class.java to setDoubleTag,
    )

    private fun Field.isList() = this.type.equals(List::class.java)

    private val componentToGroupMap: Map<*, GroupFactory> = mapOf(
        QuoteRequest.NoRelatedSym::class.java to { quickfix.fix44.QuoteRequest.NoRelatedSym() },
        QuoteRequest.NoRelatedSym.NoLegs::class.java to { quickfix.fix44.QuoteRequest.NoRelatedSym.NoLegs() }
    )

    private val groupFactory: (component: Any) -> Group? = { component ->
        componentToGroupMap[component.javaClass]?.invoke()
    }

    private fun listCopyTo(field: Field, obj: Any, target: FieldMap) {
        val list = field.get(obj) as MutableList<Any>
        list.forEach { component ->
            val group = groupFactory(component)
            if (group != null) {
                if (!target.isSetField(group.fieldTag)) {
                    target.setInt(group.fieldTag, list.size)
                }
                //group.setFields(component.javaClass, component)
                target.addGroup(group)
            }
        }
    }

    fun fieldCopyTo(field: Field, obj: Any, target: FieldMap) {
        val value = field.get(obj)
        if (value != null) {
            val tag = dictionary.tag(field.name)
            if (tag != null && !field.isList()) {
                val setTag = setTagMap[field.type]
                if (setTag != null) {
                    target.setTag(tag, field, obj)
                }
            } else if (field.isList()) {
                listCopyTo(field, obj, target)
            }
        }
    }

    private fun walkObj(type: Class<*>, obj: Any, action: (field: Field) -> Unit) {

        val fields = type.declaredFields

        fields.forEach { f ->
            if (!f.isList()) {
                action.invoke(f)
            } else {
                val list = f.get(obj) as MutableList<Any>
                list.forEach { e ->
                    walkObj(e.javaClass, e, action)
                }
            }
        }

    }

    fun walk(obj: FixMessage, fieldAction: (field: Field) -> Unit) {

        walkObj(FixMessage::class.java, obj, fieldAction)

        walkObj(obj.javaClass, obj, fieldAction)

    }

    fun fromObject(obj: FixMessage): Message {
        val msg = Message()

        return msg
    }

}