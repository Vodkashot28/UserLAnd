package tech.ula.ui

import tech.ula.model.entities.App

class DebianAppsFragment : BaseFilteredAppsListFragment() {
    override fun categoryFilter(apps: List<App>): List<App> = apps.filter { it.name == "debian12" }
}
