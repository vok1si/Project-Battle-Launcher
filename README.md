# Project Battle Auto Launcher

The public source for the Windows launcher bootstrap and host wrapper used by Project Battle Auto. This repository contains the code that checks and installs signed launcher updates and starts the installed launcher.

## Build

Pushes and pull requests run the `Windows build` GitHub Actions workflow. It compiles the two launcher executables with the Windows .NET Framework C# compiler and publishes them as workflow artifacts. The workflow does not package the Minecraft game, Java runtime, mods, private signing key, or player data.

For a local build, run `./Build.ps1`. Output is written to `dist/`.

## Release and scope

The full player distribution includes a legacy Java launcher, Java runtime, Minecraft files, and third-party mods. Those payload components are not built from this repository and are distributed separately. This repository makes the updater and host launcher source reproducible; it does not claim that third-party game payloads are open source.

The update channel uses the public key in `public-key.xml`. The corresponding private release key must remain outside this repository and must never be added to a commit, issue, or Actions artifact.

## Code signing

The project is preparing an application for a code-signing review. Do not describe a release as SignPath-signed until the Foundation approves the project and the signing workflow is enabled.

## License

All original source code, build scripts, configuration, and assets in this repository are licensed under the MIT License; see [LICENSE](LICENSE). Minecraft, Java, and third-party mods are not included in this license and remain subject to their respective terms.
