package me.lukario.dreamRealm2;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.lukario.dreamRealm2.commands.DamageTest;
import me.lukario.dreamRealm2.commands.GetCommand;
import me.lukario.dreamRealm2.commands.SumCommand;
import me.lukario.dreamRealm2.items.armor.Ferocity;
import me.lukario.dreamRealm2.items.gui.*;
import me.lukario.dreamRealm2.items.guns_and_crates.Medkit;
import me.lukario.dreamRealm2.items.guns_and_crates.guns.*;
import me.lukario.dreamRealm2.items.special.builder.*;
import me.lukario.dreamRealm2.items.special.magic.*;
import me.lukario.dreamRealm2.items.special.ranged.bow.*;
import me.lukario.dreamRealm2.items.special.ranged.misc.*;
import me.lukario.dreamRealm2.items.swords.*;
import me.lukario.dreamRealm2.items.special.Clock;
import me.lukario.dreamRealm2.items.special.JetSu;
import me.lukario.dreamRealm2.items.special.ranged.misc.MidasStaff;
import me.lukario.dreamRealm2.items.swords.cultivation.JadeSword;
import me.lukario.dreamRealm2.items.swords.cultivation.RubySword;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class DreamRealm2 extends JavaPlugin {

    private ProtocolManager protocolManager;
    private ShopGUI shopGUI;
    private TeleportGUI teleportGUI;
    private EnderChest EnderChest;

    @Override
    public void onEnable() {
        System.out.println("The spell initialized");
        System.out.println("----------------------------------------");
        System.out.println("---                                  ---");
        System.out.println("---                                  ---");
        System.out.println("---                                  ---");
        System.out.println("---      Welcome to Dream Realm      ---");
        System.out.println("---                                  ---");
        System.out.println("---                                  ---");
        System.out.println("---                                  ---");
        System.out.println("----------------------------------------");


        getServer().getPluginManager().registerEvents(new EntityListener(),this);
        getServer().getPluginManager().registerEvents(new DoubleJump(),this);
        getServer().getPluginManager().registerEvents(new Ak(this),this);
        getServer().getPluginManager().registerEvents(new CustomSword(), this);
        getServer().getPluginManager().registerEvents(new CookWithSneak(), this);
        getServer().getPluginManager().registerEvents(new Aug(this), this);
        getServer().getPluginManager().registerEvents(new Remington(this), this);
        getServer().getPluginManager().registerEvents(new ParticleAccelerator(this), this);
        getServer().getPluginManager().registerEvents(new Mp5(this), this);
        getServer().getPluginManager().registerEvents(new Lmg(this), this);
        getServer().getPluginManager().registerEvents(new RubySword(this), this);
        getServer().getPluginManager().registerEvents(new JadeSword(this), this);
        getServer().getPluginManager().registerEvents(new M24(this), this);
        getServer().getPluginManager().registerEvents(new GiantSword(), this);
        getServer().getPluginManager().registerEvents(new Glock(this), this);
        getServer().getPluginManager().registerEvents(new Medkit(this), this);
        getServer().getPluginManager().registerEvents(new Mech(this), this);
        getServer().getPluginManager().registerEvents(new LaserEye(this), this);
        getServer().getPluginManager().registerEvents(new CAS(this), this);
        getServer().getPluginManager().registerEvents(new CommandGUI(this), this);
        getServer().getPluginManager().registerEvents(new SkillsGUI(this), this);
        getServer().getPluginManager().registerEvents(new Jet(this), this);
        getServer().getPluginManager().registerEvents(new GUIItem(this), this);
        getServer().getPluginManager().registerEvents(new Freja(this), this);
        getServer().getPluginManager().registerEvents(new Stack(this), this);
        getServer().getPluginManager().registerEvents(new FireCracker(this), this);
        getServer().getPluginManager().registerEvents(new SphereCage(this), this);
        getServer().getPluginManager().registerEvents(new GraveYard(this), this);
        getServer().getPluginManager().registerEvents(new Missile(this), this);
        getServer().getPluginManager().registerEvents(new Claws(this), this);
        getServer().getPluginManager().registerEvents(new Terminator(this), this);
        getServer().getPluginManager().registerEvents(new Satellite(this), this);
        getServer().getPluginManager().registerEvents(new Swift(this), this);
        getServer().getPluginManager().registerEvents(new Link(this), this);
        getServer().getPluginManager().registerEvents(new Meteor(this), this);
        getServer().getPluginManager().registerEvents(new YetiSword(this), this);
        getServer().getPluginManager().registerEvents(new MidasStaff(this), this);
        getServer().getPluginManager().registerEvents(new Swipe(this), this);
        getServer().getPluginManager().registerEvents(new Style(this), this);
        getServer().getPluginManager().registerEvents(new Slash(this), this);
        getServer().getPluginManager().registerEvents(new ChainedBuff(this), this);
        getServer().getPluginManager().registerEvents(new Chain(this), this);
        getServer().getPluginManager().registerEvents(new Portal(this), this);
        getServer().getPluginManager().registerEvents(new ShareHealth(this), this);
        getServer().getPluginManager().registerEvents(new Fence(this), this);
        getServer().getPluginManager().registerEvents(new Flash(this), this);
        getServer().getPluginManager().registerEvents(new Theather(this), this);
        getServer().getPluginManager().registerEvents(new FireWand(this), this);
        getServer().getPluginManager().registerEvents(new Wrench(this), this);
        getServer().getPluginManager().registerEvents(new FlameThrower(this), this);
        getServer().getPluginManager().registerEvents(new Katana(this), this);
        getServer().getPluginManager().registerEvents(new me.lukario.dreamRealm2.items.swords.Dagger(this), this);
        getServer().getPluginManager().registerEvents(new TornadoBow(this), this);
        getServer().getPluginManager().registerEvents(new Shuriken(this), this);
        getServer().getPluginManager().registerEvents(new JetSu(this), this);
        getServer().getPluginManager().registerEvents(new Cards(this), this);
        getServer().getPluginManager().registerEvents(new Clock(this), this);
        getServer().getPluginManager().registerEvents(new me.lukario.dreamRealm2.items.swords.Hyperion(this), this);
        getServer().getPluginManager().registerEvents(new me.lukario.dreamRealm2.items.swords.Scythe(this), this);
        getServer().getPluginManager().registerEvents(new WizardWand(this), this);
        getServer().getPluginManager().registerEvents(new me.lukario.dreamRealm2.items.swords.SinOfSolace(this), this);
        getServer().getPluginManager().registerEvents(new Arch(this), this);
        getServer().getPluginManager().registerEvents(new me.lukario.dreamRealm2.items.swords.ShadowDance(this), this);
        getServer().getPluginManager().registerEvents(new me.lukario.dreamRealm2.items.swords.DualWield(this), this);
        getServer().getPluginManager().registerEvents(new Pyromancer(this), this);
        getServer().getPluginManager().registerEvents(new JujuShortBow(this), this);
        getServer().getPluginManager().registerEvents(new me.lukario.dreamRealm2.items.swords.Rapier(this), this);
        getServer().getPluginManager().registerEvents(new Ferocity(), this);

        getCommand("get").setExecutor(new GetCommand());
        getCommand("sum").setExecutor(new SumCommand());
        getCommand("damagetest").setExecutor(new DamageTest());

        protocolManager = ProtocolLibrary.getProtocolManager();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        this.shopGUI = new ShopGUI(this);
        getServer().getPluginManager().registerEvents(shopGUI, this);

        teleportGUI = new TeleportGUI(this);
        getServer().getPluginManager().registerEvents(teleportGUI, this);

        this.EnderChest = new EnderChest(this);
        getServer().getPluginManager().registerEvents(EnderChest, this);
    }

    @Override
    public void onDisable() {
        shopGUI.saveData();
        teleportGUI.saveData();
        System.out.println("--- You have left the Dream Realm ---");

        NamespacedKey key = new NamespacedKey(this, "ToBeRemoved"); // same key used when tagging

        // Loop through all worlds
        for (var world : getServer().getWorlds()) {
            // Loop through all entities in each world
            for (var entity : world.getEntities()) {
                // Check if entity is an ArmorStand and has the tag
                if (entity instanceof ArmorStand stand) {
                    if (stand.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
                        stand.remove(); // remove the armor stand
                    }
                }
            }
        }
    }
}
