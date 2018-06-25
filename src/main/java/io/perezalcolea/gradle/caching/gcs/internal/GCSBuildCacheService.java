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

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.StorageObject;
import org.gradle.caching.BuildCacheEntryReader;
import org.gradle.caching.BuildCacheEntryWriter;
import org.gradle.caching.BuildCacheException;
import org.gradle.caching.BuildCacheKey;
import org.gradle.caching.BuildCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class GCSBuildCacheService implements BuildCacheService {

    private static final String BUILD_CACHE_CONTENT_TYPE = "application/vnd.gradle.build-cache-artifact";
    private static final Logger logger = LoggerFactory.getLogger(GCSBuildCacheService.class);

    private final Storage storage;
    private final String bucketName;
    private final String path;

    public GCSBuildCacheService(Storage storage, String bucketName, String path) {
        this.storage = storage;
        this.bucketName = bucketName;
        this.path = path;
    }

    @Override
    public boolean load(BuildCacheKey buildCacheKey, BuildCacheEntryReader buildCacheEntryReader) throws BuildCacheException {
        final String bucketPath = getBucketPath(buildCacheKey);
        try (InputStream is = storage.objects().get(bucketName, bucketPath).executeMediaAsInputStream()) {
            buildCacheEntryReader.readFrom(is);
            return true;
        } catch (IOException e) {
            logger.info("Did not find cache item '{}' in GCS bucket", bucketPath);
            return false;
        }
    }

    @Override
    public void store(BuildCacheKey buildCacheKey, BuildCacheEntryWriter buildCacheEntryWriter) throws BuildCacheException {
        final String bucketPath = getBucketPath(buildCacheKey);
        logger.info("Start storing cache entry '{}' in GCS bucket", bucketPath);
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            buildCacheEntryWriter.writeTo(os);
            try (InputStream is = new ByteArrayInputStream(os.toByteArray())) {
                InputStreamContent content = new InputStreamContent(BUILD_CACHE_CONTENT_TYPE, is);
                StorageObject metadataObject = new StorageObject().setMetadata(new HashMap<>());
                Storage.Objects.Insert insertObject = storage.objects().insert(bucketName, metadataObject, content).setName(bucketPath);
                insertObject.getMediaHttpUploader().setDisableGZipContent(true);
                insertObject.execute();
            }
        } catch (IOException e) {
            throw new BuildCacheException("Error while storing cache object in GCS bucket", e);
        }
    }

    @Override
    public void close() throws IOException {
        //Not necessary for GCS client
    }

    private String getBucketPath(BuildCacheKey key) {
        if (path == null || path.length() == 0) {
            return key.getHashCode();
        }
        return (path + '/' + key.getHashCode()).replaceAll("[/]+", "/");
    }
}
