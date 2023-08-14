[![Java CI with Gradle](https://github.com/beanvest/beanvest/actions/workflows/gradle.yml/badge.svg)](https://github.com/beanvest/beanvest/actions/workflows/gradle.yml)
# Beanvest

## Getting all the tools
  Once you have [Nix](https://nixos.org/), `nix-shell` will start bash configured with all the necessary tools. 
  You can see dependencies in [shell.nix](shell.nix).
  
## Building, testing, generating

- building, testing and generating everything
  ```bash
  ./gradlew all
  ./gradlew nativeAll
  ```
  
- running tests
  ```bash
  ./gradlew test
  ./gradlew nativeTest
  ```

- building binaries
  ```bash
  ./gradlew nativeCompile
  ```

- regenerating usage docs
  ```bash
  ./gradlew generateUsageDoc
  ./gradlew generateUsageDocNative
  ```


## Running it
See [usage examples](generated/usage.md).

## Contributing
To save you trouble fixing/reverting commits or going back to regenerate some files project uses
[pre-commit](https://pre-commit.com/). Run `pre-commit init` to initialize git hooks
and then all the checks will be done automatically when you commit any changes.  
