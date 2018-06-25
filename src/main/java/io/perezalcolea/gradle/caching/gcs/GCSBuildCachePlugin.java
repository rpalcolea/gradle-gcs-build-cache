/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.perezalcolea.gradle.caching.gcs;

import io.perezalcolea.gradle.caching.gcs.internal.GCSBuildCacheServiceFactory;
import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.gradle.caching.configuration.BuildCacheConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCSBuildCachePlugin implements Plugin<Settings> {
    private static final Logger logger = LoggerFactory.getLogger(GCSBuildCachePlugin.class);

    @Override
    public void apply(Settings settings) {
        logger.info("Registering GCS build cache");
        BuildCacheConfiguration buildCacheConfiguration = settings.getBuildCache();
        buildCacheConfiguration.registerBuildCacheService(GCSBuildCache.class, GCSBuildCacheServiceFactory.class);
    }
}
