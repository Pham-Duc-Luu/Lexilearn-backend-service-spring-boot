package MainBackendService.dto.GraphqlDto.enums;

import com.jooq.sample.model.enums.UseravatarHatStyle;

public enum HatStyle {
    BEANIE(UseravatarHatStyle.beanie),
    TURBAN(UseravatarHatStyle.turban),
    NONE(UseravatarHatStyle.none);

    private final UseravatarHatStyle useravatarHatStyle;

    HatStyle(UseravatarHatStyle useravatarHatStyle) {
        this.useravatarHatStyle = useravatarHatStyle;
    }

    public static HatStyle fromString(Object value) {
        if (value == null) return null;
        try {
            return HatStyle.valueOf(value.toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public UseravatarHatStyle getHatStyle() {
        return useravatarHatStyle;
    }
}
