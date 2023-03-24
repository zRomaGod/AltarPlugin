package br.net.rankup.altar.model;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

@Getter
@Setter
@AllArgsConstructor
public class AltarModel {

    private String name;
    private int radius;
    private int timePerTick;
    private int timeFinish;
    private Location location;
    private int amounDrop;
    private int playerNeed;
    private List<ItemStack> items;
    private double hologramHeight;
    private List<String> hologramLines;
    private Hologram hologram;
    private Entity entity;

    public void onDrop() {
        this.playEffect(this.location);
        if(this.getItems().isEmpty()) return;
        for (int i = 0; i < this.amounDrop; ++i) {
            final ItemStack entity2 = this.getItems().get(new Random().nextInt(this.items.size()));
            this.location.getWorld().dropItemNaturally(this.location.clone().add(0.0, 1.0, 0.0), entity2);
        }
        this.location.getWorld().strikeLightningEffect(this.location);
        this.location.getWorld().spawn(this.location, Firework.class);
    }

    private void playEffect(final Location altarLocation) {
        for (final Entity entity21 : altarLocation.getWorld().getNearbyEntities(altarLocation, 5.0, 5.0, 5.0)) {
            if (entity21.getType() != EntityType.PLAYER) {
                continue;
            }
            final Player player = (Player)entity21;
            final float f = player.getLocation().getPitch();
            if (f >= 0.0f) {
                player.setVelocity(player.getEyeLocation().getDirection().multiply(-1).multiply(1.5));
                if (f < 70.0f) {
                    continue;
                }
                player.setVelocity(player.getEyeLocation().getDirection().add(new Vector(1, 0, 0)).multiply(-1).multiply(1.5));
            }
            else {
                player.setVelocity(player.getEyeLocation().getDirection().multiply(1.5));
                if (f > -70.0f) {
                    continue;
                }
                player.setVelocity(player.getEyeLocation().getDirection().add(new Vector(1, 0, 0)).multiply(1.5));
            }
        }
    }

}
