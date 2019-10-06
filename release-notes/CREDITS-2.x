Here are people who have contributed to development Jackson JSON process 
core component, version 2.x
(version numbers in brackets indicate release in which the problem was fixed)

(note: for older credits, check out release notes for 1.x versions)

Tatu Saloranta, tatu.saloranta@iki.fi: author

Bill Burke:

* Reported #11: ContextResolvers don't work for ObjectMapper due over-aggressive caching
 (and provided samples that show how to fix it)
 (2.2.2)

Matt Bishop:

* Reported #12: Missing OSGi imports
 (2.2.2)

Michael Brackx (brackxm@github.com)

* Requested #19: Add `InputStream` as unwritable class
 (2.2.3)

Dain Sundstrom:
* Suggested #6: Add `JaxRSFeature.ADD_NO_SNIFF_HEADER` to automatically add
  X-Content-Type-Options header (works with IE)
 (2.2.3)

Jonathan Haber (HiJon89@github):
* Contributed #61: Fix disabling of `JaxRSFeature` (was always enabling features)
 (2.5.2)
* Reported #60: Problems with serialization of List of non-polymorphic values.
 (2.6.0)

rsprit@github:
* Reported #63, contributed fix: Support JAX-RS 2.0 in OSGi environment for Smile,
  CBOR too
 (2.5.4)

M. Tarık Yurt (mtyurt@github)
* Contributed #68: Add YAML provider
 (2.6.0)

Nick Kleinschmidt (kleinsch@github)

* Reported, contributed fix for #80: Non-JSON providers don't
  support custom MIME types with extensions
 (2.7.4)

Tim Ward (timothyjward@github)

* Contributed #93: Jackson OSGi metadata is incomplete
 (2.8.5)

John McCarthy (jvmccarthy@github)
* Reported #111: AnnotationBundleKey equality fails for Parameter Annotations
 (2.10.0)

Marius Lewerenz (mlewe@github)
* Reported #113: `@JacksonFeature` can't be used for deserialization (not
  applicable to parameters)
 (2.10.0)

Lukáš Petrovický (triceo@github)

* Reported #114: module-info.java references legacy javax.ws.rs.api
