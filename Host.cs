using System;
using System.Diagnostics;
using System.IO;
using System.Management;
using System.Net;
using System.Security.Cryptography;
using System.Text;
using System.Web.Script.Serialization;
using System.Threading;
using System.Windows.Forms;
class Host {
 const string ForgeVersion="1.20.1-forge-47.4.28";
 const string ForgeInstallerUrl="https://maven.minecraftforge.net/net/minecraftforge/forge/1.20.1-47.4.28/forge-1.20.1-47.4.28-installer.jar";
 const string ForgeInstallerSha1="585dd6071897becaed3b09bff4c3d9559e0cbc75";
 static string Q(string text){return "\""+text+"\"";}
 static string Hash(string path){using(var sha=SHA256.Create())using(var f=File.OpenRead(path))return Convert.ToBase64String(sha.ComputeHash(f));}
 static string Sha1(string path){using(var sha=SHA1.Create())using(var f=File.OpenRead(path))return BitConverter.ToString(sha.ComputeHash(f)).Replace("-","").ToLowerInvariant();}
 static string ReadProperty(string file,string key,string fallback) {
  if(!File.Exists(file))return fallback;
  foreach(string raw in File.ReadAllLines(file,Encoding.UTF8)) {
   string line=raw.TrimStart();if(line.Length==0||line[0]=='#'||line[0]=='!')continue;
   int split=line.IndexOf('=');if(split<0)split=line.IndexOf(':');if(split<0)continue;
   if(line.Substring(0,split).Trim().Equals(key,StringComparison.Ordinal))return line.Substring(split+1).Trim().Replace("\\\\","\\");
  }
  return fallback;
 }
 static void SetProperty(string file,string key,string value) {
  Directory.CreateDirectory(Path.GetDirectoryName(file));
  var lines=File.Exists(file)?new System.Collections.Generic.List<string>(File.ReadAllLines(file,Encoding.UTF8)):new System.Collections.Generic.List<string>();
  bool found=false;
  for(int i=0;i<lines.Count;i++) {
   string t=lines[i].TrimStart();if(t.Length==0||t[0]=='#'||t[0]=='!')continue;
   int split=t.IndexOf('=');if(split<0)split=t.IndexOf(':');if(split<0)continue;
   if(t.Substring(0,split).Trim().Equals(key,StringComparison.Ordinal)){lines[i]=key+"="+value;found=true;break;}
  }
  if(!found)lines.Add(key+"="+value);
  string temp=file+".next";File.WriteAllLines(temp,lines, new UTF8Encoding(false));
  if(File.Exists(file))File.Replace(temp,file,null);else File.Move(temp,file);
 }
 static void EnsureMinecraftBase(string minecraftDir,string tempDir) {
  string baseDir=Path.Combine(minecraftDir,"versions","1.20.1");
  string baseJson=Path.Combine(baseDir,"1.20.1.json");
  Directory.CreateDirectory(baseDir);
  if(!File.Exists(baseJson)) {
   ServicePointManager.SecurityProtocol|=SecurityProtocolType.Tls12;
   using(var client=new WebClient()) {
    string manifest=client.DownloadString("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json");
    var serializer=new JavaScriptSerializer();
    var root=(System.Collections.Generic.Dictionary<string,object>)serializer.DeserializeObject(manifest);
    var versions=(object[])root["versions"];
    string metadataUrl=null;
    foreach(object item in versions) {
     var version=(System.Collections.Generic.Dictionary<string,object>)item;
     if(Convert.ToString(version["id"])=="1.20.1"){metadataUrl=Convert.ToString(version["url"]);break;}
    }
    if(String.IsNullOrEmpty(metadataUrl))throw new IOException("Не удалось найти официальные данные Minecraft 1.20.1.");
    string partial=Path.Combine(tempDir,"minecraft-1.20.1.json.download");
    client.DownloadFile(metadataUrl,partial);
    if(File.Exists(baseJson))File.Delete(baseJson);File.Move(partial,baseJson);
   }
  }
  string profiles=Path.Combine(minecraftDir,"launcher_profiles.json");
  if(!File.Exists(profiles))File.WriteAllText(profiles,"{\"profiles\":{\"projectbattle\":{\"name\":\"Project Battle\",\"lastVersionId\":\"1.20.1\",\"type\":\"custom\"}},\"selectedProfile\":\"projectbattle\"}",new UTF8Encoding(false));
 }
 static bool ForgeInstalled(string minecraftDir) {
  string versions=Path.Combine(minecraftDir,"versions",ForgeVersion);
  string descriptor=Path.Combine(versions,ForgeVersion+".json");
  string universal=Path.Combine(minecraftDir,"libraries","net","minecraftforge","forge","1.20.1-47.4.28","forge-1.20.1-47.4.28-universal.jar");
  string client=Path.Combine(minecraftDir,"versions","1.20.1","1.20.1.jar");
  if(!File.Exists(descriptor)||!File.Exists(universal)||new FileInfo(universal).Length<1024||!File.Exists(client))return false;
  try {
   var root=(System.Collections.Generic.Dictionary<string,object>)new JavaScriptSerializer().DeserializeObject(File.ReadAllText(descriptor,Encoding.UTF8));
   if(!root.ContainsKey("id")||!Convert.ToString(root["id"]).Equals(ForgeVersion,StringComparison.Ordinal)||!root.ContainsKey("libraries"))return false;
   foreach(object item in (object[])root["libraries"]) {
    var library=item as System.Collections.Generic.Dictionary<string,object>;if(library==null||!library.ContainsKey("downloads"))continue;
    var downloads=library["downloads"] as System.Collections.Generic.Dictionary<string,object>;if(downloads==null||!downloads.ContainsKey("artifact"))continue;
    var artifact=downloads["artifact"] as System.Collections.Generic.Dictionary<string,object>;if(artifact==null||!artifact.ContainsKey("path"))continue;
    string relative=Convert.ToString(artifact["path"]).Replace('/',Path.DirectorySeparatorChar);
    if(!File.Exists(Path.Combine(minecraftDir,"libraries",relative)))return false;
   }
   return true;
  } catch(IOException){return false;}
    catch(InvalidCastException){return false;}
    catch(ArgumentException){return false;}
 }
 static void EnsureForge(string profile,string tempDir,string bundledJava) {
  string config=Path.Combine(profile,"ProjectBattleLauncher","launcher.properties");
  string minecraftDir=ReadProperty(config,"minecraftDir",Path.Combine(profile,".minecraft"));
  if(!Path.IsPathRooted(minecraftDir))minecraftDir=Path.GetFullPath(Path.Combine(profile,minecraftDir));
  Directory.CreateDirectory(minecraftDir);Directory.CreateDirectory(tempDir);
  if(!ForgeInstalled(minecraftDir)) {
   ServicePointManager.SecurityProtocol|=SecurityProtocolType.Tls12;
   EnsureMinecraftBase(minecraftDir,tempDir);
   string installer=Path.Combine(tempDir,"forge-1.20.1-47.4.28-installer.jar");
   string partial=installer+".download";
   try {
    using(var client=new WebClient())client.DownloadFile(ForgeInstallerUrl,partial);
    if(!Sha1(partial).Equals(ForgeInstallerSha1,StringComparison.OrdinalIgnoreCase))throw new IOException("Контрольная сумма установщика Forge не совпала. Загрузка отменена.");
    if(File.Exists(installer))File.Delete(installer);File.Move(partial,installer);
    var install=new ProcessStartInfo(Path.Combine(bundledJava,"bin","java.exe"),"-jar "+Q(installer)+" --installClient "+Q(minecraftDir)) {
     WorkingDirectory=tempDir,UseShellExecute=false,CreateNoWindow=true,RedirectStandardOutput=true,RedirectStandardError=true
    };
    using(var process=Process.Start(install)) {
     string output=process.StandardOutput.ReadToEnd();string error=process.StandardError.ReadToEnd();process.WaitForExit();
     if(process.ExitCode!=0)throw new IOException("Не удалось установить Forge 47.4.28 (код "+process.ExitCode+"). Проверьте подключение к интернету и повторите запуск."+(String.IsNullOrWhiteSpace(error)?"":"\n"+error.Trim()));
    }
    if(!ForgeInstalled(minecraftDir))throw new IOException("Установщик завершился, но файлы Forge 47.4.28 не прошли проверку. Повторите запуск; старая версия Forge не удалена.");
   } finally {try{if(File.Exists(partial))File.Delete(partial);}catch(IOException){}}
  }
  SetProperty(config,"forgeVersionId",ForgeVersion);
 }
 static bool Inside(string root,string path) {
  string prefix=Path.GetFullPath(root).TrimEnd(Path.DirectorySeparatorChar)+Path.DirectorySeparatorChar;
  return Path.GetFullPath(path).StartsWith(prefix,StringComparison.OrdinalIgnoreCase);
 }
 static string UserData() {return Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData),"Project Battle Auto");}
 static void CleanupUnusedVersions(string data,string currentPackage) {
  string versions=Path.GetFullPath(Path.Combine(data,"versions"));
  string current=Path.GetFullPath(currentPackage);
  if(!Path.GetDirectoryName(current).Equals(versions,StringComparison.OrdinalIgnoreCase))return;
  foreach(string path in Directory.GetDirectories(versions)) {
   string full=Path.GetFullPath(path);
   if(full.Equals(current,StringComparison.OrdinalIgnoreCase))continue;
   string name=Path.GetFileName(full);
   int separator=name.LastIndexOf('-');
   Version version;Guid id;
   if(separator<1||!Version.TryParse(name.Substring(0,separator),out version)||
      !Guid.TryParseExact(name.Substring(separator+1),"N",out id))continue;
   try {
    if((new DirectoryInfo(full).Attributes&FileAttributes.ReparsePoint)!=0)continue;
    Directory.Delete(full,true);
   } catch(IOException) { } catch(UnauthorizedAccessException) { }
  }
 }
 [STAThread]static void Main(string[] args) {
  try {
   if(args.Length==4 && args[0]=="--replace") {
    int pid=Int32.Parse(args[1]);
    try {using(var p=Process.GetProcessById(pid)) if(!p.WaitForExit(60000))throw new IOException("Закройте предыдущий обновлятор и повторите запуск.");}catch(ArgumentException){}
    string destination=Path.GetFullPath(args[2]),source=Path.GetFullPath(args[3]);
    string outer=Path.GetDirectoryName(destination);
    if(!Inside(Path.Combine(outer,"Project Battle Data","versions"),source)&&
       !Inside(Path.Combine(UserData(),"versions"),source))throw new IOException("Некорректный путь обновления EXE.");
    string temp=destination+".next";
    File.Copy(source,temp,true);
    if(Hash(temp)!=Hash(source))throw new IOException("Не удалось проверить новый EXE.");
    bool done=false;
    for(int i=0;i<40&&!done;i++)try {File.Replace(temp,destination,destination+".previous",true);done=true;}catch(IOException){Thread.Sleep(250);}
    if(!done)throw new IOException("EXE занят. Закройте лаунчер и повторите запуск.");
    Process.Start(new ProcessStartInfo(destination){WorkingDirectory=outer,UseShellExecute=false});return;
   }
   string package=AppDomain.CurrentDomain.BaseDirectory.TrimEnd(Path.DirectorySeparatorChar);
   string data=Directory.GetParent(Directory.GetParent(package).FullName).FullName;
   int parentId;
   if(!Int32.TryParse(Environment.GetEnvironmentVariable("PB_BOOTSTRAP_PID"),out parentId)) {
    using(var wmi=new ManagementObject("win32_process.handle='"+Process.GetCurrentProcess().Id+"'")){wmi.Get();parentId=Convert.ToInt32(wmi["ParentProcessId"]);}
   }
   string bootstrap;
   using(var parent=Process.GetProcessById(parentId))bootstrap=parent.MainModule.FileName;
   string outerDir=Path.GetDirectoryName(bootstrap);
   if(!data.Equals(Path.Combine(outerDir,"Project Battle Data"),StringComparison.OrdinalIgnoreCase)&&
      !data.Equals(UserData(),StringComparison.OrdinalIgnoreCase))throw new IOException("Некорректное расположение файлов лаунчера.");
   string replacement=Path.Combine(package,"bootstrap","Project Battle.exe");
   if(Hash(bootstrap)!=Hash(replacement)) {
    var helper=new ProcessStartInfo(Process.GetCurrentProcess().MainModule.FileName,"--replace "+parentId+" "+Q(bootstrap)+" "+Q(replacement)){UseShellExecute=false,CreateNoWindow=true};
    Process.Start(helper);return;
   }
   string profile=Path.Combine(data,"profile"),tempDir=Path.Combine(profile,"temp");Directory.CreateDirectory(tempDir);
   string bundledJava=Path.Combine(package,"java17");
   if(!File.Exists(Path.Combine(bundledJava,"bin","java.exe")))throw new IOException("В пакете отсутствует Java 17. Переустановите лаунчер.");
   EnsureForge(profile,tempDir,bundledJava);
   string entry=Path.Combine(package,"Project Battle Alpha 0.1.76","Project Battle Alpha 0.1.76.exe");
   var launch=new ProcessStartInfo(entry){WorkingDirectory=Path.GetDirectoryName(entry),UseShellExecute=false};
   launch.EnvironmentVariables["APPDATA"]=profile;launch.EnvironmentVariables["TEMP"]=tempDir;launch.EnvironmentVariables["TMP"]=tempDir;
   launch.EnvironmentVariables["JAVA_HOME"]=bundledJava;
   launch.EnvironmentVariables["PATH"]=Path.Combine(bundledJava,"bin")+";"+Environment.GetEnvironmentVariable("PATH");
   using(var game=Process.Start(launch)) {
    // The current launcher has started; older complete packages are no longer needed.
    CleanupUnusedVersions(data,package);
    game.WaitForExit();
   }
  }catch(Exception ex){MessageBox.Show(ex.Message,"Project Battle",MessageBoxButtons.OK,MessageBoxIcon.Error);}
 }
}
