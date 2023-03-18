package com.mackerel_frontend_aos.presentation.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mackerel_frontend_aos.data.repository.ExceptionDialog
import com.mackerel_frontend_aos.data.repository.shared_preference.SharedPreferenceContext
import com.mackerel_frontend_aos.databinding.ActivityLoginBinding
import com.mackerel_frontend_aos.presentation.viewmodel.LoginViewModel

class Login : AppCompatActivity() {
    private var mBinding: ActivityLoginBinding? = null
    private val binding get() = mBinding!!
    lateinit var mContext: Context
    lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this@Login

        //Dialog
        viewModel  = ViewModelProvider(this)[LoginViewModel::class.java]
        viewModel.dialog.observe(this, Observer {
            if(it != null) {
                ExceptionDialog(this, it.header, it.body).showDialog()
            }
        })

        //자동 로그인 선택한 유저
        if (SharedPreferenceContext.preference.getPreference("autoLoginId") != "" && SharedPreferenceContext.preference.getPreference("autoLoginPassword") != "") {
            binding.checkboxAutoLogin.isChecked = true
            val memberId = SharedPreferenceContext.preference.getPreference("autoLoginId")
            val password = SharedPreferenceContext.preference.getPreference("autoLoginPassword")
            viewModel.postLogin(memberId, password)
            val intent = Intent(mContext, MainActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        //TextWatcher
        binding.edittextLoginInputId.doOnTextChanged { p0: CharSequence?, p1: Int, p2: Int, p3: Int -> isEditTextFilled() }
        binding.edittextLoginInputPassword.doOnTextChanged { p0: CharSequence?, p1: Int, p2: Int, p3: Int -> isEditTextFilled() }

        //액티비티 이동
        binding.buttonLogin.setOnClickListener {
            postLogin()
        }

        binding.buttonFindId.setOnClickListener {
            val intent = Intent(this, FindId::class.java)
            startActivity(intent)
        }

        binding.buttonFindPassword.setOnClickListener {
            val intent = Intent(this, FindPassword::class.java)
            startActivity(intent)
        }

        binding.buttonSignup.setOnClickListener {
            val intent = Intent(this, Terms::class.java)
            startActivity(intent)
        }
    }

    //EditText 모두 작성시 버튼 활성화
    private fun isEditTextFilled() {
        val id = binding.edittextLoginInputId.text.toString()
        val password = binding.edittextLoginInputPassword.text.toString()

        if (id.isNotEmpty() && password.isNotEmpty()) {
            binding.buttonLogin.isClickable = true
            binding.buttonLogin.isEnabled = true
        } else {
            binding.buttonLogin.isClickable = false
            binding.buttonLogin.isEnabled = false
        }
    }

    private fun postLogin() {
        val memberId = binding.edittextLoginInputId.text.toString()
        val password = binding.edittextLoginInputPassword.text.toString()

        viewModel.postLogin(memberId, password)
        viewModel.loginSuccess.observe(this, Observer {
            if(it) {
                if (binding.checkboxAutoLogin.isChecked) {
                    SharedPreferenceContext.preference.setPreference("autoLoginId", memberId)
                    SharedPreferenceContext.preference.setPreference("autoLoginPassword", password)
                } else {
                    SharedPreferenceContext.preference.setPreference("autoLoginId", "")
                    SharedPreferenceContext.preference.setPreference("autoLoginPassword", "")
                }

                val intent = Intent(mContext, MainActivity::class.java)
                startActivity(intent)
                this@Login.finish()
            }
        })
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}