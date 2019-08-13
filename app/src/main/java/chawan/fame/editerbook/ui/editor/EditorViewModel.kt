package chawan.fame.editerbook.ui.editor

import android.util.Log
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import chawan.fame.editerbook.extension.SingleLiveEvent
import chawan.fame.editerbook.model.editor.Data
import chawan.fame.editerbook.model.editor.EditerModel
import chawan.fame.editerbook.model.editor.EditerViewType
import chawan.fame.editerbook.model.editor.TextStyle
import chawan.fame.editerbook.util.CheckStyle
import chawan.fame.editerbook.util.GenerateKey


class EditorViewModel : ViewModel() {
    var editerModel: MutableList<EditerModel> = mutableListOf()
    var editorModelLiveData = SingleLiveEvent<MutableList<EditerModel>>()

    fun addView(position: Int, viewType: EditerViewType, text: CharSequence, isFocus: Boolean = false) {
        val model = EditerModel()
        val data = Data()
        data.text = text.toString()
        data.style = TextStyle.NORMAL
        data.inlineStyleRange = CheckStyle.checkSpan(null, text)
        model.id = GenerateKey.getKey(editerModel)
        model.viewType = viewType
        model.isFocus = isFocus
        model.data = data
        data.selection = 0

        editerModel.forEach {
            it.isFocus = false
        }

        editerModel.add(position, model)
        editorModelLiveData.postValue(editerModel)
    }

    fun updateText(
        position: Int, text: CharSequence, style: TextStyle,
        isFocus: Boolean = false,
        updateStyle: Boolean
    ) {
        val model = editerModel[position]
        val data = model.data!!
        data.text = text.toString()
        if (updateStyle) {
            data.inlineStyleRange.clear()
            data.inlineStyleRange = CheckStyle.checkSpan(null, text)
        }
        data.style = style
        model.data = data
        data.selection = 0

        if (isFocus) {
            editerModel.forEach {
                it.isFocus = false
            }
            model.isFocus = isFocus
        }

        editorModelLiveData.postValue(editerModel)
    }

    fun updateFocus(position: Int, focus: Boolean) {
        editerModel.forEach {
            it.isFocus = false
        }
        val model = editerModel[position]
        model.isFocus = focus
    }

    fun showBorder(position: Int, isShow: Boolean) {
        editerModel.forEach {
            it.showBorder = false
        }
        val model = editerModel[position]
        model.showBorder = isShow
    }

    fun updateAlignLeft(position: Int, selection: Int) {
        val model = editerModel[position]
        val data = model.data!!
        data.selection = selection
        data.alight = Gravity.START
        model.data = data
        editorModelLiveData.postValue(editerModel)
    }

    fun updateAlignCenter(position: Int, selection: Int) {
        val model = editerModel[position]
        val data = model.data!!
        data.selection = selection
        data.alight = Gravity.CENTER
        model.data = data
        editorModelLiveData.postValue(editerModel)
    }

    fun updateAlignRight(position: Int, selection: Int) {
        val model = editerModel[position]
        val data = model.data!!
        data.selection = selection
        data.alight = Gravity.END
        model.data = data
        editorModelLiveData.postValue(editerModel)
    }

    fun updateStyle(position: Int, editText: EditText) {
        val model = editerModel[position]
        val data = model.data!!
        data.inlineStyleRange.clear()
        data.inlineStyleRange = CheckStyle.checkSpan(editText, "")
        editorModelLiveData.postValue(editerModel)
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
        editorModelLiveData.postValue(editerModel)
    }

    fun addLine(position: Int) {
        val model = EditerModel()
        val data = Data()
        data.text = ""
        model.viewType = EditerViewType.LINE
        model.id = GenerateKey.getKey(editerModel)
        model.data = data

        editerModel.add(position, model)
        editorModelLiveData.postValue(editerModel)
    }

    fun addLineWithEditText(position: Int) {
        val model = EditerModel()
        val data = Data()
        data.text = ""
        model.viewType = EditerViewType.LINE
        model.id = GenerateKey.getKey(editerModel)
        model.data = data

        editerModel.add(position, model)

        val model2 = EditerModel()
        val data2 = Data()
        data2.text = ""
        data2.style = TextStyle.NORMAL
        model2.viewType = EditerViewType.EDIT_TEXT
        model2.id = GenerateKey.getKey(editerModel)
        model2.data = data
        model2.isFocus = true

        editerModel.add(position + 1, model2)
        editorModelLiveData.postValue(editerModel)
    }

    fun changeToQuote(position: Int) {
        editerModel.forEach {
            it.isFocus = false
        }

        val model = editerModel[position]
        model.isFocus = true
        model.viewType = EditerViewType.QUOTE
        editorModelLiveData.postValue(editerModel)
    }

    fun changeToHeader(position: Int) {
        editerModel.forEach {
            it.isFocus = false
        }

        val model = editerModel[position]
        model.isFocus = true
        model.viewType = EditerViewType.HEADER
        editorModelLiveData.postValue(editerModel)
    }

    fun changeToEditText(position: Int) {
        editerModel.forEach {
            it.isFocus = false
        }

        val model = editerModel[position]
        model.isFocus = true
        model.viewType = EditerViewType.EDIT_TEXT
        editorModelLiveData.postValue(editerModel)
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