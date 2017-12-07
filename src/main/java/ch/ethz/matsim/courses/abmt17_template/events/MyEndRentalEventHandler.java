package ch.ethz.matsim.courses.abmt17_template.events;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contrib.carsharing.events.EndRentalEvent;
import org.matsim.contrib.carsharing.events.handlers.EndRentalEventHandler;

public class MyEndRentalEventHandler implements EndRentalEventHandler{

	@Override
	public void handleEvent(EndRentalEvent event) {
		double time = event.getTime();
		Id<Link> linkId = event.getLinkId();
		Id<Person> personId = event.getPersonId();
		String vehicleId = event.getvehicleId();
		
		try {
			writer.newLine();
			writer.write(iteration + "\t"+
			               event.getPersonId()+"\t"+ 
					       event.getActType()+"\tend\t" + 
			               coord.getX()+"\t"+
					       coord.getY()+"\t"+
			               event.getTime());
		} catch (IOException e){
			throw new UncheckedIOException (e);
		}
		
		
	}

}
