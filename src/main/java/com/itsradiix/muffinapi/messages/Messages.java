package com.itsradiix.muffinapi.messages;

import com.itsradiix.muffinapi.MuffinAPI;
import com.itsradiix.muffinapi.models.YAMLFileManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Messages {

	private static Messages instance;

	private final JavaPlugin plugin = MuffinAPI.getPlugin();
	private final Logger logger = plugin.getLogger();

	private final Map<String, String> messages = new HashMap<>();

	private YAMLFileManager messagesFile;

	public Messages(){
		instance = this;
	}

	public void setupFromFile(String folder, String fileName, boolean isDefaultResource, boolean isAutoReloading){
		messagesFile = new YAMLFileManager(folder, fileName, isDefaultResource, isAutoReloading);
	}

	public void setMessage(String var, String msg) {
		messages.put(var, msg);
	}

	public String getMessage(String var){
		return ColorTranslator.translateColorCodes(messages.get(var));
	}

	public String getMessage(String var, String def){
		String message = messages.get(var);
		if (message == null || message.isBlank()){
			message = def;
		}
		return ColorTranslator.translateColorCodes(message);
	}

	public static Messages getInstance() {
		return instance;
	}

	public Map<String, String> getMessages() {
		return messages;
	}

	public YAMLFileManager getMessagesFile() {
		return messagesFile;
	}
}
