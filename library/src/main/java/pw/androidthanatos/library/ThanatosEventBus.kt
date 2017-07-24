package pw.androidthanatos.library

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors

/**
 * 事件总线
 */
class ThanatosEventBus private constructor(){

    /**
     * 普通事件的列表
     */
    private val events: ConcurrentHashMap<Any,MutableList<MethodInfo>?> = ConcurrentHashMap()

    /**
     * 粘性事件的列表
     */
    private val stickyEvents = ConcurrentHashMap<Class<*>,Any?>()

    /**
     * 切换线程到主线程
     */
    private val uiHandler: Handler by  lazy { Handler() }

    /**
     * 线程池
     */
    private val threadPool = Executors.newFixedThreadPool(4)

    companion object {
        /**
         * 懒加载 ThanatosEventBus ,当使用的时候才去创建当前类对象
         */
        private val instance: ThanatosEventBus by lazy { ThanatosEventBus() }

        /**
         * @return ThanatosEventBus 对外暴露获取当前类实例的唯一方法
         */
        fun getDefault(): ThanatosEventBus = instance
    }

    /**
     * 对当前类进行注册
     * @param obj  当前类实例对象 或者activity的上下文对象
     */
    fun register(obj: Any){
        val clazz = obj::class.java
        val list = events[clazz]
        if (list == null){
            postMethodInfo(obj)
        }
    }


    /**
     * 注册eventbus事件后 将当前类的所有要接收的事件方法信息添加到列表中去
     */
    private fun postMethodInfo(clazz: Any) {
        val list: MutableList<MethodInfo> = CopyOnWriteArrayList()
        var clz: Class<*>? = clazz::class.java
        while(clz != null){

            if (clz.name.startsWith("java.") || clz.name.startsWith("android.") || clz.name.startsWith("javax.")) {
                break
            }
            val methods = clz.declaredMethods.filter { it.isAnnotationPresent(Subscribe::class.java) }

            val nomalMethod = methods.filter { !it.getAnnotation(Subscribe::class.java).sticky }
            val stickyMethod = methods.filter { it.getAnnotation(Subscribe::class.java).sticky }

            //当前接收事件的方法为普通接收方法，将方法信息添加到事件缓存列表
            nomalMethod.forEach {
                it.methodException()
                list.add(MethodInfo(it,
                        it.getAnnotation(Subscribe::class.java).threadModel,
                        it.getAnnotation(Subscribe::class.java).sticky,it.parameterTypes[0]))
            }
            //当前接收事件为粘性事件，直接从粘性事件缓存列表中获取事件信息
            stickyMethod.forEach {
                it.methodException()
                val method = it
                val sticky = stickyEvents.filter { it.key.isAssignableFrom(method.parameterTypes[0]) } as HashMap
                val value = sticky.remove(method.parameterTypes[0])
                if (value != null){
                    invoke(method,clazz,value,method.getAnnotation(Subscribe::class.java).threadModel)
                }
            }
            events.put(clazz,list)
            clz = clz.superclass as Class<*>
        }
    }

    /**
     * 方法异常处理
     */
    private fun  Method.methodException(){
        if (this.parameterTypes.isEmpty()) throw RuntimeException("Subscribe method parameter must have one")
        if (this.parameterTypes.size >1) throw RuntimeException("Subscribe method parameter only have one")
    }

    /**
     * 需要发送的事件
     * @param msg 事件信息
     */
    fun post(msg: Any){
        events.forEach {
            val target = it.key
            it.value?.forEach {
                if (it.event.isAssignableFrom(msg::class.java)){
                    postToSubscribe(it,target,msg)
                }
            }
        }
    }

    /**
     * 将普通事件传递给订阅者
     */
    private fun postToSubscribe(it: MethodInfo, target: Any, msg: Any) {
        it.method.isAccessible = true
        if (!it.sticky){
            invoke(it.method,target,msg,it.threadModel)
        }
    }

    /**
     * 执行订阅者方法
     */
    private fun invoke(method: Method, target: Any, msg: Any, threadModel: ThreadModel) {
        when(threadModel){

            ThreadModel.Main ->{
                if (Looper.myLooper() == Looper.getMainLooper()){
                    method.invoke(target,msg)
                }else{
                    uiHandler.post { method.invoke(target,msg) }
                }
            }

            ThreadModel.Post ->{
                method.invoke(target,msg)
            }

            ThreadModel.Background ->{
                threadPool.execute {
                    method.invoke(target,msg)
                }
            }
        }
    }

    /**
     * 需要发送的粘性事件
     * @param msg 事件信息
     */
    fun postSticky(msg: Any){
        stickyEvents.put(msg::class.java,msg)
    }

    /**
     * 获取到粘性事件并将粘性事件移除掉
     */
    fun removeStickyEvent(clazz: Class<*>) = stickyEvents.remove(clazz)

    /**
     * 获取到粘性事件，不将粘性事件移除，以备后边逻辑使用，一半不建议使用此方法
     */
    fun getStickyEvent(clazz: Class<*>) = stickyEvents[clazz]

    /**
     * 对当前类进行注销
     * @param obj 当前类实例对象 或者activity的上下文对象
     */
    fun unregister(obj: Any){
        events.remove(obj)
    }
}