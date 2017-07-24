# ThanatosEventBus
自己在使用EventBus后对知识的渴望和对其的热爱使我有了写一个自己的事件总线的冲动，当然了无论在学习什么的时候肯定还是要先了解
其原理才可以。不要骂我不要脸的在展示自己写的东西。
<br>
<br>
![](http://image.baidu.com/search/detail?ct=503316480&z=undefined&tn=baiduimagedetail&ipn=d&word=斗图表情%20我错了&step_word=&ie=utf-8&in=&cl=2&lm=-1&st=undefined&cs=435922930,3261093505&os=36796307,2673472862&simid=3583247060,320525246&pn=1&rn=1&di=105028857230&ln=244&fr=&fmq=1500891033611_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&is=0,0&istype=0&ist=&jit=&bdtype=0&spn=0&pi=0&gsm=0&hs=2&objurl=http%3A%2F%2Fimg.qqzhi.com%2Fupload%2Fimg_5_435922930D3261093505_11.jpg&rpstart=0&rpnum=0&adpicid=0)
<br>
好吧！至于EventBus怎么用大家都知道这里就不献丑了，接下来说说我写的这个的完成度。
<br>
一： EventBus3.0使用的是注解注入的方式，而我使用的还是注解反射机制来处理，说来惭愧啊 ！<br>
二： 实现了类似于EventBus3.0的粘性事件发布和处理。<br>
三： 使用起来和EventBus3.0一样，但是没有事件优先级。。。。<br>
四： 好吧允许我装逼下，代码使用Kotlin编写。

#### 使用流程：

1. 添加依赖
```
自己可以下载下来类库添加到自己的项目区。。。。
```
2. 注册
```
ThanatosEventBus.getDefault().register(this)
```
3. 发布普通事件
```
ThanatosEventBus.getDefault().post("你好我是SecondActivity")
```
4. 发布粘性事件
```
ThanatosEventBus.getDefault().postSticky(A("我来自MainActivity",100))
```
5. 接收普通事件
```
@Subscribe(threadModel = ThreadModel.Post)
fun receive(str: String){
    Log.d("MainReceiver:   ${Thread.currentThread().name}",str)
}
```
6. 接收粘性事件<br>

①接收事件不移除事件
 ```
 ThanatosEventBus.getDefault().getStickyEvent(String::class.java)
 ```
 ```
 @Subscribe(sticky = true,threadModel = ThreadModel.Background)
 fun receiver(str: MainActivity.A){
     Log.d("SecondReceiver:  ${Thread.currentThread().name}","$str")
 }
 ```
②接收事件移除事件
```
ThanatosEventBus.getDefault().removeStickyEvent(String::class.java)
```
以上就是我自己写的事件总线所实现的功能，请大婶们检阅，不足之处请多多批评。<br>
需要源码的司机请点击下边连接<br>
<https://github.com/waws80/ThanatosEventBus/><br>
感觉还凑合的朋友请给个start哦！！