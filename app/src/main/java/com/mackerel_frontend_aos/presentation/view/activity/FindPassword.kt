package com.mackerel_frontend_aos.presentation.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mackerel_frontend_aos.data.repository.ExceptionDialog
import com.mackerel_frontend_aos.databinding.ActivityFindPasswordBinding
import com.mackerel_frontend_aos.presentation.viewmodel.FindPasswordViewModel

class FindPassword : AppCompatActivity() {
    private var mBinding: ActivityFindPasswordBinding? = null
    private val binding get() = mBinding!!
    lateinit var mContext: Context
    lateinit var viewModel: FindPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFindPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this@FindPassword

        //Dialog
        viewModel  = ViewModelProvider(this)[FindPasswordViewModel::class.java]
        viewModel.dialog.observe(this, Observer {
            if(it != null) {
                ExceptionDialog(this, it.header, it.body).showDialog()
            }
        })

        //TextWatcher
        binding.edittextFindPasswordInputId.doOnTextChanged { p0: CharSequence?, p1: Int, p2: Int, p3: Int -> isEditTextFilled() }
        binding.edittextFindPasswordInputName.doOnTextChanged { p0: CharSequence?, p1: Int, p2: Int, p3: Int -> isEditTextFilled() }
        binding.edittextFindPasswordInputPhoneNumber.doOnTextChanged { p0: CharSequence?, p1: Int, p2: Int, p3: Int -> isEditTextFilled() }
        binding.edittextFindPasswordInputCertificationNumber.doOnTextChanged { p0: CharSequence?, p1: Int, p2: Int, p3: Int -> isEditTextFilled() }

        viewModel.timerCount.observe(this, Observer {
            val second = it % 60
            val minute = it / 60
            binding.edittextFindPasswordCertificationNumber.suffixText = String.format("$minute:%02d", second)
            if (it == 0) {
                binding.edittextFindPasswordCertificationNumber.suffixText = String.format("0:00")
                Toast.makeText(applicationContext, "인증 시간이 만료되었습니다.", Toast.LENGTH_SHORT).show()
                binding.edittextFindPasswordCertificationNumber.isEnabled = false
                binding.buttonFindPasswordCheckCertification.isEnabled = false
            }
        })

        binding.buttonFindPasswordSendCertification.setOnClickListener {
            binding.layoutFindPasswordCertificationNumber.visibility = View.VISIBLE
            binding.buttonFindPasswordSendCertification.text = "인증번호 재전송"
            binding.edittextFindPasswordCertificationNumber.isEnabled = true
            binding.edittextFindPasswordInputCertificationNumber.requestFocus()
            viewModel.timerStart()
            postSendCertificationNumber(binding.edittextFindPasswordInputPhoneNumber.text.toString())
        }

        binding.buttonFindPasswordCheckCertification.setOnClickListener {
            postConfirmsCertificationNumber(
                binding.edittextFindPasswordInputPhoneNumber.text.toString(),
                binding.edittextFindPasswordInputCertificationNumber.text.toString().toInt()
            )
        }

        //액티비티 이동
        binding.buttonFindPassword.setOnClickListener {
            val memberId = binding.edittextFindPasswordInputId.text.toString()
            val name = binding.edittextFindPasswordInputName.text.toString()
            val phoneNumber = binding.edittextFindPasswordInputPhoneNumber.text.toString()
            postFindPasswordGetToken(memberId, name, phoneNumber)
        }
    }

    //EditText 모두 작성시 버튼 활성화
    private fun isEditTextFilled() {
        val id = binding.edittextFindPasswordInputId.text.toString()
        val name = binding.edittextFindPasswordInputName.text.toString()
        val phoneNumber = binding.edittextFindPasswordInputPhoneNumber.text.toString()
        val certificationNumber =
            binding.edittextFindPasswordInputCertificationNumber.text.toString()

        if (phoneNumber.length == 11) {
            binding.buttonFindPasswordSendCertification.isEnabled = true
            binding.buttonFindPasswordSendCertification.isClickable = true
            binding.edittextFindPasswordInputCertificationNumber.isEnabled = true
        } else {
            binding.buttonFindPasswordSendCertification.isEnabled = false
            binding.buttonFindPasswordSendCertification.isClickable = false
            binding.buttonFindPasswordCheckCertification.isEnabled = false
            binding.buttonFindPasswordCheckCertification.isClickable = false
            binding.edittextFindPasswordInputCertificationNumber.isEnabled = false
        }

        if (certificationNumber.length == 6) {
            binding.buttonFindPasswordCheckCertification.isEnabled = true
            binding.buttonFindPasswordCheckCertification.isClickable = true
        } else {
            binding.buttonFindPasswordCheckCertification.isEnabled = false
            binding.buttonFindPasswordCheckCertification.isClickable = false
        }

        if (id.isNotEmpty() && name.isNotEmpty() && phoneNumber.isNotEmpty() && certificationNumber.isNotEmpty()) {
            binding.buttonFindPassword.isClickable = true
            binding.buttonFindPassword.isEnabled = true
        } else {
            binding.buttonFindPassword.isClickable = false
            binding.buttonFindPassword.isEnabled = false
        }
    }

    //핸드폰 인증번호 전송
    private fun postSendCertificationNumber(phone: String) {
        viewModel.postSendCertificationNumber(phone)
        viewModel.sendCertificationNumber.observe(this, Observer {
            if (it) {
                //time?.cancel()
                binding.edittextFindPasswordInputPhoneNumber.isEnabled = false
            }
        })
    }

    //핸드폰 인증번호 확인
    private fun postConfirmsCertificationNumber(phone: String, number: Int) {
        viewModel.postConfirmsCertificationNumber(phone, number)
        viewModel.confirmsCertificationNumber.observe(this, Observer {
            if (it) {
                //time?.cancel()
                viewModel.timerStop()
                binding.edittextFindPasswordInputPhoneNumber.isEnabled = false
                binding.edittextFindPasswordInputCertificationNumber.isEnabled = false
                binding.buttonFindPasswordCheckCertification.isEnabled = false
                binding.buttonFindPasswordSendCertification.isEnabled = false
                Toast.makeText(mContext, "인증이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //비밀번호 변경 권한 토큰 발급 통신
    private fun postFindPasswordGetToken(memberId: String, name: String, phone: String) {
        viewModel.postFindPasswordGetToken(memberId, name, phone)
        viewModel.findPasswordGetToken.observe(this, Observer {
            if (it) {
                viewModel.findPasswordGetTokenAuthToken.observe(this, Observer { authToken ->
                    if (authToken != "") {
                        viewModel.findPasswordGetTokenAuthTokenExpiresIn.observe(this, Observer { authTokenExpiresIn ->
                            if (authTokenExpiresIn != null) {
                                val intent = Intent(mContext, FindPasswordResult::class.java)
                                intent.putExtra("passwordAuthToken", authToken)
                                intent.putExtra("passwordAuthTokenExpiresIn", authTokenExpiresIn)
                                startActivity(intent)
                            }
                        })
                    }
                })
            }
        })
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}