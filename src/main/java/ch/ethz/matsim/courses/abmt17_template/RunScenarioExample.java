package ch.ethz.matsim.courses.abmt17_template;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.population.PersonUtils;
import org.matsim.core.scenario.ScenarioUtils;

import abmt17.pt.ABMTPTModule;
import abmt17.scoring.ABMTScoringModule;
import ch.ethz.matsim.baseline_scenario.analysis.simulation.ModeShareListenerModule;

/**
 * This is the template for the 2017 ABMT course at ETHZ
 * 
 * With this example you can run the scenario. The only argument
 * to the script is the path to the config file (abmt_config.xml).
 * 
 * In Eclipse, for instance, you can right click on the "main"
 * method and choose "Run As ..." -> "Java Application". At
 * first you will get an error, because no command line argument
 * has been provided. In the run menu "Green Arrow" under the menu
 * bar you can now click on "Run configurations ..." where you
 * can choose "RunScenarioExample". Click on "Arguments" and 
 * set the command line arguments to "abmt_config.xml".
 * 
 * Furthermore, add the following to VM arguments:
 * -Xmx10G 
 * It will tell Java to use up to 10GB of RAM for the simulation.
 */
public class RunScenarioExample {
	static public void main(String[] args) {
		Config config = ConfigUtils.loadConfig("E:/ETH Semester 3/JAVA/abmt17_template/scenario/oldScenario/astra_config.xml"); // Load the config file (command line argument)

		Scenario scenario = ScenarioUtils.loadScenario(config); // Load scenario

		setCaravail.set(scenario, "E:/ETH Semester 3/JAVA/abmt17_template/scenario/oldScenario/population_in_old.csv");
		
		Population population = scenario.getPopulation();
		for (Person pe: population.getPersons().values()){
	    	for(PlanElement ele: pe.getSelectedPlan().getPlanElements()){
	    		if (ele instanceof Leg){ //could also be Activity
	    			if (((Leg)ele).getMode().equals("car"))
	    				System.out.println(((Leg)ele).getMode()+pe.getId());    			
	    		}
	    	}
		}
		
		Controler controler = new Controler(scenario); // Set up simulation controller

		// Some additional modules to create a more realistic simulation
		controler.addOverridingModule(new ABMTScoringModule()); // Required if scoring of activities is used
		controler.addOverridingModule(new ABMTPTModule()); // More realistic "teleportation" of public transport trips
		controler.addOverridingModule(new ModeShareListenerModule()); // Writes correct mode shares in every iteration

		controler.run();
	}

}
