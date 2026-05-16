package tech.ula.utils.preferences

import android.content.Context

class AppsPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("apps", Context.MODE_PRIVATE)

    fun setDistributionsList(distributionList: Set<String>) {
        with(prefs.edit()) {
            putStringSet("distributionsList", distributionList)
            apply()
        }
    }

    fun getDistributionsList(): Set<String> {
        val default = setOf("debian", "ubuntu")
        return prefs.getStringSet("distributionsList", default) ?: default
    }
}