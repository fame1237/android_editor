package chawan.fame.editerbook.ui.reader

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import chawan.fame.editerbook.R
import chawan.fame.editerbook.extension.toClass
import chawan.fame.editerbook.extension.toJson
import chawan.fame.editerbook.model.editor.EditerModel
import chawan.fame.editerbook.ui.editor.EditerAdapter
import chawan.fame.editerbook.ui.editor.ReaderContentRawStateAdapter
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_reader_content.*
import org.json.JSONArray
import org.json.JSONObject


class ReaderContentActivity : AppCompatActivity() {
    var editerModel: MutableList<EditerModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader_content)
        initView()
    }

    private fun initView() {
        intent.getStringExtra("value")?.let {
            val json = JSONArray(it)
            for (i in 0 until json.length()) {
                editerModel.add((json.get(i) as JSONObject).toClass(EditerModel::class.java))
            }

            var adapter = ReaderContentRawStateAdapter(this, editerModel)
            rvReadContent.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            rvReadContent.adapter = adapter

            tvReadContent.text = JSONArray(editerModel.toJson()).toString(2)

            btnRawText.setOnClickListener {
                rvReadContent.visibility = View.GONE
                scrollTvReadContent.visibility = View.VISIBLE
            }

            btnShowView.setOnClickListener {
                rvReadContent.visibility = View.VISIBLE
                scrollTvReadContent.visibility = View.GONE
            }
        }
    }
}
