# JUEL - Java Unified Expression Language

[_JUEL_](http://juel.sf.net) is an implementation of the Unified Expression Language (EL),
specified as part of the JSP 2.1 standard ([JSR-245](http://jcp.org/aboutJava/communityprocess/final/jsr245/)),
which has been introduced in JEE5. Additionally, JUEL 2.2 implements the JSP 2.2 maintenance release
specification for full JEE6 compliance.

## Motivation

Once, the EL started as part of JSTL. Then, the EL made its way into the JSP 2.0 standard.
Now, though part of JSP 2.1, the EL API has been separated into package `javax.el` and all dependencies
to the core JSP classes have been removed.

In other words: the EL is ready for use in non-JSP applications!

## Features

_JUEL_ provides a lightweight and efficient implementation of the Unified Expression Language.

- High Performance – Parsing expressions is certainly the expected performance bottleneck.
  _JUEL_ uses a hand-coded parser which is up to 10 times faster than the previously used (javacc) generated parser!
  Once built, expression trees are evaluated at highest speed.
- Pluggable Cache – Even if _JUEL_'s parser is fast, parsing expressions is relative expensive.
  Therefore, it's best to parse an expression string only once. JUEL provides a default caching mechanism which
  should be sufficient in most cases. However, _JUEL_ allows to plug in your own cache easily.
- Small Footprint – _JUEL_ has been carefully designed to minimize memory usage as well as code size.
- Method Invocations – _JUEL_ supports method invocations as in `${foo.matches('[0-9]+')}`.
  Methods are resolved and invoked using the EL's resolver mechanism. As of _JUEL_ 2.2, method invocations are
  enabled by default.
- VarArg Calls – _JUEL_ supports Java 5 VarArgs in function and method invocations.
  E.g., binding `String.format(String, String...)` to function `format` allows for `${format('Hey %s','Joe')}`.
  As of _JUEL_ 2.2, VarArgs are enabled by default.
- Pluggable – _JUEL_ can be configured to be transparently detected as EL implementation by a Java runtime
  environment or JEE application server. Using _JUEL_ does not require an application to explicitly reference
  any of the _JUEL_ specific implementation classes.

## Documentation

Visit the _JUEL_ [site](http://juel.sf.net/guide) at Sourceforge.

## Downloads

The _JUEL_ [distribution](http://sourceforge.net/projects/juel/files/juel/juel-2.2/) contains the following JAR files:

- `juel-api-2.2.x.jar` - the `javax.el` API classes.
- `juel-impl-2.2.x.jar` - the `de.odysseus.el` implementation classes.
- `juel-spi-2.2.x.jar` - the `META-INF/service/javax.el.ExpressionFactory` service provider resource.
  (You will need this if you have several expression language implementations on your classpath and want to
  force _JUEL_'s implementation to be chosen by `ExpressionFactory.newInstance()`).

The JARs are also available via Maven:

	<dependency>
	  <groupId>de.odysseus.juel</groupId>
	  <artifactId>juel-api</artifactId>
	  <version>2.2.x</version>
	</dependency>

	<dependency>
	  <groupId>de.odysseus.juel</groupId>
	  <artifactId>juel-impl</artifactId>
	  <version>2.2.x</version>
	</dependency>

	<dependency>
	  <groupId>de.odysseus.juel</groupId>
	  <artifactId>juel-spi</artifactId>
	  <version>2.2.x</version>
	</dependency>

## Development

Visit the [Github project](http://github.com/beckchr/juel/).

## Status

_JUEL_ is considered production stable. The code is well tested (80% coverage) and feature complete.

## License

_JUEL_ is available under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).


_(c) 2006-2012 Odysseus Software_
