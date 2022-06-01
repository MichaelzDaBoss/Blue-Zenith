package cat.module;

import cat.BlueZenith;
import cat.module.modules.combat.*;
import cat.module.modules.fun.*;
import cat.module.modules.hvh.GoldClaimerBot;
import cat.module.modules.hvh.PulsiveBot;
import cat.module.modules.hvh.PulsiveGoldBot;
import cat.module.modules.hvh.RicePitBot;
import cat.module.modules.misc.*;
import cat.module.modules.movement.*;
import cat.module.modules.player.*;
import cat.module.modules.render.*;

import java.util.ArrayList;

public final class ModuleManager {
    private final ArrayList<Module> modules = new ArrayList<>();

    public ModuleManager() {

        // Combat
        modules.add(new Aura());
        modules.add(new Criticals());
        modules.add(new TargetStrafe());
        modules.add(new Velocity());
        modules.add(new AntiBot());

        // Fun
        modules.add(new DamageImpact());
        modules.add(new DeathScreen());
        modules.add(new hampter());
        modules.add(new YesFall());
        modules.add(new AntiBan());
        PulsiveBot pulsiveBot = new PulsiveBot();
        modules.add(pulsiveBot);
        PulsiveGoldBot pulsiveGoldBot = new PulsiveGoldBot();
        modules.add(pulsiveGoldBot);
        modules.add(new GoldClaimerBot());
        modules.add(new RicePitBot());

        // Misc
        modules.add(new AutoRegister());
        modules.add(new FPSLimiter());
        modules.add(new InvMove());
        modules.add(new MemoryFix());
        modules.add(new Spammer());
        modules.add(new StreamerMode());
        modules.add(new Disabler());
        modules.add(new PingSpoof());
        modules.add(new LightningDetector());
        modules.add(new StaffDetector());
        modules.add(new ChatBypass());
        modules.add(new NoRotate());

        // Movement
        modules.add(new AirJump());
        modules.add(new AirWalk());
        modules.add(new Flight());
        modules.add(new LongJump());
        modules.add(new NoSlowDown());
        modules.add(new Speed());
        modules.add(new Sprint());
        modules.add(new Fly());
        modules.add(new CustomFly());
        modules.add(new KeepSprint());

        // Player
        modules.add(new BedNuker());
        modules.add(new ChestStealer());
        modules.add(new FastEat());
        modules.add(new NoFall());
        modules.add(new Regen());
        modules.add(new Scaffold());
        modules.add(new Phase());
        modules.add(new InvManager());
        Blink blink = new Blink();
        modules.add(blink);
        modules.add(new Timer());

        // Render
        modules.add(new Animations());
        modules.add(new AntiBlind());
        modules.add(new CameraClip());
        modules.add(new Chams());
        modules.add(new ClickGUI());
        modules.add(new CustomCape());
        modules.add(new FullBright());
        modules.add(new HUD());
        modules.add(new NameTags());
        modules.add(new NoHurtCam());
        modules.add(new Rotations());

        modules.forEach(Module::loadValues);

        blink.setState(false);
        pulsiveBot.setState(false);
        pulsiveGoldBot.setState(false);
    }

    public ArrayList<Module> getModules(){
        return modules;
    }

    public Module getModule(String name) {
        for (Module m : modules) {
            if(m.getName().equalsIgnoreCase(name)){
                return m;
            }
            for(String alias : m.aliases) {
                if(alias.equalsIgnoreCase(name)) {
                    return m;
                }
            }
        }
        return null;
    }

    public Module getModule(Class<?> clazz) {
       return modules.stream().filter(mod -> mod.getClass() == clazz).findFirst().orElse(null);
    }

    public static Module getModuleClass(Class<?> clazz) {
        return BlueZenith.moduleManager.getModule(clazz);
    }

    public ArrayList<Module> getModules(ModuleCategory category) {
        ArrayList<Module> modules = new ArrayList<>();
        for (Module m : this.modules) {
            if (m.getCategory() == category) {
                modules.add(m);
            }
        }
        return modules;
    }

    public void handleKey(int keyCode){
        for (Module m : modules) {
            if(m.keyBind != 0 && keyCode == m.keyBind){
                m.toggle();
            }
        }
    }
}
