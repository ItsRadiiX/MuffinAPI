package com.itsradiix.muffinapi.models;

import com.itsradiix.muffinapi.MuffinAPI;
import com.itsradiix.muffinapi.messages.ColorTranslator;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YAMLFileManager {

	protected static JavaPlugin core = MuffinAPI.getPlugin();
	protected static List<YAMLFileManager> autoReloadingFiles = new ArrayList<>();
	protected static int autoReloadTime = 10;

	protected File file;
	protected File folder;
	protected String folderString;
	protected long fileDateModified;
	protected FileConfiguration fileConfiguration;
	protected FileConfiguration defaultFileConfiguration = null;
	protected String fileName;
	protected String fileNameExtension = ".yml";
	protected boolean isAutoReloading;
	protected boolean invalidLoad = false;
	protected boolean isDefaultResource;

	public YAMLFileManager(String folder, String fileName){
		new YAMLFileManager(folder, fileName, false, false);
	}

	public YAMLFileManager(String folder, String fileName, boolean isDefaultResource, boolean isAutoReloading){
		folderString = folder;
		this.folder = new File(core.getDataFolder() + "/" + folderString);
		this.fileName = fileName;
		this.isDefaultResource = isDefaultResource;
		this.isAutoReloading = isAutoReloading;
		fileNameExtension = fileName + fileNameExtension;
		file = new File(this.folder, fileNameExtension);
		setup();
	}

	protected void setup(){
		if (!folder.exists()){
			folder.mkdirs();
		}

		if (isDefaultResource){
			InputStream is = core.getResource(fileNameExtension);
			if (is != null){
				defaultFileConfiguration = YamlConfiguration.loadConfiguration(new InputStreamReader(is));
			}
		}

		if (file.exists()){
			try {
				if (loadYaml(file.getPath())){
					fileConfiguration = YamlConfiguration.loadConfiguration(file);
					invalidLoad = false;
					if (isDefaultResource){
						addDefaults(fileConfiguration);
					}
				}
			} catch (Exception e) {
				catchException();
			}
		} else {
			if (isDefaultResource){
				core.saveResource(fileNameExtension, true);
				file = new File(folder, fileNameExtension);
			}
			fileConfiguration = YamlConfiguration.loadConfiguration(file);
			save();
			logToConsole(fileNameExtension + " file has been created!");
		}
		fileDateModified = file.lastModified();
		if (isAutoReloading){
			autoReloadingFiles.add(this);
		}
	}

	public void startAutoReloading(){
		if (!isAutoReloading){
			isAutoReloading = true;
			autoReloadingFiles.add(this);
		}
	}

	public void stopAutoReloading(){
		if (isAutoReloading){
			isAutoReloading = false;
			autoReloadingFiles.remove(this);
		}
	}

	/**
	 * Try to parse / load the YAML file to check if there was an error
	 * @param Path path to the file that needs to be parsed
	 * @return returns true if no error was detected
	 * @throws Exception if there was an error with parsing the YAML
	 */
	protected static boolean loadYaml(String Path) throws Exception {
		Map map;
		Yaml yaml = new Yaml();
		InputStream stream = new FileInputStream(Path);
		map = yaml.load(stream);
		if (map == null) {
			throw new YAMLFileManagerException("Failed to read yaml file");
		}
		return true;
	}

	/**
	 * Add Default parameters if they dont exist in the currently loaded file.
	 * @param fileConfiguration this it the configuration where the default are added to.
	 */
	protected void addDefaults(FileConfiguration fileConfiguration){
		// Compare both YamlConfigs, set defaults and save
		fileConfiguration.setDefaults(defaultFileConfiguration);
		fileConfiguration.options().copyDefaults(true);
		save();
	}

	/**
	 * Save the file to disk
	 */
	public void save() {
		if (!invalidLoad) {
			try {
				fileConfiguration.save(file);
			} catch(IOException e){
				e.fillInStackTrace();
			}
		}
	}

	public static void setAutoReloading(int seconds){
		autoReloadTime = seconds;
		autoReload(autoReloadTime);
	}

	private static void autoReload(int seconds){
		Bukkit.getScheduler().runTaskTimerAsynchronously(core, () -> {
			for (YAMLFileManager fileManager : autoReloadingFiles){
				fileManager.reload();
			}
		}, 0, (seconds*20L));
	}

	public boolean reload(){
		File tmpFile = new File(folder, fileNameExtension);

		if (fileDateModified != tmpFile.lastModified()) {
			try {
				if (loadYaml(file.getPath())) {
					fileConfiguration = YamlConfiguration.loadConfiguration(tmpFile);
					file = tmpFile;
					fileDateModified = file.lastModified();
					invalidLoad = false;
					return true;
				}
			} catch (Exception e) {
				catchException();
			} finally {
				YAMLFileReloadEvent fre = new YAMLFileReloadEvent("Reloading");
				Bukkit.getScheduler().runTask(core, () -> Bukkit.getServer().getPluginManager().callEvent(fre));
			}
		}
		return false;
	}

	public void catchException(){
		if (!invalidLoad) {
			logToConsole("&cAn error occurred while trying to load " + fileNameExtension);
			logToConsole("&cThis is most likely a YAML Exception, there's a syntax error somewhere.");
			if (isDefaultResource) {
				Reader reader = new InputStreamReader(core.getResource(fileNameExtension));
				fileConfiguration = YamlConfiguration.loadConfiguration(reader);
				logToConsole("&cLoading default configuration...");
			}
		}
	}

	public String getString(String path){
		return fileConfiguration.getString(path);
	}

	public String getString(String path, String defaultValue){
		return fileConfiguration.getString(path, defaultValue);
	}

	// Simplified version of the getBoolean from fileConfiguration
	public boolean getBoolean(String path){
		return fileConfiguration.getBoolean(path);
	}

	// Simplified version of the getInt from fileConfiguration
	public int getInt(String path){
		return fileConfiguration.getInt(path);
	}

	public int getInt(String path, int defaultValue){
		return fileConfiguration.getInt(path, defaultValue);
	}

	// Simplified version of the getDouble from fileConfiguration
	public double getDouble(String path){
		return fileConfiguration.getDouble(path);
	}

	// Simplified version of the getLong from fileConfiguration
	public long getLong(String path){
		return fileConfiguration.getLong(path);
	}

	// Return fileName
	public String getFileName() {
		return fileName;
	}

	public File getFile() {
		return file;
	}

	// Return fileName with extension
	public String getFileNameExtension() {
		return fileNameExtension;
	}

	// Return FileConfiguration
	public FileConfiguration getFileConfiguration() {
		return fileConfiguration;
	}

	public List<String> getStringList(String path){
		return fileConfiguration.getStringList(path);
	}

	public List<Boolean> getBooleanList(String path){
		return fileConfiguration.getBooleanList(path);
	}

	public List<Double> getDoubleList(String path){
		return fileConfiguration.getDoubleList(path);
	}

	public List<Long> getLongList(String path){
		return fileConfiguration.getLongList(path);
	}

	public List<Short> getShortList(String path){
		return fileConfiguration.getShortList(path);
	}

	public List<Integer> getIntList(String path){
		return fileConfiguration.getIntegerList(path);
	}

	public List<Byte> getByteList(String path){
		return fileConfiguration.getByteList(path);
	}

	public List<Character> getCharacterList(String path){
		return fileConfiguration.getCharacterList(path);
	}

	public List<Float> getFloatList(String path){
		return fileConfiguration.getFloatList(path);
	}

	public List<?> getList(String path){
		return fileConfiguration.getList(path);
	}

	public Object[] getObjectArray(String path){
		return fileConfiguration.getList(path).toArray();
	}

	public static void logToConsole(String message){
		Bukkit.getLogger().info(ColorTranslator.translateColorCodes(message));
	}

	public static class Builder {
		private String folder = "";
		private String fileName = "";
		private boolean defaultResource = false;
		private boolean isAutoReloading = false;

		public Builder fileName(String fileName) {
			this.fileName = fileName;
			return this;
		}

		public Builder folder(String folder){
			this.folder = folder;
			return this;
		}

		public Builder defaultResource(){
			defaultResource = true;
			return this;
		}

		public Builder autoReloads(){
			isAutoReloading = true;
			return this;
		}

		public YAMLFileManager build(){
			return new YAMLFileManager(folder, fileName, defaultResource, isAutoReloading);
		}
	}

	public static class YAMLFileManagerException extends Exception {
		public YAMLFileManagerException(String errorMessage){
			super(errorMessage);
		}
	}

	public static class YAMLFileReloadEvent extends Event {
		private static final HandlerList handlers = new HandlerList();
		private final String message;

		public YAMLFileReloadEvent(String example) {
			message = example;
		}

		public String getMessage() {
			return message;
		}

		public @NotNull
		HandlerList getHandlers() {
			return handlers;
		}

		public static HandlerList getHandlerList() {
			return handlers;
		}
	}

}
