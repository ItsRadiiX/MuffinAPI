package com.itsradiix.muffinapi.models;

import com.itsradiix.muffinapi.messages.ColorTranslator;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

	/**
	 * Play Sound at location of Command Sender.
	 * Method will check if Command Sender is instance of player.
	 * @param sender CommandSender sender of the command.
	 * @param sound Sound sound to be played.
	 */
	public static void playSound(CommandSender sender, Sound sound){
		if (sender instanceof Player p){
			p.playSound(p.getLocation(), sound, 0.3F, 1.0F);
		}
	}

	/**
	 * Play Sound at location of Player.
	 * @param p Player player to play sound to.
	 * @param sound Sound sound to be played.
	 */
	public static void playSound(Player p, Sound sound){
		p.playSound(p.getLocation(), sound, 0.3F, 1.0F);
	}

	/**
	 * Create an ItemStack based on a Material, display name and lore.
	 * @param material Material to be used.
	 * @param displayName Name of the item.
	 * @param lore Lore of the item.
	 * @return ItemStack returns the created ItemStack.
	 */
	public static ItemStack makeItem(Material material, String displayName, String... lore) {
		ItemStack item = new ItemStack(material);
		return getItemStack(item, displayName, lore);
	}

	/**
	 * Method to set display name and lore to itemStack.
	 * This method is used internally and cannot be used outside of this class.
	 * @param itemStack ItemStack where data is to be applied on
	 * @param displayName Name of the item.
	 * @param lore Lore of the item.
	 * @return Item returns the created ItemStack with all data applied on.
	 */
	@NotNull
	private static ItemStack getItemStack(ItemStack itemStack, String displayName, String[] lore) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.displayName(Component.text(ColorTranslator.translateColorCodes(displayName)));
		List<String> tmpLore = Arrays.asList(lore);

		for (int i = 0; i < tmpLore.size(); i++) {
			tmpLore.set(i, ColorTranslator.translateColorCodes(tmpLore.get(i)));
		}

		itemMeta.setLore(tmpLore);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	public static List<String> returnPlayerList(CommandSender sender, String[] args){
		List<String> subArgs = new ArrayList<>();
		if (args.length == 1){
			Bukkit.getOnlinePlayers().forEach(player -> subArgs.add(player.getName()));
		}
		subArgs.remove(sender.getName());
		return subArgs;
	}

}
