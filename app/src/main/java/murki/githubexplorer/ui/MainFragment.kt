package murki.githubexplorer.ui


import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.rx2.Rx2Apollo
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_main.mainRecyclerView
import kotlinx.android.synthetic.main.fragment_main.mainSwipeRefresh
import murki.githubexplorer.GithubExplorerApp

import murki.githubexplorer.R
import murki.githubexplorer.data.MyReposQuery
import murki.githubexplorer.viewmodel.RepoItemVM


class MainFragment : Fragment() {

    private var itemsFetchDisposable: Disposable? = null

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

        when(savedInstanceState) {
            null -> fetchItems()
            else -> reloadFromSavedState(savedInstanceState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val adapter = mainRecyclerView.adapter as MainAdapter?
        // using let to only execute block if not null
        adapter?.dataset?.let {
            outState.putParcelableArrayList(MAIN_ADAPTER_LIST, it as ArrayList<out Parcelable>)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelRequest()
    }

    private fun isRefreshing(isRefreshing: Boolean) {
        mainSwipeRefresh?.post({
            mainSwipeRefresh.isRefreshing = isRefreshing
        })
    }

    private fun reloadFromSavedState(savedInstanceState: Bundle) {
        showListItems(savedInstanceState.getParcelableArrayList(MAIN_ADAPTER_LIST))
    }

    private fun fetchItems() {
        isRefreshing(true)

        cancelRequest()

        val githubExplorerApp = activity?.applicationContext as GithubExplorerApp

        val itemsFetchObservable = Rx2Apollo.from(githubExplorerApp.apolloClient.query(
                MyReposQuery.builder()
                        .last(10)
                        .build())
        )

        itemsFetchDisposable = itemsFetchObservable.subscribe({ response ->
            Log.d(CLASSNAME, "onResponse() - Displaying card VMs in Adapter")
            // TODO: deal with response.errors()
            val repoItemVMs: List<RepoItemVM>? = response.data()?.viewer()?.repositories()?.nodes()?.map { it ->
                RepoItemVM(it.name(), it.description())
            }
            activity?.runOnUiThread {
                isRefreshing(false)
                showListItems(ArrayList(repoItemVMs))
            }
        }, { error ->
            Log.e(CLASSNAME, "onFailure() - ERROR", error)
            activity?.runOnUiThread {
                isRefreshing(false)
                Toast.makeText(activity, "OnError=" + error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showListItems(repoItemVMs: ArrayList<RepoItemVM>?) {
        mainRecyclerView?.swapAdapter(MainAdapter(repoItemVMs), false)
    }

    private fun cancelRequest() {
        itemsFetchDisposable?.dispose()
    }

    companion object {
        private val CLASSNAME: String = "MainFragment"
        private val MAIN_ADAPTER_LIST: String = "MainAdapterListKey"
    }
}
