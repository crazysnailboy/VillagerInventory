package net.crazysnailboy.mods.villagerinventory.util;

import java.lang.reflect.Method;
import net.minecraft.entity.Entity;


public class EntityUtils
{

	private static final Method getFlag = ReflectionHelper.getDeclaredMethod(Entity.class, new String[] { "getFlag", "func_70083_f" }, int.class);
	private static final Method setFlag = ReflectionHelper.getDeclaredMethod(Entity.class, new String[] { "setFlag", "func_70052_a" }, int.class, boolean.class);


	/**
	 * Returns true if the flag is active for the entity.
	 * Uses reflection to call {@link net.minecraft.entity.Entity#getFlag(int)};
	 * Known flags: 0: burning; 1: sneaking; 2: unused; 3: sprinting; 4: unused; 5: invisible; 6: glowing; 7: elytra flying
	 */
	public static boolean getFlag(Entity entity, int flag)
	{
		return ReflectionHelper.invokeMethod(getFlag, entity, flag);
	}

	/**
	 * Enable or disable a entity flag.
	 * Uses reflection to call {@link net.minecraft.entity.Entity#setFlag(int,boolean)};
	 * Known flags: 0: burning; 1: sneaking; 2: unused; 3: sprinting; 4: unused; 5: invisible; 6: glowing; 7: elytra flying
	 */
	public static void setFlag(Entity entity, int flag, boolean value)
	{
		ReflectionHelper.invokeMethod(setFlag, entity, flag, value);
	}

}