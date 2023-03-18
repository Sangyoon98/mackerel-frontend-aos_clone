package com.mackerel_frontend_aos.presentation.view.activity

import android.R
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.mackerel_frontend_aos.databinding.ActivityTermsDetailBinding

class TermsDetail : AppCompatActivity() {
    private var mBinding: ActivityTermsDetailBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTermsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "개인정보 처리방침"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.webViewTermsDetail.webViewClient = WebViewClient()
        binding.webViewTermsDetail.loadUrl("http://ec2-43-201-94-220.ap-northeast-2.compute.amazonaws.com:8080/privacyPolicy/policy-test.html")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}