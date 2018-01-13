package murki.githubexplorer.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.apollographql.apollo.rx2.Rx2Apollo
import io.reactivex.Observable
import murki.githubexplorer.GithubExplorerApp
import murki.githubexplorer.data.MyReposQuery
import java.lang.RuntimeException

// TODO: Switch to plain ViewModel when injecting the apolloClient
class MainViewModel(application: Application) : AndroidViewModel(application) {

    fun repoItems(count: Long): Observable<List<RepoItemVM>?> {

        val myReposQuery: MyReposQuery = MyReposQuery.builder()
                .last(count)
                .build()

        return Rx2Apollo.from(getApplication<GithubExplorerApp>().apolloClient.query(myReposQuery)).map { response ->
            if (response.errors().isEmpty()) {
                // Map result if free of errors
                response.data()?.viewer()?.repositories()?.nodes()?.map { it ->
                    RepoItemVM(it.name(), it.description())
                }
            } else {
                throw RuntimeException("Response has ${response.errors().count()} error(s)=${response.errors()}")
            }
        }

    }
}