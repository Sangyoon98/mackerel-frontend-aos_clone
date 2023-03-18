package com.mackerel_frontend_aos.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.mackerel_frontend_aos.data.model.ConfirmsCertificationNumberRequest
import com.mackerel_frontend_aos.data.model.SendCertificationNumberRequest
import com.mackerel_frontend_aos.data.repository.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Signup3ViewModel: ViewModel() {

    private val retrofitInterface: RetrofitInterface = RetrofitInterface.create()

    //Dialog
    private val _dialog = SingleLiveEvent<HttpErrorCode>()
    val dialog: LiveData<HttpErrorCode>
        get() = _dialog

    //Send Certification Number
    private val _sendCertificationNumber = MutableLiveData<Boolean>()
    val sendCertificationNumber: LiveData<Boolean>
        get() = _sendCertificationNumber

    //Confirms Certification Number
    private val _confirmsCertificationNumber = MutableLiveData<Boolean>()
    val confirmsCertificationNumber: LiveData<Boolean>
        get() = _confirmsCertificationNumber

    //timer
    private lateinit var job: Job
    private val _timerCount = MutableLiveData<Int>()
    val timerCount: LiveData<Int>
        get() = _timerCount

    init {
        _sendCertificationNumber.value = false
        _confirmsCertificationNumber.value = false
        _timerCount.value = 120
    }

    //Coroutine Exception Handler
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        val code: HttpErrorCode? = CoroutineExceptionObject.exception(throwable)
        _dialog.value = code!!
    }

    //핸드폰 인증번호 전송
    fun postSendCertificationNumber(phone: String) {
        viewModelScope.launch(exceptionHandler) {
            val result = retrofitInterface.postSendCertificationNumber(
                SendCertificationNumberRequest(phone)
            )
            _sendCertificationNumber.value = true

            Log.d(
                "logFish", "인증번호 전송 통신 Retrofit [\n" +
                        "id: ${result.id}\n" +
                        "createAt: ${result.createAt}" +
                        "]"
            )
        }
    }

    //핸드폰 인증번호 확인
    fun postConfirmsCertificationNumber(phone: String, number: Int) {
        viewModelScope.launch(exceptionHandler) {
            val result = retrofitInterface.postConfirmsCertificationNumber(
                ConfirmsCertificationNumberRequest(phone, number)
            )
            _confirmsCertificationNumber.value = true

            Log.d(
                "logFish", "인증번호 확인 통신 Retrofit [\n" +
                        "id: ${result.id}\n" +
                        "createAt: ${result.createAt}" +
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