package ch.ethz.matsim.courses.abmt17_template.events;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contrib.carsharing.events.StartRentalEvent;
import org.matsim.contrib.carsharing.events.handlers.StartRentalEventHandler;
import org.matsim.core.controler.events.IterationEndsEvent;
import org.matsim.core.controler.events.ShutdownEvent;
import org.matsim.core.controler.events.StartupEvent;
import org.matsim.core.controler.listener.IterationEndsListener;
import org.matsim.core.controler.listener.ShutdownListener;
import org.matsim.core.controler.listener.StartupListener;
import org.matsim.core.utils.collections.QuadTree;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.io.IOUtils;

public class MyStartRentalEventHandlerQuadTree implements StartRentalEventHandler, StartupListener, ShutdownListener, IterationEndsListener{

	private BufferedWriter writer = null;
	private final String filePath;
	private int iteration =1;
	private final Network network;
	private QuadTree<double[]> myTree;
	private QuadTree<double[]> lastTree;
	public final double duration;
	public final double bufferDist;
	
	public MyStartRentalEventHandlerQuadTree (String filePath, Network network, final double duration, final double bufferDist, final double x1, final double y1, final double x2, final double y2){
		this.filePath=filePath;
		this.network=network;
		this.duration=duration;
		this.bufferDist=bufferDist;
		this.myTree=new QuadTree<double[]>(x1,y1,x2,y2);
		this.lastTree=new QuadTree<double[]>(x1,y1,x2,y2);
	}
	
	
	public void reset(int iteration) {
		this.iteration = iteration;
		myTree.clear();
	}
	
	public int getIteration(){
		return iteration;
	}
	
	public double getDuration() {
		return duration;
	}
	
	@Override
	public void notifyShutdown(ShutdownEvent arg0) {
		// TODO Auto-generated method stub
		
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
		
		myTree.put(coord1.getX(), coord1.getY(), new double[] {time, distance});			
	}
	
	@Override
	public void notifyIterationEnds(IterationEndsEvent arg0) {
		lastTree=myTree;
	}
	
	public void close(){
		try {
			writer.close();
		} catch (IOException e){
			throw new UncheckedIOException (e);
		}
	}
	
	public double getEsti(final double startT, final double x, final double y) {
		Collection<double[]> dist = lastTree.getDisk(x, y, bufferDist);
		double average = 0;
		int number = 0;
		for (double[] elem : dist) {
	        if(elem[0]>startT &&elem[0]<startT+duration) {
	        	average = average+elem[1];
	        	number++;
	        }
	    }
		if (number == 0){
			return 100;
		}
		else return average/number;		
	}

}
