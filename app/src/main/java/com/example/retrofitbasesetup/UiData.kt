package com.example.retrofitbasesetup

sealed class UiData<out T> {

    object Initial: UiData<Nothing>()

    object Loading: UiData<Nothing>()

    data class Success<T>(
        val data: T
    ): UiData<T>()

    data class Fail(
        val error: Throwable
    ): UiData<Nothing>()
}
sealed class MyUiData<out T,out V> {

    object Initial: MyUiData<Nothing,Nothing>()

    object Loading: MyUiData<Nothing,Nothing>()

    data class Success<T,V>(
        val data: Pair<T, V>
    ): MyUiData<T,V>()

    data class Fail(
        val error: Throwable
    ): MyUiData<Nothing,Nothing>()
}