import enums.EventType;

import java.util.*;

public class Simulation {

    public static int currentSimulationLength = 0;
    public static int maxSimulationLength = 100000;
    private static Random randoms = new Random();

    public static double time = 0;
    public static double prevTime = 0;

    public static void initSimulation(PriorityQueue<Event> scheduledEvents, List<Queue> queueScheduler,
                                      Map<Integer, double[]> timeQueueSize) {
        while (currentSimulationLength < maxSimulationLength) {
            Event currentEvent = scheduledEvents.poll();

            prevTime = time;
            time = currentEvent.getTime();

            Queue currentQueue = queueScheduler.get(currentEvent.getIdInit());

            Queue destinyQueue = currentEvent.getIdFinish() != null ?
                    queueScheduler.get(currentEvent.getIdFinish()) : null;

            switch (currentEvent.getType()) {
                case ARRIVAL:
                    arrival(currentQueue, queueScheduler, scheduledEvents, timeQueueSize);
                    break;
                case TRANSITION:
                    transition(currentQueue, destinyQueue, queueScheduler, scheduledEvents, timeQueueSize);
                    break;
            }
            currentSimulationLength++;
        }
        print(queueScheduler, timeQueueSize);
    }

    private static void arrival(Queue currentQueue, List<Queue> queueScheduler, PriorityQueue<Event> scheduledEvents,
                                Map<Integer, double[]> timeQueueSize) {
        queueScheduler.forEach(queue -> timeQueueSize.get(queue.getId())[queue.getActualQueueState()] += time - prevTime);
        if (haveSpace(currentQueue)) {
            currentQueue.setActualQueueState(currentQueue.getActualQueueState() + 1);
            if (currentQueue.getActualQueueState() <= currentQueue.getServers()) {
                Queue destiny = sort(currentQueue, queueScheduler);
                if (destiny != null) {
                    scheduleTransition(currentQueue, destiny, scheduledEvents);
                }
            }
        } else {
            currentQueue.addLoss();
        }
        scheduleArrival(currentQueue, scheduledEvents);
    }


    private static void transition(Queue origin, Queue destiny, List<Queue> queueScheduler,
                                   PriorityQueue<Event> scheduledEvents, Map<Integer, double[]> timeQueueSize) {
        queueScheduler.forEach(queue -> timeQueueSize.get(queue.getId())[queue.getActualQueueState()] += time - prevTime);
        origin.setActualQueueState(origin.getActualQueueState() - 1);

        if (origin.getActualQueueState() >= origin.getServers()) {
            Queue nextQueue = sort(origin, queueScheduler);
            if (nextQueue != null) {
                scheduleTransition(origin, nextQueue, scheduledEvents);
            }
        }

        if (destiny != null) {
            if (haveSpace(destiny)) {
                destiny.setActualQueueState(destiny.getActualQueueState() + 1);
                if (destiny.getActualQueueState() <= destiny.getServers()) {
                    Queue destiny2 = sort(destiny, queueScheduler);
                    if (destiny2 != null) {
                        scheduleTransition(destiny, destiny2, scheduledEvents);
                    }
                }
            } else {
                destiny.addLoss();
            }
        }
    }

    public static void scheduleArrival(Queue currentQueue, PriorityQueue<Event> scheduledEvents) {
        double random = randoms.next();
        double arrivalTime = (currentQueue.getMaxArrival() - currentQueue.getMinArrival()) *
                random + currentQueue.getMinArrival();
        double arrivalRealTime = arrivalTime + time;

        scheduledEvents.add(new Event(EventType.ARRIVAL, arrivalRealTime, currentQueue.getId()));
    }

    private static void scheduleTransition(Queue initQueue, Queue finishQueue, PriorityQueue<Event> scheduledEvents) {
        double random = randoms.next();
        double outputTime = (initQueue.getMaxOutput() - initQueue.getMinOutput()) * random +
                initQueue.getMinOutput();
        double outputRealTime = outputTime + time;

        scheduledEvents.add(new Event(EventType.TRANSITION, outputRealTime, initQueue.getId(),
                finishQueue.getId()));
    }

    private static Queue sort(final Queue origin, List<Queue> queueScheduler) {
        double interval = 0.0;
        final double random = randoms.next();

        List<Map.Entry<Integer, Double>> sorted = new ArrayList<>(origin.getProbability().entrySet());
        sorted.sort(Map.Entry.comparingByValue());

        Queue destinyQueue = null;
        for (Map.Entry<Integer, Double> queue : sorted) {
            interval += queue.getValue();
            if (random <= interval) {
                destinyQueue = queueScheduler.get(queue.getKey());
                break;
            }
        }
        return destinyQueue;
    }

    private static boolean haveSpace(Queue currentQueue) {
        return currentQueue.getCapacity() == -1 || currentQueue.getActualQueueState() < currentQueue.getCapacity();
    }

    public static void print(List<Queue> queueScheduler,   Map<Integer, double[]> timeQueueSize) {
        timeQueueSize.forEach((id, queues) -> {
            Queue currentQueue = queueScheduler.get(id);
            System.out.print("\nFila " + id);
            System.out.print(" (G/G/" + currentQueue.getServers());

            if (queueScheduler.get(id).getCapacity() != -1) {
            } else {
                System.out.println(")");
            }

            if (currentQueue.getMinArrival() != 0.0) {
                String arrivalCustomer = String.format("Chegada do cliente: %.2f .. %.2f",
                        currentQueue.getMinArrival(),
                        currentQueue.getMaxArrival());
                System.out.println(arrivalCustomer);
            }

            if (queueScheduler.get(id).getMinArrival() != 0.0) {
                String arrivalCustomer = String.format("Chegada do cliente: %.2f .. %.2f",
                        currentQueue.getMinArrival(),
                        currentQueue.getMaxArrival());
                System.out.println(arrivalCustomer);
            }

            String serviceInfo = String.format("Atendimento do cliente: %.2f .. %.2f",
                    currentQueue.getMinOutput(),
                    currentQueue.getMaxOutput());

            System.out.println(serviceInfo);

            String lossInfo = String.format("Perda de clientes: %d", currentQueue.getLoss());
            System.out.println(lossInfo);

            for (int i = 0; i < queues.length; i++) {
                if (queues[i] / time * 100 < 10) {
                    System.out.printf("[" + i + "]: 0%.5f", queues[i] / time * 100);
                } else {
                    System.out.printf("[" + i + "]: %.5f", queues[i] / time * 100);
                }
                System.out.print("%\n");
            }
        });
    }
}
