package dev.muon.otherworldorigins.power;

import dev.muon.otherworldorigins.OtherworldOrigins;
import io.github.edwinmindcraft.apoli.api.configuration.HolderConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModPowers {
    public static final DeferredRegister<PowerFactory<?>> POWER_FACTORIES = DeferredRegister.create(ApoliRegistries.POWER_FACTORY_KEY, OtherworldOrigins.MODID);

    public static final RegistryObject<PowerFactory<InnateAptitudeBonusPower.Configuration>> INNATE_APTITUDE_BONUS = POWER_FACTORIES.register("innate_aptitude_bonus", InnateAptitudeBonusPower::new);

    public static final RegistryObject<PowerFactory<HolderConfiguration<ConfiguredEntityAction<?, ?>>>> ACTION_ON_LEVELUP = POWER_FACTORIES.register("action_on_levelup", ActionOnLevelupPower::new);
    public static final RegistryObject<PowerFactory<TradeDiscountPower.Configuration>> TRADE_DISCOUNT = POWER_FACTORIES.register("trade_discount", TradeDiscountPower::new);
    public static final RegistryObject<PowerFactory<ModifyThirstExhaustionPower.Configuration>> MODIFY_THIRST_EXHAUSTION = POWER_FACTORIES.register("modify_thirst_exhaustion", ModifyThirstExhaustionPower::new);
    public static final RegistryObject<PowerFactory<CharismaPower.Configuration>> CHARISMA = POWER_FACTORIES.register("charisma", CharismaPower::new);
    public static final RegistryObject<PowerFactory<LeveledAttributePower.Configuration>> LEVELED_ATTRIBUTE = POWER_FACTORIES.register("leveled_attribute", LeveledAttributePower::new);

    private static <T extends PowerFactory<?>> RegistryObject<T> registerConditional(String name, Supplier<T> factory, String modId) {
        if (ModList.get().isLoaded(modId)) {
            return POWER_FACTORIES.register(name, factory);
        }
        return null;
    }

    public static void register(IEventBus eventBus) {
        POWER_FACTORIES.register(eventBus);
    }
}