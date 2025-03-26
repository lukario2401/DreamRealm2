package me.lukario.dreamRealm2.items.special.ranged.bow;

import me.lukario.dreamRealm2.Misc;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.eclipse.aether.metadata.Metadata;

import java.util.*;

public class Stack implements Listener {

    private final Plugin plugin;

    public Stack(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
        startStackCountManagement();
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Stack";
    private static final String ITEM_LORE = ChatColor.YELLOW + "#########";
    private static final Material ITEM_MATERIAL = Material.IRON_SHOVEL;

    private static final HashMap<UUID,Float> cooldownLeft = new HashMap<>();
    private static final HashMap<UUID,Float> cooldownRight = new HashMap<>();

    private final Map<UUID, Float> stackCount = new HashMap<>();
private final Map<UUID, Long> lastUpdateTime = new HashMap<>(); // Track last update time

    // Call this whenever you update stackCount for a UUID
    public void updateStackCount(UUID uuid, float newValue) {
        stackCount.put(uuid, newValue);
        lastUpdateTime.put(uuid, System.currentTimeMillis()); // Record update time
    }

    private void startStackCountManagement() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                Iterator<Map.Entry<UUID, Float>> iterator = stackCount.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<UUID, Float> entry = iterator.next();
                    UUID uuid = entry.getKey();

                    if (stackCount.get(uuid) == null) {
                        stackCount.put(uuid, 1f);
                    }

                    Long lastUpdate = lastUpdateTime.get(uuid);

                    // If no update in the last 20 ticks (20 ticks = 1000ms)
                    if (lastUpdate != null && currentTime - lastUpdate >= 5000) {
                        iterator.remove(); // Remove from stackCount
                        lastUpdateTime.remove(uuid); // Cleanup
                    }
                }
            }
        }.runTaskTimer(plugin, 100, 100); // Check every second (20 ticks)
    }

    private void cooldownManagement(){
        new BukkitRunnable(){
            @Override
            public void run(){
                Set<UUID> keyLeft = cooldownLeft.keySet();
                for (UUID uuid : keyLeft){
                    if (cooldownLeft.get(uuid)>0){
                        cooldownLeft.put(uuid,cooldownLeft.get(uuid)-1);
                    }
                }
                Set<UUID> keyRight = cooldownRight.keySet();
                for (UUID uuid : keyRight){
                    if (cooldownRight.get(uuid)>0){
                        cooldownRight.put(uuid,cooldownRight.get(uuid)-1);
                    }
                }
            }
        }.runTaskTimer(plugin,0,1);
    }

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
    public void stackUsed(PlayerInteractEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (cooldownLeft.get(uuid)==null){
            cooldownLeft.put(uuid,0f);
        }
        if (cooldownRight.get(uuid)==null){
            cooldownRight.put(uuid,0f);
        }

        if (!isHoldingTheCorrectItem(player)){return;}

        if (event.getAction()== Action.LEFT_CLICK_AIR||event.getAction()==Action.LEFT_CLICK_BLOCK){
            if (cooldownLeft.get(uuid)==0){

                stack(player);
                player.playSound(player, Sound.ENTITY_BLAZE_SHOOT,1,1);

                cooldownLeft.put(uuid,2f);

            }else{
                player.sendMessage(ChatColor.DARK_RED+"Cooldown: "+(cooldownLeft.get(uuid)/20)+"s");
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
            }
        }
    }

    private void stack(Player player){

        UUID playerUUID = player.getUniqueId();

        Location spawnLocation = player.getLocation().add(0,1,0);
        ArmorStand armorStand = spawnLocation.getWorld().spawn(spawnLocation, ArmorStand.class);


        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);
        armorStand.setSmall(true);

        armorStand.setGravity(false);
        armorStand.setInvisible(false);
        armorStand.setBasePlate(false);


        Location location = player.getLocation().add(0,1,0);

        new BukkitRunnable(){
            float timeRunning = 0f;
            @Override
            public void run(){
                if (timeRunning>120){
                    this.cancel();
                }

                Vector direction = location.getDirection().normalize();

                Location current = location.clone().add(direction.clone().multiply(timeRunning));
                armorStand.teleport(current);
//                current.getWorld().spawnParticle(Particle.FIREWORK,current,1,0,0,0,0);

                for (LivingEntity livingEntity1 : armorStand.getLocation().getNearbyLivingEntities(1.5f)){
                    if (!livingEntity1.equals(player)){
                        if (!livingEntity1.equals(armorStand)){

                            if (stackCount.get(playerUUID) == null){
                                stackCount.put(playerUUID,1f);
                            }

                            float damageForLivingEntity1 = stackCount.get(playerUUID) * stackCount.get(playerUUID);
                            Misc.damageNoTicks(livingEntity1,damageForLivingEntity1,player);

                            stackCount.put(playerUUID,stackCount.get(playerUUID)+1);

                            updateStackCount(playerUUID,stackCount.get(playerUUID)+1);

                            player.sendMessage("your stack is: " + stackCount.get(playerUUID));
                            armorStand.remove();
                            this.cancel();
                            break;
                        }
                    }
                }
                timeRunning+=1f;
            }
        }.runTaskTimer(plugin,0,1);
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
