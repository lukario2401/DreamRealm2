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

import java.util.*;

public class Freja implements Listener {

    private final Plugin plugin;

    public Freja(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Freja";
    private static final String ITEM_LORE = ChatColor.YELLOW + "##########";
    private static final Material ITEM_MATERIAL = Material.CROSSBOW;

    private static final HashMap<UUID,Float> cooldownLeft = new HashMap<>();
    private static final HashMap<UUID,Float> frejaBoltCount = new HashMap<>();

    private static final HashMap<UUID,Float> cooldownRight = new HashMap<>();
    private static final HashMap<UUID,Float> frejaRightClickHeld = new HashMap<>();

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
    public void frejaUsed(PlayerInteractEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (cooldownLeft.get(uuid)==null){
            cooldownLeft.put(uuid,0f);
        }
        if (cooldownRight.get(uuid)==null){
            cooldownRight.put(uuid,0f);
        }

        if (frejaBoltCount.get(uuid)==null){
            frejaBoltCount.put(uuid,12f);
        }

        if (frejaRightClickHeld.get(uuid)==null){
            frejaRightClickHeld.put(uuid,0f);
        }


        if (!isHoldingTheCorrectItem(player)){return;}

        if (event.getAction()== Action.LEFT_CLICK_AIR||event.getAction()==Action.LEFT_CLICK_BLOCK){
            if (cooldownLeft.get(uuid)==0){

                if (frejaBoltCount.get(uuid)>0){

                    frejaBoltCount.put(uuid,frejaBoltCount.get(uuid)-1f);
                    frejaLeftClick(player);
                    player.playSound(player, Sound.ENTITY_BLAZE_SHOOT,1,1);
                    player.sendMessage("You have: " + frejaBoltCount.get(uuid) + " Bolts");

                }else{

                    player.sendMessage(" Freja reloading ");
                    player.playSound(player, Sound.BLOCK_ANVIL_PLACE,1,1);

                    cooldownLeft.put(uuid,20f);
                    frejaBoltCount.put(uuid,12f);

                }

            }else{
                player.sendMessage(ChatColor.DARK_RED+"Cooldown: "+(cooldownLeft.get(uuid)/20)+"s");
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
            }
        }
        if (event.getAction()== Action.RIGHT_CLICK_AIR||event.getAction()==Action.RIGHT_CLICK_BLOCK){
            if (cooldownRight.get(uuid)==0){
                cooldownRight.put(uuid,20f);
                frejaRightClickHeld.put(uuid,frejaRightClickHeld.get(uuid)+1f);
                float frejaOldRightClickHeldCount = 0;
                final float[] frejaNewRightClickHeldCount = {0};

                Set<UUID> keyLeft = frejaRightClickHeld.keySet();
                for (UUID uuid1 : keyLeft){
                    frejaOldRightClickHeldCount = frejaRightClickHeld.get(uuid1);
                }

                float finalFrejaOldRightClickHeldCount = frejaOldRightClickHeldCount;
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        Set<UUID> keyLeft = frejaRightClickHeld.keySet();
                        for (UUID uuid1 : keyLeft){
                            frejaNewRightClickHeldCount[0] = frejaRightClickHeld.get(uuid1);
                        }
                        if (finalFrejaOldRightClickHeldCount == frejaNewRightClickHeldCount[0]){
                            frejaRC(player);
                        }
                        this.cancel();
                    }
                }.runTaskTimer(plugin,0,1);
            }else{
                player.sendMessage(ChatColor.DARK_RED+"Cooldown: "+(cooldownRight.get(uuid)/20)+"s");
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
            }
        }
        event.setCancelled(true);
    }

    private void frejaLeftClick(Player player){

        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize();

        new BukkitRunnable(){
            @Override
            public void run(){
                for (float i = 0; i<=32f; i+=0.5f){

                    Location current = location.clone().add(direction.clone().multiply(i));
                    current.getWorld().spawnParticle(Particle.SOUL,current,1,0,0,0,0);

                    if (current.getBlock().getType()!=Material.AIR){
                        i+=256;
                    }

                    for (LivingEntity livingEntity : current.getNearbyLivingEntities(0.6f)){
                        if (!livingEntity.equals(player)){
                            if (livingEntity instanceof ArmorStand){
                            }else{

                                Misc.damageNoTicks(livingEntity,52, player);
                                i+=256;
                                this.cancel();
                            }
                        }
                    }
                }
                this.cancel();
            }
        }.runTaskTimer(plugin,0,5);
    }

    private void frejaRC(Player player){

         UUID uuid = player.getUniqueId();

            if (frejaBoltCount.get(uuid)>9){
                frejaBoltCount.put(uuid,12f);
            }else{
                frejaBoltCount.put(uuid,frejaBoltCount.get(uuid)+3f);
            }
            frejaRightClick(player);
            player.playSound(player, Sound.ENTITY_BLAZE_SHOOT,1,1);
            cooldownRight.put(uuid,30f);

    }

    private void frejaRightClick(Player player){

        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize();
        LivingEntity livingEntityToGetStuckToo = null;
        Location globalLocation = null;

        ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);

        armorStand.setInvulnerable(true);
        armorStand.setSmall(true);
        armorStand.setMarker(true);

        armorStand.setGravity(false);
        armorStand.setBasePlate(false);

        for (float i = 0; i<=48f; i+=1f){

            Location current = location.clone().add(direction.clone().multiply(i));

            if (current.getBlock().getType() != Material.AIR){
                i+=256;
            }

            for (LivingEntity livingEntity : current.getNearbyLivingEntities(1.25f)){
                if (!livingEntity.equals(player)){
                    if (!livingEntity.equals(armorStand)){

                        livingEntityToGetStuckToo=livingEntity;
                        break;

                    }
                }
            }

            globalLocation=current;
        }

        Location finalGlobalLocation = globalLocation;
        LivingEntity finalLivingEntityToGetStuckToo = livingEntityToGetStuckToo;

        new BukkitRunnable(){
            int timeRunning = 0;
            @Override
            public void run(){
                if (timeRunning>=40){

                    armorStand.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_EMITTER,armorStand.getLocation(),1,0,0,0,0);

                    for (LivingEntity livingEntity : armorStand.getLocation().getNearbyLivingEntities(3)){
                        livingEntity.sendMessage("gay");
                        Misc.damageNoTicks(livingEntity,62,player);
                    }

                    armorStand.remove();
                    this.cancel();
                }
                if (finalLivingEntityToGetStuckToo ==null){
                    Location locationForArmorStand = finalGlobalLocation;
                    armorStand.teleport(locationForArmorStand);
                }else{
                    Location locationForArmorStand = finalLivingEntityToGetStuckToo.getLocation();
                    armorStand.teleport(locationForArmorStand);
                }

                timeRunning+=1;
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
