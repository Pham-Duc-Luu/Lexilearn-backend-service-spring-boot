package com.MainBackendService.dto.GraphqlDto.enums;

import com.jooq.sample.model.enums.UseravatarEyeBrow;

public enum EyeBrowStyle {
    UP(UseravatarEyeBrow.up),
    UP_WOMAN(UseravatarEyeBrow.upWoman);

    private final UseravatarEyeBrow useravatarEyeBrowStyle;

    EyeBrowStyle(UseravatarEyeBrow useravatarEyeBrowStyle) {
        this.useravatarEyeBrowStyle = useravatarEyeBrowStyle;
    }

    public static EyeBrowStyle fromString(Object value) {
        if (value == null) return null;
        try {
            return EyeBrowStyle.valueOf(value.toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public UseravatarEyeBrow getEyeBrowStyle() {
        return useravatarEyeBrowStyle;
    }
}
