package tech.ula.utils

import android.content.Context
import java.io.File

class FallbackAssetProvider(private val context: Context) {

    fun copyToDistributionDirectory(distributionType: String, filesDirPath: String): Boolean {
        val targetDir = File("$filesDirPath/$distributionType")
        targetDir.mkdirs()

        val assetManager = context.assets
        val fallbackDir = "fallback"
        val files: Array<String>
        try {
            files = assetManager.list(fallbackDir) ?: return false
        } catch (e: Exception) {
            return false
        }

        var allCopied = true
        for (filename in files) {
            try {
                val targetFile = File(targetDir, filename)
                if (targetFile.exists()) continue
                assetManager.open("$fallbackDir/$filename").use { input ->
                    targetFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                targetFile.setExecutable(true, false)
            } catch (e: Exception) {
                allCopied = false
            }
        }
        return allCopied
    }
}
