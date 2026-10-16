package com.github.bumblebee202111.doubean.feature.groups.topic

import android.content.Context
import android.content.MutableContextWrapper
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebView

private const val TAG = "TopicWebViewPool"


object TopicWebViewPool {
    private const val MAX_CAPACITY = 3
    private val pool = LinkedHashMap<String, WebView>(MAX_CAPACITY, 0.75f, true)

    
    fun isPreloaded(topicId: String): Boolean {
        synchronized(pool) {
            return pool.containsKey(topicId)
        }
    }

    
    fun getOrCreate(
        topicId: String,
        context: Context,
        factory: (Context) -> WebView = { WebView(it) },
    ): WebView {
        synchronized(pool) {
            val existing = pool[topicId]
            if (existing != null) {
                (existing.parent as? ViewGroup)?.removeView(existing)
                (existing.context as? MutableContextWrapper)?.baseContext = context
                Log.d(TAG, "Reusing pooled WebView for topic: $topicId with updated Context")
                return existing
            }

            
            if (pool.size >= MAX_CAPACITY) {
                val eldestKey = pool.keys.firstOrNull()
                if (eldestKey != null) {
                    val evicted = pool.remove(eldestKey)
                    Log.d(TAG, "Evicting eldest pooled WebView: $eldestKey")
                    destroyWebView(evicted)
                }
            }

            Log.d(TAG, "Creating new WebView for topic: $topicId")
            val wrapper = MutableContextWrapper(context)
            val newWebView = factory(wrapper)
            pool[topicId] = newWebView
            return newWebView
        }
    }

    
    fun detachFromWindow(topicId: String) {
        synchronized(pool) {
            val webView = pool[topicId] ?: return
            (webView.parent as? ViewGroup)?.removeView(webView)
            (webView.context as? MutableContextWrapper)?.baseContext =
                webView.context.applicationContext
            Log.d(TAG, "Detached pooled WebView for topic: $topicId to ApplicationContext")
        }
    }

    
    fun release(topicId: String) {
        synchronized(pool) {
            val evicted = pool.remove(topicId)
            if (evicted != null) {
                Log.d(TAG, "Releasing and destroying WebView for popped topic: $topicId")
                destroyWebView(evicted)
            }
        }
    }

    
    fun destroyAll() {
        synchronized(pool) {
            Log.d(TAG, "Destroying all pooled WebViews (count=${pool.size})")
            for (webView in pool.values) {
                destroyWebView(webView)
            }
            pool.clear()
        }
    }

    private fun destroyWebView(webView: WebView?) {
        if (webView == null) return
        try {
            (webView.parent as? ViewGroup)?.removeView(webView)
            (webView.context as? MutableContextWrapper)?.baseContext =
                webView.context.applicationContext
            webView.stopLoading()
            webView.loadUrl("about:blank")
            webView.clearHistory()
            webView.removeAllViews()
            webView.destroy()
        } catch (e: Exception) {
            Log.e(TAG, "Error destroying WebView", e)
        }
    }
}
