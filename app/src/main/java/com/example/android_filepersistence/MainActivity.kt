package com.example.android_filepersistence

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val inputText = loadData()
        if (inputText != null) {
            val editText = findViewById<EditText>(R.id.editText1)
            editText.setText(inputText)
            editText.setSelection(inputText.length)
            Toast.makeText(this, "restore data", Toast.LENGTH_SHORT).show()
        }

        val buttonSaveData = findViewById<Button>(R.id.button_saveData)
        buttonSaveData.setOnClickListener {
            val editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
            editor.putString("name", "zzmeow")
            editor.putInt("age", 24)
            editor.putBoolean("isHandsome", true)
            ////缓存的数据存放在/data/data/com.example.Android_FilePersistence/shared_prefs 目录下
            editor.apply()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        val editText = findViewById<EditText>(R.id.editText1)
        val input = editText.text.toString()
        saveData(input)
    }

    private fun saveData(inputText: String){
        try {
            //缓存的数据存放在/data/data/com.example.Android_FilePersistence/anotherData 目录下
            val output = openFileOutput("anotherData", Context.MODE_PRIVATE)
            val writer = BufferedWriter(OutputStreamWriter(output))
            writer.use {
                it.write(inputText)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadData(): String {
        val content = StringBuilder()
        try {
            val input = openFileInput("anotherData")
            val reader = BufferedReader(InputStreamReader(input))
            reader.use {
                //将读到的每行内容回调到lambda，然后在lambda表达式完成拼接逻辑
                reader.forEachLine {
                    content.append(it)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return content.toString()
    }
}