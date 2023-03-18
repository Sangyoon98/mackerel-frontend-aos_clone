package com.mackerel_frontend_aos.presentation.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mackerel_frontend_aos.databinding.ActivityFindIdResultBinding

class FindIdResult : AppCompatActivity() {
    private var mBinding: ActivityFindIdResultBinding? = null
    private val binding get() = mBinding!!
    lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFindIdResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this@FindIdResult

        binding.textviewFindIdMemberId.text = intent.getStringExtra("memberId")
        binding.textviewFindIdJoinAt.text = intent.getStringExtra("joinAt")

        //액티비티 이동
        binding.buttonFindIdResultToLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.buttonFindIdResultToFindPassword.setOnClickListener {
            val intent = Intent(this, FindPassword::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}