package com.mackerel_frontend_aos.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.mackerel_frontend_aos.data.model.LoginRequest
import com.mackerel_frontend_aos.data.repository.*
import com.mackerel_frontend_aos.data.repository.shared_preference.SharedPreferenceContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

    private val retrofitInterface: RetrofitInterface = RetrofitInterface.create()

    //Dialog
    private val _dialog = SingleLiveEvent<HttpErrorCode>()
    val dialog: LiveData<HttpErrorCode>
        get() = _dialog

    //Login Success
    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean>
        get() = _loginSuccess

    init {
        _loginSuccess.value = false
    }

    //Coroutine Exception Handler
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        val code: HttpErrorCode? = CoroutineExceptionObject.exception(throwable)
        _dialog.value = code!!
    }

    //로그인 통신
    fun postLogin(memberId: String, password: String) {
        viewModelScope.launch(exceptionHandler) {
            val result = retrofitInterface.postLogin(LoginRequest(memberId, password))
            val resultId = result.id
            val resultCreateAt = result.createAt
            val resultAccToken = result.data.accessToken
            val resultRefreshToken = result.data.refreshToken

            //Save Token
            SharedPreferenceContext.preference.setPreference("accessToken", resultAccToken)
            SharedPreferenceContext.preference.setPreference("refreshToken", resultRefreshToken)
            _loginSuccess.value = true

            Log.d(
                "logFish", "로그인 통신 Retrofit [\n" +
                        "id: $resultId\n" +
                        "createAt: $resultCreateAt\n" +
                        "accessToken: $resultAccToken\n" +
                        "refreshToken: $resultRefreshToken" +
                        "]"
            )
        }
    }
}