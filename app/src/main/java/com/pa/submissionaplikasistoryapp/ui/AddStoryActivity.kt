package com.pa.submissionaplikasistoryapp.ui

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.pa.submissionaplikasistoryapp.R
import com.pa.submissionaplikasistoryapp.databinding.ActivityAddStoryBinding
import com.pa.submissionaplikasistoryapp.ui.viewmodel.RegisterViewModel
import com.pa.submissionaplikasistoryapp.ui.viewmodel.RegisterViewModelFactory
import com.pa.submissionaplikasistoryapp.utils.createCustomTempFile
import com.pa.submissionaplikasistoryapp.utils.reduceFileImage
import com.pa.submissionaplikasistoryapp.utils.rotateFile
import com.pa.submissionaplikasistoryapp.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding

    private var getFile: File? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val factory: RegisterViewModelFactory by lazy {
        RegisterViewModelFactory.getInstance(this.application)
    }
    private val viewModel: RegisterViewModel by viewModels {
        factory
    }

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Check for camera permission
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.cameraXButton.setOnClickListener { startCameraX() }
        binding.cameraButton.setOnClickListener { startTakePhoto() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle(getString(R.string.dialog_title))
            alertDialogBuilder.setMessage(getString(R.string.dialog_message))
            alertDialogBuilder.setPositiveButton(getString(R.string.yes)) { _: DialogInterface, _: Int ->
                getLocation()
            }
            alertDialogBuilder.setNegativeButton(getString(R.string.no)) { _: DialogInterface, _: Int ->
                uploadImageWithoutLocation()
            }
            // Menampilkan AlertDialog
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

        }


    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.pa.submissionaplikasistoryapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun getLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    uploadImage(location)
                } else {
                    Toast.makeText(
                        this,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun uploadImage(location: Location) {
        val desc = binding.etDescription.text.toString()
        if (getFile != null && desc.isNotEmpty()) {
            showProgressBar(true)

            val file = reduceFileImage(getFile as File)

            val description =
                binding.etDescription.text.toString()
                    .toRequestBody("text/plain".toMediaType())

            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            val lat = location.latitude
            val lon = location.longitude

            val multiForm = "application/json"

            viewModel.uploadStory(
                imageMultipart,
                description,
                lat,
                lon,
                multiForm
            ).observe(this) { response ->
                if (response.error) {
                    Toast.makeText(this@AddStoryActivity, "Failed Upload Story", Toast.LENGTH_SHORT)
                        .show()
                    showProgressBar(false)
                } else {
                    showProgressBar(false)
                    Toast.makeText(
                        this@AddStoryActivity,
                        "Sucessfuly Upload Story",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@AddStoryActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }
        } else {
            Toast.makeText(
                this@AddStoryActivity,
                getString(R.string.add_story_reminder),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun uploadImageWithoutLocation() {
        val desc = binding.etDescription.text.toString()
        if (getFile != null && desc.isNotEmpty()) {
            showProgressBar(true)

            val file = reduceFileImage(getFile as File)

            val description =
                binding.etDescription.text.toString()
                    .toRequestBody("text/plain".toMediaType())

            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            val multiForm = "application/json"

            viewModel.uploadStoryWithoutLocation(
                imageMultipart,
                description,
                multiForm
            ).observe(this) { response ->
                if (response.error) {
                    Toast.makeText(this@AddStoryActivity, "Failed Upload Story", Toast.LENGTH_SHORT)
                        .show()
                    showProgressBar(false)
                } else {
                    showProgressBar(false)
                    Toast.makeText(
                        this@AddStoryActivity,
                        "Sucessfuly Upload Story",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@AddStoryActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }
        } else {
            Toast.makeText(
                this@AddStoryActivity,
                getString(R.string.add_story_reminder),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getLocation()
                }
                else -> {
                    // No location access granted.
                    Snackbar.make(
                        binding.root,
                        "Location Permission Denied",
                        Snackbar.LENGTH_SHORT
                    )
                        .setAction("Please allow location permission in settings") {
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also { intent ->
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }.show()
                }
            }
        }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean


            myFile.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            myFile.let { file ->
                getFile = file
                binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(myFile.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                getFile = myFile
                binding.previewImageView.setImageURI(uri)
            }
        }
    }

    private fun showProgressBar(loading: Boolean) {
        if (loading) {
            binding.progressbar.visibility = View.VISIBLE
        } else {
            binding.progressbar.visibility = View.GONE
        }
    }


}