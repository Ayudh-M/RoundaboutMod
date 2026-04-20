package net.hydra.jojomod.stand.powers;

import com.google.common.collect.Lists;
import net.hydra.jojomod.client.StandIcons;
import net.hydra.jojomod.entity.ModEntities;
import net.hydra.jojomod.entity.stand.StandEntity;
import net.hydra.jojomod.event.AbilityIconInstance;
import net.hydra.jojomod.event.index.SoundIndex;
import net.hydra.jojomod.event.powers.StandPowers;
import net.hydra.jojomod.stand.powers.presets.NewPunchingStand;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class PowersWeatherReport extends NewPunchingStand {
    public PowersWeatherReport(LivingEntity self) {
        super(self);
    }

    @Override
    public StandPowers generateStandPowers(LivingEntity entity) {
        return new PowersWeatherReport(entity);
    }

    @Override
    public StandEntity getNewStandEntity() {
        return ModEntities.THE_WORLD.create(this.getSelf().level());
    }

    @Override
    protected Byte getSummonSound() {
        return SoundIndex.SUMMON_SOUND;
    }

    @Override
    public List<AbilityIconInstance> drawGUIIcons(GuiGraphics context, float delta, int mouseX, int mouseY,
                                                  int leftPos, int topPos, byte level, boolean bypass) {
        List<AbilityIconInstance> icons = Lists.newArrayList();

        icons.add(drawSingleGUIIcon(context, 18, leftPos + 20, topPos + 80, 0,
                "ability.roundabout.punch",
                "instruction.roundabout.press_attack",
                StandIcons.THE_WORLD_PUNCH, 0, level, bypass));

        icons.add(drawSingleGUIIcon(context, 18, leftPos + 20, topPos + 99, 0,
                "ability.roundabout.guard",
                "instruction.roundabout.hold_block",
                StandIcons.THE_WORLD_GUARD, 0, level, bypass));

        icons.add(drawSingleGUIIcon(context, 18, leftPos + 39, topPos + 80, 0,
                "ability.roundabout.barrage",
                "instruction.roundabout.barrage",
                StandIcons.THE_WORLD_BARRAGE, 0, level, bypass));

        icons.add(drawSingleGUIIcon(context, 18, leftPos + 58, topPos + 80, 0,
                "ability.roundabout.dodge",
                "instruction.roundabout.press_skill",
                StandIcons.DODGE, 3, level, bypass));

        return icons;
    }
}