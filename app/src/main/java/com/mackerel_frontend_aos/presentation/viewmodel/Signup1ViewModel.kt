package com.mackerel_frontend_aos.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.mackerel_frontend_aos.data.repository.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class Signup1ViewModel: ViewModel() {

    private val retrofitInterface: RetrofitInterface = RetrofitInterface.create()

    //Dialog
    private val _dialog = SingleLiveEvent<HttpErrorCode>()
    val dialog: LiveData<HttpErrorCode>
        get() = _dialog

    //Id is Exist
    private val _idIsExist = MutableLiveData<Boolean>()
    val idIsExist: LiveData<Boolean>
        get() = _idIsExist

    //Nickname is Exist
    private val _nicknameIsExist = MutableLiveData<Boolean>()
    val nicknameIsExist: LiveData<Boolean>
        get() = _nicknameIsExist

    init {
    }

    //Coroutine Exception Handler
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        val code: HttpErrorCode? = CoroutineExceptionObject.exception(throwable)
        _dialog.value = code!!
    }

    //아이디 존재 여부 통신
    fun getIdExist(memberId: String) {
        viewModelScope.launch(exceptionHandler) {
            val result = retrofitInterface.getIdExist(memberId)
            val idIsExist = result.data.isExistence
            _idIsExist.value = idIsExist

            Log.d(
                "logFish", "아이디 존재 여부 확인 Retrofit [\n" +
                        "id: ${result.id}\n" +
                        "createAt: ${result.createAt}\n" +
                        "isExistence: ${result.data.isExistence}" +
                        "]"
            )
        }
    }

    //닉네임 존재 여부 통신
    fun getNicknameExist(nickname: String) {
        viewModelScope.launch(exceptionHandler) {
            val result = retrofitInterface.getNicknameExist(nickname)
            val nicknameIsExist = result.data.isExistence
            _nicknameIsExist.value = nicknameIsExist

            Log.d(
                "logFish", "닉네임 존재 여부 확인 Retrofit [\n" +
                        "id: ${result.id}\n" +
                        "createAt: ${result.createAt}\n" +
                        "isExistence: ${result.data.isExistence}" +
                        "]"
            )
        }
    }
}