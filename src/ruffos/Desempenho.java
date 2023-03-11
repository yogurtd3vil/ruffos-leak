package ruffos;

import java.lang.management.ManagementFactory;
import java.time.Instant;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

public class Desempenho {

	/**public static void printUsage(Runtime runtime, TextChannel tc) {
		long total, free, used;
		int mb = 1024 * 1024;

		total = runtime.totalMemory();
		free = runtime.freeMemory();
		used = total - free;
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("💾 Relatório - Memória do Ruffos");
		eb.setTimestamp(Instant.now());
		double usedPercent = ((double) used / (double) total) * 100;
		double freePercent = ((double) free / (double) total) * 100;
		eb.setDescription("Memória total: **" + total / mb + "MB**\nMemória usada: **" + used / mb
				+ "MB**\nMemória livre: **" + free / mb + "MB**\nPorcentagem de uso da memória: **" + usedPercent
				+ "%**\nPorcentagem de memória livre: **" + freePercent + "%**");
		tc.sendMessage(eb.build()).queue();
	}

	public static void log(Object obj, TextChannel tc) {
		tc.sendMessage(obj.toString()).complete();
	}**/

	public static int calcCPU(TextChannel tc, long cpuStartTime, long elapsedStartTime, int cpuCount) {
		long end = System.nanoTime();
		long totalAvailCPUTime = cpuCount * (end - elapsedStartTime);
		long totalUsedCPUTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime() - cpuStartTime;
		// log("Total CPU Time:" + totalUsedCPUTime + " ns.");
		// log("Total Avail CPU Time:" + totalAvailCPUTime + " ns.");
		float per = ((float) totalUsedCPUTime * 100) / (float) totalAvailCPUTime;
		return (int) per;
	}

	public static void desempenho() {
		int gb = 1024 * 1024 * 1024;
		/* DISC SPACE DETAILS */
		Runtime runtime;
		byte[] bytes;
		// Print initial memory usage.
		runtime = Runtime.getRuntime();
	//	log("Relatório da memória:", tc);
	//	printUsage(runtime, tc);

		// Allocate a 1 Megabyte and print memory usage
		//log("Alocando mais 1MB de memória e testando...", tc);
		bytes = new byte[1024 * 1024];
		//printUsage(runtime, tc);

		bytes = null;
		// Invoke garbage collector to reclaim the allocated memory.
		runtime.gc();
		System.gc();
	}

}
