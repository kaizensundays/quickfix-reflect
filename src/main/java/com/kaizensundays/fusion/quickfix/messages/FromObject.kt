package com.kaizensundays.fusion.quickfix.messages

import com.kaizensundays.fusion.quickfix.firstCharToUpper
import com.kaizensundays.fusion.quickfix.getValue
import quickfix.FieldMap
import quickfix.Message
import java.lang.reflect.Field
import java.math.BigDecimal

/**
 * Created: Saturday 7/30/2022, 12:55 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class FromObject(private val dictionary: FixDictionary) {

    private val setCharTag: SetTag = { tag, field, obj, _ ->
        obj.getValue(field) { value -> this.setChar(tag, value as Char) }
    }

    private val setStringTag: SetTag = { tag, field, obj, _ ->
        obj.getValue(field) { value -> this.setString(tag, value as String) }
    }

    private val setIntTag: SetTag = { tag, field, obj, _ ->
        obj.getValue(field) { value -> this.setInt(tag, value as Int) }
    }

    private val setLongTag: SetTag = { tag, field, obj, _ ->
        obj.getValue(field) { value -> this.setDecimal(tag, BigDecimal.valueOf(value as Long)) }
    }

    private val setDoubleTag: SetTag = { tag, field, obj, _ ->
        obj.getValue(field) { value -> this.setDouble(tag, value as Double) }
    }

    private val setTagByFieldNameMap: MutableMap<String, SetTag> = mutableMapOf()

    fun registerSetTagByFieldName(name: String, setTag: SetTag) {
        setTagByFieldNameMap[name] = setTag
    }

    private val setTagMap: Map<Class<*>, SetTag> = mapOf(
        Character::class.java to setCharTag,
        String::class.java to setStringTag,
        Integer::class.java to setIntTag,
        java.lang.Long::class.java to setLongTag,
        java.lang.Double::class.java to setDoubleTag,
    )

    private fun Field.isList() = this.type.equals(List::class.java)

    private fun listCopyTo(field: Field, obj: Any, target: FieldMap) {
        val list = field.get(obj)
        if (list is List<*>) {
            list.filterIsInstance<FixGroup>().forEach { fixGroup ->
                val group = fixGroup.create()
                if (!target.isSetField(group.fieldTag)) {
                    target.setInt(group.fieldTag, list.size)
                }
                val fields = fixGroup.javaClass.declaredFields
                fields.forEach { field ->
                    fieldCopyTo(field, fixGroup, group)
                }
                target.addGroup(group)
            }
        }
    }

    fun fieldCopyTo(field: Field, obj: Any, target: FieldMap) {
        val value = field.get(obj)
        if (value != null) {
            val tag = dictionary.tag(field.name)
            if (tag != null && !field.isList()) {
                var setTag = setTagByFieldNameMap[field.name.firstCharToUpper()]
                if (setTag != null) {
                    target.setTag(tag, field, obj, dictionary)
                } else {
                    setTag = setTagMap[field.type]
                    if (setTag != null) {
                        target.setTag(tag, field, obj, dictionary)
                    }
                }
            } else if (field.isList()) {
                listCopyTo(field, obj, target)
            }
        }
    }

    fun fromObject(obj: FixMessage): Message {
        val msg = Message()

        FixMessage::class.java.declaredFields.forEach { field ->
            fieldCopyTo(field, obj, msg.header)
        }

        obj.javaClass.declaredFields.forEach { field ->
            fieldCopyTo(field, obj, msg)
        }

        return msg
    }

}