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

typealias SetTag = FieldMap.(Int, Field, Any, FixDictionary) -> Unit
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

/*
    private val componentToGroupMap: Map<*, GroupFactory> = mapOf(
        QuoteRequest.NoRelatedSym::class.java to { quickfix.fix44.QuoteRequest.NoRelatedSym() },
        QuoteRequest.NoRelatedSym.NoLegs::class.java to { quickfix.fix44.QuoteRequest.NoRelatedSym.NoLegs() }
    )

    private val tagToGroupBeanMap: Map<*, GroupBeanFactory> = mapOf(
        NoRelatedSym.FIELD to { QuoteRequest.NoRelatedSym() },
        NoLegs.FIELD to { QuoteRequest.NoRelatedSym.NoLegs() },
    )
*/

/*
    private val groupFactory: (component: Any) -> Group? = { component ->
        componentToGroupMap[component.javaClass]?.invoke()
    }
*/

/*
    private inline fun Any.getValue(field: Field, set: (Any) -> Unit) {
        val value = field.get(this)
        if (value != null) {
            set(value)
        }
    }

    val setCharTag: SetTag = { tag, field, obj, _ ->
        obj.getValue(field) { value -> this.setChar(tag, value as Char) }
    }

    private val setStringTag: SetTag = { tag, field, obj, _ ->
        obj.getValue(field) { value -> this.setString(tag, value as String) }
    }

    private val setIntTag: SetTag = { tag, field, obj, _ ->
        obj.getValue(field) { value -> this.setInt(tag, value as Int) }
    }

    val setLongTag: SetTag = { tag, field, obj, _ ->
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

    val setDoubleTag: SetTag = { tag, field, obj, _ ->
        obj.getValue(field) { value -> this.setDouble(tag, value as Double) }
    }
*/

/*
    val setCharField: SetField = { field, tag, msg, _ ->
        if (msg.isSetField(tag)) {
            val value = msg.getChar(tag)
            if (!field.isFinal()) {
                field.set(this, value)
            }
        }
    }

    val setStringField: SetField = { field, tag, msg, _ ->
        if (msg.isSetField(tag)) {
            val value = msg.getString(tag)
            if (!field.isFinal()) {
                field.set(this, value)
            }
        }
    }

    val setIntField: SetField = { field, tag, msg, _ ->
        if (msg.isSetField(tag)) {
            val value = msg.getInt(tag)
            if (!field.isFinal()) {
                field.set(this, value)
            }
        }
    }

    val setLongField: SetField = { field, tag, msg, _ ->
        if (msg.isSetField(tag) && !field.isFinal()) {
            when (fixType(tag)) {
                "UTCTIMESTAMP" -> {
                    val value = toEpochMilli(msg.getUtcTimeStamp(tag))
                    field.set(this, value)
                }
                "MONTHYEAR" -> {
                    val value = msg.getInt(tag).toLong()
                    field.set(this, value)
                }
            }
        }
    }

    val setDoubleField: SetField = { field, tag, msg, _ ->
        if (msg.isSetField(tag)) {
            val value = msg.getDouble(tag)
            if (!field.isFinal()) {
                field.set(this, value)
            }
        }
    }
*/

/*
    val setTagMap: Map<Class<*>, SetTag> = mapOf(
        Character::class.java to setCharTag,
        String::class.java to setStringTag,
        Integer::class.java to setIntTag,
        java.lang.Long::class.java to setLongTag,
        java.lang.Double::class.java to setDoubleTag,
    )
*/

/*
    private val setFieldMap: Map<Class<*>, SetField> = mapOf(
        Character::class.java to setCharField,
        String::class.java to setStringField,
        Integer::class.java to setIntField,
        java.lang.Long::class.java to setLongField,
        java.lang.Double::class.java to setDoubleField,
    )
*/

/*
    private fun Field.isFinal() = Modifier.isFinal(this.modifiers)
*/

/*
    private fun Field.isList() = this.type.equals(List::class.java)

    private fun tag(fieldName: String): Int? {
        val field = dictionary.nameToFieldMap()[fieldName.firstCharToUpper()]
        return field?.number?.toInt()
    }
*/

/*
    fun fixType(tag: Int): String {
        val field = dictionary.tagToFieldMap()[tag]
        return if (field != null) field.type else "?"
    }
*/

/*
    fun FieldMap.set(field: Field, obj: Any) {

        val tag = tag(field.name)
        if (tag != null && !field.isList()) {

            val setTag = setTagMap[field.type]
            if (setTag != null) {
                this.setTag(tag, field, obj, dictionary)
            }

        } else if (field.isList()) {
            @Suppress("UNCHECKED_CAST")
            val array = field.get(obj) as MutableList<Any>
            array.forEach { component ->
                val group = groupFactory(component)
                if (group != null) {
                    if (!this.isSetField(group.fieldTag)) {
                        this.setInt(group.fieldTag, array.size)
                    }
                    group.setFields(component.javaClass, component)
                    this.addGroup(group)
                }
            }
        }
    }
*/

/*
    fun FieldMap.setFields(type: Class<*>, obj: Any) {

        val fieldMap = type.declaredFields.map { f -> f.name.firstCharToUpper() to f }.toMap()

        val names = fieldMap.map { entry -> entry.key }

        names.forEach { name ->

            val f = fieldMap[name]
            if (f != null) {
                this.set(f, obj)
            }
        }

    }
*/

    override fun fromObject(obj: FixMessage): Message {

        return fo.fromObject(obj)
/*
        val msg = Message()

        msg.header.setFields(FixMessage::class.java, obj)

        msg.setFields(obj.javaClass, obj)

        return msg
*/
    }

/*
    fun Field.getGroups(obj: Any): MutableList<Any> {
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
*/

/*
    fun FieldMap.getFields(type: Class<*>, obj: Any) {

        val fieldMap = type.declaredFields.map { f -> f.name.firstCharToUpper() to f }.toMap()

        val names = fieldMap.map { entry -> entry.key }

        names.forEach { name ->
            val field = fieldMap[name]
            if (field != null) {
                val tag = tag(field.name)
                if (tag != null && !field.isList()) {
                    val setField = setFieldMap[field.type]
                    if (setField != null) {
                        obj.setField(field, tag, this, dictionary)
                    }
                } else if (tag != null && field.isList()) {
                    if (isSetField(tag)) {
                        val groups = getGroups(tag)
                        groups.forEach { group ->
                            val factory = tagToGroupBeanMap[tag]
                            if (factory != null) {
                                val groupBean = factory.invoke()
                                group.getFields(groupBean.javaClass, groupBean)
                                val list = field.getGroups(obj)
                                list.add(groupBean)
                            }
                        }
                    }
                }
            }
        }
    }
*/

    override fun toObject(msg: Message): FixMessage {

        return to.toObject(msg)

/*
        val obj = msgTypeToJavaTypeMap[msg.header.getString(MsgType.FIELD)]?.invoke() ?: throw IllegalStateException()

        msg.header.getFields(FixMessage::class.java, obj)

        msg.getFields(obj.javaClass, obj)

        val groupTags = dictionary.getGroupTags(msg)

        groupTags.forEach { tag ->
            val groups = msg.getGroups(tag)
            if (groups != null && groups.isNotEmpty()) {
                println("")
            }
        }

        return obj
*/
    }

}