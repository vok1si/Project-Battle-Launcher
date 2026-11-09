package com.warproject.launcher;
import java.lang.reflect.*;import java.nio.file.*;import java.net.*;import java.util.*;
public class PackIntegration {
 static Object field(Object o,String n)throws Exception{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);return f.get(o);}
 public static void main(String[] args)throws Exception{
  Path root=Path.of(args[0]);Files.createDirectories(root);
  Class<?> c=Class.forName("com.warproject.launcher.ProjectBattleAlpha$Config");var constructor=c.getDeclaredConstructor(Path.class);constructor.setAccessible(true);Object config=constructor.newInstance(root.resolve("config"));
  Class<?> m=Class.forName("com.warproject.launcher.ProjectBattleAlpha$Manifest");Method load=m.getDeclaredMethod("load",c);load.setAccessible(true);Object manifest=load.invoke(null,config);
  var specs=new ArrayList<PackMaintenance.FileSpec>();
  for(Object f:(List<?>)field(manifest,"files")){specs.add(new PackMaintenance.FileSpec((String)field(f,"path"),(String)field(f,"url"),(String)field(f,"sha256"),(Long)field(f,"size"),(Boolean)field(f,"required")));}
  PackDownloadManager dl=new PackDownloadManager();int i=0;
  for(var f:specs){Path p=PackMaintenance.inside(root,f.path());dl.download(f.path(),new URL(f.url()),p,f.size(),f.sha256());System.out.println("Downloaded "+(++i)+"/"+specs.size());}
  if(!PackMaintenance.inspect(root,specs).isEmpty())throw new AssertionError("integrity");
  var victim=specs.get(specs.size()-1);Path v=PackMaintenance.inside(root,victim.path());Files.writeString(v,"corrupted");var issues=PackMaintenance.inspect(root,specs);if(issues.size()!=1)throw new AssertionError("repair selection");dl.download(victim.path(),new URL(victim.url()),v,victim.size(),victim.sha256());
  if(!PackMaintenance.inspect(root,specs).isEmpty())throw new AssertionError("repair failed");System.out.println("PASS fresh pack and selective repair: "+field(manifest,"version"));
 }
}
