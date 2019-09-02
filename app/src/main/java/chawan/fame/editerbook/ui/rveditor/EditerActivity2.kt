package chawan.fame.editerbook.ui.rveditor

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import chawan.fame.editerbook.EditorBookApplication
import chawan.fame.editerbook.R
import chawan.fame.editerbook.extension.filterGetIndex
import chawan.fame.editerbook.extension.toClass
import chawan.fame.editerbook.extension.toJson
import chawan.fame.editerbook.model.editor.EditerModel
import chawan.fame.editerbook.model.editor.EditerViewType
import chawan.fame.editerbook.model.editor.TextStyle
import chawan.fame.editerbook.ui.editor.EditerAdapter
import chawan.fame.editerbook.ui.editor.EditorViewModel
import chawan.fame.editerbook.ui.reader.ReaderContentActivity
import chawan.fame.editerbook.util.KeyboardHelper
import chawan.fame.editerbook.util.SetStyle
import chawan.fame.editerbook.view.SetAlignmentDialog
import co.fictionlog.fictionlog.data.local.database.table.EditerTable
import com.yalantis.ucrop.UCrop
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_editer2.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.lang.Exception
import java.util.*

class EditerActivity2 : AppCompatActivity(), EditerAdapter.OnChange, SetAlignmentDialog.OnClick {


    lateinit var mViewModel: EditorViewModel
    var adapter: EditerAdapter? = null
    var cursorPosition = 0
    var edtView: View? = null
    var mAlertDialog: AlertDialog? = null
    var timer = Timer()
    var timerIsRun = false

    val FICTIONLOG_IMAGE = "fictionlog_image"
    val REQUEST_SELECT_PICTURE = 10000
    val REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101

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
                mViewModel.getModel()[cursorPosition].viewType != EditerViewType.IMAGE
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
            mViewModel.getModel()[position].viewType == EditerViewType.QUOTE -> {
                btnQuote.setColorFilter(
                    ContextCompat.getColor(this, R.color.colorOrange)
                    , PorterDuff.Mode.SRC_IN
                )
                btnHeader.setColorFilter(
                    ContextCompat.getColor(this, R.color.grey_image)
                    , PorterDuff.Mode.SRC_IN
                )
            }
            mViewModel.getModel()[position].viewType == EditerViewType.HEADER -> {
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
                mViewModel.getModel()[cursorPosition].viewType != EditerViewType.IMAGE
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
        Single.fromCallable {
            mViewModel.addView(
                position,
                EditerViewType.EDIT_TEXT,
                text,
                true
            )
        }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.computation())
            .subscribe { _ ->
                adapter?.let {
                    it.upDateItem(position)
                    rvEditor.post {
                        rvEditor.scrollToPosition(position)
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
//        Log.e("startPosition", startPosition.toString())
//        Log.e("endPosition", endPosition.toString())
    }

    override fun onPreviousLine(position: Int, text: CharSequence, selection: Int) {
        var viewType = mViewModel.getModel()[position - 1].viewType

        when (viewType) {
            EditerViewType.IMAGE -> {
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
            EditerViewType.LINE -> {
                mViewModel.removeViewAndKeepFocus(position - 1, position)
                adapter?.let {
                    it.upDateRemoveItemWithoutCurrentChange(position - 1)
                }
            }
            else -> {
                mViewModel.updateText(
                    position - 1,
                    text,
                    TextStyle.NORMAL,
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
        mViewModel.updateText(position, text, TextStyle.NORMAL, false, null, updateStyle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editer2)
        mViewModel = ViewModelProviders.of(this).get(EditorViewModel::class.java)
        EditorBookApplication.database!!.editerQuery().getContent(0)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { model ->
                if (model.isNotEmpty()) {
                    var editerModel: MutableList<EditerModel> = mutableListOf()
                    val json = JSONArray(model[0].content)
                    for (i in 0 until json.length()) {
                        editerModel.add((json.get(i) as JSONObject).toClass(EditerModel::class.java))
                    }
                    mViewModel.setModel(editerModel)
                } else {
                    mViewModel.addView(0, EditerViewType.EDIT_TEXT, "", true)
                }
                initView()
                initViewModel()
            }
    }

    private fun initViewModel() {
        mViewModel.editorModelLiveData.observe(this, Observer {
            if (timerIsRun) {
                timer.cancel()
                timer = Timer()
                timerIsRun = false
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        Single.fromCallable {
                            EditorBookApplication.database!!.editerQuery()
                                .insertContent(EditerTable(0, it.toJson()))
                        }
                            .subscribeOn(Schedulers.computation())
                            .subscribe { _ ->
                                Log.d("onDataChange", it.toJson())
                            }

                        initButton(it.toJson())
                    }
                }, 5000)
            } else {
                timerIsRun = true
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        Single.fromCallable {
                            EditorBookApplication.database!!.editerQuery()
                                .insertContent(EditerTable(0, it.toJson()))
                        }
                            .subscribeOn(Schedulers.computation())
                            .subscribe { _ ->
                                Log.d("onDataChange", it.toJson())
                            }
                        initButton(it.toJson())
                    }
                }, 5000)
            }
        })
    }

    private fun initButton(value: String) {
        btnShowViewMode.setOnClickListener {
            var intent = Intent(this, ReaderContentActivity::class.java)
            intent.putExtra("value", value)
            startActivity(intent)
        }
    }

    private fun initView() {
        adapter = EditerAdapter(this, this, mViewModel.getModel())
        rvEditor.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvEditor.adapter = adapter
        rvEditor.itemAnimator = null

        btnAlighment.setOnClickListener {
            try {
                var fragmentImageViewerDialog = SetAlignmentDialog.Builder()
                    .build(
                        it,
                        mViewModel.getModel()[cursorPosition].data?.alight,
                        (mViewModel.getModel()[cursorPosition].viewType == EditerViewType.INDENT)
                    )
                fragmentImageViewerDialog.retainInstance = true
                fragmentImageViewerDialog.show(supportFragmentManager, "alignment")
            } catch (ex: Exception) {
                var fragmentImageViewerDialog = SetAlignmentDialog.Builder()
                    .build(it, null, false)
                fragmentImageViewerDialog.retainInstance = true
                fragmentImageViewerDialog.show(supportFragmentManager, "alignment")
            }
        }



        btnAddImage.setOnClickListener {
            KeyboardHelper.hideSoftKeyboard(this)
            pickFromGallery()
        }

        btnQuote.setOnClickListener {
            if (cursorPosition <= mViewModel.getSize()) {
                if (mViewModel.getModel()[cursorPosition].viewType == EditerViewType.EDIT_TEXT) {
                    adapter?.let {
                        mViewModel.changeToQuote(cursorPosition)
                        it.updateCurrentItem(cursorPosition)
                    }

                    btnQuote.setColorFilter(
                        ContextCompat.getColor(this, R.color.colorOrange)
                        , PorterDuff.Mode.SRC_IN
                    )
                } else if (mViewModel.getModel()[cursorPosition].viewType == EditerViewType.QUOTE) {
                    adapter?.let {
                        mViewModel.changeToEditText(cursorPosition)
                        it.updateCurrentItem(cursorPosition)
                    }

                    btnQuote.setColorFilter(
                        ContextCompat.getColor(this, R.color.grey_image)
                        , PorterDuff.Mode.SRC_IN
                    )
                }
                else{
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
                if (mViewModel.getModel()[cursorPosition].viewType == EditerViewType.EDIT_TEXT) {
                    adapter?.let {
                        mViewModel.changeToHeader(cursorPosition)
                        it.updateCurrentItem(cursorPosition)
                        btnHeader.setColorFilter(
                            ContextCompat.getColor(this, R.color.colorOrange)
                            , PorterDuff.Mode.SRC_IN
                        )
                    }
                } else if (mViewModel.getModel()[cursorPosition].viewType == EditerViewType.HEADER) {
                    adapter?.let {
                        mViewModel.changeToEditText(cursorPosition)
                        it.updateCurrentItem(cursorPosition)
                    }
                    btnHeader.setColorFilter(
                        ContextCompat.getColor(this, R.color.grey_image)
                        , PorterDuff.Mode.SRC_IN
                    )
                }
                else{
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
            mViewModel.addImageModel(cursorPosition + 1, image)
            it.upDateImageItem(cursorPosition)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_PICTURE) {
                val selectedUri = data?.data
                selectedUri?.let {
                    addImageToModel(selectedUri.toString())
                }
//                if (selectedUri != null) {
//                    startCropActivity(data.data!!)
//                } else {
//                    Toast.makeText(this, "ไม่สามารถรับรูปนี่ได้", Toast.LENGTH_LONG).show()
//                }
            }
//            else if (requestCode == UCrop.REQUEST_CROP) {
//                data?.let {
//                    handleCropResult(it)
//                }
//            }
        }
//        if (resultCode == UCrop.RESULT_ERROR) {
//        }
    }

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
            addImageToModel(resultUri.encodedPath)
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
