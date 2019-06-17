package studio.xmatrix.coffee.data.common.converter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class BitmapConverterFactory private constructor(): Converter.Factory() {

    companion object {
        fun create(): BitmapConverterFactory {
            return BitmapConverterFactory()
        }
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, Bitmap?>? {
        if (type == Bitmap::class.java) {
            return Converter { value -> BitmapFactory.decodeStream(value.byteStream()) }
        }
        return null
    }
}
