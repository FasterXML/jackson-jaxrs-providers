## Overview

This is a multi-module project that contains Jackson-based JAX-RS providers for following data formats:

* [JSON](https://github.com/FasterXML/jackson-core)
* [Smile](https://github.com/FasterXML/jackson-dataformat-smile) (binary JSON)
* [XML](https://github.com/FasterXML/jackson-dataformat-xml)
* [CBOR](https://github.com/FasterXML/jackson-dataformat-cbor) (another kind of binary JSON)

Providers implement JAX-RS `MessageBodyReader` and `MessageBodyWriter` handlers for specific
data formats. They also contain SPI settings for auto-registration.

[![Build Status](https://fasterxml.ci.cloudbees.com/job/jackson-jaxrs-providers-master/badge/icon)](https://fasterxml.ci.cloudbees.com/job/jackson-jaxrs-providers-master/)

## Status

As of Jackson 2.2, this module replaces individual JAX-RS provider modules that were used with earlier Jackson versions.

## Maven dependency

To use JAX-RS on Maven-based projects, use dependencies like:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.jaxrs</groupId>
  <artifactId>jackson-jaxrs-json-provider</artifactId>
  <version>2.3.3</version>
</dependency>
```

(above is for JSON provider; modify appropriately for other providers)

## Usage

Due to auto-registration, it should be possible to simply add Maven dependency
(or include jar if using other build systems) and let JAX-RS implementation discover
provider.
If this does not work you need to consult documentation of the JAX-RS implementation for details.

## Other

For documentation, downloads links, check out [Wiki](../../wiki)
