package io.perezalcolea.gradle.caching.gcs

import io.perezalcolea.gradle.caching.gcs.internal.GCSBuildCacheServiceFactory
import org.gradle.api.initialization.Settings
import org.gradle.caching.configuration.BuildCacheConfiguration
import spock.lang.Specification

class GCSBuildCachePluginSpec extends Specification {

    def 'applies plugin'() {
        setup:
        Settings mockSettings = Mock(Settings)
        BuildCacheConfiguration  mockBuildCacheConfiguration = Mock(BuildCacheConfiguration)

        when:
        GCSBuildCachePlugin plugin = new GCSBuildCachePlugin()
        plugin.apply(mockSettings)

        then:
        1 * mockSettings.getBuildCache() >> mockBuildCacheConfiguration

        mockBuildCacheConfiguration.registerBuildCacheService(GCSBuildCache, GCSBuildCacheServiceFactory)
    }
}
