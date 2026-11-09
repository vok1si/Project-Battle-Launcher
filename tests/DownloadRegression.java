package com.warproject.launcher;
import com.sun.net.httpserver.HttpServer;
import java.net.*;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicInteger;
public class DownloadRegression {
 public static void main(String[] args) throws Exception {
  Path root=Files.createTempDirectory("pb-download-test");
  byte[] good="verified payload".getBytes(); Path source=root.resolve("source"); Files.write(source,good);
  String hash=PackMaintenance.sha256(source);
  HttpServer server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
  AtomicInteger calls=new AtomicInteger();
  server.createContext("/file",e->{calls.incrementAndGet(); String mode=e.getRequestURI().getQuery(); byte[] data=good; int status=200;
   if("416".equals(mode)) {e.sendResponseHeaders(416,-1);e.close();return;}
   if("404".equals(mode)) {e.sendResponseHeaders(404,-1);e.close();return;}
   if("bad".equals(mode)) data="wrong!!! payload".getBytes();
   if("large".equals(mode)) data=new byte[10000];
   if("range".equals(mode)) {status=206;e.getResponseHeaders().set("Content-Range","bytes 2-15/16");}
   if("resume".equals(mode)) {status=206;data=java.util.Arrays.copyOfRange(good,4,good.length);e.getResponseHeaders().set("Content-Range","bytes 4-"+(good.length-1)+"/"+good.length);}
   e.sendResponseHeaders(status,data.length);try(var out=e.getResponseBody()){out.write(data);}e.close();
  }); server.start();
  try {
   for(String mode:new String[]{"ok","416","404","bad","large","range","resume"}) {
    calls.set(0); Path target=root.resolve(mode); Files.writeString(target,"old");
    if(mode.equals("resume"))Files.write(target.resolveSibling(mode+".part"),java.util.Arrays.copyOf(good,4));
    boolean success=false;
    try {new PackDownloadManager().download(mode,new URL("http://127.0.0.1:"+server.getAddress().getPort()+"/file?"+mode),target,good.length,hash);success=true;}catch(java.io.IOException expected){}
    boolean expected=mode.equals("ok")||mode.equals("resume");
    if(success!=expected || calls.get()>3 || (!success&&!Files.readString(target).equals("old")))throw new AssertionError(mode);
    if(success&&!PackMaintenance.sha256(target).equals(hash))throw new AssertionError(mode);
    System.out.println("PASS "+mode+" requests="+calls.get());
   }
  }finally{server.stop(0);}
 }
}
