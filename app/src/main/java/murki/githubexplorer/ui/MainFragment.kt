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
import either.fold
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
            isRefreshing(true)
            mainViewModel.setLastCountAndTrigger(10)
        }

        mainRecyclerView.setHasFixedSize(true)
        showListItems(ArrayList())

        Log.d(CLASSNAME, "mainViewModel.repositories.observe()")
        isRefreshing(true)
        mainViewModel.setLastCount(10)
        mainViewModel.repositories.observe(this, Observer { resultEither ->
            Log.d(CLASSNAME, "Observer onChanged() called")
            isRefreshing(false)
            resultEither?.fold({ data ->
                Log.d(CLASSNAME, "Success - Displaying card VMs in Adapter")
                showListItems(ArrayList(data))
            }, { errorMessage ->
                Log.e(CLASSNAME, "Error - $errorMessage")
                Toast.makeText(activity, "Error fetching Repo items", Toast.LENGTH_LONG).show()
            })
        })
    }

    private fun isRefreshing(isRefreshing: Boolean) {
        mainSwipeRefresh?.post({
            mainSwipeRefresh.isRefreshing = isRefreshing
        })
    }

    private fun showListItems(repoItemVMs: ArrayList<RepoItemVM>?) {
        // TODO: Use DiffUtils/stable Ids/notify exact changes
        mainRecyclerView?.swapAdapter(MainAdapter(repoItemVMs), true)
    }

    companion object {
        private val CLASSNAME: String = "MainFragment"
    }
}
