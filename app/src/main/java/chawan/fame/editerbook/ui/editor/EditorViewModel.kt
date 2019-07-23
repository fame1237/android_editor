package chawan.fame.editerbook.ui.editor

import android.util.Log
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import androidx.lifecycle.ViewModel
import chawan.fame.editerbook.model.editor.Data
import chawan.fame.editerbook.model.editor.EditerModel
import chawan.fame.editerbook.model.editor.EditerViewType
import chawan.fame.editerbook.model.editor.TextStyle
import chawan.fame.editerbook.util.GenerateKey


class EditorViewModel : ViewModel() {
    var editerModel: MutableList<EditerModel> = mutableListOf()

    fun addView(position: Int, viewType: EditerViewType, text: String, isFocus: Boolean = false) {
        val model = EditerModel()
        val data = Data()
        data.text = text
        data.style = TextStyle.NORMAL
        model.id = GenerateKey.getKey(editerModel)
        model.viewType = viewType
        model.isFocus = isFocus
        model.data = data

        editerModel.forEach {
            it.isFocus = false
        }

        editerModel.add(position, model)
    }

    fun updateText(position: Int, text: String, style: TextStyle, isFocus: Boolean = false) {

        val model = editerModel[position]
        val data = model.data!!
        data.text = text
        data.style = style
        model.data = data
        if (isFocus) {
            editerModel.forEach {
                it.isFocus = false
            }
            model.isFocus = isFocus
        }
        Log.d("Check", toString())
    }

    fun updateAlignLeft(position: Int) {
        val model = editerModel[position]
        val data = model.data!!
        data.alight = Gravity.START
        model.data = data
    }

    fun updateAlignCenter(position: Int) {
        val model = editerModel[position]
        val data = model.data!!
        data.alight = Gravity.CENTER
        model.data = data
    }

    fun updateAlignRight(position: Int) {
        val model = editerModel[position]
        val data = model.data!!
        data.alight = Gravity.END
        model.data = data
    }

    fun addImageModel(position: Int, src: String) {
        val model = EditerModel()
        val data = Data()
        data.text = ""
        data.src = src
        data.style = TextStyle.NORMAL
        model.viewType = EditerViewType.IMAGE
        model.id = GenerateKey.getKey(editerModel)
        model.data = data

        editerModel.forEach {
            it.isFocus = false
        }

        editerModel.add(position, model)

        val model2 = EditerModel()
        val data2 = Data()
        data2.text = ""
        data2.style = TextStyle.NORMAL
        model2.viewType = EditerViewType.EDIT_TEXT
        model2.isFocus = true
        model2.id = GenerateKey.getKey(editerModel)
        model2.data = data

        editerModel.forEach {
            it.isFocus = false
        }

        editerModel.add(position + 1, model2)
    }

    fun changeToQuote(position: Int) {
        editerModel.forEach {
            it.isFocus = false
        }

        val model = editerModel[position]
        model.isFocus = true
        model.viewType = EditerViewType.QUOTE
    }

    fun changeToEditText(position: Int) {
        editerModel.forEach {
            it.isFocus = false
        }

        val model = editerModel[position]
        model.isFocus = true
        model.viewType = EditerViewType.EDIT_TEXT
    }


    fun getModel(): MutableList<EditerModel> {
        return editerModel
    }

    fun removeViewAt(position: Int) {
        editerModel.removeAt(position)
    }

    fun getView(position: Int): Any? {
        return editerModel[position].mViewObject
    }

    fun getSize(): Int {
        return editerModel.size
    }

    fun getViewType(position: Int): EditerViewType {
        return editerModel[position].viewType!!
    }

    fun getText(position: Int): String {
        if (editerModel[position].mViewObject is EditText) {
            return (editerModel[position].mViewObject as EditText).text.toString()
        } else if (editerModel[position].viewType == EditerViewType.QUOTE && editerModel[position].mViewObject is LinearLayout) {
            return ((editerModel[position].mViewObject as LinearLayout).getChildAt(1) as EditText).text.toString()
        }
        return ""
    }

    fun getTextFromModel(position: Int): String {
        editerModel[position].data?.let {
            return it.text
        }
        return ""
    }

    fun getIndexOf(view: Any): Int {
        editerModel.forEachIndexed { index, editerModel ->
            if (editerModel.mViewObject == view) {
                return index
            }
        }
        return 0
    }

    override fun toString(): String {
        return "EditorViewModel(editerModel=$editerModel)"
    }


}