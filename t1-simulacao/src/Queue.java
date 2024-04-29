import java.util.HashMap;

public class Queue {
    private int id;
    private double minArrival;
    private double maxArrival;
    private double minOutput;
    private double maxOutput;
    private double firstArrival;
    private int capacity;
    private int servers;
    private int actualQueueState;
    private int loss;

    private HashMap<Integer, Queue> destinyQueue = new HashMap<>();
    private HashMap<Integer, Double> probability = new HashMap<>();

    public Queue() {
    }

    public Queue(int id, int capacity, double firstArrival, double minArrival, double maxArrival,
                 double minOutput, double maxOutput, int servers) {
        this.id = id;
        this.firstArrival = firstArrival;
        this.servers = servers;
        this.minArrival = minArrival;
        this.maxArrival = maxArrival;
        this.minOutput = minOutput;
        this.maxOutput = maxOutput;
        this.capacity = capacity;
    }

    public Queue(int id, int servers, int capacity, double minOutput, double maxOutput){
        this.id = id;
        this.servers = servers;
        this.capacity = capacity;
        this.minOutput = minOutput;
        this.maxOutput = maxOutput;
    }

    public int getId() {
        return id;
    }

    public double getFirstArrival() {
        return firstArrival;
    }

    public double getMinArrival() {
        return minArrival;
    }

    public double getMaxArrival() {
        return maxArrival;
    }

    public double getMinOutput() {
        return minOutput;
    }

    public double getMaxOutput() {
        return maxOutput;
    }

    public int getServers() {
        return servers;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getLoss() {
        return loss;
    }

    public void addLoss() {
        this.loss++;
    }

    public int getActualQueueState() {
        return actualQueueState;
    }

    public void setActualQueueState(int newActualQueueState) {
        this.actualQueueState = newActualQueueState;
    }

    public HashMap<Integer, Double> getProbability() {
        return probability;
    }

    public void destinyQueue(Integer key, Queue queue) {
        this.destinyQueue.put(key, queue);
    }

    public void probability(Integer key, Double probability) {
        this.probability.put(key, probability);
    }
}