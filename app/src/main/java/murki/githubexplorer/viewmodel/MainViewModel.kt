package murki.githubexplorer.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import either.Either
import either.Left
import either.Right
import either.fold
import murki.githubexplorer.GithubExplorerApp
import murki.githubexplorer.data.MyReposQuery
import java.util.concurrent.atomic.AtomicBoolean

// TODO: Switch to plain ViewModel when injecting the apolloClient
class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repositories: LiveData<Either<List<RepoItemVM>?, String?>>

    init {
        val query: MyReposQuery = MyReposQuery.builder()
                .last(10)
                .build()
        repositories = Transformations.map(ApolloLiveData(getApplication<GithubExplorerApp>().apolloClient.query(query)), { responseEither ->
            responseEither.fold({ response ->
                if (response.errors().isEmpty()) {
                    Log.d(CLASSNAME, "Data emitted from apollo query with ${response.data()?.viewer()?.repositories()?.nodes()?.size} items")
                    Left(response.data()?.viewer()?.repositories()?.nodes()?.map { it ->
                        RepoItemVM(it.name(), it.description())
                    })
                } else {
                    Log.e(CLASSNAME, "Response has ${response.errors().count()} GraphQL error(s)=${response.errors()}")
                    Right(response.errors().toString())
                }
            }, { error ->
                Log.e(CLASSNAME, "Graphql error=${error.message}")
                Right(error.message)
            })
        })
    }

    override fun onCleared() {
        Log.d(CLASSNAME, "ViewModel onCleared() called")
        repositories as ApolloLiveData<*>
        repositories.cancel()
    }

    companion object {
        private val CLASSNAME: String = "MainViewModel"
    }

    class ApolloLiveData<T>(private val call: ApolloCall<T>) : LiveData<Either<Response<T>, ApolloException>>() {
        private val started = AtomicBoolean(false)

        override fun onActive() {
            Log.d(CLASSNAME, "ApolloLiveData onActive() called")
            if (started.compareAndSet(false, true)) { // ensure there is only one ongoing call
                Log.d(CLASSNAME, "Enqueueing Apollo call")
                call.enqueue(object : ApolloCall.Callback<T>() {
                    override fun onResponse(response: Response<T>) {
                        Log.d(CLASSNAME, "Apollo onResponse() callback. Thread=${Thread.currentThread().name}")
                        postValue(Left(response))
                    }

                    override fun onFailure(ex: ApolloException) {
                        Log.e(CLASSNAME, "Apollo onFailure() callback. Msg=${ex.message}, Thread=${Thread.currentThread().name}")
                        postValue(Right(ex))
                    }

                })
            }
        }

        override fun onInactive() {
            Log.d(CLASSNAME, "LiveData onInactive() called")
        }

        fun cancel() {
            if (!call.isCanceled) {
                call.cancel()
            }
        }
    }
}