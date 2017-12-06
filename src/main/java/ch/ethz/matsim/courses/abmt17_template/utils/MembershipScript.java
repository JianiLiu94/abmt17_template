package ch.ethz.matsim.courses.abmt17_template.utils;

import java.util.ArrayList;
import java.util.List;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.internal.MatsimReader;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.core.utils.io.MatsimXmlWriter;

public class MembershipScript extends MatsimXmlWriter {
	
	private Scenario scenario;
	
	public MembershipScript(Scenario scenario) {
		this.scenario = scenario;
	}
	
	public void write(String file) {
		openFile(file);
		
		writeXmlHead();
		
		writeStartTag("memberships", null);
		writeMembership();
		writeEndTag("memberships");

		close();
	}
	
	private void writeMembership() {
		//TODO: you need to add a check if the person is living in the city
		//if it is add a membership card.
		for (Person person : this.scenario.getPopulation().getPersons().values()) {			
			
			writePerson(person);
			
		}
	}
	
	private void writePerson(Person person) {
		List<Tuple<String, String>> attsP = new ArrayList<Tuple<String, String>>();
		
		attsP.add(new Tuple<>("id", person.getId().toString()));
		
		List<Tuple<String, String>> attsC = new ArrayList<Tuple<String, String>>();

		attsC.add(new Tuple<>("id", "Catchacar"));

		List<Tuple<String, String>> attsF = new ArrayList<Tuple<String, String>>();

		attsF.add(new Tuple<>("name", "freefloating"));

		writeStartTag("person", attsP);
		writeStartTag("company", attsC);
		writeStartTag("carsharing", attsF, true);

		writeEndTag("company");		
		writeEndTag("person");

	}
	

	public static void main(String[] args) {

		Config config = ConfigUtils.createConfig();
		
		MutableScenario scenario = ScenarioUtils.createMutableScenario(config);
		
		MatsimReader populationReader = new PopulationReader(scenario);		
		//input population file
		populationReader.readFile(args[0]);	
		
		MembershipScript as = new MembershipScript(scenario);
		//output file i.e. membership.xml
		as.write(args[1]);
		
	}

}
