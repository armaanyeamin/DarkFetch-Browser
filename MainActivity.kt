package com.darkfetch.browser

import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressText: TextView
    private var privateMode = false

    private val adHosts = listOf(
        "doubleclick.net",
        "googlesyndication",
        "adsystem",
        "facebookads",
        "tracking",
        "analytics"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        val urlBox = findViewById<EditText>(R.id.urlBox)
        val downloadBtn = findViewById<Button>(R.id.downloadBtn)
        val privateBtn = findViewById<Button>(R.id.privateBtn)
        val historyBtn = findViewById<Button>(R.id.historyBtn)
        progressText = findViewById(R.id.progressText)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {

                val url = request?.url.toString()
                if (adHosts.any { url.contains(it) }) {
                    return WebResourceResponse("text/plain", "utf-8", null)
                }
                return super.shouldInterceptRequest(view, request)
            }
        }

        webView.loadUrl("https://www.google.com")

        downloadBtn.setOnClickListener {
            val link = urlBox.text.toString()
            if (link.isNotEmpty()) showQualityDialog(link)
        }

        privateBtn.setOnClickListener {
            privateMode = !privateMode
            if (privateMode) enablePrivate()
            else disablePrivate()
        }

        historyBtn.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    private fun showQualityDialog(baseUrl: String) {
        val qualities = arrayOf("360p", "720p HD", "1080p Full HD")

        AlertDialog.Builder(this)
            .setTitle("Select Quality")
            .setItems(qualities) { _, which ->
                startDownload(baseUrl, qualities[which])
            }
            .show()
    }

    private fun startDownload(url: String, quality: String) {
        progressText.text = "Downloading $quality"

        val request = DownloadManager.Request(Uri.parse(url))
        request.setNotificationVisibility(
            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
        )
        request.setDestinationInExternalPublicDir(
            "/Download",
            "video_${System.currentTimeMillis()}.mp4"
        )

        val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)

        saveHistory("video_$quality")
    }

    private fun saveHistory(name: String) {
        val prefs = getSharedPreferences("history", MODE_PRIVATE)
        prefs.edit().putString(System.currentTimeMillis().toString(), name).apply()
    }

    private fun enablePrivate() {
        webView.settings.saveFormData = false
        webView.clearCache(true)
        webView.clearHistory()
        Toast.makeText(this, "Private mode ON", Toast.LENGTH_SHORT).show()
    }

    private fun disablePrivate() {
        Toast.makeText(this, "Private mode OFF", Toast.LENGTH_SHORT).show()
    }
}
