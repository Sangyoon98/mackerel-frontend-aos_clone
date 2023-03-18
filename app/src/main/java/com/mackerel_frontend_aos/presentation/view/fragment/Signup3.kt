package com.mackerel_frontend_aos.presentation.view.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mackerel_frontend_aos.data.repository.ExceptionDialog
import com.mackerel_frontend_aos.databinding.FragmentSignup3Binding
import com.mackerel_frontend_aos.presentation.view.activity.Signup
import com.mackerel_frontend_aos.presentation.viewmodel.Signup1ViewModel
import com.mackerel_frontend_aos.presentation.viewmodel.Signup3ViewModel
import java.util.regex.Pattern

class Signup3 : Fragment() {
    private var mBinding: FragmentSignup3Binding? = null
    private val binding get() = mBinding!!
    lateinit var signUp: Signup
    lateinit var viewModel: Signup3ViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        signUp = context as Signup
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentSignup3Binding.inflate(inflater, container, false)

        //Dialog
        viewModel  = ViewModelProvider(this)[Signup3ViewModel::class.java]
        viewModel.dialog.observe(signUp, Observer {
            if(it != null) {
                ExceptionDialog(signUp, it.header, it.body).showDialog()
            }
        })

        binding.edittextSignupName.errorIconDrawable = null

        binding.edittextSignupInputName.doOnTextChanged { p0: CharSequence?, p1: Int, p2: Int, p3: Int -> isEditTextFilledName() }
        binding.edittextSignupInputPhoneNumber.doOnTextChanged { p0: CharSequence?, p1: Int, p2: Int, p3: Int -> isEditTextFilledPhone() }
        binding.edittextSignupInputCertificationNumber.doOnTextChanged { p0: CharSequence?, p1: Int, p2: Int, p3: Int -> isEditTextFilledPhone() }

        viewModel.timerCount.observe(viewLifecycleOwner, Observer {
            val second = it % 60
            val minute = it / 60
            binding.edittextSignupCertificationNumber.suffixText = String.format("$minute:%02d", second)
            if (it == 0) {
                binding.edittextSignupCertificationNumber.suffixText = String.format("0:00")
                Toast.makeText(context, "인증 시간이 만료되었습니다.", Toast.LENGTH_SHORT).show()
                binding.edittextSignupCertificationNumber.isEnabled = false
                binding.buttonSignupCheckCertification.isEnabled = false
            }
        })

        binding.buttonSignupSendCertification.setOnClickListener {
            binding.layoutSignupCertificationNumber.visibility = View.VISIBLE
            binding.buttonSignupSendCertification.text = "인증번호\n재전송"
            binding.edittextSignupCertificationNumber.isEnabled = true
            binding.edittextSignupInputCertificationNumber.requestFocus()
            viewModel.timerStart()
            postSendCertificationNumber(binding.edittextSignupInputPhoneNumber.text.toString())
        }

        binding.buttonSignupCheckCertification.setOnClickListener {
            postConfirmsCertificationNumber(
                binding.edittextSignupInputPhoneNumber.text.toString(),
                binding.edittextSignupInputCertificationNumber.text.toString().toInt()
            )
        }

        return binding.root
    }

    //EditText 내용 전달
    private fun isEditTextFilledName() {
        val name = binding.edittextSignupInputName.text.toString()
        val phoneNumber = binding.edittextSignupInputPhoneNumber.text.toString()
        val certificationNumber = binding.edittextSignupInputCertificationNumber.text.toString()
        val isTextFilled =
            name.isNotEmpty() && phoneNumber.isNotEmpty() && certificationNumber.isNotEmpty()

        //nickname check
        when {
            (!Pattern.matches("^[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣|\\s]*$", name)) -> binding.edittextSignupName.error = "특수문자가 포함이 되어 있습니다."
            (!Pattern.matches("^\\S*$", name)) -> binding.edittextSignupName.error = "공백이 포함되어 있습니다."
            else -> binding.edittextSignupName.isErrorEnabled = false
        }

        //send data
        (activity as Signup).signup3(name, phoneNumber, certificationNumber, isTextFilled)
    }

    //EditText 내용 전달
    private fun isEditTextFilledPhone() {
        val name = binding.edittextSignupInputName.text.toString()
        val phoneNumber = binding.edittextSignupInputPhoneNumber.text.toString()
        val certificationNumber = binding.edittextSignupInputCertificationNumber.text.toString()
        val isTextFilled =
            name.isNotEmpty() && phoneNumber.isNotEmpty() && certificationNumber.isNotEmpty()

        if (phoneNumber.length == 11) {
            binding.buttonSignupSendCertification.isEnabled = true
            binding.buttonSignupSendCertification.isClickable = true
            binding.edittextSignupInputCertificationNumber.isEnabled = true
        } else {
            binding.buttonSignupSendCertification.isEnabled = false
            binding.buttonSignupSendCertification.isClickable = false
            binding.buttonSignupCheckCertification.isEnabled = false
            binding.buttonSignupCheckCertification.isClickable = false
            binding.edittextSignupInputCertificationNumber.isEnabled = false
        }

        if (certificationNumber.length == 6) {
            binding.buttonSignupCheckCertification.isEnabled = true
            binding.buttonSignupCheckCertification.isClickable = true
        } else {
            binding.buttonSignupCheckCertification.isEnabled = false
            binding.buttonSignupCheckCertification.isClickable = false
        }

        //send data
        (activity as Signup).signup3(name, phoneNumber, certificationNumber, isTextFilled)
    }

    //핸드폰 인증번호 전송
    private fun postSendCertificationNumber(phone: String) {
        viewModel.postSendCertificationNumber(phone)
        viewModel.sendCertificationNumber.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.edittextSignupInputPhoneNumber.isEnabled = false
            }
        })
    }

    //핸드폰 인증번호 확인
    private fun postConfirmsCertificationNumber(phone: String, number: Int) {
        viewModel.postConfirmsCertificationNumber(phone, number)
        viewModel.confirmsCertificationNumber.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.timerStop()
                binding.edittextSignupInputPhoneNumber.isEnabled = false
                binding.edittextSignupInputCertificationNumber.isEnabled = false
                binding.buttonSignupCheckCertification.isEnabled = false
                binding.buttonSignupSendCertification.isEnabled = false
                Toast.makeText(context, "인증이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}