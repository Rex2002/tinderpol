package de.dhbw.tinderpol.data

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class CountriesDeserializer: JsonDeserializer<Any> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Any? {
        val res: Any?
        val str = json!!.asJsonPrimitive.asString
        res = try {
            str.toDouble()
        } catch (e: java.lang.Exception) {
            str
        }
        return res
    }
}