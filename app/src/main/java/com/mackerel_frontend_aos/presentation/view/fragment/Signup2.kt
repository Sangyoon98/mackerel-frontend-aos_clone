package com.mackerel_frontend_aos.presentation.view.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mackerel_frontend_aos.data.model.SchoolListDataList
import com.mackerel_frontend_aos.data.repository.ExceptionDialog
import com.mackerel_frontend_aos.data.repository.PathToMultipartBody
import com.mackerel_frontend_aos.databinding.FragmentSignup2Binding
import com.mackerel_frontend_aos.presentation.view.activity.Signup
import com.mackerel_frontend_aos.presentation.view.adapter.AutoCompleteTextViewAdapter
import com.mackerel_frontend_aos.presentation.viewmodel.Signup2ViewModel
import java.io.*

class Signup2 : Fragment() {
    private var mBinding: FragmentSignup2Binding? = null
    private val binding get() = mBinding!!
    lateinit var signUp: Signup
    lateinit var viewModel: Signup2ViewModel
    var school = ""
    var grade = ""
    var image = ""
    private var photoUri: Uri? = null
    private var isTextFilled = false
    private lateinit var items: List<SchoolListDataList>
    private lateinit var adapter: AutoCompleteTextViewAdapter

    // Permissions
    val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        signUp = context as Signup
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentSignup2Binding.inflate(inflater, container, false)

        //Dialog
        viewModel  = ViewModelProvider(this)[Signup2ViewModel::class.java]
        viewModel.dialog.observe(signUp, Observer {
            if(it != null) {
                ExceptionDialog(signUp, it.header, it.body).showDialog()
            }
        })

        //카메라 사용을 위한 권한 체크
        checkPermissions(PERMISSIONS)

        //카메라로 인텐트
        binding.layoutSignupInputImage.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            var photoFile = File(
                File("${signUp.filesDir}/image").apply {
                    if (!this.exists()) {
                        this.mkdirs()
                    }
                }, PathToMultipartBody.newJpgFileName()
            )

            photoUri = FileProvider.getUriForFile(
                signUp,
                "com.mackerel_frontend_aos.fileprovider",
                photoFile
            )
            image = photoFile.absolutePath.toString()


            takePictureIntent.resolveActivity(signUp.packageManager)?.also {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                activityResult.launch(takePictureIntent)
                //startActivityForResult(takePictureIntent, 100)
            }
        }

        //학교 목록 자동 완성
        binding.edittextSignupInputSchool.doOnTextChanged { p0: CharSequence?, p1: Int, p2: Int, p3: Int -> schoolItems() }
        viewModel.schoolList.observe(signUp, Observer {
            items = it
            adapter = AutoCompleteTextViewAdapter(signUp, com.mackerel_frontend_aos.R.layout.auto_complete_text_view_item, items)
            binding.edittextSignupInputSchool.setAdapter(adapter)
            adapter.notifyDataSetChanged()
        })

        //텍스트 값 액티비티로 실시간 전달
        binding.edittextSignupInputSchool.doOnTextChanged { p0: CharSequence?, p1: Int, p2: Int, p3: Int -> isEditTextFilled() }

        //학년 버튼 클릭시 동작 코드
        binding.buttonSignupGrade1.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                binding.buttonSignupGrade1.isClickable = isChecked
                binding.buttonSignupGrade1.isEnabled = isChecked
                grade = "HIGH_FIRST"
            }
            isEditTextFilled()
        }

        binding.buttonSignupGrade2.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                binding.buttonSignupGrade2.isClickable = isChecked
                binding.buttonSignupGrade2.isEnabled = isChecked
                grade = "HIGH_SECOND"
            }
            isEditTextFilled()
        }

        binding.buttonSignupGrade3.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                binding.buttonSignupGrade3.isClickable = isChecked
                binding.buttonSignupGrade3.isEnabled = isChecked
                grade = "HIGH_THIRD"
            }
            isEditTextFilled()
        }

        return binding.root
    }

    //학교 목록 갱신
    private fun schoolItems() {
        val schoolName = binding.edittextSignupInputSchool.text.toString()
        if (schoolName.isNotEmpty()) {
            viewModel.getSchoolList(schoolName)
        }
    }

    //EditText 내용 전달
    private fun isEditTextFilled() {
        school = binding.edittextSignupInputSchool.text.toString()
        isTextFilled = school.isNotEmpty() && image.isNotEmpty() && grade.isNotEmpty()

        //send data
        (activity as Signup).signup2(school, image, grade, isTextFilled)
    }

    //카메라 사진 촬영 후 동작 코드
    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                        val contentResolver: ContentResolver = signUp.contentResolver
                        val instream: InputStream? =
                            photoUri?.let { contentResolver.openInputStream(it) }
                        val imgBitmap = BitmapFactory.decodeStream(instream)
                        val rotateImgBitmap = PathToMultipartBody.rotatedBitmap(imgBitmap, photoUri.toString())
                        binding.imageviewSignupImageBitmap.setImageBitmap(rotateImgBitmap)
                    } else {
                        val imageBitmap = photoUri?.let {
                            ImageDecoder.createSource(signUp.contentResolver, it)
                        }

                        binding.imageviewSignupImageBitmap.setImageBitmap(imageBitmap?.let {
                            ImageDecoder.decodeBitmap(it)
                        })
                    }
                    binding.layoutSignupImageBitmap.visibility = View.VISIBLE
                    binding.layoutSignupImageNone.visibility = View.GONE

                    //send data
                    (activity as Signup).signup2(school, image, grade, isTextFilled)
                    Log.d("logFish", "Bitmap Absolute Path: $image")
                }
            }
        }

    /*//카메라 사진 촬영 후 동작 코드
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode) {
                100 -> {
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                        val contentResolver: ContentResolver = ContentResolver.
                        val instream: InputStream = contentResolver.openInputStream(photoUri)
                        val imgBitmap = BitmapFactory.decodeStream(instream)
                        binding.imageviewSignupImageBitmap.setImageBitmap(imgBitmap)
                        Log.d("logFish", "안드로이드 로그")
                    } else {
                        val imageBitmap = photoUri?.let {
                            ImageDecoder.createSource(signUp.contentResolver, it)
                        }

                        binding.imageviewSignupImageBitmap.setImageBitmap(imageBitmap?.let {
                            ImageDecoder.decodeBitmap(it)
                        })
                    }
                    binding.layoutSignupImageBitmap.visibility = View.VISIBLE
                    binding.layoutSignupImageNone.visibility = View.GONE

                    //send data
                    (activity as Signup).signup2(school, image, grade, isTextFilled)
                    Log.d("logFish", "Bitmap Absolute Path: $image")
                }
            }
        }
    }*/

    //권한 확인
    private fun checkPermissions(permissions: Array<String>): Boolean {
        val permissionList: MutableList<String> = mutableListOf()
        for (permission in permissions) {
            val result = ContextCompat.checkSelfPermission(signUp, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission)
            }
        }
        if (permissionList.isNotEmpty()) {
            ActivityCompat.requestPermissions(signUp, permissionList.toTypedArray(), 100)
            return false
        }
        return true
    }

    //권한 확인 결과
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(signUp, "권한 승인 부탁드립니다.", Toast.LENGTH_SHORT).show()
                signUp.finish()
            }
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}