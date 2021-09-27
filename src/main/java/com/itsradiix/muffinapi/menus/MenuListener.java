package com.itsradiix.muffinapi.menus;


import com.itsradiix.muffinapi.exceptions.MenuManagerException;
import com.itsradiix.muffinapi.exceptions.MenuManagerNotSetupException;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class MenuListener implements Listener {

	@EventHandler
	public void onMenuClick(InventoryClickEvent e){

		InventoryHolder holder = e.getInventory().getHolder();
		if (holder instanceof Menu) {
			if (e.getCurrentItem() == null) {
				return;
			}

			Menu menu = (Menu) holder;

			if (menu.cancelAllClicks()){
				e.setCancelled(true);
			}

			try{
				menu.handleMenu(e);
			} catch (MenuManagerNotSetupException menuManagerNotSetupException) {
				System.out.println(ChatColor.RED + "THE MENU MANAGER HAS NOT BEEN CONFIGURED. CALL MENUMANAGER.SETUP()");
			} catch (MenuManagerException menuManagerException) {
				menuManagerException.printStackTrace();
			}
		}

	}

}

