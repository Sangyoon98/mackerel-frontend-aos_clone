package com.mackerel_frontend_aos.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.mackerel_frontend_aos.data.repository.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class SignupViewModel(application: Application): AndroidViewModel(application) {

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SignupViewModel(application) as T
        }
    }

    private val retrofitInterface: RetrofitInterface = RetrofitInterface.create()

    //Dialog
    private val _dialog = SingleLiveEvent<HttpErrorCode>()
    val dialog: LiveData<HttpErrorCode>
        get() = _dialog

    //Join Success
    private val _joinSuccess = MutableLiveData<Boolean>()
    val joinSuccess: LiveData<Boolean>
        get() = _joinSuccess

    init {
        _joinSuccess.value = false
    }

    //Coroutine Exception Handler
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        val code: HttpErrorCode? = CoroutineExceptionObject.exception(throwable)
        _dialog.value = code!!
    }

    fun postJoin(
        imageUri: String,
        memberId: String,
        password: String,
        nickname: String,
        school: String,
        grade: String,
        name: String,
        phone: String,
        adNotifications: Boolean
    ) {
        //Path 부터 비트맵 추출
        val mBitmap = PathToMultipartBody.pathToBitmap(imageUri)
        //비트맵 회전
        val mBitmapRotate = mBitmap?.let { PathToMultipartBody.rotatedBitmap(mBitmap, imageUri) }
        //비트맵 리사이징
        val mBitmapResize = mBitmapRotate?.let { PathToMultipartBody.resizeBitmapImage(it, 1080) }
        //비트맵 URI 추출
        val mBitmapUri =
            mBitmapResize?.let { PathToMultipartBody.getImageUri(getApplication(), it) }
        //비트맵 절대경로 추출
        val realPath = mBitmapUri?.let { RealPath.getRealPath(getApplication(), it) }
        //File 제작
        val file = realPath?.let { File(it) }
        //RequestBody 객체로 생성(photo)
        val request = RequestBody.create(MediaType.parse("image/png"), file)
        //MultipartBody 객체로 생성
        val photo = MultipartBody.Part.createFormData("photo", file?.name, request)
        //RequestBody 객체로 생성(info)
        val info = RequestBody.create(
            MediaType.parse("application/json"), "{\"memberId\" : \"$memberId\"," +
                    " \"password\" : \"$password\"," +
                    " \"nickname\" : \"$nickname\"," +
                    " \"school\" : \"$school\"," +
                    " \"grade\" : \"$grade\"," +
                    " \"name\" : \"$name\"," +
                    " \"phone\" : \"$phone\"," +
                    "\"agreements\" : {" +
                    " \"adNotifications\" : $adNotifications}}"
        )

        Log.d("logFish", "photo: $realPath")
        Log.d(
            "logFish", "info: {\"memberId\" : \"$memberId\"," +
                    " \"password\" : \"$password\"," +
                    " \"nickname\" : \"$nickname\"," +
                    " \"school\" : \"$school\"," +
                    " \"grade\" : \"$grade\"," +
                    " \"name\" : \"$name\"," +
                    " \"phone\" : \"$phone\"," +
                    "\"agreements\" : {" +
                    " \"adNotifications\" : $adNotifications}}"
        )

        viewModelScope.launch(exceptionHandler) {
            val result = retrofitInterface.postJoin(photo, info)
            _joinSuccess.value = true
            Log.d(
                "logFish", "회원가입 통신 Retrofit [\n" +
                        "id: ${result.id}\n" +
                        "createAt: ${result.createAt}" +
                        "]"
            )
        }
    }
}