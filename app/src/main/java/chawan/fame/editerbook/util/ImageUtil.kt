package chawan.fame.editerbook.util

import android.graphics.Bitmap
import android.R.attr.bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream


object ImageUtil {

    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun decodeBase64(base64: String): ByteArray {
        return Base64.decode(base64, Base64.DEFAULT)
    }

}