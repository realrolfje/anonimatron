# Contributing to Anonimatron

Thank you for your interest and time in contributing to Anonimatron. I'm sure you've already found the [code of conduct](CODE_OF_CONDUCT.md) by now so lets cover your options:

## Reporting an issue or feature request

If you noticed a problem, or have a great idea for a feature, you are welcome to [open an issue](https://github.com/realrolfje/anonimatron/issues) so we can help you out.

## Contributing code

If you already figured out how to fix the bug you've found, or added a great feature, I'd like to hear from you. This being GitHub, you are welcome to [fork this repository](https://help.github.com/articles/fork-a-repo/). I'm happy to review your pull requests or look at your branch.

In order to build the codebase you'll need Maven and Docker installed. Run the following command in the root of the project to install the dependencies we'll need and run the tests to ensure your environment is healthy:

```console
$ mvn install
```

To build/test Anonimatron in different Java versions, you need to run the tests with the provided docker scripts. This makes sure Anonimatron stays Java 8 compatible:

```console
$ ./scripts/test-in-docker.sh
$ ./scripts/test-in-docker.sh --java 8
$ ./scripts/test-in-docker.sh --java 17 clean test
```

The test wrapper delegates to `./scripts/mvn-in-docker.sh`. Use that script directly when you want to run a different Maven goal in Docker:

```console
$ ./scripts/mvn-in-docker.sh --java 8 clean package
```

The Docker Maven wrapper mounts your checkout into the container, mounts `$HOME/.m2/settings.xml` read-only when it exists, and stores the writable Docker Maven home in `target/docker-maven`.

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

Releases are created with `./create_release.sh`. The script still performs the release flow: merge `develop` into `master`, set the release version, deploy and sign the Maven release, commit and tag the release, merge back to `develop`, set the next snapshot version, and push the branches.

The Maven commands in the release script run through Docker using Java 8:

```console
$ ./scripts/mvn-in-docker.sh --java 8 clean deploy -P release
```

In order to release to the sonatype Maven Repository, please create a security token at your [sonatype Nexus profile page](https://oss.sonatype.org/#profile;User%20Token) and add it to your `$HOME/.m2/settings.xml`:

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

The Docker release build mounts that settings file read-only and writes its Maven repository and temporary Maven files under `target/docker-maven`. The `maven-clean-plugin` configuration keeps that directory available during `mvn clean deploy`; `mvn clean` does not remove it. Delete `target` manually when you want to clear the Docker Maven cache as well.

After doing this, running the release script on a SNAPSHOT release should release to the snapshot repository, and on a non-snapshot release it should promote to the central maven repo. Be careful with your powers.
