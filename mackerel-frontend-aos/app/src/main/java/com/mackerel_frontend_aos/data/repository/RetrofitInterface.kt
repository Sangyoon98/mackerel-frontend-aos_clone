package com.mackerel_frontend_aos.data.repository

import com.mackerel_frontend_aos.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface RetrofitInterface {
    companion object {
        //Test Server
        private const val BASE_URL =
            "http://high-school-fish.com:8080 "

        //Mock Server
        //private const val BASE_URL = "https://9464d0e2-74ee-4139-b695-ae20278d40b4.mock.pstmn.io"

        fun create(): RetrofitInterface {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitInterface::class.java)
        }
    }

    //회원 가입
    @Multipart
    @POST("/api/v1/join")
    suspend fun postJoin(
        @Part photo: MultipartBody.Part,
        @Part("info") info: RequestBody
    ): SignupResponse

    //로그인
    @POST("/api/v1/login")
    suspend fun postLogin(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    //아이디 존재 여부 확인
    @GET("/api/v1/members/id/{memberId}/exist")
    suspend fun getIdExist(
        @Path("memberId") memberId: String
    ): IdExistResponse

    //닉네임 존재 여부 확인
    @GET("/api/v1/members/nickname/{nickname}/exist")
    suspend fun getNicknameExist(
        @Path("nickname") nickname: String
    ): NicknameExistResponse

    //학교 검색
    @GET("/api/v1/schools/{schoolName}")
    suspend fun getSchoolList(
        @Path("schoolName") schoolName: String
    ): SchoolListResponse

    /*
    //핸드폰 인증 번호 전송
    @POST("/api/v1/messages/certification-numbers")
    suspend fun postSendCertificationNumber(
        @Body sendCertificationNumberRequest: SendCertificationNumberRequest
    ) : SendCertificationNumberResponse
    */

    //핸드폰 인증 번호 전송(Test)
    @POST("/api/test/v1/messages/certification-numbers")
    suspend fun postSendCertificationNumber(
        @Body sendCertificationNumberRequest: SendCertificationNumberRequest
    ): SendCertificationNumberResponse

    //핸드폰 인증번호 확인
    @POST("/api/v1/messages/certification-numbers/confirms")
    suspend fun postConfirmsCertificationNumber(
        @Body confirmsCertificationNumberRequest: ConfirmsCertificationNumberRequest
    ): ConfirmsCertificationNumberResponse

    //아이디 찾기
    @GET("/api/v1/members/name/{name}/phone/{phone}")
    suspend fun getFindId(
        @Path("name") name: String,
        @Path("phone") phone: String
    ): FindIdResponse

    //비밀번호 변경 권한 토큰 발급
    @POST("/api/v1/members/password/token")
    suspend fun postFindPasswordGetToken(
        @Body findPasswordGetTokenRequest: FindPasswordGetTokenRequest
    ): FindPasswordGetTokenResponse

    //비밀번호 수정
    @PUT("/api/v1/members/password")
    suspend fun putFindPasswordFixPassword(
        @Header("Authorization") authorization: String,
        @Body findPasswordFixPasswordRequest: FindPasswordFixPasswordRequest
    ): FindPasswordFixPasswordResponse
}