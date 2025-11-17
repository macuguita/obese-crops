{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; };

        libs = with pkgs; [
          libpulseaudio
          libGL
          glfw
          openal
          flite
          stdenv.cc.cc.lib
        ];
      in
      {
        devShells.default = pkgs.mkShell {
          packages = with pkgs; [
            jdk
          ];

          buildInputs = libs;

          LD_LIBRARY_PATH = pkgs.lib.makeLibraryPath libs;
        };
      }
    );
}
