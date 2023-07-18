# Beanvest

## Supported JDK
Application can be built to native binaries using GraalVM. Java 17 is minimum required. It can be downloaded from official website: https://www.graalvm.org/downloads/#

## Running tests
```
./gradlew test
```

## Building binaries
```
./gradlew nativeCompile
```

## Running it
```
./ledger/beanvest/build/native/nativeCompile/beanvest returns
./ledger/beanvest/build/native/nativeCompile/beanvest journal
./ledger/beanvest/build/native/nativeCompile/beanvest export
./ledger/beanvest/build/native/nativeCompile/beanvest import
```