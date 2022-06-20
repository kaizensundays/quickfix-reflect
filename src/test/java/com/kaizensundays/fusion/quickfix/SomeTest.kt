package com.kaizensundays.fusion.quickfix

import com.kaizensundays.fusion.quickfix.fix44.FixType
import org.junit.Test
import org.springframework.core.io.ClassPathResource
import java.io.FileInputStream
import javax.xml.bind.JAXBContext
import javax.xml.transform.stream.StreamSource
import kotlin.test.assertEquals

/**
 * Created: Monday 6/20/2022, 12:15 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class SomeTest {

    @Test
    fun unmarshalFix() {

        val resource = ClassPathResource("FIX44.xml")

        val context = JAXBContext.newInstance(FixType::class.java)

        val unmarshaller = context.createUnmarshaller()

        val source = StreamSource(FileInputStream(resource.file))

        val element = unmarshaller.unmarshal(source, FixType::class.java)

        val fix = element.value

        val fields = fix.fields.field

        assertEquals(916, fields.size)

        val fieldMap = fields.map { field -> field.number to field }.toMap()

        assertEquals("MsgType", fieldMap["35"]?.name)
        assertEquals("Currency", fieldMap["15"]?.name)
        assertEquals("OrderQty", fieldMap["38"]?.name)
        assertEquals("Side", fieldMap["54"]?.name)
        assertEquals("Symbol", fieldMap["55"]?.name)

    }

}