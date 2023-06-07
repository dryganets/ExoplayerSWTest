package testapp.av1.playground

import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView

abstract class TestCaseListActivity : ListActivity() {

    abstract val renderers: List<RendererType>
    abstract  val files: List<FileInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val testCases = mutableListOf<TestCase>()

        for (renderer in renderers) {
            for(file in files) {
                val name = file.toString() + " " + renderer.name;
                val testCase = TestCase(name, renderer, file.assetName)
                testCases.add(testCase)
            }
        }

        this.listAdapter = ArrayAdapter(this, R.layout.list_row, R.id.listRowText, testCases)
    }

    override fun onListItemClick(listView: ListView, v: View?, position: Int, id: Long) {

        // Get the list data adapter.
        val listAdapter = listView.adapter
        // Get user selected item object.
        val selectItemObj = listAdapter.getItem(position) as TestCase
        val item = selectItemObj as TestCase

        val intent = Intent(this, VideoPlayerActivity::class.java)
        intent.putExtra(VideoPlayerActivity.ARG_RENDERER_TYPE, item.rendererType.ordinal)
        intent.putExtra(VideoPlayerActivity.ARG_ASSET_NAME, item.assetName)
        startActivity(intent)
    }
}