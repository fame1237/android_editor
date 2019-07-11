package chawan.fame.editerbook.model.editor


class EditerModel {
    var viewType: EditerViewType? = null
    var data: Data? = null
    var mViewObject: Any? = null


    override fun toString(): String {
        return "EditerModel(viewType=$viewType, data=$data, mViewObject=$mViewObject)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EditerModel) return false

        if (viewType != other.viewType) return false
        if (data != other.data) return false
        if (mViewObject != other.mViewObject) return false

        return true
    }

    override fun hashCode(): Int {
        var result = viewType?.hashCode() ?: 0
        result = 31 * result + (data?.hashCode() ?: 0)
        result = 31 * result + (mViewObject?.hashCode() ?: 0)
        return result
    }
}

enum class EditerViewType {
    QUOTE, IMAGE, LINE, EDIT_TEXT
}