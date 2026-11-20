package com.isdenmois.readish

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.isdenmois.ebookparser.EBookFile
import com.isdenmois.readish.apps.model.AppEntry
import com.isdenmois.readish.apps.presentational.AppsContent
import com.isdenmois.readish.apps.presentational.AppsUiState
import com.isdenmois.readish.home.model.Book
import com.isdenmois.readish.home.presentational.HomeContent
import com.isdenmois.readish.home.presentational.HomeUiState
import com.isdenmois.readish.library.model.LibraryBook
import com.isdenmois.readish.library.presentational.LibraryContent
import com.isdenmois.readish.library.presentational.LibraryUiState
import com.isdenmois.readish.transfers.presentational.TransfersContent
import com.isdenmois.readish.transfers.presentational.TransfersUiState
import com.isdenmois.readish.ui.LoadState
import com.isdenmois.readish.ui.theme.ReadishTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [31], qualifiers = "w800dp-h1280dp-mdpi")
class ScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun home_populated() {
        composeRule.setContent {
            ReadishTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    HomeContent(
                        state = populatedState(),
                        onOpenTransfers = {},
                        onOpenLibrary = {},
                        onOpenCurrentBook = {},
                        onOpenBook = {},
                        onShowBrightness = {},
                        onOpenApps = {},
                    )
                }
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/home_populated.png")
    }

    @Test
    fun home_empty() {
        composeRule.setContent {
            ReadishTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    HomeContent(
                        state = HomeUiState(
                            books = LoadState.Success(emptyList()),
                            latest = LoadState.Success(emptyList()),
                        ),
                        onOpenTransfers = {},
                        onOpenLibrary = {},
                        onOpenCurrentBook = {},
                        onOpenBook = {},
                        onShowBrightness = {},
                        onOpenApps = {},
                    )
                }
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/home_empty.png")
    }

    @Test
    fun library_populated() {
        composeRule.setContent {
            ReadishTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    LibraryContent(
                        state = LibraryUiState(books = LoadState.Success(libraryBooks())),
                        onBack = {},
                        onOpenBook = {},
                        onSort = {},
                        onFilter = {},
                        onPreviousPage = {},
                        onNextPage = {},
                    )
                }
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/library_populated.png")
    }

    @Test
    fun library_no_matches() {
        composeRule.setContent {
            ReadishTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    LibraryContent(
                        state = LibraryUiState(
                            books = LoadState.Success(libraryBooks()),
                            filter = "zzz",
                        ),
                        onBack = {},
                        onOpenBook = {},
                        onSort = {},
                        onFilter = {},
                        onPreviousPage = {},
                        onNextPage = {},
                    )
                }
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/library_no_matches.png")
    }

    @Test
    fun transfers() {
        composeRule.setContent {
            ReadishTheme {
                TransfersContent(
                    state = TransfersUiState(address = "http://192.168.1.42:8083"),
                    onBack = {},
                    onOpenWifiSettings = {},
                )
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/transfers.png")
    }

    @Test
    fun apps_populated() {
        composeRule.setContent {
            ReadishTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppsContent(
                        state = AppsUiState(apps = LoadState.Success(apps(25))),
                        onBack = {},
                        onOpenOnyxHome = {},
                        onOpenApp = {},
                        onPreviousPage = {},
                        onNextPage = {},
                    )
                }
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/apps_populated.png")
    }

    @Test
    fun apps_empty() {
        composeRule.setContent {
            ReadishTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppsContent(
                        state = AppsUiState(apps = LoadState.Success(emptyList())),
                        onBack = {},
                        onOpenOnyxHome = {},
                        onOpenApp = {},
                        onPreviousPage = {},
                        onNextPage = {},
                    )
                }
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/apps_empty.png")
    }

    private fun apps(count: Int) = APP_LABELS.take(count).mapIndexed { index, label ->
        AppEntry(
            packageName = "com.example.${label.lowercase()}",
            label = label,
            icon = if (index % 4 == 3) null else appIcon(shade = 40 + index * 8),
        )
    }

    private fun appIcon(shade: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888)
        val paint = Paint().apply {
            color = android.graphics.Color.rgb(shade, shade, shade)
            isAntiAlias = true
        }

        Canvas(bitmap).drawCircle(32f, 32f, 26f, paint)

        return bitmap
    }

    private fun libraryBooks() = listOf(
        Triple("Dune", "Frank Herbert", "dune.fb2"),
        Triple("Neuromancer", "William Gibson", "neuromancer.epub"),
        Triple("Solaris", "Stanisław Lem", "solaris.fb2"),
        Triple("The Left Hand of Darkness", "Ursula K. Le Guin", "left-hand.epub"),
        Triple("Piranesi", "Susanna Clarke", "piranesi.epub"),
        Triple("Roadside Picnic", "Arkady Strugatsky", "picnic.fb2"),
        Triple("Blindsight", "Peter Watts", "blindsight.epub"),
        Triple("Hyperion", "Dan Simmons", "hyperion.fb2"),
        Triple("Exhalation", "Ted Chiang", "exhalation.epub"),
        Triple("Anathem", "Neal Stephenson", "anathem.fb2"),
        Triple("Children of Time", "Adrian Tchaikovsky", "children-of-time.epub"),
        Triple("The Dispossessed", "Ursula K. Le Guin", "dispossessed.fb2"),
        Triple("A Wizard of Earthsea", "Ursula K. Le Guin", "earthsea.epub"),
    ).mapIndexed { index, (title, author, fileName) ->
        LibraryBook(
            file = File(fileName),
            title = title,
            author = author,
            cover = null,
            modifiedAt = (13 - index).toLong(),
        )
    }

    private fun populatedState() = HomeUiState(
        books = LoadState.Success(
            listOf(
                bookOf(1, "Dune", "Frank Herbert", "dune.fb2"),
                bookOf(2, "Neuromancer", "William Gibson", "neuromancer.epub"),
                bookOf(3, "Solaris", "Stanisław Lem", "solaris.fb2"),
                bookOf(4, "The Left Hand of Darkness", "Ursula K. Le Guin", "left-hand.epub"),
            ),
        ),
        latest = LoadState.Success(
            listOf(
                fileOf("Piranesi", "Susanna Clarke", "piranesi.epub"),
                fileOf("Roadside Picnic", "Arkady Strugatsky", "picnic.fb2"),
                fileOf("Blindsight", "Peter Watts", "blindsight.epub"),
            ),
        ),
    )

    private fun bookOf(id: Int, title: String, author: String, fileName: String) = Book(
        id = id,
        title = title,
        author = author,
        size = 1024 * 800,
        position = 1024 * 320,
        readTime = 10,
        file = File(fileName),
        cover = null,
    )

    private fun fileOf(title: String, author: String, fileName: String) = EBookFile(
        title = title,
        author = author,
        file = File(fileName),
        cover = null,
    )
}

private val APP_LABELS = listOf(
    "Browser",
    "Calculator",
    "Calendar",
    "Camera",
    "Clock",
    "Contacts",
    "Dictionary",
    "Email",
    "Files",
    "Gallery",
    "Maps",
    "Messages",
    "Music",
    "News",
    "Notes",
    "Photos",
    "Podcasts",
    "Recorder",
    "Settings",
    "Store",
    "Tasks",
    "Terminal",
    "Video",
    "Weather",
    "Books",
)
