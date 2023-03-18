package com.mackerel_frontend_aos.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.mackerel_frontend_aos.data.model.ConfirmsCertificationNumberRequest
import com.mackerel_frontend_aos.data.model.FindPasswordGetTokenRequest
import com.mackerel_frontend_aos.data.model.SendCertificationNumberRequest
import com.mackerel_frontend_aos.data.repository.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FindPasswordViewModel: ViewModel() {

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

    //Find Password Get Token
    private val _findPasswordGetToken = SingleLiveEvent<Boolean>()
    private val _findPasswordGetTokenAuthToken = SingleLiveEvent<String>()
    private val _findPasswordGetTokenAuthTokenExpiresIn = SingleLiveEvent<Int>()
    val findPasswordGetToken: LiveData<Boolean>
        get() = _findPasswordGetToken
    val findPasswordGetTokenAuthToken: LiveData<String>
        get() = _findPasswordGetTokenAuthToken
    val findPasswordGetTokenAuthTokenExpiresIn: LiveData<Int>
        get() = _findPasswordGetTokenAuthTokenExpiresIn

    //timer
    private lateinit var job: Job
    private val _timerCount = MutableLiveData<Int>()
    val timerCount: LiveData<Int>
        get() = _timerCount

    init {
        _sendCertificationNumber.value = false
        _confirmsCertificationNumber.value = false
        _findPasswordGetToken.value = false
        _findPasswordGetTokenAuthToken.value = ""
        _findPasswordGetTokenAuthTokenExpiresIn.value = 0
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

    //비밀번호 변경 권한 토큰 발급
    fun postFindPasswordGetToken(memberId: String, name: String, phone: String) {
        viewModelScope.launch(exceptionHandler) {
            val result = retrofitInterface.postFindPasswordGetToken(FindPasswordGetTokenRequest(memberId, name, phone))
            _findPasswordGetToken.value = true
            _findPasswordGetTokenAuthToken.value = result.data.passwordAuthToken
            _findPasswordGetTokenAuthTokenExpiresIn.value = result.data.passwordAuthTokenExpiresIn

            Log.d(
                "logFish", "비밀번호 변경 권한 토큰 발급 통신 Retrofit [\n" +
                        "id: ${result.id}\n" +
                        "createAt: ${result.createAt}\n" +
                        "passwordAuthToken: ${result.data.passwordAuthToken}\n" +
                        "passwordAuthTokenExpiresIn: ${result.data.passwordAuthTokenExpiresIn}\n" +
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