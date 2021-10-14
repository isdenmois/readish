package com.isdenmois.readish.shared.api.parser

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.isdenmois.readish.shared.lib.BitmapDecoder
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import javax.xml.parsers.DocumentBuilderFactory

class EPUBParser(private val path: String) : IBookParser {
    private val file = File(path)
    private lateinit var zipFile: ZipFile
    private var opfPath: String? = null
    private var xpp: XmlPullParser? = null

    override fun parse(): BookFile? {
        if (!file.canRead()) {
            return null
        }

        val start = System.currentTimeMillis()
        zipFile = ZipFile(file)

        zipFile.use {
            val xpp = getXpp() ?: return null
            var eventType = xpp.eventType

            var title: String? = null
            var imageID: String? = null
            var coverPath: String? = null

            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagname = xpp.name

                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (title == null && tagname == "dc:title") {
                            title = xpp.nextText()
                        }

                        if (imageID == null && tagname == "meta") {
                            if (xpp.getAttributeValue(null, "name") == "cover") {
                                imageID = xpp.getAttributeValue(null, "content")
                            }
                        }

                        if (imageID != null && tagname == "item" && imageID == xpp.getAttributeValue(null, "id")) {
                            coverPath = xpp.getAttributeValue(null, "href")
                            if (opfPath!!.contains('/')) {
                                coverPath = "${opfPath!!.substring(0, opfPath!!.lastIndexOf('/'))}/$coverPath"
                            }
                            break;
                        }
                    }
                    else -> {
                    }
                }

                eventType = xpp.next()
            }

            val coverEntry = if (coverPath != null) zipFile.getEntry(coverPath) else null
            val coverStream = if (coverEntry != null) zipFile.getInputStream(coverEntry) else getITunesArtwork()
            var cover: Bitmap? = null

            coverStream?.use {
                cover = BitmapDecoder.decodeStream(coverStream)
            }

            val file = BookFile(
                title = title ?: file.name,
                path = path,
                cover = cover,
            )
            Log.d("TextReader/EPUB", "Parse took " + (System.currentTimeMillis() - start))

            return file
        }

    }

    private fun getXpp(): XmlPullParser? {
        if (xpp != null) {
            return xpp
        }

        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        val builderFactory = DocumentBuilderFactory.newInstance()
        val docBuilder = builderFactory.newDocumentBuilder()
        val container: ZipEntry = zipFile.getEntry("META-INF/container.xml") ?: return null

        val doc = docBuilder.parse(zipFile.getInputStream(container))
        opfPath = doc.getElementsByTagName("rootfile")
            ?.item(0)?.attributes?.getNamedItem("full-path")?.textContent
            ?: return null

        val opfEntry = zipFile.getEntry(opfPath) ?: return null
        val stream = zipFile.getInputStream(opfEntry)

        parser.setInput(stream, "UTF-8")

        return parser
    }

    private fun getITunesArtwork(): InputStream? {
        val iTunesArtwork = zipFile.getEntry("iTunesArtwork") ?: return null

        return zipFile.getInputStream(iTunesArtwork)
    }
}
