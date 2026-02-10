package edu.kit.ifv.populationsynthesis.datasource.input

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

/**
 * The census data occasionally has some fields that are not numeric, or potentially missing.
 * The standard definitions are as follows:
 *      '-' translates to "rounded to zero"
 *      '/' translates to "omitted to protect personal data"
 *      'N/A' i haven't seen one of these in the data but still added it as a protective default
 *
 * We then register this module so that the parser knows how to translate these texts into a nullable
 * integer. in Jackson.kt file
 */
class LenientCensusIntDeserializer : JsonDeserializer<Int?>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Int? {
        val text = p.text.trim()
        // Treat empty, dash, or other non-numeric as null
        if (text.isEmpty() || text == "/" || text.equals("N/A", ignoreCase = true)) {
            return null
        }

        if(text  == "–" ) return 0
        return text.toIntOrNull()
    }
}