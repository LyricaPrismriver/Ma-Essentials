package com.maciej916.maessentials.commands;

import com.maciej916.maessentials.classes.player.EssentialPlayer;
import com.maciej916.maessentials.config.ConfigValues;
import com.maciej916.maessentials.data.DataManager;
import com.maciej916.maessentials.libs.Methods;
import com.maciej916.maessentials.libs.Teleport;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import static com.maciej916.maessentials.libs.Methods.requestTeleport;
import static com.maciej916.maessentials.libs.Methods.simpleTeleport;

public class CommandTpa{
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> builder = Commands.literal("tpa").requires(source -> source.hasPermissionLevel(0));
        builder
            .executes(context -> tpa(context))
            .then(Commands.argument("targetPlayer", EntityArgument.players())
                    .executes(context -> tpaArgs(context)));
        dispatcher.register(builder);
    }

    private static int tpa(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().asPlayer();
        player.sendMessage(Methods.formatText("maessentials.provide.player"));
        return Command.SINGLE_SUCCESS;
    }

    private static int tpaArgs(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().asPlayer();
        ServerPlayerEntity targetPlayer = EntityArgument.getPlayer(context, "targetPlayer");
        doTpa(player, targetPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static void doTpa(ServerPlayerEntity player, ServerPlayerEntity target) {
        EssentialPlayer eslPlayer = DataManager.getPlayer(player);

        if (player == target) {
            player.sendMessage(Methods.formatText("tpa.maessentials.self"));
            return;
        }

        long cooldown = eslPlayer.getUsage().getTeleportCooldown("tpa", ConfigValues.tpa_cooldown);
        if (cooldown != 0) {
            player.sendMessage(Methods.formatText("maessentials.cooldown.teleport", cooldown));
            return;
        }

        eslPlayer.getUsage().setCommandUsage("tpa");
        eslPlayer.saveData();

        if (requestTeleport(player, player, target, ConfigValues.tpa_timeout)) {
            player.sendMessage(Methods.formatText("tpa.maessentials.request", target.getDisplayName()));
            target.sendMessage(Methods.formatText("tpa.maessentials.request.target", player.getDisplayName()));

            ClickEvent clickEventAccept = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tpaccept " + player.getDisplayName().getString());
            HoverEvent eventHoverAccept = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Methods.formatText("tpa.maessentials.request.target.accept.hover", "/tpaccept " + player.getDisplayName().getString()));
            TextComponent textAccept = new StringTextComponent("/tpaccept");
            textAccept.getStyle().setClickEvent(clickEventAccept);
            textAccept.getStyle().setHoverEvent(eventHoverAccept);
            target.sendMessage(Methods.formatText("tpa.maessentials.request.target.accept", textAccept));

            ClickEvent clickEventDeny = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tpdeny " + player.getDisplayName().getString());
            HoverEvent eventHoverDeny = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Methods.formatText("tpa.maessentials.request.target.deny.hover", "/tpdeny " + player.getDisplayName().getString()));
            TextComponent textDeny = new StringTextComponent("/tpdeny");
            textDeny.getStyle().setClickEvent(clickEventDeny);
            textDeny.getStyle().setHoverEvent(eventHoverDeny);
            target.sendMessage(Methods.formatText("tpa.maessentials.request.target.deny", textDeny));
        }
    }
}