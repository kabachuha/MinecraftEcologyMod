package ecomod.common.utils.newmc;

import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

//From Minecraft 1.10
public class EMBlockPos extends Vec3
{
    private static final Logger LOGGER = LogManager.getLogger();
    /** An immutable block pos with zero as all coordinates. */
    public static final EMBlockPos ORIGIN = new EMBlockPos(0, 0, 0);
    private static final int NUM_X_BITS = 1 + MathHelper.calculateLogBaseTwo(MathHelper.roundUpToPowerOfTwo(30000000));
    private static final int NUM_Z_BITS = NUM_X_BITS;
    private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
    private static final int Y_SHIFT = 0 + NUM_Z_BITS;
    private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
    private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
    private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
    private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

    public EMBlockPos(int x, int y, int z)
    {
        super(x, y, z);
    }

    public EMBlockPos(double x, double y, double z)
    {
        super(x, y, z);
    }

    public EMBlockPos(Entity source)
    {
        this(source.posX, source.posY, source.posZ);
    }
    
    public EMBlockPos(TileEntity tile)
    {
    	this(tile.xCoord, tile.yCoord, tile.zCoord);
    }

    public EMBlockPos(Vec3 vec)
    {
        this(vec.xCoord, vec.yCoord, vec.zCoord);
    }
    
    public EMBlockPos(ChunkCoordinates pos)
    {
    	this(pos.posX, pos.posY, pos.posZ);
    }
    
    public int getX()
    {
    	return MathHelper.floor_double(xCoord);
    }
    
    public int getY()
    {
    	return MathHelper.floor_double(yCoord);
    }
    
    public int getZ()
    {
    	return MathHelper.floor_double(zCoord);
    }

    /**
     * Add the given coordinates to the coordinates of this BlockPos
     */
    public EMBlockPos add(double x, double y, double z)
    {
        return x == 0.0D && y == 0.0D && z == 0.0D ? this : new EMBlockPos((double)this.getX() + x, (double)this.getY() + y, (double)this.getZ() + z);
    }

    /**
     * Add the given coordinates to the coordinates of this BlockPos
     */
    public EMBlockPos add(int x, int y, int z)
    {
        return x == 0 && y == 0 && z == 0 ? this : new EMBlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    /**
     * Add the given Vector to this BlockPos
     */
    public EMBlockPos add(Vec3 vec)
    {
        return vec.xCoord == 0 && vec.yCoord == 0 && vec.zCoord == 0 ? this : new EMBlockPos(this.getX() + vec.xCoord, this.getY() + vec.yCoord, this.getZ() + vec.zCoord);
    }

    /**
     * Subtract the given Vector from this BlockPos
     */
    public EMBlockPos subtract(Vec3 vec)
    {
        return vec.xCoord == 0 && vec.yCoord == 0 && vec.zCoord == 0 ? this : new EMBlockPos(this.getX() - vec.xCoord, this.getY() - vec.yCoord, this.getZ() - vec.zCoord);
    }

    /**
     * Offset this BlockPos 1 block up
     */
    public EMBlockPos up()
    {
        return this.up(1);
    }

    /**
     * Offset this BlockPos n blocks up
     */
    public EMBlockPos up(int n)
    {
        return this.offset(EnumFacing.UP, n);
    }

    /**
     * Offset this BlockPos 1 block down
     */
    public EMBlockPos down()
    {
        return this.down(1);
    }

    /**
     * Offset this BlockPos n blocks down
     */
    public EMBlockPos down(int n)
    {
        return this.offset(EnumFacing.DOWN, n);
    }

    /**
     * Offset this BlockPos 1 block in northern direction
     */
    public EMBlockPos north()
    {
        return this.north(1);
    }

    /**
     * Offset this BlockPos n blocks in northern direction
     */
    public EMBlockPos north(int n)
    {
        return this.offset(EnumFacing.NORTH, n);
    }

    /**
     * Offset this BlockPos 1 block in southern direction
     */
    public EMBlockPos south()
    {
        return this.south(1);
    }

    /**
     * Offset this BlockPos n blocks in southern direction
     */
    public EMBlockPos south(int n)
    {
        return this.offset(EnumFacing.SOUTH, n);
    }

    /**
     * Offset this BlockPos 1 block in western direction
     */
    public EMBlockPos west()
    {
        return this.west(1);
    }

    /**
     * Offset this BlockPos n blocks in western direction
     */
    public EMBlockPos west(int n)
    {
        return this.offset(EnumFacing.WEST, n);
    }

    /**
     * Offset this BlockPos 1 block in eastern direction
     */
    public EMBlockPos east()
    {
        return this.east(1);
    }

    /**
     * Offset this BlockPos n blocks in eastern direction
     */
    public EMBlockPos east(int n)
    {
        return this.offset(EnumFacing.EAST, n);
    }

    /**
     * Offset this BlockPos 1 block in the given direction
     */
    public EMBlockPos offset(EnumFacing facing)
    {
        return this.offset(facing, 1);
    }
    
    public EMBlockPos offset(ForgeDirection facing)
    {
        return this.offset(facing, 1);
    }
    
    public EMBlockPos offset(ForgeDirection facing, int n)
    {
        return n == 0 ? this : new EMBlockPos(this.getX() + facing.offsetX * n, this.getY() + facing.offsetY * n, this.getZ() + facing.offsetZ * n);
    }

    /**
     * Offsets this BlockPos n blocks in the given direction
     */
    public EMBlockPos offset(EnumFacing facing, int n)
    {
        return n == 0 ? this : new EMBlockPos(this.getX() + facing.getFrontOffsetX() * n, this.getY() + facing.getFrontOffsetY() * n, this.getZ() + facing.getFrontOffsetZ() * n);
    }

    /**
     * Calculate the cross product of this and the given Vector
     */
    public EMBlockPos crossProduct(Vec3 vec)
    {
        return new EMBlockPos(this.getY() * vec.zCoord - this.getZ() * vec.yCoord, this.getZ() * vec.xCoord - this.getX() * vec.zCoord, this.getX() * vec.yCoord - this.getY() * vec.xCoord);
    }

    /**
     * Serialize this BlockPos into a long value
     */
    public long toLong()
    {
        return ((long)this.getX() & X_MASK) << X_SHIFT | ((long)this.getY() & Y_MASK) << Y_SHIFT | ((long)this.getZ() & Z_MASK) << 0;
    }

    /**
     * Create a BlockPos from a serialized long value (created by toLong)
     */
    public static EMBlockPos fromLong(long serialized)
    {
        int i = (int)(serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
        int j = (int)(serialized << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
        int k = (int)(serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
        return new EMBlockPos(i, j, k);
    }

    /**
     * Create an Iterable that returns all positions in the box specified by the given corners
     */
    public static Iterable<EMBlockPos> getAllInBox(EMBlockPos from, EMBlockPos to)
    {
        final EMBlockPos blockpos = new EMBlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        final EMBlockPos blockpos1 = new EMBlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        return new Iterable<EMBlockPos>()
        {
            public Iterator<EMBlockPos> iterator()
            {
                return new AbstractIterator<EMBlockPos>()
                {
                    private EMBlockPos lastReturned;
                    protected EMBlockPos computeNext()
                    {
                        if (this.lastReturned == null)
                        {
                            this.lastReturned = blockpos;
                            return this.lastReturned;
                        }
                        else if (this.lastReturned.equals(blockpos1))
                        {
                            return (EMBlockPos)this.endOfData();
                        }
                        else
                        {
                            int i = this.lastReturned.getX();
                            int j = this.lastReturned.getY();
                            int k = this.lastReturned.getZ();

                            if (i < blockpos1.getX())
                            {
                                ++i;
                            }
                            else if (j < blockpos1.getY())
                            {
                                i = blockpos.getX();
                                ++j;
                            }
                            else if (k < blockpos1.getZ())
                            {
                                i = blockpos.getX();
                                j = blockpos.getY();
                                ++k;
                            }

                            this.lastReturned = new EMBlockPos(i, j, k);
                            return this.lastReturned;
                        }
                    }
                };
            }
        };
    }

    /**
     * Returns a version of this BlockPos that is guaranteed to be immutable.
     *  
     * <p>When storing a BlockPos given to you for an extended period of time, make sure you
     * use this in case the value is changed internally.</p>
     */
    public EMBlockPos toImmutable()
    {
        return this;
    }

    /**
     * Like getAllInBox but reuses a single MutableBlockPos instead. If this method is used, the resulting BlockPos
     * instances can only be used inside the iteration loop.
     */
    public static Iterable<EMBlockPos.MutableBlockPos> getAllInBoxMutable(EMBlockPos from, EMBlockPos to)
    {
        final EMBlockPos blockpos = new EMBlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        final EMBlockPos blockpos1 = new EMBlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        return new Iterable<EMBlockPos.MutableBlockPos>()
        {
            public Iterator<EMBlockPos.MutableBlockPos> iterator()
            {
                return new AbstractIterator<EMBlockPos.MutableBlockPos>()
                {
                    private EMBlockPos.MutableBlockPos theBlockPos;
                    protected EMBlockPos.MutableBlockPos computeNext()
                    {
                        if (this.theBlockPos == null)
                        {
                            this.theBlockPos = new EMBlockPos.MutableBlockPos(blockpos.getX(), blockpos.getY(), blockpos.getZ());
                            return this.theBlockPos;
                        }
                        else if (this.theBlockPos.equals(blockpos1))
                        {
                            return (EMBlockPos.MutableBlockPos)this.endOfData();
                        }
                        else
                        {
                            int i = this.theBlockPos.getX();
                            int j = this.theBlockPos.getY();
                            int k = this.theBlockPos.getZ();

                            if (i < blockpos1.getX())
                            {
                                ++i;
                            }
                            else if (j < blockpos1.getY())
                            {
                                i = blockpos.getX();
                                ++j;
                            }
                            else if (k < blockpos1.getZ())
                            {
                                i = blockpos.getX();
                                j = blockpos.getY();
                                ++k;
                            }

                            this.theBlockPos.x = i;
                            this.theBlockPos.y = j;
                            this.theBlockPos.z = k;
                            return this.theBlockPos;
                        }
                    }
                };
            }
        };
    }

    public static class MutableBlockPos extends EMBlockPos
        {
            /** Mutable X Coordinate */
            protected int x;
            /** Mutable Y Coordinate */
            protected int y;
            /** Mutable Z Coordinate */
            protected int z;

            public MutableBlockPos()
            {
                this(0, 0, 0);
            }

            public MutableBlockPos(EMBlockPos pos)
            {
                this(pos.getX(), pos.getY(), pos.getZ());
            }

            public MutableBlockPos(int x_, int y_, int z_)
            {
                super(0, 0, 0);
                this.x = x_;
                this.y = y_;
                this.z = z_;
            }

            /**
             * Gets the X coordinate.
             */
            public int getX()
            {
                return this.x;
            }

            /**
             * Gets the Y coordinate.
             */
            public int getY()
            {
                return this.y;
            }

            /**
             * Gets the Z coordinate.
             */
            public int getZ()
            {
                return this.z;
            }

            /**
             * Sets the position, MUST not be name 'set' as that causes obfusication conflicts with func_185343_d
             */
            public EMBlockPos.MutableBlockPos setPos(int xIn, int yIn, int zIn)
            {
                this.x = xIn;
                this.y = yIn;
                this.z = zIn;
                return this;
            }

            public EMBlockPos.MutableBlockPos setPos(double xIn, double yIn, double zIn)
            {
                return this.setPos(MathHelper.floor_double(xIn), MathHelper.floor_double(yIn), MathHelper.floor_double(zIn));
            }

            @SideOnly(Side.CLIENT)
            public EMBlockPos.MutableBlockPos setPos(Entity entityIn)
            {
                return this.setPos(entityIn.posX, entityIn.posY, entityIn.posZ);
            }

            public EMBlockPos.MutableBlockPos setPos(Vec3 vec)
            {
                return this.setPos(vec.xCoord, vec.yCoord, vec.zCoord);
            }

            public EMBlockPos.MutableBlockPos move(EnumFacing facing)
            {
                return this.move(facing, 1);
            }

            public EMBlockPos.MutableBlockPos move(EnumFacing facing, int p_189534_2_)
            {
                return this.setPos(this.x + facing.getFrontOffsetX() * p_189534_2_, this.y + facing.getFrontOffsetY() * p_189534_2_, this.z + facing.getFrontOffsetZ() * p_189534_2_);
            }

            public void setY(int yIn)
            {
                this.y = yIn;
            }

            /**
             * Returns a version of this BlockPos that is guaranteed to be immutable.
             *  
             * <p>When storing a BlockPos given to you for an extended period of time, make sure you
             * use this in case the value is changed internally.</p>
             */
            public EMBlockPos toImmutable()
            {
                return new EMBlockPos(this);
            }
        }

    public static final class PooledMutableBlockPos extends EMBlockPos.MutableBlockPos
        {
            private boolean released;
            private static final List<EMBlockPos.PooledMutableBlockPos> POOL = Lists.<EMBlockPos.PooledMutableBlockPos>newArrayList();

            private PooledMutableBlockPos(int xIn, int yIn, int zIn)
            {
                super(xIn, yIn, zIn);
            }

            public static EMBlockPos.PooledMutableBlockPos retain()
            {
                return retain(0, 0, 0);
            }

            public static EMBlockPos.PooledMutableBlockPos retain(double xIn, double yIn, double zIn)
            {
                return retain(MathHelper.floor_double(xIn), MathHelper.floor_double(yIn), MathHelper.floor_double(zIn));
            }

            @SideOnly(Side.CLIENT)
            public static EMBlockPos.PooledMutableBlockPos retain(Vec3 vec)
            {
                return retain(vec.xCoord, vec.yCoord, vec.zCoord);
            }

            public static EMBlockPos.PooledMutableBlockPos retain(int xIn, int yIn, int zIn)
            {
                synchronized (POOL)
                {
                    if (!POOL.isEmpty())
                    {
                    	EMBlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = (EMBlockPos.PooledMutableBlockPos)POOL.remove(POOL.size() - 1);

                        if (blockpos$pooledmutableblockpos != null && blockpos$pooledmutableblockpos.released)
                        {
                            blockpos$pooledmutableblockpos.released = false;
                            blockpos$pooledmutableblockpos.setPos(xIn, yIn, zIn);
                            return blockpos$pooledmutableblockpos;
                        }
                    }
                }

                return new EMBlockPos.PooledMutableBlockPos(xIn, yIn, zIn);
            }

            public void release()
            {
                synchronized (POOL)
                {
                    if (POOL.size() < 100)
                    {
                        POOL.add(this);
                    }

                    this.released = true;
                }
            }

            /**
             * Sets the position, MUST not be name 'set' as that causes obfusication conflicts with func_185343_d
             */
            public EMBlockPos.PooledMutableBlockPos setPos(int xIn, int yIn, int zIn)
            {
                if (this.released)
                {
                	EMBlockPos.LOGGER.error("PooledMutableBlockPosition modified after it was released.", new Throwable());
                    this.released = false;
                }

                return (EMBlockPos.PooledMutableBlockPos)super.setPos(xIn, yIn, zIn);
            }

            @SideOnly(Side.CLIENT)
            public EMBlockPos.PooledMutableBlockPos setPos(Entity entityIn)
            {
                return (EMBlockPos.PooledMutableBlockPos)super.setPos(entityIn);
            }

            public EMBlockPos.PooledMutableBlockPos setPos(double xIn, double yIn, double zIn)
            {
                return (EMBlockPos.PooledMutableBlockPos)super.setPos(xIn, yIn, zIn);
            }

            public EMBlockPos.PooledMutableBlockPos setPos(Vec3 vec)
            {
                return (EMBlockPos.PooledMutableBlockPos)super.setPos(vec);
            }

            public EMBlockPos.PooledMutableBlockPos move(EnumFacing facing)
            {
                return (EMBlockPos.PooledMutableBlockPos)super.move(facing);
            }

            public EMBlockPos.PooledMutableBlockPos move(EnumFacing facing, int p_189534_2_)
            {
                return (EMBlockPos.PooledMutableBlockPos)super.move(facing, p_189534_2_);
            }
        }
}
