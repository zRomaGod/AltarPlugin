package br.net.rankup.altar.tasks;

import br.net.rankup.altar.AltarPlugin;
import br.net.rankup.altar.misc.ProgressBar;
import br.net.rankup.altar.misc.TimeFormat;
import br.net.rankup.altar.model.AltarModel;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AltarRunnable extends BukkitRunnable {

    public void run() {
        AltarPlugin.getInstance().getAltarManager().getAltars().values().forEach(altarModel -> {

            if(altarModel.getItems().size() < 0) return;
            if(altarModel.getLocation() == null) return;

            if(altarModel.getHologram().isDeleted()) {
                altarModel.getLocation().getChunk().load();
                Hologram hologram = HologramsAPI.createHologram(AltarPlugin.getInstance(),
                        altarModel.getLocation().clone().add(0, altarModel.getHologramHeight(), 0));
                for (final String line : altarModel.getHologramLines()) {
                    hologram.appendTextLine(line
                                    .replace("{status}", "§eAguardando jogadores.")
                            .replace("{bar}", ProgressBar.progressBar(altarModel.getTimePerTick(), altarModel.getTimeFinish(), "▎"))
                            .replace("{tempo}", TimeFormat.formatTime(altarModel.getTimeFinish()-altarModel.getTimePerTick()))
                            .replace('&', '§'));
                    hologram.setAllowPlaceholders(true);
                }
            }


            if(altarModel.getItems().size() == 0) {
                Hologram hologram = altarModel.getHologram();
                for (int i = 0; i < altarModel.getHologramLines().size(); i++) {
                    TextLine line = (TextLine) hologram.getLine(i);
                    line.setText(altarModel.getHologramLines().get(i)
                            .replace("{status}", "§cNenhúm drop setado!")
                            .replace("{bar}", ProgressBar.progressBar(altarModel.getTimePerTick(), altarModel.getTimeFinish(), "▎"))
                            .replace("{tempo}", TimeFormat.formatTime(altarModel.getTimeFinish()-altarModel.getTimePerTick()))
                            .replace('&', '§'));
                }
                return;
            }

            if(!getNearbyEntities(altarModel.getLocation(), altarModel)) {
                Hologram hologram = altarModel.getHologram();
                for (int i = 0; i < altarModel.getHologramLines().size(); i++) {
                    TextLine line = (TextLine) hologram.getLine(i);
                    line.setText(altarModel.getHologramLines().get(i)
                            .replace("{status}", "§eAguardando jogadores.")
                            .replace("{bar}", ProgressBar.progressBar(altarModel.getTimePerTick(), altarModel.getTimeFinish(), "▎"))
                            .replace("{tempo}", TimeFormat.formatTime(altarModel.getTimeFinish()-altarModel.getTimePerTick()))
                            .replace('&', '§'));
                }
            } else {
                Hologram hologram = altarModel.getHologram();
                    for (int i = 0; i < altarModel.getHologramLines().size(); i++) {
                        TextLine line = (TextLine) hologram.getLine(i);
                        line.setText(altarModel.getHologramLines().get(i)
                                .replace("{status}", "§cGerando próximo drop.")
                                        .replace("{bar}", ProgressBar.progressBar(altarModel.getTimePerTick(), altarModel.getTimeFinish(), "▎"))
                                        .replace("{tempo}", TimeFormat.formatTime(altarModel.getTimeFinish()-altarModel.getTimePerTick()))
                                .replace('&', '§'));
                    }
                    altarModel.setTimePerTick(altarModel.getTimePerTick()+1);

                    if(altarModel.getTimePerTick() >= altarModel.getTimeFinish()) {
                        altarModel.setTimePerTick(0);
                        altarModel.onDrop();
                    }
            }
        });
    }




    public boolean getNearbyEntities(final Location altarLocation, AltarModel altarModel) {
        int amountPlayer = 0;
        for (final Entity entity : altarLocation.getWorld().getNearbyEntities(altarLocation, altarModel.getRadius(),  altarModel.getRadius(),  altarModel.getRadius())) {
            if (entity instanceof Player) {
                ++amountPlayer;
            }
        }
        return amountPlayer >= altarModel.getPlayerNeed();
    }
}
