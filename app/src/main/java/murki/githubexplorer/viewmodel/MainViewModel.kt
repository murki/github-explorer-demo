package murki.githubexplorer.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import murki.githubexplorer.GithubExplorerApp
import murki.githubexplorer.data.MyReposQuery

// TODO: Switch to plain ViewModel when injecting the apolloClient
class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repositories: LiveData<Resource<List<RepoItemVM>?>>
    private val lastCount = MutableLiveData<Long>()

    init {
        repositories = Transformations.map(getApplication<GithubExplorerApp>().apolloClient.query(
                MyReposQuery.builder()
                        .last(10)
                        .build()).toLiveData(), { gqlResponse ->
            if (gqlResponse.response != null && gqlResponse.response.errors().isEmpty()) {
                Log.d(CLASSNAME, "Data emitted from apollo query with ${gqlResponse.response.data()?.viewer()?.repositories()?.nodes()?.size} items")
                // Map result if free of errors
                Resource(gqlResponse.response.data()?.viewer()?.repositories()?.nodes()?.map { it ->
                    RepoItemVM(it.name(), it.description())
                })
            } else if(gqlResponse.exception != null) {
                Resource(null, gqlResponse.exception.message)
            } else {
                Log.e(CLASSNAME, "Response has ${gqlResponse.response?.errors()?.count()} GraphQL error(s)=${gqlResponse.response?.errors()}")
                Resource(null, gqlResponse.response?.errors()?.toString())
            }
        })
    }

    fun setLastCount(value: Long) {
        if (lastCount.value == value) {
            return
        }
        lastCount.value = value
    }

    companion object {
        private val CLASSNAME: String = "MainViewModel"
    }

    fun <T> ApolloCall<T>.toLiveData() : LiveData<ApolloLiveDataResponse<T>> {
        return object: LiveData<ApolloLiveDataResponse<T>>() {

            lateinit var clonedCall: ApolloCall<T>

            override fun onActive() {
                Log.d(CLASSNAME, "LiveData onActive() called - cloning and enqueueing Apollo call")
                clonedCall = this@toLiveData.clone()
                clonedCall.enqueue(object: ApolloCall.Callback<T>() {
                    override fun onResponse(response: Response<T>) {
                        Log.d(CLASSNAME, "Apollo onResponse() callback. Thread=${Thread.currentThread().name}")
                        postValue(ApolloLiveDataResponse(response))
                    }

                    override fun onFailure(ex: ApolloException) {
                        Log.e(CLASSNAME, "Apollo onFailure() callback. Msg=${ex.message}, Thread=${Thread.currentThread().name}")
                        postValue(ApolloLiveDataResponse(null, ex))
                    }

                })
            }

            override fun onInactive() {
                Log.d(CLASSNAME, "LiveData onInactive() called - cancelling Apollo call")
                clonedCall.cancel()
            }
        }
    }

    // TODO: Convert into type Either
    data class ApolloLiveDataResponse<T>(val response: Response<T>? = null, val exception: ApolloException? = null)

    data class Resource<out T>(val data: T? = null, val errorMessage: String? = null)
}