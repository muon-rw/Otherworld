package dev.muon.otherworldorigins.entity;

import dev.muon.otherworldorigins.OtherworldOrigins;
import dev.muon.otherworldorigins.entity.summons.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


@Mod.EventBusSubscriber(modid = OtherworldOrigins.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, OtherworldOrigins.MODID);
    static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String name, EntityType.Builder<T> builder) {
        return ENTITY_TYPES.register(name, () -> builder.build(OtherworldOrigins.MODID + ":" + name));
    }

    /**
     *
     * Summons
     */
    public static final RegistryObject<EntityType<SummonedSkeleton>> SUMMON_SKELETON = registerEntity("summon_skeleton",
            EntityType.Builder.<SummonedSkeleton>of(SummonedSkeleton::new, MobCategory.CREATURE).sized(1.0F, 1.8F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<SummonedZombie>> SUMMON_ZOMBIE = registerEntity("summon_zombie",
            EntityType.Builder.<SummonedZombie>of(SummonedZombie::new, MobCategory.CREATURE).sized(1.0F, 1.8F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<SummonedWitherSkeleton>> SUMMON_WITHER_SKELETON = registerEntity("summon_wither_skeleton",
            EntityType.Builder.<SummonedWitherSkeleton>of(SummonedWitherSkeleton::new, MobCategory.CREATURE).sized(1.0F, 2.1F).clientTrackingRange(10));

    public static final RegistryObject<EntityType<SummonedGrizzlyBear>> SUMMON_GRIZZLY_BEAR = registerEntity("summon_grizzly_bear",
            EntityType.Builder.<SummonedGrizzlyBear>of(SummonedGrizzlyBear::new, MobCategory.CREATURE).sized(1.4F, 1.4F).clientTrackingRange(10));

    public static final RegistryObject<EntityType<SummonedIronGolem>> SUMMON_IRON_GOLEM = registerEntity("summon_iron_golem",
            EntityType.Builder.<SummonedIronGolem>of(SummonedIronGolem::new, MobCategory.CREATURE).sized(2.0F, 2.5F).clientTrackingRange(10));

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(SUMMON_GRIZZLY_BEAR.get(), SummonedGrizzlyBear.createAttributes().build());
    }


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

}
