# Project Battle Java launcher maintenance

The original Java source was unavailable. These four files were recovered from the ProjectBattleAlpha 0.1.78.12 JAR with Vineflower and repaired where decompilation lost Java types. `ProjectBattleAlpha.java` and `DiscordPresenceService.java` replace those classes in a supplied base JAR; `PackMaintenance.java` and `PackDownloadManager.java` are new. The unchanged classes and assets remain in the base JAR.

Build with JDK 21 and PowerShell:

```powershell
./Build-Java.ps1 -BaseJar 'C:\path\ProjectBattleAlpha-0.1.78.12.jar' -OutputJar 'C:\path\ProjectBattleAlpha-0.1.78.13.jar'
```

The build script does not publish or sign an update. Review and validate the generated launcher with a disposable game profile before distributing it. The exaroton status panel requires `/server/status` on the Project Battle auth API and server credentials configured on that backend. No exaroton token belongs in the JAR.
