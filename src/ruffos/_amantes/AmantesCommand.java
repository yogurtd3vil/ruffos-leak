package ruffos._amantes;

import java.awt.Color;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import ruffos._casamento.CasamentoManager;
import ruffos._config.ConfigManager;
import ruffos._cooldowns.CooldownsManager;
import ruffos._economia.EconomiaManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class AmantesCommand extends ListenerAdapter implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		Message msg = ctx.getMessage();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			if (ctx.getArgs().size() == 0) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"<a:nao_ruffos:717039420389458021> **Comando incompleto:**\n\nPara enviar o convite de amante para um usuário, use `c!amantes add @usuário`.\nPara remover um amante, use `c!amantes remover [ID]`.\nPara listar seus amantes, use `c!amantes listar`.\n"
								+ (!Utils.isFakeGuild(g)
										? "Para fazer GF com todos os seus amantes, use `c!amantes gf`."
										: "Para fazer SF com todos os seus amantes, use `c!amantes sf`."),
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (ctx.getArgs().get(0).equalsIgnoreCase("add")) {
				if (msg.getMentions().getMembers().size() != 1) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							"<a:nao_ruffos:717039420389458021> **Comando errado:** Use c!amantes add @usuário.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (!CasamentoManager.casado(g, u.getId())) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							"<a:nao_ruffos:717039420389458021> **Erro:** Você deve estar casado(a) para ter novos(as) amantes.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				User amante = msg.getMentions().getMembers().get(0).getUser();
				if (u == amante) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							"<a:nao_ruffos:717039420389458021> **Erro:** Você não pode ser amante de si mesmo(a).",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				List<String> amantes = AmantesManager.getAmantes(g, u);
				if (amantes.contains(amante.getId())) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							"<a:nao_ruffos:717039420389458021> **Erro:** Este(a) usuário(a) já é um de seus(uas) amantes.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTimestamp(Instant.now());
				eb.setTitle("😈 CONVITE PARA AMANTE!");
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				boolean casado = CasamentoManager.getParceiro(g, amante.getId()) != null;
				String casad = u.getAsMention() + " está convidando " + amante.getAsMention() + " para por gaia em <@"
						+ CasamentoManager.getParceiro(g, amante.getId())
						+ "> e disse: O que você acha da gente por chifre nesse(a) trouxa que pensa que você jamais faria isso com ele(a)? Todo(a) bobinho(a), pensando que quando você dorme com ele(a), você realmente está em apenas uma call, sendo que sabemos que você usa o Discord do navegador e o do PC juntos e enquanto ele(a) dorme, você fica me gadando e pedindo "
						+ (Utils.isFakeWorld(g) ? "SF" : "GF") + ". Hoje eu resolvo aceitar, e aí, vamos por gaia?";
				String ncasad = u.getAsMention() + " está convidando " + amante.getAsMention()
						+ " para uma amizade colorida e disse: Lembra as vezes que eu lhe via em **Eventos +18** e insistia que você era fake? Era unicamente para você me provar que não por cam. Sempre quis ver todo seu web corpo, lindo, gostoso e sensual. Essa bundinha cabeluda sua... Hum... Que gostoso. Então, vamos?";
				eb.setDescription(casado ? casad : ncasad);
				Button sim = Button.success("simamante;" + u.getId() + ";" + amante.getId(),
						Emoji.fromFormatted("<a:sim_ruffos:717039486646616155>")),
						nao = Button.danger("naoamante;" + u.getId() + ";" + amante.getId(),
								Emoji.fromFormatted("<a:nao_ruffos:717039420389458021>"));
				ctx.getChannel().sendMessageEmbeds(eb.build()).queue((msgg) -> {
					msgg.editMessageComponents(ActionRow.of(sim, nao)).queue();
					msgg.delete().queueAfter(30, TimeUnit.SECONDS);
				});
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("remover")) {
				if (ctx.getArgs().size() != 2) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							"<a:nao_ruffos:717039420389458021> **Comando errado:** Use c!amantes remover [ID].",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				List<String> amantes = AmantesManager.getAmantes(g, u);
				if (!amantes.contains(ctx.getArgs().get(1))) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							"<a:nao_ruffos:717039420389458021> **Erro:** Este(a) usuário(a) não é um de seus(uas) amantes.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				AmantesManager.removerAmante(g, u, ctx.getArgs().get(1));
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"<@" + ctx.getArgs().get(1) + "> não é mais um(a) dos(as) amantes de " + u.getAsMention() + "!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("listar")) {
				AmantesManager.verificarAmantes(g, u);
				List<String> amantes = AmantesManager.getAmantes(g, u);
				String am = "";
				for (String s : amantes) {
					am = am + "<@" + s + ">, ";
				}
				am = StringUtils.chop(am);
				am = StringUtils.chop(am);
				Utils.enviarEmbed(ctx.getChannel(), u, null, "Sua lista de amantes:",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				if (!am.isEmpty()) {
					ctx.getChannel().sendMessage(am).queue();
				} else {
					ctx.getChannel().sendMessage("Lista vazia.").queue();
				}
				Utils.enviarEmbed(ctx.getChannel(), u, null, "Fim da lista.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("gf") && !(Utils.isFakeGuild(g))) {
				AmantesManager.verificarAmantes(g, u);
				long time = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo")).getTimeInMillis();
				if (CooldownsManager.getCooldownTime(g, u.getId(), "amantes") >= time) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							"<a:nao_ruffos:717039420389458021> Aguarde, você só poderá fazer GF com seus(uas) amantes novamente em **"
									+ Utils.getTime(CooldownsManager.getCooldownTime(g, u.getId(), "amantes") - time)
									+ "**!",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				List<String> amantes = AmantesManager.getAmantes(g, u);
				int acumulado = 0;
				Random r = new Random();
				for (int i = 0; i != amantes.size(); i++) {
					int ganho = r.nextInt(350);
					while (ganho < 150) {
						ganho = r.nextInt(350);
					}
					acumulado += ganho;
				}
				EconomiaManager.addDinheiroMaos(g, u, acumulado);
				CooldownsManager.addCooldown(g, u.getId(), "amantes", Long.parseLong("1800000"));
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"Você fez GF com todos(as) os(as) seus(uas) **" + amantes.size() + "** amantes e recebeu **"
								+ Utils.getDinheiro(acumulado) + "**.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("sf") && (Utils.isFakeGuild(g))) {
				AmantesManager.verificarAmantes(g, u);
				long time = new Date().getTime();
				if (CooldownsManager.getCooldownTime(g, u.getId(), "amantes") >= time) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							"<a:nao_ruffos:717039420389458021> Aguarde, você só poderá fazer SF com seus(uas) amantes novamente em **"
									+ Utils.getTime(CooldownsManager.getCooldownTime(g, u.getId(), "amantes") - time)
									+ "**!",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				List<String> amantes = AmantesManager.getAmantes(g, u);
				int acumulado = 0;
				Random r = new Random();
				for (int i = 0; i != amantes.size(); i++) {
					int ganho = r.nextInt(350);
					while (ganho < 150) {
						ganho = r.nextInt(350);
					}
					acumulado += ganho;
				}
				EconomiaManager.addDinheiroMaos(g, u, acumulado);
				CooldownsManager.addCooldown(g, u.getId(), "amantes", Long.parseLong("1800000"));
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"Você fez SF com todos(as) os(as) seus(uas) **" + amantes.size() + "** amantes e recebeu **"
								+ Utils.getDinheiro(acumulado) + "**.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			}
		}
	}

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		Button b = event.getButton();
		Guild g = event.getGuild();
		TextChannel tc = event.getGuild().getTextChannelById(event.getChannel().getId());
		String[] split = b.getId().split(";");
		if (split[0].equals("simamante")) {
			String casar1 = split[1];
			String casar2 = split[2];
			User ucasar1 = event.getGuild().retrieveMemberById(casar1).complete().getUser();
			if (event.getUser().getId().equals(casar2)) {
				event.getMessage().delete().queue();
				List<String> amantes = AmantesManager.getAmantes(g, ucasar1);
				if (CasamentoManager.getParceiro(g, casar1) != null
						&& CasamentoManager.getParceiro(g, casar1).equals(casar2)) {
					Utils.enviarEmbed(tc, ucasar1, null,
							"<a:nao_ruffos:717039420389458021> **Erro:** Você não pode se tornar amante de quem você é casado(a).",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (amantes.size() >= 10) {
					Utils.enviarEmbed(tc, ucasar1, null,
							"<a:nao_ruffos:717039420389458021> **Erro:** Você excedeu o limite de amantes **(10)**.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (amantes.contains(casar2)) {
					Utils.enviarEmbed(tc, ucasar1, null,
							"<a:nao_ruffos:717039420389458021> **Erro:** Você já é um dos(as) amantes de <" + casar2
									+ ">.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				AmantesManager.addAmante(g, ucasar1, event.getGuild().retrieveMemberById(casar2).complete().getUser());
				Utils.enviarEmbed(tc, ucasar1, null,
						"<@" + casar2 + "> aceitou ser um(a) dos(as) amantes de " + ucasar1.getAsMention() + "!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			}
		} else if (split[0].equals("naoamante")) {
			String casar1 = split[1];
			String casar2 = split[2];
			User ucasar1 = event.getGuild().retrieveMemberById(casar1).complete().getUser();
			if (event.getUser().getId().equals(casar2)) {
				event.getMessage().delete().queue();
				Utils.enviarEmbed(tc, ucasar1, null,
						"<@" + casar2 + "> recusou ser um(a) dos(as) amantes de " + ucasar1.getAsMention() + "!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			}
		}
	}

	@Override
	public String getName() {
		return "c!amantes";
	}

	@Override
	public String getHelp() {
		return "Sistema de amantes";
	}

}
