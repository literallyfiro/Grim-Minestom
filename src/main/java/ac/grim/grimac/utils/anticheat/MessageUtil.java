package ac.grim.grimac.utils.anticheat;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.utils.nmsutil.ChatUtil;
import lombok.experimental.UtilityClass;
import net.minestom.server.coordinate.Point;

import java.util.regex.Pattern;

@UtilityClass
public class MessageUtil {
    public String toUnlabledString(Point vec) {
        return vec == null ? "null" : vec.x() + ", " + vec.y() + ", " + vec.z();
    }

    public String format(String string) {
        string = formatWithNoColor(string);
//        string = translateHexCodes(string);
        return ChatUtil.translateAlternateColorCodes(string);
    }

    public String formatWithNoColor(String string) {
        return string.replace("%prefix%", GrimAPI.INSTANCE.getConfigManager().getPrefix());
    }

    private static final Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})");

    private String translateHexCodes(String message) {
//        Matcher matcher = HEX_PATTERN.matcher(message);
//        StringBuilder sb = new StringBuilder(message.length());
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
