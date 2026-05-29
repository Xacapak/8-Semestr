package com.semestr_8.ComputerSimulation;

import java.util.*;

/**
 * Задание 3: Одноканальная СМО с ожиданием (бесконечная очередь)
 * λ(t) = 0.2 - 0.003t  (нестационарный пуассоновский поток)
 * μ(t) = 0.2  при t∈[0,20]
 *        0.9  при t∈(20,80]
 *        0.4  при t∈(80,100]
 * Моделирование методом прореживания + событийное (FIFO)
 * Определяется количество заявок в накопителе (очереди) в моменты изменения состояния.
 */
public class Task3 {

    private static final double T_MAX = 100.0;
    private static final double LAMBDA_MAX = 0.2;   // макс. интенсивность λ(t)
    private final Random random = new Random();

    // Функция интенсивности поступления
    private double lambda(double t) {
        double val = 0.2 - 0.003 * t;
        return Math.max(0.0, val);
    }

    // Функция интенсивности обслуживания (кусочно-постоянная)
    private double mu(double t) {
        if (t <= 20) return 0.2;
        else if (t <= 80) return 0.9;
        else return 0.4;
    }

    // Генерация моментов поступления методом прореживания
    private List<Double> generateArrivalTimes() {
        List<Double> arrivals = new ArrayList<>();
        double t = 0;
        while (t < T_MAX) {
            double dt = -Math.log(1 - random.nextDouble()) / LAMBDA_MAX;
            t += dt;
            if (t > T_MAX) break;
            double prob = lambda(t) / LAMBDA_MAX;
            if (random.nextDouble() < prob) {
                arrivals.add(t);
            }
        }
        return arrivals;
    }

    public void run() {
        // Генерируем поток поступлений
        List<Double> arrivals = generateArrivalTimes();
        int totalArrived = arrivals.size();

        // Структуры для событийной модели
        // Очередь FIFO – хранит моменты поступления заявок (или просто время начала ожидания)
        Queue<Double> queue = new LinkedList<>();
        double currentTime = 0;
        double serviceEndTime = 0;       // время окончания текущего обслуживания
        boolean busy = false;

        // Списки для записи изменений состояния очереди и канала
        List<Double> timePoints = new ArrayList<>();      // моменты изменений
        List<Integer> queueSize = new ArrayList<>();      // размер очереди в этот момент
        List<Integer> channelState = new ArrayList<>();   // 0 – своб., 1 – зан.

        // Счётчики
        int served = 0;
        int rejected = 0;   // в СМО с ожиданием отказов нет, но для полноты оставим (все приняты)
        double totalQueueLength = 0.0;
        double lastTime = 0.0;
        int currentQueue = 0;

        // Начальное состояние
        timePoints.add(0.0);
        queueSize.add(0);
        channelState.add(0);

        // Объединяем все события: поступления и окончания обслуживания
        // Будем обрабатывать их в хронологическом порядке с помощью приоритетной очереди
        PriorityQueue<Event> eventQueue = new PriorityQueue<>(Comparator.comparingDouble(e -> e.time));
        // Добавляем все поступления
        for (double arr : arrivals) {
            eventQueue.add(new Event(arr, EventType.ARRIVAL));
        }

        while (!eventQueue.isEmpty()) {
            Event ev = eventQueue.poll();
            double t = ev.time;
            // Добавляем накопленную длину очереди за интервал [lastTime, t)
            totalQueueLength += currentQueue * (t - lastTime);
            lastTime = t;

            if (ev.type == EventType.ARRIVAL) {
                // Поступление новой заявки
                if (!busy) {
                    // Канал свободен – начинаем обслуживание сразу
                    double mu_t = mu(t);
                    double serviceTime = -Math.log(1 - random.nextDouble()) / mu_t;
                    double finishTime = t + serviceTime;
                    eventQueue.add(new Event(finishTime, EventType.COMPLETION));
                    busy = true;
                    served++;
                    // Запись состояния: очередь не меняется, канал становится занят
                    timePoints.add(t);
                    queueSize.add(currentQueue);
                    channelState.add(1);
                } else {
                    // Канал занят – ставим в очередь
                    currentQueue++;
                    timePoints.add(t);
                    queueSize.add(currentQueue);
                    channelState.add(1);
                }
            } else { // EventType.COMPLETION – окончание обслуживания
                busy = false;
                if (currentQueue > 0) {
                    // Есть заявки в очереди – начинаем обслуживать следующую
                    currentQueue--;
                    double mu_t = mu(t);
                    double serviceTime = -Math.log(1 - random.nextDouble()) / mu_t;
                    double finishTime = t + serviceTime;
                    eventQueue.add(new Event(finishTime, EventType.COMPLETION));
                    busy = true;
                    served++;
                    timePoints.add(t);
                    queueSize.add(currentQueue);
                    channelState.add(1);
                } else {
                    // Очередь пуста, канал свободен
                    timePoints.add(t);
                    queueSize.add(0);
                    channelState.add(0);
                }
            }
        }

        // После обработки всех событий добавляем финальную точку до T_MAX
        if (lastTime < T_MAX) {
            totalQueueLength += currentQueue * (T_MAX - lastTime);
            timePoints.add(T_MAX);
            queueSize.add(currentQueue);
            channelState.add(busy ? 1 : 0);
        }

        double avgQueueLength = totalQueueLength / T_MAX;
        double rejectionProb = 0.0; // в СМО с ожиданием отказов нет
        double utilization = (double) served / totalArrived; // доля обслуженных (должна быть 1, т.к. все приняты)

        // ===== ВЫВОД ТЕКСТОВЫХ РЕЗУЛЬТАТОВ =====
        System.out.println("Задание 3: Одноканальная СМО с ожиданием (очередь)");
        System.out.println("λ(t) = 0.2 - 0.003t");
        System.out.println("μ = 0.2 [0,20],  μ = 0.9 (20,80],  μ = 0.4 (80,100]");
        System.out.println("Всего поступило заявок: " + totalArrived);
        System.out.println("Обслужено заявок: " + served);
        System.out.println("Отказов (не обслужено): 0 (система с очередью)");
        System.out.printf("Средняя длина очереди: %.3f%n", avgQueueLength);
        System.out.printf("Коэффициент использования канала (доля времени занят): %.3f%n", utilization);
        System.out.println();

        // ===== ВИЗУАЛИЗАЦИЯ =====
        GraphicalConstruction.plotTask3(timePoints, queueSize, channelState);

        System.out.println("Нажмите Enter для возврата в меню...");
        new Scanner(System.in).nextLine();
    }

    // Вспомогательные классы для событий
    private enum EventType { ARRIVAL, COMPLETION }
    private static class Event {
        double time;
        EventType type;
        Event(double t, EventType typ) { time = t; type = typ; }
    }
}