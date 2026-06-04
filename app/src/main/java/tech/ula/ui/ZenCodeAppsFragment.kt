package tech.ula.ui

import tech.ula.model.entities.App

class ZenCodeAppsFragment : BaseFilteredAppsListFragment() {
    override fun categoryFilter(apps: List<App>): List<App> =
        apps.filter { it.name == "zencode-server" || it.name == "zencode-dashboard" }
}
