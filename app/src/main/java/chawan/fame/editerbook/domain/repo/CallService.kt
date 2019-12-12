package chawan.fame.editerbook.domain.repo

import android.os.Handler
import android.os.Looper
import android.util.Log
import chawan.fame.editerbook.MyApolloMutationClient
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloCallback
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.storylog_editor.ui.editor.api.UploadImageMutation
import com.example.storylog_editor.ui.editor.api.type.UploadImageInput
import com.example.storylog_editor.ui.editor.api.type.UploadImageType
import org.jetbrains.annotations.NotNull

object CallService {
    interface GraphQLServiceCallBack {
        fun onServiceGraphQLSuccess(value: String, tag: String)
        fun onServiceGraphQLError(value: String, tag: String)
    }


    fun uploadImage(image: String, callback: GraphQLServiceCallBack) {
        val uiHandler = Handler(Looper.getMainLooper())

        var call = UploadImageMutation.builder().input(
            UploadImageInput.builder().image(image)
                .itemId("5d9b09c848a76d001a6e729f")
                .type(UploadImageType.BOOK).build()
        ).build()

        val dataCallback = ApolloCallback<UploadImageMutation.Data>(object :
            ApolloCall.Callback<UploadImageMutation.Data>() {
            override fun onResponse(@NotNull response: Response<UploadImageMutation.Data>) {
                if (response.errors().size == 0) {
                    response.data()?.let {
                        callback.onServiceGraphQLSuccess(it.uploadImage()!!, "UPLOAD")
                    }
                } else {
                    response.errors().let {
                        it.forEach {
                            callback.onServiceGraphQLError(it.message()!!, "UPLOAD")
                        }
                    }
                }
            }

            override fun onFailure(@NotNull e: ApolloException) {
                Log.e("onFailure", e.message.toString())
            }
        }, uiHandler)

        MyApolloMutationClient("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1OWRlZjQ5NmVhNjg4ZTJiNjAxNzhlMjUiLCJ0eXBlIjoiaW5kaXZpZHVhbCIsInBlcm1pc3Npb24iOm51bGwsImlhdCI6MTU2ODM1OTkyNn0.AokpyKeSppQjTiW2XFQfMN6RGBhg6WoTwLj-rFS_OyY").getMyApolloClient()
            .mutate(call).enqueue(dataCallback)
    }
}