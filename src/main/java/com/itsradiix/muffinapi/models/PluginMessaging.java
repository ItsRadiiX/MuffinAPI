package com.itsradiix.muffinapi.models;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.itsradiix.muffinapi.MuffinAPI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * PluginMessaging Utility class which is used to send pluginMessages to BungeeCord instance
 */
public class PluginMessaging {

	// Important variables
	public static JavaPlugin plugin = MuffinAPI.getPlugin();

	/**
	 * Connect a Player to a Server.
	 * @param p Player to be connected to a Server.
	 * @param server Name of Server to be connected with.
	 */
	public static void connectPlayerToServer(Player p, String server){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(server);
		p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
	}

	/**
	 * Connect another Player to a Server.
	 * @param p player issuing the connectOther.
	 * @param otherPlayer Name of Player to be connected to a server.
	 * @param server Name of Server to be connected with.
	 */
	public static void connectOtherPlayerToServer(Player p, String otherPlayer, String server){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("ConnectOther");
		out.writeUTF(otherPlayer);
		out.writeUTF(server);
		p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
	}

	/**
	 * Send message to a player.
	 * @param p player issuing the sendMessage.
	 * @param toPlayer Name of the Player to send a message to.
	 * @param message message to be send.
	 */
	public static void sendMessage(Player p, String toPlayer, String message){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Message");
		out.writeUTF(toPlayer);
		out.writeUTF(message);
		p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
	}

	/**
	 * Send Raw Message to a player
	 *
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 * IMPORTANT NOTICE: message must be in json format.
	 * Helpful link for creating such message: https://minecraft.tools/en/tellraw.php
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 *
	 * @param p player issuing the sendMessage.
	 * @param toPlayer Name of the Player to send a message to.
	 * @param rawMessage rawMessage to be send.
	 */
	public static void sendMessageRaw(Player p, String toPlayer, String rawMessage){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("MessageRaw");
		out.writeUTF(toPlayer);
		out.writeUTF(rawMessage);
		p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
	}

	/**
	 * Kick a player from the network
	 * @param p player issuing the kickPlayer.
	 * @param playerToKick name of the player to be kicked from network.
	 * @param reason message to be displayed as reason for the kick
	 */
	public static void kickPlayer(Player p, String playerToKick, String reason){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("KickPlayer");
		out.writeUTF(playerToKick);
		out.writeUTF(reason);
		p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
	}

	/**
	 * Forward custom Data to specific player.
	 * @param p player issuing the ForwardToPlayer.
	 * @param playerToForwardTo name of the Player to be forwarded to.
	 * @param channel name of the custom data channel.
	 * @param data Data to be send. TIP: GSON serialization / deserialization could be useful if needed.
	 */
	public static void forwardToPlayer(Player p, String playerToForwardTo, String channel, String data){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("ForwardToPlayer");
		out.writeUTF(playerToForwardTo);
		writeForwardData(p, channel, data, out);
	}

	/**
	 * Forward custom data to player
	 * @param p player issuing the Forward.This player will also receive the custom data.
	 * @param channel name of the custom data channel.
	 * @param data Data to be send. TIP: GSON serialization / deserialization could be useful if needed.
	 */
	public static void forward(Player p, String channel, String data){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Forward");
		writeForwardData(p, channel, data, out);
	}

	/**
	 * Method to write Forward data
	 * This method is private as it is used for generic purposes
	 * @param p player issuing the Forward.
	 * @param channel name of the custom data channel.
	 * @param data Data to be send. TIP: GSON serialization / deserialization could be useful if needed.
	 * @param out ByteArrayDataOutput where the custom data will be set into and read from.
	 */
	private static void writeForwardData(Player p, String channel, String data, ByteArrayDataOutput out) {
		out.writeUTF(channel);

		ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
		DataOutputStream msgOut = new DataOutputStream(msgBytes);
		try {
			msgOut.writeUTF(data);
			msgOut.writeShort(123);
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.writeShort(msgBytes.toByteArray().length);
		out.write(msgBytes.toByteArray());

		p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
	}
}
