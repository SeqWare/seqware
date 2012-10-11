/**
 * This package provides the SeqWare module interface and API, including
 * common code and classes. SeqWare uses modules as the main unit oF
 * processing. Most modules will wrap a command-line tool providing a
 * uniform way to call it and capture its output. The details vary by module,
 * but significant amounts of the code are the same, and are abstracted into
 * this package. Modules wrapping specific tools are provided in the
 * net.sourceforge.seqware.pipeline.modules packages, and sub-packages.
 * 
 * To create their own modules, users are expected to extend the
 * {@link Module} class or the {@link net.sourceforge.seqware.pipeline.modules.GenericCommandRunner
 * GenericCommandRunner}class. the [TODO-Link] HelloWorld module provides an
 * illustrative example.
 */
package net.sourceforge.seqware.pipeline.module;
