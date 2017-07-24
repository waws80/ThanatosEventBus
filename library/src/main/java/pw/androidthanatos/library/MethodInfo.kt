package pw.androidthanatos.library

import java.lang.reflect.Method

/**
 * 接收事件的方法信息
 */
data class MethodInfo(val method: Method,
                      val threadModel: ThreadModel,
                      val sticky: Boolean,
                      val event: Class<*>)