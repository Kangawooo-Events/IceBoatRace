package org.limelight.iceBoatRace.mapVoteSystem;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;


public class MapVoteCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp() || args.length == 0)
            return true;

        Player sendPlayer = (Player) sender;

        if (args[0].equals("start")) {
            sendPlayer.sendMessage(ChatColor.AQUA + "[ICEBOAT] Started Vote");
            voteStart();
        }

        return true;
    }

    private void voteStart() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            openVoteUI(player);
        }
    }

    private void openVoteUI(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 27, "Vote");

        String marioHeadTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjBhODUyZmI3ZGRjMWE3MzU5MzY4YWRlNjNmMThjMzM5ZjdhZDBlMzBjZTA1MTRkNGE4Y2VkODAwYjdlZjhiZiJ9fX0=";
        String cactusHeadTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA4MzY2YjgxMWM0MzU5YWVhNjY4NjMwMGNmZDQ1MGQ5ZWFhNDYxNGMzYWYwOGExN2YxNTEzZjVkMmY0OGM0YSJ9fX0=";
        String presentHeadTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjFiYzlkNDJiMDA0MWU4Zjk1Y2I5YjI2NjI4ZmRhZjUwY2QwZTM2ZjdiYjlkNmIzYTRkMmFmMzk0OWRhOTdkNiJ9fX0=";
        String kangaUUID = "a9933aa8-96d0-4a5a-9907-517f16d8cb76";

        ItemStack marioIcon = getCustomHead(marioHeadTexture, "Rainbow Road");
        ItemStack cactusIcon = getCustomHead(cactusHeadTexture, "Wild West");
        ItemStack presentIcon = getCustomHead(presentHeadTexture, "Winter Wonderland");

        ItemStack kangaIcon = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) kangaIcon.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.fromString(kangaUUID));
        skullMeta.setDisplayName(ChatColor.RED + "" + ChatColor.stripColor("Kangawooorld"));
        skullMeta.setOwnerProfile(profile);
        kangaIcon.setItemMeta(skullMeta);

        inventory.setItem(10, marioIcon);
        inventory.setItem(12, cactusIcon);
        inventory.setItem(14, presentIcon);
        inventory.setItem(16, kangaIcon);

        player.openInventory(inventory);
    }


    public ItemStack getCustomHead(String base64Texture, String name) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);

        head.editMeta(SkullMeta.class, skullMeta -> {
            final UUID uuid = UUID.randomUUID();
            final PlayerProfile playerProfile = Bukkit.createProfile(uuid, "");
            playerProfile.setProperty(new ProfileProperty("textures", base64Texture));
            skullMeta.setDisplayName(ChatColor.RED + "" + ChatColor.stripColor(name));

            skullMeta.setPlayerProfile(playerProfile);
        });

        return head;
    }
}

// Limewolf here, just testing the github stuff