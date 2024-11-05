package dev.muon.otherworldorigins;

import dev.muon.otherworldorigins.entity.ModEntities;
import dev.muon.otherworldorigins.entity.summons.*;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OtherworldOrigins.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.SUMMONED_IRON_GOLEM.get(), SummonedIronGolem.createAttributes().build());
    }

}
