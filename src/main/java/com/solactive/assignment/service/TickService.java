package com.solactive.assignment.service;

import com.solactive.assignment.businessobject.Tick;
import com.solactive.assignment.stats.TickStatistics;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Deque;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class TickService {

    //Maintain dequeue for all records
    Deque<Tick> tickRecords = new LinkedList<>();

    //Maintain max dequeue to retrieve maximum any point
    Deque<Tick> maxTicks = new LinkedList<>();

    //Maintain min dequeue to retrieve minimum any point
    Deque<Tick> minTicks = new LinkedList<>();

    //Maintain to lock to ensure concurrent updates
    private Lock lock = new ReentrantLock();

    //Statistics output variables
    public volatile static long COUNT = 0;
    public volatile static double MAX = 0;
    public volatile  static double MIN  = 0;
    public volatile  static double AVG = 0;
    public volatile  static double SUM = 0;


    //Delete expired ticks in all dequeues.
    public void deleteTicks(){
        ZonedDateTime timer = ZonedDateTime.now(ZoneOffset.UTC);
        cleanup(timer, tickRecords, true);
        cleanup(timer, minTicks, false);
        cleanup(timer, maxTicks, false);
    }

    //Cleanup operation
    private void cleanup(ZonedDateTime timer, Deque<Tick> ticks, boolean computation) {
        while(!ticks.isEmpty()){
            Tick record = ticks.getFirst();
            if(record.getTimestamp()<(timer.toEpochSecond()-60)*1000){
                lock.lock();
                //Keep polling out old ticks
                ticks.pollFirst();
                //Update stats on every poll
                if(computation){
                    COUNT--;
                    SUM-=record.getPrice();
                    AVG = SUM/COUNT;
                }
                lock.unlock();
            } else{
                break;
            }
        }
    }


    //Method for persisting a tick
    public void saveTick(Tick tickInput){
        //Cleanup old ticks
        deleteTicks();
        lock.lock();
        //Add new tick to collection
        tickRecords.addLast(tickInput);

        /**
         * Maintain deques monotonically increasing and decreasing  to retreive max/min at any point
         */

        //Update max queue
        if(!maxTicks.isEmpty()){
            while(!maxTicks.isEmpty() && (tickInput.getPrice() > maxTicks.peekLast().getPrice())){
                maxTicks.removeLast();
            }
            maxTicks.addLast(tickInput);

        } else{
            maxTicks.addLast(tickInput);
        }

        //Update min queue
        if(!minTicks.isEmpty()){
            while(!minTicks.isEmpty() && (tickInput.getPrice() < minTicks.peekLast().getPrice())){
                minTicks.removeLast();
            }
            minTicks.addLast(tickInput);

        } else{
            minTicks.addLast(tickInput);
        }

        double price = tickInput.getPrice();

        //Update stats
        COUNT++;
        SUM+=price;
        AVG = SUM/COUNT;
        MIN = minTicks.peekFirst().getPrice();
        MAX = maxTicks.peekFirst().getPrice();
        lock.unlock();

    }

    /**
     * Fetch global statistics IN O(1) by updating latest stats value to the output bean
     * @return
     */
    public TickStatistics fetchTickStatistics(){
        deleteTicks();
        TickStatistics output = new TickStatistics();
        output.setAvg(AVG);
        output.setCount(COUNT);
        output.setMax(maxTicks.isEmpty() ? 0: MAX);
        output.setMin(minTicks.isEmpty() ? 0: MIN);
        return output;

    }


    /**
     * Fetch instrument specific statistics. Using streams for now as there is no bound complexity
        for this operattion, but however the same logic of global dequeue can be replicated for
        each specific instrument by maintaining a hashmap of instrument and its dequeue which
        might be costly in terms of space efficiency.
     **/
    public TickStatistics fetchTickStatisticsInstrument(String instrument){
        deleteTicks();
        TickStatistics output = new TickStatistics();
        DoubleSummaryStatistics result = new DoubleSummaryStatistics();
        tickRecords.stream().
                filter(tick -> tick.getInstrument().equals(instrument)).
                forEach(tick->result.accept(tick.getPrice()));
        output.setCount(result.getCount());
        output.setMax(result.getMax());
        output.setMin(result.getMin());
        output.setAvg(result.getAverage());
        return output;
    }

}
