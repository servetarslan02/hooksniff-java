<h1 align="center">
    <a style="text-decoration: none" href="https://hooksniff.vercel.app">
      <p align="center">🪝 HookSniff - Reliable Webhook Delivery</p>
    </a>
</h1>
<h2 align="center">
  <a href="https://hooksniff.vercel.app">Website</a> | <a href="https://hooksniff.vercel.app/docs">Documentation</a> | <a href="https://github.com/servetarslan02/HookSniff/discussions">Community</a>
</h2>

Java library for interacting with the HookSniff API and verifying webhook signatures.

[![Maven Central](https://img.shields.io/maven-central/v/io.github.servetarslan02/hooksniff-sdk?label=maven-central)](https://central.sonatype.com/artifact/io.github.servetarslan02/hooksniff-sdk)

## Features

- ✅ Full API support
- ✅ Webhook signature verification (HMAC-SHA256)
- ✅ Java 1.8+ compatible

## Installation

### Maven

```xml
<dependency>
  <groupId>io.github.servetarslan02</groupId>
  <artifactId>hooksniff-sdk</artifactId>
  <version>1.1.1</version>
</dependency>
```

### Gradle

```groovy
implementation "io.github.servetarslan02:hooksniff-sdk:1.1.1"
```

## Usage

See [API documentation](https://hooksniff.vercel.app/docs) for detailed usage.

## Development

### Requirements

- Java 1.8+
- Gradle

### Build

```sh
./gradlew build
```

### Test

```sh
./gradlew test
```

### Publish to Maven Central

```sh
./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository
```

## License

MIT
