package com.MainBackendService.dto.GraphqlDto.enums;

import com.jooq.sample.model.enums.UseravatarShirtStyle;

public enum ShirtStyle {
    HOODY(UseravatarShirtStyle.hoody),
    SHORT(UseravatarShirtStyle.short_),
    POLO(UseravatarShirtStyle.polo);

    private final UseravatarShirtStyle useravatarShirtStyle;

    ShirtStyle(UseravatarShirtStyle useravatarShirtStyle) {
        this.useravatarShirtStyle = useravatarShirtStyle;
    }

    public static ShirtStyle fromString(Object value) {
        if (value == null) return null;
        try {
            return ShirtStyle.valueOf(value.toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public UseravatarShirtStyle getShirtStyle() {
        return useravatarShirtStyle;
    }
}
