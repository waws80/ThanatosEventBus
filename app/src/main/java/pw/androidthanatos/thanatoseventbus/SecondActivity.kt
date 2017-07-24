package pw.androidthanatos.thanatoseventbus

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import pw.androidthanatos.library.Subscribe
import pw.androidthanatos.library.ThanatosEventBus
import pw.androidthanatos.library.ThreadModel

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        Log.d("SecondActivity","onCreate")
        ThanatosEventBus.getDefault().register(this)
        //直接获取粘性事件
        Log.d("Second-----:  ${Thread.currentThread().name}",
                "${ThanatosEventBus.getDefault().removeStickyEvent(String::class.java)}")

    }
    fun click(v: View){
        ThanatosEventBus.getDefault().post("你好我是SecondActivity")
    }

    /**
     * 注解获取粘性事件
     */
    @Subscribe(sticky = true,threadModel = ThreadModel.Background)
    fun receiver(str: MainActivity.A){
        Log.d("SecondReceiver:  ${Thread.currentThread().name}","$str")
    }
    override fun onDestroy() {
        super.onDestroy()
        ThanatosEventBus.getDefault().unregister(this)
    }
}
