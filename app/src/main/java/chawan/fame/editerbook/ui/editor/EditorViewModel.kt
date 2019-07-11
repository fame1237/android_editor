package chawan.fame.editerbook.ui.editor

import androidx.lifecycle.ViewModel
import chawan.fame.editerbook.model.editor.Data
import chawan.fame.editerbook.model.editor.EditerModel
import chawan.fame.editerbook.model.editor.EditerViewType
import chawan.fame.editerbook.model.editor.TextStyle


class EditorViewModel : ViewModel() {
    var editerModel: MutableList<EditerModel> = mutableListOf()

    fun addView(position: Int,viewType: EditerViewType, view: Any) {
        var model = EditerModel()
        model.viewType = viewType
        model.mViewObject = view
        editerModel.add(position,model)
    }

    fun updateText(position: Int, text: String, style: TextStyle) {
        var model = editerModel.get(position)
        var data = Data()
        data.text = text
        data.style = style
        model.data = data
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

    fun getIndexOf(view: Any): Int {
        editerModel.forEachIndexed { index, editerModel ->
            if (editerModel.mViewObject == view) {
                return index
            }
        }
        return 0
    }
}