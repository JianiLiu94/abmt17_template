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
import org.matsim.contrib.carsharing.events.handlers.EndRentalEventHandler;
import org.matsim.core.controler.events.ShutdownEvent;
import org.matsim.core.controler.events.StartupEvent;
import org.matsim.core.controler.listener.ShutdownListener;
import org.matsim.core.controler.listener.StartupListener;
import org.matsim.core.utils.io.IOUtils;

public class MyEndRentalEventHandler implements EndRentalEventHandler, StartupListener, ShutdownListener{

	private BufferedWriter writer = null;
	private final String filePath;
	private int iteration =1;
	private final Network network;

	public MyEndRentalEventHandler (String filePath, Network network){
		this.filePath=filePath;
		this.network=network;
	}
	
	public void reset(int iteration) {
		this.iteration = iteration;
	}
	
	@Override
	public void notifyStartup(StartupEvent event) {
		this.writer = IOUtils.getBufferedWriter(filePath);
		try {
			writer.write("iteration\ttime\tX\tY\tpersonId\tvehicleId");
		} catch (IOException e){
			throw new UncheckedIOException (e);
		}		
	}
	
	
	@Override
	public void handleEvent(EndRentalEvent event) {
		double time = event.getTime();
		Link link = network.getLinks().get(event.getLinkId());
		Coord coord = link.getCoord();
		Id<Person> personId = event.getPersonId();
		String vehicleId = event.getvehicleId();
				
		try {
			writer.newLine();
			writer.write(iteration + "\t"+
			               time+"\t"+
			               coord.getX()+"\t"+
			               coord.getY()+"\t"+
					       personId+"\t"+
					       vehicleId);
		} catch (IOException e){
			throw new UncheckedIOException (e);
		}
		
		
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



}
