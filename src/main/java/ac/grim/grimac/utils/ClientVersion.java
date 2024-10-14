package ac.grim.grimac.utils;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public enum ClientVersion {
    V_1_7_10(5),
    V_1_8(47),
    V_1_9(107),
    V_1_9_1(108),
    V_1_9_2(109),
    V_1_9_3(110),
    V_1_10(210),
    V_1_11(315),
    V_1_11_1(316),
    V_1_12(335),
    V_1_12_1(338),
    V_1_12_2(340),
    V_1_13(393),
    V_1_13_1(401),
    V_1_13_2(404),
    V_1_14(477),
    V_1_14_1(480),
    V_1_14_2(485),
    V_1_14_3(490),
    V_1_14_4(498),
    V_1_15(573),
    V_1_15_1(575),
    V_1_15_2(578),
    V_1_16(735),
    V_1_16_1(736),
    V_1_16_2(751),
    V_1_16_3(753),
    V_1_16_4(754),
    V_1_17(755),
    V_1_17_1(756),
    V_1_18(757),
    V_1_18_2(758),
    V_1_19(759),
    V_1_19_1(760),
    V_1_19_3(761),
    V_1_19_4(762),
    V_1_20(763),
    V_1_20_2(764),
    V_1_20_3(765),
    V_1_20_5(766),
    V_1_21(767),
    UNKNOWN(-1, true);

    private final int protocolVersion;
    private final String name;

    ClientVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
        this.name = this.name().substring(2).replace("_", ".");
    }

    ClientVersion(int protocolVersion, boolean isNotRelease) {
        this.protocolVersion = protocolVersion;
        if (isNotRelease) {
            this.name = this.name();
        } else {
            this.name = this.name().substring(2).replace("_", ".");
        }
    }

    public static @NotNull ClientVersion getById(int protocolVersion) {
        for (ClientVersion version : values()) {
            if (version.protocolVersion > protocolVersion) {
                break;
            }

            if (version.protocolVersion == protocolVersion) {
                return version;
            }
        }
        return UNKNOWN;
    }

    public boolean isNewerThan(ClientVersion target) {
        return this.protocolVersion > target.protocolVersion;
    }

    public boolean isNewerThanOrEquals(ClientVersion target) {
        return this.protocolVersion >= target.protocolVersion;
    }

    public boolean isOlderThan(ClientVersion target) {
        return this.protocolVersion < target.protocolVersion;
    }

    public boolean isOlderThanOrEquals(ClientVersion target) {
        return this.protocolVersion <= target.protocolVersion;
    }

    public String getReleaseName() {
        return name;
    }

}
