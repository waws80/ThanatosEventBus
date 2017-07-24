package pw.androidthanatos.library

/**
 * eventBus事件注解
 */

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Subscribe(val threadModel: ThreadModel = ThreadModel.Main,
                           val sticky: Boolean = false)