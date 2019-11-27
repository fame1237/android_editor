package com.example.storylog_editor.ui.editor

import android.net.Uri
import android.widget.EditText
import androidx.lifecycle.ViewModel
import com.example.storylog_editor.extension.SingleLiveEvent
import com.example.storylog_editor.extension.filterGetIndex
import com.example.storylog_editor.model.*
import com.example.storylog_editor.util.CheckStyle
import com.example.storylog_editor.util.GenerateKey

data class UpdateImageData(var keyId: String, var src: String)

data class UpdateImageSuccessData(var position: Int, var src: String)

class EditorViewModel : ViewModel() {

    var editerModel: ContentRawState = ContentRawState()
    var editorModelLiveData = SingleLiveEvent<ContentRawState>()

    var uploadImageToServerLiveData = SingleLiveEvent<UpdateImageData>()

    var uploadImageSuccessLiveData = SingleLiveEvent<Int>()

    fun uploadImageSuccess(keyId: String, src: String) {

        var index = editerModel.blocks.filterGetIndex {
            it.key == keyId
        }

        index?.let {
            editerModel.blocks[index].data?.src = src
            uploadImageSuccessLiveData.postValue(it)
        }
    }

    fun setModel(model: ContentRawState) {
        this.editerModel = model
    }

    fun addView(
        position: Int,
        viewType: String,
        text: CharSequence,
        isFocus: Boolean = false
    ) {
        editerModel.blocks.forEach {
            it.isFocus = false
        }

        val model = EditerModel()
        val data = Data()
        model.text = text.toString()
        model.inlineStyleRange = CheckStyle.checkSpan(null, text)
        model.key = GenerateKey.getStringKey(editerModel.blocks)
        model.type = viewType
        model.isFocus = isFocus
        model.data = data
        data.selection = text.length

        editerModel.blocks.add(position, model)
        editorModelLiveData.postValue(editerModel)
    }

    fun updateText(
        position: Int, text: CharSequence, isFocus: Boolean = false,
        selection: Int?,
        updateStyle: Boolean
    ) {
        val model = editerModel.blocks[position]
        val data = model.data!!
        model.text = text.toString()
        if (updateStyle) {
            model.inlineStyleRange.clear()
            model.inlineStyleRange = CheckStyle.checkSpan(null, text)
        }
        model.data = data
        selection?.let {
            data.selection = selection
        }

        if (isFocus) {
            editerModel.blocks.forEach {
                it.isFocus = false
            }
            model.isFocus = isFocus
        }

        editorModelLiveData.postValue(editerModel)
    }

    fun updatePasteText(
        position: Int, text: CharSequence, isFocus: Boolean = false,
        selection: Int?,
        updateStyle: Boolean
    ) {
        val model = editerModel.blocks[position]
        val data = model.data!!
        model.text = text.toString()
        if (updateStyle) {
            model.inlineStyleRange.clear()
            model.inlineStyleRange = CheckStyle.checkSpan(null, text)
        }
        model.data = data
        data.selection = text.length

        if (isFocus) {
            editerModel.blocks.forEach {
                it.isFocus = false
            }
            model.isFocus = isFocus
        }

        editorModelLiveData.postValue(editerModel)
    }

    fun clearFocus() {
        editerModel.blocks.forEach {
            it.isFocus = false
        }
    }

    fun updateFocus(position: Int, focus: Boolean) {
        editerModel.blocks.forEach {
            it.isFocus = false
        }
        val model = editerModel.blocks[position]
        model.isFocus = focus
    }

    fun goneBorder() {
        editerModel.blocks.forEach {
            it.showBorder = false
        }
    }

    fun showBorder(position: Int, isShow: Boolean) {
        editerModel.blocks.forEach {
            it.showBorder = false
        }
        val model = editerModel.blocks[position]
        model.showBorder = isShow
    }

    fun updateIndent(position: Int, selection: Int) {
        val model = editerModel.blocks[position]
        val data = model.data!!
        data.selection = selection
        model.data = data
        model.type = "indent"
        editerModel.blocks.forEach {
            it.isFocus = false
        }
        model.isFocus = true
        editorModelLiveData.postValue(editerModel)
    }

    fun updateAlignLeft(position: Int, selection: Int) {
        val model = editerModel.blocks[position]
        val data = model.data!!
        data.selection = selection
        model.data = data
        model.type = "unstyled"
        editerModel.blocks.forEach {
            it.isFocus = false
        }
        model.isFocus = true


        editorModelLiveData.postValue(editerModel)
    }

    fun updateAlignCenter(position: Int, selection: Int) {
        val model = editerModel.blocks[position]
        val data = model.data!!
        data.selection = selection
        model.data = data
        model.type = "center"
        editerModel.blocks.forEach {
            it.isFocus = false
        }
        model.isFocus = true
        editorModelLiveData.postValue(editerModel)
    }

    fun updateAlignRight(position: Int, selection: Int) {
        val model = editerModel.blocks[position]
        val data = model.data!!
        data.selection = selection
        model.type = "right"
        model.data = data
        editerModel.blocks.forEach {
            it.isFocus = false
        }
        model.isFocus = true
        editorModelLiveData.postValue(editerModel)
    }

    fun updateStyle(position: Int, editText: EditText) {
        val model = editerModel.blocks[position]
        val data = model.data!!
        model.inlineStyleRange.clear()
        model.inlineStyleRange = CheckStyle.checkSpan(editText, "")
        editorModelLiveData.postValue(editerModel)
    }

    fun addImageModel(position: Int, uri: Uri) {
        val model = EditerModel()
        val data = Data()
        model.text = ""
        data.uri = uri
        model.type = "atomic:image"
        model.key = GenerateKey.getStringKey(editerModel.blocks)
        model.data = data


        val model2 = EditerModel()
        val data2 = Data()
        model2.text = ""
        model2.type = "unstyled"
        model2.key = GenerateKey.getStringKey(editerModel.blocks)
        model2.data = data2
        model2.isFocus = true

        editerModel.blocks.forEach {
            it.isFocus = false
        }

        editerModel.blocks.add(position, model)
        editerModel.blocks.add(position + 1, model2)
        editorModelLiveData.postValue(editerModel)
    }

    fun addLine(position: Int) {
        val model = EditerModel()
        val data = Data()
        model.text = ""
        model.type = "atomic:break"
        model.key = GenerateKey.getStringKey(editerModel.blocks)
        model.data = data

        editerModel.blocks.add(position, model)
        editorModelLiveData.postValue(editerModel)
    }

    fun addLineWithEditText(position: Int) {
        val model = EditerModel()
        val data = Data()
        model.text = ""
        model.type = "atomic:break"
        model.key = GenerateKey.getStringKey(editerModel.blocks)
        model.data = data

        editerModel.blocks.add(position, model)

        val model2 = EditerModel()
        val data2 = Data()
        model2.text = ""
        model2.type = "unstyled"
        model2.key = GenerateKey.getStringKey(editerModel.blocks)
        model2.data = data2
        model2.isFocus = true

        editerModel.blocks.add(position + 1, model2)
        editorModelLiveData.postValue(editerModel)
    }

    fun changeToQuote(position: Int) {
        editerModel.blocks.forEach {
            it.isFocus = false
        }

        val model = editerModel.blocks[position]
        model.isFocus = true
        model.type = "blockquote"
        editorModelLiveData.postValue(editerModel)
    }

    fun changeToHeader(position: Int) {
        editerModel.blocks.forEach {
            it.isFocus = false
        }

        val model = editerModel.blocks[position]
        model.isFocus = true
        model.type = "header-three"
        editorModelLiveData.postValue(editerModel)
    }

    fun changeToEditText(position: Int) {
        editerModel.blocks.forEach {
            it.isFocus = false
        }

        val model = editerModel.blocks[position]
        model.isFocus = true
        model.type = "unstyled"
        editorModelLiveData.postValue(editerModel)
    }


    fun getModel(): MutableList<EditerModel> {
        return editerModel.blocks
    }

    fun removeViewAt(position: Int) {
        editerModel.blocks.removeAt(position)
        editorModelLiveData.postValue(editerModel)
    }

    fun removeViewAndKeepFocus(removePosition: Int, keepFocusPostion: Int) {
        val model = editerModel.blocks[keepFocusPostion]
        model.isFocus = true
        editerModel.blocks.removeAt(removePosition)
    }


    fun getSize(): Int {
        return editerModel.blocks.size
    }


    fun uploadImageToServer(keyID: String, base64: String) {
        uploadImageToServerLiveData.postValue(UpdateImageData(keyID, base64))
    }


    override fun toString(): String {
        return "EditorViewModel(editerModel=$editerModel)"
    }


}