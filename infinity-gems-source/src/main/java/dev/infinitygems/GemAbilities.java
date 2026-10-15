package dev.infinitygems;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Alle Fähigkeiten der Gems. Läuft ausschliesslich auf dem Server. */
public final class GemAbilities {
    private GemAbilities() {
    }

    /** @return true, wenn die Fähigkeit ausgelöst wurde (dann startet der Cooldown). */
    public static boolean activate(Gem gem, ServerLevel level, ServerPlayer player) {
        return switch (gem) {
            case RUBY -> ruby(level, player);
            case SAPPHIRE -> sapphire(level, player);
            case EMERALD -> emerald(level, player);
            case AMETHYST -> amethyst(level, player);
            case TOPAZ -> topaz(level, player);
            case VOID -> voidGem(level, player);
            case INFINITY -> infinity(level, player);
        };
    }

    // ---------------------------------------------------------------- Rubin

    private static boolean ruby(ServerLevel level, ServerPlayer player) {
        shootFireballs(level, player);
        sound(level, player, SoundEvents.BLAZE_SHOOT, 1.0f, 1.0f);
        burst(level, player.position().add(0, 1.0, 0), ParticleTypes.FLAME, 25, 0.4);
        return true;
    }

    private static void shootFireballs(ServerLevel level, ServerPlayer player) {
        Vec3 look = player.getLookAngle();
        for (float angle : new float[] { -0.14f, 0f, 0.14f }) {
            Vec3 dir = look.yRot(angle);
            SmallFireball fireball = new SmallFireball(level, player, dir.scale(1.3));
            fireball.setPos(player.getX() + dir.x, player.getEyeY() + dir.y - 0.2, player.getZ() + dir.z);
            level.addFreshEntity(fireball);
        }
    }

    // --------------------------------------------------------------- Saphir

    private static boolean sapphire(ServerLevel level, ServerPlayer player) {
        frostWave(level, player, 8.0, 100, 4.0f);
        sound(level, player, SoundEvents.GLASS_BREAK, 1.0f, 0.6f);
        ring(level, player.position().add(0, 0.2, 0), 2.0, ParticleTypes.SNOWFLAKE);
        ring(level, player.position().add(0, 0.6, 0), 4.5, ParticleTypes.SNOWFLAKE);
        ring(level, player.position().add(0, 1.0, 0), 7.0, ParticleTypes.SNOWFLAKE);
        return true;
    }

    private static void frostWave(ServerLevel level, ServerPlayer player, double radius, int slowTicks, float damage) {
        for (LivingEntity target : enemiesAround(level, player, radius)) {
            // Slowness V = praktisch eingefroren
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, slowTicks, 4));
            target.hurtServer(level, level.damageSources().playerAttack(player), damage);
            burst(level, target.position().add(0, target.getBbHeight() / 2, 0), ParticleTypes.SNOWFLAKE, 12, 0.4);
        }
    }

    // -------------------------------------------------------------- Smaragd

    private static boolean emerald(ServerLevel level, ServerPlayer player) {
        healAround(level, player, 8.0, 8.0f, 200);
        sound(level, player, SoundEvents.PLAYER_LEVELUP, 1.0f, 1.4f);
        burst(level, player.position().add(0, 1.0, 0), ParticleTypes.HAPPY_VILLAGER, 40, 0.8);
        ring(level, player.position().add(0, 0.3, 0), 3.0, ParticleTypes.HAPPY_VILLAGER);
        return true;
    }

    private static void healAround(ServerLevel level, ServerPlayer player, double radius, float amount, int regenTicks) {
        player.heal(amount);
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, regenTicks, 1));

        List<Player> allies = level.getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(radius),
                p -> p != player && p.isAlive());
        for (Player ally : allies) {
            ally.heal(amount * 0.75f);
            ally.addEffect(new MobEffectInstance(MobEffects.REGENERATION, regenTicks, 1));
            burst(level, ally.position().add(0, 1.0, 0), ParticleTypes.HAPPY_VILLAGER, 20, 0.5);
        }
    }

    // ------------------------------------------------------------- Amethyst

    private static boolean amethyst(ServerLevel level, ServerPlayer player) {
        Vec3 from = player.position().add(0, 1.0, 0);
        if (!blink(level, player, 12.0)) {
            return false;
        }
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, 200, 2));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 200, 2));
        sound(level, player, SoundEvents.LIGHTNING_BOLT_THUNDER, 0.4f, 1.6f);
        burst(level, from, ParticleTypes.ELECTRIC_SPARK, 40, 0.6);
        burst(level, player.position().add(0, 1.0, 0), ParticleTypes.ELECTRIC_SPARK, 40, 0.6);
        burst(level, player.position().add(0, 1.0, 0), ParticleTypes.WITCH, 25, 0.6);
        return true;
    }

    // ---------------------------------------------------------------- Topas

    private static boolean topaz(ServerLevel level, ServerPlayer player) {
        // Stärke II = deutlich mehr Nahkampfschaden, dazu Widerstand II
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 300, 1));
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 300, 1));
        sound(level, player, SoundEvents.ANVIL_PLACE, 0.8f, 0.7f);
        burst(level, player.position().add(0, 1.0, 0), ParticleTypes.TOTEM_OF_UNDYING, 40, 0.6);
        ring(level, player.position().add(0, 0.3, 0), 1.5, ParticleTypes.CRIT);
        return true;
    }

    // ------------------------------------------------------------- Void-Gem

    private static boolean voidGem(ServerLevel level, ServerPlayer player) {
        Vec3 from = player.position().add(0, 1.0, 0);
        if (!blink(level, player, 16.0)) {
            return false;
        }
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 200, 0));
        sound(level, player, SoundEvents.ENDERMAN_TELEPORT, 1.0f, 0.7f);
        burst(level, from, ParticleTypes.PORTAL, 60, 0.6);
        burst(level, player.position().add(0, 1.0, 0), ParticleTypes.PORTAL, 60, 0.6);
        burst(level, player.position().add(0, 1.0, 0), ParticleTypes.WITCH, 25, 0.6);
        return true;
    }

    // ----------------------------------------------------------- Infinity Gem

    private static boolean infinity(ServerLevel level, ServerPlayer player) {
        final int ticks = 600; // 30 Sekunden Infinity-Modus

        // Smaragd: Heilung für alle in der Nähe
        healAround(level, player, 10.0, 10.0f, ticks);

        // Saphir: Frostwelle
        frostWave(level, player, 10.0, 100, 6.0f);

        // Rubin: Gegner in der Nähe brennen
        for (LivingEntity target : enemiesAround(level, player, 10.0)) {
            target.igniteForSeconds(8.0f);
        }

        // Amethyst + Topas + Void + Rubin-Passiv: Buffs für den Spieler
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, ticks, 2));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, ticks, 2));
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, ticks, 1));
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, ticks, 1));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, ticks, 0));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, ticks, 2));
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 200, 0));

        // Auftritt
        Vec3 center = player.position().add(0, 1.0, 0);
        sound(level, player, SoundEvents.BEACON_ACTIVATE, 1.5f, 1.0f);
        sound(level, player, SoundEvents.LIGHTNING_BOLT_THUNDER, 0.8f, 1.2f);
        ParticleOptions[] all = { ParticleTypes.FLAME, ParticleTypes.SNOWFLAKE, ParticleTypes.HAPPY_VILLAGER,
                ParticleTypes.ELECTRIC_SPARK, ParticleTypes.TOTEM_OF_UNDYING, ParticleTypes.PORTAL };
        for (int i = 0; i < all.length; i++) {
            ring(level, center.add(0, -0.7 + i * 0.35, 0), 2.0 + i * 1.2, all[i]);
            burst(level, center, all[i], 30, 0.9);
        }
        return true;
    }

    // ------------------------------------------------------------ Hilfsmethoden

    private static List<LivingEntity> enemiesAround(ServerLevel level, ServerPlayer player, double radius) {
        AABB box = player.getBoundingBox().inflate(radius);
        return level.getEntitiesOfClass(LivingEntity.class, box,
                e -> e != player && e.isAlive() && e instanceof Enemy);
    }

    /** Teleportiert in Blickrichtung zu einer sicheren Stelle. Gibt false zurück, wenn keine gefunden wurde. */
    private static boolean blink(ServerLevel level, ServerPlayer player, double maxDistance) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 end = eye.add(look.scale(maxDistance));

        BlockHitResult hit = level.clip(
                new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        double reach = hit.getType() == HitResult.Type.MISS
                ? maxDistance
                : Math.max(0, eye.distanceTo(hit.getLocation()) - 0.6);

        for (double d = reach; d >= 1.5; d -= 0.5) {
            Vec3 offset = look.scale(d);
            AABB moved = player.getBoundingBox().move(offset.x, offset.y, offset.z);
            if (!level.noCollision(player, moved)) {
                continue;
            }
            Vec3 target = player.position().add(offset);
            if (!isSafe(level, BlockPos.containing(target))) {
                continue;
            }
            if (player.randomTeleport(target.x, target.y, target.z, true)) {
                return true;
            }
        }
        return false;
    }

    /** Sicher = innerhalb von 4 Blöcken unter dem Ziel gibt es festen Boden, und weder Lava noch Feuer. */
    private static boolean isSafe(ServerLevel level, BlockPos feet) {
        for (int i = 0; i <= 4; i++) {
            BlockState state = level.getBlockState(feet.below(i));
            if (state.getFluidState().is(FluidTags.LAVA) || state.is(BlockTags.FIRE)
                    || state.is(Blocks.MAGMA_BLOCK) || state.is(Blocks.CACTUS)) {
                return false;
            }
            if (!state.isAir() && !state.getFluidState().isEmpty()) {
                return true; // Wasser als Landung ist ok
            }
            if (!state.getCollisionShape(level, feet.below(i)).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static void sound(ServerLevel level, ServerPlayer player, SoundEvent event, float volume, float pitch) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(), event, SoundSource.PLAYERS, volume, pitch);
    }

    private static void burst(ServerLevel level, Vec3 pos, ParticleOptions particle, int count, double spread) {
        level.sendParticles(particle, pos.x, pos.y, pos.z, count, spread, spread, spread, 0.05);
    }

    private static void ring(ServerLevel level, Vec3 center, double radius, ParticleOptions particle) {
        int points = 48;
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            level.sendParticles(particle, center.x + Math.cos(angle) * radius, center.y,
                    center.z + Math.sin(angle) * radius, 1, 0, 0.05, 0, 0.01);
        }
    }
}
