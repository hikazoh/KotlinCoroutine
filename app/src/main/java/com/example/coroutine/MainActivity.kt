package com.example.coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.EnumSet.of
import java.util.stream.Stream.of
import kotlin.coroutines.CoroutineContext
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var dateTextView = findViewById<TextView>(R.id.textView)
        dateTextView.text = GiantJobs.getNow()


        var button = findViewById<Button>(R.id.button)
        val model = ViewModelProvider.NewInstanceFactory().create(StringViewModel::class.java)
        model.getStr().observe(this, androidx.lifecycle.Observer {
            dateTextView.text = it
        })
        button.setOnClickListener {
            model.updateData()
        }
    }

}

object GiantJobs {
    suspend fun  getNowAfterGiantJob(ctx: CoroutineScope) :Deferred<String>{
        return ctx.async {
            Thread.sleep(5000L)
            getNow()
        }
    }

    fun getNow() : String = SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Calendar.getInstance().time)

}

class StringViewModel : ViewModel(){
    private  val str : MutableLiveData<String> by lazy {
            MutableLiveData<String>()
    }
    fun getStr() : LiveData<String> {
        return str
    }

    fun updateData() {
        GlobalScope.launch {
            str.postValue(GiantJobs.getNowAfterGiantJob(this).await())
        }
    }
}

