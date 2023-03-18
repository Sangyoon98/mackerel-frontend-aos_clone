package com.mackerel_frontend_aos.presentation.view.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mackerel_frontend_aos.data.repository.ExceptionDialog
import com.mackerel_frontend_aos.databinding.ActivityFindPasswordResultBinding
import com.mackerel_frontend_aos.presentation.viewmodel.FindPasswordResultViewModel
import com.mackerel_frontend_aos.presentation.viewmodel.FindPasswordViewModel
import java.util.*
import java.util.regex.Pattern

class FindPasswordResult : AppCompatActivity() {
    private var mBinding: ActivityFindPasswordResultBinding? = null
    private val binding get() = mBinding!!
    lateinit var mContext: Context
    private var passwordAuthToken = ""
    private var passwordAuthTokenExpiresIn = 0
    lateinit var viewModel: FindPasswordResultViewModel

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFindPasswordResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this@FindPasswordResult

        //Dialog
        viewModel  = ViewModelProvider(this)[FindPasswordResultViewModel::class.java]
        viewModel.dialog.observe(this, Observer {
            if(it != null) {
                ExceptionDialog(this, it.header, it.body).showDialog()
            }
        })

        binding.edittextFindPasswordResultPassword.errorIconDrawable = null
        binding.edittextFindPasswordResultPasswordCheck.errorIconDrawable = null

        passwordAuthToken = intent.getStringExtra("passwordAuthToken")!!
        passwordAuthTokenExpiresIn = intent.getIntExtra("passwordAuthTokenExpiresIn", 0)

        //TextWatcher
        binding.edittextFindPasswordResultInputPassword.doOnTextChanged { p0: CharSequence?, p1: Int, p2: Int, p3: Int -> isEditTextFilled() }
        binding.edittextFindPasswordResultInputPasswordCheck.doOnTextChanged { p0: CharSequence?, p1: Int, p2: Int, p3: Int -> isEditTextFilled() }

        viewModel.timerStart()
        viewModel.timerCount.observe(this, Observer {
            Log.d("logFish", "${it / 60}분 ${it % 60}초")
            if (it == 0) {
                Toast.makeText(applicationContext, "인증 시간이 만료되었습니다.", Toast.LENGTH_SHORT).show()
                viewModel.timerStop()
            }
        })

        //액티비티 이동
        binding.buttonFindPasswordResult.setOnClickListener {
            putFindPasswordFixPassword(binding.edittextFindPasswordResultInputPassword.text.toString())
        }
    }

    //EditText 모두 작성시 버튼 활성화
    @RequiresApi(Build.VERSION_CODES.M)
    fun isEditTextFilled() {
        var password = binding.edittextFindPasswordResultInputPassword.text.toString()
        var passwordCheck = binding.edittextFindPasswordResultInputPasswordCheck.text.toString()

        if (password.length < 8) {
            //8자리 이상
            binding.edittextFindPasswordResultPassword.error = "비밀번호는 8~15글자의 영문과 숫자이어야 합니다."
        } else {
            val pattern = "^[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣|\\s]*$"
            if (Pattern.matches(pattern, password)) {
                //특수문자 미포함시
                binding.edittextFindPasswordResultPassword.error = "특수문자를 포함해야 합니다."
            } else {
                //조건 만족시
                binding.edittextFindPasswordResultPassword.isErrorEnabled = false
                if (!password.equals(passwordCheck)) {
                    //비밀번호 불일치시
                    binding.edittextFindPasswordResultPasswordCheck.error = "비밀번호가 일치하지 않습니다."
                } else {
                    binding.edittextFindPasswordResultPasswordCheck.isErrorEnabled = false
                    if (password.isNotEmpty() && passwordCheck.isNotEmpty()) {
                        binding.buttonFindPasswordResult.isClickable = true
                        binding.buttonFindPasswordResult.isEnabled = true

                    } else {
                        binding.buttonFindPasswordResult.isClickable = false
                        binding.buttonFindPasswordResult.isEnabled = false
                    }
                }
            }
        }
    }

    //비밀번호 수정 통신
    private fun putFindPasswordFixPassword(password: String) {
        viewModel.putFindPasswordFixPassword(passwordAuthToken, password)
        viewModel.findPasswordFixPassword.observe(this, Observer {
            if (it) {
                Toast.makeText(mContext, "비밀번호 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show()
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
}