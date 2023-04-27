package com.example.qrcodescanner

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.qrcodescanner.databinding.ActivityMainBinding
import com.google.zxing.BinaryBitmap
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Reader
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var requestCamera: ActivityResultLauncher<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                val intent = Intent(this, BarcodeScan::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnBc.setOnClickListener {
            requestCamera?.launch(android.Manifest.permission.CAMERA)
        }
    }

    fun scan(view: View) {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            try {
                val imageUri: Uri? = data?.data
                val imageStream: InputStream? = contentResolver.openInputStream(imageUri!!)
                val selectedImage: Bitmap? = BitmapFactory.decodeStream(imageStream)
                try {
                    val bMap: Bitmap? = selectedImage
                    var contents: String? = null
                    val intArray = IntArray(bMap!!.width * bMap!!.height)
                    bMap!!.getPixels(intArray, 0, bMap!!.width, 0, 0, bMap!!.width, bMap!!.height)
                    val source: LuminanceSource =
                        RGBLuminanceSource(bMap.width, bMap.height, intArray)

                    val bitmap = BinaryBitmap(HybridBinarizer(source))

                    val reader: Reader = MultiFormatReader()

                    val result: Result = reader.decode(bitmap)
                    contents = result.text;

                    Toast.makeText(applicationContext,contents,Toast.LENGTH_LONG).show()

                }catch (e: IOException) {
                    e.printStackTrace()
                }
                //  image_view.setImageBitmap(selectedImage);
            }catch (e:FileNotFoundException){
                e.printStackTrace();

                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show()
        }
    }
}
