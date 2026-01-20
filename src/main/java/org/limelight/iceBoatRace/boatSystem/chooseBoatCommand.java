package org.limelight.iceBoatRace.boatSystem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static org.limelight.iceBoatRace.IceBoatRace.plugin;

public class chooseBoatCommand implements Listener, CommandExecutor {
    private final NamespacedKey boatTypeKey = new NamespacedKey(plugin,"boatType");

    static ItemStack[] contents = new ItemStack[]{
            ItemStack.of(Material.DARK_OAK_BOAT),
            ItemStack.of(Material.SPRUCE_BOAT),
            ItemStack.of(Material.OAK_BOAT),
            ItemStack.of(Material.ACACIA_BOAT),
            ItemStack.of(Material.BIRCH_BOAT),
            ItemStack.of(Material.PALE_OAK_BOAT),
            ItemStack.of(Material.MANGROVE_BOAT),
            ItemStack.of(Material.CHERRY_BOAT)
    };


    public static Inventory getBoatInventory(Player player){
        Inventory inventory = Bukkit.createInventory(player, 9, "Choose Your Boat");
        inventory.setContents(contents);
        return inventory;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if (!event.getView().getTitle().equals("Choose Your Boat")) return;

        String boatType = BoatHandler.getBoatName(event.getCursor().getType());
        if (boatType == null) return;

        event.getWhoClicked().getPersistentDataContainer().set(boatTypeKey, PersistentDataType.STRING,boatType);
    }

    public static void startChoose(){
        for (Player player: Bukkit.getOnlinePlayers()){
            if (player.isOp() ) continue;
            player.openInventory(getBoatInventory(player));
        }
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return true;
    }
}
