package com.itsradiix.muffinapi.menus;

import com.itsradiix.muffinapi.exceptions.MenuManagerException;
import com.itsradiix.muffinapi.exceptions.MenuManagerNotSetupException;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class MenuManager {

	private static final Map<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
	private static boolean isSetup = false;

	private static void registerMenuListener(Server server, Plugin plugin){

		boolean isAlreadyRegistered = false;
		for (RegisteredListener rl : InventoryClickEvent.getHandlerList().getRegisteredListeners()){
			System.out.println(rl.getListener().getClass().getSimpleName());
			if (rl.getListener() instanceof MenuListener) {
				isAlreadyRegistered = true;
				break;
			}
		}
		if (!isAlreadyRegistered){
			server.getPluginManager().registerEvents(new MenuListener(), plugin);
		}
	}

	public static void setup(Server server, Plugin plugin){
		registerMenuListener(server, plugin);
		isSetup = true;
	}

	public static void openMenu(Class<? extends Menu> menuClass, Player player) throws MenuManagerException, MenuManagerNotSetupException {
		try {
			menuClass.getConstructor(PlayerMenuUtility.class).newInstance(getPlayerMenuUtility(player)).open();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
			throw new MenuManagerException();
		}
	}

	public static PlayerMenuUtility getPlayerMenuUtility(Player p) throws MenuManagerNotSetupException {

		if (!isSetup){
			throw new MenuManagerNotSetupException();
		}

		PlayerMenuUtility playerMenuUtility;
		if (!(playerMenuUtilityMap.containsKey(p))) {

			//Construct PMU
			playerMenuUtility = new PlayerMenuUtility(p);
			playerMenuUtilityMap.put(p, playerMenuUtility);

			return playerMenuUtility;
		} else {
			return playerMenuUtilityMap.get(p);
		}
	}

}
