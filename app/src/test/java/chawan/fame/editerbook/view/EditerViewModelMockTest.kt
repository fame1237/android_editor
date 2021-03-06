package chawan.fame.editerbook.view


import android.os.Build
import chawan.fame.editerbook.testObserver
import com.example.storylog_editor.extension.SingleLiveEvent
import com.example.storylog_editor.model.*
import com.example.storylog_editor.ui.editor.EditorViewModel
import com.example.storylog_editor.util.CheckStyle
import com.google.common.truth.Truth
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [Build.VERSION_CODES.LOLLIPOP, Build.VERSION_CODES.M],
    packageName = "chawan.fame.editerbook"
)
class EditerViewModelMockTest {

    lateinit var editerViewModel: EditorViewModel
    var editorModelObserver = SingleLiveEvent<MutableList<EditerModel>>()
    var editerModel: MutableList<EditerModel> = mutableListOf()

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)
        editerViewModel = EditorViewModel()
    }

    @After
    fun after() {
    }

    @Test
    fun `test add view should return view`() {
        editerModel.clear()
        val model = EditerModel()
        val data = Data()
        data.text = "my text"
        data.style = "NORMAL"
        data.inlineStyleRange = CheckStyle.checkSpan(null, "")
        model.type = "unstyled"
        model.isFocus = true
        model.data = data
        data.selection = 0
        data.alight = Alignment.CENTER

        editerViewModel.addView(0, "unstyled", "my text", Alignment.CENTER, true)
        model.id = editerViewModel.getModel()[0].id
        editerModel.add(model)
        val liveDataUnderTest = editerViewModel.editorModelLiveData.testObserver()


        Truth.assert_()
            .that(liveDataUnderTest.observedValues[0])
            .isEqualTo(editerModel)
    }

    @Test
    fun `test addImageModel should return views`() {
        editerModel.clear()
        val model = EditerModel()
        val data = Data()
        data.text = ""
        data.src = "fictionlog"
        data.style = "NORMAL"
        model.type = "atomic:image"
        model.data = data
        model.isFocus = false

        val model2 = EditerModel()
        val data2 = Data()
        data2.text = ""
        data2.src = ""
        data2.style = "NORMAL"
        model2.type = "unstyled"
        model2.data = data2
        model2.isFocus = true

        editerViewModel.addImageModel(0,"fictionlog")
        model.id = editerViewModel.getModel()[0].id
        model2.id = editerViewModel.getModel()[1].id

        editerModel.add(model)
        editerModel.add(model2)

        val liveDataUnderTest = editerViewModel.editorModelLiveData.testObserver()


        Truth.assert_()
            .that(liveDataUnderTest.observedValues[0])
            .isEqualTo(editerModel)
    }

    @Test
    fun `test add line should return views`() {
        editerModel.clear()
        val model = EditerModel()
        val data = Data()
        data.text = ""
        data.src = ""
        model.type = "atomic:break"
        model.data = data
        model.isFocus = false

        val model2 = EditerModel()
        val data2 = Data()
        data2.text = ""
        data2.src = ""
        data2.style = "NORMAL"
        model2.type = "unstyled"
        model2.data = data2
        model2.isFocus = true

        editerViewModel.addLineWithEditText(0)
        model.id = editerViewModel.getModel()[0].id
        model2.id = editerViewModel.getModel()[1].id

        editerModel.add(model)
        editerModel.add(model2)

        val liveDataUnderTest = editerViewModel.editorModelLiveData.testObserver()


        Truth.assert_()
            .that(liveDataUnderTest.observedValues[0])
            .isEqualTo(editerModel)
    }

    @Test
    fun `test change to quote view`() {
        editerModel.clear()
        val model = EditerModel()
        val data = Data()
        data.text = "my text"
        data.style = "NORMAL"
        data.inlineStyleRange = CheckStyle.checkSpan(null, "")
        model.type = "blockquote"
        model.isFocus = true
        model.data = data
        data.selection = 0
        data.alight = Alignment.CENTER

        editerViewModel.addView(0, "unstyled", "my text", Alignment.CENTER, true)
        model.id = editerViewModel.getModel()[0].id
        editerModel.add(model)
        editerViewModel.changeToQuote(0)
        val liveDataUnderTest = editerViewModel.editorModelLiveData.testObserver()


        Truth.assert_()
            .that(liveDataUnderTest.observedValues[0])
            .isEqualTo(editerModel)
    }

    @Test
    fun `test change to header view`() {
        editerModel.clear()
        val model = EditerModel()
        val data = Data()
        data.text = "my text"
        data.style = "NORMAL"
        data.inlineStyleRange = CheckStyle.checkSpan(null, "")
        model.type = "header-three"
        model.isFocus = true
        model.data = data
        data.selection = 0
        data.alight = Alignment.CENTER

        editerViewModel.addView(0, "unstyled", "my text", Alignment.CENTER, true)
        model.id = editerViewModel.getModel()[0].id
        editerModel.add(model)
        editerViewModel.changeToHeader(0)
        val liveDataUnderTest = editerViewModel.editorModelLiveData.testObserver()


        Truth.assert_()
            .that(liveDataUnderTest.observedValues[0])
            .isEqualTo(editerModel)
    }

    @Test
    fun `test removeModel should remove view by index`() {
        editerModel.clear()
        val model = EditerModel()
        val data = Data()
        data.text = "my text1"
        data.style = "NORMAL"
        data.inlineStyleRange = CheckStyle.checkSpan(null, "")
        model.type = "unstyled"
        model.isFocus = false
        model.data = data
        data.selection = 0
        data.alight = Alignment.CENTER

        val model2 = EditerModel()
        val data2 = Data()
        data2.text = "my text2"
        data2.style = "NORMAL"
        data2.inlineStyleRange = CheckStyle.checkSpan(null, "")
        model2.type = "unstyled"
        model2.isFocus = false
        model2.data = data2
        data2.selection = 0
        data2.alight = Alignment.CENTER

        val model3 = EditerModel()
        val data3 = Data()
        data3.text = "my text3"
        data3.style = "NORMAL"
        data3.inlineStyleRange = CheckStyle.checkSpan(null, "")
        model3.type = "unstyled"
        model3.isFocus = true
        model3.data = data3
        data3.selection = 0
        data3.alight = Alignment.CENTER

        editerViewModel.addView(0, "unstyled", "my text1", Alignment.CENTER, true)
        editerViewModel.addView(1, "unstyled", "my text2", Alignment.CENTER, true)
        editerViewModel.addView(2, "unstyled", "my text3", Alignment.CENTER, true)
        model.id = editerViewModel.getModel()[0].id
        model2.id = editerViewModel.getModel()[1].id
        model3.id = editerViewModel.getModel()[2].id
        editerModel.add(model)
        editerModel.add(model2)
        editerModel.add(model3)
        val liveDataUnderTest = editerViewModel.editorModelLiveData.testObserver()


        Truth.assert_()
            .that(liveDataUnderTest.observedValues[0])
            .isEqualTo(editerModel)

        editerViewModel.removeViewAt(1)

        editerModel.removeAt(1)

        Truth.assert_()
            .that(liveDataUnderTest.observedValues[0])
            .isEqualTo(editerModel)

    }

    @Test
    fun `test clear focus then is focus return false`() {
        editerModel.clear()
        val model = EditerModel()
        val data = Data()
        data.text = "my text1"
        data.style = "NORMAL"
        data.inlineStyleRange = CheckStyle.checkSpan(null, "")
        model.type = "unstyled"
        model.isFocus = false
        model.data = data
        data.selection = 0
        data.alight = Alignment.CENTER

        val model2 = EditerModel()
        val data2 = Data()
        data2.text = "my text2"
        data2.style = "NORMAL"
        data2.inlineStyleRange = CheckStyle.checkSpan(null, "")
        model2.type = "unstyled"
        model2.isFocus = false
        model2.data = data2
        data2.selection = 0
        data2.alight = Alignment.CENTER

        val model3 = EditerModel()
        val data3 = Data()
        data3.text = "my text3"
        data3.style = "NORMAL"
        data3.inlineStyleRange = CheckStyle.checkSpan(null, "")
        model3.type = "unstyled"
        model3.isFocus = false
        model3.data = data3
        data3.selection = 0
        data3.alight = Alignment.CENTER

        editerViewModel.addView(0, "unstyled", "my text1", Alignment.CENTER, true)
        editerViewModel.addView(1, "unstyled", "my text2", Alignment.CENTER, true)
        editerViewModel.addView(2, "unstyled", "my text3", Alignment.CENTER, true)
        model.id = editerViewModel.getModel()[0].id
        model2.id = editerViewModel.getModel()[1].id
        model3.id = editerViewModel.getModel()[2].id
        editerModel.add(model)
        editerModel.add(model2)
        editerModel.add(model3)
        val liveDataUnderTest = editerViewModel.editorModelLiveData.testObserver()

        editerViewModel.clearFocus()

        Truth.assert_()
            .that(liveDataUnderTest.observedValues[0])
            .isEqualTo(editerModel)

    }


}