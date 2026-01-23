package com.example.plugin.commands;

import com.example.plugin.HardcoreModePlugin;
import com.example.plugin.ui.HardcoreSettingsPage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.permissions.HytalePermissions;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class HardcoreGuiCommand extends AbstractPlayerCommand {
    private final HardcoreModePlugin plugin;

    public HardcoreGuiCommand(HardcoreModePlugin plugin) {
        super("hardgui", "Open Hardcore Mode settings");
        this.plugin = plugin;

        requirePermission(HytalePermissions.fromCommand("hardgui"));

    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void execute(
            CommandContext context,
            Store<EntityStore> store,
            Ref<EntityStore> ref,
            PlayerRef playerRef,
            World world
    ) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            context.sendMessage(Message.raw("Player not found."));
            return;
        }
        
        player.getPageManager().openCustomPage(ref, store, new HardcoreSettingsPage(plugin, playerRef));
    }
}
