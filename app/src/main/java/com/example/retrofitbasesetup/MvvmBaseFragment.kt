package com.example.retrofitbasesetup



import androidx.lifecycle.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


abstract class MvvmBaseFragment<V : ViewDataBinding, M : BaseViewModel>(
    @LayoutRes val layoutId: Int,
    val vm: Int
) : BaseFragment() {


    protected lateinit var mBinding: V
    protected lateinit var mViewModel: M


    protected abstract fun getViewModel(): M


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = getViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        mBinding.lifecycleOwner=viewLifecycleOwner
        mBinding.setVariable(vm, mViewModel)
        initBaseObservers()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeUI()
    }

    open fun subscribeUI() {

    }


    private fun initBaseObservers() {

        lifecycleScope.launchWhenCreated {

            mViewModel.infoMessage.collect {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
        lifecycleScope.launchWhenStarted {
            mViewModel.requestEvent.collectIn { state ->
                when (state) {
                    is RequestState.Start -> {
                    }

                    is RequestState.Loading -> {
                        showLoading("Please Wait")
                    }

                    is RequestState.Success -> {
                        hideLoading()
                    }

                    is RequestState.Error -> {
                        hideLoading()
                        state.errorDetail?.let {
                            if (it is ApiError) {
                                fragmentNavhelper.handleApiError(it, state.showErrorMsg)
                            } else {
                                if (state.showErrorMsg) {
                                    Toast.makeText(
                                        requireContext(),
                                        it.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } ?: kotlin.run {
                            if (state.showErrorMsg) {
                                Toast.makeText(
                                    requireContext(),
                                    "Unknown error occurred.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }.exhaustive
            }
        }
    }

    protected fun <T> LiveData<T>.onResult(action: (T) -> Unit) {
        observe(this@MvvmBaseFragment) {
            it.let(action)
        }
    }

    protected inline fun <T> Flow<T>.collectIn(
        owner: LifecycleOwner = viewLifecycleOwner,
        minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
        crossinline action: suspend CoroutineScope.(T) -> Unit
    ) = owner.lifecycleScope.launch {
        owner.repeatOnLifecycle(minActiveState) {
            collect {
                action(it)
            }
        }
    }
}