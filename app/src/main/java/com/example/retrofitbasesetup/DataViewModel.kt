package com.example.retrofitbasesetup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DataViewModel : BaseViewModel() {

    private val _onApiClick=MutableLiveData<Event<Unit>>()
    val onApiClick:LiveData<Event<Unit>> get() = _onApiClick

    fun apiClick(){
        _onApiClick.value=Event(Unit)

    }

    private val repository = DataRepository()
    private val _data = MutableSharedFlow<IResponse<DataResponse>?>(1)
    val data: SharedFlow<IResponse<DataResponse>?> get() = _data

    private val _liveData = MutableLiveData<IResponse<DataResponse>?>()
    val liveData: LiveData<IResponse<DataResponse>?> get() = _liveData



    fun fetchData() {
        viewModelScope.launch {
            handleRequest(
                 {  repository.getUsersData()},
                {
                    if (it.isSuccessful) {
                        _liveData.value = it
                    } else {
                        it.errorResponse?.message?.let { it1 -> showInfo(it1) }
                    }
                }
            )

        }
    }




}