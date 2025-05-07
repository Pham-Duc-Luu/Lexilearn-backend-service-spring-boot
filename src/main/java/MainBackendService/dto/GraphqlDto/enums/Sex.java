package MainBackendService.dto.GraphqlDto.enums;

import com.jooq.sample.model.enums.UseravatarSex;


public enum Sex {
    MAN(UseravatarSex.man),
    WOMAN(UseravatarSex.woman);

    private final UseravatarSex useravatarSex;

    Sex(UseravatarSex useravatarSex) {
        this.useravatarSex = useravatarSex;
    }

    public static Sex fromString(Object value) {
        if (value == null) return null;
        try {
            return Sex.valueOf(value.toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public UseravatarSex getSex() {
        return useravatarSex;
    }
}
