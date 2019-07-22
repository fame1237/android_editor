package chawan.fame.editerbook.ui.rveditor

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import chawan.fame.editerbook.R
import chawan.fame.editerbook.model.editor.EditerViewType
import chawan.fame.editerbook.model.editor.TextStyle
import chawan.fame.editerbook.ui.editor.EditerAdapter
import chawan.fame.editerbook.ui.editor.EditorViewModel
import chawan.fame.editerbook.util.ImageUtil
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_editer2.*
import java.io.File

class EditerActivity2 : AppCompatActivity(), EditerAdapter.OnChange {

    var adapter: EditerAdapter? = null
    lateinit var mViewModel: EditorViewModel
    var cursorPosition = 0
    val REQUEST_SELECT_PICTURE = 10000
    var REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101
    private var mAlertDialog: AlertDialog? = null
    private val FICTIONLOG_IMAGE = "fictionlog_image"

    override fun updateCursorPosition(position: Int) {
        cursorPosition = position
        Log.e("MyCursorPosition", cursorPosition.toString())
    }

    override fun onNextLine(position: Int, text: String) {
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

    override fun onPreviousLine(position: Int, text: String) {
        mViewModel.updateText(
            position - 1,
            text,
            TextStyle.NORMAL,
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

    override fun onUpdateText(position: Int, text: String) {
        mViewModel.updateText(position, text, TextStyle.NORMAL)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editer2)
        mViewModel = ViewModelProviders.of(this).get(EditorViewModel::class.java)
        mViewModel.addView(0, EditerViewType.EDIT_TEXT, "")
        mViewModel.addView(1, EditerViewType.EDIT_TEXT, "")
        initView()
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
    }

    private fun addImageToModel(image: String) {
        adapter?.let {
            mViewModel.addImageModel(cursorPosition + 1, image)
            it.upDateItem(cursorPosition + 1)
        }
    }

    private fun editTextSetTextAlignLeft() {
        adapter?.let {
            mViewModel.updateAlignLeft(cursorPosition)
            it.updateCurrentItem(cursorPosition)
        }
    }

    private fun editTextSetTextAlignCenter() {
        adapter?.let {
            mViewModel.updateAlignCenter(cursorPosition)
            it.updateCurrentItem(cursorPosition)
        }
    }

    private fun editTextSetTextAlignRight() {
        adapter?.let {
            mViewModel.updateAlignRight(cursorPosition)
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
