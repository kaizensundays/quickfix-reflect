package com.kaizensundays.fusion.quickfix.messages

import com.kaizensundays.fusion.quickfix.fix44.FieldType
import com.kaizensundays.fusion.quickfix.fix44.FixType
import org.springframework.core.io.ClassPathResource
import java.io.FileInputStream
import javax.xml.bind.JAXBContext
import javax.xml.transform.stream.StreamSource

/**
 * Created: Saturday 7/2/2022, 12:48 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class FixDictionary(private val path: String) {

    private var nameToFieldMap: Map<String, FieldType> = emptyMap()
    private var tagToFieldMap: Map<Int, FieldType> = emptyMap()

    fun init() {

        val resource = ClassPathResource("FIX44.xml")

        val context = JAXBContext.newInstance(FixType::class.java)

        val unmarshaller = context.createUnmarshaller()

        val source = StreamSource(FileInputStream(resource.file))

        val element = unmarshaller.unmarshal(source, FixType::class.java)

        val fix = element.value

        val fields = fix.fields.field

        nameToFieldMap = fields.map { field -> field.name to field }.toMap()

        tagToFieldMap = fields.map { field -> field.number.toInt() to field }.toMap()
    }

    fun nameToFieldMap(): Map<String, FieldType> {
        return nameToFieldMap;
    }

    fun tagToFieldMap(): Map<Int, FieldType> {
        return tagToFieldMap;
    }

}