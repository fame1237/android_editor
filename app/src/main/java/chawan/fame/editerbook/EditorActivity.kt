package chawan.fame.editerbook

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.annotation.Nullable
import android.support.v4.app.ActivityCompat
import android.support.v4.widget.ImageViewCompat
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import chawan.fame.editerbook.glide.GlideApp
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*


class EditorActivity : AppCompatActivity() {

    var edtList: MutableList<Any> = mutableListOf()
    var timer = Timer()
    val REQUEST_SELECT_PICTURE = 10000
    var REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101
    private var mAlertDialog: AlertDialog? = null
    private val FICTIONLOG_IMAGE = "fictionlog_image"

    var cursorPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
            addLine(cursorPosition + 1)
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
        if (edtList[cursorPosition] is EditText) {
            (edtList[cursorPosition] as EditText).gravity = Gravity.LEFT
        }
    }

    private fun editTextSetTextAlignCenter() {
        if (edtList[cursorPosition] is EditText) {
            (edtList[cursorPosition] as EditText).gravity = Gravity.CENTER
        }
    }

    private fun editTextSetTextAlignRight() {
        if (edtList[cursorPosition] is EditText) {
            (edtList[cursorPosition] as EditText).gravity = Gravity.RIGHT
        }
    }

    private val ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm"

    private fun getRandomString(sizeOfRandomString: Int): String {
        val random = java.util.Random()
        val sb = StringBuilder(sizeOfRandomString)
        for (i in 0 until sizeOfRandomString)
            sb.append(ALLOWED_CHARACTERS.get(random.nextInt(ALLOWED_CHARACTERS.length)))
        return sb.toString()
    }

    fun addEditTextToView(position: Int, text: CharSequence) {
        var mEditText = EditText(this)

        edtList.add(position, mEditText)
        layoutEditor.addView(mEditText, position)

        var edittext = edtList[position] as EditText
        edittext.setText(text)

        edittext.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                keyCode == EditorInfo.IME_ACTION_DONE ||
                keyEvent.action == KeyEvent.ACTION_DOWN &&
                keyEvent.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                onNextLine(edtList.indexOf(mEditText))
                true
            } else if (keyEvent.action == KeyEvent.ACTION_DOWN &&
                keyEvent.keyCode == KeyEvent.KEYCODE_DEL
            ) {
                onPreviousLine(edtList.indexOf(mEditText))
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
            }
        })

        edittext.setOnFocusChangeListener { view, b ->
            if (b) {
                cursorPosition = edtList.indexOf(mEditText)
            }
        }


        edittext.customSelectionActionModeCallback = StyleCallback(edittext)
    }

    fun onPreviousLine(position: Int) {
        if (cursorPosition == 0) {
            return
        } else {
            if (edtList[position] is EditText) {
                if (edtList[position - 1] is EditText) {
                    var edittext = edtList[position] as EditText
                    var previosEdittext = edtList[position - 1] as EditText
                    if (position >= 0 && position <= edtList.size) {
                        if (edittext.selectionEnd == 0) {
                            if (position - 1 >= 0 && position - 1 < edtList.size) {
                                var selectionCursor = previosEdittext.text.toString().length
                                previosEdittext.setText("${previosEdittext.text}${edittext.text}")
                                previosEdittext.setSelection(selectionCursor)
                                layoutEditor.removeView(edittext)
                                edtList.removeAt(position)
                                previosEdittext.requestFocus()
                            }
                        }
                    }
                } else if (edtList[position - 1] is ImageView) {
                    var edittext = edtList[position] as EditText
                    if (edittext.selectionEnd == 0) {
                        layoutEditor.removeViewAt(position - 1)
                        edtList.removeAt(position - 1)
                    }
                } else if (edtList[position - 1] is LinearLayout) {
                    var edittext = edtList[position] as EditText
                    if (edittext.selectionEnd == 0) {
                        var previosEdittext = ((edtList[position - 1] as LinearLayout).getChildAt(1) as EditText)
                        var selectionCursor = previosEdittext.text.toString().length
                        previosEdittext.setText("${previosEdittext.text}${edittext.text}")
                        previosEdittext.setSelection(selectionCursor)
                        previosEdittext.requestFocus()
                        previosEdittext.isFocusable = true
                        layoutEditor.removeViewAt(position)
                        edtList.removeAt(position)
                        showKeyboard()
                    }
                } else if (edtList[position - 1] is RelativeLayout) {
                    var edittext = edtList[position] as EditText
                    if (edittext.selectionEnd == 0) {
                        layoutEditor.removeViewAt(position - 1)
                        edtList.removeAt(position - 1)
                    }
                }
            } else if (edtList[position] is LinearLayout) {
                if (edtList[position - 1] is EditText) {
                    var edittext = ((edtList[position] as LinearLayout).getChildAt(1) as EditText)
                    var previosEdittext = edtList[position - 1] as EditText
                    if (position >= 0 && position <= edtList.size) {
                        if (edittext.selectionEnd == 0) {
                            if (position - 1 >= 0 && position - 1 < edtList.size) {
                                var selectionCursor = previosEdittext.text.toString().length
                                previosEdittext.setText("${previosEdittext.text}${edittext.text}")
                                previosEdittext.setSelection(selectionCursor)
                                layoutEditor.removeViewAt(position)
                                edtList.removeAt(position)
                                previosEdittext.requestFocus()
                            }
                        }
                    }
                } else if (edtList[position - 1] is LinearLayout) {
                    var edittext = ((edtList[position] as LinearLayout).getChildAt(1) as EditText)
                    if (edittext.selectionEnd == 0) {
                        var previosEdittext = ((edtList[position - 1] as LinearLayout).getChildAt(1) as EditText)
                        var selectionCursor = previosEdittext.text.toString().length
                        previosEdittext.setText("${previosEdittext.text}${edittext.text}")
                        previosEdittext.setSelection(selectionCursor)
                        previosEdittext.requestFocus()
                        previosEdittext.isFocusable = true
                        layoutEditor.removeViewAt(position)
                        edtList.removeAt(position)
                        showKeyboard()
                    }
                } else if (edtList[position - 1] is ImageView) {
                    var edittext = ((edtList[position] as LinearLayout).getChildAt(1) as EditText)
                    if (edittext.selectionEnd == 0) {
                        layoutEditor.removeViewAt(position - 1)
                        edtList.removeAt(position - 1)
                    }
                } else if (edtList[position - 1] is RelativeLayout) {
                    var edittext = ((edtList[position] as LinearLayout).getChildAt(1) as EditText)
                    if (edittext.selectionEnd == 0) {
                        layoutEditor.removeViewAt(position - 1)
                        edtList.removeAt(position - 1)
                    }
                }
            }
        }
    }

    fun onNextLine(position: Int) {
        Log.d("test", "test")
        if (edtList[position] is EditText) {
            if (position + 1 <= edtList.size) {
                setTextOnNextEdittext(position, position + 1)
            }
        } else if (edtList[position] is LinearLayout) {
            var edittext = (edtList[position] as LinearLayout).getChildAt(1) as EditText
            if (position + 1 <= edtList.size) {
                var string = edittext.text.toString()
                    .substring(edittext.selectionEnd, edittext.text.length)
                edittext.setText(
                    edittext.text.toString().substring(
                        0,
                        edittext.selectionEnd
                    )
                )
                addEditTextToView(position + 1, string)
                var nextEdittext = edtList[position + 1] as EditText
                nextEdittext.requestFocus()
                nextEdittext.setSelection(0)
            }
        }
    }

    fun setTextOnPreviosEdittext(previousPosition: Int, currentPosition: Int) {
        var edittext = edtList[currentPosition] as EditText
    }

    fun setTextOnNextEdittext(currentPosition: Int, nextPosition: Int) {
        var edittext = edtList[currentPosition] as EditText
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
                ssb.setSpan(
                    it,
                    textTypeFaceStartPosition,
                    textTypeFaceEndPosition,
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                )
            }
        }
        addEditTextToView(nextPosition,string.subSequence(edittext.selectionEnd, edittext.text.length))
        edittext.setText(ssb.subSequence(0, edittext.selectionEnd))

        var nextEdittext = edtList[nextPosition] as EditText
        nextEdittext.requestFocus()
        nextEdittext.setSelection(0)

    }


    private fun addLine(position: Int) {
        val llp1 = RelativeLayout.LayoutParams(
            ScreenUtil.dpToPx(200f),
            ScreenUtil.dpToPx(2f)
        )

        llp1.setMargins(0, ScreenUtil.dpToPx(30f), 0, ScreenUtil.dpToPx(30f))
        var view1 = View(this)
        view1.setBackgroundColor(this.resources.getColor(R.color.background_default))
        view1.layoutParams = llp1

        var relativeLine = RelativeLayout(this)
        relativeLine.gravity = Gravity.CENTER
        relativeLine.addView(view1)

        cursorPosition = position
        edtList.add(cursorPosition, relativeLine)
        layoutEditor.addView(relativeLine, cursorPosition)
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

        llpTopBottom.setMargins(0, ScreenUtil.dpToPx(10f), 0, ScreenUtil.dpToPx(10f))
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
                onNextLine(edtList.indexOf(linearBlockVertical))
                true
            } else if (keyEvent.action == KeyEvent.ACTION_DOWN &&
                keyEvent.keyCode == KeyEvent.KEYCODE_DEL
            ) {
                onPreviousLine(edtList.indexOf(linearBlockVertical))
                false
            } else {
                false
            }
        }

        editText.setOnFocusChangeListener { view, b ->
            if (b) {
                Log.e("layout Quote", position.toString())
                cursorPosition = edtList.indexOf(linearBlockVertical)
            }
        }

        cursorPosition = position
        edtList.add(position, linearBlockVertical)
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

            edtList.add(cursorPosition, image)
            layoutEditor.addView(image, cursorPosition)

        } else {
        }
    }

}
