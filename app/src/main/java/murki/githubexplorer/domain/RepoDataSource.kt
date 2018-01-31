package murki.githubexplorer.domain

import android.arch.paging.ItemKeyedDataSource
import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import murki.githubexplorer.data.MyReposQuery
import murki.githubexplorer.viewmodel.RepoItemVM

class RepoDataSource(private val apolloClient: ApolloClient) : ItemKeyedDataSource<String, RepoItemVM>() {

    override fun getKey(item: RepoItemVM): String {
        return item.cursor
    }

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<RepoItemVM>) {
        Log.d(CLASSNAME, "loadInitial(${params.requestedInitialKey}) called")
        val request = MyReposQuery.builder()
                .count(params.requestedLoadSize.toLong())
                .after(params.requestedInitialKey)
                .build()

        executeApolloCall(request, callback)
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<RepoItemVM>) {
        Log.d(CLASSNAME, "loadBefore(${params.key}) called")
        // ignored, since we only want to paginate forward
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<RepoItemVM>) {
        Log.d(CLASSNAME, "loadAfter(${params.key}) called")
        val request = MyReposQuery.builder()
                .count(params.requestedLoadSize.toLong())
                .after(params.key)
                .build()

        executeApolloCall(request, callback)
    }

    private fun executeApolloCall(request: MyReposQuery, callback: LoadCallback<RepoItemVM>) {
        apolloClient.query(request).enqueue(object : ApolloCall.Callback<MyReposQuery.Data>() {
            override fun onResponse(response: Response<MyReposQuery.Data>) {
                val items: List<RepoItemVM> = response.data()?.viewer()?.repositories()?.repositoryEdges()?.mapNotNull { edge ->
                    edge.repositoryNode()?.let { node ->
                        RepoItemVM(node.id(), edge.cursor(), node.name(), node.description())
                    }
                }.orEmpty()
                callback.onResult(items)
            }

            override fun onFailure(e: ApolloException) {
                TODO("Figure out what to do with errors")
            }
        })
    }

    companion object {
        private const val CLASSNAME: String = "RepoDataSource"
    }
}