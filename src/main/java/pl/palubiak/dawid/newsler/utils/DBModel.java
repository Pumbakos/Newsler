package pl.palubiak.dawid.newsler.utils;

/**
 * Abstract class allowing to identify an object as a database object and to perform operations on it <br>
 * (operations that can be performed only on types of such objects)
 */
public abstract class DBModel {
    protected long _id;

    public long getId() {
        return _id;
    }

    public void setId(long _id) {
        this._id = _id;
    }
}

