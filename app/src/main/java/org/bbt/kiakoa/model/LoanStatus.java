package org.bbt.kiakoa.model;

import org.bbt.kiakoa.R;

/**
 * Enum representing a {@link Loan} state in {@link LoanLists}
 */
public enum LoanStatus {
    LENT,
    BORROWED,
    RETURNED,
    NONE;

    /**
     * Return an icon corresponding to a {@link LoanStatus}
     * @return image id
     */
    public int getIndex() {
        int index;
        switch (this) {
            case LENT:
                index = 0;
                break;
            case BORROWED:
                index = 1;
                break;
            case RETURNED:
                index = 2;
                break;
            default:
                index = 3;
                break;
        }
        return index;
    }

    /**
     * Return an icon corresponding to a {@link LoanStatus}
     * @return image id
     */
    public int getIconId() {
        int imageId;
        switch (this) {
            case LENT:
                imageId = R.drawable.ic_lent_24dp;
                break;
            case BORROWED:
                imageId = R.drawable.ic_borrowed_24dp;
                break;
            case RETURNED:
                imageId = R.drawable.ic_return_24dp;
                break;
            default:
                imageId = R.drawable.ic_list_24dp;
                break;
        }
        return imageId;
    }

    /**
     * Return a label corresponding to a {@link LoanStatus}
     * @return string id
     */
    public int getLabelId() {
        int labelId;
        switch (this) {
            case LENT:
                labelId = R.string.lent;
                break;
            case BORROWED:
                labelId = R.string.borrowed;
                break;
            case RETURNED:
                labelId = R.string.returned;
                break;
            default:
                labelId = R.string.none;
                break;
        }
        return labelId;
    }
}
