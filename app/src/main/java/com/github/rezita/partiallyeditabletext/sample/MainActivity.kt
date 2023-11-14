package com.github.rezita.partiallyeditabletext.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.github.rezita.partiallyeditabletext.PartiallyEditableText as PartiallyEditableText


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        initView()
        setListeners()
    }

    private fun initView() {
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

    private fun showResponseDialog() {
        val editText = findViewById<PartiallyEditableText>(R.id.editable)
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

    private fun setListeners(){
        val partiallyEditText = findViewById<PartiallyEditableText>(R.id.editable)
        partiallyEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                Toast.makeText(applicationContext, "Typed: ${partiallyEditText.editableText}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}