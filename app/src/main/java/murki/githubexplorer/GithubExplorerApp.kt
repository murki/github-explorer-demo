package murki.githubexplorer

import android.app.Application
import com.apollographql.apollo.ApolloClient
import okhttp3.OkHttpClient

class GithubExplorerApp : Application() {

    var apolloClient: ApolloClient? = null
        private set

    override fun onCreate() {
        super.onCreate()

        // TODO: Inject
        val okHttpClient: OkHttpClient = OkHttpClient.Builder().addInterceptor {
            chain -> chain.proceed(chain.request().newBuilder().header("Authorization", "bearer ${BuildConfig.GITHUB_API_KEY}").build())
        }.build()

        apolloClient = ApolloClient.builder()
                .serverUrl("https://api.github.com/graphql")
                .okHttpClient(okHttpClient)
                .build()
    }
}
