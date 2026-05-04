package tech.ula.utils

import android.content.Context
import android.util.Log
import io.sentry.Breadcrumb
import io.sentry.Sentry
import io.sentry.SentryLevel
import tech.ula.viewmodel.IllegalState

sealed class BreadcrumbType {
    // These types should override toString with return values of < 15 characters so that they
    // are easily identified in the Sentry UI.
    object ReceivedIntent : BreadcrumbType() {
        override fun toString(): String {
            return "Intent received"
        }
    }
    object SubmittedEvent : BreadcrumbType() {
        override fun toString(): String {
            return "Event submitted"
        }
    }
    object ReceivedEvent : BreadcrumbType() {
        override fun toString(): String {
            return "Event received"
        }
    }
    object ObservedState : BreadcrumbType() {
        override fun toString(): String {
            return "State observed"
        }
    }
    object RuntimeError : BreadcrumbType() {
        override fun toString(): String {
            return "Runtime error"
        }
    }
}

data class UlaBreadcrumb(
    val originatingClass: String,
    val type: BreadcrumbType,
    val details: String
)

interface Logger {
    fun initialize(context: Context? = null)

    fun addBreadcrumb(breadcrumb: UlaBreadcrumb)

    fun addExceptionBreadcrumb(err: Exception)

    fun sendIllegalStateLog(state: IllegalState)

    fun sendEvent(message: String)
}

class SentryLogger : Logger {
    override fun initialize(context: Context?) {
        // Sentry is initialized via AndroidManifest.xml meta-data
    }

    override fun addBreadcrumb(breadcrumb: UlaBreadcrumb) {
        val key = "${breadcrumb.type}"
        val value = "${breadcrumb.originatingClass}: ${breadcrumb.details}"
        Sentry.addBreadcrumb(Breadcrumb().apply {
            category = key
            message = value
            level = SentryLevel.INFO
        })
        Log.i("Breadcrumb", "$key $value")
    }

    override fun addExceptionBreadcrumb(err: Exception) {
        val stackTrace = err.stackTrace.first()
        Sentry.addBreadcrumb(Breadcrumb().apply {
            category = "Exception"
            setData("type", err.javaClass.simpleName)
            setData("file", stackTrace.fileName)
            setData("lineNumber", stackTrace.lineNumber.toString())
            level = SentryLevel.ERROR
        })
    }

    override fun sendIllegalStateLog(state: IllegalState) {
        val message = state.javaClass.simpleName
        Sentry.captureMessage(message, SentryLevel.ERROR)
        Log.e("ILLEGAL_STATE", message)
    }

    override fun sendEvent(message: String) {
        Sentry.captureMessage(message, SentryLevel.ERROR)
        Log.e("EVENT", message)
    }
}