
package br.net.rankup.altar;

import br.net.rankup.altar.commands.AltarCommand;
import br.net.rankup.altar.inventory.ContentsInventory;
import br.net.rankup.altar.listener.AltarListener;
import br.net.rankup.altar.listener.InventoryClose;
import br.net.rankup.altar.manager.AltarManager;
import br.net.rankup.altar.misc.BukkitUtils;
import br.net.rankup.altar.misc.ConfigUtils;
import br.net.rankup.altar.tasks.AltarRunnable;
import io.github.rysefoxx.inventory.plugin.pagination.InventoryManager;
import lombok.Getter;
import lombok.Setter;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import me.saiintbrisson.minecraft.command.message.MessageHolder;
import me.saiintbrisson.minecraft.command.message.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
public final class AltarPlugin extends JavaPlugin {

    static long start = 0;
    private AtomicBoolean enable = new AtomicBoolean(false);
    private static ConfigUtils configuration;
    private static AltarPlugin instance;
    @Getter
    private InventoryManager inventoryManager;
    @Getter
    private  BukkitFrame bukkitFrame;
    private AltarManager altarManager;

    @Override
    public void onEnable() {
        instance = this;

        start = System.currentTimeMillis();
        configuration = new ConfigUtils(this,"config.yml");
        configuration.saveDefaultConfig();

        bukkitFrame = new BukkitFrame(AltarPlugin.getInstance());
        loadCommands();
        inventoryManager = new InventoryManager(this);
        inventoryManager.invoke();
        bukkitFrame.registerCommands(new AltarCommand());
        (this.altarManager = new AltarManager()).loadAll();
        this.getServer().getPluginManager().registerEvents(new InventoryClose(), this);
        this.getServer().getPluginManager().registerEvents(new AltarListener(), this);
        new AltarRunnable().runTaskTimer(this, 20L, 20L);

        BukkitUtils.sendMessage(Bukkit.getConsoleSender(), "&aplugin started successfully ({time} ms)".replace("{time}",""+(System.currentTimeMillis() - start)));
        enable.set(true);
    }

    @Override
    public void onDisable() {
            AltarPlugin.getInstance().getAltarManager().getAltars().values().forEach(altarModel -> {
                configuration.getConfig().set("altars."+altarModel.getName()+".time-per-tick", altarModel.getTimePerTick());
                configuration.saveConfig();
                getConfigUtils().saveConfig();
                altarModel.getEntity().remove();
            });
            BukkitUtils.sendMessage(Bukkit.getConsoleSender(), "&cplugin successfully turned off!");
    }

    public static AltarPlugin getInstance() { return instance; }
    public static FileConfiguration getConfiguration() {
        return configuration.getConfig();
    }
    public static ConfigUtils getConfigUtils() {
        return configuration;
    }

    public static long getStart() {
        return start;
    }

    private void loadCommands() {
        MessageHolder messageHolder = getBukkitFrame().getMessageHolder();
        messageHolder.setMessage(MessageType.ERROR, "§cOcorreu um erro durante a execução deste comando, erro: §7{error}§c.");
        messageHolder.setMessage(MessageType.INCORRECT_USAGE, "§cUtilize: /{usage}");
        messageHolder.setMessage(MessageType.NO_PERMISSION, "§cVocê não tem permissão para executar esse comando.");
        messageHolder.setMessage(MessageType.INCORRECT_TARGET, "§cVocê não pode utilizar este comando pois ele é direcionado apenas para {target}.");
    }
        }
