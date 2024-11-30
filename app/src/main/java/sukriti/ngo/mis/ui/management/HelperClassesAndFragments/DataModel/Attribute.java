package sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel;

public class Attribute {
    String CODE, NAME, PARENT;

    public Attribute(String CODE, String NAME) {
        this.CODE = CODE;
        this.NAME = NAME;
    }

    public Attribute(String CODE, String NAME, String PARENT) {
        this.CODE = CODE;
        this.NAME = NAME;
        this.PARENT = PARENT;
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getPARENT() {
        return PARENT;
    }

    public void setPARENT(String PARENT) {
        this.PARENT = PARENT;
    }
}
