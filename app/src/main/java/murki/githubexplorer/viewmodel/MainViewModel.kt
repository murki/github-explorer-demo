package murki.githubexplorer.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.util.Log
import murki.githubexplorer.GithubExplorerApp
import murki.githubexplorer.domain.RepoDataSourceFactory

// TODO: Switch to plain ViewModel when injecting the apolloClient
class MainViewModel(application: Application) : AndroidViewModel(application) {

    // TODO: Inject
    private val repoDataSourceFactory: RepoDataSourceFactory = RepoDataSourceFactory(getApplication<GithubExplorerApp>().apolloClient)
    private val countTrigger = MutableLiveData<Long>()
    val repositories: LiveData<PagedList<RepoItemVM>>

    init {
        repositories = Transformations.switchMap(countTrigger, { count ->
            val pagedListConfig = PagedList.Config.Builder()
                    .setInitialLoadSizeHint(count.toInt())
                    .setPageSize(count.toInt())
                    .build()
            LivePagedListBuilder(repoDataSourceFactory, pagedListConfig).build()
        })
    }

    fun setCount(value: Long) {
        countTrigger.value = value
    }

    override fun onCleared() {
        Log.d(CLASSNAME, "ViewModel onCleared() called")
        // TODO: Figure out how to cancel on-going operation
    }

    companion object {
        private const val CLASSNAME: String = "MainViewModel"
    }

}