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
