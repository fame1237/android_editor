package com.example.storylog_editor.ui.editor

import android.widget.EditText
import androidx.lifecycle.ViewModel
import com.example.storylog_editor.extension.SingleLiveEvent
import com.example.storylog_editor.model.*
import com.example.storylog_editor.util.CheckStyle
import com.example.storylog_editor.util.GenerateKey

class EditorViewModel : ViewModel() {

    var editerModel: MutableList<EditerModel> = mutableListOf()
    var editorModelLiveData = SingleLiveEvent<MutableList<EditerModel>>()

    fun setModel(model: MutableList<EditerModel>) {
        this.editerModel = model
    }

    fun addView(
        position: Int,
        viewType: EditerViewType,
        text: CharSequence,
        align: Alignment,
        isFocus: Boolean = false
    ) {
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
        data.alight = align

        editerModel.forEach {
            it.isFocus = false
        }

        editerModel.add(position, model)
        editorModelLiveData.postValue(editerModel)
    }

    fun updateText(
        position: Int, text: CharSequence, style: TextStyle,
        isFocus: Boolean = false,
        selection: Int?,
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
        selection?.let {
            data.selection = selection
        }

        if (isFocus) {
            editerModel.forEach {
                it.isFocus = false
            }
            model.isFocus = isFocus
        }

        editorModelLiveData.postValue(editerModel)
    }

    fun clearFocus() {
        editerModel.forEach {
            it.isFocus = false
        }
    }

    fun updateFocus(position: Int, focus: Boolean) {
        editerModel.forEach {
            it.isFocus = false
        }
        val model = editerModel[position]
        model.isFocus = focus
    }

    fun goneBorder() {
        editerModel.forEach {
            it.showBorder = false
        }
    }

    fun showBorder(position: Int, isShow: Boolean) {
        editerModel.forEach {
            it.showBorder = false
        }
        val model = editerModel[position]
        model.showBorder = isShow
    }

    fun updateIndent(position: Int, selection: Int) {
        val model = editerModel[position]
        val data = model.data!!
        data.selection = selection
        model.data = data
        data.alight = Alignment.INDENT
        editerModel.forEach {
            it.isFocus = false
        }
        model.isFocus = true
        editorModelLiveData.postValue(editerModel)
    }

    fun updateAlignLeft(position: Int, selection: Int) {
        val model = editerModel[position]
        val data = model.data!!
        data.selection = selection
        data.alight = Alignment.START
        model.data = data
        editerModel.forEach {
            it.isFocus = false
        }
        model.isFocus = true


        editorModelLiveData.postValue(editerModel)
    }

    fun updateAlignCenter(position: Int, selection: Int) {
        val model = editerModel[position]
        val data = model.data!!
        data.selection = selection
        data.alight = Alignment.CENTER
        model.data = data
        editerModel.forEach {
            it.isFocus = false
        }
        model.isFocus = true
        editorModelLiveData.postValue(editerModel)
    }

    fun updateAlignRight(position: Int, selection: Int) {
        val model = editerModel[position]
        val data = model.data!!
        data.selection = selection
        data.alight = Alignment.END
        model.data = data
        editerModel.forEach {
            it.isFocus = false
        }
        model.isFocus = true
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


        val model2 = EditerModel()
        val data2 = Data()
        data2.text = ""
        data2.style = TextStyle.NORMAL
        model2.viewType = EditerViewType.EDIT_TEXT
        model2.id = GenerateKey.getKey(editerModel)
        model2.data = data2
        model2.isFocus = true

        editerModel.forEach {
            it.isFocus = false
        }

        editerModel.add(position, model)
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
        model2.data = data2
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
        editorModelLiveData.postValue(editerModel)
    }

    fun removeViewAndKeepFocus(removePosition: Int, keepFocusPostion: Int) {
        val model = editerModel[keepFocusPostion]
        model.isFocus = true
        editerModel.removeAt(removePosition)
    }



    fun getSize(): Int {
        return editerModel.size
    }


    override fun toString(): String {
        return "EditorViewModel(editerModel=$editerModel)"
    }


}