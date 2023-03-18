package com.mackerel_frontend_aos.presentation.view.activity

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.mackerel_frontend_aos.databinding.ActivityTermsBinding

class Terms : AppCompatActivity() {
    private var mBinding: ActivityTermsBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTermsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.textviewTerms2.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        binding.checkboxTermsAllAccept.setOnClickListener { onCheckChanged(binding.checkboxTermsAllAccept) }
        binding.checkboxTermsTerms1.setOnClickListener { onCheckChanged(binding.checkboxTermsTerms1) }
        binding.checkboxTermsTerms2.setOnClickListener { onCheckChanged(binding.checkboxTermsTerms2) }
        binding.checkboxTermsTermsChoose.setOnClickListener { onCheckChanged(binding.checkboxTermsTermsChoose) }

        binding.textviewTerms2.setOnClickListener {
            val intent = Intent(this, TermsDetail::class.java)
            startActivity(intent)
        }

        binding.buttonTerms.setOnClickListener {
            val termsChoose = binding.checkboxTermsTermsChoose.isChecked
            val intent = Intent(this, Signup::class.java)
            intent.putExtra("adNotifications", termsChoose)
            startActivity(intent)
        }
    }

    private fun onCheckChanged(compoundButton: CompoundButton) {
        when (compoundButton) {
            binding.checkboxTermsAllAccept -> {
                if (binding.checkboxTermsAllAccept.isChecked) {
                    binding.checkboxTermsTerms1.isChecked = true
                    binding.checkboxTermsTerms2.isChecked = true
                    binding.checkboxTermsTermsChoose.isChecked = true
                    binding.buttonTerms.isClickable = true
                    binding.buttonTerms.isEnabled = true
                } else {
                    binding.checkboxTermsTerms1.isChecked = false
                    binding.checkboxTermsTerms2.isChecked = false
                    binding.checkboxTermsTermsChoose.isChecked = false
                    binding.buttonTerms.isClickable = false
                    binding.buttonTerms.isEnabled = false
                }
            }
            else -> {
                binding.checkboxTermsAllAccept.isChecked = (
                        binding.checkboxTermsTerms1.isChecked
                                && binding.checkboxTermsTerms2.isChecked
                                && binding.checkboxTermsTermsChoose.isChecked
                        )

                binding.buttonTerms.isClickable = (
                        binding.checkboxTermsTerms1.isChecked
                                && binding.checkboxTermsTerms2.isChecked
                        )

                binding.buttonTerms.isEnabled = (
                        binding.checkboxTermsTerms1.isChecked
                                && binding.checkboxTermsTerms2.isChecked
                        )
            }
        }
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}