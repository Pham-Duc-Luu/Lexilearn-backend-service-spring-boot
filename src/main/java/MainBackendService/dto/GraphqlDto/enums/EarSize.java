package MainBackendService.dto.GraphqlDto.enums;

import com.jooq.sample.model.enums.UseravatarEarSize;

public enum EarSize {
    SMALL(UseravatarEarSize.small),
    BIG(UseravatarEarSize.big);

    private final UseravatarEarSize useravatarEarSize;

    EarSize(UseravatarEarSize useravatarEarSize) {
        this.useravatarEarSize = useravatarEarSize;
    }

    public static EarSize fromString(Object value) {
        if (value == null) return null;
        try {
            return EarSize.valueOf(value.toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public UseravatarEarSize getEarSize() {
        return useravatarEarSize;
    }
}
