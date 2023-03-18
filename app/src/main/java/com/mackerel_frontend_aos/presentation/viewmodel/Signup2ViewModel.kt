package com.mackerel_frontend_aos.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mackerel_frontend_aos.data.model.SchoolListDataList
import com.mackerel_frontend_aos.data.repository.CoroutineExceptionObject
import com.mackerel_frontend_aos.data.repository.HttpErrorCode
import com.mackerel_frontend_aos.data.repository.RetrofitInterface
import com.mackerel_frontend_aos.data.repository.SingleLiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class Signup2ViewModel : ViewModel() {

    private val retrofitInterface: RetrofitInterface = RetrofitInterface.create()

    //Dialog
    private val _dialog = SingleLiveEvent<HttpErrorCode>()
    val dialog: LiveData<HttpErrorCode>
        get() = _dialog

    //School List
    private val _schoolList = SingleLiveEvent<List<SchoolListDataList>>()
    val schoolList: LiveData<List<SchoolListDataList>>
        get() = _schoolList

    //Coroutine Exception Handler
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        val code: HttpErrorCode? = CoroutineExceptionObject.exception(throwable)
        _dialog.value = code!!
    }

    fun getSchoolList(schoolName: String) {
        viewModelScope.launch(exceptionHandler) {
            val result = retrofitInterface.getSchoolList(schoolName)
            val schoolList = result.data
            _schoolList.value = schoolList

            Log.d(
                "logFish", "학교 목록 확인 Retrofit [\n" +
                        "id: ${result.id}\n" +
                        "createAt: ${result.createAt}\n" +
                        "data: ${result.data}" +
                        "]"
            )

            schoolList.forEach {
                Log.d(
                    "logFish",
                    "name: ${it.name}\n" + "address: ${it.address}\n"
                )
            }
        }
    }
}