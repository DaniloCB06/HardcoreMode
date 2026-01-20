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
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Set;

public class HardcoreGuiCommand extends AbstractPlayerCommand {
    private final HardcoreModePlugin plugin;

    public HardcoreGuiCommand(HardcoreModePlugin plugin) {
        super("hardgui", "Open Hardcore Mode settings");
        this.plugin = plugin;
        requirePermission(HytalePermissions.fromCommand("hardgui"));
        setPermissionGroups("OP", "ADMIN");
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

        if (!isAuthorized(playerRef)) {
            context.sendMessage(Message.raw("You do not have permission to use this command."));
            return;
        }

        player.getPageManager().openCustomPage(ref, store, new HardcoreSettingsPage(plugin, playerRef));
    }

    private boolean isAuthorized(PlayerRef playerRef) {
        if (playerRef == null) {
            return false;
        }

        PermissionsModule permissions = PermissionsModule.get();
        if (permissions == null) {
            return false;
        }

        Set<String> groups = permissions.getGroupsForUser(playerRef.getUuid());
        if (groups == null || groups.isEmpty()) {
            return false;
        }

        for (String group : groups) {
            if ("OP".equalsIgnoreCase(group) || "ADMIN".equalsIgnoreCase(group)) {
                return true;
            }
        }

        return false;
    }
}
