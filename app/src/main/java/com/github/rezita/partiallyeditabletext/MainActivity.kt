package com.github.rezita.partiallyeditabletext

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.rezita.partiallyeditabletext.PartiallyEditableText as PartiallyEditableText

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    fun initView() {
        val btn = findViewById<Button>(R.id.button)
        btn.setOnClickListener { showResponseDialog() }
    }

    fun showWhatTyped() {
        val editText = findViewById<PartiallyEditableText>(R.id.editable)
        val typed = editText.editableText
        Toast.makeText(
            this,
            String.format(getString(R.string.dialog_msg), typed),
            Toast.LENGTH_LONG
        ).show()
    }

    fun showResponseDialog() {
        val editText = findViewById<PartiallyEditableText>(R.id.editable);
        val typed = editText.editableText
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setMessage(String.format(getString(R.string.dialog_msg), typed))
            .setNeutralButton(getString(R.string.dialog_btn)) { dialog, which ->
                dialog.dismiss()
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}