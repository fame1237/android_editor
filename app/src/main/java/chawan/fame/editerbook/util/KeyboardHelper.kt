package chawan.fame.editerbook.util

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.util.*

object KeyboardHelper {

    var hide = false
    private var timer: Timer? = null

    fun hideSoftKeyboard(context: Context?, view: View?) {
        if (context == null || view == null) {
            return
        }

        val imm = context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)

    }


    fun hideSoftKeyboardForced(context: Context?, view: View) {
        if (context == null) {


            return
        }

        val imm = context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromInputMethod(view.windowToken, 0)

    }

    fun hideSoftKeyboard(activity: Activity?) {
        if (activity == null) {
            return
        }
        val imm = activity
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
        hide = true
    }

    fun hideSoftKeyboard2(activity: Activity?) {
        if (activity == null) {
            return
        }
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                val imm = activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
            }
        }, 1000)
    }

    fun showSoftKeyboard(context: Context?, editText: EditText) {

        if (context == null) {
            return
        }

        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        editText.requestFocus()
    }

    fun showSoftKeyboardForcefully(context: Context?, editText: EditText) {
        if (timer != null) {
            timer!!.cancel()
        }
        if (hide) {
            hide = false
            return
        } else {
            if (context == null) {
                return
            }
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
        }
    }

}