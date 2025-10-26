package me.lukario.dreamRealm2.items.special.magic;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class ParticleAccelerator implements Listener {

    private final Plugin plugin;

    public ParticleAccelerator(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Particle accelerator";
    private static final String ITEM_LORE = ChatColor.YELLOW + "##########";
    private static final Material ITEM_MATERIAL = Material.IRON_SHOVEL;

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(ITEM_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ITEM_NAME);
            meta.setLore(Arrays.asList(ITEM_LORE));
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void usedLaserEye(PlayerInteractEvent event){

        Player player = event.getPlayer();
        if (!isHoldingTheCorrectItem(player)){return;}

        if (event.getAction() == Action.LEFT_CLICK_AIR||event.getAction() == Action.LEFT_CLICK_BLOCK){

//            spawnSineWaveTrail(player,1f, 0.6f, 2, true);
            circularSinTrail(player,0.3f,0.3f,1);

        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR||event.getAction() == Action.RIGHT_CLICK_BLOCK){

            spawnExpandingTighteningWave(player,1.5f, 4.8f, 4);
        }

    }

    private static void circularSinTrail(Player player, float frequency, float amplitude, float particlePerBlock) {
        Location center = player.getLocation().add(0, 1.0, 0); // center point (around chest height)
        double radius = 3.0; // distance from center
        double step = 360.0 / (particlePerBlock * 180); // controls smoothness

        for (double angle = 0; angle < 360; angle += step) {
            // Convert angle to radians
            double radians = Math.toRadians(angle);

            // Circular coordinates (XZ-plane)
            double x = Math.cos(radians) * radius;
            double z = Math.sin(radians) * radius;

            // Vertical oscillation
            double y = Math.sin(angle * frequency) * amplitude;

            // Build final particle location
            Location particleLoc = center.clone().add(x, y, z);

            // Spawn particle
            particleLoc.getWorld().spawnParticle(Particle.FLAME, particleLoc, 1, 0, 0, 0, 0, null, true);
        }
    }


    private static void spawnSineWaveTrail(Player player, float frequency, float strength, float particlePerBlock, boolean isSine){
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize();

        Vector right = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();

        float iIncrease = 1/particlePerBlock;

        for (float i = 0; i < 16; i +=iIncrease){

            double wave = 0;
            if (isSine){
                wave = Math.sin(i * frequency) * strength;
            }else{
                wave = Math.cos(i * frequency) * strength;
            }

            Location current = location.clone()
                    .add(direction.clone().multiply(i)
                    .add(right.clone().multiply(wave))
                    );

            current.getWorld().spawnParticle(Particle.FLAME,current,1,0,0,0,0,null,true);
        }
    }

    private static void spawnExpandingTighteningWave(Player player, double baseFrequency, double baseStrength, double particlesPerBlock) {
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize();
        Vector right = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();

        double iIncrease = 1.0 / particlesPerBlock;
        double reach = 32.0;

        for (double i = 0; i < reach; i += iIncrease) {
            double progress = i / reach; // goes from 0 → 1

            // Frequency increases with distance
            double dynamicFrequency = baseFrequency * (1.0 + progress);
            // Amplitude increases with distance
            double dynamicAmplitude = baseStrength * (0.1 + progress);

            // Side-to-side wave
            double wave = Math.sin(i * dynamicFrequency) * dynamicAmplitude;
            // Gentle vertical variation (optional, looks natural)
            double waveUp = Math.cos(i * dynamicFrequency * 0.7) * (dynamicAmplitude / 2.5);

            Location current = location.clone()
                    .add(direction.clone().multiply(i))
                    .add(right.clone().multiply(wave));
//                    .add(0, waveUp, 0);

            current.getWorld().spawnParticle(Particle.FLAME, current, 1, 0, 0, 0, 0, null, true);
        }
    }



    private static boolean isHoldingTheCorrectItem(Player player) {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();

        if (isCorrectItem(mainHandItem)) {
            return true;
        } else return isCorrectItem(offHandItem);
    }

    private static boolean isCorrectItem(ItemStack item) {
        if (item == null || item.getType() != ITEM_MATERIAL) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        if (!ChatColor.stripColor(meta.getDisplayName()).equals(ChatColor.stripColor(ITEM_NAME))) {
            return false;
        }

        if (meta.getLore() == null) return false;

        for (String loreLine : meta.getLore()) {
            if (ChatColor.stripColor(loreLine).equals(ChatColor.stripColor(ITEM_LORE))) {
                return true;
            }
        }
        return false;
    }

}
