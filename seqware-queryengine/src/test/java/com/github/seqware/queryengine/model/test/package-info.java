/**
 * This package contains tests that are not reliant upon any particular type of
 * serialization type but work with our model objects. These tests will be run
 * by default through each test suite. By default, this means that tests will
 * run against default Java serialization and proto buffer serialization, then
 * against a non-persistent back-end, file storage, and a proper HBase back-end,
 * then against either models that are aware of HBase and can use optimizations
 * for HBase (or are not).
 *
 * The upshot is that we can quickly diagnose whether an error is due to our
 * serialization, our storage back-end, or an optimization that we have
 * implemented.
 */
package com.github.seqware.queryengine.model.test;
