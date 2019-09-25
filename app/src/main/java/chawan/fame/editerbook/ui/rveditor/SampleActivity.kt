package chawan.fame.editerbook.ui.rveditor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import chawan.fame.editerbook.R
import com.example.editer_library.ui.editor.EditerFragment
import com.example.editer_library.ui.editor.EditorViewModel

class SampleActivity : AppCompatActivity() {

    lateinit var mViewModel: EditorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        mViewModel = ViewModelProviders.of(this).get(EditorViewModel::class.java)
        initViewModel()
        replaceFragment(EditerFragment.newInstance())
    }

    private fun initViewModel() {
        mViewModel.editorModelLiveData.observe(this, Observer {
            //todo data will response here
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        try {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.commit()
        } catch (ex: Exception) {
        }
    }
}
