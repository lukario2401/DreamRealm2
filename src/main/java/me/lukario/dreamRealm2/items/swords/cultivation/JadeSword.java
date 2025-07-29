package me.lukario.dreamRealm2.items.swords.cultivation;

import com.comphenix.protocol.wrappers.EnumWrappers;
import me.lukario.dreamRealm2.Misc;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class JadeSword implements Listener {

    private final Plugin plugin;

    private static final String ITEM_NAME  = ChatColor.of("#D88F07") + "Jade Blade";
    private static final String ITEM_LORE  = ChatColor.YELLOW + "Summons six spinning swords";
    private static final Material ITEM_MATERIAL = Material.IRON_SWORD;

    private static final HashMap<UUID,Float> amountOfArmorStands = new HashMap<>();
    private static final HashMap<UUID,Float> newSwordCooldown = new HashMap<>();

    private final Map<UUID, List<ArmorStand>> playerArmorStands = new HashMap<>();


    public JadeSword(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
        runEveryTickForPlayers();
    }

    private void cooldownManagement() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Set<UUID> keyLeft = newSwordCooldown.keySet();
                for (UUID uuid : keyLeft) {
                    if (newSwordCooldown.get(uuid) > 0) {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player!=null){
                            if (isHoldingTheCorrectItem(player)){
                                if (amountOfArmorStands.get(uuid)<6){
                                    newSwordCooldown.put(uuid, newSwordCooldown.get(uuid) - 1);
                                }
                            }
                        }
                    }else{
                        Player player = Bukkit.getPlayer(uuid);
                        if (player!=null){
                            if (amountOfArmorStands.get(uuid)<6){
                                newSwordCooldown.put(uuid,40f);
                                amountOfArmorStands.put(uuid,amountOfArmorStands.get(uuid)+1);

                                ArmorStand stand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);

                                ItemStack customItem = new ItemStack(Material.IRON_SWORD);
                                ItemMeta meta = customItem.getItemMeta();
                                if (meta != null) {
                                    meta.setCustomModelData(16);
                                    customItem.setItemMeta(meta);
                                }

                                stand.getEquipment().setHelmet(customItem);
                                stand.setVisible(false);
                                stand.setMarker(true);
                                stand.setGravity(false);
                                stand.setSmall(false);

                                playerArmorStands.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(stand);

                            }
                        }
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
            meta.setCustomModelData(16);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (newSwordCooldown.get(uuid)==null){
            newSwordCooldown.put(uuid,0f);
        }
        if (amountOfArmorStands.get(uuid)==null){
            amountOfArmorStands.put(uuid,0f);
        }
    }

    @EventHandler
    public void jadeSwordUsed(PlayerInteractEvent event){
        if (event.getHand() != org.bukkit.inventory.EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!isHoldingTheCorrectItem(player)){return;}

        if (event.getAction()== Action.LEFT_CLICK_AIR||event.getAction()==Action.LEFT_CLICK_BLOCK){
            leftClickJadeSword(player,-20f);
            leftClickJadeSword(player,0f);
            leftClickJadeSword(player,20f);
        }
        if (event.getAction()== Action.RIGHT_CLICK_AIR||event.getAction()==Action.RIGHT_CLICK_BLOCK) {
//           player.sendMessage("Right");
            if (amountOfArmorStands.get(uuid)>0){
                amountOfArmorStands.put(uuid,amountOfArmorStands.get(uuid)-1);

                List<ArmorStand> stands = playerArmorStands.get(player.getUniqueId());
                if (stands != null && !stands.isEmpty()) {
                    rightClickJadeSword(player);
//                    ArmorStand lastStand = stands.getLast();
//                    lastStand.remove();
                    stands.removeLast();
                }
            }

//            Material heldMaterial = player.getInventory().getItemInMainHand().getType();
//            player.setCooldown(heldMaterial, 1);
        }
    }

    private void leftClickJadeSword(Player player, Float degree){

        Location location = player.getEyeLocation();
        location.setYaw(location.getYaw()+degree);
        Vector direction = location.getDirection().normalize();
        UUID uuid = player.getUniqueId();

        for (float i = 0; i<=6f; i+=0.5f){

            Location current = location.clone().add(direction.clone().multiply(i));
            current.getWorld().spawnParticle(Particle.SOUL,current,1,0,0,0,0);

            if (current.getBlock().isSolid()){
                i+=256;
            }

            for (LivingEntity livingEntity : current.getNearbyLivingEntities(0.6f)){
                if (!livingEntity.equals(player)){
                    if (livingEntity instanceof ArmorStand){
                    }else{

                        Misc.damageNoTicks(livingEntity,amountOfArmorStands.get(uuid)*12, player);
                        player.playSound(player,Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);

                        i+=256;
                    }
                }
            }
        }
    }

    private void rightClickJadeSword(Player player){
        ArmorStand armorStand = null;
        Location finalLocation = null;

        List<ArmorStand> stands = playerArmorStands.get(player.getUniqueId());
        if (stands != null && !stands.isEmpty()) {
            ArmorStand armorStandFromList = stands.getLast();
            armorStand = armorStandFromList;
        }


        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize();

        for (float i = 0; i <= 48; i +=0.5f){
            Location current = location.clone().add(direction.clone().multiply(i));

            if (current.getBlock().isSolid()){
                finalLocation=current;
                i+=256;
            }

            if (i==16){
                finalLocation=current;
            }
        }

        if (armorStand != null && finalLocation != null) {
            beamBetweenTwoEntity(armorStand, finalLocation, player);
        }

    }

     private void beamBetweenTwoEntity(ArmorStand armorStand, Location finalLocation,Player player){
        Location startLocation = armorStand.getLocation().add(0,1,0);
        Location endLocation = finalLocation;

        Vector direction = endLocation.toVector().subtract(startLocation.toVector()).normalize();
        double distance = startLocation.distance(endLocation);
        Location current = startLocation;

        new BukkitRunnable(){
            int timeAlive = 0;
            @Override
            public void run(){
                if (armorStand.isDead()){
                    timeAlive+=200;
                }
                if (timeAlive>=200){

                    for (LivingEntity livingEntity : current.getNearbyLivingEntities(3)){
                        if (livingEntity instanceof ArmorStand){
                        }else{
                            Misc.damageNoTicks(livingEntity,24,player);
                        }
                    }

                    current.getWorld().playSound(current,Sound.ENTITY_GENERIC_EXPLODE,3,1);
                    current.getWorld().spawnParticle(Particle.EXPLOSION,current,2,0,0,0,0);
                    current.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER,current,2,0,0,0,0);

                    List<ArmorStand> stands = playerArmorStands.get(player.getUniqueId());
                    if (stands != null && !stands.isEmpty()) {
                        stands.remove(armorStand);
                    }
                    armorStand.remove();
                    this.cancel();
                }

                if (current.distance(endLocation)>0.6){

//                    current.getWorld().spawnParticle(Particle.ENCHANTED_HIT,current,1,0,0,0,0);
                    current.add(direction.clone().multiply(0.5));
                    armorStand.teleport(current);

                }else{
                    timeAlive+=256;
                }
                timeAlive+=1;
                                if (armorStand.isDead()){
                    timeAlive+=200;
                }
                if (timeAlive>=200){

                    for (LivingEntity livingEntity : current.getNearbyLivingEntities(3)){
                        if (livingEntity instanceof ArmorStand){
                        }else{
                            Misc.damageNoTicks(livingEntity,24,player);
                        }
                    }

                    current.getWorld().playSound(current,Sound.ENTITY_GENERIC_EXPLODE,3,1);
                    current.getWorld().spawnParticle(Particle.EXPLOSION,current,2,0,0,0,0);
                    current.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER,current,2,0,0,0,0);

                    List<ArmorStand> stands = playerArmorStands.get(player.getUniqueId());
                    if (stands != null && !stands.isEmpty()) {
                        stands.remove(armorStand);
                    }
                    armorStand.remove();
                    this.cancel();
                }

                if (current.distance(endLocation)>0.6){

//                    current.getWorld().spawnParticle(Particle.ENCHANTED_HIT,current,1,0,0,0,0);
                    current.add(direction.clone().multiply(0.5));
                    armorStand.teleport(current);

                }else{
                    timeAlive+=256;
                }
                timeAlive+=1;
            }
        }.runTaskTimer(plugin,0,1);
    }

        // Predefined offsets for each sword (x = left/right, y = up, z = forward)
    private final Vector[] swordOffsets = new Vector[]{
        new Vector(0.8, 1.2, 0.4),    // 1: up + right
        new Vector(-0.8, 1.2, 0.4),   // 2: up + left
        new Vector(1.2, 1.4, 0.8),    // 3: further right + up
        new Vector(-1.2, 1.4, 0.8),   // 4: further left + up
        new Vector(1.6, 1.6, 1.2),    // 5: even further right
        new Vector(-1.6, 1.6, 1.2)    // 6: even further left
    };

    private void runEveryTickForPlayers() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();

                    // Update action bar
                    player.sendActionBar(
                        net.kyori.adventure.text.Component.text(
                            "Cooldown: " + newSwordCooldown.getOrDefault(uuid, 0f)
                            + " | Stands: " + amountOfArmorStands.getOrDefault(uuid, 0f)
                        )
                    );

                    // Update armor stand positions
                    List<ArmorStand> stands = playerArmorStands.get(uuid);
                    if (stands != null && !stands.isEmpty()) {
                        Location baseLoc = player.getLocation();
                        baseLoc.setY(baseLoc.getY() + 0.5); // slight height offset

                        for (int i = 0; i < stands.size() && i < swordOffsets.length; i++) {
                            ArmorStand stand = stands.get(i);
                            Vector offset = swordOffsets[i];

                            // Optional: floating animation (sine wave)
                            double floatY = Math.sin((System.currentTimeMillis() / 200.0) + i) * 0.1;
                            Vector animatedOffset = offset.clone();
                            animatedOffset.setY(animatedOffset.getY() + floatY);

                            // Rotate offset based on player yaw
                            Location swordLoc = baseLoc.clone().add(rotateVector(animatedOffset, baseLoc.getYaw()));
                            stand.teleport(swordLoc);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1); // Runs every tick
    }

    // Rotates a vector by yaw (so it stays relative to player's facing)
    private Vector rotateVector(Vector v, float yawDegrees) {
        double yaw = Math.toRadians(-yawDegrees);
        double cos = Math.cos(yaw);
        double sin = Math.sin(yaw);
        double x = v.getX() * cos - v.getZ() * sin;
        double z = v.getX() * sin + v.getZ() * cos;
        return new Vector(x, v.getY(), z);
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
