using System;
using System.Diagnostics;
using System.IO;
using System.Management;
using System.Security.Cryptography;
using System.Threading;
using System.Windows.Forms;
class Host {
 static string Q(string text){return "\""+text+"\"";}
 static string Hash(string path){using(var sha=SHA256.Create())using(var f=File.OpenRead(path))return Convert.ToBase64String(sha.ComputeHash(f));}
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
   string entry=Path.Combine(package,"Project Battle Alpha 0.1.76","Project Battle Alpha 0.1.76.exe");
   var launch=new ProcessStartInfo(entry){WorkingDirectory=Path.GetDirectoryName(entry),UseShellExecute=false};
   launch.EnvironmentVariables["APPDATA"]=profile;launch.EnvironmentVariables["TEMP"]=tempDir;launch.EnvironmentVariables["TMP"]=tempDir;
   string bundledJava=Path.Combine(package,"java17");
   if(!File.Exists(Path.Combine(bundledJava,"bin","java.exe")))throw new IOException("В пакете отсутствует Java 17. Переустановите лаунчер.");
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
