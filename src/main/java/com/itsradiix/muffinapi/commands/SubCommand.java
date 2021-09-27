package com.itsradiix.muffinapi.commands;

import com.itsradiix.muffinapi.messages.ColorTranslator;
import com.itsradiix.muffinapi.messages.Messages;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * SubCommand class for creating Spigot SubCommands.
 * To create a new SubCommand, use the Builder class.
 */
public class SubCommand {

	protected static final Messages messages = Messages.getInstance();

	// Important Command variables
	protected Command baseCommand;
	protected String name;
	protected String description;
	protected String syntax;
	protected String permission;
	protected boolean allowConsole;
	protected boolean hideConsoleUsage;
	protected boolean hideTabComplete;
	protected BiConsumer<CommandSender, String[]> perform;
	protected BiFunction<CommandSender, String[], List<String>> tabListArgs;

	/**
	 * Default SubCommand Constructor
	 * @param name SubCommand name
	 * @param description SubCommand description
	 * @param syntax SubCommand syntax
	 * @param permission SubCommand permission
	 * @param allowConsole Is Console allowed to use SubCommand
	 * @param hideConsoleUsage Hide SubCommand in Console
	 * @param perform SubCommand perform runnable
	 * @param tabListArgs SubCommand tabListArgs runnable
	 */
	public SubCommand(String name, String description, String syntax, String permission, boolean allowConsole, boolean hideConsoleUsage, boolean hideTabComplete, BiConsumer<CommandSender, String[]> perform, BiFunction<CommandSender, String[], List<String>> tabListArgs){
		this.name = name;
		this.description = description;
		this.syntax = syntax;
		this.permission = permission;
		this.allowConsole = allowConsole;
		this.hideConsoleUsage = hideConsoleUsage;
		this.hideTabComplete = hideTabComplete;
		this.perform = perform;
		this.tabListArgs = tabListArgs;
	}

	/**
	 * Set Base Command of SubCommand
	 * @param baseCommand
	 */
	public void setBaseCommand(Command baseCommand) {
		this.baseCommand = baseCommand;
	}

	/**
	 * @return Command returns base command
	 */
	public Command getBaseCommand() {
		return baseCommand;
	}

	/**
	 * @return String returns SubCommand name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return String returns SubCommand description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return String returns SubCommand syntax
	 */
	public String getSyntax() {
		return syntax;
	}

	/**
	 * @return String returns SubCommand permission
	 */
	public String getPermission() {
		return permission;
	}

	/**
	 * @return boolean returns if Console is allowed to use SubCommand
	 */
	public boolean isAllowConsole() {
		return allowConsole;
	}

	/**
	 * @return boolean returns if SubCommand is hidden from console
	 */
	public boolean isHideConsoleUsage() {
		return hideConsoleUsage;
	}

	/**
	 * @return boolean returns if SubCommand is hidden from Tab Complete
	 */
	public boolean isHideTabComplete() {
		return hideTabComplete;
	}

	/**
	 * Perform the command runnable
	 * @param sender CommandSender of issued command.
	 * @param args Arguments of issued command.
	 */
	public void perform(CommandSender sender, String[] args){
		perform.accept(sender, args);
	}

	/**
	 * Get tab list args and filter them based on args input
	 * @param args Arguments of issued command.
	 * @return String[] returns Array of arguments.
	 */
	public List<String> getTabListArgs(CommandSender sender, String[] args){
		List<String> tmp = new ArrayList<>();
		List<String> tabArgs = tabListArgs.apply(sender, args);
		for (String s : tabArgs){
			if (s.toLowerCase().startsWith(args[(args.length-1)].toLowerCase())){
				tmp.add(s);
			}
		}
		return tmp;
	}

	public static class Builder {

		protected String name = "";
		protected String description = "";
		protected String syntax = "/" + name;
		protected String permission = "";
		protected boolean allowConsole = false;
		protected boolean hideConsoleUsage = false;
		protected boolean hideTabComplete = false;
		protected BiConsumer<CommandSender, String[]> perform = (sender, args) -> {};
		protected BiFunction<CommandSender, String[], List<String>> tabListArgs = (sender, args) -> new ArrayList<>();

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}

		public Builder syntax(String syntax) {
			this.syntax = syntax;
			return this;
		}

		public Builder permission(String permission) {
			this.permission = permission;
			return this;
		}

		public Builder allowConsole(){
			this.allowConsole = true;
			return this;
		}

		public Builder hideConsoleUsage(){
			this.hideConsoleUsage = true;
			return this;
		}

		public Builder hideTabComplete(){
			this.hideTabComplete = true;
			return this;
		}

		public Builder perform(BiConsumer<CommandSender, String[]> perform){
			this.perform = perform;
			return this;
		}

		public Builder tabListArguments(BiFunction<CommandSender, String[], List<String>> tabListArgs){
			this.tabListArgs = tabListArgs;
			return this;
		}

		public SubCommand build(){
			return new SubCommand(name, description, syntax, permission, allowConsole, hideConsoleUsage, hideTabComplete, perform, tabListArgs);
		}

		public SubCommand buildHelpCommand(Command baseCommand){
			return new SubCommand(
					"help",
					"Displays this help message",
					"/" + baseCommand.name + " help",
					baseCommand.permission + ".help",
					true,
					false,
					false,
					((sender, args) -> {
						sender.sendMessage(ColorTranslator.translateColorCodes(messages.getMessage("helpHeader", "&b()&3=-=&b[ &6%command_name% &b]&3=-=&b()").replaceAll("%command_name%", baseCommand.name)));

						List<SubCommand> subCommands = baseCommand.subCommands;
						sender.sendMessage(ColorTranslator.translateColorCodes("&b" + baseCommand.syntax + " &7-&r " + baseCommand.description));
						for (SubCommand subCommand : subCommands) {
							if (sender.hasPermission(subCommand.getPermission())) {
								sender.sendMessage(ColorTranslator.translateColorCodes("&b" +
										subCommand.getSyntax() +
										" &7-&r " +
										subCommand.getDescription()));
							}
						}

						sender.sendMessage(ColorTranslator.translateColorCodes(messages.getMessage("helpFooter", "&b()&3=-=-=-=-=-=-=&b()") + "\n"));
						if (sender instanceof Player player){
							player.playSound(player.getLocation(), Sound.ENTITY_EGG_THROW, 0.2F, 1.0F);
						}
					}),
					((sender, args) -> new ArrayList<>()));
		}

	}
}
