package net.crazysnailboy.mods.villagerinventory.util;


import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindClassException;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindMethodException;


public class ReflectionHelper
{

	/**
	 * Obtain a class by name.
	 * Simplified version of {@link net.minecraftforge.fml.relauncher.ReflectionHelper#getClass(ClassLoader, String...)}
	 */
	public static final Class<?> getClass(String... classNames)
	{
		Exception failed = null;
		for (String className : classNames)
		{
			try
			{
				return Class.forName(className);
			}
			catch (Exception ex)
			{
				failed = ex;
			}
		}
		throw new UnableToFindClassException(classNames, failed);
	}


	/**
	 * Obtain a field by name from class declaringClass.
	 * Wrapper around {@link net.minecraftforge.fml.relauncher.ReflectionHelper#findField(Class, String...)}, included for convenience/competeness.
	 */
	public static final Field getDeclaredField(final Class<?> declaringClass, String... fieldNames)
	{
		return net.minecraftforge.fml.relauncher.ReflectionHelper.findField(declaringClass, fieldNames);
	}

	public static final Field getDeclaredField(final String className, String... fieldNames)
	{
		return net.minecraftforge.fml.relauncher.ReflectionHelper.findField(getClass(className), fieldNames);
	}


	/**
	 * Obtain an array of all the static fields of a given type from class declaringClass.
	 */
	public static final <T> T[] getDeclaredFields(final Class<T> type, final Class<?> declaringClass)
	{
		final List<T> result = new ArrayList<T>();
		for (Field field : declaringClass.getDeclaredFields())
		{
			final int modifiers = field.getModifiers();
			if (field.getType().isAssignableFrom(type) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers))
			{
				try
				{
					result.add((T)field.get(null));
				}
				catch (Exception ex)
				{
					throw new UnableToAccessFieldException(new String[0], ex);
				}
			}
		}
		return result.toArray((T[])Array.newInstance(type, result.size()));
	}


	/**
	 * Gets the generic type parameter from an interface of type interfaceClass declared on class declaringClass.
	 */
	public static final Type getGenericInterfaceType(final Class<?> declaringClass, final Class<?> interfaceClass)
	{
		for (Type type : declaringClass.getGenericInterfaces())
		{
			if (type instanceof ParameterizedType)
			{
				final ParameterizedType parameterizedType = (ParameterizedType)type;
				if (parameterizedType.getRawType() == interfaceClass)
				{
					return parameterizedType.getActualTypeArguments()[0];
				}
			}
		}
		return null;
	}


	/**
	 * Obtain a Method by name from class declaringClass.
	 * Simplified version of {@link net.minecraftforge.fml.relauncher.ReflectionHelper#findMethod(Class, Object, String[], Class...)}.
	 */
	public static final Method getDeclaredMethod(final Class<?> declaringClass, String[] methodNames, Class<?>... parameterTypes)
	{
		Exception failed = null;
		for (String methodName : methodNames)
		{
			try
			{
				Method method = declaringClass.getDeclaredMethod(methodName, parameterTypes);
				method.setAccessible(true);
				return method;
			}
			catch (Exception ex)
			{
				failed = ex;
			}
		}
		throw new UnableToFindMethodException(methodNames, failed);
	}

	public static final Method getDeclaredMethod(final String className, String[] methodNames, Class<?>... parameterTypes)
	{
		return getDeclaredMethod(getClass(className), methodNames, parameterTypes);
	}


	/**
	 * Obtain the value of Field fieldToAccess from an Object instance.
	 */
	public static final <T, E> T getFieldValue(final Field fieldToAccess, E instance)
	{
		try
		{
			return (T)fieldToAccess.get(instance);
		}
		catch (Exception ex)
		{
			throw new UnableToAccessFieldException(fieldToAccess, ex);
		}
	}

	/**
	 * Set the value of Field fieldToAccess on an Object instance.
	 */
	public static final <T, E> void setFieldValue(final Field fieldToAccess, E instance, T value)
	{
		try
		{
			fieldToAccess.set(instance, value);
		}
		catch (Exception ex)
		{
			throw new UnableToAccessFieldException(fieldToAccess, ex);
		}
	}


	/**
	 * Invoke Method methodToAccess on an Object instance.
	 */
	public static final <T, E> T invokeMethod(final Method methodToAccess, E instance, Object... args)
	{
		try
		{
			if (methodToAccess.getReturnType().equals(Void.TYPE))
			{
				methodToAccess.invoke(instance, args);
				return null;
			}
			else
			{
				return (T)methodToAccess.invoke(instance, args);
			}
		}
		catch (Exception ex)
		{
			throw new UnableToInvokeMethodException(methodToAccess, ex);
		}
	}

	public static final <T, E> T invokeMethod(final Method methodToAccess, E instance)
	{
		return invokeMethod(methodToAccess, instance, (Object[])null);
	}


	public static class UnableToInvokeMethodException extends RuntimeException
	{
		public UnableToInvokeMethodException(final Method method, Exception ex)
		{
			super(ex);
		}
	}

	public static class UnableToAccessFieldException extends net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToAccessFieldException
	{
		public UnableToAccessFieldException(final String[] fieldNames, Exception ex)
		{
			super(fieldNames, ex);
		}

		public UnableToAccessFieldException(final Field field, Exception ex)
		{
			this(new String[] { field.getName() }, ex);
		}
	}

}