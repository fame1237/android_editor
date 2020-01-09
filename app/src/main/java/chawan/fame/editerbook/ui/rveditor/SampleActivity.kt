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
import com.example.storylog_editor.extension.toJson
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
        replaceFragment(EditorFragment.newInstance(true))
    }

    private fun initViewModel() {
        mViewModel.titleLiveData.observe(this, Observer {
            Log.e("title", it)
        })
        
        mViewModel.editorModelLiveData.observe(this, Observer {
            Log.e("test", it.toJson())
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
                "         \"key\":\"77kua\",\n" +
                "         \"text\":\"กาลครั้งหนึ่ง\",\n" +
                "         \"type\":\"unstyled\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"7k9le\",\n" +
                "         \"text\":\"\",\n" +
                "         \"type\":\"unstyled\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"7ddi2\",\n" +
                "         \"text\":\"\",\n" +
                "         \"type\":\"atomic:break\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"31gto\",\n" +
                "         \"text\":\"\",\n" +
                "         \"type\":\"unstyled\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"1tk4o\",\n" +
                "         \"text\":\"อิอิ\",\n" +
                "         \"type\":\"atomic:image\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "            \"src\":\"https://s3.ap-southeast-1.amazonaws.com/media-local.fictionlog/chapters/5dd39c305ec258001ac96d9f/5dd39c47eG9wucOz.jpeg\",\n" +
                "            \"imageSize\":{ \n" +
                "               \"width\":1413,\n" +
                "               \"height\":2000\n" +
                "            }\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"e6t8p\",\n" +
                "         \"text\":\"วันหนึ่ง ได้พบเจอผู้คน\",\n" +
                "         \"type\":\"indent\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"ujj3\",\n" +
                "         \"text\":\"งงดิ กูยังงงเลย\",\n" +
                "         \"type\":\"blockquote\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"43ho6\",\n" +
                "         \"text\":\"อิอิasdfasdf     wefwefwwef\",\n" +
                "         \"type\":\"header-three\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "            { \n" +
                "               \"offset\":17,\n" +
                "               \"length\":10,\n" +
                "               \"style\":\"STRIKETHROUGH\"\n" +
                "            },\n" +
                "            { \n" +
                "               \"offset\":17,\n" +
                "               \"length\":10,\n" +
                "               \"style\":\"ITALIC\"\n" +
                "            }\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"89km7\",\n" +
                "         \"text\":\"อิอิ\",\n" +
                "         \"type\":\"indent\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"4aeej\",\n" +
                "         \"text\":\"อิอิอิอิอิอิอิอิ .   อิอิอิ.    แอแอ\",\n" +
                "         \"type\":\"unstyled\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "            { \n" +
                "               \"offset\":0,\n" +
                "               \"length\":28,\n" +
                "               \"style\":\"BOLD\"\n" +
                "            },\n" +
                "            { \n" +
                "               \"offset\":8,\n" +
                "               \"length\":13,\n" +
                "               \"style\":\"ITALIC\"\n" +
                "            },\n" +
                "            { \n" +
                "               \"offset\":21,\n" +
                "               \"length\":7,\n" +
                "               \"style\":\"UNDERLINE\"\n" +
                "            }\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"falja\",\n" +
                "         \"text\":\"dsfpas[lf[pdsalf[p\",\n" +
                "         \"type\":\"unstyled\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "            { \n" +
                "               \"offset\":10,\n" +
                "               \"length\":6,\n" +
                "               \"style\":\"STRIKETHROUGH\"\n" +
                "            }\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"a12t8\",\n" +
                "         \"text\":\"\",\n" +
                "         \"type\":\"unstyled\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"4nfvv\",\n" +
                "         \"text\":\"อิอิอิอิอิ\",\n" +
                "         \"type\":\"right\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"9c1s3\",\n" +
                "         \"text\":\"\",\n" +
                "         \"type\":\"unstyled\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"3ec9q\",\n" +
                "         \"text\":\"\",\n" +
                "         \"type\":\"atomic:break\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"cdp7n\",\n" +
                "         \"text\":\"\",\n" +
                "         \"type\":\"unstyled\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"9cmv1\",\n" +
                "         \"text\":\"\",\n" +
                "         \"type\":\"unstyled\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"d31hm\",\n" +
                "         \"text\":\"\",\n" +
                "         \"type\":\"unstyled\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"84nt7\",\n" +
                "         \"text\":\"ddd\",\n" +
                "         \"type\":\"atomic:image\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "            \"src\":\"https://s3.ap-southeast-1.amazonaws.com/media-local.fictionlog/chapters/5dd39c305ec258001ac96d9f/5dd79e9b5g5pKURi.jpeg\",\n" +
                "            \"imageSize\":{ \n" +
                "               \"width\":679,\n" +
                "               \"height\":960\n" +
                "            }\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"anmo1\",\n" +
                "         \"text\":\"\",\n" +
                "         \"type\":\"unstyled\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"datpc\",\n" +
                "         \"text\":\"\",\n" +
                "         \"type\":\"unstyled\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"518tc\",\n" +
                "         \"text\":\"\",\n" +
                "         \"type\":\"unstyled\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"2jq86\",\n" +
                "         \"text\":\"ไม่เคยคิดว่ารบกวนเลยสักครั้ง.  ไม่เคยคิดว่ารบกวนเลยสักครั้ง. ไม่เคยคิดว่ารบกวนเลยสักครั้ง. ไม่เคยคิดว่ารบกวนเลยสักครั้ง\",\n" +
                "         \"type\":\"unstyled\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      },\n" +
                "      { \n" +
                "         \"key\":\"6o4am\",\n" +
                "         \"text\":\"\",\n" +
                "         \"type\":\"unstyled\",\n" +
                "         \"depth\":0,\n" +
                "         \"inlineStyleRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"entityRanges\":[ \n" +
                "\n" +
                "         ],\n" +
                "         \"data\":{ \n" +
                "\n" +
                "         }\n" +
                "      }\n" +
                "   ],\n" +
                "   \"entityMap\":{ \n" +
                "\n" +
                "   }\n" +
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
