package com.pichs.xsimpledialog

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pichs.xdialog.NiceDialog
import com.pichs.xsimpledialog.databinding.ActivityMainBinding
import com.pichs.xsimpledialog.databinding.DialogLayoutBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.btnShowDialog.setOnClickListener {
            NiceDialog(DialogLayoutBinding::class.java)
                .config {
                    width = -1
                    height = 400
                    backgroundColor = Color.parseColor("#ff0000")
                    dimAmount  = 0.5f
                }.bind {
                    binding.btnDismiss.setOnClickListener {
                       dismiss()
                    }
                }.show(supportFragmentManager, "tagNormal")
        }

    }
}