# AEM Utils

<p align="center">
    <a href="https://maven-badges.sml.io/sonatype-central/com.merkle.oss.aem/aem-utils/">
        <img alt="Sonatype Central Version" src="https://img.shields.io/maven-central/v/com.merkle.oss.aem/aem-utils?strategy=highestVersion&logo=sonatype&logoColor=white&logoSize=auto&label=sonatype-central&color=blue&link=https%3A%2F%2Fmaven-badges.sml.io%2Fsonatype-central%2Fcom.merkle.oss.aem%2Faem-utils%2F"></a>
    <a href="https://javadoc.io/doc/com.merkle.oss.aem/aem-utils">
        <img alt="Javadoc" src="https://javadoc.io/badge2/com.merkle.oss.aem/aem-utils/javadoc.svg?color=yellow"></a>
    <a href="https://sonarcloud.io/summary/overall?id=merkle-open_aem-utils&branch=master">
        <img alt="SonarQube - Quality Gate" src="https://sonarcloud.io/api/project_badges/measure?project=merkle-open_aem-utils&metric=alert_status"></a>
    <a href="https://sonarcloud.io/summary/overall?id=merkle-open_aem-utils&branch=master">
        <img alt="SonarQube - Security Rating" src="https://sonarcloud.io/api/project_badges/measure?project=merkle-open_aem-utils&metric=security_rating"></a>    
    <a href="https://sonarcloud.io/summary/overall?id=merkle-open_aem-utils&branch=master">
        <img alt="SonarQube - Reliability Rating" src="https://sonarcloud.io/api/project_badges/measure?project=merkle-open_aem-utils&metric=reliability_rating"></a>
    <a href="https://sonarcloud.io/summary/overall?id=merkle-open_aem-utils&branch=master">
        <img alt="SonarQube - Maintainability Rating" src="https://sonarcloud.io/api/project_badges/measure?project=merkle-open_aem-utils&metric=sqale_rating"></a>
    <a href="https://sonarcloud.io/summary/overall?id=merkle-open_aem-utils&branch=master">
        <img alt="SonarQube - Code Coverage" src="https://sonarcloud.io/api/project_badges/measure?project=merkle-open_aem-utils&metric=coverage"></a>    
    <a href="https://sonarcloud.io/summary/overall?id=merkle-open_aem-utils&branch=master">
        <img alt="SonarQube - Vulnerabilities" src="https://sonarcloud.io/api/project_badges/measure?project=merkle-open_aem-utils&metric=vulnerabilities"></a>
    <a href="https://github.com/merkle-open/aem-utils/actions/workflows/verify-snapshot.yml">
        <img alt="CI SNAPSHOT - Github Action" src="https://img.shields.io/github/actions/workflow/status/merkle-open/aem-utils/verify-snapshot.yml?branch=develop&logo=githubactions&logoColor=white&logoSize=auto&label=ci-snapshot&link=https%3A%2F%2Fgithub.com%2Fmerkle-open%2Faem-utils%2Factions%2Fworkflows%2Fverify-snapshot.yml"></a>
    <a href="https://github.com/merkle-open/aem-utils/actions/workflows/deploy-snapshot.yml">
        <img alt="Deploy SNAPSHOT - Github Action" src="https://img.shields.io/github/actions/workflow/status/merkle-open/aem-utils/deploy-snapshot.yml?branch=develop&logo=githubactions&logoColor=white&logoSize=auto&label=deploy-snapshot&link=https%3A%2F%2Fgithub.com%2Fmerkle-open%2Faem-utils%2Factions%2Fworkflows%2Fdeploy-snapshot.yml"></a>
</p>

The **AEM Utils** is a AEM developer toolkit engineered to modernize and streamline the development
experience on Adobe Experience Manager.

The library is designed to provide type-safe, functional handling for legacy AEM APIs, transforming traditional JCR and
Sling interactions into a more predictable and robust developer experience.

This toolkit provides a foundational set of services and utilities essential for almost all AEM projects. Whether you
are building complex search components, handling hierarchical content inheritance, or managing resource lifecycles,
these utilities offer the reliable core functionality required for modern AEM implementations.

## Features

### Utilities

| Feature                                                                              | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
|:-------------------------------------------------------------------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **[constants](core/src/main/java/com/merkle/oss/aem/utils/constants)**               | <ul><li>[CookieNameBuilder](core/src/main/java/com/merkle/oss/aem/utils/constants/CookieNameBuilder.java): A builder for constructing standardized, namespaced cookie names</li><li>[FileType](core/src/main/java/com/merkle/oss/aem/utils/constants/FileType.java): Common file types and their associated metadata used within the AEM ecosystem</li></ul>                                                                                                                                                                                                                                                                                                                                                                      |
| **[annotations](core/src/main/java/com/merkle/oss/aem/utils/injectors/annotations)** | <ul><li>[AdaptTo](core/src/main/java/com/merkle/oss/aem/utils/injectors/annotations/AdaptTo.java): Custom Sling Models injection annotation that triggers an adaptation via `org.apache.sling.api.adapter.Adaptable`</li><li>[PageProperty](core/src/main/java/com/merkle/oss/aem/utils/injectors/annotations/PageProperty.java): Custom Sling Models injection annotation for retrieving properties from an `com.day.cq.wcm.api.Page`</li></ul>                                                                                                                                                                                                                                                                                  |
| **[java](core/src/main/java/com/merkle/oss/aem/utils/java)**                         | <ul><li>[ClassUtil](core/src/main/java/com/merkle/oss/aem/utils/java/ClassUtil.java): Common Java reflection and class-handling tasks</li><li>[FunctionalUtil](core/src/main/java/com/merkle/oss/aem/utils/java/FunctionalUtil.java): Provides functional programming utilities to bridge Java APIs with modern Stream capabilities</li></ul>                                                                                                                                                                                                                                                                                                                                                                                     |
| **[jcr](core/src/main/java/com/merkle/oss/aem/utils/jcr)**                           | <ul><li>[PermissionUtil](core/src/main/java/com/merkle/oss/aem/utils/jcr/PermissionUtil.java): JCR Access Control, User Management, and Permission checking</li></ul>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| **[link](core/src/main/java/com/merkle/oss/aem/utils/link)**                         | <ul><li>[Links](core/src/main/java/com/merkle/oss/aem/utils/link/constants/Links.java): Constants and definitions for URL handling and URI manipulation within AEM</li><li>[LinkExternalizerUtil](core/src/main/java/com/merkle/oss/aem/utils/link/LinkExternalizerUtil.java): Transforming resource paths into absolute, externalized URLs</li><li>[LinkMappingUtil](core/src/main/java/com/merkle/oss/aem/utils/link/LinkMappingUtil.java): Applying Sling Resource Mapping to paths</li><li>[LinkUtil](core/src/main/java/com/merkle/oss/aem/utils/link/LinkUtil.java):  Validating, formatting, and manipulating URLs and JCR paths</li></ul>                                                                                 |
| **[query](core/src/main/java/com/merkle/oss/aem/utils/query)**                       | <ul><li>[PredicateProperties](core/src/main/java/com/merkle/oss/aem/utils/query/PredicateProperties.java): Provides predicate property paths used for `com.day.cq.search.Query`</li><li>[QueryResultHelper](core/src/main/java/com/merkle/oss/aem/utils/query/QueryResultHelper.java): Safe and efficient retrieval of `com.day.cq.search.Query` results</li><li>[QuerySearch](core/src/main/java/com/merkle/oss/aem/utils/query/QuerySearch.java): A builder-style utility to simplify the creation of `com.day.cq.search.Query` objects</li><li>[QuerySearchUtil](core/src/main/java/com/merkle/oss/aem/utils/query/QuerySearchUtil.java): providing static helper methods for `com.day.cq.search.Query` construction</li></ul> |
| **[sling](core/src/main/java/com/merkle/oss/aem/utils/sling)**                       | <ul><li>[ResourceUtil](core/src/main/java/com/merkle/oss/aem/utils/sling/ResourceUtil.java): For common `org.apache.sling.api.resource.Resource` operation focused on retrieval</li><li>[SlingUtil](core/src/main/java/com/merkle/oss/aem/utils/sling/SlingUtil.java): Provides type-safe shortcuts for object adaptation via `org.apache.sling.api.adapter.Adaptable`</li></ul>                                                                                                                                                                                                                                                                                                                                                  |
| **[wcm](core/src/main/java/com/merkle/oss/aem/utils/wcm)**                           | <ul><li>[PageManagerUtil](core/src/main/java/com/merkle/oss/aem/utils/wcm/PageManagerUtil.java): Shortcuts to interacting with `com.day.cq.wcm.api.PageManager`</li><li>[PageUtil](core/src/main/java/com/merkle/oss/aem/utils/wcm/PageUtil.java): Generic `com.day.cq.wcm.api.Page` operations and content traversion</li></ul>                                                                                                                                                                                                                                                                                                                                                                                                  |

### Services

| Feature                                                                                          | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
|:-------------------------------------------------------------------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **[cronjobs](core/src/main/java/com/merkle/oss/aem/utils/services/cronjobs)**                    | <ul><li>[AbstractSlingJobScheduler](core/src/main/java/com/merkle/oss/aem/utils/services/cronjobs/AbstractSlingJobScheduler.java): Facilitates the registration and management of Scheduled Sling Jobs using the `org.apache.sling.event.jobs.JobManager`</li></ul>                                                                                                                                                                                                                                                                                                                                         |
| **[inmemorycache](core/src/main/java/com/merkle/oss/aem/utils/services/inmemorycache/provider)** | <ul><li>[AbstractInMemoryCacheProviderService](core/src/main/java/com/merkle/oss/aem/utils/services/inmemorycache/provider/AbstractInMemoryCacheProviderService.java): Acts as a bridge to the central `InMemoryCacheService`. By extending this class, specific implementations can provide a simplified, type-safe API for a named cache.</li><li>[InMemoryCacheService](core/src/main/java/com/merkle/oss/aem/utils/services/inmemorycache/memory/InMemoryCacheService.java): A centralized service providing a simplified abstraction over the `com.github.benmanes.caffeine` caching library</li></ul> |
| **[runmode](core/src/main/java/com/merkle/oss/aem/utils/services/runmode)**                      | <ul><li>[RunModeService](core/src/main/java/com/merkle/oss/aem/utils/services/runmode/RunModeService.java): Service for detecting the current AEM execution environment</li></ul>                                                                                                                                                                                                                                                                                                                                                                                                                           |

## Installation

### Maven Dependency

Add the `aem-utils.all` and `aem-utils.core-bundle` artifact to the `<dependencies>` section

```xml

<dependency>
    <groupId>com.merkle.oss.aem</groupId>
    <artifactId>aem-utils.all</artifactId>
    <version>1.0.0</version>
    <type>zip</type>
</dependency>

```

```xml

<dependency>
    <groupId>com.merkle.oss.aem</groupId>
    <artifactId>aem-utils.core-bundle</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>

```

### Java support

Embed the package into your `core` module to make use of the Java API:

```xml

<dependency>
    <groupId>com.merkle.oss.aem</groupId>
    <artifactId>aem-utils.core-bundle</artifactId>
</dependency>

```

### Package embedding

Embed the package into your `all` deployment module using the `filevault-package-maven-plugin`:

```xml

<embedded>
    <groupId>com.merkle.oss.aem</groupId>
    <artifactId>aem-utils.all</artifactId>
    <target>/apps/{your/install/path}/install</target>
</embedded>

```

## Development

Build the full package

```
    mvn clean install -PautoInstallBundle
```

Build and deploy the full package to a local AEM Author:

```
    mvn clean install -PautoInstallPackage
```

## Compatibility & Requirements

### AEM Version

This tool requires **AEM Version 2025.9.x** or higher.

### Platform Support

- **AEM as a Cloud Service (AEMaaCS):** This tool is primarily designed and optimized for Cloud Service environments.
- **AEM On-Premise / Adobe Managed Services:** While the codebase is compatible with standard AEM On-Premise
  installations, please note that it has not been formally tested in these environments.
