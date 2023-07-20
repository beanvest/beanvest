# Beanvest

## Supported JDK
Java 17 is minimum required. Application can be built to native binaries using GraalVM. It can be downloaded from official website: https://www.graalvm.org/downloads/#

## Running tests
```bash
./gradlew test
```

## Building binaries
```bash
./gradlew nativeCompile
```

## Running it
See [usage examples](generated/usage.md).

## Contributing
To save you trouble fixing/reverting commits or going back to regenerate some files project uses
[pre-commit](https://pre-commit.com/). Run `pre-commit init` to initialize git hooks that will run 
everything that's needed when you commit any changes.  
