package me.lukario.dreamRealm2;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Misc implements Listener {

    public static void damageNoTicks(LivingEntity livingEntity, double damage, Player player){
        if (livingEntity instanceof ArmorStand){}else {
        if (livingEntity.getHealth()>damage){
            livingEntity.setHealth(livingEntity.getHealth()-damage);
            livingEntity.playHurtAnimation(1);
            if (livingEntity.getHurtSound()!=null){
                livingEntity.getWorld().playSound(livingEntity.getLocation(),livingEntity.getHurtSound(),1,1);
            }
            Creature creature = (Creature) livingEntity;
            creature.setTarget(player);
        }else{
            livingEntity.setHealth(0);
            Creature creature = (Creature) livingEntity;
            creature.setTarget(player);
        }
        }
    }
}
