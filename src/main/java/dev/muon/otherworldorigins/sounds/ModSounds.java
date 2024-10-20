package dev.muon.otherworldorigins.sounds;

import dev.muon.otherworldorigins.OtherworldOrigins;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;



public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, OtherworldOrigins.MODID);
    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
    public static RegistryObject<SoundEvent> ZHH_WOO_VOOP = registerSoundEvents("zhh_woo_voop");
    public static RegistryObject<SoundEvent> ZHH_WOO_VOOP_EARLY = registerSoundEvents("zhh_woo_voop_early");
    public static RegistryObject<SoundEvent> CRONCH = registerSoundEvents("cronch");
    public static RegistryObject<SoundEvent> DASH = registerSoundEvents("dash");
    public static RegistryObject<SoundEvent> VALKYRIE_CHANNEL = registerSoundEvents("valkyrie_channel");
    public static RegistryObject<SoundEvent> VALKYRIE_LAND = registerSoundEvents("valkyrie_land");
    public static RegistryObject<SoundEvent> DIVINE_SMITE = registerSoundEvents("divine_smite");

    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(OtherworldOrigins.loc(name)));
    }
}