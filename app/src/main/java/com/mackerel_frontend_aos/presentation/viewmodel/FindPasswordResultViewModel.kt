package com.mackerel_frontend_aos.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.mackerel_frontend_aos.data.model.FindPasswordFixPasswordRequest
import com.mackerel_frontend_aos.data.repository.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FindPasswordResultViewModel: ViewModel() {

    private val retrofitInterface: RetrofitInterface = RetrofitInterface.create()

    //Dialog
    private val _dialog = SingleLiveEvent<HttpErrorCode>()
    val dialog: LiveData<HttpErrorCode>
        get() = _dialog

    //Find Password Fix Password
    private val _findPasswordFixPassword = SingleLiveEvent<Boolean>()
    val findPasswordFixPassword: LiveData<Boolean>
        get() = _findPasswordFixPassword

    //timer
    private lateinit var job: Job
    private val _timerCount = MutableLiveData<Int>()
    val timerCount: LiveData<Int>
        get() = _timerCount

    init {
        _findPasswordFixPassword.value = false
        _timerCount.value = 120
    }

    //Coroutine Exception Handler
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        val code: HttpErrorCode? = CoroutineExceptionObject.exception(throwable)
        _dialog.value = code!!
    }

    //비밀번호 수정
    fun putFindPasswordFixPassword(passwordAuthToken: String, password: String) {
        viewModelScope.launch(exceptionHandler) {
            val result = retrofitInterface.putFindPasswordFixPassword(passwordAuthToken, FindPasswordFixPasswordRequest(password))
            _findPasswordFixPassword.value = true

            Log.d(
                "logFish", "비밀번호 수정 통신 Retrofit [\n" +
                        "id: ${result.id}\n" +
                        "createAt: ${result.createAt}\n" +
                        "]"
            )
        }
    }

    fun timerStart() {
        if(::job.isInitialized) job.cancel()

        _timerCount.value = 120
        job = viewModelScope.launch {
            while(_timerCount.value!! > 0) {
                _timerCount.value = _timerCount.value!!.minus(1)
                delay(1000L)
            }

        }
    }

    fun timerStop() {
        if(::job.isInitialized) job.cancel()
    }
}