package studio.xmatrix.coffee.data.common.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.objectbox.converter.PropertyConverter

class StringsConverter : PropertyConverter<List<String>, String> {

    val gson = Gson()

    override fun convertToDatabaseValue(entityProperty: List<String>?) = gson.toJson(entityProperty)

    override fun convertToEntityProperty(databaseValue: String?): List<String> {
        return gson.fromJson(databaseValue, object : TypeToken<List<String>>() {}.type)
    }
}
