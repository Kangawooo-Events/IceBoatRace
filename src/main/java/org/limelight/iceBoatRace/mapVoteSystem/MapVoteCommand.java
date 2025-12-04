package org.limelight.iceBoatRace.mapVoteSystem;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class MapVoteCommand implements CommandExecutor {
    public static final Map<String, Integer> voteMap = new HashMap<>();
    private final String[] mapNames = {"Rainbow Road", "Wild West", "Winter Wonderland", "Kangawooorld", "Medieval Castle"};

    private JavaPlugin plugin;
    public MapVoteCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp() || args.length > 0)
            return true;

        Player sendPlayer = (Player) sender;


        sendPlayer.sendMessage(ChatColor.AQUA + "[ICEBOAT] Started Vote");
        voteStart();

        return true;
    }

    private void voteStart() {
        for (String mapName : mapNames) {
            voteMap.put(mapName, 0);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "hasVoted"), PersistentDataType.BOOLEAN, false);
            openVoteUI(player);
        }
    }

    private void openVoteUI(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 9, "Vote");

        String marioHeadTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjBhODUyZmI3ZGRjMWE3MzU5MzY4YWRlNjNmMThjMzM5ZjdhZDBlMzBjZTA1MTRkNGE4Y2VkODAwYjdlZjhiZiJ9fX0=";
        String cactusHeadTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA4MzY2YjgxMWM0MzU5YWVhNjY4NjMwMGNmZDQ1MGQ5ZWFhNDYxNGMzYWYwOGExN2YxNTEzZjVkMmY0OGM0YSJ9fX0=";
        String presentHeadTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjFiYzlkNDJiMDA0MWU4Zjk1Y2I5YjI2NjI4ZmRhZjUwY2QwZTM2ZjdiYjlkNmIzYTRkMmFmMzk0OWRhOTdkNiJ9fX0=";
        String casteHeadTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmVlZjdlNTZjZGU3NDA3NzJkZmI3NmRkZDJmNTg0YmU4OTA3Yjg1OTc2NjhlNDAyNjM0OTg2NDY5MjMwYWE0OSJ9fX0=";
        String kangaUUID = "a9933aa8-96d0-4a5a-9907-517f16d8cb76";

        ItemStack marioIcon = getCustomHead(marioHeadTexture, "Rainbow Road");
        ItemStack cactusIcon = getCustomHead(cactusHeadTexture, "Wild West");
        ItemStack presentIcon = getCustomHead(presentHeadTexture, "Winter Wonderland");
        ItemStack castleIcon = getCustomHead(casteHeadTexture, "Medieval Castle");

        ItemStack kangaIcon = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) kangaIcon.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.fromString(kangaUUID));
        skullMeta.setDisplayName(ChatColor.RED + "" + ChatColor.stripColor("Kangawooorld"));
        skullMeta.setOwnerProfile(profile);
        kangaIcon.setItemMeta(skullMeta);

        inventory.setItem(2, marioIcon);
        inventory.setItem(3, cactusIcon);
        inventory.setItem(4, presentIcon);
        inventory.setItem(5, kangaIcon);
        inventory.setItem(6, castleIcon);

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