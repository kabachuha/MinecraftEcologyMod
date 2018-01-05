package ecomod.api.pollution;

import org.apache.commons.lang3.tuple.Pair;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.ChunkPos;

public class ChunkPollution extends Pair<Pair<Integer, Integer>, PollutionData>
{
	private int chunkX;
	private int chunkZ;
	
	
	private PollutionData pollution;
	
	
	public ChunkPollution(){}

	/**
	 * 
	 * @param xPosition <strong><i>Chunk</i></strong> x position
	 * @param zPosition	<strong><i>Chunk</i></strong> z position
	 * @param data 
	 */
	public ChunkPollution(int xPosition, int zPosition, PollutionData data) {
		this();
		chunkX = xPosition;
		chunkZ = zPosition;
		pollution = data;
	}

	public ChunkPollution(Pair<Integer, Integer> pos, PollutionData data) {
		this(pos.getLeft(), pos.getRight(), data);
	}
	
	public ChunkPollution(ChunkPos pos, PollutionData data) {
		this(pos.x, pos.z, data);
	}
	
	/**
	 * @return the chunkX
	 */
	public int getX() {
		return chunkX;
	}

	/**
	 * @param chunkX the chunkX to set
	 */
	public void setX(int chunkX) {
		this.chunkX = chunkX;
	}

	/**
	 * @return the chunkY
	 */
	public int getZ() {
		return chunkZ;
	}

	/**
	 * @param chunkY the chunkY to set
	 */
	public void setZ(int chunkZ) {
		this.chunkZ = chunkZ;
	}

	/**
	 * @return the pollution
	 */
	public PollutionData getPollution() {
		return pollution;
	}

	/**
	 * @param pollution the pollution to set
	 */
	public void setPollution(PollutionData pollution) {
		this.pollution = pollution;
	}
	
	public boolean isEmpty()
	{
		return pollution == null || (pollution.getAirPollution() == 0.0D && pollution.getWaterPollution() == 0.0D && pollution.getSoilPollution() == 0.0D);
	}
	
	public static boolean coordEquals(ChunkPollution f, ChunkPollution s)
	{
		return (f.getX() == s.getX()) && (f.getZ() == s.getZ());
	}
	
	public String toString()
	{
		return "{ \"chunkX\" : "+chunkX+", \"chunkZ\" : "+chunkZ+", \"pollution\" : "+pollution.toString()+"}";
	}
	
	public void writeByteBuf(ByteBuf bb)
	{
		bb.writeInt(chunkX);
		bb.writeInt(chunkZ);
		pollution.writeByteBuf(bb);
	}
	
	public static ChunkPollution fromByteBuf(ByteBuf bb)
	{
		ChunkPollution cp = new ChunkPollution();
		
		cp.chunkX = bb.readInt();
		cp.chunkZ = bb.readInt();
		
		cp.pollution = PollutionData.fromByteBuf(bb);
		
		return cp;
	}

	@Override
	public PollutionData setValue(PollutionData arg0)
	{
		pollution = arg0;
		return pollution;
	}

	@Override
	public Pair<Integer, Integer> getLeft()
	{
		return Pair.of(chunkX, chunkZ);
	}

	@Override
	public PollutionData getRight()
	{
		return pollution;
	}
	
	public ChunkPos getChunkPos()
	{
		return new ChunkPos(chunkX, chunkZ);
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj != null && obj instanceof ChunkPollution)
		{
			ChunkPollution p = (ChunkPollution)obj;
			return chunkX == p.chunkX && chunkZ == p.chunkZ && pollution.equals(p.pollution);
		}
		return false;
	}

	@Override
	public int compareTo(Pair<Pair<Integer, Integer>, PollutionData> other)
	{
		return pollution.compareTo(other.getRight());
	}
	
	
}