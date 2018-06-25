package io.perezalcolea.gradle.caching.gcs.internal

import io.perezalcolea.gradle.caching.gcs.GCSBuildCache
import org.gradle.caching.BuildCacheService
import org.gradle.caching.BuildCacheServiceFactory
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

@Subject(GCSBuildCacheServiceFactory)
class GCSBuildCacheServiceFactorySpec extends Specification {

    @Shared
    GCSBuildCacheServiceFactory gcsBuildCacheServiceFactory

    @Shared
    BuildCacheServiceFactory.Describer describer

    def setupSpec() {
        gcsBuildCacheServiceFactory = new GCSBuildCacheServiceFactory()
        describer = new DummyBuildCacheDescriber()
    }

    def 'creates build cache service'() {
        given:
        GCSBuildCache gcsBuildCache = new GCSBuildCache()
        gcsBuildCache.setApplicationName("my-app")
        gcsBuildCache.setBucket("gradle-gcs-build-cache")

        when:
        BuildCacheService service = gcsBuildCacheServiceFactory.createBuildCacheService(gcsBuildCache, describer)

        then:
        service
    }


    def 'creates build cache service - with path'() {
        given:
        GCSBuildCache gcsBuildCache = new GCSBuildCache()
        gcsBuildCache.setApplicationName("my-app")
        gcsBuildCache.setBucket("my-bucket")
        gcsBuildCache.setPath("cache")

        when:
        BuildCacheService service = gcsBuildCacheServiceFactory.createBuildCacheService(gcsBuildCache, describer)

        then:
        service
    }


    def 'throws IllegalStateException - no application name'() {
        given:
        GCSBuildCache gcsBuildCache = new GCSBuildCache()
        gcsBuildCache.setBucket("my-bucket")

        when:
        gcsBuildCacheServiceFactory.createBuildCacheService(gcsBuildCache, describer)

        then:
        IllegalStateException e = thrown(IllegalStateException)
        e.message == "GCS build cache config has no applicationName configured"
    }

    def 'throws IllegalStateException - no bucket'() {
        given:
        GCSBuildCache gcsBuildCache = new GCSBuildCache()
        gcsBuildCache.setApplicationName("my-application")

        when:
        gcsBuildCacheServiceFactory.createBuildCacheService(gcsBuildCache, describer)

        then:
        IllegalStateException e = thrown(IllegalStateException)
        e.message == "GCS build cache config has no bucket configured"
    }

    private class DummyBuildCacheDescriber implements BuildCacheServiceFactory.Describer {
        @Override
        BuildCacheServiceFactory.Describer type(String type) {
            return this
        }

        @Override
        BuildCacheServiceFactory.Describer config(String name, String value) {
            return this
        }
    }
}
