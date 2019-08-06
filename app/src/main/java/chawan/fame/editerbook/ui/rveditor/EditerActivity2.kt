package chawan.fame.editerbook.ui.rveditor

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import chawan.fame.editerbook.R
import chawan.fame.editerbook.model.editor.EditerViewType
import chawan.fame.editerbook.model.editor.TextStyle
import chawan.fame.editerbook.ui.editor.EditerAdapter
import chawan.fame.editerbook.ui.editor.EditorViewModel
import chawan.fame.editerbook.util.ImageUtil
import chawan.fame.editerbook.util.SetStyle
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_editer2.*
import java.io.File
import java.util.*

class EditerActivity2 : AppCompatActivity(), EditerAdapter.OnChange {


    var adapter: EditerAdapter? = null
    lateinit var mViewModel: EditorViewModel
    var cursorPosition = 0
    var edtView: View? = null

    val REQUEST_SELECT_PICTURE = 10000
    var REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101
    private var mAlertDialog: AlertDialog? = null
    private val FICTIONLOG_IMAGE = "fictionlog_image"
    private var timer = Timer()
    private var timerIsRun = false

    override fun updateCursorPosition(position: Int, view: View) {
        edtView = null
        cursorPosition = position
        edtView = view
        Log.e("MyCursorPosition", cursorPosition.toString())
    }

    override fun onNextLine(position: Int, text: CharSequence) {
        mViewModel.addView(
            position,
            EditerViewType.EDIT_TEXT,
            text,
            true
        )

        adapter?.let {
            it.upDateItem(position)
            rvEditor.post {
                rvEditor.scrollToPosition(position)
            }
        }
    }

    override fun onCursorChange(position: Int, startPosition: Int, endPosition: Int, edt: AppCompatEditText) {
        Log.e("startPosition", startPosition.toString())
        Log.e("endPosition", endPosition.toString())
    }

    override fun onPreviousLine(position: Int, text: CharSequence) {
        var viewType = mViewModel.getModel()[position - 1].viewType

        if (viewType == EditerViewType.IMAGE || viewType == EditerViewType.LINE) {
            mViewModel.removeViewAt(position - 1)
            adapter?.let {
                it.upDateRemoveItemWithoutCurrentChange(position - 1)
            }
        } else {
            mViewModel.updateText(
                position - 1,
                text,
                TextStyle.NORMAL,
                true,
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

    override fun onUpdateText(position: Int, text: CharSequence, updateStyle: Boolean) {
        mViewModel.updateText(position, text, TextStyle.NORMAL, false, updateStyle)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editer2)
        mViewModel = ViewModelProviders.of(this).get(EditorViewModel::class.java)
        mViewModel.addView(0, EditerViewType.EDIT_TEXT, "")
        initView()
        initViewModel()
    }

    private fun initViewModel() {
        mViewModel.editorModelLiveData.observe(this, Observer {
            if (timerIsRun) {
                timer.cancel()
                timer = Timer()
                timerIsRun = false
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        Log.d("onDataChange", it.toString())
                    }
                }, 10000)
            } else {
                timerIsRun = true
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        Log.d("onDataChange", it.toString())
                    }
                }, 10000)
            }
        })
    }

    private fun initView() {
        adapter = EditerAdapter(this, this, mViewModel.getModel())
        rvEditor.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvEditor.adapter = adapter

        btnLeft.setOnClickListener {
            editTextSetTextAlignLeft()
        }

        btnCenter.setOnClickListener {
            editTextSetTextAlignCenter()
        }

        btnRight.setOnClickListener {
            editTextSetTextAlignRight()
        }

        btnAddImage.setOnClickListener {
            pickFromGallery()
        }

        btnQuote.setOnClickListener {
            if (cursorPosition <= mViewModel.getSize()) {
                if (mViewModel.getModel()[cursorPosition].viewType == EditerViewType.EDIT_TEXT) {
                    adapter?.let {
                        mViewModel.changeToQuote(cursorPosition)
                        it.updateCurrentItem(cursorPosition)
                    }
                } else if (mViewModel.getModel()[cursorPosition].viewType == EditerViewType.QUOTE) {
                    adapter?.let {
                        mViewModel.changeToEditText(cursorPosition)
                        it.updateCurrentItem(cursorPosition)
                    }
                }
            }
        }

//        btnHeader.setOnClickListener {
//            if (cursorPosition <= mViewModel.getSize()) {
//                if (mViewModel.getModel()[cursorPosition].viewType == EditerViewType.EDIT_TEXT) {
//                    adapter?.let {
//                        mViewModel.changeToHeader(cursorPosition)
//                        it.updateCurrentItem(cursorPosition)
//                    }
//                } else if (mViewModel.getModel()[cursorPosition].viewType == EditerViewType.HEADER) {
//                    adapter?.let {
//                        mViewModel.changeToEditText(cursorPosition)
//                        it.updateCurrentItem(cursorPosition)
//                    }
//                }
//            }
//        }

        btnLine.setOnClickListener {
            addLine()
        }

//        btnBold.setOnClickListener {
//            setBold()
//        }
//
//        btnItalic.setOnClickListener {
//            setItalic()
//        }
//
//        btnUnderline.setOnClickListener {
//            setUnderLine()
//        }
//        btnTextStrikeOut.setOnClickListener {
//            setStrikethrough()
//        }
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
            if (cursorPosition + 1 >= mViewModel.getSize()) {
                mViewModel.addLineWithEditText(cursorPosition + 1)
                it.upDateLineItemWithEditText(cursorPosition)
            } else {
                mViewModel.addLine(cursorPosition + 1)
                it.upDateLineItem(cursorPosition)
            }
        }
    }

    private fun addImageToModel(image: String) {
        adapter?.let {
            mViewModel.addImageModel(cursorPosition + 1, image)
            it.upDateImageItem(cursorPosition)
        }
    }

    private fun editTextSetTextAlignLeft() {
        adapter?.let {
            mViewModel.updateAlignLeft(cursorPosition, (edtView as EditText).selectionEnd)
            it.updateCurrentItem(cursorPosition)
        }
    }

    private fun editTextSetTextAlignCenter() {
        adapter?.let {
            mViewModel.updateAlignCenter(cursorPosition, (edtView as EditText).selectionEnd)
            it.updateCurrentItem(cursorPosition)
        }
    }

    private fun editTextSetTextAlignRight() {
        adapter?.let {
            mViewModel.updateAlignRight(cursorPosition, (edtView as EditText).selectionEnd)
            it.updateCurrentItem(cursorPosition)
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
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_SELECT_PICTURE)
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
                if (selectedUri != null) {
                    startCropActivity(data.data!!)
                } else {
                    Toast.makeText(this, "ไม่สามารถรับรูปนี่ได้", Toast.LENGTH_LONG).show()
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                data?.let {
                    handleCropResult(it)
                }
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
        }
    }

    private fun startCropActivity(uri: Uri) {
        val destinationFileName = FICTIONLOG_IMAGE
        var uCrop = UCrop.of(uri, Uri.fromFile(File(cacheDir, "$destinationFileName.jpeg")))
        uCrop = basisConfig(uCrop)
        uCrop.start(this)
    }

    private fun basisConfig(uCrop: UCrop): UCrop {
        var uCrop = uCrop
        val displayMetrics = this.resources.displayMetrics
        val dpHeight = displayMetrics.heightPixels / displayMetrics.density
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        uCrop = uCrop.useSourceImageAspectRatio()
        uCrop = uCrop.withMaxResultSize(dpWidth.toInt(), dpHeight.toInt())
        return uCrop
    }

    private fun handleCropResult(result: Intent) {
        val resultUri = UCrop.getOutput(result)
        if (resultUri != null) {
            val bmImg = BitmapFactory.decodeFile(resultUri.encodedPath)
            addImageToModel(ImageUtil.bitmapToBase64(bmImg))
        } else {
        }
    }

}
