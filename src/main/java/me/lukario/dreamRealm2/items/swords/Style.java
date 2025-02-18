package me.lukario.dreamRealm2.items.swords;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class Style implements Listener {

    private final Plugin plugin;

    public Style(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Style";
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating ######";
    private static final Material ITEM_MATERIAL = Material.IRON_SWORD;

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

    private final HashMap<UUID,Float> cooldown = new HashMap<>();

    private final HashMap<UUID,Integer> attackCount = new HashMap<>();

    private void cooldownManagement(){
        new BukkitRunnable(){
            @Override
            public void run() {
                Set<UUID> key = cooldown.keySet();

                for (UUID uuid : key){
                    if (cooldown.get(uuid)>0){
                        cooldown.put(uuid,cooldown.get(uuid)-1);
                    }

                }
            }
        }.runTaskTimer(plugin,0,1);
    }

    @EventHandler
    public void styleUsed(PlayerInteractEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (cooldown.get(uuid)==null){
            cooldown.put(uuid,0F);
        }

        if (attackCount.get(uuid)==null){
            attackCount.put(uuid,1);
        }

        if (!isHoldingTheCorrectItem(player)){
            return;
        }

        if(Action.LEFT_CLICK_BLOCK==event.getAction()||Action.LEFT_CLICK_AIR==event.getAction()){
            if (cooldown.get(uuid)==0){
                cooldown.put(uuid,10F);

                player.getWorld().playSound(player, Sound.ENTITY_PLAYER_ATTACK_SWEEP,1,1);

                if (attackCount.get(uuid)==1){

                    style1(player);
                } else if (attackCount.get(uuid)==2) {

                    style2(player, 10);
                    style2(player, -10);

                } else if (attackCount.get(uuid)==3) {

                    style1(player);
                    style2(player,10);
                    style2(player,-10);

                } else if (attackCount.get(uuid)==4) {

                    style2(player,10);
                    style2(player,-10);
                    style2(player,30);
                    style2(player,-30);

                } else if (attackCount.get(uuid)==5){

                    style1(player);
                    style2(player,10);
                    style2(player,30);
                    style2(player,-10);
                    style2(player,-30);

                } else if (attackCount.get(uuid)==6) {

                    style2(player,10);
                    style2(player,-10);
                    style2(player,30);
                    style2(player,-30);
                    style2(player,50);
                    style2(player,-50);

                } else if (attackCount.get(uuid)==7) {

                    style1(player);
                    style2(player,10);
                    style2(player,-10);
                    style2(player,30);
                    style2(player,-30);
                    style2(player,50);
                    style2(player,-50);


                }else if (attackCount.get(uuid)==8) {

                    style2(player,10);
                    style2(player,-10);
                    style2(player,30);
                    style2(player,-30);
                    style2(player,50);
                    style2(player,-50);
                    style2(player,70);
                    style2(player,-70);


                }else if (attackCount.get(uuid)==9) {

                    style1(player);
                    style2(player,10);
                    style2(player,-10);
                    style2(player,30);
                    style2(player,-30);
                    style2(player,50);
                    style2(player,-50);
                    style2(player,70);
                    style2(player,-70);


                }else if (attackCount.get(uuid)==10) {

                    style2(player,10);
                    style2(player,-10);
                    style2(player,30);
                    style2(player,-30);
                    style2(player,50);
                    style2(player,-50);
                    style2(player,70);
                    style2(player,-70);
                    style2(player,90);
                    style2(player,-90);


                }else if (attackCount.get(uuid)==11) {

                    style1(player);
                    style2(player,10);
                    style2(player,-10);
                    style2(player,30);
                    style2(player,-30);
                    style2(player,50);
                    style2(player,-50);
                    style2(player,70);
                    style2(player,-70);
                    style2(player,90);
                    style2(player,-90);


                }
                else if (attackCount.get(uuid)==12) {

                    style1(player);
                    style3(player,10,-1,1);
                    style3(player,-10,-1,1);
                    style2(player,30);
                    style2(player,-30);
                    style2(player,50);
                    style2(player,-50);
                    style2(player,70);
                    style2(player,-70);
                    style2(player,90);
                    style2(player,-90);

                }else if (attackCount.get(uuid)==13) {

                    style1(player);
                    style3(player,10,-1,1);
                    style3(player,-10,-1,1);
                    style3(player,30,-1,1);
                    style3(player,-30,-1,1);
                    style2(player,50);
                    style2(player,-50);
                    style2(player,70);
                    style2(player,-70);
                    style2(player,90);
                    style2(player,-90);


                }else if (attackCount.get(uuid)==14) {

                    style1(player);
                    style3(player,10,-1,1);
                    style3(player,-10,-1,1);
                    style3(player,30,-1,1);
                    style3(player,-30,-1,1);
                    style3(player,50,-1,1);
                    style3(player,-50,-1,1);
                    style2(player,70);
                    style2(player,-70);
                    style2(player,90);
                    style2(player,-90);


                }else if (attackCount.get(uuid)==15) {

                    style1(player);
                    style3(player,10,-1,1);
                    style3(player,-10,-1,1);
                    style3(player,30,-1,1);
                    style3(player,-30,-1,1);
                    style3(player,50,-1,1);
                    style3(player,-50,-1,1);
                    style3(player,70,-1,1);
                    style3(player,-70,-1,1);
                    style2(player,90);
                    style2(player,-90);


                }else if (attackCount.get(uuid)==16) {

                    style1(player);
                    style3(player,10,-1,1);
                    style3(player,-10,-1,1);
                    style3(player,30,-1,1);
                    style3(player,-30,-1,1);
                    style3(player,50,-1,1);
                    style3(player,-50,-1,1);
                    style3(player,70,-1,1);
                    style3(player,-70,-1,1);
                    style3(player,90,-1,1);
                    style3(player,-90,-1,1);

                    attackCount.put(uuid,0);
                }

                attackCount.put(uuid,attackCount.get(uuid)+1);
            }else{
                player.sendMessage(ChatColor.DARK_RED + "Cooldown: " + cooldown.get(uuid)/20);
                player.getWorld().playSound(player,Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
            }
        }

    }

    private void style3(Player player, float rotation,float left, float right){
        new BukkitRunnable(){
            int timeAlive = 0;

             Location location = player.getEyeLocation();
            {location.setYaw(location.getYaw() + rotation);}

            Vector direction = location.getDirection().normalize();

            Vector rightOffset = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(right); // 0.5 blocks right
            Vector leftOffsett = direction.clone().crossProduct(new Vector(0,1,0)).normalize().multiply(left);


            @Override
            public void run(){
                if (timeAlive>=12){
                    this.cancel();
                }

                Location current = location.clone().add(direction.clone().multiply(timeAlive/2));
                current.getWorld().spawnParticle(Particle.SWEEP_ATTACK,current,1,0,0,0,0);

                for (LivingEntity livingEntity : current.getNearbyLivingEntities(1)){
                    if (!livingEntity.equals(player)){
                        livingEntity.damage(21,player);
                    }
                }

                current.clone().add(rightOffset).getWorld().spawnParticle(Particle.SWEEP_ATTACK,current.clone().add(rightOffset),3,.2,0,.2,0);

                for (LivingEntity livingEntity : current.clone().add(rightOffset).getNearbyLivingEntities(1)){
                    if (!livingEntity.equals(player)){

                        livingEntity.playHurtAnimation(1);
                        livingEntity.getWorld().playSound(current.clone().add(rightOffset),livingEntity.getHurtSound(),1,1);

                        if (livingEntity.getHealth()>21){
                        livingEntity.setHealth(livingEntity.getHealth()-21);
                        }else{
                            livingEntity.setHealth(0);
                        }

                    }
                }

                current.clone().add(leftOffsett).getWorld().spawnParticle(Particle.SWEEP_ATTACK,current.clone().add(leftOffsett),3,.2,0,.2,0);

                for (LivingEntity livingEntity : current.clone().add(leftOffsett).getNearbyLivingEntities(1)){
                    if (!livingEntity.equals(player)){

                        livingEntity.playHurtAnimation(1);
                        livingEntity.getWorld().playSound(current.clone().add(leftOffsett),livingEntity.getHurtSound(),1,1);

                        if (livingEntity.getHealth()>21){
                        livingEntity.setHealth(livingEntity.getHealth()-21);
                        }else{
                            livingEntity.setHealth(0);
                        }
                    }
                }

                timeAlive+=1;
            }
        }.runTaskTimer(plugin,0,1);
    }

    private void style2(Player player, float rotation){
        new BukkitRunnable(){
            int timeAlive = 0;

             Location location = player.getEyeLocation();
            {location.setYaw(location.getYaw() + rotation);}

            Vector direction = location.getDirection().normalize();

            @Override
            public void run(){
                if (timeAlive>=10){
                    this.cancel();
                }

                Location current = location.clone().add(direction.clone().multiply(timeAlive/2));
                current.getWorld().spawnParticle(Particle.SWEEP_ATTACK,current,1,0,0,0,0);

                for (LivingEntity livingEntity : current.getNearbyLivingEntities(1)){
                    if (!livingEntity.equals(player)){
                        livingEntity.damage(21,player);
                    }
                }
                timeAlive+=1;
            }
        }.runTaskTimer(plugin,0,1);
    }

    private void style1(Player player){
        new BukkitRunnable(){
                    int timeAlive = 0;

                    Location location = player.getEyeLocation();
                    Vector direction = location.getDirection().normalize();

                    @Override
                    public void run(){
                        if (timeAlive>=10){
                            this.cancel();
                        }

                        Location current = location.clone().add(direction.clone().multiply(timeAlive/2));
                        current.getWorld().spawnParticle(Particle.SWEEP_ATTACK,current,1,0,0,0,0);

                        for (LivingEntity livingEntity : current.getNearbyLivingEntities(1)){
                            if (!livingEntity.equals(player)){
                                livingEntity.damage(21,player);
                            }
                        }

                        timeAlive+=1;
                    }
                }.runTaskTimer(plugin,0,1);
    }

    private static boolean isHoldingTheCorrectItem(Player player) {
        return isCorrectItem(player.getInventory().getItemInMainHand()) ||
               isCorrectItem(player.getInventory().getItemInOffHand());
    }

    private static boolean isCorrectItem(ItemStack item) {
        if (item == null || item.getType() != ITEM_MATERIAL) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        if (!ChatColor.stripColor(meta.getDisplayName()).equals(ChatColor.stripColor(ITEM_NAME))) return false;
        return meta.getLore() != null && meta.getLore().contains(ITEM_LORE);
    }

}
