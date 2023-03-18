package com.mackerel_frontend_aos.presentation.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mackerel_frontend_aos.data.repository.ExceptionDialog
import com.mackerel_frontend_aos.databinding.ActivitySignupBinding
import com.mackerel_frontend_aos.presentation.view.adapter.SignupViewPagerAdapter
import com.mackerel_frontend_aos.presentation.view.fragment.Signup1
import com.mackerel_frontend_aos.presentation.view.fragment.Signup2
import com.mackerel_frontend_aos.presentation.view.fragment.Signup3
import com.mackerel_frontend_aos.presentation.viewmodel.SignupViewModel

class Signup : AppCompatActivity() {
    private var mBinding: ActivitySignupBinding? = null
    private val binding get() = mBinding!!
    lateinit var mContext: Context
    private lateinit var SignupViewPagerAdapter: SignupViewPagerAdapter
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            SignupViewModel.Factory(application)
        )[SignupViewModel::class.java]
    }

    private var _photo = ""
    private var _memberId = ""
    private var _password = ""
    private var _passwordCheck = ""
    private var _nickname = ""
    private var _school = ""
    private var _grade = ""
    private var _name = ""
    private var _phone = ""
    private var _certificationNumber = ""
    private var _adNotifications = false

    private var _signup1Filled = false
    private var _signup2Filled = false
    private var _signup3Filled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this@Signup

        //Dialog
        viewModel.dialog.observe(this, Observer {
            if(it != null) {
                ExceptionDialog(this, it.header, it.body).showDialog()
            }
        })

        _adNotifications = intent.getBooleanExtra("adNotifications", false)
        Log.d("logFish", "$_adNotifications")

        viewPagerAdapter()

        if (_signup1Filled && _signup2Filled && _signup3Filled) {
            binding.buttonSignup.isClickable = true
            binding.buttonSignup.isEnabled = true
        } else {
            binding.buttonSignup.isClickable = false
            binding.buttonSignup.isEnabled = false
        }

        //액티비티 이동
        binding.buttonSignup.setOnClickListener {
            postJoin()
        }
    }

    //프래그먼트 데이터 전달
    fun signup1(
        id: String,
        password: String,
        passwordCheck: String,
        nickname: String,
        isTextFilled: Boolean
    ) {
        Log.d("logFish", "$id $password $passwordCheck $nickname $isTextFilled")
        _memberId = id
        _password = password
        _passwordCheck = passwordCheck
        _nickname = nickname
        _signup1Filled = isTextFilled
        if (_signup1Filled && _signup2Filled && _signup3Filled) {
            binding.buttonSignup.isClickable = true
            binding.buttonSignup.isEnabled = true
        } else {
            binding.buttonSignup.isClickable = false
            binding.buttonSignup.isEnabled = false
        }
    }

    //프래그먼트 데이터 전달
    fun signup2(school: String, image: String, grade: String, isTextFilled: Boolean) {
        Log.d("logFish", "$school $image $grade $isTextFilled")
        _school = school
        _photo = image
        _grade = grade
        _signup2Filled = isTextFilled
        if (_signup1Filled && _signup2Filled && _signup3Filled) {
            binding.buttonSignup.isClickable = true
            binding.buttonSignup.isEnabled = true
        } else {
            binding.buttonSignup.isClickable = false
            binding.buttonSignup.isEnabled = false
        }
    }

    //프래그먼트 데이터 전달
    fun signup3(name: String, phone: String, certificationNumber: String, isTextFilled: Boolean) {
        Log.d("logFish", "$name $phone $certificationNumber $isTextFilled")
        _name = name
        _phone = phone
        _certificationNumber = certificationNumber
        _signup3Filled = isTextFilled
        if (_signup1Filled && _signup2Filled && _signup3Filled) {
            binding.buttonSignup.isClickable = true
            binding.buttonSignup.isEnabled = true
        } else {
            binding.buttonSignup.isClickable = false
            binding.buttonSignup.isEnabled = false
        }
    }

    //회원가입
    private fun postJoin() {
        viewModel.postJoin(
            _photo,
            _memberId,
            _password,
            _nickname,
            _school,
            _grade,
            _name,
            _phone,
            _adNotifications
        )
        viewModel.joinSuccess.observe(this, Observer {
            if (it) {
                val intent = Intent(mContext, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        })
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }

    private fun viewPagerAdapter() {
        //Adapter 안에 ViewPager2 상에 띄워줄 fragmentList 생성
        val fragmentList = listOf(Signup1(), Signup2(), Signup3())

        //ViewPagerAdapter 초기화
        SignupViewPagerAdapter = SignupViewPagerAdapter(this)
        SignupViewPagerAdapter.fragments.addAll(fragmentList)

        //ViewPager2와 Adapter 연동
        binding.viewpagerSignup.adapter = SignupViewPagerAdapter
        binding.viewpagerSignupIndicator.attachTo(binding.viewpagerSignup)
    }
}