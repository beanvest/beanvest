{ pkgs ? import (fetchTarball "https://github.com/NixOS/nixpkgs/archive/883fd35c940aa304654f9bc796e6d53ad627a9c8.tar.gz") {}
}:

pkgs.mkShell {
  packages = [
    pkgs.graalvm19-ce
    pkgs.beancount
    pkgs.fava
    pkgs.pre-commit

    pkgs.git
    pkgs.vim
  ];
  shellHook =
    ''
      export JAVA_HOME=/nix/store/i0a2rrjxhawark7wrf6hh0rkipfdwmk6-graalvm19-ce-22.3.1
    '';
}

