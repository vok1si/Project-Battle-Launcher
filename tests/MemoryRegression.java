package com.warproject.launcher;
import java.nio.file.*;
public class MemoryRegression {public static void main(String[] args)throws Exception{Path p=Files.createTempDirectory("pb-memory");Files.writeString(p.resolve("hs_err_pid42.log"),"# There is insufficient memory for the Java Runtime Environment to continue.\n");if(!PackMaintenance.processFailure(p,42,1).contains("памяти"))throw new AssertionError();if(!PackMaintenance.processFailure(p,43,1).equals("Minecraft exited with code 1"))throw new AssertionError();System.out.println("PASS memory diagnosis and unrelated crash ignored");}}
