import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TaxiPool {
    private static final long EXIRED_TIME_IN_MILISECOND = 1200;
    private static final int NUMBER_OF_TAXI = 4;
    private final List<Taxi> avaiable = Collections.synchronizedList(new ArrayList<>());
    private final List<Taxi> inUser = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger count = new AtomicInteger(0);
    private final AtomicBoolean waitting = new AtomicBoolean(false);

    public synchronized void release(Taxi taxi){
        inUser.remove(taxi);
        avaiable.add(taxi);
        System.out.println(taxi.getName() + " is free");
    }
     public synchronized Taxi getTaxi(){
        if(!avaiable.isEmpty()){
            Taxi taxi = avaiable.get(0);
            inUser.add(taxi);
            return taxi;
        }
        if (count.get() == NUMBER_OF_TAXI){
            this.waittingUntilAvailable();
            return this.getTaxi();
        }
        Taxi taxi = this.createTaxi();
        inUser.add(taxi);
        return taxi;
     }
     private Taxi createTaxi(){
        waitting(200);
        Taxi taxi = new Taxi("Taxi" + count.incrementAndGet());
         System.out.println(taxi.getName() + " is created");
         return taxi;
     }
     private void waittingUntilAvailable(){
        if(waitting.get()){
            waitting.set(false);
            throw  new TaxiNotFoundException("No taxi avaiable");
        }
        waitting.set(true);
        waitting(EXIRED_TIME_IN_MILISECOND);
     }

    private void waitting(long time) {
        try{
            TimeUnit.MICROSECONDS.sleep(time);
        }catch (InterruptedException e){
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
