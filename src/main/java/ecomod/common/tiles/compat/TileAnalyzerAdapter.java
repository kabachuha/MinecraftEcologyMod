package ecomod.common.tiles.compat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import ecomod.api.EcomodBlocks;
import ecomod.api.pollution.PollutionData;
import ecomod.common.tiles.TileAnalyzer;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class TileAnalyzerAdapter extends TileEntity implements SimpleComponent {

	@Override
	public String getComponentName() {
		return "pollution_analyzer";
	}

	@Callback
	public Object[] analyze(Context context, Arguments args) throws Exception{
		int n = 0;
		BlockPos analyzer_pos = null;
		
		for(EnumFacing f : EnumFacing.VALUES)
			if(getWorld().getBlockState(getPos().offset(f)).getBlock() == EcomodBlocks.ANALYZER)
			{
				analyzer_pos = getPos().offset(f);
				n++;
			}
		
		boolean b = false;
		if(n == 1)
		{
			TileAnalyzer ta = (TileAnalyzer)getWorld().getTileEntity(analyzer_pos);
			
			b = ta.analyze() != null;
		}
		
		return new Object[]{b};
	}
	
	@Callback
	public Object[] get_pollution(Context context, Arguments args) throws Exception{
		int n = 0;
		BlockPos analyzer_pos = null;
		
		for(EnumFacing f : EnumFacing.VALUES)
			if(getWorld().getBlockState(getPos().offset(f)).getBlock() == EcomodBlocks.ANALYZER)
			{
				analyzer_pos = getPos().offset(f);
				n++;
			}
		
		PollutionData data = null;
		if(n == 1)
		{
			TileAnalyzer ta = (TileAnalyzer)getWorld().getTileEntity(analyzer_pos);
			
			data = ta.getPollution();
		}
		
		return new Object[]{data};
	}
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
	
	@Callback
	public Object[] get_last_analyzed_time(Context context, Arguments args) throws Exception{
		int n = 0;
		BlockPos analyzer_pos = null;
		
		for(EnumFacing f : EnumFacing.VALUES)
			if(getWorld().getBlockState(getPos().offset(f)).getBlock() == EcomodBlocks.ANALYZER)
			{
				analyzer_pos = getPos().offset(f);
				n++;
			}
		
		long time = -1;
		if(n == 1)
		{
			TileAnalyzer ta = (TileAnalyzer)getWorld().getTileEntity(analyzer_pos);
			
			time = ta.last_analyzed;
		}
		
		return new Object[]{time};
	}
	
	@Callback
	public Object[] get_last_analyzed_time_formatted(Context context, Arguments args) throws Exception{
		int n = 0;
		BlockPos analyzer_pos = null;
		
		for(EnumFacing f : EnumFacing.VALUES)
			if(getWorld().getBlockState(getPos().offset(f)).getBlock() == EcomodBlocks.ANALYZER)
			{
				analyzer_pos = getPos().offset(f);
				n++;
			}
		
		long time = -1;
		if(n == 1)
		{
			TileAnalyzer ta = (TileAnalyzer)getWorld().getTileEntity(analyzer_pos);
			
			time = ta.last_analyzed;
		}
		
		return new Object[]{time == -1 ? "" : DATE_FORMAT.format(time)};
	}
}
