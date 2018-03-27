package ecomod.common.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ecomod.core.stuff.EMConfig;
import net.minecraft.profiler.Profiler;

/**
 * Overriding of the overhardcoded Minecraft Profiler
 * 
 * —making time-out warning configurable(issue #17)
 */
public class WPTProfiler extends Profiler
{
	private static final Logger LOGGER = LogManager.getLogger();
    /** List of parent sections */
    private final List<String> sectionList = Lists.<String>newArrayList();
    /** List of timestamps (System.nanoTime) */
    private final List<Long> timestampList = Lists.<Long>newArrayList();
    /** Flag profiling enabled */
    public boolean profilingEnabled;
    /** Current profiling section */
    private String profilingSection = "";
    /** Profiling map */
    private final Map<String, Long> profilingMap = Maps.<String, Long>newHashMap();

    /**
     * Clear profiling.
     */
    public void clearProfiling()
    {
        this.profilingMap.clear();
        this.profilingSection = "";
        this.sectionList.clear();
    }

    /**
     * Start section
     */
    public void startSection(String name)
    {
        if (this.profilingEnabled)
        {
            if (!this.profilingSection.isEmpty())
            {
                this.profilingSection = this.profilingSection + ".";
            }

            this.profilingSection = this.profilingSection + name;
            this.sectionList.add(this.profilingSection);
            this.timestampList.add(System.nanoTime());
        }
    }

    public void func_194340_a(Supplier<String> p_194340_1_)
    {
        if (this.profilingEnabled)
        {
            this.startSection(p_194340_1_.get());
        }
    }

    /**
     * End section
     */
    public void endSection()
    {
        if (this.profilingEnabled)
        {
            long i = System.nanoTime();
            long j = this.timestampList.remove(this.timestampList.size() - 1);
            this.sectionList.remove(this.sectionList.size() - 1);
            long k = i - j;

            this.profilingMap.put(this.profilingSection, this.profilingMap.getOrDefault(this.profilingSection, 0L) + k);

            if (k > EMConfig.wpt_profiler_timeout_warning * 1000000L)
            {
            	LOGGER.warn("Something's taking more time than usual! '{}' took aprox {} ms", this.profilingSection, (double) k / 1000000.0D);
                LOGGER.warn("(Configured timeout warning triggering delay {} ms)", EMConfig.wpt_profiler_timeout_warning);
            }
            else if (k > EMConfig.wpt_profiler_critical_timeout_warning * 1000000L)
            {
            	LOGGER.error("Something's taking FAR MORE time than usual! '{}' took aprox {} ms. Please, check the Minecraft performance! Big delays in WPT's work may cause Ecomod and Minecraft to work incoherent!", this.profilingSection, (double) k / 1000000.0D);
                LOGGER.error("(Configured critical timeout warning triggering delay {} ms)", EMConfig.wpt_profiler_critical_timeout_warning);
            }

            this.profilingSection = this.sectionList.isEmpty() ? "" : (String)this.sectionList.get(this.sectionList.size() - 1);
        }
    }

    /**
     * Get profiling data
     */
    public List<Profiler.Result> getProfilingData(String profilerName)
    {
        if (!this.profilingEnabled)
        {
            return Collections.<Profiler.Result>emptyList();
        }
        else
        {
        	long i = this.profilingMap.getOrDefault("root", 0L);
        	long j = this.profilingMap.getOrDefault(profilerName, -1L);
            List<Profiler.Result> list = Lists.<Profiler.Result>newArrayList();

            if (!profilerName.isEmpty())
            {
                profilerName = profilerName + ".";
            }

            long k = 0L;

            for (String s : this.profilingMap.keySet())
            {
                if (s.length() > profilerName.length() && s.startsWith(profilerName) && s.indexOf(".", profilerName.length() + 1) < 0)
                {
                	k += this.profilingMap.get(s);
                }
            }

            float f = (float)k;

            if (k < j)
            {
                k = j;
            }

            if (i < k)
            {
                i = k;
            }

            for (String s1 : this.profilingMap.keySet())
            {
                if (s1.length() > profilerName.length() && s1.startsWith(profilerName) && s1.indexOf(".", profilerName.length() + 1) < 0)
                {
                	long l = this.profilingMap.get(s1);
                    double d0 = (double)l * 100.0D / (double)k;
                    double d1 = (double)l * 100.0D / (double)i;
                    String s2 = s1.substring(profilerName.length());
                    list.add(new Profiler.Result(s2, d0, d1));
                }
            }

            for (String s3 : this.profilingMap.keySet())
            {
            	this.profilingMap.put(s3, this.profilingMap.get(s3) * 999L / 1000L);
            }

            if ((float)k > f)
            {
                list.add(new Profiler.Result("unspecified", (double)((float)k - f) * 100.0D / (double)k, (double)((float)k - f) * 100.0D / (double)i));
            }

            Collections.sort(list);
            list.add(0, new Profiler.Result(profilerName, 100.0D, (double)k * 100.0D / (double)i));
            return list;
        }
    }

    /**
     * End current section and start a new section
     */
    public void endStartSection(String name)
    {
        this.endSection();
        this.startSection(name);
    }

    public String getNameOfLastSection()
    {
        return this.sectionList.isEmpty() ? "[UNKNOWN]" : (String)this.sectionList.get(this.sectionList.size() - 1);
    }

    @SideOnly(Side.CLIENT)
    public void func_194339_b(Supplier<String> p_194339_1_)
    {
        this.endSection();
        this.func_194340_a(p_194339_1_);
    }
}
