package net.toxiic.multiblock;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import net.toxiic.multiblock.structure.Structure;

public class CommandTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

		String arguments = "";
		for (int i = 0; i < args.length; i++) {
			if (arguments == "") {
				arguments = args[i];
			} else {
				arguments = arguments + " " + args[i];
			}
		}
		List<String> result = new ArrayList<String>();
		if (args.length == 1) {
			List<String> suggestions = new ArrayList<String>();
			suggestions.add("give");
			suggestions.add("register");
			suggestions.add("delete");
			suggestions.add("unload");
			suggestions.add("load");
			suggestions.add("place");
			suggestions.add("list");
			suggestions.add("reload");
			suggestions.add("info");
			for (String sug : suggestions) {
				if (sug.startsWith(args[args.length - 1].toLowerCase())) {
					result.add(sug);
				}
			}
		} else {
			switch (args[0]) {
			case "give":
				if (args.length == 2) {
					List<String> suggestions = new ArrayList<String>();
					for (Structure s : Main.mm.getStructures()) {
						if (s.isLoaded()) {
							suggestions.add(s.getName().toLowerCase());
						}
					}
					for (String sug : suggestions) {
						if (sug.startsWith(args[args.length - 1].toLowerCase())) {
							result.add(sug);
						}
					}
					return result;
				} else if (args.length == 3) {
					List<String> suggestions = new ArrayList<String>();
					for (Player pl : Bukkit.getOnlinePlayers()) {
						suggestions.add(pl.getName());
					}
					for (String sug : suggestions) {
						if (sug.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
							result.add(sug);
						}
					}
					return result;
				}
				break;
			case "delete":
			case "place":
				if (args.length >= 2) {
					List<String> suggestions = new ArrayList<String>();
					for (Structure s : Main.mm.getStructures()) {
						if (s.isLoaded()) {
							suggestions.add(s.getName().toLowerCase());
						}
					}
					for (String sug : suggestions) {
						if (sug.startsWith(args[args.length - 1].toLowerCase())) {
							result.add(sug);
						}
					}
					return result;
				}
				break;
			case "register":

				break;
			case "unload":
				if (args.length >= 2) {
					List<String> suggestions = new ArrayList<String>();
					for (Structure s : Main.mm.getStructures()) {
						if (s.isLoaded()) {
							suggestions.add(s.getName().toLowerCase());
						}
					}
					for (String sug : suggestions) {
						if (sug.startsWith(args[args.length - 1].toLowerCase())) {
							result.add(sug);
						}
					}
					return result;
				}
				break;
			case "load":
				if (args.length >= 2) {
					List<String> suggestions = new ArrayList<String>();
					for (Structure s : Main.mm.getStructures()) {
						if (!s.isLoaded()) {
							suggestions.add(s.getName().toLowerCase());
						}
					}
					for (String sug : suggestions) {
						if (sug.startsWith(args[args.length - 1].toLowerCase())) {
							result.add(sug);
						}
					}
					return result;
				}
				break;
			case "info":
				if (args.length >= 2) {
					List<String> suggestions = new ArrayList<String>();
					for (Structure s : Main.mm.getStructures()) {
						suggestions.add(s.getName().toLowerCase());
					}
					for (String sug : suggestions) {
						if (sug.startsWith(args[args.length - 1].toLowerCase())) {
							result.add(sug);
						}
					}
					return result;
				}
				break;
			case "list":

				break;
			case "reload":

				break;
			}
		}
		return result;
	}
}
