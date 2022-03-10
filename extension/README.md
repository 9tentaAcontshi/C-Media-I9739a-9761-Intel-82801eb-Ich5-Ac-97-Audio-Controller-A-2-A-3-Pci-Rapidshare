[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.xpipe/extension/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.xpipe/extension)
[![javadoc](https://javadoc.io/badge2/io.xpipe/extension/javadoc.svg)](https://javadoc.io/doc/io.xpipe/extension)
[![Build Status](https://github.com/xpipe-io/xpipe_java/actions/workflows/extension.yml/badge.svg)](https://github.com/xpipe-io/xpipe_java/actions/workflows/extension.yml)

## X-Pipe Extension API

The X-Pipe extension API allows you to create extensions of any kind for X-Pipe.


### Custom data sources

A custom data source type can be implemented by creating a custom [DataSourceProvider]().
This provider contains all the information required for proper handling of your custom data sources,
whether you access it from the CLI, any API, or the X-Pipe commander gui.

### Custom data sinks

A custom data sink type can be implemented by creating a custom [DataSinkProvider]().
As the notion of a sink is abstract, it allows you to basically implement any type of sink you want.
Whether the target is a programming language, another application, a database, or a regular file.