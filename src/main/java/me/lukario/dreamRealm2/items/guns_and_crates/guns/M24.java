package me.lukario.dreamRealm2.items.guns_and_crates.guns;

import me.lukario.dreamRealm2.Misc;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class M24 implements Listener {

    private final Plugin plugin;

    public M24(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "M24 Sniper rifle";
    private static final String ITEM_LORE = ChatColor.YELLOW + "fires 7.62×51mm";
    private static final Material ITEM_MATERIAL = Material.NETHERITE_HOE;

    private static int bullet_custom_model_data = 3;
    private static float cooldownBetweenShots = 30f;
    private static float damage_per_bullet = 60f;
    private static float range_of_bullets = 90;
    private static int max_bullet_count = 6;
    private static int bullet_spread = 1;
    private static int bullet_amount_per_fire = 1;
    private static int bullet_delay = 0;
    private static int bullets_cost_per_shot = 1;

    private static final HashMap<UUID,Float> cooldownLeft = new HashMap<>();

    public static NamespacedKey AMMO_AUG_KEY;

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
            meta.setCustomModelData(2);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void remingtonUsed(PlayerInteractEvent event){

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        AMMO_AUG_KEY = new NamespacedKey(plugin, "ammo_count_aug");

        if (cooldownLeft.get(uuid) == null){
            cooldownLeft.put(uuid,0f);
        }

        if (!isHoldingTheCorrectItem(player)){return;}
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){

            ItemStack gun = player.getInventory().getItemInMainHand();
            ItemMeta meta = gun.getItemMeta();
            if (meta == null){
                player.sendMessage("meta is null");
                return;
            }

            int availableAmmo = Misc.ItemAmountInInventory(player, "copper_ingot", bullet_custom_model_data);


            if (availableAmmo > 0) {
                meta.getPersistentDataContainer().set(AMMO_AUG_KEY, PersistentDataType.INTEGER, max_bullet_count);
                player.playSound(player, Sound.ITEM_ARMOR_EQUIP_IRON,3,1);

                // Cast to Damageable properly
                if (gun.getItemMeta() instanceof Damageable) {
                    Damageable damageable = (Damageable) gun.getItemMeta();
                    damageable.setDamage(0);

                    ItemMeta newMeta = (ItemMeta) damageable;
                    newMeta.setUnbreakable(false); // Make durability bar visible
                    newMeta.setDisplayName(meta.getDisplayName());
                    newMeta.setLore(meta.getLore());
                    newMeta.getPersistentDataContainer().set(AMMO_AUG_KEY, PersistentDataType.INTEGER, max_bullet_count);
                    newMeta.setCustomModelData(meta.getCustomModelData());

                    gun.setItemMeta(newMeta);
                }

                itemToRemove(player, bullet_custom_model_data,1);
                player.sendMessage("Reloaded");
            }else{
                 player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 3,1);
            }
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){

            if (cooldownLeft.get(uuid)==0){
                cooldownLeft.put(uuid,cooldownBetweenShots);
                ItemStack gun = player.getInventory().getItemInMainHand();
                ItemMeta meta = gun.getItemMeta();

                PersistentDataContainer data = meta.getPersistentDataContainer();
                int bullets = data.getOrDefault(AMMO_AUG_KEY, PersistentDataType.INTEGER, 0);

                if (bullets>0) {
                    player.playSound(player,Sound.ENTITY_BLAZE_SHOOT, 3 ,1);
                    meta.getPersistentDataContainer().set(AMMO_AUG_KEY, PersistentDataType.INTEGER, bullets-bullets_cost_per_shot);
                    gun.setItemMeta(meta);

                    if (meta instanceof ItemMeta) {
                        data = meta.getPersistentDataContainer();
                        bullets = data.getOrDefault(AMMO_AUG_KEY, PersistentDataType.INTEGER, 0);

                        // Update durability
                        if (gun.getItemMeta() instanceof Damageable) {
                            Damageable damageable = (Damageable) gun.getItemMeta();
                            int maxDurability = 2031;

                            float maxbulletCount = max_bullet_count;

                            int visualDamage = maxDurability - (int) ((bullets / maxbulletCount) * maxDurability);
                            damageable.setDamage(visualDamage);

                            ItemMeta newMeta = (ItemMeta) damageable;
                            newMeta.setUnbreakable(false);
                            newMeta.setDisplayName(meta.getDisplayName());
                            newMeta.setLore(meta.getLore());
                            newMeta.setCustomModelData(meta.getCustomModelData());
                            newMeta.getPersistentDataContainer().set(AMMO_AUG_KEY, PersistentDataType.INTEGER, bullets);

                            gun.setItemMeta(newMeta);
                        }
                    }

                    for (int k = 0; k < bullet_amount_per_fire; k++){
                        new BukkitRunnable(){
                            @Override
                            public void run(){
                                rayCast(player, bullet_spread);
                                this.cancel();
                            }
                        }.runTaskTimer(plugin,bullet_delay,1);
                    }

                }else{
                     player.sendMessage("out of ammo");
                }
            }
        }

        event.setCancelled(true);
    }

    private void itemToRemove(Player player, int customModelData, int amountToRemove){

        for (int j = 0; j < amountToRemove; j++){
            ItemStack[] contents = player.getInventory().getContents();

                for (int i = 0; i < contents.length; i++) {
                    ItemStack current = contents[i];
                    if (current == null || current.getType() != Material.COPPER_INGOT) continue;

                    ItemMeta meta = current.getItemMeta();
                    if (meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == customModelData) {
                        if (current.getAmount() > 1) {
                            current.setAmount(current.getAmount() - 1);
                        } else {
                            contents[i] = null;
                        }
                        break;
                    }
                }
            player.getInventory().setContents(contents);
        }
    }

    private void rayCast(Player player,int sprayStrength){
        Location location = player.getEyeLocation();
        Random rand = new Random();

        location.setYaw(location.getYaw() - (float) sprayStrength/2 + rand.nextInt(sprayStrength));
        location.setPitch(location.getPitch() - (float) sprayStrength/2 + rand.nextInt(sprayStrength));

        Vector direction = location.getDirection().normalize();

        for (float i = 0; i < range_of_bullets; i+=0.5f){
            Location current = location.clone().add(direction.clone().multiply(i));

            current.getWorld().spawnParticle(Particle.ASH, current, 1 ,0,0,0,0);

            if (current.getBlock().getType()!=Material.AIR){
                return;
            }

            for (LivingEntity livingEntity : current.getNearbyLivingEntities(1)){
                if (!livingEntity.equals(player)){
                    Misc.damageNoTicks(livingEntity,damage_per_bullet,player);
                    player.playSound(player, Sound.ENTITY_GENERIC_EXPLODE,1,1);
                    livingEntity.getWorld().spawnParticle(Particle.EXPLOSION,livingEntity.getLocation(),1,0,0,0,0);
                    return;
                }
            }
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
