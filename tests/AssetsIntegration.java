package com.warproject.launcher;
import java.lang.reflect.*;import java.nio.file.*;import javax.swing.*;
public class AssetsIntegration {
 static void set(Object o,String name,Object value)throws Exception{Field f=o.getClass().getDeclaredField(name);f.setAccessible(true);f.set(o,value);}
 public static void main(String[] args)throws Exception {
  Path root=Path.of(args[0]);Class<?> c=Class.forName("com.warproject.launcher.ProjectBattleAlpha$Config");var ctor=c.getDeclaredConstructor(Path.class);ctor.setAccessible(true);Object config=ctor.newInstance(root.resolve("config"));set(config,"minecraftDir",root.resolve(".minecraft"));
  Field uf=sun.misc.Unsafe.class.getDeclaredField("theUnsafe");uf.setAccessible(true);sun.misc.Unsafe u=(sun.misc.Unsafe)uf.get(null);
  Class<?> app=Class.forName("com.warproject.launcher.ProjectBattleAlpha");Object instance=u.allocateInstance(app);set(instance,"config",config);set(instance,"logArea",new JTextArea());
  Method prepare=app.getDeclaredMethod("ensureVanillaMinecraftVersion");prepare.setAccessible(true);prepare.invoke(instance);
  System.out.println("PASS official vanilla client and complete asset preparation");System.exit(0);
 }
}
