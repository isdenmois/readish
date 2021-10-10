package com.isdenmois.readish.screens.home

import android.content.ComponentName
import android.content.Context
import android.content.Context.WIFI_SERVICE
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isdenmois.readish.shared.api.alreader.Book
import com.isdenmois.readish.shared.api.alreader.BookRepository
import com.isdenmois.readish.shared.api.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.widget.Toast

import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import com.isdenmois.readish.shared.lib.MimeUtils
import com.isdenmois.readish.shared.api.parser.BookFile
import java.io.File
import java.lang.Exception

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val repository: BookRepository
) : ViewModel() {
    var bookList = mutableStateOf<Resource<List<Book>>>(Resource.loading(null))
    var fileList = mutableStateOf<Resource<List<BookFile>>>(Resource.loading(null))

    fun loadBooks() {
        bookList.value = Resource.loading()
        fileList.value = Resource.loading()

        viewModelScope.launch {
            val books = repository.getCurrentBooks()

            bookList.value = Resource.success(books)
        }

        viewModelScope.launch {
            val files = repository.getLatestAddedBooks()

            fileList.value = Resource.success(files)
        }
    }

    fun openAlReader() {
        val packageManager = applicationContext.packageManager
        val intent = packageManager.getLaunchIntentForPackage("com.neverland.alreaderprofs")

        applicationContext.startActivity(intent)
    }

    fun openBook(path: String) {
        val packageManager = applicationContext.packageManager
        val file = File(path)
        val uri = Uri.fromFile(file)
        val type = MimeUtils.getByFileName(file.name)

        val intent =
            packageManager.getLaunchIntentForPackage("com.neverland.alreaderprofs")?.apply {
                setDataAndType(uri, type)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

        try {
            applicationContext.startActivity(intent);
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Can't start application", Toast.LENGTH_SHORT).show()
        }
    }

    fun openTransfers() {
        val wifi = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager

        if (!wifi.isWifiEnabled) {
            wifi.isWifiEnabled = true
        }

        val intent = Intent().apply {
            component = ComponentName("com.onyx", "com.onyx.reader.main.ui.MainActivity")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            applicationContext.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Can't start application", Toast.LENGTH_SHORT).show()
        }
    }

    fun openOnyxHome() {
        val intent = Intent().apply {
            component = ComponentName("com.onyx", "com.onyx.reader.main.ui.MainActivity")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            applicationContext.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Can't start application", Toast.LENGTH_SHORT).show()
        }
    }

    fun showSystemBrightnessDialog() {
        try {
            val intent = Intent("action.show.brightness.dialog")

            applicationContext.sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
