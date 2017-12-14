package ch.ethz.matsim.courses.abmt17_template;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.mnl.ModeChoiceAlternative;

/* deliberately not public */
class MNLDistribution {
	final private List<String> modes = new LinkedList<>();
	final private List<ModeChoiceAlternative> alternatives = new LinkedList<>();

	public void addAlternative(String mode, ModeChoiceAlternative alternative) {
		this.modes.add(mode);
		this.alternatives.add(alternative);
	}
	
	public double getProbability(String mode, ModeChoiceTrip trip) {
		List<Double> logits = new ArrayList<>(modes.size());

		for (int i = 0; i < modes.size(); i++) {
			logits.add(Math.exp(alternatives.get(i).estimateUtility(trip)));
		}

		return logits.get(modes.indexOf(mode)) / logits.stream().mapToDouble(Double::doubleValue).sum();
	}
}
