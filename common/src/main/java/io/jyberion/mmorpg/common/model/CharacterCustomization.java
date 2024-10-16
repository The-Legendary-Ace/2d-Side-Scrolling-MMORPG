package io.jyberion.mmorpg.common.model;

public class CharacterCustomization {
    private BodyType bodyType;
    private FaceType faceType;
    private HairType hairType;
    private ClothingType clothingType;

    public CharacterCustomization(BodyType bodyType, FaceType faceType, HairType hairType, ClothingType clothingType) {
        this.bodyType = bodyType;
        this.faceType = faceType;
        this.hairType = hairType;
        this.clothingType = clothingType;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public FaceType getFaceType() {
        return faceType;
    }

    public void setFaceType(FaceType faceType) {
        this.faceType = faceType;
    }

    public HairType getHairType() {
        return hairType;
    }

    public void setHairType(HairType hairType) {
        this.hairType = hairType;
    }

    public ClothingType getClothingType() {
        return clothingType;
    }

    public void setClothingType(ClothingType clothingType) {
        this.clothingType = clothingType;
    }
}
