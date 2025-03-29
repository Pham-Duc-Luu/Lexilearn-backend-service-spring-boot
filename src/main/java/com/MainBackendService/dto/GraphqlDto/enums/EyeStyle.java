package com.MainBackendService.dto.GraphqlDto.enums;

import com.jooq.sample.model.enums.UseravatarEyeStyle;

public enum EyeStyle {
    CIRCLE(UseravatarEyeStyle.circle),
    OVAL(UseravatarEyeStyle.oval),
    SMILE(UseravatarEyeStyle.smile);

    private final UseravatarEyeStyle useravatarEyeStyle;

    EyeStyle(UseravatarEyeStyle useravatarEyeStyle) {
        this.useravatarEyeStyle = useravatarEyeStyle;
    }

    public static EyeStyle fromString(Object value) {
        if (value == null) return null;
        try {
            return EyeStyle.valueOf(value.toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public UseravatarEyeStyle getEyeStyle() {
        return useravatarEyeStyle;
    }
}
