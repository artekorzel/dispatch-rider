package gui.common;

import java.util.Comparator;

class TimestampRecordComparator implements Comparator<Object> {

    @Override
    public int compare(Object o1, Object o2) {
        if (((TimestampRecord) o1).timestamp < ((TimestampRecord) o2).timestamp)
            return -1;
        if (((TimestampRecord) o1).timestamp > ((TimestampRecord) o2).timestamp)
            return 1;
        return 0;
    }

};
