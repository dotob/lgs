// just the
class Image {
    
    public Image(){}
    
    public Image(String filenameOrig){
    this.filename_orig = filenameOrig;
    }

    public String getFilenameOrig() {
        return this.filename_orig;
    }

    public void setFilenameOrig(String filename_orig) {
        this.filename_orig = filename_orig;
    }

    private String filename_orig;
}
