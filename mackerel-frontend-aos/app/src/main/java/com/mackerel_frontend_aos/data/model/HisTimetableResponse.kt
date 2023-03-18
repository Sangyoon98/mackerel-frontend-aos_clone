package com.mackerel_frontend_aos.data.model

import com.google.gson.annotations.SerializedName

data class HisTimetableResponse (
    @SerializedName("hisTimetable")
    val hisTimetable: List<TimetableList>
)

data class TimetableList (
    @SerializedName("head")
    val head: List<TimetableHead>,
    @SerializedName("row")
    val row: List<TimetableRow>
)

data class TimetableHead (
    @SerializedName("list_total_count")
    val count: Int,
    @SerializedName("RESULT")
    val result: TimetableResult
)

data class TimetableResult (
    @SerializedName("CODE")
    val code: String,
    @SerializedName("MESSAGE")
    val message: String
)

data class TimetableRow (
    @SerializedName("ATPT_OFCDC_SC_CODE")
    val atptOfcdcScCode: String,
    @SerializedName("ATPT_OFCDC_SC_NM")
    val atptOfcdcScNm: String,
    @SerializedName("SD_SCHUL_CODE")
    val sdSchulCode: String,
    @SerializedName("SCHUL_NM")
    val schulNm: String,
    @SerializedName("AY")
    val ay: String,
    @SerializedName("SEM")
    val sem: String,
    @SerializedName("ALL_TI_YMD")
    val allTiYmd: String,
    @SerializedName("DGHT_CRSE_SC_NM")
    val dghtCrseScNm: String,
    @SerializedName("ORD_SC_NM")
    val ordScNm: String,
    @SerializedName("DDDEP_NM")
    val dddepNm: String,
    @SerializedName("GRADE")
    val grade: String,
    @SerializedName("CLRM_NM")
    val clrmNm: String,
    @SerializedName("CLASS_NM")
    val classNm: String,
    @SerializedName("PERIO")
    val perio: String,
    @SerializedName("ITRT_CNTNT")
    val itrtCntnt: String,
    @SerializedName("LOAD_DTM")
    val loadDtm: String
)