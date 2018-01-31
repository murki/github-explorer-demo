package murki.githubexplorer.domain

import android.arch.paging.DataSource
import com.apollographql.apollo.ApolloClient
import murki.githubexplorer.viewmodel.RepoItemVM

class RepoDataSourceFactory(private val apolloClient: ApolloClient) : DataSource.Factory<String, RepoItemVM> {
    override fun create(): DataSource<String, RepoItemVM> {
        return RepoDataSource(apolloClient)
    }
}