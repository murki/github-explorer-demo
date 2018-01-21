package murki.githubexplorer

import android.app.Application
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.ResponseField
import com.apollographql.apollo.cache.normalized.CacheKey
import com.apollographql.apollo.cache.normalized.CacheKeyResolver
import com.apollographql.apollo.cache.normalized.sql.ApolloSqlHelper
import com.apollographql.apollo.cache.normalized.sql.SqlNormalizedCacheFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient

class GithubExplorerApp : Application() {

    // TODO: Remove from here in favor of (Singleton) injection
    lateinit var apolloClient: ApolloClient


    override fun onCreate() {
        super.onCreate()

        val authInterceptor = Interceptor { chain ->
            chain.proceed(chain.request().newBuilder()
                    .header(AUTH_HEADER_KEY, "bearer ${BuildConfig.GITHUB_API_KEY}")
                    .build())
        }

        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build()

        val normalizedCacheFactory = SqlNormalizedCacheFactory(ApolloSqlHelper(this, SQL_CACHE_DB_NAME))

        val cacheKeyResolver = object : CacheKeyResolver() {
            override fun fromFieldRecordSet(field: ResponseField, recordSet: Map<String, Any>): CacheKey {
                val typeName = recordSet["__typename"] as String
                if (recordSet.containsKey("id")) {
                    val typeNameAndIDKey = typeName + "." + recordSet["id"]
                    return CacheKey.from(typeNameAndIDKey)
                }
                return CacheKey.NO_KEY
            }

            // Use this resolver to customize the key for fields with variables: eg entry(repoFullName: $repoFullName).
            // This is useful if you want to make query to be able to resolved, even if it has never been run before.
            override fun fromFieldArguments(field: ResponseField, variables: Operation.Variables): CacheKey {
                // TODO: Extend to let cache store results regardless of passed variables
                return CacheKey.NO_KEY
            }
        }

        apolloClient = ApolloClient.builder()
                .serverUrl(GRAPHQL_API_ENDPOINT)
                .okHttpClient(okHttpClient)
                .normalizedCache(normalizedCacheFactory, cacheKeyResolver)
                .build()
    }

    companion object {
        private const val AUTH_HEADER_KEY = "Authorization"
        private const val GRAPHQL_API_ENDPOINT = "https://api.github.com/graphql"
        private const val SQL_CACHE_DB_NAME = "githubexplorerdb"
    }
}
