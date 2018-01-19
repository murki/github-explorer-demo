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
            isRefreshing(false)
            resultEither?.fold({ data ->
                data?.let {
                    Log.d(CLASSNAME, "Success - Displaying card VMs in Adapter")
                    showListItems(it)
                }
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

    private fun showListItems(repoItemVMs: List<RepoItemVM>) {
        (mainRecyclerView?.adapter as? MainAdapter)?.let { adapter ->
            if (adapter.dataset.isEmpty()) {
                adapter.dataset = repoItemVMs
                adapter.notifyItemRangeInserted(0, repoItemVMs.size)
            } else {
                // TODO: Use the new (Paged)ListAdapter instead: https://github.com/googlesamples/android-architecture-components/issues/135#issuecomment-330414355
                val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        return adapter.dataset[oldItemPosition].id == repoItemVMs[newItemPosition].id
                    }

                    override fun getOldListSize(): Int {
                        return adapter.dataset.size
                    }

                    override fun getNewListSize(): Int {
                        return repoItemVMs.size
                    }

                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        return adapter.dataset[oldItemPosition] == repoItemVMs[newItemPosition]
                    }

                })
                adapter.dataset = repoItemVMs
                result.dispatchUpdatesTo(adapter)
            }
        }
    }

    companion object {
        private val CLASSNAME: String = "MainFragment"
    }
}
