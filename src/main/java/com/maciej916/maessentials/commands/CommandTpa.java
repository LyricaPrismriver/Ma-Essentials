package com.maciej916.maessentials.commands;

import com.maciej916.maessentials.libs.Methods;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TextFormatting;

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
        player.sendMessage(Methods.formatText("command.maessentials.player.provide", TextFormatting.DARK_RED));
        return Command.SINGLE_SUCCESS;
    }

    private static int tpaArgs(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().asPlayer();
        ServerPlayerEntity requestedPlayer = EntityArgument.getPlayer(context, "targetPlayer");
        if (requestedPlayer != player) {
//            if (PlayerData.requestTeleport(player, player, requestedPlayer)) {
//                player.sendMessage(new TranslationTextComponent("command.maessentials.tpa.request",requestedPlayer.getDisplayName(), true));
//                requestedPlayer.sendMessage(new TranslationTextComponent("command.maessentials.tpa.target", player.getDisplayName(), true));
//            } else {
//                player.sendMessage(Methods.formatText("command.maessentials.tpa.exist", TextFormatting.DARK_RED, requestedPlayer.getDisplayName()));
//            }
        } else {
            player.sendMessage(Methods.formatText("command.maessentials.player.self", TextFormatting.DARK_RED));
        }
        return Command.SINGLE_SUCCESS;
    }
}