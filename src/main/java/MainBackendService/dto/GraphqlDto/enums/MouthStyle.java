package MainBackendService.dto.GraphqlDto.enums;

import com.jooq.sample.model.enums.UseravatarMouthStyle;

public enum MouthStyle {
    LAUGH(UseravatarMouthStyle.laugh),
    SMILE(UseravatarMouthStyle.smile),
    PEACE(UseravatarMouthStyle.peace);

    private final UseravatarMouthStyle useravatarMouthStyle;

    MouthStyle(UseravatarMouthStyle useravatarMouthStyle) {
        this.useravatarMouthStyle = useravatarMouthStyle;
    }

    public static MouthStyle fromString(Object value) {
        if (value == null) return null;
        try {
            return MouthStyle.valueOf(value.toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public UseravatarMouthStyle getMouthStyle() {
        return useravatarMouthStyle;
    }
}
