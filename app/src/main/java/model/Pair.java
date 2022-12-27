package model;

import java.io.Serializable;

public final class Pair<F extends Serializable, S extends Serializable> implements java.io.Serializable {
    private static final long serialVersionUID = -3576664616799694057L;
    public F first;
    public S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair<?, ?>)) {
            return false;
        }
        Pair<?, ?> other = (Pair<?, ?>) obj;
        return this.first.equals(other.first) && this.second.equals(other.second);
    }
}

