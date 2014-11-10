package org.rosuda.irconnect;

/**
 * special wrapper for matrix objects (data frames can be supported - discuss!)
 * @author Ralf
 */
public interface IRMatrix {

    public abstract int getColumns();

    public abstract int getRows();

    public abstract IREXP getValueAt(final int row, final int col);

    public String getColumnNameAt(final int row);

    public String getRowNameAt(final int column);
}
