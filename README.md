# Sonargraph Integration Access

This project provides a Java API for the XML report generated by Sonargraph version 8.7 and later and is used by the Sonargraph and Jenkins plugins.
 
It is licensed under the Apache License, Version 2.0.
Best way to get familiar with the code is to take a look at the JUnit tests.

For a general overview about the functionality that Sonargraph offers, take a look at the product's homepage: <a href="https://www.hello2morrow.com/products/sonargraph/architect9">https://www.hello2morrow.com/products/sonargraph/architect9</a>
The variant Sonargraph Explorer is free for Java and C# projects, but only runs a limited number of analysers.

Releases are published on Maven central, so the easiest way to use the plugin is to add a dependency to it in your Maven pom.xml. 
See /src/test/xproject-metric-aggregation/pom.xml for an example.

# Compatibility Matrix

The following lists the compatibility with Sonargraph releases.

| Sonargraph Version | Integration Access Version | 
|:------------------:|:--------------------------:| 
| 8.7                |             1.0            | 
| 8.8.0              |             1.1            |
