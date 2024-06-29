[![License](https://img.shields.io/github/license/toolarium/toolarium-sanitize-content)](https://github.com/toolarium/toolarium-sanitize-content/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.toolarium/toolarium-sanitize-content/1.0.1)](https://search.maven.org/artifact/com.github.toolarium/toolarium-sanitize-content/1.0.1/jar)
[![javadoc](https://javadoc.io/badge2/com.github.toolarium/toolarium-sanitize-content/javadoc.svg)](https://javadoc.io/doc/com.github.toolarium/toolarium-sanitize-content)

# toolarium-sanitize-content

Implements a sanitize content java library.


## Built With

* [cb](https://github.com/toolarium/common-build) - The toolarium common build

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/toolarium/toolarium-sanitize-content/tags). 


### Gradle:

```groovy
dependencies {
    implementation "com.github.toolarium:toolarium-sanitize-content:1.0.1"
}
```

### Maven:

```xml
<dependency>
    <groupId>com.github.toolarium</groupId>
    <artifactId>toolarium-sanitize-content</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Getting Started

Different file types can contain executable code as example java script. This library allows to eliminate the executable code.

```java
        String filename = "test.pdf";
        SanitizeContentResult result = SanitizeContentFactory.getInstance().getSanitizeContentProcessor().sanitize(filename, 
                new FileInputStream(Paths.get("src/test/resources",filename).toFile()),
                new FileOutputStream(Paths.get("build", filename).toFile()), 
                null);
```


### Installing

Add the annoation processor to you project or simple use the common build.

