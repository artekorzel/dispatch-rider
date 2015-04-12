package dtp.optimization;

/**
 * Queue of objects, which are sorted by given integer weight value.
 *
 * @author Szymon Borgosz
 */
public class PriorityWeightQueue {

    private QueueNode first;

    public int size() {
        int size = 0;
        QueueNode tmp = first;
        while (tmp != null) {
            size++;
            tmp = tmp.getNext();
        }
        return size;
    }

    public boolean isEmpty() {
        return first == null;
    }

    public void add(Object element, int weight) throws NullPointerException {
        QueueNode node = new QueueNode(element, weight);
        if (first == null)
            first = node;
        else {
            if (first.getWeight() > weight) {
                node.setNext(first);
                first = node;
                // System.out.println("on begin");
            } else {
                QueueNode pointer = first;
                while (pointer.getNext() != null && pointer.getWeight() < weight)
                    pointer = pointer.getNext();
                node.setNext(pointer.getNext());
                pointer.setNext(node);
            }
        }

    }

    public QueueNode get() {
        QueueNode tmp = first;
        if (first != null)
            first = tmp.getNext();
        return tmp;
    }
}
