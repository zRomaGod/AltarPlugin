package br.net.rankup.altar.manager;

import br.net.rankup.altar.AltarPlugin;
import br.net.rankup.altar.misc.BukkitUtils;
import br.net.rankup.altar.misc.LocationSerializer;
import br.net.rankup.altar.model.AltarModel;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AltarManager {


    private HashMap<String, AltarModel> altars;


    public void loadAll() {
        altars = new HashMap<>();
        int amount = 0;
        for (final String name : AltarPlugin.getConfiguration().getConfigurationSection("altars").getKeys(false)) {
            final ConfigurationSection section = AltarPlugin.getConfiguration().getConfigurationSection("altars." + name);
            Location location = LocationSerializer.deserialize(section.getString("location"));
            int amountDrop = section.getInt("amount-drop");
            int playerNeed = section.getInt("player-need");
            List<ItemStack> items = new ArrayList<>();
            for(String string : section.getStringList("items")) {
                ItemStack itemStack;
                try {
                    itemStack = BukkitUtils.deserializeItemStack(string);
                    items.add(itemStack);
                } catch (Exception e) {}
            }

            location.getChunk().load();
            double hologramHeight = section.getDouble("hologram-height");
            List<String> hologramLines = section.getStringList("hologram-lines");

            for (int i = 0; i < hologramLines.size(); i++) {
                String line = hologramLines.get(i);
                hologramLines.set(i, line.replace('&', '§'));
            }

            Hologram hologram = HologramsAPI.createHologram(AltarPlugin.getInstance(),
                    location.clone().add(0, hologramHeight, 0));
            for (final String line : hologramLines) {
                hologram.appendTextLine(line
                        .replace("{status}", "§eAguardando jogadores.")
                        .replace('&', '§'));
                hologram.setAllowPlaceholders(true);
            }
            hologram.setAllowPlaceholders(true);


            for (final Entity nearbyEntity : location.getWorld().getNearbyEntities(location, 2.0, 2.0, 2.0)) {
                if (nearbyEntity instanceof EnderCrystal) {
                    nearbyEntity.remove();
                }
            }


            Entity entity = location.getWorld().spawn(location, (Class) EnderCrystal.class);
            entity.setMetadata("altar", new FixedMetadataValue(AltarPlugin.getInstance(), name));

            int radius = section.getInt("radius");
            int timePerTick = section.getInt("time-per-tick");
            int timeFinish = section.getInt("time-finish");

            AltarModel altarModel = new AltarModel(name, radius, timePerTick, timeFinish, location, amountDrop, playerNeed, items, hologramHeight, hologramLines, hologram, entity);
            this.altars.put(name, altarModel);
            amount++;
        }
        BukkitUtils.sendMessage(Bukkit.getConsoleSender(), "&fsuccessfully loaded {int} altars ({time} ms)"
                .replace("{time}",""+(System.currentTimeMillis() - AltarPlugin.getStart()))
                .replace("{int}", amount+""));
    }

    public HashMap<String, AltarModel> getAltars() {
        return altars;
    }
}
