package chawan.fame.editerbook.extension

import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.widget.EditText

fun EditText.setSelectionChangedListener(onSelectionChangedListener: (editText: EditText, selectionStart: Int, selectionEnd: Int) -> Unit) {
    setAccessibilityDelegate(object : View.AccessibilityDelegate() {
        override fun sendAccessibilityEvent(host: View?, eventType: Int) {
            super.sendAccessibilityEvent(host, eventType)
            if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
                val editText = this@setSelectionChangedListener
                onSelectionChangedListener.invoke(editText, editText.selectionStart, editText.selectionEnd)
            }
        }
    })
}