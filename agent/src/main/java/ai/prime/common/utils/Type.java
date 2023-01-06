package ai.prime.common.utils;


public class Type {
    private String name;

    public Type(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this.getClass().equals(o.getClass())){
            return toString().equals(o.toString());
        }else{
            return false;
        }

    }


}
