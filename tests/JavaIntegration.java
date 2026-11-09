package com.warproject.launcher;
import java.nio.file.*;
public class JavaIntegration {public static void main(String[] a)throws Exception{PackMaintenance.requireJava17(Path.of(a[0]));try{PackMaintenance.requireJava17(Path.of(a[1]));throw new AssertionError();}catch(java.io.IOException expected){}System.out.println("PASS Java 17 accepted, Java 21 rejected");}}
