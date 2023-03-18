package com.mackerel_frontend_aos.data.repository

import com.mackerel_frontend_aos.data.model.HisTimetableResponse
import com.mackerel_frontend_aos.data.model.MealServiceDietInfoResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NeisRetrofitInterface {
    companion object {
        //Neis Open API URL
        private const val BASE_URL = "https://open.neis.go.kr"

        fun create(): NeisRetrofitInterface {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NeisRetrofitInterface::class.java)
        }
    }

    //급식 식단 정보 API
    @GET("/hub/mealServiceDietInfo")
    suspend fun getMealServiceDietInfo(
        @Query("KEY") key: String,
        @Query("Type") type: String,
        @Query("pIndex") pIndex: Int,
        @Query("pSize") pSize: Int,
        @Query("ATPT_OFCDC_SC_CODE") educationCode: String,
        @Query("SD_SCHUL_CODE") schoolCode: String,
        @Query("MMEAL_SC_CODE") mealCode: String,
        @Query("MLSV_YMD") mealDate: String
    ): MealServiceDietInfoResponse

    //시간표 API
    @GET("/hub/hisTimetable")
    suspend fun getHisTimetable(
        @Query("KEY") key: String,
        @Query("Type") type: String,
        @Query("pIndex") pIndex: Int,
        @Query("pSize") pSize: Int,
        @Query("ATPT_OFCDC_SC_CODE") educationCode: String,
        @Query("SD_SCHUL_CODE") schoolCode: String,
        @Query("AY") year: String,
        @Query("sem") sem: String,
        @Query("ALL_TI_YMD") date: String,
        @Query("DGHT_CRSE_SC_NM") dayNight: String,
        @Query("GRADE") gradeNumber: String,
        @Query("CLRM_NM") grade_class: String,
        @Query("CLASS_NM") classNumber: String
    ): HisTimetableResponse
}