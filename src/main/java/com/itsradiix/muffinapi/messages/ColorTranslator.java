package com.itsradiix.muffinapi.messages;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class ColorTranslator {

	private static final Pattern HEX_PATTERN = Pattern.compile("(&#[0-9a-fA-F]{6})");

	/**
	 * @param text The string of text to apply color/effects to
	 * @return Returns a string of text with color/effects applied
	 */
	public static String translateColorCodes(@NotNull String text){
		String hexColored = HEX_PATTERN.matcher(text).replaceAll(match -> "" + ChatColor.of(match.group(1)));
		return ChatColor.translateAlternateColorCodes('&', hexColored);
	}

	/**
	 * @param text The text with color codes that you want to turn into a TextComponent
	 * @return the TextComponent with hex colors and regular colors
	 */
	public static TextComponent translateColorCodesToTextComponent(@NotNull String text){
		// This is done solely to ensure hex color codes are in the format
		// fromLegacyText expects:
		// &#FF0000 -> &x&f&f&0&0&0&0
		String colored = translateColorCodes(text);

		TextComponent base = new TextComponent();
		BaseComponent[] converted = TextComponent.fromLegacyText(colored);

		for(BaseComponent comp : converted) {
			base.addExtra(comp);
		}

		return base;
	}

}
