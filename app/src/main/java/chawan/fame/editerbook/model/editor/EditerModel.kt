package chawan.fame.editerbook.model.editor


class EditerModel {
    var id: Long = 0
    var viewType: EditerViewType? = null
    var data: Data? = null
    var mViewObject: Any? = null
    var isFocus = false
    var showBorder = false

    override fun toString(): String {
        return "EditerModel(id=$id, viewType=$viewType, data=$data, mViewObject=$mViewObject, isFocus=$isFocus)"
    }
}

enum class EditerViewType {
    QUOTE, IMAGE, LINE, EDIT_TEXT, HEADER
}