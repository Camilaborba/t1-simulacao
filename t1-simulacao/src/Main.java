import enums.EventType;

import java.util.*;


public class Main {
    public static void main(String[] args) {
        List<Queue> queueScheduler = new ArrayList<>();
        PriorityQueue<Event> scheduledEvents = new PriorityQueue<>();
        Map<Integer, double[]> timeQueueSize = new HashMap<>();

        Queue q1 = new Queue(0, -1, 1.0, 2.0, 4.0, 1.0, 2.0, 1);
        Queue q2 = new Queue(1, 2, 5, 4.0, 8.0);
        Queue q3 = new Queue(2, 2, 10, 5.0, 15.0);
        queueScheduler.add(q1);
        queueScheduler.add(q2);
        queueScheduler.add(q3);

        //Conexões de fila 1
        Queue originQueue = queueScheduler.get(0);
        Queue destinyQueue = queueScheduler.get(1);
        originQueue.destinyQueue(1, destinyQueue);
        originQueue.probability(1, 0.8); // q1 -> q2 = 0.8
        Queue originQueue1 = queueScheduler.get(0);
        Queue destinyQueue1 = queueScheduler.get(2);
        originQueue1.destinyQueue(2, destinyQueue1);
        originQueue1.probability(2, 0.2); // q1 -> q3 = 0.2

        // Conexões de fila 2
        Queue originQueue2 = queueScheduler.get(1);
        Queue destinyQueue2 = queueScheduler.get(0);
        originQueue2.destinyQueue(0, destinyQueue2);
        originQueue2.probability(0, 0.3); // q2 -> q1 = 0.3
        Queue originQueue3 = queueScheduler.get(1);
        Queue destinyQueue3 = queueScheduler.get(2);
        originQueue3.destinyQueue(2, destinyQueue3);
        originQueue3.probability(2, 0.5); // q2 -> q3 = 0.5

        // Conexão de fila 3
        Queue originQueue4 = queueScheduler.get(2);
        Queue destinyQueue4 = queueScheduler.get(1);
        originQueue4.destinyQueue(1, destinyQueue4);
        originQueue4.probability(1, 0.7); // q3 -> q2 = 0.7

        queueScheduler.forEach(q -> timeQueueSize.put(q.getId(), new double[q.getCapacity() != -1 ?
                q.getCapacity() + 1 : 7]));

        scheduledEvents.add(new Event(EventType.ARRIVAL, queueScheduler.get(0).getFirstArrival(),
                queueScheduler.get(0).getId()));

        Simulation.initSimulation(scheduledEvents, queueScheduler, timeQueueSize);
    }
}