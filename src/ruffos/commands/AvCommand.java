package ruffos.commands;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class AvCommand implements ICommand {

	List<String> av = new ArrayList<>();

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		ctx.getMessage().delete().queue();
		if (ctx.getChannel().getId().equals("793992497264984074")) {
			if (ctx.getArgs().size() == 0) {
				EmbedBuilder eb = new EmbedBuilder();
				eb.setFooter(g.getName());
				eb.setTitle("📷 " + ctx.getMember().getUser().getName());
				eb.setTimestamp(Instant.now());
				eb.setColor(Color.GREEN);
				eb.setImage(ctx.getMember().getUser().getAvatarUrl() + "?size=1024");
				ctx.getChannel().sendMessageEmbeds(eb.build())
						.queue(msg -> msg.delete().queueAfter(1, TimeUnit.MINUTES));
				av.add(ctx.getMember().getId());
			} else if (Utils.getMentionedMember(g, ctx.getArgs().get(0)) != null) {
				Member member = Utils.getMentionedMember(g, ctx.getArgs().get(0));
				if (av.contains(member.getId())) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							"Recentemente deram avatar em " + member.getAsMention()
									+ ", aguarde para fazer isso novamente.",
							Color.GREEN, g.getName(), null, null, null, 10, true);
					return;
				} else if (av.contains(u.getId())) {
					if (av.contains(member.getId())) {
						Utils.enviarEmbed(ctx.getChannel(), u, null,
								"Você deu avatar em um usuário recentemente, aguarde para fazer isso novamente.",
								Color.GREEN, g.getName(), null, null, null, 10, true);
						return;
					}
				}
				if (!ctx.getMember().hasPermission(Permission.ADMINISTRATOR)) {
					av.add(u.getId());
				}
				av.add(member.getId());
				EmbedBuilder eb = new EmbedBuilder();
				eb.setFooter(g.getName());
				eb.setTitle("📷 " + member.getUser().getName());
				eb.setTimestamp(Instant.now());
				eb.setColor(Color.GREEN);
				eb.setImage(member.getUser().getAvatarUrl() + "?size=1024");
				ctx.getChannel().sendMessageEmbeds(eb.build())
						.queue(msg -> msg.delete().queueAfter(1, TimeUnit.MINUTES));
				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						av.remove(u.getId());
						av.remove(member.getId());
					}
				}, 600000);
			}
		}
	}

	@Override
	public String getName() {
		return "c!av";
	}

	@Override
	public String getHelp() {
		return null;
	}

}
