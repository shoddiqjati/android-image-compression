package com.example.jati.imagecompression

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import com.bumptech.glide.Glide
import com.example.jati.imagecompression.utils.Constants
import com.example.jati.imagecompression.utils.CreateImageUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS = mutableListOf<String>()
    private val PERMISSIONS_REQ_CODE = 101
    private val CAMERA_REQ_CODE = 102

    lateinit var imagePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPermissions()

        bt_take.setOnClickListener {
            openCamera()
        }
    }

    private fun initPermissions() {
        PERMISSIONS.add(Manifest.permission.CAMERA)
        PERMISSIONS.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        PERMISSIONS.add(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun openCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS.toTypedArray(), PERMISSIONS_REQ_CODE)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val imageData = CreateImageUtils.createFile()
            imagePath = imageData.path
            val photoUri = FileProvider.getUriForFile(this, "$packageName.provider", imageData.image)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(intent, CAMERA_REQ_CODE)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun doCompressPhoto() {
        val compressedPhoto = CreateImageUtils.compressImage(imagePath)
        Glide.with(this).load(compressedPhoto.photo).into(iv_photo)
        tv_img_size.text = "${getString(R.string.image_size)} ${compressedPhoto.originalSize} ${Constants.BYTES}"
        tv_img_compressed.text = "${getString(R.string.image_compressed)} ${compressedPhoto.size} ${Constants.BYTES}"
        tv_resolution.text = "${getString(R.string.resolution)} ${compressedPhoto.originalHeight} ${compressedPhoto.originalWidth}"
        tv_resolution_compressed.text = "${getString(R.string.compressed_resolution)} ${compressedPhoto.height} ${compressedPhoto.width}"
        tv_orientation.text = compressedPhoto.orientation
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQ_CODE) {
            openCamera()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQ_CODE && resultCode == Activity.RESULT_OK) {
            doCompressPhoto()
        }
    }
}
