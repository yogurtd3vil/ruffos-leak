package ruffos._perfil;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.Instant;

import org.apache.commons.lang3.StringUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import ruffos._amantes.AmantesManager;
import ruffos._casamento.CasamentoManager;
import ruffos._clans.ClansManager;
import ruffos._config.ConfigManager;
import ruffos._economia.EconomiaManager;
import ruffos._leveis.LeveisManager;
import ruffos._prisao.PresosManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class PerfilCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			if (ctx.getArgs().size() == 0) {
				if (!PersonagemManager.existe(g, u)) {
					PersonagemManager.criarPersonagem(g, u);
				}

				EmbedBuilder eb = new EmbedBuilder();
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription((PersonagemManager.get(g, u, "sexo").equals("sexo")
						? "Você ainda não possui um personagem! Use: **c!personagem**\n\nInformações do seu perfil:"
						: "Informações do seu perfil:"));
				eb.setFooter(g.getName());
				eb.setTimestamp(Instant.now());
				eb.addField("Seu dinheiro em mãos:", Utils.getDinheiro(EconomiaManager.getDinheiroMaos(g, u)), true);
				eb.addField("Seu dinheiro no banco:", Utils.getDinheiro(EconomiaManager.getDinheiroBanco(g, u)), true);
				eb.addField("Profissão:",
						(EconomiaManager.hasTrabalho(g, u) ? EconomiaManager.getTrabalho(g, u) : "Nenhuma"), true);
				int level = LeveisManager.getLevel(u.getId(), g);
				int xp = LeveisManager.getXP(u.getId(), g);
				int xpToNextLevel = LeveisManager.getXPToNextLevel(level + 1);
				int percent = xp * 100 / xpToNextLevel;
				eb.addField("Seu level:", "" + LeveisManager.getLevel(u.getId(), g), true);
				eb.addField("Progresso para o próximo level:",
						getProgressBar(xp, xpToNextLevel, 10) + " (**" + Math.round((percent * 10.0) / 10) + "%**)",
						true);
				eb.addField("Armado com:",
						((EconomiaManager.getArma(g, u) != null ? Utils.getArma(EconomiaManager.getArma(g, u))
								: "Nada")),
						true);
				eb.addField("Munições:", +EconomiaManager.getUsos(g, u) + "/30", true);
				String bebida = EconomiaManager.getBebida(g, u);
				eb.addField("Bebendo:", ((bebida != null) ? Utils.getBebida(bebida) : "Nada"), true);
				eb.addField("Copos restantes:",
						+EconomiaManager.getCopos(g, u) + "/" + ((bebida != null) ? Utils.getMaxBebidas(bebida) : 0),
						true);
				eb.addField("Casado(a) com:",
						(CasamentoManager.casado(g, u.getId()) ? "<@" + CasamentoManager.getParceiro(g, u.getId())
								+ "> [" + CasamentoManager.getData(g, u.getId()) + "]" : "Solteiro(a) no momento :c"),
						true);
				eb.addField("Passagens pela polícia:", "" + PresosManager.getPassagens(g, u), true);
				eb.addField("Amantes:", "" + AmantesManager.getAmantes(g, u).size(), true);
				eb.addField("Clan:", (ClansManager.hasClan(u) ? ClansManager.getClan(u) : "Nenhum"), true);
				eb.addField("Sobre mim:", PersonagemManager.get(g, u, "sobremim"), false);
				eb.setFooter(g.getName() + " » Para personalizar seu perfil: c!sobremim [bio] & c!cor [cor]");
				eb.setTimestamp(Instant.now());
				Color color = null;
				String cor = PersonagemManager.get(g, u, "cor");
				if (!PersonagemManager.get(g, u, "avatar").equals("avatar")) {
					eb.setImage(PersonagemManager.get(g, u, "avatar"));
				}
				if (!cor.equals("cor")) {
					try {
						try {
							java.lang.reflect.Field field = Class.forName("java.awt.Color").getField(cor);
							color = (Color) field.get(null);
						} catch (Exception e) {
						}
						if (color == null) {
							color = Color.decode(cor);
						}
					} catch (NumberFormatException e) {

					}
				}
				if (color != null) {
					eb.setColor(color);
				}
				ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
			} else if (ctx.getArgs().size() == 1) {
				if (ctx.getMessage().getMentions().getMembers().size() != 1) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Comando errado:** Use c!perfil @usuário",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				Member m = ctx.getMessage().getMentions().getMembers().get(0);
				if (!EconomiaManager.existeUser(g, u)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Este usuário não existe no banco de dados do Ruffos.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				u = m.getUser();
				if (!PersonagemManager.existe(g, u)) {
					PersonagemManager.criarPersonagem(g, u);
				}
				EmbedBuilder eb = new EmbedBuilder();
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription((PersonagemManager.get(g, u, "sexo").equals("sexo")
						? "Você ainda não possui um personagem! Use: **c!personagem**\n\nInformações do seu perfil:"
						: "Informações do seu perfil:"));
				eb.setFooter(g.getName());
				eb.setTimestamp(Instant.now());
				eb.addField("Seu dinheiro em mãos:", Utils.getDinheiro(EconomiaManager.getDinheiroMaos(g, u)), true);
				eb.addField("Seu dinheiro no banco:", Utils.getDinheiro(EconomiaManager.getDinheiroBanco(g, u)), true);
				eb.addField("Profissão:",
						(EconomiaManager.hasTrabalho(g, u) ? EconomiaManager.getTrabalho(g, u) : "Nenhuma"), true);
				int level = LeveisManager.getLevel(u.getId(), g);
				int xp = LeveisManager.getXP(u.getId(), g);
				int xpToNextLevel = LeveisManager.getXPToNextLevel(level + 1);
				int percent = xp * 100 / xpToNextLevel;
				eb.addField("Seu level:", "" + LeveisManager.getLevel(u.getId(), g), true);
				eb.addField("Progresso para o próximo level:",
						getProgressBar(xp, xpToNextLevel, 10) + " (**" + Math.round((percent * 10.0) / 10) + "%**)",
						true);
				eb.addField("Armado com:",
						((EconomiaManager.getArma(g, u) != null ? Utils.getArma(EconomiaManager.getArma(g, u))
								: "Nada")),
						true);
				eb.addField("Munições:", +EconomiaManager.getUsos(g, u) + "/30", true);
				String bebida = EconomiaManager.getBebida(g, u);
				eb.addField("Bebendo:", ((bebida != null) ? Utils.getBebida(bebida) : "Nada"), true);
				eb.addField("Copos restantes:", "" + EconomiaManager.getCopos(g, u) + "/"
						+ ((bebida != null) ? Utils.getMaxBebidas(bebida) : 0), true);
				eb.addField("Casado(a) com:",
						(CasamentoManager.casado(g, u.getId())
								? "<@" + CasamentoManager.getParceiro(g, u.getId()) + "> ["
										+ CasamentoManager.getData(g, u.getId()) + "]"
								: "**Solteiro(a) no momento :c**"),
						true);
				eb.addField("Passagens pela polícia:", "" + PresosManager.getPassagens(g, u), true);
				eb.addField("Amantes:", "" + AmantesManager.getAmantes(g, u).size(), true);
				eb.addField("Clan:", (ClansManager.hasClan(u) ? ClansManager.getClan(u) : "Nenhum"), true);
				eb.addField("Sobre mim:", PersonagemManager.get(g, u, "sobremim"), false);
				eb.setFooter(g.getName() + " » Para personalizar seu perfil: c!sobremim [bio] & c!cor [cor]");
				eb.setTimestamp(Instant.now());
				Color color = null;
				String cor = PersonagemManager.get(g, u, "cor");
				if (!PersonagemManager.get(g, u, "avatar").equals("avatar")) {
					eb.setImage(PersonagemManager.get(g, u, "avatar"));
				}
				if (!cor.equals("cor")) {
					try {
						try {
							java.lang.reflect.Field field = Class.forName("java.awt.Color").getField(cor);
							color = (Color) field.get(null);
						} catch (Exception e) {
						}
						if (color == null) {
							color = Color.decode(cor);
						}
					} catch (NumberFormatException e) {

					}
				}
				if (color != null) {
					eb.setColor(color);
				}
				if (PresosManager.estaPreso(g, u)) {
					SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					eb.setTitle("ESTE USUÁRIO ESTÁ PRESO ATÉ " + fmt.format(PresosManager.getDiaPreso(g, u)));
				}
				ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
			}
		}
	}

	public String getProgressBar(int current, int max, int totalBars) {
		float percent = (float) current / max;
		int progressBars = (int) (totalBars * percent);
		return StringUtils.repeat("★", progressBars) + StringUtils.repeat("☆", totalBars - progressBars);
	}

	@Override
	public String getName() {
		return "c!perfil";
	}

	@Override
	public String getHelp() {
		return "Veja o seu ou o perfil de alguém.";
	}

}
