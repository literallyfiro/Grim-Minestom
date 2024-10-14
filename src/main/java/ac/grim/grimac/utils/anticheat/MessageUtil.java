package ac.grim.grimac.utils.anticheat;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.utils.nmsutil.ChatUtil;
import ac.grim.grimac.utils.vector.Vector3f;
import ac.grim.grimac.utils.vector.Vector3i;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageUtil {
    public String toUnlabledString(Vector3i vec) {
        return vec == null ? "null" : vec.x + ", " + vec.y + ", " + vec.z;
    }

    public String toUnlabledString(Vector3f vec) {
        return vec == null ? "null" : vec.x + ", " + vec.y + ", " + vec.z;
    }

    public String format(String string) {
        string = formatWithNoColor(string);
//        string = translateHexCodes(string);
        return ChatUtil.translateAlternateColorCodes(string);
    }

    public String formatWithNoColor(String string) {
        return string.replace("%prefix%", GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("prefix", "&bGrim &8»"));
    }

    private String translateHexCodes(String message) {
//        final String hexPattern = "#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})";
//        Matcher matcher = Pattern.compile(hexPattern).matcher(message);
//        StringBuffer sb = new StringBuffer(message.length());
//        while (matcher.find()) {
//            String hex = matcher.group(1);
//            ChatColor color = ChatColor.of("#" + hex);
//            matcher.appendReplacement(sb, color.toString());
//        }
//        matcher.appendTail(sb);
//        return sb.toString();
        return message;
    }
}
