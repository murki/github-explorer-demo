package murki.githubexplorer.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.util.Log
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import either.Either
import either.Left
import either.Right
import either.fold
import murki.githubexplorer.GithubExplorerApp
import murki.githubexplorer.data.MyReposQuery
import murki.githubexplorer.framework.ApolloLiveData

// TODO: Switch to plain ViewModel when injecting the apolloClient
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val lastCountTrigger = MutableLiveData<Long>()
    val repositories: LiveData<Either<CachedResultVM<List<RepoItemVM>?>, String?>>

    init {
        repositories = Transformations.switchMap(lastCountTrigger, { lastCount ->
            Transformations.map(ApolloLiveData(getApplication<GithubExplorerApp>().apolloClient.query(
                    MyReposQuery.builder()
                            .last(lastCount)
                            .build())
                    .responseFetcher(ApolloResponseFetchers.CACHE_AND_NETWORK)), { responseEither ->
                responseEither.fold({ response ->
                    if (response.errors().isEmpty()) {
                        Log.d(CLASSNAME, "Data emitted from apollo query with ${response.data()?.viewer()?.repositories()?.nodes()?.size} items")
                        Left(CachedResultVM(response.data()?.viewer()?.repositories()?.nodes()?.map { it ->
                             RepoItemVM(it.id(), it.name(), it.description())
                        }, response.fromCache()))
                    } else {
                        Log.e(CLASSNAME, "Response has ${response.errors().count()} GraphQL error(s)=${response.errors()}")
                        Right(response.errors().toString())
                    }
                }, { error ->
                    Log.e(CLASSNAME, "Graphql error=${error.message}")
                    Right(error.message)
                })
            })
        })
    }

    fun setLastCount(value: Long) {
        lastCountTrigger.value = value
    }

    override fun onCleared() {
        Log.d(CLASSNAME, "ViewModel onCleared() called")
        repositories as ApolloLiveData<*>
        repositories.cancel()
    }

    companion object {
        private val CLASSNAME: String = "MainViewModel"
    }

}