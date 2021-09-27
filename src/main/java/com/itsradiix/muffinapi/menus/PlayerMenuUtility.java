package com.itsradiix.muffinapi.menus;

import org.bukkit.entity.Player;

import java.awt.*;
import java.util.HashMap;
import java.util.Stack;

/**
 * PlayerMenuUtility class that stores data about a Player to be used in Menu
 */
public class PlayerMenuUtility {

	private final Player owner;
	private final HashMap<String, Object> dataMap = new HashMap<>();
	private final Stack<Menu> history = new Stack<>();

	public PlayerMenuUtility(Player p){
		this.owner = p;
	}

	public Player getOwner() {
		return owner;
	}

	public void setData(String identifier, Object data){
		dataMap.put(identifier, data);
	}

	public void setData(Enum identifier, Object data){
		dataMap.put(identifier.toString(), data);
	}

	public Object getData(String identifier){
		return dataMap.get(identifier);
	}

	public Object getData(Enum identifier){
		return dataMap.get(identifier.toString());
	}

	public <T> T getData(String identifier, Class<T> classRef){
		Object obj = dataMap.get(identifier);
		return (obj == null) ? null : classRef.cast(obj);
	}

	public <T> T getData(Enum identifier, Class<T> classRef){
		Object obj = dataMap.get(identifier.toString());
		return (obj == null) ? null : classRef.cast(obj);
	}

	public Menu lastMenu(){
		history.pop();
		return history.pop();
	}

	public void pushMenu(Menu menu){
		history.push(menu);
	}
}
