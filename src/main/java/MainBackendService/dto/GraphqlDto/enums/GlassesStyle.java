package MainBackendService.dto.GraphqlDto.enums;

import com.jooq.sample.model.enums.UseravatarGlassesStyle;

public enum GlassesStyle {
    ROUND(UseravatarGlassesStyle.round),
    SQUARE(UseravatarGlassesStyle.square),
    NONE(UseravatarGlassesStyle.none);

    private final UseravatarGlassesStyle useravatarGlassesStyle;

    GlassesStyle(UseravatarGlassesStyle useravatarGlassesStyle) {
        this.useravatarGlassesStyle = useravatarGlassesStyle;
    }

    public static GlassesStyle fromString(Object value) {
        if (value == null) return null;
        try {
            return GlassesStyle.valueOf(value.toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public UseravatarGlassesStyle getGlassesStyle() {
        return useravatarGlassesStyle;
    }
}
