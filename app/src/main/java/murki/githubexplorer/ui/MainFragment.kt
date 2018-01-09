package murki.githubexplorer.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import kotlinx.android.synthetic.main.fragment_main.mainRecyclerView
import murki.githubexplorer.GithubExplorerApp

import murki.githubexplorer.R
import murki.githubexplorer.data.MyReposQuery
import murki.githubexplorer.viewmodel.RepoItemVM


class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainRecyclerView.setHasFixedSize(true)
        mainRecyclerView.adapter = MainAdapter(emptyList())

        val githubExplorerApp = activity?.applicationContext as GithubExplorerApp

        githubExplorerApp.apolloClient?.query(
                MyReposQuery.builder()
                        .last(10)
                        .build()
        )?.enqueue(object : ApolloCall.Callback<MyReposQuery.Data>() {
            override fun onResponse(response: Response<MyReposQuery.Data>) {
                Log.d(CLASSNAME, "onResponse() - Displaying card VMs in Adapter")
                val repoItemVMs: List<RepoItemVM>? = response.data()?.viewer()?.repositories()?.nodes()?.map {
                    it -> RepoItemVM(it.name(), it.description())
                }
                activity?.runOnUiThread {
                    mainRecyclerView.swapAdapter(MainAdapter(repoItemVMs), false)
                }
            }

            override fun onFailure(e: ApolloException) {
                Log.e(CLASSNAME, "onFailure() - ERROR", e)
                activity?.runOnUiThread {
                    Toast.makeText(activity, "OnError=" + e.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    companion object {
        private val CLASSNAME: String = "MainFragment"
    }
}
