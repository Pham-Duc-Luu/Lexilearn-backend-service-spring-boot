package com.MainBackendService.dto.GraphqlDto;

import com.MainBackendService.dto.GraphqlDto.enums.*;
import com.jooq.sample.model.tables.records.UseravatarRecord;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvatarDto {
    private Sex sex;
    private String faceColor;
    private EarSize earSize;
    private EyeStyle eyeStyle;
    private NoseStyle noseStyle;
    private ShirtStyle shirtStyle;
    private GlassesStyle glassesStyle;
    private String hairColor;
    private HairStyle hairStyle;
    private HatStyle hatStyle;
    private String hatColor;
    private EyeBrowStyle eyeBrowStyle;
    private String shirtColor;
    private MouthStyle mouthStyle;
    private String bgColor;

    public AvatarDto(Sex sex,
                     String faceColor,
                     EarSize earSize,
                     EyeStyle eyeStyle,
                     NoseStyle noseStyle,
                     ShirtStyle shirtStyle,
                     GlassesStyle glassesStyle,
                     String hairColor,
                     HairStyle hairStyle,
                     HatStyle hatStyle,
                     String hatColor,
                     EyeBrowStyle eyeBrowStyle,
                     String shirtColor,
                     MouthStyle mouthStyle,
                     String bgColor) {
        this.sex = sex;
        this.faceColor = faceColor;
        this.earSize = earSize;
        this.eyeStyle = eyeStyle;
        this.noseStyle = noseStyle;
        this.shirtStyle = shirtStyle;
        this.glassesStyle = glassesStyle;
        this.hairColor = hairColor;
        this.hairStyle = hairStyle;
        this.hatStyle = hatStyle;
        this.hatColor = hatColor;
        this.eyeBrowStyle = eyeBrowStyle;
        this.shirtColor = shirtColor;
        this.mouthStyle = mouthStyle;
        this.bgColor = bgColor;
    }

    public AvatarDto(ModifyUserProfileInput modifyUserProfileInput) {
        this.sex = modifyUserProfileInput.getSex();
        this.faceColor = modifyUserProfileInput.getFaceColor();
        this.earSize = modifyUserProfileInput.getEarSize();
        this.eyeStyle = modifyUserProfileInput.getEyeStyle();
        this.noseStyle = modifyUserProfileInput.getNoseStyle();
        this.shirtStyle = modifyUserProfileInput.getShirtStyle();
        this.glassesStyle = modifyUserProfileInput.getGlassesStyle();
        this.hairColor = modifyUserProfileInput.getHairColor();
        this.hairStyle = modifyUserProfileInput.getHairStyle();
        this.hatStyle = modifyUserProfileInput.getHatStyle();
        this.hatColor = modifyUserProfileInput.getHatColor();
        this.eyeBrowStyle = modifyUserProfileInput.getEyeBrowStyle();
        this.shirtColor = modifyUserProfileInput.getShirtColor();
        this.mouthStyle = modifyUserProfileInput.getMouthStyle();
        this.bgColor = modifyUserProfileInput.getBgColor();
    }


    public AvatarDto() {
    }

    public AvatarDto(UseravatarRecord useravatarRecord) {
        if (useravatarRecord != null) {
            this.sex = Sex.fromString(useravatarRecord.getSex());
            this.faceColor = useravatarRecord.getFaceColor();
            this.earSize = EarSize.fromString(useravatarRecord.getEarSize());
            this.eyeStyle = EyeStyle.fromString(useravatarRecord.getEyeStyle());
            this.noseStyle = NoseStyle.fromString(useravatarRecord.getNoseStyle());
            this.mouthStyle = MouthStyle.fromString(useravatarRecord.getMouthStyle());
            this.shirtStyle = ShirtStyle.fromString(useravatarRecord.getShirtStyle());
            this.glassesStyle = GlassesStyle.fromString(useravatarRecord.getGlassesStyle());
            this.hairColor = useravatarRecord.getHairColor();
            this.hairStyle = HairStyle.fromString(useravatarRecord.getHairStyle());
            this.hatStyle = HatStyle.fromString(useravatarRecord.getHatStyle());
            this.hatColor = useravatarRecord.getHatColor();
            this.eyeBrowStyle = EyeBrowStyle.fromString(useravatarRecord.getEyeBrow());
            this.shirtColor = useravatarRecord.getShirtColor();
            this.bgColor = useravatarRecord.getBgColor();
        }
    }
}
