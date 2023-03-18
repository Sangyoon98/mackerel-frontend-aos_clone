package com.mackerel_frontend_aos.data.repository

import android.util.Log
import retrofit2.HttpException
import java.net.SocketException
import java.net.UnknownHostException

object CoroutineExceptionObject {

    fun exception(throwable: Throwable) : HttpErrorCode? {
        when (throwable) {
            is SocketException -> return HttpErrorCode.ESOCKET

            is HttpException -> {
                val code = throwable.code()
                val message = throwable.message()
                val errorBody = throwable.response()?.errorBody()?.string()
                val errorBodyList = errorBody?.split(",", ":")
                val errorBodyCode = errorBodyList?.get(1)
                val messageList = errorBodyList?.get(3)?.split("\"")
                val errorBodyMessage = messageList?.get(1)
                Log.d("logFish", "HttpException: code = $code, message = $message")
                Log.d("logFish", "HttpException ErrorBody: code = $errorBodyCode, message = $errorBodyMessage")
                return errorBodyCode?.let { httpExceptionValue(it) }
            }

            is UnknownHostException -> return HttpErrorCode.EHOST

            else -> {
                Log.d("logFish", "$throwable")
                return HttpErrorCode.EHANDLER
            }
        }
    }

    private fun httpExceptionValue(errorBodyCode: String) : HttpErrorCode {
        return when(errorBodyCode) {
            "10400" -> HttpErrorCode.E10400
            "10401" -> HttpErrorCode.E10401
            "10402" -> HttpErrorCode.E10402
            "10403" -> HttpErrorCode.E10403
            "10404" -> HttpErrorCode.E10404
            "10405" -> HttpErrorCode.E10405
            "10406" -> HttpErrorCode.E10406
            "10407" -> HttpErrorCode.E10407
            "10408" -> HttpErrorCode.E10408
            "10409" -> HttpErrorCode.E10409
            "10410" -> HttpErrorCode.E10410
            "10411" -> HttpErrorCode.E10411
            "10412" -> HttpErrorCode.E10412
            "10413" -> HttpErrorCode.E10413
            "10414" -> HttpErrorCode.E10414
            "10415" -> HttpErrorCode.E10415
            "10416" -> HttpErrorCode.E10416
            "10417" -> HttpErrorCode.E10417
            "10418" -> HttpErrorCode.E10418
            "10419" -> HttpErrorCode.E10419
            "10420" -> HttpErrorCode.E10420
            "10500" -> HttpErrorCode.E10500
            "10503" -> HttpErrorCode.E10503
            "10511" -> HttpErrorCode.E10511
            "10512" -> HttpErrorCode.E10512
            "10513" -> HttpErrorCode.E10513
            "10514" -> HttpErrorCode.E10514
            "10515" -> HttpErrorCode.E10515
            "10516" -> HttpErrorCode.E10516
            else -> HttpErrorCode.EHTTP
        }
    }
}