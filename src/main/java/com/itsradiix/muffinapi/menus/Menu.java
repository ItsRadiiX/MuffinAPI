package com.itsradiix.muffinapi.menus;


import com.itsradiix.muffinapi.MuffinAPI;
import com.itsradiix.muffinapi.exceptions.MenuManagerException;
import com.itsradiix.muffinapi.exceptions.MenuManagerNotSetupException;
import com.itsradiix.muffinapi.messages.ColorTranslator;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Abstract Menu Class on which can be extended to create a normal menu
 */
public abstract class Menu implements InventoryHolder {

	// Important static variables
	protected static final JavaPlugin core = MuffinAPI.getPlugin();

	// Important Inventory variables
	protected PlayerMenuUtility playerMenuUtility;
	protected Player p;
	protected Inventory inventory;

	// Important opening variables
	boolean silentOpening = false;

	// Important integer row variables
	protected int rows = getSlots()/9;
	protected int inBetweenRows = rows-2;

	/**
	 * Default Menu Constructor
	 * @param playerMenuUtility playerMenuUtility of the player to open menu for.
	 */
	public Menu(PlayerMenuUtility playerMenuUtility) {
		this.playerMenuUtility = playerMenuUtility;
		p = playerMenuUtility.getOwner();
		inventory = Bukkit.createInventory(this, getSlots(), Component.text(ColorTranslator.translateColorCodes(getMenuName())));
	}

	/**
	 * let each menu decide their name
	 * @return String returns menu name
	 */
	public abstract String getMenuName();

	/**
	 * let each menu decide their own open sound
	 * @return Sound returns Open Sound
	 */
	public abstract Sound openSound();

	/**
	 * let each menu decide their own close sound
	 * @return Sound returns Close Sound
	 */
	public abstract Sound closeSound();

	/**
	 * let each menu decide their slot amount
	 * @return int returns slots amount
	 */
	public abstract int getSlots();

	public abstract boolean cancelAllClicks();

	/**
	 * let each menu decide their filler glass type
	 * @return returns ItemStack of filler Glass
	 */
	public abstract ItemStack getFillerGlass();

	/**
	 * let each menu decide how the items in the menu will be handled when clicked
	 * @param e InventoryClickEvent
	 */
	public abstract void handleMenu(InventoryClickEvent e) throws MenuManagerException, MenuManagerNotSetupException;

	/**
	 * let each menu decide what items are to be placed in the inventory menu
	 */
	public abstract void setMenuItems();

	/**
	 * Open menu asynchronously.
	 */
	public void open() {
		Bukkit.getScheduler().runTaskAsynchronously(core, () ->{
			setMenuItems();
			Bukkit.getScheduler().runTask(core, () -> openInventory(p));
			playerMenuUtility.pushMenu(this);
		});
	}

	public void back() throws MenuManagerException, MenuManagerNotSetupException {
		MenuManager.openMenu(playerMenuUtility.lastMenu().getClass(), playerMenuUtility.getOwner());
	}

	/**
	 * when called, the inventory is closed for the player.
	 */
	public void close(){
		playSound(closeSound());
		p.closeInventory();
	}

	/**
	 * shortcut method for opening inventory for player
	 * @param p Player to open Inventory for
	 */
	private void openInventory(Player p){
		p.openInventory(inventory);
	}

	/**
	 * silent open the menu
	 */
	public void silentOpen(){
		silentOpening = true;
		open();
	}

	/**
	 * reload menu
	 */
	public void reload() throws MenuManagerException, MenuManagerNotSetupException {
		p.closeInventory();
		MenuManager.openMenu(this.getClass(), p);
	}

	public void reloadItems() {
		for (int i = 0; i < inventory.getSize(); i++) {
			inventory.setItem(i, null);
		}
		setMenuItems();
	}

	public void asyncReloadItems(){
		Bukkit.getScheduler().runTaskAsynchronously(core, this::reloadItems);
	}

	/**
	 * @return boolean return if silent opening
	 */
	public boolean isSilentOpening() {
		return silentOpening;
	}

	/**
	 * Overridden method from the InventoryHolder interface
	 * @return Inventory returns inventory object
	 */
	@Override
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * Helpful utility method to fill all remaining slots with "filler glass"
	 */
	public void setFillerGlass(){
		for (int i = 0; i < getSlots(); i++) {
			if (inventory.getItem(i) == null){
				inventory.setItem(i, getFillerGlass());
			}
		}
	}

	/**
	 * Helpful utility method to fill all outer slots with "filler glass"
	 */
	public void setOuterFillerGlass(){

		if (inBetweenRows <= 0) {
			setFillerGlass();
		} else {
			for (int i = 0; i < 9; i++) {
				if (inventory.getItem(i) == null){
					inventory.setItem(i, getFillerGlass());
				}
			}

			for (int i = 0; i < inBetweenRows; i++) {

				int index = 9*(i+1);

				if (inventory.getItem(index) == null){
					inventory.setItem(index, getFillerGlass());
				}
				index = index+8;
				if (inventory.getItem(index) == null){
					inventory.setItem(index, getFillerGlass());
				}
			}

			for (int i = ((9*rows)-9); i < ((9*rows)); i++) {
				if (inventory.getItem(i) == null){
					inventory.setItem(i, getFillerGlass());
				}
			}
		}
	}


	/**
	 * Handles Border clickEvent
	 * @param e InventoryClickEvent needs to be passed down
	 * @return boolean if border was clicked
	 */
	public boolean handleBorder(InventoryClickEvent e){
		ItemStack clickedItem = e.getCurrentItem();
		return clickedItem.equals(getFillerGlass());
	}

	/**
	 * PlaySound for player
	 * @param sound Sound to play
	 */
	public void playSound(Sound sound){
		if (sound != null && p != null){
			p.playSound(p.getLocation(), sound, 0.1F, 1.0F);
		}
	}

	public ItemStack makeItem(Material material, String displayName, String... lore) {

		ItemStack item = new ItemStack(material);
		ItemMeta itemMeta = item.getItemMeta();
		if (itemMeta == null) {
			return item;
		}
		itemMeta.setDisplayName(displayName);

		//Automatically translate color codes provided
		itemMeta.setLore(Arrays.stream(lore).map(ColorTranslator::translateColorCodes).collect(Collectors.toList()));
		item.setItemMeta(itemMeta);

		return item;
	}
}
