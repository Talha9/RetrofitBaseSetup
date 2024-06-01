package com.example.retrofitbasesetup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseViewModel : ViewModel() {

    private val exceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            println("Caught $throwable")
            requestFinished(true, throwable)
        }
    private val _context by lazy { Dispatchers.Main + SupervisorJob() + exceptionHandler }
    protected val scope by lazy { CoroutineScope(_context) }


    override fun onCleared() {
        super.onCleared()
        scope.coroutineContext.cancelChildren() // do not cancel the scope, but only children's.
    }

    // This will handle all types of events based on the actions performed. //TODO:: this is cold channel, change it with Livedata
    private val requestEventChannel = MutableStateFlow<RequestState>(RequestState.Start)
    val requestEvent: StateFlow<RequestState> = requestEventChannel.asStateFlow()


    // This will handle all types of Toasts.
    private val infoMessageChannel = Channel<String>()
    val infoMessage: Flow<String> = infoMessageChannel.receiveAsFlow()

    var isInProcess = false

    fun requestStarted() = viewModelScope.launch(Dispatchers.Main) {
        if (!isInProcess) {
            isInProcess = true
            requestEventChannel.emit(RequestState.Loading)
        }
    }

    fun requestFinished() = viewModelScope.launch(Dispatchers.Main) {
        isInProcess = false
        requestEventChannel.emit(RequestState.Success)
    }

    open fun requestFinished(showErrorMsg: Boolean = true, error: Throwable?) =
        viewModelScope.launch(Dispatchers.Main) {
            isInProcess = false
            requestEventChannel.emit(RequestState.Error(showErrorMsg, errorDetail = error))
        }

    fun showInfo(message: String) = viewModelScope.launch(Dispatchers.Main) {
        infoMessageChannel.send(message)
    }

    protected suspend fun <T> handleRequest(
        call: suspend () -> IResponse<T>,
        result: suspend (IResponse<T>) -> Unit,
        defaultErrorHandling: Boolean = true
    ) {
        requestStarted()
        withContext(Dispatchers.IO) {
            val res = call.invoke()
            if (res.isSuccessful) {
                requestFinished()
            } else {
                requestFinished(defaultErrorHandling, error = res.errorResponse)
            }
            withContext(Dispatchers.Main) {
                result.invoke(res)
            }
        }
    }

    protected suspend fun <T> handleRequestt(
        call: suspend () -> IResponse<T>,
        result: suspend (IResponse<T>) -> Unit
    ) = viewModelScope.async(Dispatchers.IO) {
        requestStarted()
        withContext(Dispatchers.IO) {
            val res = call.invoke()
            if (res.isSuccessful) {
                requestFinished()
            } else {
                requestFinished(error = res.errorResponse)
            }
            withContext(Dispatchers.Main) {
                result.invoke(res)
            }
        }
    }

    protected fun <T> executeUiDataFlow(flow: () -> Flow<UiData<T>>): Flow<UiData<T>> {
        return flow.invoke().onStart {
            emit(UiData.Loading)
        }.onCompletion {
        }.catch {
            emit(
                UiData.Fail(
                    DataError(
                        code = ErrorCode.FLOW_EXCEPTION,
                        description = "Exception while getting flow data"
                    )
                )
            )
        }
    }

    protected open fun <T> executeFlow(flow: () -> Flow<T>): Flow<T> {
        return flow.invoke().onStart {
            requestStarted()
        }.onCompletion {
            requestFinished()
        }
    }


}