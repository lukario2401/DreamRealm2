package me.lukario.dreamRealm2;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.lukario.dreamRealm2.commands.DamageTest;
import me.lukario.dreamRealm2.commands.GetCommand;
import me.lukario.dreamRealm2.commands.SumCommand;
import me.lukario.dreamRealm2.items.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class DreamRealm2 extends JavaPlugin {

    private ProtocolManager protocolManager;
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
        getServer().getPluginManager().registerEvents(new CustomSword(), this);
        getServer().getPluginManager().registerEvents(new CookWithSneak(), this);
        getServer().getPluginManager().registerEvents(new GiantSword(), this);
        getServer().getPluginManager().registerEvents(new Hyperion(this), this);
        getServer().getPluginManager().registerEvents(new Scythe(this), this);
        getServer().getPluginManager().registerEvents(new Rapier(this), this);
        getServer().getPluginManager().registerEvents(new WizardWand(this), this);
        getServer().getPluginManager().registerEvents(new SinOfSolace(this), this);
        getServer().getPluginManager().registerEvents(new Arch(this), this);
        getServer().getPluginManager().registerEvents(new ShadowDance(this), this);
        getServer().getPluginManager().registerEvents(new DualWield(this), this);
        getServer().getPluginManager().registerEvents(new Pyromancer(this), this);
        getServer().getPluginManager().registerEvents(new JujuShortBow(this), this);
        getCommand("get").setExecutor(new GetCommand());
        getCommand("sum").setExecutor(new SumCommand());
        getCommand("damagetest").setExecutor(new DamageTest());

        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onDisable() {
        System.out.println("--- You have left the Dream Realm ---");
    }
}
