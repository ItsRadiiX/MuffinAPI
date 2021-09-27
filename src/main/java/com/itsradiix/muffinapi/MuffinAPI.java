package com.itsradiix.muffinapi;

import com.itsradiix.muffinapi.menus.MenuManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MuffinAPI {

	private static JavaPlugin plugin;

	 public void hook(JavaPlugin plugin){
		MuffinAPI.plugin = plugin;
		MenuManager.setup(plugin.getServer(), plugin);
	 }

	public static JavaPlugin getPlugin() {
		return plugin;
	}
}
