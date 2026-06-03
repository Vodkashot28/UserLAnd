package tech.ula.model.remote

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import tech.ula.model.entities.App
import tech.ula.utils.HttpStream
import tech.ula.utils.Logger
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class GithubAppsFetcherTest {

    @get:Rule val tempFolder = TemporaryFolder()

    private lateinit var testFilesDir: File

    @Mock lateinit var mockHttpStream: HttpStream

    @Mock lateinit var logger: Logger

    private lateinit var githubAppsFetcher: GithubAppsFetcher

    @Before
    fun setup() {
        testFilesDir = tempFolder.root
        githubAppsFetcher = GithubAppsFetcher(testFilesDir.path, mockHttpStream, logger)
    }

    @Test
    fun `returns hardcoded app list`() {
        val result = runBlocking {
            githubAppsFetcher.fetchAppsList()
        }

        assertEquals(5, result.size)

        val debian12 = result.find { it.name == "debian12" }
        assertTrue(debian12 != null)
        assertEquals("Distribution", debian12!!.category)
        assertEquals("debian12", debian12.filesystemRequired)
        assertTrue(debian12.supportsCli)
        assertTrue(debian12.supportsGui)

        val zencode = result.find { it.name == "zencode-server" }
        assertTrue(zencode != null)
        assertEquals("Development", zencode.category)
        assertTrue(zencode.supportsCli)
        assertFalse(zencode.supportsGui)

        val vscode = result.find { it.name == "vscode-server" }
        assertTrue(vscode != null)
        assertEquals("Development", vscode.category)
        assertFalse(vscode.supportsCli)
        assertTrue(vscode.supportsGui)

        val backup = result.find { it.name == "fs-backup" }
        assertTrue(backup != null)
        assertEquals("Utility", backup.category)
        assertTrue(backup.supportsCli)
        assertFalse(backup.supportsGui)

        val dashboard = result.find { it.name == "zencode-dashboard" }
        assertTrue(dashboard != null)
        assertEquals("Development", dashboard.category)
        assertEquals("debian12", dashboard.filesystemRequired)
        assertTrue(dashboard.supportsCli)
        assertFalse(dashboard.supportsGui)
    }

    @Test
    fun `fetchAppIcon is a no-op and creates no file`() {
        val app = App(name = "debian12")
        runBlocking {
            githubAppsFetcher.fetchAppIcon(app)
        }
        val iconFile = File(testFilesDir.path, "apps/debian12/debian12.png")
        assertFalse(iconFile.exists())
    }

    @Test
    fun `fetchAppDescription writes description file`() {
        val app = App(name = "debian12")
        runBlocking {
            githubAppsFetcher.fetchAppDescription(app)
        }
        val descFile = File(testFilesDir.path, "apps/debian12/debian12.txt")
        assertTrue(descFile.exists())
        assertEquals("An AI/ML-ready distribution optimized for local server runtimes.", descFile.readText())
    }

    @Test
    fun `fetchAppDescription writes correct descriptions per app`() {
        val apps = listOf(
            App(name = "debian12") to "An AI/ML-ready distribution optimized for local server runtimes.",
            App(name = "zencode-server") to "ZenCode dynamic local backend context server and hybrid MCP host.",
            App(name = "vscode-server") to "Visual Studio Code Server for remote browser-based engineering workspace.",
            App(name = "fs-backup") to "Automated filesystem snapshot and asset backup utility.",
            App(name = "zencode-dashboard") to "Terminal User Interface to monitor and control local ZenCode-Server nodes."
        )
        for ((app, expectedDesc) in apps) {
            runBlocking {
                githubAppsFetcher.fetchAppDescription(app)
            }
            val descFile = File(testFilesDir.path, "apps/${app.name}/${app.name}.txt")
            assertTrue("Description file missing for ${app.name}", descFile.exists())
            assertEquals("Wrong description for ${app.name}", expectedDesc, descFile.readText())
        }
    }

    @Test
    fun `fetchAppScript writes script file`() {
        val app = App(name = "zencode-server")
        runBlocking {
            githubAppsFetcher.fetchAppScript(app)
        }
        val scriptFile = File(testFilesDir.path, "apps/zencode-server/zencode-server.sh")
        assertTrue(scriptFile.exists())
        assertTrue(scriptFile.readText().contains("zencode-server"))
    }

    @Test
    fun `fetchAppScript writes debian12 as empty script`() {
        val app = App(name = "debian12")
        runBlocking {
            githubAppsFetcher.fetchAppScript(app)
        }
        val scriptFile = File(testFilesDir.path, "apps/debian12/debian12.sh")
        assertTrue(scriptFile.exists())
        assertEquals("", scriptFile.readText())
    }

    @Test
    fun `fetchAppScript writes dashboard TUI script`() {
        val app = App(name = "zencode-dashboard")
        runBlocking {
            githubAppsFetcher.fetchAppScript(app)
        }
        val scriptFile = File(testFilesDir.path, "apps/zencode-dashboard/zencode-dashboard.sh")
        assertTrue(scriptFile.exists())
        assertTrue(scriptFile.readText().contains("ZENCODE-SERVER DASHBOARD"))
        assertTrue(scriptFile.readText().contains("box-drawing"))
    }
}
