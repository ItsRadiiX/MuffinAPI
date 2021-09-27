package com.itsradiix.muffinapi.menus;

import com.itsradiix.muffinapi.models.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public abstract class PaginatedMenu extends Menu {

	// Important variables for PaginatedMenu
	protected int page = 0;
	protected int maxItemsPerPage = 28;
	protected int index = 0;
	protected int[] itemIntList = getItemIntList();

	/**
	 * Default PaginatedMenu Constructor
	 * @param playerMenuUtility playerMenuUtility of the player to open PaginatedMenu for.
	 */
	public PaginatedMenu(PlayerMenuUtility playerMenuUtility) {
		super(playerMenuUtility);
	}

	public abstract List<?> getData();

	public abstract void loopCode(Object object);

	public abstract HashMap<Integer, ItemStack> getCustomMenuBorderItems();

	/**
	 * Add Menu Border with buttons and outer filler glass
	 */
	public void addMenuBorder(){
		inventory.setItem(48, Utils.makeItem(Material.DARK_OAK_BUTTON, ChatColor.GREEN + "Left", "&8Click to go left."));
		inventory.setItem(49, Utils.makeItem(Material.BARRIER, ChatColor.DARK_RED + "Close", "&8Click to close menu."));
		inventory.setItem(50, Utils.makeItem(Material.DARK_OAK_BUTTON, ChatColor.GREEN + "Right", "&8Click to go right."));
		setOuterFillerGlass();
		if (getCustomMenuBorderItems() != null){
			getCustomMenuBorderItems().forEach(((integer, itemStack) -> inventory.setItem(integer, itemStack)));
		}
	}

	/**
	 * Add Menu Border with buttons and outer filler glass
	 * @param close Close button slot
	 * @param left Left button slot
	 * @param right Right button slot
	 */
	public void addMenuBorder(int close, int left, int right){
		inventory.setItem(left, Utils.makeItem(Material.DARK_OAK_BUTTON, ChatColor.GREEN + "Left", "&8Click to go left."));
		inventory.setItem(close, Utils.makeItem(Material.BARRIER, ChatColor.DARK_RED + "Close", "&8Click to close menu."));
		inventory.setItem(right, Utils.makeItem(Material.DARK_OAK_BUTTON, ChatColor.GREEN + "Right", "&8Click to go right."));
		setOuterFillerGlass();

		if (getCustomMenuBorderItems() != null){
			getCustomMenuBorderItems().forEach(((integer, itemStack) -> inventory.setItem(integer, itemStack)));
		}
	}

	/**
	 * @return int Max Items per page for this menu
	 */
	public int getMaxItemsPerPage() {
		return maxItemsPerPage;
	}

	/**
	 * handle default menu buttons
	 * @param e InventoryClickEvent
	 * @param arrayListSize size of array to loop through
	 */
	public void handleMenuButtons(InventoryClickEvent e, int arrayListSize) {
		Material material = e.getCurrentItem().getType();
		if (material.equals(Material.GRAY_STAINED_GLASS_PANE)){return;}
		if (material.equals(Material.BARRIER) && e.getSlot() == 49) {
			close();
		} else if (material.equals(Material.DARK_OAK_BUTTON)) {
			if (e.getSlot() == 48) {
				if (page != 0) {
					page = page - 1;
					super.silentOpen();
				}
			} else if (e.getSlot() == 50) {
				if (!((index + 1) >= arrayListSize)) {
					page = page + 1;
					super.silentOpen();
				}
			}
		}
	}

	/**
	 * Clear item section
	 */
	public void clearItemSection(){
		for (int i : itemIntList){
			if (inventory.getItem(i) != null){
				inventory.setItem(i, null);
			}
		}
	}

	/**
	 * @return int[] returns indexes of item section
	 */
	private int[] getItemIntList(){
		int[] tmp = new int[(7 * inBetweenRows)];
		int x = 0;
		for (int i = 0; i < inBetweenRows; i++) {
			int index = 9*(i+1);
			for (int j = 0; j < 7; j++) {
				tmp[x] = (index+j+1);
				x++;
			}
		}
		return tmp;
	}

	/**
	 * Get if click was in item section
	 * @param e InventoryClickEvent
	 * @return boolean returns if click was in item section
	 */
	public boolean clickInItemSection(InventoryClickEvent e){
		for (int i : itemIntList){
			if (e.getSlot() == i){
				return true;
			}
		}
		return false;
	}

	/**
	 * get Clicked index in item section
	 * @param e InventoryClickEvent
	 * @return int index of clicked index in item section
	 */
	public int getClickInItemSectionIndex(InventoryClickEvent e){
		if (e.getSlot() >= 10 && e.getSlot() < 17){
			return (e.getSlot()-10);
		} else if (e.getSlot() >= 19 && e.getSlot() < 26){
			return (e.getSlot()-12);
		} else if (e.getSlot() >= 28 && e.getSlot() < 35){
			return (e.getSlot()-14);
		} else {
			return (e.getSlot()-16);
		}
	}

	/**
	 * Handle menu buttons with custom menu buttons
	 * @param e InventoryClickEvent
	 * @param closeSlot close button slot
	 * @param leftButton left button slot
	 * @param rightButton right button slot
	 */
	public void handleMenuButtons(InventoryClickEvent e, int closeSlot, int leftButton, int rightButton) {
		Material material = e.getCurrentItem().getType();
		if (handleBorder(e)){return;}
		if (material.equals(Material.BARRIER) && e.getSlot() == closeSlot) {
			close();
		} else if (material.equals(Material.DARK_OAK_BUTTON)) {
			if (e.getSlot() == leftButton) {
				if (page != 0) {
					page = page - 1;
					super.silentOpen();
				}
			} else if (e.getSlot() == rightButton) {
				if (!((index + 1) >= getData().size())) {
					page = page + 1;
					super.silentOpen();
				}
			}
		}
	}

	@Override
	public void setMenuItems() {
		addMenuBorder();

		List<Object> data = (List<Object>) getData();

		if (data != null && !data.isEmpty()) {
			for (int i = 0; i < getMaxItemsPerPage(); i++) {
				index = getMaxItemsPerPage() * page + i;
				if (index >= data.size()) break;
				if (data.get(index) != null){
					loopCode(data.get(index));
				}
			}
		}
	}

	public boolean prevPage(){
		if (page == 0){
			return false;
		} else {
			page = page - 1;
			reloadItems();
			return true;
		}
	}

	public boolean nextPage(){
		if (!((index+1) >= getData().size())) {
			page = page + 1;
			reloadItems();
			return true;
		} else {
			return false;
		}
	}
}
