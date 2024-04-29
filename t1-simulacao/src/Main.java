import enums.EventType;

import java.util.*;


public class Main {

        protected  static int currentSimulationLength = 0;
        protected static int maxSimulationLength = 100000;
        public static double time = 0;
        public static double prevTime = 0;
        public static List<Queue> queueScheduler = new ArrayList<>();
        public static PriorityQueue<Event> scheduledEvents = new PriorityQueue<>();
        public static Map<Integer, double[]> timeQueueSize = new HashMap<>();
        private static Random randoms = new Random();

        public static void main(String[] args) {
            Queue q1 = new Queue(0, -1, 1.0, 2.0, 4.0, 1.0, 2.0, 1);
            Queue q2 = new Queue(1, 2, 5, 4.0, 8.0);
            Queue q3 = new Queue(2, 2, 10, 5.0, 15.0);
            queueScheduler.add(q1);
            queueScheduler.add(q2);
            queueScheduler.add(q3);

            //Conexões de q1
            Queue originQueue = queueScheduler.get(0);
            Queue destinyQueue = queueScheduler.get(1);
            originQueue.destinyQueue(1, destinyQueue);
            originQueue.probability(1, 0.8); // q1 -> q2 = 0.8
            Queue originQueue1 = queueScheduler.get(0);
            Queue destinyQueue1 = queueScheduler.get(2);
            originQueue1.destinyQueue(2, destinyQueue1);
            originQueue1.probability(2, 0.2); // q1 -> q3 = 0.2

            // Conexões de q2
            Queue originQueue2 = queueScheduler.get(1);
            Queue destinyQueue2 = queueScheduler.get(0);
            originQueue2.destinyQueue(0, destinyQueue2);
            originQueue2.probability(0, 0.3); // q2 -> q1 = 0.3
            Queue originQueue3 = queueScheduler.get(1);
            Queue destinyQueue3 = queueScheduler.get(2);
            originQueue3.destinyQueue(2, destinyQueue3);
            originQueue3.probability(2, 0.5); // q2 -> q3 = 0.5

            // Conexão de q3
            Queue originQueue4 = queueScheduler.get(2);
            Queue destinyQueue4 = queueScheduler.get(1);
            originQueue4.destinyQueue(1, destinyQueue4);
            originQueue4.probability(1, 0.7); // q3 -> q2 = 0.7

            queueScheduler.forEach(q -> timeQueueSize.put(q.getId(), new double[q.getCapacity() != -1 ?
                    q.getCapacity() + 1 : 7]));

            scheduledEvents.add(new Event(EventType.ARRIVAL, queueScheduler.get(0).getFirstArrival(),
                    queueScheduler.get(0).getId()));

            initSimulation();
        }

        public static void initSimulation() {
            while(currentSimulationLength < maxSimulationLength) {
                Event currentEvent = scheduledEvents.poll();

                prevTime = time;
                time = currentEvent.getTime();

                Queue currentQueue = queueScheduler.get(currentEvent.getIdInit());

                Queue destinyQueue = currentEvent.getIdFinish() != null ? queueScheduler.get(currentEvent.getIdFinish()) : null;

                switch (currentEvent.getType()) {
                    case ARRIVAL:
                        arrival(currentQueue);
                        break;
                    case OUTPUT:
                        output(currentQueue);
                        break;
                    case TRANSITION:
                        transition(currentQueue, destinyQueue);
                        break;
                }
                currentSimulationLength++;
            }
            print();
        }

        private static void arrival(Queue currentQueue) {
            queueScheduler.forEach(queue -> timeQueueSize.get(queue.getId())[queue.getActualQueueState()] += time - prevTime);
            if (haveSpace(currentQueue)) {
                currentQueue.setActualQueueState(currentQueue.getActualQueueState() + 1);
                if (currentQueue.getActualQueueState() <= currentQueue.getServers()) {
                    Queue destiny = sort(currentQueue);
                    if (destiny != null) {
                        scheduleTransition(currentQueue, destiny);
                    } else {
                        scheduleOutput(currentQueue);
                    }
                }
            } else {
                currentQueue.addLoss();
            }
            scheduleArrival(currentQueue);
        }

        private static void output(Queue currentQueue) {
            queueScheduler.forEach(queue -> timeQueueSize.get(queue.getId())[queue.getActualQueueState()] += time - prevTime);
            currentQueue.setActualQueueState(currentQueue.getActualQueueState() - 1);

            if (currentQueue.getActualQueueState() >= currentQueue.getServers()) {
                Queue destiny = sort(currentQueue);
                if (destiny != null) {
                    scheduleTransition(currentQueue, destiny);
                } else {
                    scheduleOutput(currentQueue);
                }
            }
        }

        private static void transition(Queue origin, Queue destiny) {
            queueScheduler.forEach(queue -> timeQueueSize.get(queue.getId())[queue.getActualQueueState()] += time - prevTime);
            origin.setActualQueueState(origin.getActualQueueState() - 1);

            if (origin.getActualQueueState() >= origin.getServers()) {
                Queue nextQueue = sort(origin);
                if (nextQueue != null) {
                    scheduleTransition(origin, nextQueue);
                } else {
                    scheduleOutput(origin);
                }
            }

            if (destiny != null) {
                if (haveSpace(destiny)) {
                    destiny.setActualQueueState(destiny.getActualQueueState() + 1);
                    if (destiny.getActualQueueState() <= destiny.getServers()) {
                        Queue destiny2 = sort(destiny);
                        if (destiny2 != null) {
                            scheduleTransition(destiny, destiny2);
                        } else {
                            scheduleOutput(destiny);
                        }
                    }
                } else {
                    destiny.addLoss();
                }
            }
        }

        public static void scheduleArrival(Queue currentQueue) {
            double random = randoms.next();
            double arrivalTime = (currentQueue.getMaxArrival() - currentQueue.getMinArrival()) *
                    random + currentQueue.getMinArrival();
            double arrivalRealTime = arrivalTime + time;

            scheduledEvents.add(new Event(EventType.ARRIVAL, arrivalRealTime, currentQueue.getId()));
        }

        public static void scheduleOutput(Queue currentQueue) {
            double random = randoms.next();
            double outputTime = (currentQueue.getMaxOutput() - currentQueue.getMinOutput()) *
                    random + currentQueue.getMinOutput();
            double outputRealTime = outputTime + time;

            scheduledEvents.add(new Event(EventType.OUTPUT, outputRealTime, currentQueue.getId()));
        }

        private static void scheduleTransition(Queue initQueue, Queue finishQueue) {
            double random = randoms.next();
            double outputTime = (initQueue.getMaxOutput() - initQueue.getMinOutput()) * random +
                    initQueue.getMinOutput();
            double outputRealTime = outputTime + time;

            scheduledEvents.add(new Event(EventType.TRANSITION, outputRealTime, initQueue.getId(),
                    finishQueue.getId()));
        }

        private static Queue sort(final Queue origin) {
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

        public static void print() {
            timeQueueSize.forEach((id, queues) -> {
                Queue currentQueue = queueScheduler.get(id);
                System.out.print("\nFila " + id);
                System.out.print(" (G/G/"+ currentQueue.getServers());

                if (queueScheduler.get(id).getCapacity() != -1){
                    System.out.println("/" + currentQueue.getCapacity() +")");
                } else {
                    System.out.println(")");
                }

                if (currentQueue.getMinArrival() != 0.0) {
                    String  arrivalCustomer = String.format("Chegada do cliente: %.2f .. %.2f",
                            currentQueue.getMinArrival(),
                            currentQueue.getMaxArrival());
                    System.out.println(arrivalCustomer);
                }

                if (queueScheduler.get(id).getMinArrival() != 0.0) {
                    String  arrivalCustomer = String.format("Chegada do cliente: %.2f .. %.2f",
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

                for (int i=0; i<queues.length; i++) {
                    if (queues[i] / time * 100 < 10){
                        System.out.printf("[" + i + "]: 0%.5f", queues[i] / time * 100);
                    } else {
                        System.out.printf("[" + i + "]: %.5f", queues[i] / time * 100);
                    }
                    System.out.print("%\n");
                }
            });
        }
}