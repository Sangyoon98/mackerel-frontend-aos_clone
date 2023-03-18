package com.mackerel_frontend_aos.presentation.view.fragment

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mackerel_frontend_aos.R
import com.mackerel_frontend_aos.data.repository.ExceptionDialog
import com.mackerel_frontend_aos.databinding.FragmentSignup1Binding
import com.mackerel_frontend_aos.presentation.view.activity.Signup
import com.mackerel_frontend_aos.presentation.viewmodel.FindPasswordResultViewModel
import com.mackerel_frontend_aos.presentation.viewmodel.Signup1ViewModel
import java.util.regex.Pattern


class Signup1 : Fragment() {
    private var mBinding: FragmentSignup1Binding? = null
    private val binding get() = mBinding!!
    lateinit var signUp: Signup
    lateinit var viewModel: Signup1ViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        signUp = context as Signup
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentSignup1Binding.inflate(inflater, container, false)

        //Dialog
        viewModel  = ViewModelProvider(this)[Signup1ViewModel::class.java]
        viewModel.dialog.observe(signUp, Observer {
            if(it != null) {
                ExceptionDialog(signUp, it.header, it.body).showDialog()
            }
        })

        binding.edittextSignupId.errorIconDrawable = null
        binding.edittextSignupPassword.errorIconDrawable = null
        binding.edittextSignupPasswordCheck.errorIconDrawable = null
        binding.edittextSignupNickname.errorIconDrawable = null

        //텍스트 실시간 Signup Activity 전송
        binding.edittextSignupInputId.doOnTextChanged { p0: CharSequence?, p1: Int, p2: Int, p3: Int -> isEditTextFilledId() }
        binding.edittextSignupInputPassword.doOnTextChanged { p0: CharSequence?, p1: Int, p2: Int, p3: Int -> isEditTextFilledPassword() }
        binding.edittextSignupInputPasswordCheck.doOnTextChanged { p0: CharSequence?, p1: Int, p2: Int, p3: Int -> isEditTextFilledPassword() }
        binding.edittextSignupInputNickname.doOnTextChanged { p0: CharSequence?, p1: Int, p2: Int, p3: Int -> isEditTextFilledNickname() }

        return binding.root
    }

    //EditText 내용 전달
    private fun isEditTextFilledId() {
        val id = binding.edittextSignupInputId.text.toString()
        val password = binding.edittextSignupInputPassword.text.toString()
        val passwordCheck = binding.edittextSignupInputPasswordCheck.text.toString()
        val nickname = binding.edittextSignupInputNickname.text.toString()

        //id check
        when {
            (id.length < 4) -> binding.edittextSignupId.error = "아이디는 4~15글자의 영문과 숫자이어야 합니다."
            (!Pattern.matches("^[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣|\\s]*$", id)) -> binding.edittextSignupId.error = "아이디는 특수문자 사용이 안됩니다."
            (!Pattern.matches("^\\S*$", id)) -> binding.edittextSignupId.error = "공백이 포함되어 있습니다."
            else -> getIdExist(id)
        }

        val isTextFilled = id.isNotEmpty() &&
                password.isNotEmpty() &&
                passwordCheck.isNotEmpty() &&
                nickname.isNotEmpty() &&
                !binding.edittextSignupId.isErrorEnabled &&
                !binding.edittextSignupPassword.isErrorEnabled &&
                !binding.edittextSignupPasswordCheck.isErrorEnabled &&
                !binding.edittextSignupNickname.isErrorEnabled

        //send data
        (activity as Signup).signup1(id, password, passwordCheck, nickname, isTextFilled)
    }

    private fun isEditTextFilledPassword() {
        val id = binding.edittextSignupInputId.text.toString()
        val password = binding.edittextSignupInputPassword.text.toString()
        val passwordCheck = binding.edittextSignupInputPasswordCheck.text.toString()
        val nickname = binding.edittextSignupInputNickname.text.toString()

        //password
        when {
            (password.length < 8) -> binding.edittextSignupPassword.error = "비밀번호는 8~15글자의 영문과 숫자이어야 합니다."
            (Pattern.matches("^[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣]*$", password)) -> binding.edittextSignupPassword.error = "특수문자를 포함해야 합니다."
            (!Pattern.matches("^\\S*$", password)) -> binding.edittextSignupPassword.error = "공백이 포함되어 있습니다."
            else -> {
                binding.edittextSignupPassword.isErrorEnabled = false
                //password check
                when {
                    (!password.equals(passwordCheck)) -> binding.edittextSignupPasswordCheck.error = "비밀번호가 일치하지 않습니다."
                    else -> binding.edittextSignupPasswordCheck.isErrorEnabled = false
                }
            }
        }

        val isTextFilled = id.isNotEmpty() &&
                password.isNotEmpty() &&
                passwordCheck.isNotEmpty() &&
                nickname.isNotEmpty() &&
                !binding.edittextSignupId.isErrorEnabled &&
                !binding.edittextSignupPassword.isErrorEnabled &&
                !binding.edittextSignupPasswordCheck.isErrorEnabled &&
                !binding.edittextSignupNickname.isErrorEnabled

        //send data
        (activity as Signup).signup1(id, password, passwordCheck, nickname, isTextFilled)
    }

    private fun isEditTextFilledNickname() {
        val id = binding.edittextSignupInputId.text.toString()
        val password = binding.edittextSignupInputPassword.text.toString()
        val passwordCheck = binding.edittextSignupInputPasswordCheck.text.toString()
        val nickname = binding.edittextSignupInputNickname.text.toString()

        //nickname check
        when {
            (nickname.length < 2) -> binding.edittextSignupNickname.error = "닉네임은 2~12글자만 허용합니다."
            (!Pattern.matches("^[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣|\\s]*$", nickname)) -> binding.edittextSignupNickname.error = "닉네임은 특수문자 사용이 안됩니다."
            (!Pattern.matches("^\\S*$", nickname)) -> binding.edittextSignupNickname.error = "공백이 포함되어 있습니다."
            (nickname.isNotEmpty()) -> getNicknameExist(nickname)
        }

        val isTextFilled = id.isNotEmpty() &&
                password.isNotEmpty() &&
                passwordCheck.isNotEmpty() &&
                nickname.isNotEmpty() &&
                !binding.edittextSignupId.isErrorEnabled &&
                !binding.edittextSignupPassword.isErrorEnabled &&
                !binding.edittextSignupPasswordCheck.isErrorEnabled &&
                !binding.edittextSignupNickname.isErrorEnabled

        //send data
        (activity as Signup).signup1(id, password, passwordCheck, nickname, isTextFilled)
    }

    //아이디 존재 여부 확인
    private fun getIdExist(id: String) {
        viewModel.getIdExist(id)
        viewModel.idIsExist.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.edittextSignupId.error = "이미 존재하는 아이디 입니다."
            } else {
                binding.edittextSignupId.isErrorEnabled = false
                binding.edittextSignupId.isHelperTextEnabled = true
                binding.edittextSignupId.helperText = "사용 가능한 아이디 입니다."
                binding.edittextSignupId.setHelperTextColor(
                    ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.successColor
                    )
                )
                binding.edittextSignupId.boxStrokeColor = ContextCompat.getColor(
                    requireContext(), R.color.highSchoolFish_Main
                )
            }
        })
    }

    //닉네임 존재 여부 확인
    private fun getNicknameExist(nickname: String) {
        viewModel.getNicknameExist(nickname)
        viewModel.nicknameIsExist.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.edittextSignupNickname.error = "이미 존재하는 닉네임 입니다."
            } else {
                binding.edittextSignupNickname.isErrorEnabled = false
                binding.edittextSignupNickname.isHelperTextEnabled = true
                binding.edittextSignupNickname.helperText = "사용 가능한 닉네임 입니다."
                binding.edittextSignupNickname.setHelperTextColor(
                    ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.successColor
                    )
                )
                binding.edittextSignupNickname.boxStrokeColor = ContextCompat.getColor(
                    requireContext(), R.color.highSchoolFish_Main
                )
            }
        })
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}