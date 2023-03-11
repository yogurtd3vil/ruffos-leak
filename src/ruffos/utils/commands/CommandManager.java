package ruffos.utils.commands;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ruffos.ConsoleColors;
import ruffos.Main;
import ruffos._administrativos.AddInventarioCommand;
import ruffos._administrativos.AddMoneyCommand;
import ruffos._administrativos.ClienteCommand;
import ruffos._administrativos.ResetCommand;
import ruffos._amantes.AmantesCommand;
import ruffos._blackjack.BlackJackCommand;
import ruffos._casamento.CasarCommand;
import ruffos._casamento.DivorciarCommand;
import ruffos._clans.ClanCommand;
import ruffos._clans.ClanCommand2;
import ruffos._config.ConfigCommand;
import ruffos._config.ConfigManager;
import ruffos._cooldowns.CooldownsCommand;
import ruffos._economia.DepositarCommand;
import ruffos._economia.DinheiroCommand;
import ruffos._economia.EmpregosCommand;
import ruffos._economia.EnviarCommand;
import ruffos._economia.Loja2Command;
import ruffos._economia.SacarCommand;
import ruffos._eventos.IniciarCommand;
import ruffos._filhos.FilhoCommand;
import ruffos._inventario.InventarioCommand;
import ruffos._inventario.UsarCommand;
import ruffos._leveis.BeberCommand;
import ruffos._lojatags.AddLojaTagCommand;
import ruffos._lojatags.ComprarTagCommand;
import ruffos._lojatags.LojaTagsCommand;
import ruffos._lojatags.RemoverLojaTagCommand;
import ruffos._pd.PrimeiraDamaCommand;
import ruffos._perfil.CorCommand;
import ruffos._perfil.PerfilCommand;
import ruffos._perfil.PersonagemCommand;
import ruffos._perfil.SobremimCommand;
import ruffos._prisao.FiancaCommand;
import ruffos._prisao.PresosManager;
import ruffos._rankings.TempoCallCommand;
import ruffos._rankings.TopDinheiroCommand;
import ruffos._rankings.TopLevelCommand;
import ruffos._rankings.TopRoubosCommand;
import ruffos._receberdinheiro.BoosterCommand;
import ruffos._receberdinheiro.CavaloCommand;
import ruffos._receberdinheiro.CrimeCommand;
import ruffos._receberdinheiro.GFCommand;
import ruffos._receberdinheiro.MadrugadaCommand;
import ruffos._receberdinheiro.RecompensaCommand;
import ruffos._receberdinheiro.RoletaCommand;
import ruffos._receberdinheiro.SFCommand;
import ruffos._receberdinheiro.SemanalCommand;
import ruffos._receberdinheiro.TrabalharCommand;
import ruffos._receberdinheiro.VenderPackCommand;
import ruffos._receberdinheiro.XcamCommand;
import ruffos._rinha.RinhaCommand;
import ruffos._roubos.RoubarBancoCommand;
import ruffos._roubos.RoubarCofreCommand;
import ruffos._roubos.RoubarCommand;
import ruffos.bolao.BolaoCommand;
import ruffos.commands.AjudaCommand;
import ruffos.commands.AvCommand;
import ruffos.commands.BannerCommand;
import ruffos.commands.CassinoCommand;
import ruffos.quitandjoingame.EntrarCommand;
import ruffos.quitandjoingame.QuitAndJoinManager;
import ruffos.quitandjoingame.SairCommand;
import ruffos.utils.EventWaiter;
import ruffos.utils.Utils;

public class CommandManager {

	public Map<String, Long> time = new HashMap<>();
	public Map<String, String> captcha = new HashMap<>();

	public CommandManager(EventWaiter waiter) {
		addCommand(new RoubarCofreCommand());
		addCommand(new DinheiroCommand());
		addCommand(new EnviarCommand());
		addCommand(new DepositarCommand());
		addCommand(new SacarCommand());
		addCommand(new RecompensaCommand());
		addCommand(new Loja2Command());
		addCommand(new PerfilCommand());
		addCommand(new RoubarCommand());
		addCommand(new CassinoCommand());
		addCommand(new RinhaCommand(waiter));
		addCommand(new TopDinheiroCommand());
		// addCommand(new DuelarCommand());
		addCommand(new EmpregosCommand());
		addCommand(new CrimeCommand());
		addCommand(new TrabalharCommand());
		addCommand(new TopRoubosCommand());
		addCommand(new TopLevelCommand());
		addCommand(new IniciarCommand());
		addCommand(new CorCommand());
		addCommand(new SobremimCommand());
		addCommand(new BeberCommand());
		addCommand(new TempoCallCommand());
		addCommand(new CasarCommand());
		addCommand(new DivorciarCommand());
		addCommand(new RoubarBancoCommand());
		addCommand(new FiancaCommand());
		addCommand(new BlackJackCommand());
		addCommand(new PersonagemCommand());
		addCommand(new FilhoCommand());
		addCommand(new ConfigCommand());
		addCommand(new AddMoneyCommand());
		addCommand(new GFCommand());
		// addCommand(new EvalCommand());
		// addCommand(new TagsPersoCommand());
		// addCommand(new PutaCommand());
		addCommand(new AmantesCommand());
		addCommand(new SemanalCommand());
		addCommand(new RoletaCommand());
		addCommand(new SairCommand());
		addCommand(new EntrarCommand());
		addCommand(new CooldownsCommand());
		addCommand(new CavaloCommand());
		addCommand(new InventarioCommand());
		addCommand(new BoosterCommand());
		addCommand(new MadrugadaCommand());
		addCommand(new UsarCommand());
		addCommand(new AddInventarioCommand());
		addCommand(new LojaTagsCommand());
		addCommand(new AddLojaTagCommand());
		addCommand(new ComprarTagCommand());
		addCommand(new RemoverLojaTagCommand());
		addCommand(new AjudaCommand());
		addCommand(new VenderPackCommand());
		addCommand(new XcamCommand());
		addCommand(new AvCommand());
		addCommand(new BannerCommand());
		//addCommand(new PrimeiraDamaCommand());
		addCommand(new ClienteCommand(waiter));
		addCommand(new BolaoCommand());
		addCommand(new SFCommand());
		addCommand(new ClanCommand2());
		addCommand(new ResetCommand());
	}

	public final List<ICommand> commands = new ArrayList<>();

	private void addCommand(ICommand cmd) {
		boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

		if (nameFound) {
			throw new IllegalArgumentException("Nome de comando ja existente");
		}

		commands.add(cmd);
	}

	@Nullable
	private ICommand getCommand(String search) {
		String searchLower = search.toLowerCase();

		for (ICommand cmd : this.commands) {
			if (cmd.getName().equalsIgnoreCase(searchLower) || cmd.getAliases().contains(searchLower)) {
				return cmd;
			}
		}

		return null;
	}

	public static Map<String, Long> cooldown = new HashMap<>();

	public void handle(MessageReceivedEvent event) {
		Guild g = event.getGuild();
		User u = event.getAuthor();
		TextChannel tc = event.getGuild().getTextChannelById(event.getChannel().getId());
		String[] split = event.getMessage().getContentRaw().replaceFirst("(?i) c!", "").split("\\s+");
		if (Main.ban.contains(u.getId())) {
			Utils.enviarEmbed(tc, u, null,
					Utils.getEmote("ban").getFormatted()
							+ " Você está banido(a) do Ruffos e por isso não pode digitar comandos.",
					Color.RED, null, null, null, Main.getJDA().getSelfUser().getAvatarUrl(), 0, true);
			return;
		}
		String invoke = split[0].toLowerCase();
		if (invoke.equals("c!eval")) {
			ICommand cmd = this.getCommand(invoke);
			event.getChannel().sendTyping().queue();
			List<String> args = Arrays.asList(split).subList(1, split.length);

			CommandContext ctx = new CommandContext(event, args);

			cmd.handle(ctx);
			return;
		}
		if (invoke.equals("c!cliente")) {
			ICommand cmd = this.getCommand(invoke);
			event.getChannel().sendTyping().queue();
			List<String> args = Arrays.asList(split).subList(1, split.length);

			CommandContext ctx = new CommandContext(event, args);

			cmd.handle(ctx);
			return;
		}
		if (invoke.equals("c!entrar")) {
			ICommand cmd = this.getCommand(invoke);
			event.getChannel().sendTyping().queue();
			List<String> args = Arrays.asList(split).subList(1, split.length);

			CommandContext ctx = new CommandContext(event, args);

			cmd.handle(ctx);
			return;
		}
		if (QuitAndJoinManager.isQuited(g, event.getMember()) && (!invoke.equals("c!cash") && !invoke.equals("c!trocar")
				&& !invoke.equals("c!comprar") && !invoke.equals("c!familia") && !invoke.equals("c!vip"))) {
			return;
		}
		if (!Utils.isIgnoredServer(g)) {
			ICommand cmd = this.getCommand(invoke);

			if (cmd != null) {
				Date d1 = new Date(u.getTimeCreated().toInstant().toEpochMilli());
				Date d2 = new Date();

				long dt = (d2.getTime() - d1.getTime()) + 3600000;
				long dias = (dt / 86400000L);

				if (dias <= 1 && !u.getId().equals("759185717481570304") && !u.getId().equals("380570412314001410")) {
					Utils.enviarEmbed(tc, u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** A conta deve estar criada há pelo menos **2** dias no discord para poder utilizar comandos do bot. Em caso de contas fakes para farmar dinheiro, a conta original pode ser banida do BOT.",
							(ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (ConfigManager.hasChat(g, tc)) {
					if (time.containsKey(u.getId())
							&& (System.currentTimeMillis() >= (time.get(u.getId()) + 7200000))) {
						if (!captcha.containsKey(u.getId())) {
							Random r = new Random();
							String a = "1234567890";
							String cod = "";
							for (int i = 0; i != 4; i++) {
								cod = cod + a.charAt(r.nextInt(a.length() - 1));
							}
							Utils.enviarEmbed(tc, u, null,
									Utils.getEmote("nao").getFormatted()
											+ " **Erro:** Para continuar utilizando comandos, digite o código: **" + cod
											+ "**.",
									((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
											: null)),
									g.getName(), null, null, null, 0, true);
							captcha.put(u.getId(), cod);
						} else {
							String cod = captcha.get(u.getId());
							Utils.enviarEmbed(tc, u, null,
									Utils.getEmote("nao").getFormatted()
											+ " **Erro:** Para continuar utilizando comandos, digite o código: **" + cod
											+ "**.",
									((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
											: null)),
									g.getName(), null, null, null, 0, true);
						}
						return;
					}
					if (PresosManager.estaPreso(g, u) && !invoke.equals("c!fianca") && !invoke.equals("c!adv")
							&& !invoke.equals("c!familia") && !invoke.equals("c!vip")) {
						if (PresosManager.getPena(g, u) > System.currentTimeMillis()) {
							SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
							Utils.enviarEmbed(tc, u, null, "Você está preso(a) desde o dia **"
									+ fmt.format(PresosManager.getDiaPreso(g, u)) + "**!\n\nMotivo: **"
									+ PresosManager.getMotivo(g, u) + "**.\nSua pena acaba em: **"
									+ Utils.getTime(PresosManager.getPena(g, u) - System.currentTimeMillis())
									+ "**.\n\nVocê pode ser solto a qualquer momento com você ou alguém pagando sua fiança com o comando `c!fianca`.",
									((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
											: null)),
									g.getName(), null, null, null, 0, true);
							return;
						} else {
							PresosManager.soltar(g, u);
						}
					}
				}
				Main.comandosExecutados++;
				if (!time.containsKey(u.getId())) {
					time.put(u.getId(), System.currentTimeMillis());
				}
				if (cooldown.containsKey(u.getId() + ";" + invoke) && !invoke.equals("c!lance")) {
					Date d = new Date(cooldown.get(u.getId() + ";" + invoke));
					d.setSeconds(d.getSeconds() + 2);
					d2 = new Date();
					if (d2.getTime() < d.getTime()) {
						Utils.enviarEmbed(tc, u, null,
								Utils.getEmote("nao").getFormatted()
										+ " Aguarde, você só poderá digitar este comando novamente em **"
										+ (Utils.getTime(d.getTime() - d2.getTime()).equals("") ? "alguns milissegundos"
												: Utils.getTime(d.getTime() - d2.getTime()))
										+ "**!",
								(ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null),
								g.getName(), null, null, null, 0, true);
						return;
					}
				}

				List<String> args = Arrays.asList(split).subList(1, split.length);

				CommandContext ctx = new CommandContext(event, args);

				cmd.handle(ctx);

				Date d = new Date();
				System.out.println(
						Utils.getTime() + ConsoleColors.BRIGHT_YELLOW + u.getName() + ConsoleColors.BRIGHT_GREEN + ": "
								+ invoke + ConsoleColors.BRIGHT_MAGENTA + " [" + event.getGuild().getName() + "]");
				cooldown.put(u.getId() + ";" + invoke, d.getTime());
			}
		}
	}

}
