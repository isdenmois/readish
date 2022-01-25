package com.isdenmois.readish.transfers.presentational

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.isdenmois.readish.transfers.lib.FileUploadedListener
import com.isdenmois.readish.transfers.lib.HTTPD
import com.isdenmois.readish.transfers.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class TransfersViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
) : ViewModel(), FileUploadedListener {
    val addressState = mutableStateOf("")
    val qrState = mutableStateOf<ImageBitmap?>(null)

    private val size = 512
    private val port = 8083

    private val uploadsDir by lazy {
        applicationContext.getString(R.string.books_dir)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            checkLocalAddress()
        }
    }

    private val server by lazy {
        HTTPD(root = uploadsDir, fileUploadedListener = this)
    }

    init {
        server.start()

        enableWifi()

        val filter = IntentFilter().apply {
            addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
            addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        }

        applicationContext.registerReceiver(receiver, filter)
    }

    override fun onCleared() {
        super.onCleared()
        server.stop()
        applicationContext.unregisterReceiver(receiver)
        disableWifi()
    }

    private fun checkLocalAddress() {
        val address = getIpAddressInLocalNetwork()

        if (address.isBlank() || address == "0.0.0.0") {
            addressState.value = ""
            qrState.value = null
            return
        }

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

    override fun onFileUploaded(file: File) {
        Toast.makeText(applicationContext, "File `${file.name}` uploaded", Toast.LENGTH_SHORT).show()
    }
}
