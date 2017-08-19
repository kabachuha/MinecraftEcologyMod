package ecomod.asm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import ecomod.api.EcomodAPI;
import ecomod.api.EcomodStuff;
import ecomod.api.client.IAnalyzerPollutionEffect.TriggeringType;
import ecomod.api.client.IRenderableHeadArmor;
import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionData.PollutionType;
import ecomod.common.pollution.PollutionEffectsConfig;
import ecomod.common.pollution.PollutionSourcesConfig;
import ecomod.common.pollution.PollutionUtils;
import ecomod.common.utils.EMUtils;
import ecomod.core.EcologyMod;
import ecomod.core.stuff.EMConfig;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
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
		
		if(strictCompareByEnvironment(name, "net.minecraft.block.BlockFire", "net.minecraft.block.BlockFire"))
			return handleBlockFire(name, basicClass);
		
		if(strictCompareByEnvironment(name, "net.minecraft.block.BlockLeaves", "net.minecraft.block.BlockLeaves"))
			return handleBlockLeaves(name, basicClass);
		
		if(strictCompareByEnvironment(name, "net.minecraft.block.BlockFarmland", "net.minecraft.block.BlockFarmland"))
			return handleBlockFarmland(name, basicClass);
		
		if(strictCompareByEnvironment(name, "net.minecraft.client.renderer.EntityRenderer", "net.minecraft.client.renderer.EntityRenderer"))
			return handleEntityRenderer(name, basicClass);
		
		if(strictCompareByEnvironment(name, "net.minecraft.client.renderer.entity.layers.LayerCustomHead", "net.minecraft.client.renderer.entity.layers.LayerCustomHead"))
			return handleLayerCustomHead(name, basicClass);
		
		if(strictCompareByEnvironment(name, "net.minecraft.item.Item", "net.minecraft.item.Item"))
			return handleItem(name, basicClass);
			
		if(strictCompareByEnvironment(name, "net.minecraft.item.ItemFood", "net.minecraft.item.ItemFood"))
			return handleItemFood(name, basicClass);
		
		if(strictCompareByEnvironment(name, "net.minecraft.tileentity.TileEntityFurnace", "net.minecraft.tileentity.TileEntityFurnace"))
			return handleTileFurnace(name, basicClass);
			
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
	
	private byte[] handleEntityRenderer(String name, byte[] bytecode)
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
			
			MethodNode mn = getMethod(classNode, "renderRainSnow", "func_78473_a", "(F)V", "(F)V");
			
			InsnList lst = new InsnList();
			
			lst.add(new LabelNode());
			lst.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ecomod/asm/EcomodClassTransformer", "renderRainAddition", "()V", false));
			
			//INVOKEVIRTUAL net/minecraft/client/renderer/texture/TextureManager.bindTexture (Lnet/minecraft/util/ResourceLocation;)V
			
			AbstractInsnNode[] ain = mn.instructions.toArray();
			int insertion_index = -1;
			
			//log.info("Environment required name is "+chooseByEnvironment("bindTexture", "func_110577_a"));
			
			for(int i = 0; i < ain.length; i++)
			{
				AbstractInsnNode insn = ain[i];
				
				if(insn instanceof MethodInsnNode)
				{
					MethodInsnNode min = (MethodInsnNode)insn;
					/*
					log.info("Method : ");
					log.info(min.getOpcode());
					log.info(min.owner);
					log.info(min.name);
					log.info(min.desc);
					log.info(min.itf);
					*/
					if(min.getOpcode() == Opcodes.INVOKEVIRTUAL && min.owner.contentEquals("net/minecraft/client/renderer/texture/TextureManager") && min.name.contentEquals(chooseByEnvironment("bindTexture", "func_110577_a")) && min.desc.contentEquals("(Lnet/minecraft/util/ResourceLocation;)V") && (min.itf == false))
					{
						log.info("FOUND: INVOKEVIRTUAL net/minecraft/client/renderer/texture/TextureManager.bindTexture (Lnet/minecraft/util/ResourceLocation;)V!!!!!");
						insertion_index = i;
						break;
					}
				}
			}
			
			if(insertion_index != -1)
			{
				mn.instructions.insert(mn.instructions.get(insertion_index), lst);
			}
			else
			{
				log.error("Not found: INVOKEVIRTUAL net/minecraft/client/renderer/texture/TextureManager.bindTexture (Lnet/minecraft/util/ResourceLocation;)V");
				return bytecode;
			}
			
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
	
	private byte[] handleBlockFire(String name, byte[] bytecode)
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
			
			MethodNode mn = getMethod(classNode, "updateTick", "func_180650_b", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V");
			
			InsnList lst = new InsnList();
			
			lst.add(new VarInsnNode(Opcodes.ALOAD, 1));
			lst.add(new VarInsnNode(Opcodes.ALOAD, 2));
			lst.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ecomod/asm/EcomodClassTransformer", "fireTickAddition", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", false));
			lst.add(new LabelNode());
			
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
	
	private byte[] handleBlockLeaves(String name, byte[] bytecode)
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
			
			MethodNode mn = getMethod(classNode, "updateTick", "func_180650_b", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V");
			
			InsnList lst = new InsnList();
			
			lst.add(new VarInsnNode(Opcodes.ALOAD, 1));
			lst.add(new VarInsnNode(Opcodes.ALOAD, 2));
			lst.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ecomod/asm/EcomodClassTransformer", "leavesTickAddition", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", false));
			lst.add(new LabelNode());
			
			//log.info(mn.instructions.getLast().getPrevious().getPrevious().toString());
			mn.instructions.insert(mn.instructions.getLast().getPrevious().getPrevious(), lst);
			
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
	
	private byte[] handleBlockFarmland(String name, byte[] bytecode)
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
			
			MethodNode mn = getMethod(classNode, "updateTick", "func_180650_b", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V");
			
			InsnList lst = new InsnList();
			
			lst.add(new VarInsnNode(Opcodes.ALOAD, 1));
			lst.add(new VarInsnNode(Opcodes.ALOAD, 2));
			lst.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ecomod/asm/EcomodClassTransformer", "farmlandTickAddition", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", false));
			lst.add(new LabelNode());
			
			//log.info(mn.instructions.getLast().getPrevious().getPrevious().toString());
			mn.instructions.insert(mn.instructions.getLast().getPrevious().getPrevious(), lst);
			
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
	
	private byte[] handleLayerCustomHead(String name, byte[] bytecode)
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
			
			MethodNode mn = getMethod(classNode, "doRenderLayer", "func_177141_a", "(Lnet/minecraft/entity/EntityLivingBase;FFFFFFF)V", "(Lnet/minecraft/entity/EntityLivingBase;FFFFFFF)V");
			
			InsnList lst = new InsnList();
			
			lst.add(new VarInsnNode(Opcodes.ALOAD, 0));
			lst.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/entity/layers/LayerCustomHead", chooseByEnvironment("modelRenderer", "field_177209_a"), "Lnet/minecraft/client/model/ModelRenderer;"));
			lst.add(new VarInsnNode(Opcodes.ALOAD, 1));
			lst.add(new VarInsnNode(Opcodes.FLOAD, 8));
			lst.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ecomod/asm/EcomodClassTransformer", "lchAddition", "(Lnet/minecraft/client/model/ModelRenderer;Lnet/minecraft/entity/EntityLivingBase;F)V", false));
			lst.add(new LabelNode());
			
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
		
	private byte[] handleItem(String name, byte[] bytecode)
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
			
			MethodNode mn = getMethod(classNode, "onEntityItemUpdate", /*It's MCforge added method*/"onEntityItemUpdate", "(Lnet/minecraft/entity/item/EntityItem;)Z", "(Lnet/minecraft/entity/item/EntityItem;)Z");
			
			InsnList lst = new InsnList();
			
			lst.add(new VarInsnNode(Opcodes.ALOAD, 1));
			lst.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ecomod/asm/EcomodClassTransformer", "itemEntityUpdateAddition", "(Lnet/minecraft/entity/item/EntityItem;)V", false));
			lst.add(new LabelNode());
			
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
	
	private byte[] handleItemFood(String name, byte[] bytecode)
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
			
			MethodNode mn = getMethod(classNode, "onFoodEaten", "func_77849_c", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)V", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)V");
			
			InsnList lst = new InsnList();
			
			lst.add(new VarInsnNode(Opcodes.ALOAD, 1));
			lst.add(new VarInsnNode(Opcodes.ALOAD, 2));
			lst.add(new VarInsnNode(Opcodes.ALOAD, 3));
			lst.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ecomod/asm/EcomodClassTransformer", "itemFoodOnEatenAddition", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)V", false));
			lst.add(new LabelNode());
			
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
	
	private byte[] handleTileFurnace(String name, byte[] bytecode)
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
			
			MethodNode mn = getMethod(classNode, "smeltItem", "func_145949_j", "()V", "()V");
			
			InsnList lst = new InsnList();
			
			lst.add(new LabelNode());
			lst.add(new VarInsnNode(Opcodes.ALOAD, 0));
			lst.add(new VarInsnNode(Opcodes.ALOAD, 1));
			lst.add(new VarInsnNode(Opcodes.ALOAD, 2));
			lst.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ecomod/asm/EcomodClassTransformer", "smeltItemFurnaceAddition", "(Lnet/minecraft/tileentity/TileEntityFurnace;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V", false));
			
			AbstractInsnNode[] ain = mn.instructions.toArray();
			int insertion_index = -1;
			
			for(int i = 0; i < ain.length; i++)
			{
				AbstractInsnNode insn = ain[i];
				
				if(insn instanceof VarInsnNode)
				{
					VarInsnNode min = (VarInsnNode)insn;

					if(min.getOpcode() == Opcodes.ASTORE && min.var == 3)
					{
						log.info("FOUND: ASTORE 3");
						insertion_index = i;
						break;
					}
				}
			}
			
			if(insertion_index != -1)
			{
				mn.instructions.insert(mn.instructions.get(insertion_index), lst);
			}
			else
			{
				log.error("Not found: ASTORE 3");
				return bytecode;
			}
			
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
	
	//Part borrowed from DummyCore(https://github.com/Modbder/DummyCore)
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
	
	//ASM Hooks:
	
	public static boolean updateTickAddition(World worldIn, BlockPos pos)
	{
		if(!worldIn.isRemote)
		{
			if(worldIn.canSeeSky(pos.up()))
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
						if(worldIn.rand.nextInt(50) == 0)
						{
							if(worldIn.rand.nextInt(8) == 0)
								worldIn.setBlockState(pos, Blocks.CLAY.getDefaultState());
							else
								worldIn.setBlockState(pos, Blocks.SAND.getDefaultState());
							
							return false;
						}
					}
				}
			}
		}
		
		return true;
	}
	
	private static final ResourceLocation rain_texture = new ResourceLocation("ecomod:textures/environment/rain.png");
	
	public static void renderRainAddition()
	{
		if(EcologyMod.proxy.getClientHandler() != null)
		if(EcologyMod.proxy.getClientHandler().acid_rain)
			Minecraft.getMinecraft().getTextureManager().bindTexture(rain_texture);
	}
	
	public static void fireTickAddition(World worldIn, BlockPos pos)
	{
		if(!worldIn.isRemote)
		{
			if(worldIn.getGameRules().getBoolean("doFireTick"))
			{
				EcomodAPI.emitPollution(worldIn, EMUtils.blockPosToPair(pos), PollutionSourcesConfig.getSource("fire_pollution"), true);
			}
		}
	}
	
	public static void leavesTickAddition(World worldIn, BlockPos pos)
	{
		if(!worldIn.isRemote)
		{
				//FIXME!!! Too loading
				//EcomodAPI.emitPollution(worldIn, EMUtils.blockPosToPair(pos), PollutionSourcesConfig.getSource("leaves_redution"), true);
			
				if(worldIn.isRainingAt(pos.up()))
				{
					if(worldIn.rand.nextInt(3) == 0)
					{
						PollutionData pollution = EcomodAPI.getPollution(worldIn, EMUtils.blockPosToPair(pos).getLeft(), EMUtils.blockPosToPair(pos).getRight()).clone();
			
						if(pollution!=null && pollution.compareTo(PollutionData.getEmpty()) != 0)
							if(PollutionEffectsConfig.isEffectActive("acid_rain", pollution))
									worldIn.setBlockToAir(pos);
					}
				}
				else
				{
					if(worldIn.rand.nextInt(10) == 0)
					{
						if(PollutionUtils.hasSurfaceAccess(worldIn, pos))
						{
							PollutionData pollution = EcomodAPI.getPollution(worldIn, EMUtils.blockPosToPair(pos).getLeft(), EMUtils.blockPosToPair(pos).getRight()).clone();
			
							if(pollution!=null && pollution.compareTo(PollutionData.getEmpty()) != 0)
								if(PollutionEffectsConfig.isEffectActive("dead_trees", pollution))
										worldIn.setBlockToAir(pos);
								else
									if(worldIn.rand.nextInt(10)==0)
										EcomodAPI.emitPollution(worldIn, EMUtils.blockPosToPair(pos), PollutionSourcesConfig.getSource("leaves_redution"), true);
						}
					}
				}
		}
	}
	
	public static void farmlandTickAddition(World worldIn, BlockPos pos)
	{
		if(!worldIn.isRemote)
		{
			PollutionData pollution = EcomodAPI.getPollution(worldIn, EMUtils.blockPosToPair(pos).getLeft(), EMUtils.blockPosToPair(pos).getRight());
			
			if(PollutionEffectsConfig.isEffectActive("no_plowing", pollution))
			{
				if(worldIn.rand.nextInt(3) == 0)
				{
					boolean sealed = true;
					for(EnumFacing f : EnumFacing.VALUES)
						sealed &= worldIn.getBlockState(pos.offset(f)).getBlock() != Blocks.DIRT;
					
					sealed &= !PollutionUtils.hasSurfaceAccess(worldIn, pos);
					
					//Turn to dirt
					IBlockState iblockstate = Blocks.DIRT.getDefaultState();
					worldIn.setBlockState(pos, iblockstate);
					AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(worldIn, pos).offset(pos);

					for (Entity entity : worldIn.getEntitiesWithinAABBExcludingEntity((Entity)null, axisalignedbb))
					{
				       	entity.setPosition(entity.posX, axisalignedbb.maxY, entity.posZ);
					}
					
					return;
				}
			}
			
			if(PollutionEffectsConfig.isEffectActive("acid_rain", pollution))
			{
				if(worldIn.rand.nextInt(3) == 0)
				{
					if(worldIn.isRainingAt(pos.up()))
					{
						//Turn to dirt
						IBlockState iblockstate = Blocks.DIRT.getDefaultState();
						worldIn.setBlockState(pos, iblockstate);
						AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(worldIn, pos).offset(pos);

						for (Entity entity : worldIn.getEntitiesWithinAABBExcludingEntity((Entity)null, axisalignedbb))
						{
					       	entity.setPosition(entity.posX, axisalignedbb.maxY, entity.posZ);
						}
					}
				}
			}
		}
	}
	
	public static void lchAddition(ModelRenderer modelRenderer, EntityLivingBase entitylivingbaseIn, float scale)
	{
		ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

        if (!itemstack.isEmpty())
        {
        	if(itemstack.getItem() instanceof IRenderableHeadArmor)
        	{
        		Minecraft minecraft = Minecraft.getMinecraft();
                GlStateManager.pushMatrix();
                
                if (entitylivingbaseIn.isSneaking())
                {
                    GlStateManager.translate(0.0F, 0.2F, 0.0F);
                }
                
                boolean flag = entitylivingbaseIn instanceof EntityVillager || entitylivingbaseIn instanceof EntityZombieVillager;
                
                if (entitylivingbaseIn.isChild() && !(entitylivingbaseIn instanceof EntityVillager))
                {
                    float f = 2.0F;
                    float f1 = 1.4F;
                    GlStateManager.translate(0.0F, 0.5F * scale, 0.0F);
                    GlStateManager.scale(0.7F, 0.7F, 0.7F);
                    GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
                }
                
                modelRenderer.postRender(0.0625F);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                
                float f3 = 0.625F;
                GlStateManager.translate(0.0F, -0.25F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.scale(0.625F, -0.625F, -0.625F);

                if (flag)
                {
                    GlStateManager.translate(0.0F, 0.1875F, 0.0F);
                }

                minecraft.getItemRenderer().renderItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.HEAD);
                
                GlStateManager.popMatrix();
        	}
        }
	}
	
	public static void itemEntityUpdateAddition(EntityItem item)
	{
		if(!item.world.isRemote)
		{
			if(item.ticksExisted % 100 == 0)
			{
				ItemStack is = item.getEntityItem();
			
				if(is.getItem() instanceof ItemFood)
				{
					if(is.hasCapability(EcomodStuff.CAPABILITY_POLLUTION, null))
					{
						if(!EcomodStuff.pollution_effects.containsKey("food_pollution"))
							return;
						
						PollutionData pd = EcologyMod.ph.getPollution(item.getEntityWorld(), EMUtils.blockPosToPair(item.getPosition()));
						
						PollutionData trig = EcomodStuff.pollution_effects.get("food_pollution").getTriggerringPollution();
						
						if(EcomodStuff.pollution_effects.get("food_pollution").getTriggeringType() == TriggeringType.AND ? pd.compareTo(trig) >= 0 : pd.compareOR(trig) >= 0)
						{
							PollutionData delta = pd.clone().add(trig.clone().multiplyAll(-1));
							
							boolean in = item.getEntityWorld().getBlockState(item.getPosition()).getMaterial() == Material.WATER;
							
							if(!in)
							for(EnumFacing dir : EnumFacing.VALUES)
								if(!in)
									in |= item.getEntityWorld().getBlockState(item.getPosition().offset(dir)).getMaterial() == Material.WATER;
							
							
							delta.multiply(PollutionType.WATER, in ? 1F : 0.25F);
							
							in = PollutionUtils.hasSurfaceAccess(item.getEntityWorld(), item.getPosition());
							
							delta.multiply(PollutionType.AIR, in ? 1F : 0.4F);
							
							in = item.getEntityWorld().getBlockState(item.getPosition()).getMaterial() == Material.GRASS || item.getEntityWorld().getBlockState(item.getPosition()).getMaterial() == Material.GROUND;
							
							if(!in)
							for(EnumFacing dir : EnumFacing.VALUES)
								if(!in)
									in |= item.getEntityWorld().getBlockState(item.getPosition().offset(dir)).getMaterial() == Material.GRASS || item.getEntityWorld().getBlockState(item.getPosition().offset(dir)).getMaterial() == Material.GROUND;
							
							delta.multiply(PollutionType.SOIL, in ? 1F : 0.2F);
							
							is.getCapability(EcomodStuff.CAPABILITY_POLLUTION, null).setPollution(is.getCapability(EcomodStuff.CAPABILITY_POLLUTION, null).getPollution().add(delta.multiplyAll(EMConfig.food_polluting_factor)));
						}
					}
				}
			}
		}
	}
	
	public static void itemFoodOnEatenAddition(ItemStack stack, World worldIn, EntityPlayer player)
	{
		if(!worldIn.isRemote)
		{
			if(stack.hasCapability(EcomodStuff.CAPABILITY_POLLUTION, null))
			{
				PollutionData pollution = stack.getCapability(EcomodStuff.CAPABILITY_POLLUTION, null).getPollution();
				
				int a = (int)(pollution.getAirPollution() * EMConfig.pollution_to_food_poison[0]);
				int b = (int)(pollution.getWaterPollution() * EMConfig.pollution_to_food_poison[1]);
				int c = (int)(pollution.getSoilPollution() * EMConfig.pollution_to_food_poison[2]);
				
				int m = (a+b+c)/3;
				int k = (int) Math.sqrt(a * b * c) / 300;
				
				if(m > 0)
					player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("nausea"), Math.min(m*20, 1200), Math.min(k, 2)));
				
				if(m >= 60)
				{
					player.sendMessage(new TextComponentString("You've eaten polluted food").setStyle(new Style().setColor(TextFormatting.DARK_GREEN)));
					player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("slowness"), m, Math.min(k, 2)));
					player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("poison"), m, Math.min(k, 2)));
				}
				
				if(m >= 200)
					player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("wither"), m, Math.min(k, 2)));
			}
		}
	}
	
	public static void smeltItemFurnaceAddition(TileEntityFurnace furnace, ItemStack ingr, ItemStack result)
	{
		if(!furnace.getWorld().isRemote)
		{
			/* FIXME!!! Not seeing Capablilties in result!
			if(ingr.getItem() instanceof ItemFood && result.getItem() instanceof ItemFood)
			{
				if(ingr.hasCapability(EcomodStuff.CAPABILITY_POLLUTION, null) && result.hasCapability(EcomodStuff.CAPABILITY_POLLUTION, null))
				{
					result.getCapability(EcomodStuff.CAPABILITY_POLLUTION, null).setPollution(result.getCapability(EcomodStuff.CAPABILITY_POLLUTION, null).getPollution().clone().add(ingr.getCapability(EcomodStuff.CAPABILITY_POLLUTION, null).getPollution()));
				}
			}
			*/
			
			EcomodAPI.emitPollution(furnace.getWorld(), EMUtils.blockPosToPair(furnace.getPos()), PollutionSourcesConfig.getSmeltedItemStackPollution(ingr), true);
		}
	}
}
