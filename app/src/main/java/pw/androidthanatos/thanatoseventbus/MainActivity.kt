package pw.androidthanatos.thanatoseventbus

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import pw.androidthanatos.library.Subscribe
import pw.androidthanatos.library.ThanatosEventBus
import pw.androidthanatos.library.ThreadModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ThanatosEventBus.getDefault().register(this)
    }
    fun click(v: View){
        ThanatosEventBus.getDefault().postSticky(A("我来自MainActivity",100))
        ThanatosEventBus.getDefault().postSticky("我是来自MainActivity的字符串")
        startActivity(Intent(this,SecondActivity::class.java))
    }
    @Subscribe(threadModel = ThreadModel.Post)
    fun receive(str: String){
        Log.d("MainReceiver:   ${Thread.currentThread().name}",str)
    }
    data class A(val str: String , val i: Int)
    override fun onDestroy() {
        super.onDestroy()
        ThanatosEventBus.getDefault().unregister(this)
    }
}
