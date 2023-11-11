{ pkgs ? import (fetchTarball "https://github.com/NixOS/nixpkgs/archive/220a5ae4c374703e94a45379ff9d1f866fdb3f1a.tar.gz") {}
}:

pkgs.mkShell {
  packages = [
    pkgs.graalvm19-ce
    pkgs.beancount
    pkgs.fava
    pkgs.pre-commit
    pkgs.nodejs_20

    pkgs.git
    pkgs.vim

    (pkgs.python3.withPackages(ps: with ps; [cfgv identify]))
  ];
  shellHook =
    ''
      export JAVA_HOME=/nix/store/7zjvbmdd16j7var3bnxfq75ipz35cmfy-graalvm19-ce-22.3.1
      export GRAALVM_HOME=/nix/store/7zjvbmdd16j7var3bnxfq75ipz35cmfy-graalvm19-ce-22.3.1
      export PATH=$(pwd)/beanvest/beanvest/build/native/nativeCompile:$PATH

      env > .env

    '';
}

