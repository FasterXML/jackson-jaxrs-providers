## Overview

This is a multi-module project that contains Jackson-based JAX-RS providers for following data formats:

* [JSON](https://github.com/FasterXML/jackson-core)
* [Smile](https://github.com/FasterXML/jackson-dataformat-smile) (binary JSON)
* [CBOR](https://github.com/FasterXML/jackson-dataformat-cbor) (another kind of binary JSON)
* [XML](https://github.com/FasterXML/jackson-dataformat-xml)
* [YAML](https://github.com/FasterXML/jackson-dataformat-yaml)

Providers implement JAX-RS `MessageBodyReader` and `MessageBodyWriter` handlers for specific
data formats. They also contain SPI settings for auto-registration.

[![Build Status](https://travis-ci.org/FasterXML/jackson-jaxrs-providers.svg?branch=master)](https://travis-ci.org/FasterXML/jackson-jaxrs-providers)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.fasterxml.jackson.jaxrs/jackson-jaxrs-json-provider/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.fasterxml.jackson.jaxrs/jackson-jaxrs-json-provider/)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/com.fasterxml.jackson.jaxrs/jackson-jaxrs-json-provider/badge.svg)](http://www.javadoc.io/doc/com.fasterxml.jackson.jaxrs/jackson-jaxrs-json-provider)

## Status

Module is fully functional and considered mature.

## Maven dependency

To use JAX-RS on Maven-based projects, use dependencies like:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.jaxrs</groupId>
  <artifactId>jackson-jaxrs-json-provider</artifactId>
  <version>2.7.4</version>
</dependency>
```

(above is for JSON provider; modify appropriately for other providers)

## Usage: registering providers

Due to auto-registration, it should be possible to simply add Maven dependency
(or include jar if using other build systems) and let JAX-RS implementation discover
provider.
If this does not work you need to consult documentation of the JAX-RS implementation for details.  

To use Jackson with Jersey see [their documentation](https://jersey.java.net/documentation/latest/media.html#json.jackson).

### Usage: registering supporting datatypes module

As of Jackson 2.8, there is a small supporting datatype module, `jackson-datatype-jaxrs` (see under `datatypes/`).
It will not be auto-registered automatically (unless user calls `ObjectMapper.findAndRegisterModules()`); instead,
user has to register it by normal means:

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new Jaxrs2TypesModule());
// and then register mapper with JAX-RS provider(s)
```

and ensuring that configured mapper is used by JAX-RS providers.

It is possible that later versions of providers may offer additional ways to get datatype module registered.

### Annotations on resources

In addition to annotation value classes, it is also possible to use a subset
of Jackson annotations with provider.

Here is a short list of supported annotations that work with all formats:

* `@JsonView` can be used to define active view for specific endpoint
* `@JsonRootName` can be used to specify alternate rootname; most often used with XML, but possibly with JSON as well.
* `@JacksonAnnotationsInside` meta-annotation may be used as a marker, to create "annotation bundles", similar to how they are used with value type annotations
* `com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures` can be used with all provid to enable/disable
    * `SerializationFeature` / `DeserializationFeature` for data-binding configuration
    * `JsonParser.Feature` / `JsonGenerator.Feature` for low(er) level Streaming read/write options

In addition there are format-specific annotations that may be used:

* JSON has:
    * `com.fasterxml.jackson.jaxrs.json.annotation.JSONP` to define `JSONP` wrapping for serialized result

## Other

For documentation, downloads links, check out [Wiki](../../wiki)
