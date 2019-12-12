package chawan.fame.editerbook.ui.rveditor

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import chawan.fame.editerbook.R
import chawan.fame.editerbook.domain.repo.CallService
import com.example.storylog_editor.ui.editor.EditorFragment
import com.example.storylog_editor.ui.editor.EditorViewModel


class SampleActivity : AppCompatActivity(), CallService.GraphQLServiceCallBack {

    lateinit var mViewModel: EditorViewModel
    var keyId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.library_editor_activity_sample)
        mViewModel = ViewModelProviders.of(this).get(EditorViewModel::class.java)
        initViewModel()
        replaceFragment(EditorFragment.newInstance(getJson2()))
    }

    private fun initViewModel() {
        mViewModel.editorModelLiveData.observe(this, Observer {
            Log.e("test", it.toString())
        })

        mViewModel.uploadImageToServerLiveData.observe(this, Observer {
            keyId = it.keyId
            uploadImage(it.src)
        })
    }

    fun uploadImage(image: String) {
        CallService.uploadImage(image, this)
    }

    private fun replaceFragment(fragment: Fragment) {
        try {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.commit()
        } catch (ex: Exception) {
        }
    }

    override fun onServiceGraphQLSuccess(value: String, tag: String) {
        when (tag) {
            "UPLOAD" -> {
                mViewModel.uploadImageSuccess(keyId, value)
            }
            else -> {
            }
        }
    }

    override fun onServiceGraphQLError(value: String, tag: String) {
        when (tag) {
            "UPLOAD" -> {
                AlertDialog.Builder(this) //set icon
                    .setTitle("Error") //set message
                    .setMessage(value) //set positive button
                    .setPositiveButton(
                        "Yes"
                    ) { dialogInterface, i ->
                        //set what would happen when positive button is clicked
                        finish()
                    } //set negative button
                    .setNegativeButton(
                        "No"
                    ) { dialogInterface, i ->
                        //set what should happen when negative button is clicked
                        Toast.makeText(
                            applicationContext,
                            "Nothing Happened",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .show()
            }
            else -> {
            }
        }
    }

    fun getJson2(): String {
        return "{ \n" +
                "   \"blocks\":[ \n" +
                "      { \n" +
                "         \"data\":{ \n" +
                "            \"selection\":0,\n" +
                "            \"src\":\"\"\n" +
                "         },\n" +
                "         \"inlineStyleRange\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"isFocus\":false,\n" +
                "         \"key\":\"OTxsw\",\n" +
                "         \"showBorder\":false,\n" +
                "         \"text\":\"\",\n" +
                "         \"type\":\"unstyled\"\n" +
                "      },\n" +
                "      { \n" +
                "         \"data\":{ \n" +
                "            \"selection\":0,\n" +
                "            \"src\":\"https://s3.ap-southeast-1.amazonaws.com/media-local.fictionlog/books/5d9b09c848a76d001a6e729f/5df1ea6cfaUPqtsy.jpec\"\n" +
                "         },\n" +
                "         \"inlineStyleRange\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"isFocus\":false,\n" +
                "         \"key\":\"rxBTq\",\n" +
                "         \"showBorder\":false,\n" +
                "         \"text\":\"ห\",\n" +
                "         \"type\":\"atomic:image\"\n" +
                "      },\n" +
                "      { \n" +
                "         \"data\":{ \n" +
                "            \"selection\":0,\n" +
                "            \"src\":\"\"\n" +
                "         },\n" +
                "         \"inlineStyleRange\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"isFocus\":false,\n" +
                "         \"key\":\"zpuXE\",\n" +
                "         \"showBorder\":false,\n" +
                "         \"text\":\"\",\n" +
                "         \"type\":\"unstyled\"\n" +
                "      }\n" +
                "   ]\n" +
                "}"
    }

    fun getJson(): String {
        return "{\n" +
                "            \"blocks\":[ \n" +
                "               { \n" +
                "                  \"key\":\"57g11\",\n" +
                "                  \"text\":\"นานมาแล้วไม่ได้จำ\",\n" +
                "                  \"type\":\"unstyled\",\n" +
                "                  \"depth\":0,\n" +
                "                  \"inlineStyleRanges\":[ \n" +
                "                  ],\n" +
                "                  \"entityRanges\":[ \n" +
                "                  ],\n" +
                "                  \"data\":{ \n" +
                "                  }\n" +
                "               },\n" +
                "               { \n" +
                "                  \"key\":\"7r0pq\",\n" +
                "                  \"text\":\"บุดด้า กำลัง ให้พรกับสาวกจำนวนมาก\",\n" +
                "                  \"type\":\"atomic:image\",\n" +
                "                  \"depth\":0,\n" +
                "                  \"inlineStyleRanges\":[ \n" +
                "                  ],\n" +
                "                  \"entityRanges\":[ \n" +
                "                  ],\n" +
                "                  \"data\":{ \n" +
                "                     \"src\":\"https://s3.ap-southeast-1.amazonaws.com/media-local.fictionlog/books/5d9b09c848a76d001a6e729f/5dd4e533OdAvpaXX.jpeg\",\n" +
                "                     \"imageSize\":{ \n" +
                "                        \"width\":610,\n" +
                "                        \"height\":726\n" +
                "                     }\n" +
                "                  }\n" +
                "               },\n" +
                "               { \n" +
                "                  \"key\":\"e0kin\",\n" +
                "                  \"text\":\"คนอย่างฉันมันอาจไม่ใช่ Buddha ในฝันไม่มีอะไรเลิศเลอ มันเทียบกับใครไม่ได้\",\n" +
                "                  \"type\":\"blockquote\",\n" +
                "                  \"depth\":0,\n" +
                "                  \"inlineStyleRanges\":[ \n" +
                "\n" +
                "                  ],\n" +
                "                  \"entityRanges\":[ \n" +
                "\n" +
                "                  ],\n" +
                "                  \"data\":{ \n" +
                "\n" +
                "                  }\n" +
                "               },\n" +
                "               { \n" +
                "                  \"key\":\"63kj9\",\n" +
                "                  \"text\":\"\",\n" +
                "                  \"type\":\"unstyled\",\n" +
                "                  \"depth\":0,\n" +
                "                  \"inlineStyleRanges\":[ \n" +
                "\n" +
                "                  ],\n" +
                "                  \"entityRanges\":[ \n" +
                "\n" +
                "                  ],\n" +
                "                  \"data\":{ \n" +
                "\n" +
                "                  }\n" +
                "               },\n" +
                "               { \n" +
                "                  \"key\":\"5l3d9\",\n" +
                "                  \"text\":\"\",\n" +
                "                  \"type\":\"atomic:break\",\n" +
                "                  \"depth\":0,\n" +
                "                  \"inlineStyleRanges\":[ \n" +
                "\n" +
                "                  ],\n" +
                "                  \"entityRanges\":[ \n" +
                "\n" +
                "                  ],\n" +
                "                  \"data\":{ \n" +
                "\n" +
                "                  }\n" +
                "               },\n" +
                "               { \n" +
                "                  \"key\":\"8k8ln\",\n" +
                "                  \"text\":\"และก็จะพอจะมี ทุกสิ่งที่มีให้เธอ\",\n" +
                "                  \"type\":\"unstyled\",\n" +
                "                  \"depth\":0,\n" +
                "                  \"inlineStyleRanges\":[ \n" +
                "\n" +
                "                  ],\n" +
                "                  \"entityRanges\":[ \n" +
                "\n" +
                "                  ],\n" +
                "                  \"data\":{ \n" +
                "\n" +
                "                  }\n" +
                "               },\n" +
                "               { \n" +
                "                  \"key\":\"ark6u\",\n" +
                "                  \"text\":\"ที่มีอะไรนั้นมองไม่เห็น กับทางที่ฝันของฉันคนนี่ที่เธอต้องการ\",\n" +
                "                  \"type\":\"header-three\",\n" +
                "                  \"depth\":0,\n" +
                "                  \"inlineStyleRanges\":[ \n" +
                "\n" +
                "                  ],\n" +
                "                  \"entityRanges\":[ \n" +
                "\n" +
                "                  ],\n" +
                "                  \"data\":{ \n" +
                "\n" +
                "                  }\n" +
                "               },\n" +
                "               { \n" +
                "                  \"key\":\"ath5h\",\n" +
                "                  \"text\":\"วันนั้นฉันเดินเข้าป่า ฉันเจอนกตัวนึงมันถามฉันว่าจะไปไหน\",\n" +
                "                  \"type\":\"center\",\n" +
                "                  \"depth\":0,\n" +
                "                  \"inlineStyleRanges\":[ \n" +
                "\n" +
                "                  ],\n" +
                "                  \"entityRanges\":[ \n" +
                "\n" +
                "                  ],\n" +
                "                  \"data\":{ \n" +
                "\n" +
                "                  }\n" +
                "               },\n" +
                "               { \n" +
                "                  \"key\":\"atvu9\",\n" +
                "                  \"text\":\"ฉันจึงตอบอยากไปให้ไกล ไกลจนกว่าคนไร้หัวใจ โหวๆ โหวๆ\",\n" +
                "                  \"type\":\"unstyled\",\n" +
                "                  \"depth\":0,\n" +
                "                  \"inlineStyleRanges\":[ \n" +
                "\n" +
                "                  ],\n" +
                "                  \"entityRanges\":[ \n" +
                "\n" +
                "                  ],\n" +
                "                  \"data\":{ \n" +
                "\n" +
                "                  }\n" +
                "               },\n" +
                "               { \n" +
                "                  \"key\":\"34rtq\",\n" +
                "                  \"text\":\"หกดกหดกหดหกดกหกกด\",\n" +
                "                  \"type\":\"right\",\n" +
                "                  \"depth\":0,\n" +
                "                  \"inlineStyleRanges\":[ \n" +
                "\n" +
                "                  ],\n" +
                "                  \"entityRanges\":[ \n" +
                "\n" +
                "                  ],\n" +
                "                  \"data\":{ \n" +
                "\n" +
                "                  }\n" +
                "               },\n" +
                "               { \n" +
                "                  \"key\":\"cn98p\",\n" +
                "                  \"text\":\"กหดหกดหกด กหดหก พำพำย ดกย ดกยสพำ ยดกก\",\n" +
                "                  \"type\":\"unstyled\",\n" +
                "                  \"depth\":0,\n" +
                "                  \"inlineStyleRanges\":[ \n" +
                "                     { \n" +
                "                        \"offset\":3,\n" +
                "                        \"length\":3,\n" +
                "                        \"style\":\"UNDERLINE\"\n" +
                "                     },\n" +
                "                     { \n" +
                "                        \"offset\":18,\n" +
                "                        \"length\":3,\n" +
                "                        \"style\":\"UNDERLINE\"\n" +
                "                     },\n" +
                "                     { \n" +
                "                        \"offset\":26,\n" +
                "                        \"length\":4,\n" +
                "                        \"style\":\"UNDERLINE\"\n" +
                "                     },\n" +
                "                     { \n" +
                "                        \"offset\":33,\n" +
                "                        \"length\":4,\n" +
                "                        \"style\":\"UNDERLINE\"\n" +
                "                     },\n" +
                "                     { \n" +
                "                        \"offset\":6,\n" +
                "                        \"length\":3,\n" +
                "                        \"style\":\"BOLD\"\n" +
                "                     },\n" +
                "                     { \n" +
                "                        \"offset\":18,\n" +
                "                        \"length\":3,\n" +
                "                        \"style\":\"BOLD\"\n" +
                "                     },\n" +
                "                     { \n" +
                "                        \"offset\":33,\n" +
                "                        \"length\":4,\n" +
                "                        \"style\":\"BOLD\"\n" +
                "                     },\n" +
                "                     { \n" +
                "                        \"offset\":6,\n" +
                "                        \"length\":3,\n" +
                "                        \"style\":\"STRIKETHROUGH\"\n" +
                "                     },\n" +
                "                     { \n" +
                "                        \"offset\":26,\n" +
                "                        \"length\":4,\n" +
                "                        \"style\":\"STRIKETHROUGH\"\n" +
                "                     },\n" +
                "                     { \n" +
                "                        \"offset\":11,\n" +
                "                        \"length\":2,\n" +
                "                        \"style\":\"ITALIC\"\n" +
                "                     }\n" +
                "                  ],\n" +
                "                  \"entityRanges\":[ \n" +
                "\n" +
                "                  ],\n" +
                "                  \"data\":{ \n" +
                "\n" +
                "                  }\n" +
                "               },\n" +
                "               { \n" +
                "                  \"key\":\"2ndfu\",\n" +
                "                  \"text\":\"\",\n" +
                "                  \"type\":\"unstyled\",\n" +
                "                  \"depth\":0,\n" +
                "                  \"inlineStyleRanges\":[ \n" +
                "\n" +
                "                  ],\n" +
                "                  \"entityRanges\":[ \n" +
                "\n" +
                "                  ],\n" +
                "                  \"data\":{ \n" +
                "\n" +
                "                  }\n" +
                "               }\n" +
                "            ],\n" +
                "            \"entityMap\":{ \n" +
                "            }\n" +
                "}"
    }
}
