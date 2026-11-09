# Launcher reliability review — 2026-11-09

Reviewed source: Project-Battle-Launcher main, published channel 0.1.78.35. Candidate fixes are not released to players.

## Confirmed fixes
- Download HTTP 416 loop was unbounded; retries now stop after three requests.
- Resumed downloads validate Content-Range start before appending.
- Downloads stop when response exceeds manifest size; existing installed target remains untouched.
- Bootstrap removes UTF-8 BOM before JSON parsing.
- Bootstrap body reads have a 45-second idle timeout and display a retryable failure.
- JVM native out-of-memory crashes are identified using the exact process PID; unrelated old crash logs are ignored.
- Default and fallback pack manifest URLs now use the maintained v0.1.0 endpoint. Existing v0.2.26 endpoint was refreshed to pack 0.2.28 on GitHub.

## Evidence
- All 61 active mod URLs returned HTTP 200 with matching Content-Length on this machine.
- Local HTTP regression tests passed: normal response, 404, 416, corrupted payload, oversized payload, incorrect range, valid resumed download. Failure cases preserve the existing target.
- Actual signed production channel and BOM-prefixed channel verified against repository public key.
- Traversal and alternate-stream paths rejected by bootstrap.
- Exact-PID memory diagnosis tests passed.
- C# bootstrap/host and complete Java overlay built successfully with JDK 21.

## Outstanding verification and risks
- This is not an exhaustive certification. Fresh-profile installation, interrupted installation recovery, concurrent launch/update, disk-full recovery, Java runtime recovery, all authentication flows and gameplay have not yet been exercised end to end.
- HTTP link checks do not establish availability for other ISPs or prevent future GitHub failures.
- The new timeout requires a real stalled HTTPS transport test beyond compilation.
- Download retry waits are bounded but all HTTP errors still retry; expired redirects or changing content during resume need further coverage.
- Memory diagnosis explains a crash; it does not yet prevent system commit exhaustion before launch.
- Server modpack matching and TaCZ server gunpack schema were not validated in this pass.
- Source reconstruction depends on the published 0.1.78.12 base JAR; the repository is not fully self-contained.
- Public launcher remains 0.1.78.35. Do not label the candidate production-ready before remaining integration checks.

## Reproduction
Compile PackMaintenance, PackDownloadManager and tests/DownloadRegression.java with JDK 21; run com.warproject.launcher.DownloadRegression. Compile/run tests/MemoryRegression.java using the same classes. Build.ps1 builds the bootstrap; tests/BootstrapRegression.cs references its EXE and reads tests/launcher-channel.json and public-key.xml. Build-Java.ps1 builds the overlay from the documented base JAR.

## Extended integration pass — 2026-11-09

Additional fixes:
- Host now drains Forge stdout and stderr concurrently and bounds installer execution to ten minutes; output is saved to forge-install.log.
- Host web downloads use 45-second connect/read timeouts.
- Previous package is retained during cleanup; damaged current.json can fall back to a valid previous.json.
- Commit uses an exclusive installation lock so two processes cannot race over current.new.json.
- Bootstrap timeout now closes stalled streams, including transports that ignore CancellationToken.
- Missing remote manifest no longer silently launches potentially stale mods after HTTP 404.
- Physical RAM selection reserves at least 2 GB or 25% for the OS; nonpositive saved values are clamped too. This is not a guarantee of sufficient current system commit capacity.
- Selected Java is executed with -version before gameplay; Java 17 required and check bounded to ten seconds.
- All official Minecraft assets are restored, not just language JSON. Existing assets and new downloads are validated using official SHA-1 and size. This addresses absent vanilla sounds on clean installs.
- Invalid/truncated library ZIPs are restored rather than accepted based only on existence. ZIP directory validation does not replace full upstream hash validation for every library.
- Build-Java stages the candidate before moving it into output; it no longer writes the base JAR over an output before patching has succeeded.

Executed integration checks:
- Real Forge 47.4.28 installation from an empty profile: PASS.
- All 61 pack files downloaded through launcher PackDownloadManager and inspected: PASS.
- One damaged mod identified and repaired: PASS.
- All 3598 official Minecraft resources prepared; one deliberately corrupted vanilla sound restored: PASS.
- Damaged Gson library restored; valid library not downloaded again: PASS.
- Java 17 accepted / Java 21 rejected: PASS.
- Fixture installer produces 200 KB on both output streams without deadlocking: PASS.
- Sleeping fixture installer times out and is killed: PASS.
- Update commit: clean install, traversal archive, missing entry, incorrect hash, old-state preservation, previous pointer, malformed current.json fallback: PASS.
- Stalled stream ignores cancellation but returns timeout; user cancellation recognized; simultaneous commit refused: PASS.
- Signed production channel verifies, including BOM; unsafe paths rejected: PASS.
- Real Minecraft launch with clean Forge/libraries, Java 17, 4 GB RAM and full modpack: resource engine started in ~29 seconds, OpenAL and sound engine started; process remained alive for 100 seconds, then harness stopped only its own child.
- Live API /health: 200; unauthenticated /auth/me: 401; existing user's /auth/me: 200; game ticket endpoint: 200. No credentials or returned ticket are recorded.
- Live server status: OFFLINE. The paid server was not started for this audit.

Remaining bounds:
- Cannot verify joining the server while it is offline, mod handshake against that server, match gameplay, or memory stability during a match.
- Email/Telegram registration and recovery flows were not exercised because they send real messages or change account data.
- Actual disk exhaustion, power loss, antivirus interference and all Windows/GPU/ISP configurations were not reproduced. Fixture failures demonstrate preservation paths, not exhaustive environmental coverage.
- Mod content warnings remain: missing Warborn/AAS/VVP models and neoforge:separate_transforms loader references. They did not prevent the observed startup; do not silently modify third-party mod archives to suppress them.
- Public EXE has not been replaced. Candidate source and executable outputs are for verification, with no change to the user's installed launcher or gameplay folders.
- Test data is isolated at D:/ProjectBattle-Diagnostics-20261109.
