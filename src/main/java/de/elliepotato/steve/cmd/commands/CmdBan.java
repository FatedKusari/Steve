package de.elliepotato.steve.cmd.commands;

import com.google.common.collect.Lists;
import de.elliepotato.steve.Steve;
import de.elliepotato.steve.cmd.model.Command;
import de.elliepotato.steve.cmd.model.CommandEnvironment;
import de.elliepotato.steve.util.Constants;
import de.elliepotato.steve.util.UtilString;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author Ellie for VentureNode LLC
 * at 10/02/2018
 */
public class CmdBan extends Command {

    /**
     * Quick perm ban command.
     *
     * @param steve Bot instance.
     */
    public CmdBan(Steve steve) {
        super(steve, "ban", "Ban a user (forever)", Lists.newArrayList(), Permission.KICK_MEMBERS,
                Lists.newArrayList("<target> [reason]"));
    }

    @Override
    protected void abstractExecute(@NotNull CommandEnvironment environment) {
        final TextChannel channel = environment.getChannel();
        final Member sender = environment.getSender();
        final String[] args = environment.getArgs();

        final User toBan = getBot().parseUser(args[0]);
        if (toBan == null) {
            getBot().messageChannel(channel, ":x: " + sender.getAsMention() + ", I couldn't find user `" + args[0] + "`.");
            return;
        }

        if (!PermissionUtil.canInteract(channel.getGuild().getMember(getBot().getJda().getUserById(Constants.PRESUMED_SELF.getIdLong())), sender))
            return;

        String reason = null;
        if (args.length > 1) {
            reason = UtilString.getFinalArg(args, 1);
        }

        getBot().modLog(channel.getGuild(), getBot().getEmbedBuilder(Steve.DiscordColor.BAN)
                .setTitle("Banned " + toBan.getName() + "#" + toBan.getDiscriminator() + " (" + toBan.getId() + ")")
                .addField("Banner", (sender.getUser().getName() + "#" + sender.getUser().getDiscriminator()), true)
                .addField("Reason", (reason != null ? reason : "No reason specified."), false));

        getBot().tempMessage(channel, ":ok_hand: Banned " + toBan.getName() + "#" + toBan.getDiscriminator() + " out this world (forever)! :eyes:"
                + (reason != null ? " (`" + reason + "`)" : ""), 10, environment.getMessage());

        if (reason != null) {
            channel.getGuild().getController().ban(channel.getGuild().getMember(toBan), 1, reason).queue();
        } else channel.getGuild().getController().ban(channel.getGuild().getMember(toBan), 1).queue();

    }

}