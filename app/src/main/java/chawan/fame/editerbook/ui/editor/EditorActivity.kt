package chawan.fame.editerbook.ui.editor

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AlertDialog
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.CharacterStyle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.lifecycle.ViewModelProviders
import chawan.fame.editerbook.R
import chawan.fame.editerbook.ScreenUtil
import chawan.fame.editerbook.StyleCallback
import chawan.fame.editerbook.glide.GlideApp
import chawan.fame.editerbook.model.editor.EditerViewType
import chawan.fame.editerbook.model.editor.TextStyle
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*


class EditorActivity : AppCompatActivity() {

    var timer = Timer()
    val REQUEST_SELECT_PICTURE = 10000
    var REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101
    private var mAlertDialog: AlertDialog? = null
    private val FICTIONLOG_IMAGE = "fictionlog_image"

    var cursorPosition = 0

    lateinit var mViewModel: EditorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mViewModel = ViewModelProviders.of(this).get(EditorViewModel::class.java)
        initView()
    }

    private fun initView() {
        addEditTextToView(0, "test1test1 test1 test1test1")
        addEditTextToView(1, "test2test2 test2 test2test2")
        cursorPosition = 1

        btnAddImage.setOnClickListener {
            pickFromGallery()
        }

        btnLine.setOnClickListener {
            addEditTextToView(cursorPosition + 1, "")
            addLine(cursorPosition + 1)
            addEditTextToView(cursorPosition + 1, "")
            if (mViewModel.getViewType(cursorPosition) == EditerViewType.EDIT_TEXT) {
                (mViewModel.getView(cursorPosition) as EditText).requestFocus()
            }
        }

        btnQuote.setOnClickListener {
            addQuote(cursorPosition + 1)
        }

        btnLeft.setOnClickListener {
            editTextSetTextAlignLeft()
        }

        btnCenter.setOnClickListener {
            editTextSetTextAlignCenter()
        }

        btnRight.setOnClickListener {
            editTextSetTextAlignRight()
        }

    }

    private fun editTextSetTextAlignLeft() {
        if (mViewModel.getViewType(cursorPosition) == EditerViewType.EDIT_TEXT) {
            (mViewModel.getView(cursorPosition) as EditText).gravity = Gravity.LEFT
        }
    }

    private fun editTextSetTextAlignCenter() {
        if (mViewModel.getViewType(cursorPosition) == EditerViewType.EDIT_TEXT) {
            (mViewModel.getView(cursorPosition) as EditText).gravity = Gravity.CENTER
        }
    }

    private fun editTextSetTextAlignRight() {
        if (mViewModel.getViewType(cursorPosition) == EditerViewType.EDIT_TEXT) {
            (mViewModel.getView(cursorPosition) as EditText).gravity = Gravity.RIGHT
        }
    }

    fun addEditTextToView(position: Int, text: CharSequence) {
        var mEditText = EditText(this)

        mViewModel.addView(position, EditerViewType.EDIT_TEXT, mEditText)
        layoutEditor.addView(mEditText, position)

        var edittext = mViewModel.getView(position) as EditText
        edittext.setText(text)

        edittext.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                keyCode == EditorInfo.IME_ACTION_DONE ||
                keyEvent.action == KeyEvent.ACTION_DOWN &&
                keyEvent.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                onNextLine(mViewModel.getIndexOf(mEditText))
                true
            } else if (keyEvent.action == KeyEvent.ACTION_DOWN &&
                keyEvent.keyCode == KeyEvent.KEYCODE_DEL
            ) {
                onPreviousLine(mViewModel.getIndexOf(mEditText))
                false
            } else {
                false
            }
        }

        edittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString() == null) {
                    mViewModel.updateText(position, "", TextStyle.NORMAL)
                } else {
                    mViewModel.updateText(position, p0.toString(), TextStyle.NORMAL)
                }
            }
        })

        edittext.setOnFocusChangeListener { view, b ->
            if (b) {
                cursorPosition = mViewModel.getIndexOf(mEditText)
            }
        }

        edittext.customSelectionActionModeCallback = StyleCallback(edittext)
        cursorPosition = position
    }

    fun onPreviousLine(position: Int) {
        if (cursorPosition == 0) {
            return
        } else {
            if (mViewModel.getViewType(position) == EditerViewType.EDIT_TEXT) {
                if (mViewModel.getViewType(position - 1) == EditerViewType.EDIT_TEXT) {
                    setTextOnPreviosEdittext(position, position - 1)
                } else if (mViewModel.getViewType(position - 1) == EditerViewType.IMAGE) {
                    var edittext = mViewModel.getView(position) as EditText
                    if (edittext.selectionEnd == 0) {
                        layoutEditor.removeViewAt(position - 1)
                        mViewModel.removeViewAt(position - 1)
                    }
                } else if (mViewModel.getViewType(position - 1) == EditerViewType.QUOTE) {
                    var edittext = mViewModel.getView(position) as EditText
                    if (edittext.selectionEnd == 0) {
                        var previosEdittext =
                            ((mViewModel.getView(position - 1) as LinearLayout).getChildAt(1) as EditText)
                        var selectionCursor = previosEdittext.text.toString().length
                        previosEdittext.setText("${previosEdittext.text}${edittext.text}")
                        previosEdittext.setSelection(selectionCursor)
                        previosEdittext.requestFocus()
                        previosEdittext.isFocusable = true
                        layoutEditor.removeViewAt(position)
                        mViewModel.removeViewAt(position)
                        showKeyboard()
                    }
                } else if (mViewModel.getViewType(position - 1) == EditerViewType.LINE) {
                    var edittext = mViewModel.getView(position) as EditText
                    if (edittext.selectionEnd == 0) {
                        layoutEditor.removeViewAt(position - 1)
                        mViewModel.removeViewAt(position - 1)
                    }
                }
            } else if (mViewModel.getViewType(position) == EditerViewType.QUOTE) {
                if (mViewModel.getViewType(position - 1) == EditerViewType.EDIT_TEXT) {
                    var edittext = ((mViewModel.getView(position) as LinearLayout).getChildAt(1) as EditText)
                    var previosEdittext = mViewModel.getView(position - 1) as EditText
                    if (position >= 0 && position <= mViewModel.getSize()) {
                        if (edittext.selectionEnd == 0) {
                            if (position - 1 >= 0 && position - 1 < mViewModel.getSize()) {
                                var selectionCursor = previosEdittext.text.toString().length
                                previosEdittext.setText("${previosEdittext.text}${edittext.text}")
                                previosEdittext.setSelection(selectionCursor)
                                layoutEditor.removeViewAt(position)
                                mViewModel.removeViewAt(position)
                                previosEdittext.requestFocus()
                            }
                        }
                    }
                } else if (mViewModel.getViewType(position - 1) == EditerViewType.QUOTE) {
                    var edittext = ((mViewModel.getView(position) as LinearLayout).getChildAt(1) as EditText)
                    if (edittext.selectionEnd == 0) {
                        var previosEdittext =
                            ((mViewModel.getView(position - 1) as LinearLayout).getChildAt(1) as EditText)
                        var selectionCursor = previosEdittext.text.toString().length
                        previosEdittext.setText("${previosEdittext.text}${edittext.text}")
                        previosEdittext.setSelection(selectionCursor)
                        previosEdittext.requestFocus()
                        previosEdittext.isFocusable = true
                        layoutEditor.removeViewAt(position)
                        mViewModel.removeViewAt(position)
                        showKeyboard()
                    }
                } else if (mViewModel.getViewType(position - 1) == EditerViewType.IMAGE) {
                    var edittext = ((mViewModel.getView(position) as LinearLayout).getChildAt(1) as EditText)
                    if (edittext.selectionEnd == 0) {
                        layoutEditor.removeViewAt(position - 1)
                        mViewModel.removeViewAt(position - 1)
                    }
                } else if (mViewModel.getViewType(position - 1) == EditerViewType.LINE) {
                    var edittext = ((mViewModel.getView(position) as LinearLayout).getChildAt(1) as EditText)
                    if (edittext.selectionEnd == 0) {
                        layoutEditor.removeViewAt(position - 1)
                        mViewModel.removeViewAt(position - 1)
                    }
                }
            }
        }
    }

    fun onNextLine(position: Int) {
        Log.d("test", "test")
        if (mViewModel.getViewType(position) == EditerViewType.EDIT_TEXT) {
            if (position + 1 <= mViewModel.getSize()) {
                setTextOnNextEdittext(position, position + 1)
            }
        } else if (mViewModel.getViewType(position) == EditerViewType.QUOTE) {
            var edittext = (mViewModel.getView(position) as LinearLayout).getChildAt(1) as EditText
            if (position + 1 <= mViewModel.getSize()) {
                var string = edittext.text.toString()
                    .substring(edittext.selectionEnd, edittext.text.length)
                edittext.setText(
                    edittext.text.toString().substring(
                        0,
                        edittext.selectionEnd
                    )
                )
                addEditTextToView(position + 1, string)
                var nextEdittext = mViewModel.getView(position + 1) as EditText
                nextEdittext.requestFocus()
                nextEdittext.setSelection(0)
            }
        }
    }

    fun setTextOnPreviosEdittext(currentPosition: Int, previousPosition: Int) {
        var editText = mViewModel.getView(currentPosition) as EditText
        var previosEdittext = mViewModel.getView(previousPosition) as EditText

        if (editText.selectionEnd == 0) {
            if (previousPosition >= 0 && previousPosition < mViewModel.getSize()) {
                var selectionCursor = previosEdittext.text.toString().length
                val currentStringSSB = SpannableStringBuilder(editText.text)
                var prevousStringSSB = SpannableStringBuilder(previosEdittext.text)

                var typeface = currentStringSSB.getSpans(0, editText.text.length, CharacterStyle::class.java)
                if (typeface.isNotEmpty()) {
                    typeface.forEach {
                        val textTypeFaceStartPosition = editText.editableText.getSpanStart(it)
                        val textTypeFaceEndPosition = editText.editableText.getSpanEnd(it)
                        currentStringSSB.setSpan(
                            it,
                            textTypeFaceStartPosition,
                            textTypeFaceEndPosition,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }

                var previousTypeface =
                    prevousStringSSB.getSpans(0, previosEdittext.text.length, CharacterStyle::class.java)
                if (previousTypeface.isNotEmpty()) {
                    previousTypeface.forEach {
                        val textTypeFaceStartPosition = previosEdittext.editableText.getSpanStart(it)
                        val textTypeFaceEndPosition = previosEdittext.editableText.getSpanEnd(it)
                        prevousStringSSB.setSpan(
                            it,
                            textTypeFaceStartPosition,
                            textTypeFaceEndPosition,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }

                if (prevousStringSSB.toString() != "")
                    previosEdittext.text = prevousStringSSB.append(currentStringSSB)
                else
                    previosEdittext.text = currentStringSSB

                var previousStringSSB2 = SpannableStringBuilder(previosEdittext.text)
                var previousTypeface2 =
                    previousStringSSB2.getSpans(0, previosEdittext.text.length, CharacterStyle::class.java)
                if (previousTypeface2.isNotEmpty()) {
                    previousTypeface2.forEach {
                        val textTypeFaceStartPosition = previosEdittext.editableText.getSpanStart(it)
                        val textTypeFaceEndPosition = previosEdittext.editableText.getSpanEnd(it)
                        previousStringSSB2.setSpan(
                            it,
                            textTypeFaceStartPosition,
                            textTypeFaceEndPosition,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                }

                previosEdittext.text = previousStringSSB2
                previosEdittext.setSelection(selectionCursor)

                layoutEditor.removeView(editText)
                mViewModel.removeViewAt(currentPosition)
                previosEdittext.requestFocus()
            }
        }
    }

    fun setTextOnNextEdittext(currentPosition: Int, nextPosition: Int) {
        var edittext = mViewModel.getView(currentPosition) as EditText
        val ssb = SpannableStringBuilder(edittext.text)
        var string = SpannableStringBuilder(edittext.text)

        var typeface = ssb.getSpans(0, edittext.selectionEnd, CharacterStyle::class.java)
        if (typeface.isNotEmpty()) {
            typeface.forEach {
                val textTypeFaceStartPosition = edittext.editableText.getSpanStart(it)
                val textTypeFaceEndPosition = edittext.editableText.getSpanEnd(it)
                ssb.setSpan(
                    it,
                    textTypeFaceStartPosition,
                    textTypeFaceEndPosition,
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                )
            }
        }

        var nextTypeface = string.getSpans(0, edittext.text.length, CharacterStyle::class.java)
        if (nextTypeface.isNotEmpty()) {
            nextTypeface.forEach {
                val textTypeFaceStartPosition = edittext.editableText.getSpanStart(it)
                val textTypeFaceEndPosition = edittext.editableText.getSpanEnd(it)
                string.setSpan(
                    it,
                    textTypeFaceStartPosition,
                    textTypeFaceEndPosition,
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                )
            }
        }
        addEditTextToView(nextPosition, string.subSequence(edittext.selectionEnd, edittext.text.length))
        edittext.setText(ssb.subSequence(0, edittext.selectionEnd))

        var nextEdittext = mViewModel.getView(nextPosition) as EditText
        nextEdittext.requestFocus()
        nextEdittext.setSelection(0)

    }


    private fun addLine(position: Int) {
        val llp1 = LinearLayout.LayoutParams(
            ScreenUtil.dpToPx(200f),
            ScreenUtil.dpToPx(2f)
        )

        llp1.setMargins(
            0,
            ScreenUtil.dpToPx(30f), 0,
            ScreenUtil.dpToPx(30f)
        )
        var view1 = View(this)
        view1.setBackgroundColor(this.resources.getColor(R.color.background_default))
        view1.layoutParams = llp1

        var linearLine = LinearLayout(this)
        linearLine.gravity = Gravity.CENTER
        linearLine.orientation = LinearLayout.VERTICAL
        linearLine.tag = "LINE_LAYOUT"
        linearLine.addView(view1)

        mViewModel.addView(position, EditerViewType.LINE, linearLine)
        layoutEditor.addView(linearLine, position)
        cursorPosition = position
    }

    private fun showKeyboard() {
        var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    private fun addQuote(position: Int) {
        // layout quote
        var editText = EditText(this)
        val llp1 = LinearLayout.LayoutParams(
            ScreenUtil.dpToPx(100f),
            ScreenUtil.dpToPx(2f)
        )

        val llpImage = LinearLayout.LayoutParams(
            ScreenUtil.dpToPx(30f),
            ScreenUtil.dpToPx(30f)
        )

        val llpEditText = LinearLayout.LayoutParams(
            ScreenUtil.dpToPx(300f),
            ScreenUtil.dpToPx(2f)
        )

        editText.setTypeface(null, Typeface.ITALIC)

        val llp2 = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val llpTop = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        llpTop.setMargins(0, ScreenUtil.dpToPx(30f), 0, 0)

        val llpTopBottom = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        llpTopBottom.setMargins(
            0,
            ScreenUtil.dpToPx(10f), 0,
            ScreenUtil.dpToPx(10f)
        )
        llpTopBottom.gravity = Gravity.CENTER

        val llpBottom = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        llpBottom.setMargins(0, 0, 0, ScreenUtil.dpToPx(30f))

        var linearBlockVertical = LinearLayout(this)
        linearBlockVertical.orientation = LinearLayout.VERTICAL
        linearBlockVertical.gravity = Gravity.CENTER

        var linearBlockHolizontal1 = LinearLayout(this)
        linearBlockHolizontal1.orientation = LinearLayout.HORIZONTAL
        linearBlockHolizontal1.gravity = Gravity.CENTER

        var linearBlockHolizontal2 = LinearLayout(this)
        linearBlockHolizontal2.orientation = LinearLayout.HORIZONTAL
        linearBlockHolizontal2.gravity = Gravity.CENTER

        var view1 = View(this)
        view1.setBackgroundColor(this.resources.getColor(R.color.background_default))
        view1.layoutParams = llp1

        var imageView1 = ImageView(this)
        imageView1.setImageDrawable(this.resources.getDrawable(R.drawable.ic_text_quote))
        imageView1.layoutParams = llpImage

        var view2 = View(this)
        view2.setBackgroundColor(this.resources.getColor(R.color.background_default))
        view2.layoutParams = llp1

        editText.layoutParams = llpEditText
        editText.gravity = Gravity.CENTER
        editText.setBackgroundColor(this.resources.getColor(R.color.white))

        linearBlockHolizontal1.addView(view1)
        linearBlockHolizontal1.addView(imageView1)
        linearBlockHolizontal1.addView(view2)
        linearBlockHolizontal1.layoutParams = llp2

        var view3 = View(this)
        view3.setBackgroundColor(this.resources.getColor(R.color.background_default))
        view3.layoutParams = llp1

        var imageView2 = ImageView(this)
        imageView2.setImageDrawable(this.resources.getDrawable(R.drawable.ic_text_quote))
        imageView2.layoutParams = llpImage

        var view4 = View(this)
        view4.setBackgroundColor(this.resources.getColor(R.color.background_default))
        view4.layoutParams = llp1

        linearBlockHolizontal2.addView(view3)
        linearBlockHolizontal2.addView(imageView2)
        linearBlockHolizontal2.addView(view4)
        linearBlockHolizontal2.layoutParams = llp2

        linearBlockVertical.addView(linearBlockHolizontal1, llpTop)
        linearBlockVertical.addView(editText, llpTopBottom)
        linearBlockVertical.addView(linearBlockHolizontal2, llpBottom)

        editText.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                keyCode == EditorInfo.IME_ACTION_DONE ||
                keyEvent.action == KeyEvent.ACTION_DOWN &&
                keyEvent.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                onNextLine(mViewModel.getIndexOf(linearBlockVertical))
                true
            } else if (keyEvent.action == KeyEvent.ACTION_DOWN &&
                keyEvent.keyCode == KeyEvent.KEYCODE_DEL
            ) {
                onPreviousLine(mViewModel.getIndexOf(linearBlockVertical))
                false
            } else {
                false
            }
        }

        editText.setOnFocusChangeListener { view, b ->
            if (b) {
                Log.e("layout Quote", position.toString())
                cursorPosition = mViewModel.getIndexOf(linearBlockVertical)
            }
        }

        cursorPosition = position
        mViewModel.addView(position, EditerViewType.QUOTE, linearBlockVertical)
        linearBlockVertical.tag = "QUOTE_LAYOUT"
        layoutEditor.addView(linearBlockVertical, position)
        //layout quote
    }


    private fun pickFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && ActivityCompat.checkSelfPermission(
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

    fun requestPermission(permission: String, rationale: String, requestCode: Int) {
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
            data?.let {
                //                handleCropError(it)
            }
        }
    }

    private fun startCropActivity(uri: Uri) {
        var destinationFileName = FICTIONLOG_IMAGE

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

    fun handleCropResult(result: Intent) {
        val resultUri = UCrop.getOutput(result)
        if (resultUri != null) {
            var bmImg = BitmapFactory.decodeFile(resultUri.encodedPath)
            var image = ImageView(this)

            GlideApp
                .with(this)
                .load(bmImg)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .into(image)

            mViewModel.addView(cursorPosition + 1, EditerViewType.IMAGE, image)
            layoutEditor.addView(image, cursorPosition + 1)

            addEditTextToView(cursorPosition + 2, "")
            if (mViewModel.getView(cursorPosition) is EditText) {
                (mViewModel.getView(cursorPosition) as EditText).requestFocus()
            }

        } else {
        }
    }

}
