# Beanvest

## Building
- building everything
```bash
pre-commit run
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

## Running it
See [usage examples](generated/usage.md).

## Contributing

There are two important bits to get it ready for dev:

- nix env
  
  Nix shell configuration for development is provided. Once you grab [Nix](https://nixos.org/) you can get straight
  into configured shell with all the tools needed with `nix-shell`. You can see dependencies in [shell.nix](shell.nix)
  
- pre-commit
  
  To save you trouble fixing/reverting commits or going back to regenerate some files project uses
  [pre-commit](https://pre-commit.com/). Run `pre-commit init` to initialize git hooks
  and then all the checks will be done automatically when you commit any changes.  
