The build.xml Ant script produces the juel-api-2.1.2-bundle.jar and
juel-impl-2.1.2-bundle.jar. The bundles contain the corresponding
jar and pom files.

The pom files contain the minimum set of elements as described at
http://maven.apache.org/guides/mini/guide-central-repository-upload.html

I'm not sure how to specify the dependency from juel-impl to juel-api.
I used an optional dependency, because users may choose to use another
implementation of the API. Don't know if this is the way to do it...

Any suggestions?