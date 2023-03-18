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
import com.mackerel_frontend_aos.databinding.ActivityFindIdBinding
import com.mackerel_frontend_aos.presentation.viewmodel.FindIdViewModel

class FindId : AppCompatActivity() {
    private var mBinding: ActivityFindIdBinding? = null
    private val binding get() = mBinding!!
    lateinit var mContext: Context
    lateinit var viewModel: FindIdViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFindIdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this@FindId

        //Dialog
        viewModel  = ViewModelProvider(this)[FindIdViewModel::class.java]
        viewModel.dialog.observe(this, Observer {
            if(it != null) {
                ExceptionDialog(this, it.header, it.body).showDialog()
            }
        })

        //TextWatcher
        binding.edittextFindIdInputName.doOnTextChanged { p0: CharSequence?, p1: Int, p2: Int, p3: Int -> isEditTextFilled() }
        binding.edittextFindIdInputPhoneNumber.doOnTextChanged { p0: CharSequence?, p1: Int, p2: Int, p3: Int -> isEditTextFilled() }
        binding.edittextFindIdInputCertificationNumber.doOnTextChanged { p0: CharSequence?, p1: Int, p2: Int, p3: Int -> isEditTextFilled() }

        viewModel.timerCount.observe(this, Observer {
            val second = it % 60
            val minute = it / 60
            binding.edittextFindIdCertificationNumber.suffixText = String.format("$minute:%02d", second)
            if (it == 0) {
                binding.edittextFindIdCertificationNumber.suffixText = String.format("0:00")
                Toast.makeText(applicationContext, "인증 시간이 만료되었습니다.", Toast.LENGTH_SHORT).show()
                binding.edittextFindIdCertificationNumber.isEnabled = false
                binding.buttonFindIdCheckCertification.isEnabled = false
            }
        })

        binding.buttonFindIdSendCertification.setOnClickListener {
            binding.layoutFindIdCertificationNumber.visibility = View.VISIBLE
            binding.buttonFindIdSendCertification.text = "인증번호\n재전송"
            binding.edittextFindIdCertificationNumber.isEnabled = true
            binding.edittextFindIdInputCertificationNumber.requestFocus()
            viewModel.timerStart()
            postSendCertificationNumber(binding.edittextFindIdInputPhoneNumber.text.toString())
        }

        binding.buttonFindIdCheckCertification.setOnClickListener {
            postConfirmsCertificationNumber(
                binding.edittextFindIdInputPhoneNumber.text.toString(),
                binding.edittextFindIdInputCertificationNumber.text.toString().toInt()
            )
        }

        //액티비티 이동
        binding.buttonFindId.setOnClickListener {
            val name = binding.edittextFindIdInputName.text.toString()
            val phone = binding.edittextFindIdInputPhoneNumber.text.toString()
            getFindId(name, phone)
        }
    }

    //EditText 모두 작성시 버튼 활성화
    private fun isEditTextFilled() {
        val name = binding.edittextFindIdInputName.text.toString()
        val phoneNumber = binding.edittextFindIdInputPhoneNumber.text.toString()
        val certificationNumber = binding.edittextFindIdInputCertificationNumber.text.toString()

        if (phoneNumber.length == 11) {
            binding.buttonFindIdSendCertification.isEnabled = true
            binding.buttonFindIdSendCertification.isClickable = true
            binding.edittextFindIdInputCertificationNumber.isEnabled = true
        } else {
            binding.buttonFindIdSendCertification.isEnabled = false
            binding.buttonFindIdSendCertification.isClickable = false
            binding.buttonFindIdCheckCertification.isEnabled = false
            binding.buttonFindIdCheckCertification.isClickable = false
            binding.edittextFindIdInputCertificationNumber.isEnabled = false
        }

        if (certificationNumber.length == 6) {
            binding.buttonFindIdCheckCertification.isEnabled = true
            binding.buttonFindIdCheckCertification.isClickable = true
        } else {
            binding.buttonFindIdCheckCertification.isEnabled = false
            binding.buttonFindIdCheckCertification.isClickable = false
        }

        if (name.isNotEmpty() && phoneNumber.isNotEmpty() && certificationNumber.isNotEmpty()) {
            binding.buttonFindId.isClickable = true
            binding.buttonFindId.isEnabled = true
        } else {
            binding.buttonFindId.isClickable = false
            binding.buttonFindId.isEnabled = false
        }
    }

    //핸드폰 인증번호 전송
    private fun postSendCertificationNumber(phone: String) {
        viewModel.postSendCertificationNumber(phone)
        viewModel.sendCertificationNumber.observe(this, Observer {
            if (it) {
                binding.edittextFindIdInputPhoneNumber.isEnabled = false
            }
        })
    }

    //핸드폰 인증번호 확인
    private fun postConfirmsCertificationNumber(phone: String, number: Int) {
        viewModel.postConfirmsCertificationNumber(phone, number)
        viewModel.confirmsCertificationNumber.observe(this, Observer {
            if (it) {
                viewModel.timerStop()
                binding.edittextFindIdInputPhoneNumber.isEnabled = false
                binding.edittextFindIdInputCertificationNumber.isEnabled = false
                binding.buttonFindIdCheckCertification.isEnabled = false
                binding.buttonFindIdSendCertification.isEnabled = false
                Toast.makeText(mContext, "인증이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //로그인 찾기
    private fun getFindId(name: String, phone: String) {
        viewModel.getFindId(name, phone)
        viewModel.findId.observe(this, Observer {
            if (it) {
                viewModel.findIdMemberId.observe(this, Observer { memberId ->
                    if (memberId != "") {
                        viewModel.findIdCreateAt.observe(this, Observer { memberCreateAt ->
                            if (memberCreateAt != "") {
                                val intent = Intent(mContext, FindIdResult::class.java)
                                intent.putExtra("memberId", memberId)
                                intent.putExtra("joinAt", memberCreateAt)
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