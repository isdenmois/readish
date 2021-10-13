package com.isdenmois.readish.screens.transfers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.isdenmois.readish.R
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.io.File

class TransfersViewModel (
    private val applicationContext: Context
) : ViewModel() {
    val addressState = mutableStateOf("")
    val qrState = mutableStateOf<ImageBitmap?>(null)

    private val size = 512
    private val port = 8083

    private val coroutineContext = viewModelScope + Dispatchers.IO
    private val uploadsDir by lazy {
        applicationContext.getString(R.string.books_dir)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            checkLocalAddress()
        }
    }

    private val server by lazy {
        embeddedServer(Jetty, port, watchPaths = emptyList()) {
            routing {
                get("/") {
                    call.respondText("All good here in ${Build.MODEL}", ContentType.Text.Plain)
                }

                post("/upload") {
                    val multipartData = call.receiveMultipart()

                    multipartData.forEachPart { part ->
                        if (part is PartData.FileItem) {
                            val fileName = part.originalFileName as String
                            part.streamProvider().copyTo(
                                File("$uploadsDir/$fileName").outputStream()
                            )

                            logAboutFileReceive(fileName)
                        }
                    }

                    call.respondText("{ \"ok\": true }", ContentType.Application.Json)
                }
            }
        }
    }

    init {
        coroutineContext.launch {
            server.start()
        }

        enableWifi()

        val filter = IntentFilter().apply {
            addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
            addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        }

        applicationContext.registerReceiver(receiver, filter)
    }

    override fun onCleared() {
        super.onCleared()
        server.stop(1000, 10000)
        applicationContext.unregisterReceiver(receiver)
        disableWifi()
    }

    private fun logAboutFileReceive(fileName: String) {
        viewModelScope.launch {
            Toast.makeText(applicationContext, "File $fileName received", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkLocalAddress() {
        val address = getIpAddressInLocalNetwork() ?: return

        addressState.value = "http://$address:$port"

        try {
            qrState.value = getNetworkQR(addressState.value)?.asImageBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
            qrState.value = null
        }
    }

    private fun getIpAddressInLocalNetwork(): String {
        val wifiManager = applicationContext.getSystemService(ComponentActivity.WIFI_SERVICE) as WifiManager
        val ipAddress = wifiManager.connectionInfo.ipAddress

        return "%d.%d.%d.%d".format(
            ipAddress and 0xff,
            ipAddress shr 8 and 0xff,
            ipAddress shr 16 and 0xff,
            ipAddress shr 24 and 0xff
        )
    }

    private fun getNetworkQR(str: String): Bitmap? {
        val result: BitMatrix

        try {
            result = MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, size, size, null)
        } catch (iae: IllegalArgumentException) {
            return null
        }

        val w = result.width
        val h = result.height
        val pixels = IntArray(w * h)
        for (y in 0 until h) {
            val offset = y * w
            for (x in 0 until w) {
                pixels[offset + x] = if (result.get(x, y)) BLACK else WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
        return bitmap
    }

    private fun enableWifi() {
        val wifiManager = applicationContext.getSystemService(ComponentActivity.WIFI_SERVICE) as WifiManager

        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
        }
    }

    private fun disableWifi() {
        val wifiManager = applicationContext.getSystemService(ComponentActivity.WIFI_SERVICE) as WifiManager

        if (wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = false
        }
    }
}
