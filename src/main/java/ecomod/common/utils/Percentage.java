package ecomod.common.utils;

import net.minecraft.util.math.MathHelper;

public class Percentage extends Number implements Comparable
{
	//Immutable!!!
	private final byte value;
	
	public Percentage()
	{
		value = 0x00;
	}
	
	public Percentage(int percents)
	{
		if(percents < 0)
			percents = 0;
		if(percents > 100)
			percents = 100;
		
		value = (byte)percents;
	}
	
	public Percentage(double decimal)
	{
		this((int)(decimal * 100));
	}
	
	@Override
	public double doubleValue() {
		return MathHelper.clamp((double)value / 100, 0D, 1D);
	}

	@Override
	public float floatValue() {
		return MathHelper.clamp((float)value / 100, 0F, 1F);
	}

	@Override
	public int intValue() {
		return (int)value;
	}

	@Override
	public long longValue() {
		return (long)value;
	}
	
	@Override
	public byte byteValue()
	{
		return value;
	}

	@Override
	public int compareTo(Object o) {
		if(o instanceof Percentage)
		{
			Percentage other = (Percentage)o;
			return Byte.compare(byteValue(), other.byteValue());
		}
		else if(o instanceof Integer || o instanceof Long)//Whole numbers
		{
			Integer other = (Integer)o;
			return intValue() == other ? 0 : intValue() < other ? -1 : 1;
		}
		else if(o instanceof Float || o instanceof Double)//Decimals
		{
			Float other = (Float)o;
			return floatValue() == other ? 0 : floatValue() < other ? -1 : 1;
		}
		return 0;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o == null)
			return false;
		
		if(o instanceof Percentage)
		{
			return value == ((Percentage)o).byteValue();
		}
		else if(o instanceof Integer || o instanceof Long)
		{
			return intValue() == (Integer) o;
		}
		else if(o instanceof Float || o instanceof Double)
		{
			return floatValue() == (Float) o;
		}
		
		return false;
	}
	
	@Override
	public String toString()
	{
		return intValue()+"%";
	}
	
	public Percentage add(Number d)
	{
		return d instanceof Integer || d instanceof Long || d instanceof Percentage || d instanceof Short ? new Percentage(intValue() + d.intValue()) : new Percentage(doubleValue() + d.doubleValue());
	}
	
	public Percentage multiply(float factor)
	{
		return new Percentage((int)(intValue() * factor));
	}
	
	public Percentage multiply(Percentage factor)
	{
		return new Percentage((int)(intValue() * factor.floatValue()));
	}
	
	public static final Percentage ZERO = new Percentage(0);
	public static final Percentage FULL = new Percentage(100);
}
