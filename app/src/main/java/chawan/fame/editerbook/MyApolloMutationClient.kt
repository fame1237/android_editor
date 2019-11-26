package chawan.fame.editerbook

import android.net.ParseException
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.ResponseField
import com.apollographql.apollo.cache.normalized.CacheKey
import com.apollographql.apollo.cache.normalized.CacheKeyResolver
import com.apollographql.apollo.cache.normalized.sql.ApolloSqlHelper
import com.apollographql.apollo.cache.normalized.sql.SqlNormalizedCacheFactory
import com.apollographql.apollo.response.CustomTypeAdapter
import com.apollographql.apollo.response.CustomTypeValue
import com.example.storylog_editor.ui.editor.api.type.CustomType
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.jetbrains.annotations.NotNull
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by fame on 7/2/2018 AD.
 */

class MyApolloMutationClient(accessToken: String) {
    private var mApolloClient: ApolloClient

    var gson = GsonBuilder().setLenient().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale("TH"))
    var value = ""

    init {

        var httpClientBuilder = if (accessToken != "") {
            OkHttpClient.Builder()
                    .addNetworkInterceptor { chain ->
                        chain.proceed(chain.request().newBuilder().header("authorization", "JWT $accessToken").build())
                    }
        } else {
            OkHttpClient.Builder()
                    .addNetworkInterceptor { chain ->
                        chain.proceed(chain.request().newBuilder().header("authorization", "").build())
                    }
        }


        var dateCustomTypeAdapter = object : CustomTypeAdapter<Date> {
            override fun decode(value: CustomTypeValue<*>): Date {
                try {
                    return DATE_FORMAT.parse(value.value.toString())
                } catch (e: ParseException) {
                    throw RuntimeException(e)
                }

            }

            override fun encode(value: Date): CustomTypeValue<*> {
                return CustomTypeValue.GraphQLString(DATE_FORMAT.format(value))
            }
        }


        val apolloSqlHelper = ApolloSqlHelper(Contextor.getContext(), "fictionlog_cache_new")
        var cacheFactory = SqlNormalizedCacheFactory(apolloSqlHelper)

        val cacheKeyResolver = object : CacheKeyResolver() {
            @NotNull
            override fun fromFieldRecordSet(@NotNull field: ResponseField, @NotNull recordSet: Map<String, Any>): CacheKey {
                return formatCacheKey(recordSet["id"] as String)
            }

            @NotNull
            override fun fromFieldArguments(@NotNull field: ResponseField, @NotNull variables: Operation.Variables): CacheKey {
                return formatCacheKey(field.resolveArgument("id", variables) as String?)
            }

            private fun formatCacheKey(id: String?): CacheKey {
                return if (id == null || id.isEmpty()) {
                    CacheKey.NO_KEY
                } else {
                    CacheKey.from(id)
                }
            }
        }

        mApolloClient = ApolloClient.builder()
                .serverUrl("https://api2.k8s-dev.fictionlog.co/graphql/")
//                .addCustomTypeAdapter(CustomType.DATE, dateCustomTypeAdapter)
                .okHttpClient(httpClientBuilder.readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .connectTimeout(30, TimeUnit.SECONDS).build())
                .build()
    }

    fun getMyApolloClient(): ApolloClient {
        return mApolloClient
    }


}