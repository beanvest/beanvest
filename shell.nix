{ pkgs ? import (fetchTarball "https://github.com/NixOS/nixpkgs/archive/1e9c7c0203be59651050ab20d624c578f0d3d3f7.tar.gz") {}
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
      export JAVA_HOME=/nix/store/379k6bs17n5hlcc5w8wmk75vrzgpvhd3-graalvm19-ce-22.3.1
      export GRAALVM_HOME=/nix/store/379k6bs17n5hlcc5w8wmk75vrzgpvhd3-graalvm19-ce-22.3.1
      export PATH=$(pwd)/beanvest/beanvest/build/native/nativeCompile:$PATH

    '';
}

