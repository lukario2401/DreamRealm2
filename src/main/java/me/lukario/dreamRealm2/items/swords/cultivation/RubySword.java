package me.lukario.dreamRealm2.items.swords.cultivation;

import me.lukario.dreamRealm2.Misc;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class RubySword implements Listener {

     private final Plugin plugin;

    private static final String ITEM_NAME  = ChatColor.of("#D88F07") + "Ruby Blade";
    private static final String ITEM_LORE  = ChatColor.YELLOW + "Summons eight spinning swords";
    private static final Material ITEM_MATERIAL = Material.IRON_SWORD;

    private static final int amountOfSwords = 12;
    private static final float cooldownForNewSword = 10;
    private static final float damageForLeftClick = 12;
    private static final float damageForRightCLick = 8;
    private static final float rangeForSword = 64;

    private static final HashMap<UUID,Float> amountOfArmorStands = new HashMap<>();
    private static final HashMap<UUID,Float> newSwordCooldown = new HashMap<>();

    private final Map<UUID, List<ArmorStand>> playerArmorStands = new HashMap<>();
    private static Map<UUID, List<LivingEntity>> livingEntitiesHit = new HashMap<>();

    NamespacedKey key = new NamespacedKey(JavaPlugin.getProvidingPlugin(getClass()), "ToBeRemoved");


    public RubySword(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
        runEveryTickForPlayers();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (newSwordCooldown.get(uuid)==null){
                newSwordCooldown.put(uuid,0f);
        }
        if (amountOfArmorStands.get(uuid)==null){
            amountOfArmorStands.put(uuid,0f);
        }
        if (livingEntitiesHit.get(uuid)==null){
            livingEntitiesHit.put(uuid, new ArrayList<>());
        }

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
                                if (amountOfArmorStands.get(uuid)<amountOfSwords){
                                    newSwordCooldown.put(uuid, newSwordCooldown.get(uuid) - 1);
                                }
                            }
                        }
                    }else{
                        Player player = Bukkit.getPlayer(uuid);
                        if (player!=null){
                            if (amountOfArmorStands.get(uuid)<amountOfSwords){
                                amountOfArmorStands.put(uuid,amountOfArmorStands.get(uuid)+1);
                                newSwordCooldown.put(uuid,amountOfArmorStands.get(uuid)*cooldownForNewSword);

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
                                stand.setSmall(true);
                                stand.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte)1);

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
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.THORNS){return;}
        if (event.getDamager() instanceof Player player) {
            if (event.getEntity() instanceof org.bukkit.entity.LivingEntity target && !(target instanceof Player)) {
                if (isHoldingTheCorrectItem(player)){
                    livingEntitiesHit.get(player.getUniqueId()).clear();
                    leftClickJadeSword(player,0f);
                    leftClickJadeSword(player,-20f);
                    leftClickJadeSword(player,20f);
                }
            }
        }
    }

    @EventHandler
    public void jadeSwordUsed(PlayerInteractEvent event){
        if (event.getHand() != org.bukkit.inventory.EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!isHoldingTheCorrectItem(player)){return;}

        if (event.getAction() == Action.LEFT_CLICK_AIR||event.getAction()==Action.LEFT_CLICK_BLOCK){
            livingEntitiesHit.get(uuid).clear();
            leftClickJadeSword(player,0f);
            leftClickJadeSword(player,-20f);
            leftClickJadeSword(player,20f);
        }
        if (event.getAction()== Action.RIGHT_CLICK_AIR||event.getAction()==Action.RIGHT_CLICK_BLOCK) {
            if (amountOfArmorStands.get(uuid)>0){
                amountOfArmorStands.put(uuid,amountOfArmorStands.get(uuid)-1);

                List<ArmorStand> stands = playerArmorStands.get(player.getUniqueId());
                if (stands != null && !stands.isEmpty()) {
                    rightClickJadeSword(player);
                    stands.removeLast();
                }
            }
        }
    }

    private void leftClickJadeSword(Player player, Float degree){

        Location location = player.getEyeLocation();
        location.setYaw(location.getYaw()+degree);
        org.bukkit.util.Vector direction = location.getDirection().normalize();
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
                        if (!livingEntitiesHit.get(uuid).contains(livingEntity)){
                            Misc.damageNoTicks(livingEntity,amountOfArmorStands.get(uuid)*damageForLeftClick, player);
                            player.playSound(player,Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                            livingEntitiesHit.get(uuid).add(livingEntity);
                            i+=256;
                        }
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
        org.bukkit.util.Vector direction = location.getDirection().normalize();

        for (float i = 0; i <= rangeForSword; i +=0.5f){
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

     private void beamBetweenTwoEntity(ArmorStand armorStand, Location finalLocation, Player player){
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
                            UUID uuid = player.getUniqueId();
                            Misc.damageNoTicks(livingEntity,(amountOfArmorStands.get(uuid)+1)*damageForRightCLick,player);
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
                            UUID uuid = player.getUniqueId();
                            Misc.damageNoTicks(livingEntity,(amountOfArmorStands.get(uuid)+1)*damageForRightCLick,player);
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
                    current.add(direction.clone().multiply(0.5));
                    armorStand.teleport(current);

                }else{
                    timeAlive+=256;
                }
                timeAlive+=1;
            }
        }.runTaskTimer(plugin,0,1);
    }



    private void runEveryTickForPlayers() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();

                    if (isHoldingTheCorrectItem(player)){
                        player.sendActionBar(
                            net.kyori.adventure.text.Component.text(
                                "Cooldown: " + newSwordCooldown.getOrDefault(uuid, 0f)
                                + " | Stands: " + amountOfArmorStands.getOrDefault(uuid, 0f)
                            )
                        );
                    }

                    // Make their armor stands follow them
                    List<ArmorStand> stands = playerArmorStands.get(uuid);
                    if (stands != null && !stands.isEmpty()) {
                        double radius = 3; // distance around player
                        double height = 1.0; // height above ground
                        double angleStep = (2 * Math.PI) / stands.size(); // even spacing

                        for (int i = 0; i < stands.size(); i++) {
                            ArmorStand stand = stands.get(i);
                            double angle = i * angleStep + (System.currentTimeMillis() / 200.0); // also rotates
                            double x = player.getLocation().getX() + radius * Math.cos(angle);
                            double z = player.getLocation().getZ() + radius * Math.sin(angle);
                            double y = player.getLocation().getY() + height;

                            Location newLoc = new Location(player.getWorld(), x, y, z);
                            if (isHoldingTheCorrectItem(player)){
                                stand.teleport(newLoc);
                            }else{
                                newLoc.setY(newLoc.getY()+600);
                                stand.teleport(newLoc);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1);
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
