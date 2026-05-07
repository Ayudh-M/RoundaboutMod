package net.hydra.jojomod.stand.powers;

import com.google.common.collect.Lists;
import net.hydra.jojomod.client.ClientNetworking;
import net.hydra.jojomod.client.ClientUtil;
import net.hydra.jojomod.client.StandIcons;
import net.hydra.jojomod.entity.ModEntities;
import net.hydra.jojomod.entity.projectile.RoadRollerEntity;
import net.hydra.jojomod.entity.stand.StandEntity;
import net.hydra.jojomod.event.AbilityIconInstance;
import net.hydra.jojomod.event.index.PowerIndex;
import net.hydra.jojomod.event.index.SoundIndex;
import net.hydra.jojomod.event.powers.StandPowers;
import net.hydra.jojomod.stand.powers.presets.NewPunchingStand;
import net.hydra.jojomod.sound.ModSounds;
import net.hydra.jojomod.stand.powers.elements.PowerContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class PowersWeatherReport extends NewPunchingStand {
    public static final byte WIND_DETECTION = PowerIndex.POWER_4_BLOCK;
    public static final int WIND_DETECTION_COLOR = 0x88E8FF;
    public static final double WIND_DETECTION_RANGE = 128.0D;

    public PowersWeatherReport(LivingEntity self) {
        super(self);
    }

    @Override
    public StandPowers generateStandPowers(LivingEntity entity) {
        return new PowersWeatherReport(entity);
    }

    @Override
    public StandEntity getNewStandEntity() {
        return ModEntities.WEATHER_REPORT.create(this.getSelf().level());
    }

    @Override
    protected Byte getSummonSound() {
        return SoundIndex.SUMMON_SOUND;
    }

    @Override
    public byte getMaxLevel() {
        return 10;
    }

    @Override
    public int getExpForLevelUp(int currentLevel) {
        int amount;
        if (currentLevel == 1) {
            amount = 100;
        } else {
            amount = 100 + ((currentLevel - 1) * 65);
        }
        return (int) (amount * getLevelMultiplier());
    }

    @Override
    public float getBarrageDamagePlayer() {
        return 8;
    }

    @Override
    public float getBarrageDamageMob() {
        return 18;
    }

    @Override
    public float getBarrageHitStrength(Entity entity) {
        float strength = super.getBarrageHitStrength(entity);
        if (strength > 0.005F) {
            if (getReducedDamage(entity)) {
                strength *= levelupDamageMod((float) (ClientNetworking.getAppropriateConfig()
                        .softAndWetSettings.softAndWetAttackMultOnPlayers * 0.01));
            } else {
                strength *= levelupDamageMod((float) (ClientNetworking.getAppropriateConfig()
                        .softAndWetSettings.softAndWetAttackMultOnMobs * 0.01));
            }
        }

        if (entity instanceof LivingEntity livingEntity
                && strength >= livingEntity.getHealth()
                && ClientNetworking.getAppropriateConfig().generalStandSettings.barragesOnlyKillOnLastHit) {
            if (entity instanceof Player) {
                strength = 0.00001F;
            } else {
                strength = 0F;
            }
        }
        return strength;
    }

    @Override
    public float getPunchStrength(Entity entity) {
        if (this.getReducedDamage(entity)) {
            return levelupDamageMod(multiplyPowerByStandConfigPlayers(1.45F));
        }
        return levelupDamageMod(multiplyPowerByStandConfigMobs(4.0F));
    }

    @Override
    public float getHeavyPunchStrength(Entity entity) {
        if (this.getReducedDamage(entity)) {
            return levelupDamageMod(multiplyPowerByStandConfigPlayers(2.15F));
        }
        return levelupDamageMod(multiplyPowerByStandConfigMobs(5.0F));
    }

    @Override
    public float multiplyPowerByStandConfigPlayers(float power) {
        return (float) (power * (ClientNetworking.getAppropriateConfig()
                .softAndWetSettings.softAndWetAttackMultOnPlayers * 0.01));
    }

    @Override
    public float multiplyPowerByStandConfigMobs(float power) {
        return (float) (power * (ClientNetworking.getAppropriateConfig()
                .softAndWetSettings.softAndWetAttackMultOnMobs * 0.01));
    }

    @Override
    public void renderIcons(GuiGraphics context, int x, int y) {
        ClientUtil.fx.roundabout$onGUI(context);

        if (isGuarding()) {
            setSkillIcon(context, x, y, 4,
                    windDetectionActive() ? StandIcons.WIND_VISION_ON : StandIcons.WIND_VISION_OFF,
                    PowerIndex.SKILL_4_GUARD);
        }

        setSkillIcon(context, x, y, 3, StandIcons.DODGE, PowerIndex.GLOBAL_DASH);

        super.renderIcons(context, x, y);
    }

    @Override
    public void powerActivate(PowerContext context) {
        switch (context) {
            case SKILL_4_GUARD, SKILL_4_CROUCH_GUARD -> toggleWindDetectionClient();
            default -> super.powerActivate(context);
        }
    }

    @Override
    public boolean setPowerOther(int move, int lastMove) {
        if (move == WIND_DETECTION) {
            return toggleWindDetection();
        }
        return super.setPowerOther(move, lastMove);
    }

    public void toggleWindDetectionClient() {
        this.tryPower(WIND_DETECTION, true);
        tryPowerPacket(WIND_DETECTION);
        if (isClient() && windDetectionActive()) {
            this.self.playSound(ModSounds.MANHATTAN_VISION_EVENT, 200F, 1.0F);
        }
    }

    public boolean toggleWindDetection() {
        if (isClient() && this.self instanceof Player player) {
            getStandUserSelf().roundabout$setUniqueStandModeToggle(!windDetectionActive());
            if (windDetectionActive()) {
                player.displayClientMessage(Component.translatable("text.roundabout.weather_report.wind_detection")
                        .withStyle(ChatFormatting.AQUA), true);
            } else {
                player.displayClientMessage(Component.translatable("text.roundabout.weather_report.wind_detection_off")
                        .withStyle(ChatFormatting.DARK_AQUA), true);
            }
        }
        return true;
    }

    public boolean windDetectionActive() {
        return getStandUserSelf().roundabout$getUniqueStandModeToggle();
    }

    @Override
    public boolean highlightsEntity(Entity ent, Player player) {
        if (!windDetectionActive()
                || ent == null
                || ent.is(player)
                || ent instanceof StandEntity
                || ent instanceof RoadRollerEntity
                || !(ent instanceof LivingEntity livingEntity)
                || !livingEntity.isAlive()
                || livingEntity.isSpectator()) {
            return false;
        }
        return player.distanceToSqr(ent) <= WIND_DETECTION_RANGE * WIND_DETECTION_RANGE;
    }

    @Override
    public int highlightsEntityColor(Entity ent, Player player) {
        return WIND_DETECTION_COLOR;
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

        icons.add(drawSingleGUIIcon(context, 18, leftPos + 77, topPos + 80, 0,
                "ability.roundabout.wind_detection",
                "instruction.roundabout.press_skill_block",
                StandIcons.WIND_VISION_ON, 1, level, bypass));

        return icons;
    }
}
