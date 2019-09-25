package com.example.storylog_editor.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

class QuoteView : FrameLayout {

    lateinit var view: View


    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttr(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttr(attrs)
    }


    init {
        initInstance()
    }

    fun initAttr(attrs: AttributeSet) {
    }

    private fun initInstance() {
//        view = View.inflate(context, R.layout.submit_comment_view, this)
    }

}