package chawan.fame.editerbook

import android.content.Context

object Contextor {

    private var mContext: Context? = null

    fun setContext(context: Context) {
        mContext = context
    }

    fun getContext(): Context? {
        return mContext
    }
    fun clear(){
        mContext = null
    }

}