package com.mackerel_frontend_aos.presentation.view.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mackerel_frontend_aos.data.repository.CoroutineExceptionObject
import com.mackerel_frontend_aos.data.repository.ExceptionDialog
import com.mackerel_frontend_aos.data.repository.FirebaseCloudMessagingService
import com.mackerel_frontend_aos.data.repository.HttpErrorCode
import com.mackerel_frontend_aos.data.repository.shared_preference.SharedPreferenceContext
import com.mackerel_frontend_aos.databinding.ActivityMainBinding
import com.mackerel_frontend_aos.presentation.viewmodel.MainActivityViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
/** Live Data 사용시 */
/*
class MainActivity : AppCompatActivity() {
    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!
    lateinit var mContext: Context
    lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this@MainActivity

        BottomSheetBehavior.from(binding.bottomSheet)

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        //Dialog
        viewModel.dialog.observe(this, Observer {
            if(it != null) {
                ExceptionDialog(this, it.header, it.body).showDialog()
            }
        })

        getMealServiceDietInfo()
        getHisTimetable()

        //Meal Table
        viewModel.mealList.observe(this, Observer {
            binding.mealTable.text = it
        })

        viewModel.timetableList.observe(this, Observer {
            binding.timeTable.text = it.toString()
        })

        //임시 로그아웃 버튼
        binding.tempLogoutButton.setOnClickListener {
            SharedPreferenceContext.preference.setPreference("autoLoginId", "")
            SharedPreferenceContext.preference.setPreference("autoLoginPassword", "")
            val intent = Intent(mContext, Login::class.java)
            startActivity(intent)
            this.finish()
        }

        /** FCM설정, Token값 가져오기 */
        FirebaseCloudMessagingService().getFirebaseToken()

        /** DynamicLink 수신확인 */
        initDynamicLink()
    }

    //Meal Table
    private fun getMealServiceDietInfo() {
        viewModel.getMealServiceDietInfo(
            "3be66d64413246a89bff54fd3d6dc93d",
            "json",
            1,
            100,
            "J10",
            "7530102",
            "2",
            "20220330"
        )
    }

    private fun getHisTimetable() {
        viewModel.getHisTimetable(
            "3be66d64413246a89bff54fd3d6dc93d",
            "json",
            1,
            100,
            "T10",
            "9290076",
            "2023",
            "1",
            "20230330",
            "주간",
            "1",
            "1-4",
            "4"
        )
    }

    /** DynamicLink */
    private fun initDynamicLink() {
        val dynamicLinkData = intent.extras
        if (dynamicLinkData != null) {
            var dataStr = "DynamicLink 수신받은 값\n"
            for (key in dynamicLinkData.keySet()) {
                dataStr += "key: $key / value: ${dynamicLinkData.getString(key)}\n"
            }
        }
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}
 */

/** Kotlin Flow 사용시 */
class MainActivity : AppCompatActivity() {
    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!
    lateinit var mContext: Context
    lateinit var viewModel: MainActivityViewModel

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        val code: HttpErrorCode? = CoroutineExceptionObject.exception(throwable)
        if (code != null) {
            ExceptionDialog(this, code.header, code.body).showDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this@MainActivity

        BottomSheetBehavior.from(binding.bottomSheet)

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        networkConnectionCode()

        //임시 로그아웃 버튼
        binding.tempLogoutButton.setOnClickListener {
            SharedPreferenceContext.preference.setPreference("autoLoginId", "")
            SharedPreferenceContext.preference.setPreference("autoLoginPassword", "")
            val intent = Intent(mContext, Login::class.java)
            startActivity(intent)
            this.finish()
        }

        /** FCM설정, Token값 가져오기 */
        FirebaseCloudMessagingService().getFirebaseToken()

        /** DynamicLink 수신확인 */
        initDynamicLink()
    }

    //Meal Table
    private fun networkConnectionCode() {
        lifecycleScope.launch(exceptionHandler) {
            viewModel.getMealServiceDietInfoFlow(
                "3be66d64413246a89bff54fd3d6dc93d",
                "json",
                1,
                100,
                "J10",
                "7530102",
                "2",
                "20220330"
            ).collect {
                binding.mealTable.text = it
            }

            viewModel.getHisTimetableFlow(
                "3be66d64413246a89bff54fd3d6dc93d",
                "json",
                1,
                100,
                "T10",
                "9290076",
                "2023",
                "1",
                "20230330",
                "주간",
                "1",
                "1-4",
                "4"
            ).collect {
                binding.timeTable.text = it.toString()
            }
        }
    }

    /** DynamicLink */
    private fun initDynamicLink() {
        val dynamicLinkData = intent.extras
        if (dynamicLinkData != null) {
            var dataStr = "DynamicLink 수신받은 값\n"
            for (key in dynamicLinkData.keySet()) {
                dataStr += "key: $key / value: ${dynamicLinkData.getString(key)}\n"
            }
        }
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}