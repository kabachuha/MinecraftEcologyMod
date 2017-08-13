package ecomod.asm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import ecomod.api.EcomodAPI;
import ecomod.api.pollution.PollutionData;
import ecomod.common.pollution.PollutionEffectsConfig;
import ecomod.common.utils.EMUtils;
import net.minecraft.init.Blocks;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.classloading.FMLForgePlugin;

public class EcomodClassTransformer implements IClassTransformer
{
	boolean b = false;
	static Logger log = LogManager.getLogger("EcomodASM");
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		if(strictCompareByEnvironment(name, "net.minecraft.block.BlockGrass", "net.minecraft.block.BlockGrass"))
			return handleBlockGrass(name, basicClass);
		
		return basicClass;
	}

	
	public static String chooseByEnvironment(String deobf, String obf)
	{
		return FMLForgePlugin.RUNTIME_DEOBF ? obf : deobf;
	}
	
	private byte[] handleBlockGrass(String name, byte[] bytecode)
	{
		log.info("Transforming "+name);
		log.info("Initial size: "+bytecode.length+" bytes");
		
		byte[] bytes = bytecode.clone();
		
		try
		{
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(bytes);
			classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			
			MethodNode mn = getMethod(classNode, "updateTick", "func_180650_b!&!b", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V");
			
			LabelNode ln = new LabelNode();
			InsnList lst = new InsnList();
			
			//Injection:
			
			//Invoking updateTickAddition
			lst.add(new VarInsnNode(Opcodes.ALOAD, 1));
			lst.add(new VarInsnNode(Opcodes.ALOAD, 2));
			lst.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ecomod/asm/EcomodClassTransformer", "updateTickAddition", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z", false));
			
			//Whether updateTickAddition worked
			lst.add(new JumpInsnNode(Opcodes.IFEQ, ln));
			
			//if false return from updateTick
			lst.add(new LabelNode());
			lst.add(new InsnNode(Opcodes.RETURN));
			
			//if true continue
			lst.add(ln);
			lst.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
			
			
			mn.instructions.insert(mn.instructions.get(1), lst);
			
			classNode.accept(cw);
			bytes = cw.toByteArray();
			
			log.info("Transformed "+name);
			log.info("Final size: "+bytes.length+" bytes");
			
			return bytes;
		}
		catch(Exception e)
		{
			log.error("Unable to patch "+name+"!");
			log.error(e.toString());
			e.printStackTrace();
			
			return bytecode;
		}
	}
	
	//Part borrowed from DummyCore
	public static final String REGEX_NOTCH_FROM_MCP = "!&!";
	
	public static MethodNode getMethod(ClassNode cn, String deobfName, String obfName, String deobfDesc, String obfDesc)
	{
		String methodName = null;
		String methodDesc = null;
		if(!checkSameAndNullStrings(deobfName,obfName))
			methodName = chooseByEnvironment(deobfName,obfName);
		if(!checkSameAndNullStrings(deobfDesc,obfDesc))
			methodDesc = chooseByEnvironment(deobfDesc,obfDesc);
		
		String additionalMN = "";
		if(methodName != null && methodName.contains(REGEX_NOTCH_FROM_MCP))
		{
			String[] sstr = methodName.split(REGEX_NOTCH_FROM_MCP);
			methodName = sstr[0];
			additionalMN = sstr[1];
		}
		
		String additionalMD = "";
		if(methodDesc != null && methodDesc.contains(REGEX_NOTCH_FROM_MCP))
		{
			String[] sstr = methodDesc.split(REGEX_NOTCH_FROM_MCP);
			methodDesc = sstr[0];
			additionalMD = sstr[1];
		}
		
		if(checkSameAndNullStrings(methodName, methodDesc))
			return null;
		for(MethodNode mn : cn.methods)
			if((methodName == null || methodName.equals(mn.name) || additionalMN.equals(mn.name)) && (methodDesc == null || methodDesc.equals(mn.desc) || additionalMD.equals(mn.desc)))
				return mn;
		
		return null;
	}
	
	public static boolean checkSameAndNullStrings(String par1, String par2)
	{
		if(par1 == par2)
		{
			if(par1 == null && par2 == null)
				return true;
			else
				if(par1 != null && par2 != null)
					if(par1.isEmpty() && par2.isEmpty())
						return true;
		}
		
		return false;
	}
	
	public static boolean strictCompareByEnvironment(String name, String deobf, String obf)
	{
		String comparedTo = chooseByEnvironment(deobf.replace('/', '.'),obf.replace('/', '.'));
		return comparedTo.equalsIgnoreCase(name.replace('/', '.'));
	}
	
	//
	
	public static boolean updateTickAddition(World worldIn, BlockPos pos)
	{
		if(!worldIn.isRemote)
		{
			if(worldIn.canBlockSeeSky(pos))
			{
				PollutionData pd = EcomodAPI.getPollution(worldIn, EMUtils.blockPosToPair(pos).getLeft(), EMUtils.blockPosToPair(pos).getRight());
				if(pd != null)
				if(PollutionEffectsConfig.isEffectActive("wasteland", pd))
				{
					if(worldIn.rand.nextInt(10)==0)
					{
						worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState());
						return false;
					}
					else
					{
						if(worldIn.rand.nextInt(10) == 0)
						{
							if(worldIn.rand.nextBoolean())
								worldIn.setBlockState(pos, Blocks.SAND.getDefaultState());
							else
								worldIn.setBlockState(pos, Blocks.MYCELIUM.getDefaultState());
							
							return false;
						}
					}
				}
			}
		}
		
		return true;
	}
}
