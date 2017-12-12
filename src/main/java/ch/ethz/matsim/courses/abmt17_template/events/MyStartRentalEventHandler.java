package ch.ethz.matsim.courses.abmt17_template.events;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contrib.carsharing.events.EndRentalEvent;
import org.matsim.contrib.carsharing.events.StartRentalEvent;
import org.matsim.contrib.carsharing.events.handlers.EndRentalEventHandler;
import org.matsim.contrib.carsharing.events.handlers.StartRentalEventHandler;
import org.matsim.core.controler.events.IterationEndsEvent;
import org.matsim.core.controler.events.ShutdownEvent;
import org.matsim.core.controler.events.StartupEvent;
import org.matsim.core.controler.listener.IterationEndsListener;
import org.matsim.core.controler.listener.ShutdownListener;
import org.matsim.core.controler.listener.StartupListener;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.io.IOUtils;

public class MyStartRentalEventHandler implements StartRentalEventHandler, StartupListener, ShutdownListener, IterationEndsListener{

	private BufferedWriter writer = null;
	private final String filePath;
	private int iteration =1;
	private final Network network;
	public double[] distances;
	public int[] amountVehicles;
	public double[] distanceLastIte = new double[30];

	public MyStartRentalEventHandler (String filePath, Network network){
		this.filePath=filePath;
		this.network=network;
	}
	
	public void reset(int iteration) {
		this.iteration = iteration;
		this.distances = new double[30];
		this.amountVehicles = new int[30]; 
	}
	
	public double[] getEsti(){
		return distanceLastIte;
	}
	
	public int getIteration(){
		return iteration;
	}
	
	@Override
	public void notifyStartup(StartupEvent event) {
		this.writer = IOUtils.getBufferedWriter(filePath);
		try {
			writer.write("iteration\ttime\tX1\tY1\tX2\tY2\tpersonId\tvehicleId");
		} catch (IOException e){
			throw new UncheckedIOException (e);
		}		
	}
	
	
	@Override
	public void handleEvent(StartRentalEvent event) {
		double time = event.getTime();
		Link link1 = network.getLinks().get(event.getOriginLinkId());
		Coord coord1 = link1.getCoord();
		Link link2 = network.getLinks().get(event.getPickuplinkId());
		Coord coord2 = link2.getCoord();
		double distance = CoordUtils.calcEuclideanDistance(coord1, coord2);
		Id<Person> personId = event.getPersonId();
		String vehicleId = event.getvehicleId();
				
		try {
			writer.newLine();
			writer.write(iteration + "\t"+
			               time+"\t"+
			               coord1.getX()+"\t"+
			               coord1.getY()+"\t"+
			               coord2.getX()+"\t"+
			               coord2.getY()+"\t"+
					       personId+"\t"+
					       vehicleId);
		} catch (IOException e){
			throw new UncheckedIOException (e);
		}
		
		int t = (int)(time/3600);
		distances[t] = distances[t] + distance;
		amountVehicles[t]++;
		
	}


	public void close(){
		try {
			writer.close();
		} catch (IOException e){
			throw new UncheckedIOException (e);
		}
	}

	@Override
	public void notifyShutdown(ShutdownEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyIterationEnds(IterationEndsEvent event) {
		// TODO Auto-generated method stub
		for (int i=0; i<30;i++){
			if (amountVehicles[i]!=0){
			distanceLastIte[i] = distances[i]/amountVehicles[i];
		}
		}
	}

}
