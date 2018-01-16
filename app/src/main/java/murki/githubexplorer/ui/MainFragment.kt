package murki.githubexplorer.ui


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_main.mainRecyclerView
import kotlinx.android.synthetic.main.fragment_main.mainSwipeRefresh
import murki.githubexplorer.R
import murki.githubexplorer.viewmodel.MainViewModel
import murki.githubexplorer.viewmodel.RepoItemVM


class MainFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Switch to use of ViewModelFactory so we don't use Reflection
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_dark)
        mainSwipeRefresh.setOnRefreshListener {
            fetchItems()
        }

        mainRecyclerView.setHasFixedSize(true)
        showListItems(ArrayList())

        fetchItems()
//        when (savedInstanceState) {
//            null -> fetchItems()
//            else -> reloadFromSavedState(savedInstanceState)
//        }
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        Log.d(CLASSNAME, "onSaveInstanceState()")
//        val adapter = mainRecyclerView.adapter as MainAdapter?
//        // using let to only execute block if not null
//        adapter?.dataset?.let {
//            outState.putParcelableArrayList(MAIN_ADAPTER_LIST, it as ArrayList<out Parcelable>)
//        }
//    }

    private fun isRefreshing(isRefreshing: Boolean) {
        mainSwipeRefresh?.post({
            mainSwipeRefresh.isRefreshing = isRefreshing
        })
    }

//    private fun reloadFromSavedState(savedInstanceState: Bundle) {
//        Log.d(CLASSNAME, "reloadFromSavedState()")
//        showListItems(savedInstanceState.getParcelableArrayList(MAIN_ADAPTER_LIST))
//    }

    private fun fetchItems() {
        Log.d(CLASSNAME, "fetchItems()")
        isRefreshing(true)

        mainViewModel.setLastCount(10)
        mainViewModel.repositories.observe(this, Observer { repoItemVMs ->
            Log.d(CLASSNAME, "Observer onChanged() called")
            isRefreshing(false)
            if (repoItemVMs?.data != null) {
                Log.d(CLASSNAME, "Success - Displaying card VMs in Adapter")
                showListItems(ArrayList(repoItemVMs.data))
            } else {
                Log.e(CLASSNAME, "Error - ${repoItemVMs?.errorMessage}")
                Toast.makeText(activity, "Error fetching Repo items", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showListItems(repoItemVMs: ArrayList<RepoItemVM>?) {
        // TODO: Use DiffUtils/stable Ids/notify exact changes
        mainRecyclerView?.swapAdapter(MainAdapter(repoItemVMs), true)
    }

    companion object {
        private val CLASSNAME: String = "MainFragment"
//        private val MAIN_ADAPTER_LIST: String = "MainAdapterListKey"
    }
}
