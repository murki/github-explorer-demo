package murki.githubexplorer.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            mainViewModel.setCount(editTextRepoCount.text.toString().toLong())
        }

        btnRepoList.setOnClickListener {
            isRefreshing(true)
            mainViewModel.setCount(editTextRepoCount.text.toString().toLong())
        }

        val adapter = MainAdapter()
        mainRecyclerView.setHasFixedSize(true)
        mainRecyclerView.adapter = adapter

        Log.d(CLASSNAME, "mainViewModel.repositories.observe()")
        mainViewModel.repositories.observe(this, Observer { items ->
            Log.d(CLASSNAME, "Observer onChanged() called")
            showListItems(adapter, items)
            isRefreshing(false)
        })
    }

    private fun isRefreshing(isRefreshing: Boolean) {
        // TODO: Figure out a way to show refresh based on LiveData activity
        mainSwipeRefresh?.post({
            mainSwipeRefresh.isRefreshing = isRefreshing
        })
    }

    private fun showListItems(adapter: MainAdapter, repoItemVMs: PagedList<RepoItemVM>?) {
        adapter.setList(repoItemVMs)
    }

    companion object {
        private const val CLASSNAME: String = "MainFragment"
    }
}
