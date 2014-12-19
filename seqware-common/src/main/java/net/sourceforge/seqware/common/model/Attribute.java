package net.sourceforge.seqware.common.model;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import java.util.Objects;

/**
 * <p>
 * Attribute interface.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public abstract class Attribute<T, S extends Attribute> implements Comparable<S> {

    /**
     * <p>
     * getTag.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public abstract String getTag();

    /**
     * <p>
     * setTag.
     * </p>
     *
     * @param tag
     *            a {@link java.lang.String} object.
     */
    public abstract void setTag(String tag);

    /**
     * <p>
     * getValue.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public abstract String getValue();

    /**
     * <p>
     * setValue.
     * </p>
     *
     * @param value
     *            a {@link java.lang.String} object.
     */
    public abstract void setValue(String value);

    /**
     * <p>
     * getUnit.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public abstract String getUnit();

    /**
     * <p>
     * setUnit.
     * </p>
     *
     * @param unit
     *            a {@link java.lang.String} object.
     */
    public abstract void setUnit(String unit);

    /**
     * Associate this attribute with its parent
     *
     * @param parent
     */
    public abstract void setAttributeParent(T parent);

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.getTag());
        hash = 97 * hash + Objects.hashCode(this.getUnit());
        hash = 97 * hash + Objects.hashCode(this.getValue());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Attribute other = (Attribute) obj;
        if (!Objects.equals(this.getTag(), other.getTag())) {
            return false;
        }
        if (!Objects.equals(this.getValue(), other.getValue())) {
            return false;
        }
        return Objects.equals(this.getUnit(), other.getUnit());
    }

    /**
     *
     * @param that
     * @return
     */
    @Override
    public int compareTo(S that) {
        return ComparisonChain.start().compare(this.getTag(), that.getTag(), Ordering.natural().nullsFirst())
                .compare(this.getValue(), that.getValue(), Ordering.natural().nullsFirst())
                .compare(this.getUnit(), that.getUnit(), Ordering.natural().nullsFirst()).result();
    }

}
