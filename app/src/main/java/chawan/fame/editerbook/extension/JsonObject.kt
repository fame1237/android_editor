package chawan.fame.editerbook.extension

import com.google.gson.Gson
import org.json.JSONObject
import com.google.gson.GsonBuilder



/**
 * Created by fame on 7/2/2018 AD.
 */


fun <T : Any?> JSONObject.toClass(toClass: Class<T>): T {
     val gson = Gson()
    return gson.fromJson(this.toString(), toClass)
}

fun <T : Any?> String.toClass(toClass: Class<T>): T {
    val gson = Gson()
    return gson.fromJson(this, toClass)
}


fun <T: Any?> T.toJson(): String{
    val gsonBuilder = GsonBuilder()
    gsonBuilder.serializeNulls()
    val gson = gsonBuilder.create()
    return gson.toJson(this)
}