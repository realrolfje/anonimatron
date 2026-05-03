# Contributing to Anonimatron

Thank you for your interest and time in contributing to Anonimatron. I'm sure you've already found the [code of conduct](CODE_OF_CONDUCT.md) by now so lets cover your options:

## Reporting an issue or feature request

If you noticed a problem, or have a great idea for a feature, you are welcome to [open an issue]()https://github.com/realrolfje/anonimatron/issues) so we can help you out.

## Contributing code

If you already figured out how to fix the bug you've found, or added a great feature, I'd like to hear from you. This being GitHub, you are welcome to [fork this repository](https://help.github.com/articles/fork-a-repo/). I'm happy to review your pull requests or look at your branch.

In order to build the codebase you'll need Maven installed. Run the following command in the root of the project to install the dependencies we'll need and run the tests to ensure your environment is healthy:

```console
$ mvn install
```

At this point you can contribute your code changes. Please add unit tests for anything new / changed. You can verify your changes work with:

```console
$ mvn test
```

To build the code locally so you can use your release as you'd use the one from GitHub Releases, run:

```console
$ mvn package
```

The built version is now in `target/anonimatron-[version].zip`.

## Publishing to mavenrepo (experimental)

The `pom.xml` file adheres to http://central.sonatype.org/pages/requirements.html. Release to mavenrepo is based on https://medium.com/pleo/deploying-to-mavens-central-repository-835253a119db

In order to run `mvn clean deploy` to release to the sonatype Maven Repository, please create a security token at your [sonatype Nexus profile page](https://oss.sonatype.org/#profile;User%20Token) and add it to your settings xml:

```xml
<servers>
    <server>
        <id>ossrh</id>
        <username>YOUR_SONATYPE_TOKEN</username>
        <password>YOUR_SONATYPE_TOKENPASS</password>
    </server>
  </servers>
</settings>
```

After doing this, running `maven deploy` on a SNAPSHOT release should release to the snapshot repository, and on a non-snapshot release it should promote to the central maven repo. Be careful with your powers.