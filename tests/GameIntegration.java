package com.warproject.launcher;
import java.lang.reflect.*;import java.nio.file.*;
public class GameIntegration {
 static void set(Object o,String n,Object v)throws Exception{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);f.set(o,v);}
 public static void main(String[] a)throws Exception{
  Path root=Path.of(a[0]);Class<?> c=Class.forName("com.warproject.launcher.ProjectBattleAlpha$Config");var ctor=c.getDeclaredConstructor(Path.class);ctor.setAccessible(true);Object config=ctor.newInstance(root.resolve("profile/config"));set(config,"minecraftDir",root.resolve("profile/.minecraft"));set(config,"instanceDir",root.resolve("pack"));set(config,"autoJoinServer",false);set(config,"ramMb",4096);set(config,"playerName","Diagnostics");
  Class<?> sink=Class.forName("com.warproject.launcher.ProjectBattleAlpha$LogSink");Object log=Proxy.newProxyInstance(sink.getClassLoader(),new Class[]{sink},(o,m,args)->{System.out.println(args[0]);return null;});Class<?> r=Class.forName("com.warproject.launcher.ProjectBattleAlpha$Runner");var rc=r.getDeclaredConstructor(c,sink);rc.setAccessible(true);Object runner=rc.newInstance(config,log);var launch=r.getDeclaredMethod("launch");launch.setAccessible(true);Process game=(Process)launch.invoke(runner);
  Thread reader=new Thread(()->{try{Files.copy(game.getInputStream(),root.resolve("game-smoke.log"),StandardCopyOption.REPLACE_EXISTING);}catch(Exception e){e.printStackTrace();}});reader.start();
  boolean exited=game.waitFor(100,java.util.concurrent.TimeUnit.SECONDS);if(exited){System.out.println("GAME_EXIT="+game.exitValue());}else{System.out.println("GAME_ALIVE_AFTER_100_SECONDS pid="+game.pid());game.destroy();if(!game.waitFor(10,java.util.concurrent.TimeUnit.SECONDS))game.destroyForcibly();}reader.join(5000);System.exit(exited&&game.exitValue()!=0?1:0);
 }
}
