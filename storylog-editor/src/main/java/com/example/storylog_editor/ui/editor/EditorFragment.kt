package com.example.storylog_editor.ui.editor

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.storylog_editor.R
import com.example.storylog_editor.extension.toClass
import com.example.storylog_editor.model.EditerModel
import com.example.storylog_editor.util.ImageUtil
import com.example.storylog_editor.util.KeyboardHelper
import com.example.storylog_editor.view.SetAlignmentDialog
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class EditorFragment : Fragment(), EditorAdapter.OnChange, SetAlignmentDialog.OnClick {


    lateinit var mViewModel: EditorViewModel
    var adapter: EditorAdapter? = null
    var cursorPosition = 0
    var edtView: View? = null
    var mAlertDialog: AlertDialog? = null
    var timer = Timer()
    var timerIsRun = false

    val FICTIONLOG_IMAGE = "fictionlog_image"
    val REQUEST_SELECT_PICTURE = 10000
    val REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101
    var rvEditor: RecyclerView? = null
    var btnQuote: ImageView? = null
    var btnHeader: ImageView? = null
    var btnLine: ImageView? = null
    var btnAlighment: ImageView? = null
    var btnAddImage: ImageView? = null

    private var myClipboard: ClipboardManager? = null
    private var myClip: ClipData? = null

    override fun onPasteText(position: Int, textList: MutableList<String>) {
        var count = 0
        if (textList.size == 1) {
            mViewModel.updatePasteText(position, textList[0], true, null, false)
        } else {
            mViewModel.updatePasteText(position, textList[0], false, null, false)
            textList.forEachIndexed { index, text ->
                if (index > 0) {
                    count++
                    mViewModel.addView(
                        position + index,
                        mViewModel.getModel()[position].type,
                        text,
                        true
                    )
                }
            }
        }
        adapter?.let {
            //            it.upDateItemInsertRange(position, count + 1)
            it.notifyDataSetChanged()
            rvEditor?.layoutManager?.scrollToPosition(position + count + 1)
        }
    }

    override fun onNextLine(position: Int, text: CharSequence) {
        if (mViewModel.getModel()[position - 1].type == "unstyled"
            || mViewModel.getModel()[position - 1].type == "center"
            || mViewModel.getModel()[position - 1].type == "right" ||
            mViewModel.getModel()[position - 1].type == "indent"
        )
            mViewModel.addView(
                position, mViewModel.getModel()[position - 1].type, text,
                true
            )
        else
            mViewModel.addView(
                position, "unstyled", text,
                true
            )

        adapter?.let {
            it.upDateItem(position)
//            rvEditor?.post {
            rvEditor?.layoutManager?.scrollToPosition(position)
//            }
        }
    }

    override fun onPreviousLine(position: Int, text: CharSequence, selection: Int) {
        var viewType = mViewModel.getModel()[position - 1].type

        when (viewType) {
            "atomic:image" -> {
                mViewModel.updateFocus(position, false)
                mViewModel.showBorder(position - 1, true)
                adapter?.let {
                    it.updateCurrentItem(position)
                    it.updateCurrentItem(position - 1)
                }
                rvEditor?.post {
                    rvEditor?.scrollToPosition(position - 1)
                }
                KeyboardHelper.hideSoftKeyboard(activity)
            }
            "atomic:break" -> {
                mViewModel.removeViewAndKeepFocus(position - 1, position)
                adapter?.let {
                    it.upDateRemoveItemWithoutCurrentChange(position - 1)
                }
            }
            else -> {
                mViewModel.updateText(
                    position - 1,
                    text,
                    true,
                    selection,
                    true
                )

                mViewModel.removeViewAt(position)
                adapter?.let {
                    it.upDateRemoveItem(position)
                    rvEditor?.post {
                        rvEditor?.scrollToPosition(position - 1)
                    }
                }
            }
        }
    }

    override fun onCursorChange(
        position: Int,
        startPosition: Int,
        endPosition: Int,
        edt: AppCompatEditText
    ) {
    }

    override fun onDeleteRow(position: Int) {
        mViewModel.removeViewAt(position)
        adapter?.let {
            it.upDateRemoveItemWithoutCurrentChange(position)
        }
    }

    override fun onUpdateText(position: Int, text: CharSequence, updateStyle: Boolean) {
        mViewModel.updateText(position, text, false, null, updateStyle)
    }

    override fun updateCursorPosition(position: Int, view: View, imageIndex: MutableList<Int>) {
        edtView = null
        cursorPosition = position
        edtView = view
        mViewModel.goneBorder()

        context?.let {
            when {
                mViewModel.getModel()[position].type == "blockquote" -> {
                    btnQuote?.setColorFilter(
                        ContextCompat.getColor(it, R.color.colorOrange)
                        , PorterDuff.Mode.SRC_IN
                    )
                    btnHeader?.setColorFilter(
                        ContextCompat.getColor(it, R.color.grey_image)
                        , PorterDuff.Mode.SRC_IN
                    )
                }
                mViewModel.getModel()[position].type == "header-three" -> {
                    btnQuote?.setColorFilter(
                        ContextCompat.getColor(it, R.color.grey_image)
                        , PorterDuff.Mode.SRC_IN
                    )
                    btnHeader?.setColorFilter(
                        ContextCompat.getColor(it, R.color.colorOrange)
                        , PorterDuff.Mode.SRC_IN
                    )
                }
                else -> {
                    btnQuote?.setColorFilter(
                        ContextCompat.getColor(it, R.color.grey_image)
                        , PorterDuff.Mode.SRC_IN
                    )
                    btnHeader?.setColorFilter(
                        ContextCompat.getColor(it, R.color.grey_image)
                        , PorterDuff.Mode.SRC_IN
                    )
                }
            }
        }
        if (cursorPosition > 0) {
            if (mViewModel.getSize() < cursorPosition ||
                mViewModel.getModel()[cursorPosition].type != "atomic:image"
            ) {
                imageIndex.forEach {
                    adapter?.updateCurrentItem(it)
                }
            }
        }
    }

    override fun onUpdateBold() {
        mViewModel.updateStyle(cursorPosition, edtView as EditText)
    }

    override fun setShowBorderFalse(position: Int) {
        mViewModel.showBorder(position, false)
    }

    override fun clearFocus(position: Int) {
        mViewModel.clearFocus()
        if (cursorPosition > 0) {
            if (mViewModel.getSize() < cursorPosition ||
                mViewModel.getModel()[cursorPosition].type != "atomic:image"
            ) {
                adapter?.notifyItemChanged(cursorPosition)
                rvEditor?.post {
                    rvEditor?.scrollToPosition(position)
                }
            }
        }
    }

    override fun onClickAlignLeft() {
        editTextSetTextAlignLeft()
    }

    override fun onClickAlignCenter() {
        editTextSetTextAlignCenter()
    }

    override fun onClickAlignRight() {
        editTextSetTextAlignRight()
    }

    override fun onClickIndent() {
        editTextSetTextIndent()
    }

    private fun editTextSetTextIndent() {
        adapter?.let {
            if (edtView is EditText) {
                mViewModel.updateIndent(cursorPosition, (edtView as EditText).selectionEnd)
                it.updateCurrentItem(cursorPosition)
            }
        }
    }

    private fun editTextSetTextAlignLeft() {
        adapter?.let {
            if (edtView is EditText) {
                mViewModel.updateAlignLeft(cursorPosition, (edtView as EditText).selectionEnd)
                it.updateCurrentItem(cursorPosition)
            }
        }
    }

    private fun editTextSetTextAlignCenter() {
        adapter?.let {
            if (edtView is EditText) {
                mViewModel.updateAlignCenter(cursorPosition, (edtView as EditText).selectionEnd)
                it.updateCurrentItem(cursorPosition)
            }
        }
    }

    private fun editTextSetTextAlignRight() {
        adapter?.let {
            if (edtView is EditText) {
                mViewModel.updateAlignRight(cursorPosition, (edtView as EditText).selectionEnd)
                it.updateCurrentItem(cursorPosition)
            }
        }
    }


    companion object {
        fun newInstance(data: String): EditorFragment {
            val fragment = EditorFragment()
            val args = Bundle()
            args.putString("data", data)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): EditorFragment {
            val fragment = EditorFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.let {
            mViewModel = ViewModelProviders.of(it).get(EditorViewModel::class.java)
        }
        val view = inflater.inflate(R.layout.fragment_editer, container, false)
        rvEditor = view.findViewById(R.id.rvEditor)
        btnAddImage = view.findViewById(R.id.btnAddImage)
        btnAlighment = view.findViewById(R.id.btnAlighment)
        btnHeader = view.findViewById(R.id.btnHeader)
        btnLine = view.findViewById(R.id.btnLine)
        btnQuote = view.findViewById(R.id.btnQuote)
        initViewModel()
        return view
    }

    private fun initViewModel() {
        mViewModel.uploadImageSuccessLiveData.observe(this, androidx.lifecycle.Observer {
            adapter?.notifyItemChanged(it, false)
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        myClipboard =
            activity!!.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager?
        if (arguments != null && arguments!!.getString("data") != null) {
            var editerModel: MutableList<EditerModel> = mutableListOf()
            val json = JSONArray(arguments!!.getString("data"))
            for (i in 0 until json.length()) {
                editerModel.add((json.get(i) as JSONObject).toClass(EditerModel::class.java))
            }
            mViewModel.setModel(editerModel)
        } else {
            mViewModel.addView(0, "unstyled", "", true)
        }
        initView()
    }

    private fun initView() {
        adapter = EditorAdapter(context!!, activity!!, this, mViewModel.getModel())
        rvEditor?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvEditor?.adapter = adapter
        rvEditor?.itemAnimator = null

        btnAlighment?.setOnClickListener {
            fragmentManager?.let { fragmentManager ->
                try {
                    var fragmentImageViewerDialog = SetAlignmentDialog.Builder()
                        .build(
                            it,
                            mViewModel.getModel()[cursorPosition].type,
                            this
                        )

                    fragmentImageViewerDialog.retainInstance = true
                    fragmentImageViewerDialog.show(fragmentManager, "alignment")
                } catch (ex: Exception) {
                    var fragmentImageViewerDialog = SetAlignmentDialog.Builder()
                        .build(it, "unstyled", this)
                    fragmentImageViewerDialog.retainInstance = true
                    fragmentImageViewerDialog.show(fragmentManager, "alignment")
                }
            }
        }



        btnAddImage?.setOnClickListener {
            KeyboardHelper.hideSoftKeyboard(activity)
            pickFromGallery()
        }

        context?.let { context ->
            btnQuote?.setOnClickListener {
                if (cursorPosition <= mViewModel.getSize()) {
                    if (mViewModel.getModel()[cursorPosition].type == "unstyled") {
                        adapter?.let {
                            mViewModel.changeToQuote(cursorPosition)
                            it.updateCurrentItem(cursorPosition)
                        }

                        btnQuote?.setColorFilter(
                            ContextCompat.getColor(context, R.color.colorOrange)
                            , PorterDuff.Mode.SRC_IN
                        )
                    } else if (mViewModel.getModel()[cursorPosition].type == "blockquote") {
                        adapter?.let {
                            mViewModel.changeToEditText(cursorPosition)
                            it.updateCurrentItem(cursorPosition)
                        }

                        btnQuote?.setColorFilter(
                            ContextCompat.getColor(context, R.color.grey_image)
                            , PorterDuff.Mode.SRC_IN
                        )
                    } else {
                        adapter?.let {
                            mViewModel.changeToQuote(cursorPosition)
                            it.updateCurrentItem(cursorPosition)
                        }

                        btnQuote?.setColorFilter(
                            ContextCompat.getColor(context, R.color.colorOrange)
                            , PorterDuff.Mode.SRC_IN
                        )
                    }
                }
            }

            btnHeader?.setOnClickListener {
                if (cursorPosition <= mViewModel.getSize()) {
                    if (mViewModel.getModel()[cursorPosition].type == "unstyled") {
                        adapter?.let {
                            mViewModel.changeToHeader(cursorPosition)
                            it.updateCurrentItem(cursorPosition)
                            btnHeader?.setColorFilter(
                                ContextCompat.getColor(context, R.color.colorOrange)
                                , PorterDuff.Mode.SRC_IN
                            )
                        }
                    } else if (mViewModel.getModel()[cursorPosition].type == "header-three") {
                        adapter?.let {
                            mViewModel.changeToEditText(cursorPosition)
                            it.updateCurrentItem(cursorPosition)
                        }
                        btnHeader?.setColorFilter(
                            ContextCompat.getColor(context, R.color.grey_image)
                            , PorterDuff.Mode.SRC_IN
                        )
                    } else {
                        adapter?.let {
                            mViewModel.changeToHeader(cursorPosition)
                            it.updateCurrentItem(cursorPosition)
                            btnHeader?.setColorFilter(
                                ContextCompat.getColor(context, R.color.colorOrange)
                                , PorterDuff.Mode.SRC_IN
                            )
                        }
                    }
                }
            }
        }

        btnLine?.setOnClickListener {
            addLine()
        }

        var clipboard =
            activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        var clipData = clipboard.primaryClip
        var itemCount = clipData?.itemCount ?: 0
        if (itemCount > 0) {
            var item = clipData?.getItemAt(0)
            var text = item?.text.toString()
        }
    }


    private fun addLine() {
        adapter?.let {
            //            if (cursorPosition + 1 >= mViewModel.getSize()) {
            mViewModel.addLineWithEditText(cursorPosition + 1)
            it.upDateLineItemWithEditText(cursorPosition)
            rvEditor?.post {
                rvEditor?.scrollToPosition(cursorPosition + 2)
            }
//            } else {
//                mViewModel.addLine(cursorPosition + 1)
//                it.upDateLineItem(cursorPosition)
//            }
        }
    }


    private fun pickFromGallery() {
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                "Storage read permission is needed to pick files.",
                REQUEST_STORAGE_READ_ACCESS_PERMISSION
            )
        } else {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                REQUEST_SELECT_PICTURE
            )
        }
    }

    private fun requestPermission(permission: String, rationale: String, requestCode: Int) {
        if (shouldShowRequestPermissionRationale(permission)) {
            showPermissionAlertDialog("Permission needed", rationale,
                DialogInterface.OnClickListener { dialog, which ->
                    requestPermissions(arrayOf(permission), requestCode)
                }, "OK", DialogInterface.OnClickListener { dialog, which -> }, "Cancel"
            )
        } else {
            requestPermissions(arrayOf(permission), requestCode)
        }
    }

    private fun showPermissionAlertDialog(
        @Nullable title: String, @Nullable message: String,
        @Nullable onPositiveButtonClickListener: DialogInterface.OnClickListener,
        @NonNull positiveText: String,
        @Nullable onNegativeButtonClickListener: DialogInterface.OnClickListener,
        @NonNull negativeText: String
    ) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener)
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener)
        mAlertDialog = builder.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_SELECT_PICTURE) {
                val selectedUri = data?.data

                selectedUri?.let {
                    //                    var imageStream = activity!!.contentResolver.openInputStream(selectedUri)
                    var imageBase64 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source =
                            ImageDecoder.createSource(activity!!.contentResolver, selectedUri)
                        ImageUtil.bitmapToBase64(ImageDecoder.decodeBitmap(source))
                    } else {
                        ImageUtil.bitmapToBase64(
                            MediaStore.Images.Media.getBitmap(
                                activity!!.contentResolver,
                                selectedUri
                            )
                        )
                    }

                    addImageToModel(selectedUri)

                    mViewModel.uploadImageToServer(
                        mViewModel.getModel()[cursorPosition + 1].id,
                        imageBase64
                    )
                }
            }
        }
    }

    private fun addImageToModel(uri: Uri) {
        adapter?.let {
            mViewModel.addImageModel(cursorPosition + 1, uri)
            it.upDateImageItem(cursorPosition)
            rvEditor?.scrollToPosition(cursorPosition + 2)
        }
    }

    fun getPathFromURI(contentUri: Uri): String {
        var res = ""
//        var proj = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val proj = arrayOf(MediaStore.Images.Media._ID)
        var cursor = activity!!.contentResolver.query(contentUri, proj, null, null, null)
        cursor?.let {
            if (cursor.moveToFirst()) {
                var column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                res = cursor.getString(column_index)
            }
            cursor.close()
        }
        return res
    }

    override fun onStop() {
        KeyboardHelper.hideSoftKeyboard(activity)
        super.onStop()
    }

    override fun onDestroy() {
        KeyboardHelper.hideSoftKeyboard(activity)
        super.onDestroy()
    }

    fun hideKeyboard() {
        if (activity?.window != null) {
            val imm =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity?.window?.decorView?.windowToken, 0)
        }
    }
}