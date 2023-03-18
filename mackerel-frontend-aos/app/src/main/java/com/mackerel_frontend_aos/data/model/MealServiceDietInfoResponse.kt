package com.mackerel_frontend_aos.data.model

import com.google.gson.annotations.SerializedName

data class MealServiceDietInfoResponse (
    @SerializedName("mealServiceDietInfo")
    val mealServiceDietInfo: List<MealTableList>
)

data class MealTableList (
    @SerializedName("head")
    val head: List<MealHead>,
    @SerializedName("row")
    val row: List<MealRow>
)

data class MealHead (
    @SerializedName("list_total_count")
    val count: Int,
    @SerializedName("RESULT")
    val result: MealResult
)

data class MealResult (
    @SerializedName("CODE")
    val code: String,
    @SerializedName("MESSAGE")
    val message: String
)

data class MealRow (
    @SerializedName("ATPT_OFCDC_SC_CODE")
    val atptOfcdcScCode: String,
    @SerializedName("ATPT_OFCDC_SC_NM")
    val atptOfcdcScNm: String,
    @SerializedName("SD_SCHUL_CODE")
    val sdSchulCode: String,
    @SerializedName("SCHUL_NM")
    val schulNm: String,
    @SerializedName("MMEAL_SC_CODE")
    val mmealScCode: String,
    @SerializedName("MMEAL_SC_NM")
    val mmealScNm: String,
    @SerializedName("MLSV_YMD")
    val mlsvYmd: String,
    @SerializedName("MLSV_FGR")
    val mlsvFgr: String,
    @SerializedName("DDISH_NM")
    val ddishNm: String,
    @SerializedName("ORPLC_INFO")
    val orplcInfo: String,
    @SerializedName("CAL_INFO")
    val calInfo: String,
    @SerializedName("NTR_INFO")
    val ntrInfo: String,
    @SerializedName("MLSV_FROM_YMD")
    val mlsvFromYmd: String,
    @SerializedName("MLSV_TO_YMD")
    val mlsvToYmd: String
)