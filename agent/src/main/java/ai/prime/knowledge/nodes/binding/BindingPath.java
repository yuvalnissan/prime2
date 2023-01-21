package ai.prime.knowledge.nodes.binding;


import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.Unification;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class BindingPath {

    private List<Data> binders;
    private Unification unification;


    public BindingPath(List<Data> binders) {
        this(binders, new Unification());
    }

    public BindingPath(List<Data> binders, Unification unification) {
        if (unification == null)
            throw new RuntimeException("Cannot create a BindingPath with null unification");

        this.unification = unification;

        this.binders = new LinkedList<>();
        for (Data binder : binders) {
            Data bound = binder.bind(unification, false);
            //Preventing duplicate entries, which will lead to endless loop
            if (!this.binders.contains(bound)) {
                this.binders.add(bound);
            }
        }
    }

    public boolean hasMore() {
        return binders.size() > 0;
    }

    public Unification getUnification() {
        return unification;
    }

    public Data getCurrentBinder() {
        return binders.get(0);
    }

    public List<Data> getRemainingBinders() {
        return binders.subList(1, binders.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BindingPath that = (BindingPath) o;
        return Objects.equals(binders, that.binders) &&
                Objects.equals(getUnification(), that.getUnification());
    }

    @Override
    public int hashCode() {
        return Objects.hash(binders, getUnification());
    }

    @Override
    public String toString() {
        return "BindingPath{" +
                "binders=" + binders +
                ", unification=" + unification +
                '}';
    }
}
