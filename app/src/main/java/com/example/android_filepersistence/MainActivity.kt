package com.example.android_filepersistence

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.util.Log
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

    private val TAG = "mylog_mainActivity"

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

        val buttonRestoreData = findViewById<Button>(R.id.button_restoreData)
        buttonRestoreData.setOnClickListener {
            val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
            val name = prefs.getString("name", "null")
            val age = prefs.getInt("age", 0)
            val isHandsome = prefs.getBoolean("isHandsome", false)
            Log.v(TAG , "name = $name, age = $age, isHadnsome = $isHandsome")
        }

        val dbHelper = MyDataBaseHelper(this, "BookStore.db", 4)

        val buttonCreateDB = findViewById<Button>(R.id.button_createDB)
        buttonCreateDB.setOnClickListener {
            dbHelper.writableDatabase
        }

        val buttonAddData = findViewById<Button>(R.id.button_addData)
        buttonAddData.setOnClickListener {
            val db = dbHelper.writableDatabase
            //组装第一条数据
            val value1 = ContentValues().apply {
                //没有给id赋值，因为在建表时已经设置了id自增
                put("name", "Kotlin Core Code")
                put("price", 58)
                put("author", "Water")
                put("pages", 300)
            }
            db.insert("Book", null, value1)

            //组装第二条数据
            val value2 = ContentValues().apply {
                put("name", "Big Story")
                put("price", 30)
                put("author", "zzmeow")
                put("pages", 666)
            }
            db.insert("Book", null, value2)
            Toast.makeText(this, "insert Data", Toast.LENGTH_SHORT).show()
        }

        //用于更新数据
        val buttonUpdateData = findViewById<Button>(R.id.button_updateData)
        buttonUpdateData.setOnClickListener {
            val db = dbHelper.writableDatabase
            val value = ContentValues()
            value.put("price", 9.9)
            //第三个参数对应的是SQL语句的where部分，表示更新所有name等于?的行
            //而?是一个占位符，可以通过第四个参数提供的一个字符串数组为第三个参数中的每个占位符指定相应的内容
            db.update("Book", value, "name = ?", arrayOf("Kotlin Core Code"))
            Toast.makeText(this, "update Data", Toast.LENGTH_SHORT).show()
        }

        //用于删除数据
        val buttonDeleteData = findViewById<Button>(R.id.button_deleteData)
        buttonDeleteData.setOnClickListener {
            val db = dbHelper.writableDatabase
            db.delete("Book", "pages > ?", arrayOf("500"))
            Toast.makeText(this, "delete Data", Toast.LENGTH_SHORT).show()
        }

        val buttonQueryData = findViewById<Button>(R.id.button_queryData)
        buttonQueryData.setOnClickListener {
            val db = dbHelper.writableDatabase
            val cursor = db.query(false, "Book", null, null, null,
                null,  null, null, null)
            // 将数据指针移动到第一行
            if (cursor.moveToFirst()) {
                do {
                    //遍历数据库中每一列
                    val author = cursor.getString(cursor.getColumnIndexOrThrow("author"))
                    val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val pages = cursor.getInt(cursor.getColumnIndexOrThrow("pages"))
                    Log.v(TAG, "query data: author = $author, prize = $price, name = $name, pages = $pages")
                }while (cursor.moveToNext())
            }
            cursor.close()
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