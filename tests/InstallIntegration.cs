using System;using System.IO;using System.IO.Compression;using System.Security.Cryptography;using ProjectBattleAuto;
class InstallIntegration {
 static Release R(string zip,string version){return new Release{version=version,entry="app.exe",size=new FileInfo(zip).Length,sha256=BitConverter.ToString(SHA256.Create().ComputeHash(File.ReadAllBytes(zip))).Replace("-","")};}
 static string Zip(string dir,string name,string entry){string path=Path.Combine(dir,name);using(var z=ZipFile.Open(path,ZipArchiveMode.Create)){using(var w=new StreamWriter(z.CreateEntry(entry).Open()))w.Write("fixture");}return path;}
 static void Main(){string dir=Path.Combine(Path.GetTempPath(),"pb-install-"+Guid.NewGuid());Directory.CreateDirectory(dir);Engine.Root=Path.Combine(dir,"data");
  string zip=Zip(dir,"valid.zip","app.exe");var first=Engine.Commit(zip,R(zip,"1.0.0"));string state=File.ReadAllText(Path.Combine(Engine.Root,"current.json"));
  foreach(string entry in new[]{"../escape.exe","missing.exe"}){string bad=Zip(dir,Guid.NewGuid()+".zip",entry);try{Engine.Commit(bad,R(bad,"1.0.1"));throw new Exception("accepted invalid package");}catch(IOException){}if(File.ReadAllText(Path.Combine(Engine.Root,"current.json"))!=state)throw new Exception("lost current");}
  var corrupt=R(zip,"1.0.2");corrupt.sha256=new string('0',64);try{Engine.Commit(zip,corrupt);throw new Exception("accepted wrong hash");}catch(IOException){}
  Engine.Commit(zip,R(zip,"1.0.3"));if(File.ReadAllText(Path.Combine(Engine.Root,"previous.json"))!=state)throw new Exception("lost rollback");
  if(Engine.Current().version!="1.0.3")throw new Exception();
  File.WriteAllText(Path.Combine(Engine.Root,"current.json"),"broken json");if(Engine.Current()==null||Engine.Current().version!="1.0.0")throw new Exception("fallback failed");
  Console.WriteLine("PASS fresh commit, traversal rejection, missing entry, wrong hash, atomic replacement, rollback pointer, corrupt state");
 }
}
