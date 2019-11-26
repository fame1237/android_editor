package chawan.fame.editerbook.ui.rveditor

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import chawan.fame.editerbook.R
import chawan.fame.editerbook.domain.repo.CallService
import com.example.storylog_editor.ui.editor.EditerFragment
import com.example.storylog_editor.ui.editor.EditorViewModel


class SampleActivity : AppCompatActivity(), CallService.GraphQLServiceCallBack {

    lateinit var mViewModel: EditorViewModel

    var keyId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        mViewModel = ViewModelProviders.of(this).get(EditorViewModel::class.java)
        initViewModel()
        replaceFragment(EditerFragment.newInstance())
    }

    private fun initViewModel() {
        mViewModel.editorModelLiveData.observe(this, Observer {

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
                val alertDialog: AlertDialog = AlertDialog.Builder(this) //set icon
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
}
