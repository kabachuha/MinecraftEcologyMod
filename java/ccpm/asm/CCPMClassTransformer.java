package ccpm.asm;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.ASMifier;

import org.objectweb.asm.ClassWriter;

import DummyCore.Utils.ASMManager;
import ccpm.api.CCPMApi;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import scala.annotation.meta.companionMethod;

public class CCPMClassTransformer implements IClassTransformer {
	private Logger log = LogManager.getLogger("EcologyMod|ASM");
	public CCPMClassTransformer() {
		log.info("Constructing CCPMClassTransformer");
	}

	
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if(name.length() < 5 || transformedName.length() < 5 || bytes == null || bytes.length == 0)
			return bytes;
		
		ClassReader reader  = new ClassReader(bytes);
		
		if(name.contains("com.google") || name.startsWith("java") || name.startsWith("org") || name.startsWith("oshi") || name.startsWith("com.sun") || name.startsWith("com.ibm") || name.startsWith("paul") || name.startsWith("scala") || name.startsWith("net.minecraftforge") || name.startsWith("DummyCore") || name.startsWith("tv") || name.contains("$") || name.startsWith("com.typesafe"))
		{
			return bytes;
		}
		//
		if(reader.getInterfaces() == null)
			return bytes;
		if(reader.getInterfaces().length == 0)
			return bytes;
		
		log.info("Working on class "+name);
		//log.info("Has interfaces!");
		
		boolean b = false;
		for(String i : reader.getInterfaces())
		{
			log.info(i);
			if(i.equals(Type.getInternalName(IGrowable.class)))
			{
				b = true;
				log.info("Is IGrowable!");
				break;
			}
		}
		
		if(!b)
			return bytes;

		log.info("Starting transforming ["+name+"]");
		log.info("Class starting bytes lenght is"+bytes.length);
		ClassNode clazz = new ClassNode();
		
		reader.accept(clazz, ClassReader.EXPAND_FRAMES);
		
		if(ASMManager.checkAnnotationForClass(clazz, "Lccpm/api/NoGrowableTransformalbe;"))
		{
			log.info("Class ["+name+"] won't be transformed, because it's transformation has been canceled by author.");
			return bytes;
		}
		
		boolean isp = false;
		for(MethodNode method : clazz.methods)
		{
			log.debug(method.name);
			try {
				if(method.name.equals("func_176473_a"))
				{
					log.info("Patching method [canGrow("+method.name+")] in "+name);
					InsnList il = method.instructions;
					
					AbstractInsnNode first = il.getFirst();
					
					InsnList toInject = new InsnList();
					
					Label lab = new Label();
					LabelNode ln = new LabelNode(lab);
					
					//Thanks very much to Prototik(https://github.com/Prototik) because he has helped me with ASM
					toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
					toInject.add(new VarInsnNode(Opcodes.ALOAD, 2));
					toInject.add(new VarInsnNode(Opcodes.ALOAD, 3));
					toInject.add(new VarInsnNode(Opcodes.ILOAD, 4));
					toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(CCPMApi.class), "postEvent", "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;Z)Z", false));
					toInject.add(new VarInsnNode(Opcodes.ISTORE, 5));
					toInject.add(new FrameNode(Opcodes.F_NEW | Opcodes.F_SAME, 0, null, 0, null));
					toInject.add(new VarInsnNode(Opcodes.ILOAD, 5));
					toInject.add(new JumpInsnNode(Opcodes.IFNE, ln));
					toInject.add(new InsnNode(Opcodes.ICONST_0));
					toInject.add(new InsnNode(Opcodes.IRETURN));
					toInject.add(ln);
					//
					
					
					il.insertBefore(first, toInject);
					il.resetLabels();
					log.info("Method canGrow patched!");
					isp = true;
				}
			} catch (Exception e) {
				log.error("Unable to patch method canGrow in "+name);
				e.printStackTrace();
				return bytes;
			}
		}
		
		if(!isp)
		{
			log.error("Method isn't patched correct!");
			FMLCommonHandler.instance().exitJava(-1, true);
			return bytes;
		}
		
		ClassWriter writer = new ClassWriter(0);
		
		clazz.accept(writer);
		log.info(transformedName+" patched!");
		log.info("Class current bytes lenght is "+writer.toByteArray().length);
		return writer.toByteArray();
	}

}
