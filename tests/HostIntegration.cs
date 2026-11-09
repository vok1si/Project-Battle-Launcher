using System;using System.IO;using System.Diagnostics;using System.Reflection;
class HostIntegration {
 static object Call(string name,params object[] args){try{return typeof(Host).GetMethod(name,BindingFlags.Static|BindingFlags.NonPublic).Invoke(null,args);}catch(TargetInvocationException e){throw e.InnerException;}}
 static void Main(string[] args){
  if(args.Length>0&&args[0]=="fresh") {Directory.CreateDirectory(args[1]);Call("EnsureForge",args[1],Path.Combine(args[1],"temp"),args[2]);if(!(bool)Call("ForgeInstalled",Path.Combine(args[1],".minecraft")))throw new Exception("not installed");Console.WriteLine("PASS fresh Forge installation");return;}
  var start=new ProcessStartInfo("powershell.exe","-NoProfile -Command \"[Console]::Error.Write(('E'*200000));[Console]::Out.Write(('O'*200000))\""){UseShellExecute=false,CreateNoWindow=true,RedirectStandardOutput=true,RedirectStandardError=true};
  object[] call={start,15000,null,null};int code=(int)Call("RunInstaller",call);if(code!=0||((string)call[2]).Length!=200000||((string)call[3]).Length!=200000)throw new Exception("stream drain");Console.WriteLine("PASS concurrent stdout/stderr drain");
  start.Arguments="-NoProfile -Command Start-Sleep -Seconds 30";try{Call("RunInstaller",start,1000,null,null);throw new Exception("timeout absent");}catch(IOException){Console.WriteLine("PASS installer timeout");}
 }
}
