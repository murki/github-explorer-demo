package murki.githubexplorer

import android.app.Application
import com.apollographql.apollo.ApolloClient
import okhttp3.OkHttpClient

class GithubExplorerApp : Application() {

    // TODO: Remove from here in favor of (Singleton) injection
    val apolloClient: ApolloClient = ApolloClient.builder()
            .serverUrl("https://api.github.com/graphql")
            .okHttpClient(OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        chain.proceed(chain.request().newBuilder()
                                .header("Authorization", "bearer ${BuildConfig.GITHUB_API_KEY}")
                                .build())
                    }.build())
            .build()

}
