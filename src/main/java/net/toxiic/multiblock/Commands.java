package net.toxiic.multiblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.toxiic.multiblock.structure.Structure;

public class Commands implements CommandExecutor {

	String str_notEnoughArgs = ChatColor.RED + "Not enough arguments.";
	String str_structureNotFound = ChatColor.RED + "Structure not found.";
	String str_toManyArgs = ChatColor.RED + "To many arguments.";
	String str_structureLoaded = ChatColor.AQUA + "Structure '%s' loaded.";
	String str_structureNotLoaded = ChatColor.AQUA + "Structure '%s' wasn't loaded.";
	String str_structureUnloaded = ChatColor.AQUA + "Structure '%s' unloaded.";
	String str_structureAlreadyLoaded = ChatColor.AQUA + "Structure '%s' is already loaded.";
	String str_playerNotFound = ChatColor.RED + "Player " + ChatColor.GOLD + "%p" + ChatColor.RED + " not found.";
	String str_structureDeleted = ChatColor.AQUA + "Structure '" + ChatColor.GOLD + "%s" + ChatColor.AQUA
			+ "' was deleted.";

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player && sender.isOp()) {
			Player player = (Player) sender;
			if (args.length == 0) {

			} else {
				switch (args[0].toLowerCase()) {
				case "help":
					player.sendMessage(ChatColor.AQUA + "/mb give [structure] <player> " + ChatColor.YELLOW
							+ " Gives the player a block that will generate a structure.");
					player.sendMessage(ChatColor.AQUA + "/mb register [name] " + ChatColor.YELLOW
							+ " Registers the selected WorldEdit Region as a multiblock structure.");
					player.sendMessage(ChatColor.AQUA + "/mb place [structure] " + ChatColor.YELLOW
							+ " Placed a Structure where you are looking,");
					player.sendMessage(
							ChatColor.AQUA + "/mb list " + ChatColor.YELLOW + " Lists all registered Structures.");
					player.sendMessage(ChatColor.AQUA + "/mb load [structure] " + ChatColor.YELLOW
							+ " Loads a Structure, allowing it to be interacted with.");
					player.sendMessage(ChatColor.AQUA + "/mb unload [structure] " + ChatColor.YELLOW
							+ " Unloads a Structure. Unloaded structures cannot be interacted with.");
					player.sendMessage(ChatColor.AQUA + "/mb info [structure] " + ChatColor.YELLOW
							+ " Gives into on a specific structure.");
					player.sendMessage(ChatColor.AQUA + "/mb delete [structure] " + ChatColor.YELLOW
							+ " Removes a structure from the server.");
					return true;
				case "give":
					if (args.length == 1) {
						player.sendMessage(str_notEnoughArgs);
						return true;
					} else if (args.length >= 2) {
						String name = args[1];
						Structure s = Main.mm.getStructureByName(name);
						NBTItem spawner = new NBTItem(new ItemStack(Material.REDSTONE_BLOCK));
						spawner.setString("multiblock_spawner", name);
						ItemStack item = spawner.getItem();
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName(ChatColor.GOLD + "Structure Spawner");
						List<String> lore = new ArrayList<String>();
						lore.add(ChatColor.RED + name);
						lore.add(ChatColor.AQUA + "Length : " + s.getSize().getX());
						lore.add(ChatColor.AQUA + "Width : " + s.getSize().getZ());
						lore.add(ChatColor.AQUA + "Height : " + s.getSize().getY());
						meta.setLore(lore);
						meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, false);
						meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
						meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
						item.setItemMeta(meta);
						if (args.length == 2) {
							player.getInventory().addItem(item);
							return true;
						} else {
							Player p = Bukkit.getPlayer(args[2]);
							if (p != null) {
								p.getInventory().addItem(item);
								return true;
							} else {
								player.sendMessage(str_playerNotFound.replace("%p", args[2]));
								return true;
							}
						}
					} else {
						player.sendMessage(str_toManyArgs);
						return true;
					}
				case "register":
					if (args.length == 1) {
						player.sendMessage(str_notEnoughArgs);
						break;
					} else if (args.length == 2) {
						try {
							String name = args[1];
							Region sel = null;
							try {
								sel = Main.worldEditPlugin.getSession(player)
										.getSelection(Main.worldEditPlugin.getSession(player).getSelectionWorld());
							} catch (IncompleteRegionException e) {
								player.sendMessage(
										ChatColor.RED + "You must select the structure with a WorldEdit Region.");
								e.printStackTrace();
							}
							if (sel != null) {
								BlockVector3 min = sel.getMinimumPoint();
								BlockVector3 max = sel.getMaximumPoint();
								HashMap<Location, BlockData> mb = new HashMap<Location, BlockData>();
								for (int x = min.getBlockX(); x <= max.getBlockX(); x = x + 1) {
									for (int y = min.getBlockY(); y <= max.getBlockY(); y = y + 1) {
										for (int z = min.getBlockZ(); z <= max.getBlockZ(); z = z + 1) {
											Location loc = new Location(player.getWorld(), x, y, z);
											mb.put(new Location(null, x - min.getBlockX(), y - min.getBlockY(),
													z - min.getBlockZ()), loc.getBlock().getBlockData());
										}
									}
								}
								new Structure(name, mb);
								player.sendMessage(ChatColor.AQUA + "Multiblock '" + name + "' Registered");
								return true;
							} else {
								player.sendMessage(ChatColor.RED + "invalid region");
								return true;
							}
						} catch (NullPointerException err) {
							player.sendMessage(str_structureNotFound.replace("%s", args[1]));
							return true;
						}
					} else {
						player.sendMessage(str_toManyArgs);
						return true;
					}
				case "place":
					if (args.length == 1) {
						player.sendMessage(str_notEnoughArgs);
						return true;
					}
					if (args.length == 2) {
						String name = args[1];
						if (Main.mm.getStructureNames().contains(name)) {
							Structure s = Main.mm.getStructureByName(name);
							Main.mm.placeStructure(s, player.getTargetBlockExact(9).getLocation());
							player.sendMessage(ChatColor.AQUA + "Multiblock '" + s.getName() + "' placed.");
							return true;
						} else {
							player.sendMessage(str_structureNotFound.replace("%s", args[1]));
							return true;
						}
					} else {
						player.sendMessage(str_toManyArgs);
						return true;
					}
				case "list":
					player.sendMessage(ChatColor.GOLD + "Multiblock Structures.");
					player.sendMessage(
							ChatColor.AQUA + String.valueOf(Main.mm.getStructures().size()) + " structures detected.");
					for (Structure s : Main.mm.getStructures()) {
						if (s.isLoaded()) {
							player.sendMessage(
									ChatColor.GREEN + s.getName() + " | " + s.getBlocks().size() + " blocks");
							return true;
						} else {
							player.sendMessage(ChatColor.RED + s.getName() + " | " + s.getBlocks().size() + " blocks");
							return true;
						}
					}
					return true;
				case "inventory":
					if (args.length == 1) {
						player.sendMessage(str_notEnoughArgs);
						return true;
					} else {
						switch (args[1].toLowerCase()) {
						case "edit":
							if (args.length == 2) {
								player.sendMessage(str_notEnoughArgs);
								return true;
							} else {
								String name = args[3].toLowerCase();
								Structure s = Main.mm.getStructureByName(name);
								return true;
							}
						}
					}
				case "load":
					if (args.length == 1) {
						player.sendMessage(str_notEnoughArgs);
						return true;
					} else if (args.length == 2) {
						player.sendMessage(ChatColor.GOLD + "Attempting to load Structure '" + args[1] + "'.");
						try {
							Structure s = Main.mm.getStructureByName(args[1]);
							if (s.isLoaded()) {
								player.sendMessage(
										ChatColor.AQUA + "Multiblock '" + s.getName() + "' is already loaded.");
								return true;
							} else {
								Main.mm.loadStructure(s);
								player.sendMessage(ChatColor.AQUA + "Multiblock '" + s.getName() + "' loaded.");
								return true;
							}
						} catch (NullPointerException err) {
							player.sendMessage(str_structureNotFound.replace("%s", args[1]));
							return true;
						}
					} else {
						player.sendMessage(str_toManyArgs);
						return true;
					}
				case "unload":
					if (args.length == 1) {
						player.sendMessage(str_notEnoughArgs);
						return true;
					} else if (args.length == 2) {
						player.sendMessage(ChatColor.GOLD + "Attempting to unload Structure '" + args[1] + "'.");
						try {
							Structure s = Main.mm.getStructureByName(args[1]);
							if (!s.isLoaded()) {
								player.sendMessage(ChatColor.AQUA + "Multiblock '" + s.getName() + "' wasn't loaded.");
								return true;
							} else {
								Main.mm.unloadStructure(s);
								player.sendMessage(ChatColor.AQUA + "Multiblock '" + s.getName() + "' unloaded.");
								return true;
							}
						} catch (NullPointerException err) {
							player.sendMessage(str_structureNotFound.replace("%s", args[1]));
							return true;
						}
					} else {
						player.sendMessage(str_toManyArgs);
						return true;
					}
				case "info":
				case "i":
					if (args.length == 1) {
						player.sendMessage(str_notEnoughArgs);
						return true;
					} else if (args.length == 2) {
						try {
							Structure s = Main.mm.getStructureByName(args[2]);
							if (s != null) {
								Location size = s.getSize();
								player.sendMessage("Name      : " + s.getName());
								player.sendMessage("Blocks    : " + s.getBlocks().size());
								player.sendMessage("Size      : " + size.getBlockX() + "x " + size.getBlockY() + "y "
										+ size.getBlockZ() + "z ");
								return true;
							}
						} catch (Exception err) {
							player.sendMessage(str_structureNotFound.replace("%s", args[1]));
							return true;
						}
					} else {
						player.sendMessage(str_toManyArgs);
						return true;
					}
					break;
				case "delete":
					if (args.length == 1) {
						player.sendMessage(str_notEnoughArgs);
						return true;
					} else if (args.length == 2) {
						if (Main.mm.getStructureNames().contains(args[1])) {
							Main.mm.getStructureByName(args[1]).delete();
							player.sendMessage(str_structureDeleted.replace("%s", args[1]));
							return true;
						} else {
							player.sendMessage(str_structureNotFound);
							return true;
						}
					} else {
						player.sendMessage(str_toManyArgs);
						return true;
					}
				case "reload":
					Main.plugin.saveConfig();
					Main.plugin.saveDefaultConfig();
					Main.plugin.reloadConfig();
					player.sendMessage("Config reloaded.");
					return true;
				default:
					player.sendMessage(ChatColor.RED + "That is not a valid option!");
					return true;
				}
			}
		}
		return true;
	}
}
