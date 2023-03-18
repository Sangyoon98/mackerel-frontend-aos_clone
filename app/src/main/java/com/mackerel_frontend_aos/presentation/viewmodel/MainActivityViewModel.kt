package com.mackerel_frontend_aos.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.mackerel_frontend_aos.data.repository.NeisRetrofitInterface
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.Flow

class MainActivityViewModel : ViewModel() {
    private val retrofitInterface: NeisRetrofitInterface = NeisRetrofitInterface.create()

    /** Live Data 사용시 */
    /*
    //Dialog
    private val _dialog = SingleLiveEvent<HttpErrorCode>()
    val dialog: LiveData<HttpErrorCode>
        get() = _dialog

    //School List
    private val _mealList = SingleLiveEvent<String>()
    val mealList: LiveData<String>
        get() = _mealList

    //School List
    private val _timetableList = SingleLiveEvent<ArrayList<String>>()
    val timetableList: LiveData<ArrayList<String>>
        get() = _timetableList

    //Coroutine Exception Handler
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        val code: HttpErrorCode? = CoroutineExceptionObject.exception(throwable)
        _dialog.value = code!!
    }

    fun getMealServiceDietInfo(
        key: String,
        type: String,
        pIndex: Int,
        pSize: Int,
        educationCode: String,
        schoolCode: String,
        mealCode: String,
        mealDate: String
    ) {
        viewModelScope.launch(exceptionHandler) {
            val result = retrofitInterface.getMealServiceDietInfo(key, type, pIndex, pSize, educationCode, schoolCode, mealCode, mealDate)
            val mealTableList = result.mealServiceDietInfo

            _mealList.value = mealTableList[1].row[0].ddishNm
        }
    }

    fun getHisTimetable(
        key: String,
        type: String,
        pIndex: Int,
        pSize: Int,
        educationCode: String,
        schoolCode: String,
        year: String,
        sem: String,
        date: String,
        dayNight: String,
        gradeNumber: String,
        grade_class: String,
        classNumber: String
    ) {
        viewModelScope.launch(exceptionHandler) {
            val result = retrofitInterface.getHisTimetable(key, type, pIndex, pSize, educationCode, schoolCode, year, sem, date, dayNight, gradeNumber, grade_class, classNumber)
            val timetableList = result.hisTimetable
            val list: ArrayList<String> = arrayListOf()
            timetableList[1].row.forEach {
                list.add(it.itrtCntnt)
            }

            _timetableList.value = list
        }
    }
     */

    /** Kotlin Flow 사용시 */
    fun getMealServiceDietInfoFlow(
        key: String,
        type: String,
        pIndex: Int,
        pSize: Int,
        educationCode: String,
        schoolCode: String,
        mealCode: String,
        mealDate: String
    ): Flow<String> = flow {
        val result = retrofitInterface.getMealServiceDietInfo(key, type, pIndex, pSize, educationCode, schoolCode, mealCode, mealDate)
        val mealTableList = result.mealServiceDietInfo
        val meal = mealTableList[1].row[0].ddishNm
        emit(meal)
    }

    fun getHisTimetableFlow(
        key: String,
        type: String,
        pIndex: Int,
        pSize: Int,
        educationCode: String,
        schoolCode: String,
        year: String,
        sem: String,
        date: String,
        dayNight: String,
        gradeNumber: String,
        grade_class: String,
        classNumber: String
    ): Flow<ArrayList<String>> = flow {
        val result = retrofitInterface.getHisTimetable(key, type, pIndex, pSize, educationCode, schoolCode, year, sem, date, dayNight, gradeNumber, grade_class, classNumber)
        val timetableList = result.hisTimetable
        val list: ArrayList<String> = arrayListOf()
        timetableList[1].row.forEach {
            list.add(it.itrtCntnt)
        }
        emit(list)
    }
}