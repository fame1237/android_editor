package chawan.fame.editerbook.ui.rveditor

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import chawan.fame.editerbook.R
import com.example.storylog_editor.ui.editor.EditorAdapter
import com.example.storylog_editor.ui.editor.EditorViewModel
import com.example.storylog_editor.util.KeyboardHelper
import com.example.storylog_editor.util.SetStyle
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.library_editor_activity_editer.*
import java.io.File
import java.util.*

class EditorAcitivity : AppCompatActivity(), EditorAdapter.OnChange, com.example.storylog_editor.view.SetAlignmentDialog.OnClick {
    override fun onPasteText(position: Int, textList: MutableList<String>) {

    }

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

    private var myClipboard: ClipboardManager? = null
    private var myClip: ClipData? = null


    override fun onDeleteRow(position: Int) {
        mViewModel.removeViewAt(position)
        adapter?.let {
            it.upDateRemoveItemWithoutCurrentChange(position)
        }
    }

    override fun clearFocus(position: Int) {
        mViewModel.clearFocus()
        if (cursorPosition > 0) {
            if (mViewModel.getSize() < cursorPosition ||
                mViewModel.getModel()[cursorPosition].type != "atomic:image"
            ) {
                adapter?.notifyItemChanged(cursorPosition)
                rvEditor.post {
                    rvEditor.scrollToPosition(position)
                }
            }
        }
    }

    override fun setShowBorderFalse(position: Int) {
        mViewModel.showBorder(position, false)
    }

    override fun updateCursorPosition(position: Int, view: View, imageIndex: MutableList<Int>) {
        edtView = null
        cursorPosition = position
        edtView = view
        mViewModel.goneBorder()

        when {
            mViewModel.getModel()[position].type == "blockquote" -> {
                btnQuote.setColorFilter(
                    ContextCompat.getColor(this, R.color.colorOrange)
                    , PorterDuff.Mode.SRC_IN
                )
                btnHeader.setColorFilter(
                    ContextCompat.getColor(this, R.color.grey_image)
                    , PorterDuff.Mode.SRC_IN
                )
            }
            mViewModel.getModel()[position].type == "header-three" -> {
                btnQuote.setColorFilter(
                    ContextCompat.getColor(this, R.color.grey_image)
                    , PorterDuff.Mode.SRC_IN
                )
                btnHeader.setColorFilter(
                    ContextCompat.getColor(this, R.color.colorOrange)
                    , PorterDuff.Mode.SRC_IN
                )
            }
            else -> {
                btnQuote.setColorFilter(
                    ContextCompat.getColor(this, R.color.grey_image)
                    , PorterDuff.Mode.SRC_IN
                )
                btnHeader.setColorFilter(
                    ContextCompat.getColor(this, R.color.grey_image)
                    , PorterDuff.Mode.SRC_IN
                )
            }
        }

//        mViewModel.getModel()[position].data?.selection = (view as EditText).selectionEnd
//        mViewModel.updateFocus(position, true)
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

    override fun onNextLine(position: Int, text: CharSequence) {
//        Single.fromCallable {
//            mViewModel.addView(
//                position,
//                "unstyled",
//                text,
//                mViewModel.getModel()[position - 1].data!!.alight,
//                true
//            )
//        }.observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.computation())
//            .subscribe { _ ->
//                adapter?.let {
//                    it.upDateItem(position)
//                    rvEditor.post {
//                        rvEditor.scrollToPosition(position)
//                    }
//                }
//            }
    }

    override fun onCursorChange(
        position: Int,
        startPosition: Int,
        endPosition: Int,
        edt: AppCompatEditText
    ) {
//        Log.e("startPosition", startPosition.toString())
//        Log.e("endPosition", endPosition.toString())
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
                rvEditor.post {
                    rvEditor.scrollToPosition(position - 1)
                }
                KeyboardHelper.hideSoftKeyboard(this)
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
                    rvEditor.post {
                        rvEditor.scrollToPosition(position - 1)
                    }
                }
            }
        }
    }

    override fun onUpdateText(position: Int, text: CharSequence, updateStyle: Boolean) {
        mViewModel.updateText(position, text, false, null, updateStyle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.library_editor_fragment_editer)
//        mViewModel = ViewModelProviders.of(this).get(EditorViewModel::class.java)
//        EditorBookApplication.database!!.editerQuery().getContent(0)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe { model ->
//                if (model.isNotEmpty()) {
//                    var editerModel: MutableList<EditerModel> = mutableListOf()
//                    val json = JSONArray(model[0].content)
//                    for (i in 0 until json.length()) {
//                        editerModel.add((json.get(i) as JSONObject).toClass(EditerModel::class.java))
//                    }
//                    mViewModel.setModel(editerModel)
//                } else {
//                    mViewModel.addView(0, "unstyled", "", Alignment.START, true)
//                }
//                initView()
//                initViewModel()
//            }
//        myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
    }

    private fun initViewModel() {
//        mViewModel.editorModelLiveData.observe(this, Observer {
//            if (timerIsRun) {
//                timer.cancel()
//                timer = Timer()
//                timerIsRun = false
//                timer.schedule(object : TimerTask() {
//                    override fun run() {
//                        Single.fromCallable {
//                            EditorBookApplication.database!!.editerQuery()
//                                .insertContent(EditerTable(0, it.toJson()))
//                        }
//                            .subscribeOn(Schedulers.computation())
//                            .subscribe { _ ->
//                                Log.d("onDataChange", it.toJson())
//                            }
//
//                        initButton(it.toJson())
//                    }
//                }, 5000)
//            } else {
//                timerIsRun = true
//                timer.schedule(object : TimerTask() {
//                    override fun run() {
//                        Single.fromCallable {
//                            EditorBookApplication.database!!.editerQuery()
//                                .insertContent(EditerTable(0, it.toJson()))
//                        }
//                            .subscribeOn(Schedulers.computation())
//                            .subscribe { _ ->
//                                Log.d("onDataChange", it.toJson())
//                            }
//                        initButton(it.toJson())
//                    }
//                }, 5000)
//            }
//        })
    }

    private fun initButton(value: String) {
//        btnShowViewMode.setOnClickListener {
//            var intent = Intent(this, ReaderContentActivity::class.java)
//            intent.putExtra("value", value)
//            startActivity(intent)
//        }
    }

    private fun initView() {
        adapter = EditorAdapter(this, this,this, mViewModel.getModel())
        rvEditor.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvEditor.adapter = adapter
        rvEditor.itemAnimator = null

//        btnAlighment.setOnClickListener {
//            try {
//                var fragmentImageViewerDialog = com.example.storylog_editor.view.SetAlignmentDialog.Builder()
//                    .build(
//                        it,
//                        mViewModel.getModel()[cursorPosition].data!!.alight
//                    )
//
//                fragmentImageViewerDialog.retainInstance = true
//                fragmentImageViewerDialog.show(supportFragmentManager, "alignment")
//            } catch (ex: Exception) {
//                var fragmentImageViewerDialog = com.example.storylog_editor.view.SetAlignmentDialog.Builder()
//                    .build(it, null)
//                fragmentImageViewerDialog.retainInstance = true
//                fragmentImageViewerDialog.show(supportFragmentManager, "alignment")
//            }
//        }



        btnAddImage.setOnClickListener {
            KeyboardHelper.hideSoftKeyboard(this)
            pickFromGallery()
        }

        btnQuote.setOnClickListener {
            if (cursorPosition <= mViewModel.getSize()) {
                if (mViewModel.getModel()[cursorPosition].type == "unstyled") {
                    adapter?.let {
                        mViewModel.changeToQuote(cursorPosition)
                        it.updateCurrentItem(cursorPosition)
                    }

                    btnQuote.setColorFilter(
                        ContextCompat.getColor(this, R.color.colorOrange)
                        , PorterDuff.Mode.SRC_IN
                    )
                } else if (mViewModel.getModel()[cursorPosition].type == "blockquote") {
                    adapter?.let {
                        mViewModel.changeToEditText(cursorPosition)
                        it.updateCurrentItem(cursorPosition)
                    }

                    btnQuote.setColorFilter(
                        ContextCompat.getColor(this, R.color.grey_image)
                        , PorterDuff.Mode.SRC_IN
                    )
                } else {
                    adapter?.let {
                        mViewModel.changeToQuote(cursorPosition)
                        it.updateCurrentItem(cursorPosition)
                    }

                    btnQuote.setColorFilter(
                        ContextCompat.getColor(this, R.color.colorOrange)
                        , PorterDuff.Mode.SRC_IN
                    )
                }
            }
        }

        btnHeader.setOnClickListener {
            if (cursorPosition <= mViewModel.getSize()) {
                if (mViewModel.getModel()[cursorPosition].type == "unstyled") {
                    adapter?.let {
                        mViewModel.changeToHeader(cursorPosition)
                        it.updateCurrentItem(cursorPosition)
                        btnHeader.setColorFilter(
                            ContextCompat.getColor(this, R.color.colorOrange)
                            , PorterDuff.Mode.SRC_IN
                        )
                    }
                } else if (mViewModel.getModel()[cursorPosition].type == "header-three") {
                    adapter?.let {
                        mViewModel.changeToEditText(cursorPosition)
                        it.updateCurrentItem(cursorPosition)
                    }
                    btnHeader.setColorFilter(
                        ContextCompat.getColor(this, R.color.grey_image)
                        , PorterDuff.Mode.SRC_IN
                    )
                } else {
                    adapter?.let {
                        mViewModel.changeToHeader(cursorPosition)
                        it.updateCurrentItem(cursorPosition)
                        btnHeader.setColorFilter(
                            ContextCompat.getColor(this, R.color.colorOrange)
                            , PorterDuff.Mode.SRC_IN
                        )
                    }
                }
            }
        }

        btnLine.setOnClickListener {
            addLine()
        }

    }


    private fun setBold() {
        edtView?.let {
            if (edtView is EditText) {
                Log.e("selectionStart", (edtView as EditText).selectionStart.toString())
                Log.e("selectionEnd", (edtView as EditText).selectionEnd.toString())
                SetStyle.setBold(edtView as EditText)
                mViewModel.updateStyle(cursorPosition, edtView as EditText)
            }
        }
    }


    // on click copy button
    fun copyText(view: View) {
//        myClipboard?.primaryClip = myClip

        Toast.makeText(this, "Text Copied", Toast.LENGTH_SHORT).show()
    }

    // on click paste button
    fun pasteText(view: View) {
        val abc = myClipboard?.primaryClip
        val item = abc?.getItemAt(0)


        Toast.makeText(applicationContext, "Text Pasted", Toast.LENGTH_SHORT).show()
    }

    override fun onUpdateBold() {
        mViewModel.updateStyle(cursorPosition, edtView as EditText)
    }

    private fun setItalic() {
        edtView?.let {
            if (edtView is EditText) {
                Log.e("selectionStart", (edtView as EditText).selectionStart.toString())
                Log.e("selectionEnd", (edtView as EditText).selectionEnd.toString())
                SetStyle.setItalic(edtView as EditText)
                mViewModel.updateStyle(cursorPosition, edtView as EditText)
            }
        }
    }

    private fun setUnderLine() {
        edtView?.let {
            if (edtView is EditText) {
                Log.e("selectionStart", (edtView as EditText).selectionStart.toString())
                Log.e("selectionEnd", (edtView as EditText).selectionEnd.toString())
                SetStyle.setUnderLine(edtView as EditText)
                mViewModel.updateStyle(cursorPosition, edtView as EditText)
            }
        }
    }

    private fun setStrikethrough() {
        edtView?.let {
            if (edtView is EditText) {
                Log.e("selectionStart", (edtView as EditText).selectionStart.toString())
                Log.e("selectionEnd", (edtView as EditText).selectionEnd.toString())
                SetStyle.setStrikethrough(edtView as EditText)
                mViewModel.updateStyle(cursorPosition, edtView as EditText)
            }
        }
    }

    private fun addLine() {
        adapter?.let {
            //            if (cursorPosition + 1 >= mViewModel.getSize()) {
            mViewModel.addLineWithEditText(cursorPosition + 1)
            it.upDateLineItemWithEditText(cursorPosition)
            rvEditor.post {
                rvEditor.scrollToPosition(cursorPosition + 2)
            }
//            } else {
//                mViewModel.addLine(cursorPosition + 1)
//                it.upDateLineItem(cursorPosition)
//            }
        }
    }

    private fun addImageToModel(image: String) {
        adapter?.let {
//            mViewModel.addImageModel(cursorPosition + 1, image)
//            it.upDateImageItem(cursorPosition)
//            rvEditor.scrollToPosition(cursorPosition + 2)
        }
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

    private fun pickFromGallery() {
        if (ActivityCompat.checkSelfPermission(
                this,
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            showPermissionAlertDialog("Permission needed", rationale,
                DialogInterface.OnClickListener { dialog, which ->
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(permission), requestCode
                    )
                }, "OK", DialogInterface.OnClickListener { dialog, which -> }, "Cancel"
            )
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
    }

    private fun showPermissionAlertDialog(
        @Nullable title: String, @Nullable message: String,
        @Nullable onPositiveButtonClickListener: DialogInterface.OnClickListener,
        @NonNull positiveText: String,
        @Nullable onNegativeButtonClickListener: DialogInterface.OnClickListener,
        @NonNull negativeText: String
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener)
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener)
        mAlertDialog = builder.show()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode == RESULT_OK) {
//            if (requestCode == REQUEST_SELECT_PICTURE) {
//                val selectedUri = data?.data
//                selectedUri?.let {
//                    addImageToModel(selectedUri.toString())
//                }
////                if (selectedUri != null) {
////                    startCropActivity(data.data!!)
////                } else {
////                    Toast.makeText(this, "ไม่สามารถรับรูปนี่ได้", Toast.LENGTH_LONG).show()
////                }
//            }
////            else if (requestCode == UCrop.REQUEST_CROP) {
////                data?.let {
////                    handleCropResult(it)
////                }
////            }
//        }
////        if (resultCode == UCrop.RESULT_ERROR) {
////        }
//    }

    private fun startCropActivity(uri: Uri) {
        val destinationFileName = FICTIONLOG_IMAGE
        var uCrop = UCrop.of(uri, Uri.fromFile(File(cacheDir, "$destinationFileName.jpeg")))
        uCrop = basisConfig(uCrop)
        uCrop.start(this)
    }

    private fun basisConfig(uCrop: UCrop): UCrop {
        var uCrop = uCrop
        uCrop = uCrop.useSourceImageAspectRatio()
        return uCrop
    }

    private fun handleCropResult(result: Intent) {
        val resultUri = UCrop.getOutput(result)
        if (resultUri != null) {
//            addImageToModel(resultUri.encodedPath)
        } else {
        }
    }

    override fun onStop() {
        KeyboardHelper.hideSoftKeyboard(this)
        super.onStop()
    }

    override fun onDestroy() {
        KeyboardHelper.hideSoftKeyboard(this)
        super.onDestroy()
    }


    fun hideKeyboard() {
        if (this.window != null) {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.window.decorView.windowToken, 0)
        }
    }


}
