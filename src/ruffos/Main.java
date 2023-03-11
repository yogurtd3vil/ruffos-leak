package ruffos;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import ruffos._amantes.AmantesCommand;
import ruffos._blackjack.BlackJackListener;
import ruffos._casamento.CasarCommand;
import ruffos._casamento.DivorciarCommand;
import ruffos._clans.ClanCommand2;
import ruffos._config.ConfigManager;
import ruffos._database.Database;
import ruffos._database.DatabaseListeners;
import ruffos._economia.EconomiaListener;
import ruffos._economia.EmpregosCommand;
import ruffos._economia.EnviarCommand;
import ruffos._economia.Loja2Command;
import ruffos._eventos.Dirigivel;
import ruffos._eventos.airdrop.AirDrop;
import ruffos._eventos.airdrop.AirDropListener;
import ruffos._eventos.porquinho.Porquinho;
import ruffos._eventos.porquinho.PorquinhoListener;
import ruffos._filhos.FilhosManager;
import ruffos._leveis.LeveisListener;
import ruffos._lojatags.ComprarTagCommand;
import ruffos._lojatags.LojaTagsListener;
import ruffos._perfil.CorCommand;
import ruffos._perfil.SobremimCommand;
import ruffos._rinha.RinhaListener;
import ruffos._roubos.RouboListener;
import ruffos._tempocall.TempoCall;
import ruffos.utils.EventWaiter;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandManager;

public class Main extends ListenerAdapter {

	private static Database database;

	public static Database getDatabase() {
		return database;
	}

	public static JDA jda;

	public static JDA getJDA() {
		return jda;
	}

	public static CommandManager manager;

	public static int comandosExecutados = 0;
	public static long tempoOnline = 0;

	public static Map<Guild, Porquinho> porquinhos = new HashMap<>();
	public static Map<Guild, AirDrop> airDrop = new HashMap<>();
	public static EventWaiter waiter;

	public static void main(String[] args) {
		System.out.println(Utils.getTime() + "Iniciando BOT...");
		database = new Database();
		waiter = new EventWaiter();
		manager = new CommandManager(waiter);
		JDABuilder jdaBuild = JDABuilder.createDefault(
				"TOKEN", GatewayIntent.GUILD_MEMBERS,
				GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES,
				GatewayIntent.GUILD_BANS, GatewayIntent.GUILD_MESSAGE_REACTIONS,
				GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_VOICE_STATES,
				GatewayIntent.GUILD_PRESENCES);
		jdaBuild.setAutoReconnect(true);
		jdaBuild.addEventListeners(new Main());
		jdaBuild.addEventListeners(new DatabaseListeners());
		jdaBuild.addEventListeners(new EconomiaListener());
		// jdaBuild.addEventListeners(new DueloListener());
		jdaBuild.addEventListeners(new LeveisListener());
		jdaBuild.addEventListeners(new RinhaListener());
		jdaBuild.addEventListeners(new TempoCall());
		jdaBuild.addEventListeners(new PorquinhoListener());
		jdaBuild.addEventListeners(new RouboListener());
		jdaBuild.addEventListeners(new BlackJackListener());
		jdaBuild.addEventListeners(new Dirigivel());
		jdaBuild.addEventListeners(new AirDropListener());
		jdaBuild.addEventListeners(new LojaTagsListener());
		jdaBuild.addEventListeners(new EmpregosCommand());
		jdaBuild.addEventListeners(new Loja2Command());
		jdaBuild.addEventListeners(new CasarCommand());
		jdaBuild.addEventListeners(new DivorciarCommand());
		jdaBuild.addEventListeners(new AmantesCommand());
		jdaBuild.addEventListeners(new CorCommand());
		jdaBuild.addEventListeners(new SobremimCommand());
		jdaBuild.addEventListeners(new EnviarCommand());
		jdaBuild.addEventListeners(new ComprarTagCommand());
		jdaBuild.addEventListeners(new ClanCommand2());
		// jdaBuild.addEventListeners(new Tiroteio());
		jdaBuild.addEventListeners(waiter);
		jdaBuild.setMemberCachePolicy(MemberCachePolicy.ALL);
		jdaBuild.setChunkingFilter(ChunkingFilter.ALL);
		banir();
		database.setGlobalConnection();
		try {
			jda = jdaBuild.build();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}

	void relatorio() {
		new Timer().scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				Desempenho.desempenho();
			}
		}, 3600000, 3600000);
	}

	void atualizar() {
		new Timer().scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				System.out.println(Utils.getTime() + ConsoleColors.BRIGHT_GREEN
						+ "Atualizando informações sobre o Ruffos..." + ConsoleColors.RESET);
				VoiceChannel servidores = Main.getJDA().getVoiceChannelById("717061772124618753");
				VoiceChannel membros = Main.getJDA().getVoiceChannelById("717061800826503488");
				VoiceChannel cmds = Main.getJDA().getVoiceChannelById("717061838373650512");
				VoiceChannel tempoOn = Main.getJDA().getVoiceChannelById("717062871602692138");
				VoiceChannel cmdsP = Main.getJDA().getVoiceChannelById("717061888101318696");
				servidores.getManager().setName("Servidores: " + Main.getJDA().getGuilds().size()).queue();
				membros.getManager().setName("Usuários: " + Main.getJDA().getUsers().size()).queue();
				cmds.getManager().setName("Cmds executados: " + comandosExecutados).queue();
				int cmdsPS = Integer.parseInt(String.valueOf(comandosExecutados / 60));
				cmdsP.getManager().setName("Cmds p/ min: " + cmdsPS).queue();
				Calendar saoPauloDate = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
				Date d = new Date(saoPauloDate.getTimeInMillis());
				tempoOn.getManager().setName("Tempo ligado: " + Utils.getTime(d.getTime() - tempoOnline)).complete();
			}
		}, 5000, 3600 * 1000);
	}

	@Override
	public void onReady(ReadyEvent event) {
		JDA jda = event.getJDA();
		Calendar saoPauloDate = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
		Date d = new Date(saoPauloDate.getTimeInMillis());
		tempoOnline = d.getTime();
		event.getJDA().upsertCommand("ajuda", "Ajuda do Ruffos.").queue();
		event.getJDA().updateCommands();
		System.out.println(Utils.getTime() + ConsoleColors.BRIGHT_GREEN + "Iniciando ruffos com "
				+ ConsoleColors.BRIGHT_CYAN + event.getGuildTotalCount() + ConsoleColors.BRIGHT_GREEN + " guild(s), "
				+ ConsoleColors.BRIGHT_CYAN + jda.getUsers().size() + ConsoleColors.BRIGHT_GREEN + " usuário(s) "
				+ ConsoleColors.RESET);
		System.out.println(Utils.getTime() + ConsoleColors.BRIGHT_YELLOW + "Carregando servidores e dados..."
				+ ConsoleColors.RESET);
		System.out.println(event.getJDA().getGuilds());
		Utils.loadEmpregos();
		for (Guild g : jda.getGuilds()) {
			System.out.println(g.getName());
			// Tiroteio.tiroteio = Main.getJDA().getGuildById("936599806674087976");
			if (!Utils.isIgnoredServer(g)) {
				System.out.println(Utils.getTime() + ConsoleColors.BRIGHT_GREEN + "Carregando: "
						+ ConsoleColors.BRIGHT_WHITE + g.getName() + ";" + g.getId() + ConsoleColors.BRIGHT_GREEN + "."
						+ ConsoleColors.RESET);
				database.criarTabelas(g);
				System.gc();
				relatorio();
				FilhosManager.ficarComSede(g);
				FilhosManager.ficarComFome(g);
				FilhosManager.perderFelicidade(g);
				porquinhos.put(g, new Porquinho());
				airDrop.put(g, new AirDrop());
//				TempoCall.carregar(g);
				System.out.println(Utils.getTime() + ConsoleColors.BRIGHT_GREEN + "Servidor carregado: "
						+ ConsoleColors.BRIGHT_WHITE + g.getName() + ConsoleColors.BRIGHT_GREEN + "."
						+ ConsoleColors.RESET);
			}
		}
		TempoCall.liberado = true;
		System.out.println(Utils.getTime() + ConsoleColors.BRIGHT_GREEN + "Ruffos iniciado e pronto para uso."
				+ ConsoleColors.RESET);
		// atualizar();
	}

	Map<Guild, Integer> msgs = new HashMap<>();

	public static List<String> ban = new ArrayList<>();

	static void banir() {
		ban.add("234543026247172097");
		ban.add("613551819817222185");
		ban.add("749181692669067294");
		ban.add("732442039488348200");
		ban.add("974050227361296434");
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		event.reply("Para obter ajuda do BOT, clique aqui: https://discord.gg/RxRj6vt9gk").setEphemeral(true).queue();
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		TextChannel tc = event.getGuild().getTextChannelById(event.getChannel().getId());
		if (TempoCall.liberado && event.isFromType(ChannelType.TEXT)) {
			String prefix = "c!";
			String raw = event.getMessage().getContentRaw().toLowerCase();
			User u = event.getAuthor();
			Guild g = event.getGuild();
			if (raw.equals("quitbot") && event.getAuthor().getId().equals("380570412314001410")) {
				event.getGuild().leave().queue();
				return;
			}
			if (manager.time.containsKey(u.getId())
					&& (System.currentTimeMillis() >= (manager.time.get(u.getId()) + 7200000))
					&& manager.captcha.containsKey(u.getId())
					&& manager.captcha.get(u.getId()).equals(event.getMessage().getContentRaw())) {
				Utils.enviarEmbed(tc, u, null, u.getAsMention() + ", agora você pode digitar comandos novamente!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				manager.time.remove(u.getId());
				manager.captcha.remove(u.getId());
			}
			if (raw.startsWith(prefix)) {
				manager.handle(event);
				return;
			}
			if (!Utils.isIgnoredServer(g) && !u.isBot()) {
				porquinhos.get(g).msgs++;
				if (porquinhos.get(g).msgs >= 900 && !porquinhos.get(g).iniciado) {
					porquinhos.get(g).msgs = 0;
					Random r = new Random();
					int numero = r.nextInt(20);
					while (numero == 0) {
						numero = r.nextInt(20);
					}
					int valor = r.nextInt(2000);
					while (valor < 1000) {
						valor = r.nextInt(2000);
					}
					porquinhos.get(g).iniciar(g);
				} else if (airDrop.get(g).msgs >= 2000 && airDrop.get(g).idMSG == 0) {
					airDrop.get(g).msgs = 0;
					airDrop.get(g).dropar(g);
				}
				int msg = msgs.containsKey(g) ? msgs.get(g) : 0;
				msg++;
				msgs.put(g, msg);
				/**
				 * if (ConfigManager.temCfg(g, "dirigivelmsgs") && msg >=
				 * Integer.parseInt(ConfigManager.getConfig(g, "dirigivelmsgs"))) {
				 * Dirigivel.start(g.getTextChannelById(ConfigManager.getConfig(g,
				 * "chatgeral"))); msgs.put(g, 0); }
				 **/
			}
		}
	}

}
