package tech.ula.model.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import tech.ula.utils.Logger
import tech.ula.utils.SentryLogger
import tech.ula.utils.UlaFiles
import java.io.IOException
import java.net.UnknownHostException

class UrlProvider {
    fun getBaseUrl(): String {
        return "https://api.github.com/"
    }
}

class GithubApiClient(
    private val ulaFiles: UlaFiles,
    private val urlProvider: UrlProvider = UrlProvider(),
    private val logger: Logger = SentryLogger()
) {
    private val client = OkHttpClient()
    private val latestResults: HashMap<String, ReleasesResponse?> = hashMapOf()

    companion object {
        const val REPO_OWNER = "Vodkashot28"
        const val REPO_PREFIX = "UserLAnd-Next-Assets-"
        const val FALLBACK_TAG = "v1.0.0"
    }

    private fun getReleaseToUseForRepo(repo: String): String {
        return "latest"
    }

    private fun getAssetDownloadUrl(repo: String, filename: String): String {
        val arch = ulaFiles.getArchType()
        return "https://github.com/$REPO_OWNER/$REPO_PREFIX$repo/releases/download/$FALLBACK_TAG/$arch-$filename"
    }

    @Throws(IOException::class)
    suspend fun getAssetsListDownloadUrl(repo: String): String = withContext(Dispatchers.IO) {
        val result = latestResults[repo] ?: queryLatestRelease(repo)

        return@withContext result.assets.find { it.name == "${ulaFiles.getArchType()}-assets.txt" }?.downloadUrl
            ?: throw IOException("Asset not found: ${ulaFiles.getArchType()}-assets.txt in repo $repo")
    }

    @Throws(IOException::class)
    suspend fun getLatestReleaseVersion(repo: String): String = withContext(Dispatchers.IO) {
        val result = latestResults[repo] ?: queryLatestRelease(repo)

        return@withContext result.tag
    }

    @Throws(IOException::class)
    suspend fun getAssetEndpoint(assetType: String, repo: String): String = withContext(Dispatchers.IO) {
        val result = latestResults[repo] ?: queryLatestRelease(repo)
        val assetName = "${ulaFiles.getArchType()}-$assetType"

        return@withContext result.assets.find { it.name == assetName }?.downloadUrl
            ?: throw IOException("Asset not found: $assetName in repo $repo")
    }

    suspend fun getAssetEndpointOrFallback(assetType: String, repo: String): String {
        return getAssetEndpointsWithMirrors(assetType, repo).first()
    }

    suspend fun getAssetEndpointsWithMirrors(assetType: String, repo: String): List<String> {
        val primaryUrl = try {
            getAssetEndpoint(assetType, repo)
        } catch (err: IOException) {
            getAssetDownloadUrl(repo, assetType)
        }
        val mirrorUrls = buildMirrorUrls(assetType, repo)
        return listOf(primaryUrl) + mirrorUrls
    }

    suspend fun getLatestReleaseVersionOrFallback(repo: String): String {
        return try {
            getLatestReleaseVersion(repo)
        } catch (err: IOException) {
            FALLBACK_TAG
        }
    }

    private fun buildMirrorUrls(assetType: String, repo: String): List<String> {
        val arch = ulaFiles.getArchType()
        val filename = "$arch-$assetType"
        val repoFull = "$REPO_PREFIX$repo"
        val tag = FALLBACK_TAG
        return listOf(
            "https://github.com/$REPO_OWNER/$repoFull/releases/download/$tag/$filename",
            "https://cdn.jsdelivr.net/gh/$REPO_OWNER/$repoFull@$tag/$filename"
        )
    }

    @Throws(IOException::class, UnknownHostException::class)
    private suspend fun queryLatestRelease(repo: String): ReleasesResponse = withContext(Dispatchers.IO) {
        val releaseToUse = getReleaseToUseForRepo(repo)
        val base = urlProvider.getBaseUrl()
        val url = base + "repos/$REPO_OWNER/$REPO_PREFIX$repo/releases/$releaseToUse"
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(ReleasesResponse::class.java)
        val request = Request.Builder()
                .url(url)
                .build()
        val response = try {
            client.newCall(request).execute()
        } catch (err: UnknownHostException) {
            logger.addExceptionBreadcrumb(err)
            throw err
        }
        if (!response.isSuccessful) {
            response.close()
            val err = IOException("Unexpected code: $response")
            logger.addExceptionBreadcrumb(err)
            throw err
        }

        val result = response.use {
            val body = it.body ?: throw IOException("Empty response body from $url")
            adapter.fromJson(body.source()) ?: throw IOException("Failed to parse response from $url")
        }
        latestResults[repo] = result
        return@withContext result
    }

    @JsonClass(generateAdapter = true)
    internal data class ReleasesResponse(
        val url: String,
        val name: String,
        @Json(name = "tag_name") val tag: String,
        val assets: List<GithubAsset>
    )

    @JsonClass(generateAdapter = true)
    internal data class GithubAsset(
        val url: String,
        val name: String,
        @Json(name = "browser_download_url") val downloadUrl: String
    )
}
