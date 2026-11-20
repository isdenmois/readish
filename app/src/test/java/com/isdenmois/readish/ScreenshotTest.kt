package com.isdenmois.readish

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.isdenmois.ebookparser.EBookFile
import com.isdenmois.readish.home.model.Book
import com.isdenmois.readish.home.model.LoadState
import com.isdenmois.readish.home.presentational.HomeContent
import com.isdenmois.readish.home.presentational.HomeUiState
import com.isdenmois.readish.transfers.presentational.TransfersContent
import com.isdenmois.readish.transfers.presentational.TransfersUiState
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
                        onOpenCurrentBook = {},
                        onOpenBook = {},
                        onShowBrightness = {},
                        onOpenOnyxHome = {},
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
                        onOpenCurrentBook = {},
                        onOpenBook = {},
                        onShowBrightness = {},
                        onOpenOnyxHome = {},
                    )
                }
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/home_empty.png")
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
