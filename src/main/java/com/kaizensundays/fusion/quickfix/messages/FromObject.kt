package com.kaizensundays.fusion.quickfix.messages

import quickfix.FieldMap
import quickfix.Message
import quickfix.field.converter.CharConverter
import quickfix.field.converter.DecimalConverter
import quickfix.field.converter.DoubleConverter
import quickfix.field.converter.IntConverter
import java.lang.reflect.Field
import java.math.BigDecimal

/**
 * Created: Saturday 7/30/2022, 12:55 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class FromObject(private val dictionary: FixDictionary) {

    private inline fun FieldMap.set(tag: Int, field: Field, source: Any, convert: (Any) -> String) {
        val value = field.get(source)
        if (value != null) {
            this.setString(tag, convert.invoke(value))
        }
    }

    private val setCharTag: SetTag = { tag, field, obj ->
        this.set(tag, field, obj) { value -> CharConverter.convert(value as Char) }
    }

    private val setStringTag: SetTag = { tag, field, obj ->
        this.set(tag, field, obj) { value -> value as String }
    }

    private val setIntTag: SetTag = { tag, field, obj ->
        this.set(tag, field, obj) { value -> IntConverter.convert(value as Int) }
    }

    private val setLongTag: SetTag = { tag, field, obj ->
        this.set(tag, field, obj) { value -> DecimalConverter.convert(BigDecimal.valueOf(value as Long)) }
    }

    private val setDoubleTag: SetTag = { tag, field, obj ->
        this.set(tag, field, obj) { value -> DoubleConverter.convert(value as Double) }
    }

    private val setTagMap: Map<Class<*>, SetTag> = mapOf(
        Character::class.java to setCharTag,
        String::class.java to setStringTag,
        Integer::class.java to setIntTag,
        java.lang.Long::class.java to setLongTag,
        java.lang.Double::class.java to setDoubleTag,
    )

    private val setTagByFieldNameMap: MutableMap<String, SetTag> = mutableMapOf()

    fun register(converter: TagConverter) {
        setTagByFieldNameMap[converter.getTagName()] = { tag, field, source ->
            converter.setTag(source, field, this, tag)
        }
    }

    private fun listCopyTo(field: Field, source: Any, target: FieldMap) {
        val list = field.get(source)
        if (list is List<*>) {
            list.filterIsInstance<FixGroup>().forEach { fixGroup ->
                val group = fixGroup.createQuickFixGroup()
                if (!target.isSetField(group.fieldTag)) {
                    target.setInt(group.fieldTag, list.size)
                }
                val fields = fixGroup.javaClass.declaredFields
                fields.forEach { field ->
                    set(field, fixGroup, group)
                }
                target.addGroup(group)
            }
        }
    }

    fun set(field: Field, source: Any, target: FieldMap) {
        val value = field.get(source)
        if (value != null) {
            val tag = dictionary.tag(field.name)
            if (tag != null && !field.isList()) {
                var setTag = setTagByFieldNameMap[field.name.firstCharToUpper()]
                if (setTag != null) {
                    target.setTag(tag, field, source)
                } else {
                    setTag = setTagMap[field.type]
                    if (setTag != null) {
                        target.setTag(tag, field, source)
                    }
                }
            } else if (field.isList()) {
                listCopyTo(field, source, target)
            }
        }
    }

    fun fromObject(obj: FixMessage): Message {

        val msg = Message()

        FixMessage::class.java.declaredFields.forEach { field ->
            set(field, obj, msg.header)
        }

        obj.javaClass.declaredFields.forEach { field ->
            set(field, obj, msg)
        }

        return msg
    }

}