package measure.printer;

import measure.Measure;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MeasureData implements Iterable<List<Measure>>, Serializable {

    private final List<List<Measure>> measures = new LinkedList<>();

    public void addMeasures(List<Measure> measures) {
        this.measures.add(measures);
    }

    @Override
    public Iterator<List<Measure>> iterator() {
        return new Iterator<List<Measure>>() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < measures.size();
            }

            @Override
            public List<Measure> next() {
                return measures.get(index++);
            }

            @Override
            public void remove() {
                measures.remove(index);
                if (index >= measures.size())
                    index = measures.size() - 1;
            }

        };
    }

}
