/*
 * Copyright (c) 2025 macuguita
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.macuguita.obese_crops.mixin;

import java.util.Optional;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.macuguita.obese_crops.common.reg.OCComponents;
import com.macuguita.obese_crops.common.reg.OCItemTags;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Definition(id = "crit", method = "Lnet/minecraft/world/entity/player/Player;crit(Lnet/minecraft/world/entity/Entity;)V")
	@Definition(id = "target", local = @Local(type = Entity.class, argsOnly = true))
	@Expression("this.crit(target)")
	@WrapOperation(
			method = "attack",
			at = @At("MIXINEXTRAS:EXPRESSION")
	)
	private void obese_crops$attack(
			Player instance,
			Entity entityHit,
			Operation<Void> original,
			@Local(type = ItemStack.class, ordinal = 0) @NotNull ItemStack itemStack
	) {
		if (itemStack.is(OCItemTags.SCYTHES)) {
			double strength = 1.0D;
			if (entityHit instanceof LivingEntity livingEntity) {
				if (itemStack.has(OCComponents.PULLING_SPEED.get())) {
					Float pullingSpeed = itemStack.get(OCComponents.PULLING_SPEED.get());
					float baseSpeed = Optional.ofNullable(pullingSpeed).orElse(0.0f);
					strength = baseSpeed * (float) (1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
				}
			}
			entityHit.setDeltaMovement(this.position().subtract(entityHit.position()).scale(strength));
			entityHit.hurtMarked = true;
		}
	}

	@Definition(id = "bl4", local = @Local(type = boolean.class, ordinal = 3))
	@Expression("bl4")
	@ModifyExpressionValue(
			method = "attack",
			at = @At("MIXINEXTRAS:EXPRESSION")
	)
	private boolean obese_crops$attack(
			boolean original,
			@Local(type = ItemStack.class, ordinal = 0) @NotNull ItemStack itemStack
	) {
		return original || itemStack.is(OCItemTags.SCYTHES);
	}
}
