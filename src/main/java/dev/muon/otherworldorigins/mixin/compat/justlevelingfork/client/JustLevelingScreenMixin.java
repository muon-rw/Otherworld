package dev.muon.otherworldorigins.mixin.compat.justlevelingfork.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.seniors.justlevelingfork.client.screen.JustLevelingScreen;
import com.seniors.justlevelingfork.registry.aptitude.Aptitude;
import dev.muon.otherworldorigins.OtherworldOrigins;
import dev.muon.otherworldorigins.network.CheckFeatScreenMessage;
import dev.muon.otherworldorigins.power.InnateAptitudeBonusPower;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JustLevelingScreen.class, remap = false)
public class JustLevelingScreenMixin {
    @Unique
    private Aptitude otherworld$iteratingAptitude;

    @Unique
    private Aptitude otherworld$selectedAptitude;

    @ModifyExpressionValue(
            method = "drawAptitudes",
            at = @At(value = "FIELD",
                    target = "Lcom/seniors/justlevelingfork/handler/HandlerCommonConfig;aptitudeMaxLevel:I")
    )
    private int modifyMaxLevelMainPage(int originalMaxLevel) {
        if (this.otherworld$iteratingAptitude == null) {
            return originalMaxLevel;
        }
        return otherworld$getModifiedMaxLevel(originalMaxLevel, this.otherworld$iteratingAptitude);
    }

    @ModifyExpressionValue(
            method = "drawSkills",
            at = @At(value = "FIELD",
                    target = "Lcom/seniors/justlevelingfork/handler/HandlerCommonConfig;aptitudeMaxLevel:I")
    )
    private int modifyMaxLevelSkillsPage(int originalMaxLevel) {
        if (this.otherworld$selectedAptitude == null) {
            return originalMaxLevel;
        }
        return otherworld$getModifiedMaxLevel(originalMaxLevel, this.otherworld$selectedAptitude);
    }

    @Unique
    private int otherworld$getModifiedMaxLevel(int originalMaxLevel, Aptitude aptitude) {
        String aptitudeName = aptitude.getName();
        int bonus = InnateAptitudeBonusPower.getBonus(Minecraft.getInstance().player, aptitudeName);
        int newMaxLevel = originalMaxLevel + bonus;
        //OtherworldOrigins.LOGGER.info("Aptitude: {}, Original max level: {}, Bonus: {}, New max level: {}", aptitudeName, originalMaxLevel, bonus, newMaxLevel);
        return newMaxLevel;
    }



    @WrapOperation(
            method = "drawAptitudes",
            at = @At(value = "INVOKE",
                    target = "Lcom/seniors/justlevelingfork/registry/aptitude/Aptitude;getKey()Ljava/lang/String;")
    )
    private String captureCurrentAptitude(Aptitude aptitude, Operation<String> original) {
        this.otherworld$iteratingAptitude = aptitude;
        return original.call(aptitude);
    }

    @WrapOperation(
            method = "drawSkills",
            at = @At(value = "INVOKE",
                    target = "Lcom/seniors/justlevelingfork/registry/RegistryAptitudes;getAptitude(Ljava/lang/String;)Lcom/seniors/justlevelingfork/registry/aptitude/Aptitude;")
    )
    private Aptitude captureSelectedAptitude(String selectedAptitude, Operation<Aptitude> original) {
        Aptitude aptitude = original.call(selectedAptitude);
        this.otherworld$selectedAptitude = aptitude;
        return aptitude;
    }

    @Inject(method = "drawSkills", at = @At(value = "INVOKE", target = "Lcom/seniors/justlevelingfork/network/packet/common/AptitudeLevelUpSP;send(Lcom/seniors/justlevelingfork/registry/aptitude/Aptitude;)V"))
    private void onAptitudeLevelUp(GuiGraphics matrixStack, int x, int y, int mouseX, int mouseY, CallbackInfo ci) {
        LocalPlayer player = ((JustLevelingScreen) (Object) this).getMinecraft().player;
        if (player != null) {
            OtherworldOrigins.CHANNEL.sendToServer(new CheckFeatScreenMessage());
        }

    }
}