package com.sakura.bot.utils;

import java.util.List;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.managers.GuildController;

public final class RoleUtil {
    private RoleUtil() {
    }

    public static void addRole(Guild guild, User user, String roleName) {
        Role role = findRole(guild, roleName);
        Member member = FinderUtil.findMembers(user.getId(), guild)
            .stream()
            .findFirst()
            .orElseThrow(IllegalStateException::new);
        new GuildController(guild).addSingleRoleToMember(member, role)
            .queue();
    }

    public static void removeRole(Guild guild, User user, String roleName) {
        Role role = findRole(guild, roleName);
        Member member = FinderUtil.findMembers(user.getId(), guild)
            .stream()
            .findFirst()
            .orElseThrow(IllegalStateException::new);
        new GuildController(guild).removeSingleRoleFromMember(member, role)
            .queue();
    }

    public static Role findRole(Guild guild, String roleName) {
        return guild.getRolesByName(roleName, false).stream()
            .findFirst()
            .orElseThrow(IllegalStateException::new);
    }

    public static List<Role> getMemberRoles(CommandEvent event) {
        Guild guild = GuildUtil.getGuild(event.getEvent());
        Member member = FinderUtil.findMembers(event.getAuthor().getId(), guild)
            .stream()
            .findFirst()
            .orElseThrow(IllegalStateException::new);
        return member.getRoles();
    }
}
