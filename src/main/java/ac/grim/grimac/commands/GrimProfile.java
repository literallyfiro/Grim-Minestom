package ac.grim.grimac.commands;

public class GrimProfile{
//    @Subcommand("profile")
//    @CommandPermission("grim.profile")
//    @CommandCompletion("@players")
//    public void onConsoleDebug(CommandSender sender, OnlinePlayer target) {
//        Player player = null;
//        if (sender instanceof Player) player = (Player) sender;
//
//        // Short circuit due to minimum java requirements for MultiLib
//        if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_18) && MultiLibUtil.isExternalPlayer(target.getPlayer())) {
//            String alertString = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("player-not-this-server", "%prefix% &cThis player isn't on this server!");
//            sender.sendMessage(MessageUtil.format(alertString));
//            return;
//        }
//
//        GrimPlayer grimPlayer = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(target.getPlayer());
//        if (grimPlayer == null) {
//            String message = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("player-not-found", "%prefix% &cPlayer is exempt or offline!");
//            sender.sendMessage(MessageUtil.format(message));
//            return;
//        }
//
//        for (String message : GrimAPI.INSTANCE.getConfigManager().getConfig().getStringList("profile")) {
//            message = GrimAPI.INSTANCE.getExternalAPI().replaceVariables(grimPlayer, message, true);
//            sender.sendMessage(message);
//        }
//    }
}
