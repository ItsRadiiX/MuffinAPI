package com.itsradiix.muffinapi;

import org.bukkit.plugin.java.JavaPlugin;

public final class MuffinAPI {

	private static JavaPlugin plugin;

	 public void hook(JavaPlugin plugin){
		MuffinAPI.plugin = plugin;
	 }

	 public void unhook(){

	 }

	public static JavaPlugin getPlugin() {
		return plugin;
	}
}
