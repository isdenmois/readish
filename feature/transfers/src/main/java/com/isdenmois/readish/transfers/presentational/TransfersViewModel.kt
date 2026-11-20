package com.isdenmois.readish.transfers.presentational

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.isdenmois.readish.transfers.R
import com.isdenmois.readish.transfers.lib.UploadServer
import com.isdenmois.readish.transfers.lib.formatIpAddress
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import javax.inject.Inject
import com.isdenmois.readish.ui.R as UiR

@HiltViewModel
class TransfersViewModel
@Inject
constructor(@ApplicationContext private val applicationContext: Context) :
    ViewModel() {
    private val _uiState = MutableStateFlow(TransfersUiState())
    val uiState: StateFlow<TransfersUiState> = _uiState.asStateFlow()

    private val size = 512
    private val port = UploadServer.PORT

    private val uploadsDir by lazy {
        applicationContext.getString(UiR.string.books_dir)
    }

    private val receiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                checkLocalAddress()
            }
        }

    private val server by lazy {
        UploadServer(uploadsDir = File(uploadsDir), onFileUploaded = ::onFileUploaded)
    }

    init {
        server.start()

        val filter =
            IntentFilter().apply {
                addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
                addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            }

        applicationContext.registerReceiver(receiver, filter)
    }

    override fun onCleared() {
        super.onCleared()
        server.stop()
        applicationContext.unregisterReceiver(receiver)
    }

    fun openWifiSettings() {
        val intent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent(Settings.Panel.ACTION_WIFI)
            } else {
                Intent(Settings.ACTION_WIFI_SETTINGS)
            }.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        applicationContext.startActivity(intent)
    }

    private fun checkLocalAddress() {
        val address = getIpAddressInLocalNetwork()

        if (address.isBlank() || address == "0.0.0.0") {
            _uiState.value = TransfersUiState()
            return
        }

        val url = "http://$address:$port"

        try {
            val qr = getNetworkQR(url)?.asImageBitmap()

            _uiState.update { it.copy(address = url, qr = qr) }
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.update { it.copy(address = url, qr = null) }
        }
    }

    private fun getIpAddressInLocalNetwork(): String {
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager

        return formatIpAddress(wifiManager.connectionInfo.ipAddress)
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

    private fun onFileUploaded(file: File) {
        Toast.makeText(
            applicationContext,
            applicationContext.getString(R.string.uploaded_file, file.name),
            Toast.LENGTH_SHORT,
        ).show()
    }
}
