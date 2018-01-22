package murki.githubexplorer.ui


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import either.fold
import kotlinx.android.synthetic.main.fragment_main.btnRepoList
import kotlinx.android.synthetic.main.fragment_main.editTextRepoCount
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
            mainViewModel.setLastCount(editTextRepoCount.text.toString().toLong())
        }

        btnRepoList.setOnClickListener {
            isRefreshing(true)
            mainViewModel.setLastCount(editTextRepoCount.text.toString().toLong())
        }

        mainRecyclerView.setHasFixedSize(true)
        mainRecyclerView.adapter = MainAdapter()

        Log.d(CLASSNAME, "mainViewModel.repositories.observe()")
        mainViewModel.repositories.observe(this, Observer { resultEither ->
            Log.d(CLASSNAME, "Observer onChanged() called")
            resultEither?.fold({ result ->
                if (!result.isFromCache) {
                    // if data returned from cache, keep the loading indicator
                    isRefreshing(false)
                }
                result.data?.let { data ->
                    Log.d(CLASSNAME, "Success - Displaying card VMs in Adapter")
                    showListItems(data)
                }
            }, { errorMessage ->
                isRefreshing(false)
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

    private fun showListItems(repoItemVMs: List<RepoItemVM>) {
        (mainRecyclerView?.adapter as? MainAdapter)?.setList(repoItemVMs)
    }

    companion object {
        private const val CLASSNAME: String = "MainFragment"
    }
}
