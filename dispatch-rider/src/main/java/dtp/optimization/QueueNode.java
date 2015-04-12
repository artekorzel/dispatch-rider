package dtp.optimization;

/**
 * Queue node consisting of Object and integer weight. Used in PriorityWeightQueue.
 *
 * @author Szymon Borgosz
 */
public class QueueNode {

    private QueueNode next;
    private Object element;
    private int weight;

    public QueueNode(Object element, int weight) {
        super();
        this.element = element;
        this.weight = weight;
        this.next = null;
    }

    public Object getElement() {
        return element;
    }

    public void setElement(Object element) {
        this.element = element;
    }

    public QueueNode getNext() {
        return next;
    }

    public void setNext(QueueNode next) {
        this.next = next;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
