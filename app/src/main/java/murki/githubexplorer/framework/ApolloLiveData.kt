package murki.githubexplorer.framework

import android.arch.lifecycle.LiveData
import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import either.Either
import either.Left
import either.Right
import murki.githubexplorer.viewmodel.MainViewModel
import java.util.concurrent.atomic.AtomicBoolean

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

    companion object {
        private val CLASSNAME: String = "ApolloLiveData"
    }
}