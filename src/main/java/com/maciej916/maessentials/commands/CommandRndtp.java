package com.maciej916.maessentials.commands;

import com.maciej916.maessentials.classes.Location;
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
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

import static com.maciej916.maessentials.libs.Methods.simpleTeleport;

public class CommandRndtp {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> builder = Commands.literal("rndtp").requires(source -> source.hasPermissionLevel(0));
        builder.executes(context -> rndtp(context));
        dispatcher.register(builder);
    }

    private static int rndtp(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerWorld world = context.getSource().getWorld();
        ServerPlayerEntity player = context.getSource().asPlayer();
        EssentialPlayer eslPlayer = DataManager.getPlayer(player);

        long cooldown = eslPlayer.getUsage().getCommandCooldown("rndtp", ConfigValues.rndtp_cooldown);
        if (cooldown != 0) {
            player.sendMessage(Methods.formatText("maessentials.cooldown", cooldown));
            return Command.SINGLE_SUCCESS;
        }

        Location location = findRandomTp(world, player, 0);
        if (location == null) {
            player.sendMessage(Methods.formatText("rndtp.maessentials.not_found"));
            return Command.SINGLE_SUCCESS;
        }

        eslPlayer.getUsage().setCommandUsage("rndtp");
        eslPlayer.saveData();
        if (simpleTeleport(player, location, "rndtp", ConfigValues.rndtp_delay)) {
            if (ConfigValues.rndtp_delay == 0) {
                player.sendMessage(Methods.formatText("rndtp.maessentials.teleport"));
            } else {
                player.sendMessage(Methods.formatText("rndtp.maessentials.teleport.wait", ConfigValues.rndtp_delay));
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    private static Location findRandomTp(World world, ServerPlayerEntity player, int count) {
        if (count == 10) {
            return null;
        }
        count++;

        Location spawnLocation = DataManager.getWorld().getSpawn();
        Random rand = new Random();

        int min = ConfigValues.rndtp_range_min;
        int max = ConfigValues.rndtp_range_max;

        int x = (int) Math.round(spawnLocation.x) + rand.nextInt(max + min) - min;
        int y = world.getMaxHeight();
        int z = (int) Math.round(spawnLocation.z) + rand.nextInt(max + min) - min;

        Chunk chunk = world.getChunk(x >> 4, z >> 4);
        Biome biome = world.getBiome(new BlockPos(x, y, z));

        if (biome.getCategory().getName().equals("ocean")) {
            return findRandomTp(world, player, count);
        }

        while (y > 0) {
            y--;
            BlockPos groundPos = new BlockPos(x, y-2, z);
            if (!chunk.getBlockState(groundPos).getMaterial().equals(Material.AIR)) {
                BlockPos legPos = new BlockPos(x, y-1, z);
                if (chunk.getBlockState(legPos).getMaterial().equals(Material.AIR)) {
                    BlockPos headPos = new BlockPos(x, y, z);
                    if (chunk.getBlockState(headPos).getMaterial().equals(Material.AIR)) {
                        return new Location(x + 0.5, y, z+ 0.5, 0, 0, player.dimension.getId());
                    }
                }
            }
        }
        return findRandomTp(world, player, count);
    }
}