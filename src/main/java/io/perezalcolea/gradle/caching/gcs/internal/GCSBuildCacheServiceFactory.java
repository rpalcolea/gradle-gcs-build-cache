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
package io.perezalcolea.gradle.caching.gcs.internal;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import io.perezalcolea.gradle.caching.gcs.GCSBuildCache;
import org.gradle.caching.BuildCacheService;
import org.gradle.caching.BuildCacheServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class GCSBuildCacheServiceFactory implements BuildCacheServiceFactory<GCSBuildCache> {

    private static final Logger logger = LoggerFactory.getLogger(GCSBuildCacheServiceFactory.class);

    @Override
    public BuildCacheService createBuildCacheService(GCSBuildCache gcsBuildCache, Describer describer) {
        logger.debug("Start creating GCS build cache service");

        verifyConfig(gcsBuildCache);

        describer
                .type("GCS")
                .config("Bucket", gcsBuildCache.getBucket());

        if (gcsBuildCache.getPath() != null) {
            describer.config("Path", gcsBuildCache.getPath());
        }

        Storage storage = createStorage(gcsBuildCache);
        return new GCSBuildCacheService(storage, gcsBuildCache.getBucket(), gcsBuildCache.getPath());
    }

    private void verifyConfig(GCSBuildCache gcsBuildCache) {
        if(gcsBuildCache.getApplicationName() == null || gcsBuildCache.getApplicationName().isEmpty()) {
            throw new IllegalStateException("GCS build cache config has no applicationName configured");
        }
        if(gcsBuildCache.getBucket() == null || gcsBuildCache.getBucket().isEmpty()) {
            throw new IllegalStateException("GCS build cache config has no bucket configured");
        }
    }

    private Storage createStorage(GCSBuildCache gcsBuildCache) {
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            List<String> scopes = Collections.singletonList("https://www.googleapis.com/auth/devstorage.read_write");
            GoogleCredential credential = GoogleCredential.getApplicationDefault().createScoped(scopes);
            return new Storage.Builder(httpTransport, jsonFactory, credential).setApplicationName(gcsBuildCache.getApplicationName()).build();
        } catch (Exception e) {
            throw new RuntimeException("error initializing GCS Storage Client", e);
        }
    }
}
