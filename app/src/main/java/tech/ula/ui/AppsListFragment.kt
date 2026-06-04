package tech.ula.ui

import tech.ula.model.entities.App

class AppsListFragment : BaseFilteredAppsListFragment() {
    override fun categoryFilter(apps: List<App>): List<App> = apps
}
