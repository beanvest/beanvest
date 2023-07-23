# Beanvest

## Dev env
Nix shell configuration for development is provided. Once you get [Nix](https://nixos.org/) you can get straight
into configured shell with `nix-shell`. You can see list of tools needed in [shell.nix](shell.nix)

## Building
- building everything
```bash
pre-commit run
```

- bunning tests
```bash
./gradlew test
```

- building binaries
```bash
./gradlew nativeCompile
```

## Running it
See [usage examples](generated/usage.md).

## Contributing
To save you trouble fixing/reverting commits or going back to regenerate some files project uses
[pre-commit](https://pre-commit.com/). Run `pre-commit init` to initialize git hooks that will run 
everything that's needed when you commit any changes.  
