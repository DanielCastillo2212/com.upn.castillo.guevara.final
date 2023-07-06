package com.example.comupncastilloguevarafinal.ImageUpload;

public class ImageUploadResquest {
    private String base64Image;

    public ImageUploadResquest(String base64Image) {
        this.base64Image = base64Image;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }
}
