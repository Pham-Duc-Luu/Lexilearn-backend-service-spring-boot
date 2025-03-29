package com.MainBackendService.dto.GraphqlDto.enums;

import com.jooq.sample.model.enums.UseravatarHairStyle;

public enum HairStyle {
    NORMAL(UseravatarHairStyle.normal),
    THICK(UseravatarHairStyle.thick),
    MOHAWK(UseravatarHairStyle.mohawk),
    WOMAN_LONG(UseravatarHairStyle.womanLong),
    WOMAN_SHORT(UseravatarHairStyle.womanShort);

    private final UseravatarHairStyle useravatarHairStyle;

    HairStyle(UseravatarHairStyle useravatarHairStyle) {
        this.useravatarHairStyle = useravatarHairStyle;
    }

    public static HairStyle fromString(Object value) {
        if (value == null) return null;
        try {
            return HairStyle.valueOf(value.toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public UseravatarHairStyle getHairStyle() {
        return useravatarHairStyle;
    }
}
