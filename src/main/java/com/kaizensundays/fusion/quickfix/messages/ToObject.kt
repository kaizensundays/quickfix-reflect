package com.kaizensundays.fusion.quickfix.messages

import com.kaizensundays.fusion.quickfix.firstCharToUpper
import quickfix.FieldMap
import quickfix.Message
import quickfix.field.MsgType
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.function.Supplier

/**
 * Created: Saturday 7/30/2022, 12:57 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class ToObject(private val dictionary: FixDictionary) {

    private val msgTypeToJavaTypeMap: MutableMap<String, Supplier<FixMessage>> = mutableMapOf()

    private val classNameToFixGroupMap: MutableMap<String, FixGroup> = mutableMapOf()

    private fun tag(fieldName: String): Int? {
        val field = dictionary.nameToFieldMap()[fieldName.firstCharToUpper()]
        return field?.number?.toInt()
    }

    fun fixType(tag: Int): String {
        val field = dictionary.tagToFieldMap()[tag]
        return if (field != null) field.type else "?"
    }

    private fun Field.isList() = this.type.equals(List::class.java)

    private fun Field.isFinal() = Modifier.isFinal(this.modifiers)

    private val setCharField: SetField = { field, tag, msg, _ ->
        if (msg.isSetField(tag)) {
            val value = msg.getChar(tag)
            if (!field.isFinal()) {
                field.set(this, value)
            }
        }
    }

    private val setStringField: SetField = { field, tag, msg, _ ->
        if (msg.isSetField(tag)) {
            val value = msg.getString(tag)
            if (!field.isFinal()) {
                field.set(this, value)
            }
        }
    }

    private val setIntField: SetField = { field, tag, msg, _ ->
        if (msg.isSetField(tag)) {
            val value = msg.getInt(tag)
            if (!field.isFinal()) {
                field.set(this, value)
            }
        }
    }

    private val setLongField: SetField = { field, tag, msg, _ ->
        if (msg.isSetField(tag) && !field.isFinal()) {
            val value = msg.getDecimal(tag)
            field.set(this, value.toLong())
        }
    }

    private val setFieldByFieldNameMap: MutableMap<String, SetField> = mutableMapOf()

    fun register(factory: Supplier<FixMessage>) {

        val msg = factory.get()

        msgTypeToJavaTypeMap[msg.msgType] = factory

        val map = findFixGroups(msg.javaClass)
        classNameToFixGroupMap.putAll(map)

    }

    private fun findFixGroups(type: Class<out Any>, map: MutableMap<String, FixGroup>): MutableMap<String, FixGroup> {

        return type.declaredClasses
            .filterIsInstance<Class<FixGroup>>()
            .fold(map) { m, c ->
                m[c.canonicalName] = c.newInstance()
                findFixGroups(c, m)
            }
    }

    fun findFixGroups(type: Class<out Any>): Map<String, FixGroup> {

        return findFixGroups(type, mutableMapOf())
    }


    fun register(converter: TagConverter) {
        setFieldByFieldNameMap[converter.getTagName()] = { tag, field, source, dictionary ->
            converter.setField(source, field, this, tag)
        }
    }

    private val setDoubleField: SetField = { field, tag, msg, _ ->
        if (msg.isSetField(tag)) {
            val value = msg.getDouble(tag)
            if (!field.isFinal()) {
                field.set(this, value)
            }
        }
    }

    private val setFieldMap: Map<Class<*>, SetField> = mapOf(
        Character::class.java to setCharField,
        String::class.java to setStringField,
        Integer::class.java to setIntField,
        java.lang.Long::class.java to setLongField,
        java.lang.Double::class.java to setDoubleField,
    )

    private fun Field.getGroups(obj: Any): MutableList<Any> {
        var list = this.get(obj)
        if (list == null) {
            if (this.isFinal()) {
                throw java.lang.IllegalStateException("Field " + this.name + " must not be final")
            }
            list = mutableListOf<Any>()
            this.set(obj, list)
        } else {
            if (list !is MutableList<*>) {
                throw java.lang.IllegalStateException("Field " + this.name + " must be MutableList")
            }
        }
        @Suppress("UNCHECKED_CAST")
        return list as MutableList<Any>
    }

    private fun setGroups(source: FieldMap, tag: Int, field: Field, target: Any) {
        if (source.isSetField(tag)) {
            val groups = source.getGroups(tag)
            groups.forEach { group ->
                val key = target.javaClass.canonicalName + "." + field.name.firstCharToUpper()
                val factory = classNameToFixGroupMap[key]
                if (factory != null) {
                    val groupBean = factory.createGroup()
                    set(group, groupBean.javaClass, groupBean)
                    val list = field.getGroups(target)
                    list.add(groupBean)
                }
            }
        }
    }

    private fun setField(source: FieldMap, tag: Int, field: Field, target: Any) {
        var setField = setFieldByFieldNameMap[field.name.firstCharToUpper()]
        if (setField != null) {
            target.setField(field, tag, source, dictionary)
        } else {
            setField = setFieldMap[field.type]
            if (setField != null) {
                target.setField(field, tag, source, dictionary)
            }
        }
    }

    private fun set(source: FieldMap, field: Field, target: Any) {

        val tag = tag(field.name)
        if (tag != null) {
            if (field.isList()) {
                setGroups(source, tag, field, target)
            } else {
                setField(source, tag, field, target)
            }
        }

    }

    fun set(source: FieldMap, type: Class<*>, target: Any) {
        type.declaredFields.forEach { field -> set(source, field, target) }
    }

    fun toObject(msg: Message): FixMessage {

        val msgType = msg.header.getString(MsgType.FIELD)

        val obj = msgTypeToJavaTypeMap[msgType]?.get() ?: throw IllegalStateException("Java class for msgType '$msgType' was not registered.")

        set(msg.header, FixMessage::class.java, obj)

        set(msg, obj.javaClass, obj)

        return obj
    }

}