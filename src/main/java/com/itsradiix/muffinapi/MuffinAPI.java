package com.itsradiix.muffinapi;

import com.itsradiix.muffinapi.menus.MenuManager;
import com.itsradiix.muffinapi.messages.ColorTranslator;
import org.bukkit.plugin.java.JavaPlugin;

public final class MuffinAPI {

	private static JavaPlugin plugin;
	private static final String version = "1.0";

	 public void hook(JavaPlugin plugin){
		MuffinAPI.plugin = plugin;
		MenuManager.setup(plugin.getServer(), plugin);

		log("&6MuffinAPI&7 has hooked into &6" + plugin.getName());
		log("");
		log("&a  __  __        __  __ _                _____ _____        _.-------._");
		log("&a |  \\/  |      / _|/ _(_)         /\\   |  __ \\_   _|    .-';  ;`-'& ; `&.");
		log("&a | \\  / |_   _| |_| |_ _ _ __    /  \\  | |__) || |     & &  ;  &   ; ;   \\");
		log("&a | |\\/| | | | |  _|  _| | '_ \\  / /\\ \\ |  ___/ | |     \\      ;    &   &_/");
		log("&a | |  | | |_| | | | | | | | | |/ ____ \\| |    _| |_     F\"\"\"---...---\"\"\"J");
		log("&a |_|  |_|\\__,_|_| |_| |_|_| |_/_/    \\_\\_|   |_____|    | | | | | | | | |");
		log("&a                                                        J | | | | | | | F");
		log("&7Version: &a" + version + "                                            `---.|.|.|.---'");
		log("");
	 }

	public static JavaPlugin getPlugin() {
		return plugin;
	}

	private void log(String log){
		 plugin.getLogger().info(ColorTranslator.translateColorCodes(log));
	}

	public static String getVersion() {
		return version;
	}
}
