# android-editor

# Supported styles: #
* Bold
* Italic
* Underline
* Strikethrough
* Image
* Quote
* Header
* Line

# Samples for Storylog Editor #
```
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
```
