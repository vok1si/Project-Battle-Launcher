# Project Battle Auto Launcher

The public source for the Windows launcher bootstrap and host wrapper used by Project Battle Auto. This repository contains the code that checks and installs signed launcher updates and starts the installed launcher.

## Build

Pushes and pull requests run the `Windows build` GitHub Actions workflow. It compiles the two launcher executables with the Windows .NET Framework C# compiler and publishes them as workflow artifacts. The workflow does not package the Minecraft game, Java runtime, mods, private signing key, or player data.

For a local build, run `./Build.ps1`. Output is written to `dist/`.

## Release and scope

The full player distribution includes a Java launcher, Java runtime, Minecraft files, and third-party mods. The Java launcher maintenance source and overlay build are now in [legacy-java](legacy-java/README.md). The overlay needs the published 0.1.78.12 Java JAR for unchanged classes and assets. The Java runtime, Minecraft files, and third-party mods are not built from this repository and are distributed separately. This repository does not claim that third-party game payloads are open source.

The update channel uses the public key in `public-key.xml`. The matching RSA private key is protected with Windows DPAPI for the current Windows user at `%LOCALAPPDATA%\ProjectBattleRelease\launcher-channel-key.dpapi`. `Sign-Channel.ps1` uses it to sign a channel manifest locally. Keep a recovery copy in a secure password manager or encrypted backup before reinstalling Windows; never add the key or its decrypted XML to this repository, an issue, or a build artifact.

Replacing `public-key.xml` rotates trust. Users with an older bootstrap must replace `Project Battle.exe` once before channel updates can work again.

## Code signing

The application for SignPath Foundation code signing has not yet been approved, and no release is currently signed with its certificate. Do not describe a release as SignPath-signed until the Foundation approves the project and the signing workflow is enabled. See the [Code signing policy](https://project-battle-portal.vokisu9.workers.dev/code-signing-policy.html) for current status, source, build, and release approval details.

## License

All original source code, build scripts, configuration, and assets in this repository are licensed under the MIT License; see [LICENSE](LICENSE). Minecraft, Java, and third-party mods are not included in this license and remain subject to their respective terms.
