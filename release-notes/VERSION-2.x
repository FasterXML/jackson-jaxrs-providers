Project: jackson-jaxrs-providers
Sub-modules:
  jackson-jaxrs-cbor-provider
  jackson-jaxrs-json-provider
  jackson-jaxrs-smile-provider
  jackson-jaxrs-xml-provider
  jackson-jaxrs-yaml-provider

------------------------------------------------------------------------
=== Releases ===
------------------------------------------------------------------------

2.20 (not yet released)

- Generate SBOMs [JSTEP-14]

2.19.1 (13-Jun-2025)

- Add explicit version for Woodstox, stax2-api (no longer managed via jackson-bom)

2.19.0 (24-Apr-2025)

#200: Narrow types to format specific (e.g. CBORMapper) when resolving
  via JAX-RS Providers 
#205: Propagate the pretty printer from the writer to the generator in
  ProviderBase.writeTo()
 (contributed by @motlin)
* Woodstox dependency now 7.1.0

2.18.4 (06-May-2025)
2.18.3 (28-Feb-2025)
2.18.2 (27-Nov-2024)
2.18.1 (28-Oct-2024)

No changes since 2.18.0

2.18.0 (26-Sep-2024)

#192: Bring back code to support JAXRS v1 (revert #134 for 2.18)
 (contributed by @pjfanning)
#193: `JacksonJaxbJsonProvider` has @Produces(MediaType.WILDCARD) and yet
  hasMatchingMediaType(MediaType.WILDCARD) return false
 (contributed by Yura)
* Woodstox dependency now 7.0.0

2.17.3 (01-Nov-2024)

No changes since 2.17.2

2.17.2 (05-Jul-2024)

* Woodstox dependency now 6.7.0

2.17.1 (04-May-2024)

#184: Use `ReentrantLock`s instead of synchronized blocks
 (contributed by @pjfanning)
#187: Mark variables as volatile for safe concurrent access
 (contributed by @pjfanning)

2.17.0 (12-Mar-2024)

* Woodstox dependency now 6.6.1

2.16.2 (09-Mar-2024)

No changes since 2.16.1

2.16.1 (24-Dec-2023)

#178: Deprecate local `LRUMap`, use `jackson-databind` provided one instead

2.16.0 (15-Nov-2023)

No changes since 2.15

2.15.4 (15-Feb-2024)
2.15.3 (12-Oct-2023)
2.15.2 (30-May-2023)
2.15.1 (16-May-2023)

No changes since 2.15.0

2.15.0 (23-Apr-2023)

#170: Add `JaxRsFeature.READ_FULL_STREAM` to consume all content, on by default
 (contributed by Steven S)
* Woodstox dependency now 6.5.1

2.14.3 (05-May-2023)

No changes since 2.14.2

2.14.2 (28-Jan-2023)

#166: `ProviderBase` class shows contention on synchronized block using
 `LRUMap` _writers instance
 (fix contributed by @pjfanning)
* Upgrade Woodstox to 6.4.0 for a fix to OSGi metadata

2.14.1 (21-Nov-2022)

No changes since 2.14.0

2.14.0 (05-Nov-2022)

* (xml) Woodstox dependency -> 6.4.0

2.13.5 (not yet released)

* (xml) Woodstox dependency -> 6.4.0

2.13.4 (03-Sep-2022)

* (xml) Woodstox dependency -> 6.3.1

2.13.3 (14-May-2022)
2.13.2 (06-Mar-2022)
2.13.1 (19-Dec-2021)

No changes since 2.13.0

2.13.0 (30-Sep-2021)

#134: Remove work-around for JAX-RS 1.x wrt JAX-RS 2 type `NoContentException`
#146: Create new alternate jackson-jakarta-rs-providers repo for Jakarta
  (not Javax) RS implementation -- also remove "jakarta" classifier variants
  from this project
  
- Update `jakarta.xml.bind-api` dep (2.3.2 -> 2.3.3)

2.12.7 (26-May-2022)
2.12.6 (15-Dec-2021)
2.12.5 (27-Aug-2021)
2.12.4 (06-Jul-2021)
2.12.3 (12-Apr-2021)

No changes since 2.12.2

2.12.2 (03-Mar-2021)

#132: jaxrs jakarta versions have javax.ws references in OSGi manifest
 (reported by Scott L)
#136: Create manifest files separately and reference in bundle plugin, shade in
  the new manifest for Jakarta separately before install
 (contributed by Marc M)
* Woodstox dependency to 6.2.4

2.12.1 (08-Jan-2021)

No changes since 2.12.0

2.12.0 (29-Nov-2020)

#127: Allow multiple implementations of JAX-RS for all providers
 (requested by fenixcitizen@github)
#128: Module-Info Enhancements - JAX-RS updates for Jakarta Release version
 (contributed by Marc M)
- Add Gradle Module Metadata (https://blog.gradle.org/alignment-with-gradle-module-metadata)

2.11.4 (12-Dec-2020)

- Upgrade Woodstox dependency to 6.2.3

2.11.4 (12-Dec-2020)
2.11.3 (02-Oct-2020)
2.11.2 (02-Aug-2020)
2.11.1 (25-Jun-2020)
2.11.0 (26-Apr-2020)

No changes since 2.10.x

2.10.5 (21-Jul-2020)

No changes since 2.10.4

2.10.4 (03-May-2020)

- Upgrade Woodstox dependency to 6.2.0 (minor improvement to MSV shading)

2.10.3 (03-Mar-2020)

#120: Incorrect export of `com.fasterxml.jackson.jaxrs.json` for JSON provider
 (reported by Lukáš P)

2.10.2 (05-Jan-2020)

#121: Allow multiple implementations of ws.rs
 (contributed by Marc M)

2.10.1 (09-Nov-2019)

#109: Use privileged action to check for JAX-RS 1 vs 2
 (contributed by James P)

2.10.1 (09-Nov-2019)

#114: module-info.java references legacy javax.ws.rs.api
 (reported by Lukáš P)

2.10.0 (26-Sep-2019)

#111: AnnotationBundleKey equality fails for Parameter Annotations
 (reported by John M)
#113: `@JacksonFeature` can't be used for deserialization (not applicable to parameters)
 (reported by Marius L)
- Add JDK9+ `module-info` with Moditect plugin
- Update Woodstox dependency by XML provider

2.9.10 (21-Sep-2019)

- Align Woodstox version XML provider uses to one used by `jackson-dataformat-xml`,
  5.3.0, with `stax2-api` 4.2 (was issue with 2.9.9)

2.9.9 (16-May-2019)
2.9.8 (15-Dec-2018)
2.9.7 (19-Sep-2018)
2.9.6 (12-Jun-2018)
2.9.5 (26-Mar-2018)
2.9.4 (24-Jan-2018)
2.9.3 (09-Dec-2017)
2.9.2 (14-Oct-2017)
2.9.1 (07-Sep-2017)
2.9.0 (30-Jul-2017)

No functional changes since 2.8.

2.8.10 (24-Aug-2017)

#97: Extend version range to allow usage with jax-rs 2.1

2.8.9 (12-Jun-2017)
2.8.8 (05-Apr-2017)
2.8.7 (21-Feb-2017)
2.8.6 (12-Jan-2017)

No changes since 2.8.5

2.8.5 (16-Nov-2016)

#91: Implemented dynamic selection of NoContentException to try to
   support JAX-RS 1.x.
  (contributed by Spikhalskiy@github)
#93: Jackson OSGi metadata is incomplete
 (contributed by Tim W)

2.8.4 (14-Oct-2016)
2.8.3 (17-Sep-2016)
2.8.2 (30-Aug-2016)

No changes since 2.8.1

2.8.1 (20-Jul-2016)

#87: JacksonJaxbJsonProvider should use the real "value.getClass()" to build the root type

2.8.0 (05-Jul-2016)

#22: Remove `@Provider` annotation from `JsonParseExceptionMapper` and
  `JsonMappingExceptionMapper`
#48: Support compact serialization of `javax.ws.rs.core.Link`, deserialization
#82: Upgrade JAX-RS dependency to 2.0
#86: ContextResolver<ObjectMapper> must be called first when provided
 (requested by NicoNes@github)

2.7.5 (11-Jun-2016)

No change since 2.7.4

2.7.4 (29-Apr-2016)

#80: Non-JSON providers don't support custom MIME types with extensions
 (reported and fixed by Nick K)

2.7.3 (16-Mar-2016)
2.7.2 (27-Feb-2016)
2.7.1 (02-Feb-2016)

No changes since 2.7.0

2.7.0 (10-Jan-2016)

No changes since 2.6.

2.6.6 (05-Apr-2016)
2.6.5 (19-Jan-2016)
2.6.4 (07-Dec-2015)
2.6.3 (12-Oct-2015)
2.6.2 (15-Sep-2015)
2.6.1 (09-Aug-2015)

No changes since 2.6.0.

2.6.0 (20-Jul-2015)

#39: Build alternate jars with qualifier "no-metainf-services", which do
 NOT include `META-INF/services` metadata for auto-registration
#60: Problems with serialization of List of non-polymorphic values.
 (reported by Jonathan H)
#66: Should check `_cfgCheckCanDeserialize` in `isReadable()` (and not
  `_cfgCheckCanSerialize`
 (reported by seanzhou1023@github)
#68: Add YAML provider
 (contributed byb mtyurt@github)
#69: Add deserialization support for `MappingIterator`, to support iteration over large input

2.5.4 (not yet released)

#63: Support JAX-RS 2.0 in OSGi environment for Smile, CBOR too
 (contributed by rsprit@github)

2.5.3 (24-Apr-2015)

No changes since 2.5.2

2.5.2 (29-Mar-2015)

#61: Fix disabling of `JaxRSFeature` (was always enabling features)
 (contributed by Jonathan H, HiJon89@github)
- Update Woodstox dep to 4.4.1, stax2-api 3.1.4.

2.5.1 (06-Feb-2015)
2.5.0 (01-Jan-2015)

No changes since 2.4

2.4.4 (25-Nov-2014)
2.4.3 (04-Aug-2014)
2.4.2 (15-Aug-2014)
2.4.1 (17-Jun-2014)

No changes

2.4.0 (02-Jun-2014)

#49: Add `JaxRSFeature.ALLOW_EMPTY_INPUT`, disabling of which can prevent
  mapping of empty input into Java null value

2.3.3 (14-Apr-2014)

#41: Try to resolve problems with RESTeasy, missing `_configForWriting`
  override.
 (reported by `tbroyer@github`)

2.3.2 (01-Mar-2014)

#40: Allow use of "text/x-json" content type by default
 (requested by kdeenanauth@github)
#42: Add CBOR provider (using jackson-dataformat-cbor)
#43: Verify that format-specific mappers are properly overridden
 (like `XmlMapper` for xml)

2.3.1 (28-Dec-2013)

#37: Enable use of JAX-RS 2.0 API
 (contributed by larsp@github)

2.3.0 (14-Nov-2013)

#24: Allow defining default view to use for endpoints without View annotation
#33: Provide a way to customize `ObjectReader` / `ObjectWriter` used by end points

2.2.3 (24-Aug-2013)

#6: Add `JaxRSFeature.ADD_NO_SNIFF_HEADER` to automatically add X-Content-Type-Options
  header (works with IE)
 (suggested by Dain S)
#12, #16: More OSGi manifest fixes
 (reported by 'logoff@github')
#18: Add LICENSE, NOTICE files in artifacts
#19: Add `InputStream` as unwritable class
 (requested by Michael B)
#26: Missing OSGi import for base, (c.f.j.databind.cfg)
 (reported by jerome-leclercq@github)

2.2.2 (31-May-2013)

#11: ContextResolvers don't work for ObjectMapper due over-aggressive caching
 (reported by Bill Burke (from Resteasy))
(PARTIAL) #12: OSGi imports missing dependency from json/smile/xml to base package
 (reported by Matt Bishop)
#14: Allow "application/javascript" type for JSON provider
 (requested by Stephan202@github)

2.2.1 (03-May-2013)

#8: ProviderBase does not close `JsonGenerator`
 (contributed by Steven S)
#9: Dependency to Jetty was accidentally left as compile-time; should be
  'test'
 (reported by KlausBrunner@github)
#10: Problems with proxying of `ProviderBase` (add no-arg constructor)

2.2.0 (22-Apr-2013)

Changes:

#1: Allow binding input to `JsonParser`
#5: Add 'provider.removeUntouchable()'
* Add Woodstox dependency (not just in 'test' scope) to try to avoid problems
  with users relying on SJSXP (such as 'extra' xmlns declarations)
#8: Ensure that `JsonGenerator` is always properly closed, including error cases,
  otherwise can hide problems (due to missing flush of content)
 (contributed by Steven S)

2.1.2 (05-Dec-2012)

No changes.

2.1.1 (11-Nov-2012)

JSON

  * [Issue#17]: Accept empty content as 'null', instead of throwing an
    EOFException
   (requested by Matt B, cjellick@github)

2.1.0 (08-Oct-2012)

XML

  * [Issue#4] Exception on empty content, should return null.

2.0.2 (14-May-2012)

JSON:

  * [Issue-11] Change JAXB annotation module dependency to optional for OSGi
  * [Issue-12] Revert untouchable change to 1.x compatible; so that
    'String' and 'byte[]' are again "untouchable" (JSON provider will NOT
    try to convert them)

2.0.1 (23-Apr-2012)

General:

  * Changed 'jaxrs-311' dependency from 'compile' to 'provided'

JSON:

  * SPI (META-INF/services/) fixed so auto-registration should now work
   (contributed by Simone T)
  * Issue-10: NPE in EndpointConfig, if JAX-RS container passes null
    set of Annotations to writeTo()

2.0.0 (25-Mar-2012)

JSON:

  * [Issue-1] Add @JSONP annotation for declarative JSONP support
  * [Issue-2] Add @JacksonFeatures annotation for reconfiguring serialization,
    deserialization features on per-endpoint basis

[entries for versions 1.x and earlier not retained; refer to earlier releases)
