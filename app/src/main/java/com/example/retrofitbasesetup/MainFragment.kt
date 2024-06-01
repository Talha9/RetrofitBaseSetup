package com.example.retrofitbasesetup

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.retrofitbasesetup.databinding.FragmentMainBinding
import kotlinx.coroutines.launch


class MainFragment :
    MvvmBaseFragment<FragmentMainBinding, DataViewModel>(R.layout.fragment_main, BR.baseviewModel) {
    override fun getViewModel() = DataViewModel()
    override fun subscribeUI() {
        super.subscribeUI()
        mViewModel.onApiClick.observe(viewLifecycleOwner) {
            mViewModel.fetchData()
        }
        lifecycleScope.launch {
            mViewModel.liveData.observe(viewLifecycleOwner) {
               it?.let {
                   if(it.isSuccessful){
                       Toast.makeText(
                           requireContext(),
                           "" + it?.data?.data?.get(0)?.employee_name,
                           Toast.LENGTH_SHORT
                       ).show()
                   }
               }

            }

            /* mViewModel.data.collectIn { data ->
                 if (data?.isSuccessful == true) {
                     Toast.makeText(
                         requireContext(),
                         "" + data.data?.data?.get(0)?.employee_name,
                         Toast.LENGTH_SHORT
                     ).show()
                 } else {
                     if (data != null) {
                         Toast.makeText(
                             requireContext(),
                             "" + data.errorResponse.toString(),
                             Toast.LENGTH_SHORT
                         ).show()

                     }
                 }


             }*/

        }

    }
}