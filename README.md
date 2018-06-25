# gradle-gcs-build-cache

Google Cloud Storage Gradle build cache implementation

[![Apache License 2.0](https://img.shields.io/badge/License-Apache%20License%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Build Status](https://travis-ci.org/rpalcolea/gradle-gcs-build-cache.svg?branch=master)](https://travis-ci.org/rpalcolea/gradle-gcs-build-cache)

Inspired in https://plugins.gradle.org/plugin/ch.myniva.s3-build-cache

# Usage

Please note that this plugin is not yet ready for production. Feedback though is very welcome. Please open an issue if you find a bug or have an idea for an improvement.

## Apply plugin

```groovy
plugins {
  id "io.perezalcolea.gcs-build-cache" version "0.1.0"
}
```

## Configuration

The GCS build cache implementation has a few configuration options:

| Configuration Key | Description | Mandatory | Default Value |
| ----------------- | ----------- | --------- | ------------- |
| `bucket` | The name of the AWS GCS bucket where cache objects should be stored. | yes | |
| `path` | The path under which all cache objects should be stored. | no | |


The `buildCache` configuration block might look like this:

```
 apply plugin: 'io.perezalcolea.gcs-build-cache'
 
 ext.isCiServer = System.getenv().containsKey("CI")
 
 buildCache {
     local {
         enabled = !isCiServer
     }
     remote(io.perezalcolea.gradle.caching.gcs.GCSBuildCache) {
         bucket = 'your-bucket'
         push = isCiServer
     }
 }

```

More details about configuring the Gradle build cache can be found in the
[official Gradle documentation](https://docs.gradle.org/current/userguide/build_cache.html#sec:build_cache_configure).

### GCS credentials

The plugin uses Google Cloud service accounts https://cloud.google.com/storage/docs/authentication#generating-a-private-key

You can create your JSON service account file and set `GOOGLE_APPLICATION_CREDENTIALS` environment variable with the file path.
