package com.MainBackendService.dto.GraphqlDto.enums;

import com.jooq.sample.model.enums.UseravatarNoseStyle;

public enum NoseStyle {
    SHORT(UseravatarNoseStyle.short_),
    LONG(UseravatarNoseStyle.long_),
    ROUND(UseravatarNoseStyle.round);

    private final UseravatarNoseStyle useravatarNoseStyle;

    NoseStyle(UseravatarNoseStyle useravatarNoseStyle) {
        this.useravatarNoseStyle = useravatarNoseStyle;
    }

    public static NoseStyle fromString(Object value) {
        if (value == null) return null;
        try {
            return NoseStyle.valueOf(value.toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public UseravatarNoseStyle getNoseStyle() {
        return useravatarNoseStyle;
    }
}
