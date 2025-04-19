package me.lukario.dreamRealm2.items.special.builder;

import me.lukario.dreamRealm2.Misc;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class Mech implements Listener {

    private final Plugin plugin;

    public Mech(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Mech";
    private static final String ITEM_LORE = ChatColor.YELLOW + "#############";
    private static final Material ITEM_MATERIAL = Material.HEART_POTTERY_SHERD;

    HashMap<UUID,ArmorStand> uuidArmorStandHashMap = new HashMap<>();

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

    @EventHandler()
    public void mechSummoned(PlayerInteractEvent event){
         Player player = event.getPlayer();
         UUID uuid = player.getUniqueId();

         if (!isHoldingTheCorrectItem(player)){return;}



//         if (player.getLocation().distance(uuidArmorStandHashMap.get(uuid).getLocation())<1){return;}
        boolean willCancel = false;
        for (LivingEntity livingEntity : player.getLocation().getNearbyLivingEntities(3)){
            if (!livingEntity.equals(player)){
                if (livingEntity!=null){
                    willCancel=true;
                }
            }
        }

        if (willCancel){
            event.setCancelled(true);
            return;
        }

         if (event.getAction()== Action.LEFT_CLICK_AIR||event.getAction()==Action.LEFT_CLICK_BLOCK){
             Horse horse = player.getWorld().spawn(player.getLocation(), Horse.class);
             ArmorStand armorStand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);

             horse.setTamed(true);
             horse.setAdult();

             horse.setJumpStrength(2);
             horse.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 4,true,false)); // +20% speed for 60s
             horse.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, PotionEffect.INFINITE_DURATION, 4,true,false)); // +20% speed for 60s
             horse.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 5,true,false)); // +20% speed for 60s

             horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
             horse.setInvisible(true);
             horse.setAI(false);

             armorStand.setInvulnerable(true);
             armorStand.setMarker(true);
             armorStand.setInvisible(true);
             armorStand.setGravity(false);

             ItemStack item = new ItemStack(Material.FEATHER);
             ItemMeta meta = item.getItemMeta();
             meta.setCustomModelData(5);
             item.setItemMeta(meta);

             armorStand.setItem(EquipmentSlot.HEAD,item);

             horse.addPassenger(armorStand);
             horse.addPassenger(player);

             uuidArmorStandHashMap.put(uuid,armorStand);

             new BukkitRunnable(){
                 @Override
                 public void run(){
                     if (player.getLocation().distance(uuidArmorStandHashMap.get(uuid).getLocation())>2){
                         armorStand.remove();
                         horse.remove();
                         this.cancel();
                     }
                     if (!armorStand.isValid()){
                         this.cancel();
                     }
                     if (!horse.isValid()){
                         armorStand.remove();
                         this.cancel();
                     }
                     armorStand.setBodyYaw(horse.getBodyYaw());
                 }
             }.runTaskTimer(plugin,0,1);
         }
         event.setCancelled(true);
    }

    @EventHandler
    public void mechUsed(PlayerInteractEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        ArmorStand armorStand = uuidArmorStandHashMap.get(uuid);
        double distance = 2;

        if (armorStand!=null){
            distance = player.getLocation().distance(armorStand.getLocation());
        }

        if (distance<=1){
            if (event.getAction()== Action.LEFT_CLICK_AIR||event.getAction()==Action.LEFT_CLICK_BLOCK){
                mechMainCannonLaunch(player);
            }
            if (event.getAction()== Action.RIGHT_CLICK_AIR||event.getAction()==Action.RIGHT_CLICK_BLOCK){
                mechMachineGun(player);
            }
        }
    }

    private void mechMainCannonLaunch(Player player){
        player.playSound(player,Sound.BLOCK_BEACON_ACTIVATE,3,1);

        new BukkitRunnable(){
            int i = 0;
            @Override
            public void run(){

                Location location = player.getEyeLocation();
                Vector direction = location.getDirection().normalize();

                Location tempLocation = location.clone().add(direction.clone().multiply(2));

                tempLocation.getWorld().spawnParticle(Particle.END_ROD,tempLocation,5,0.2,0.2,0.2,0);

                if (i>=20){
                    player.playSound(player,Sound.ENTITY_GENERIC_EXPLODE,1,1);

                     for (float i = 0; i <= 96; i +=0.5f){
                        Location current = location.clone().add(direction.clone().multiply(i));
                        current.getWorld().spawnParticle(Particle.WITCH,current,5,0.2,0.2,0.2,0);

                        for (LivingEntity livingEntity : current.getNearbyLivingEntities(1)){
                            if (!livingEntity.equals(player)){
                                if (livingEntity instanceof ArmorStand){}else{
                                    if (livingEntity instanceof Horse){}else{
                                        Misc.damageNoTicks(livingEntity,52,player);
                                        player.playSound(player,Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                                        current.getWorld().spawnParticle(Particle.EXPLOSION,current,1,0,0,0,0);
                                        i+=256;
                                        this.cancel();
                                    }
                                }
                            }
                        }
                    }
                    this.cancel();
                }
                i+=1;
            }
        }.runTaskTimer(plugin,0,1);
    }

    private void mechMachineGun(Player player){
        player.playSound(player,Sound.BLOCK_BEACON_DEACTIVATE,3,1);

        new BukkitRunnable(){
            int j = 0;
            @Override
            public void run(){

               Location location = player.getEyeLocation();
               Vector direction = location.getDirection().normalize();

               if (j%2==0){
                player.getWorld().playSound(player,Sound.ITEM_CROSSBOW_SHOOT,1,1);

                for (float i = 0; i <= 96; i +=0.5f){
                    Location current = location.clone().add(direction.clone().multiply(i));
                    current.getWorld().spawnParticle(Particle.ENCHANTED_HIT,current,5,0.2,0.2,0.2,0);

                    for (LivingEntity livingEntity : current.getNearbyLivingEntities(1)){
                        if (!livingEntity.equals(player)){
                            if (livingEntity instanceof ArmorStand){}else{
                                if (livingEntity instanceof Horse){}else{
                                    Misc.damageNoTicks(livingEntity,21,player);
                                    player.playSound(player,Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                                    current.getWorld().spawnParticle(Particle.EXPLOSION,current,1,0,0,0,0);
                                }
                            }
                        }
                    }
                }
               }
               if (j>=40){
                   this.cancel();
               }
               j+=1;
            }
        }.runTaskTimer(plugin,0,2);
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
