package ch.ethz.matsim.courses.abmt17_template;

import ch.ethz.matsim.courses.abmt17_template.events.MyStartRentalEventHandler;
import ch.ethz.matsim.courses.abmt17_template.events.MyStartRentalEventHandlerQuadTree;
import ch.ethz.matsim.mode_choice.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.mnl.ModeChoiceAlternative;
import ch.ethz.matsim.mode_choice.mnl.prediction.EmptyPredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.PredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPrediction;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPredictor;

public class CarSharingModeChoiceAlternativeQuadTree implements ModeChoiceAlternative{
	final private CarSharingChoiceParameters params;
	final private TripPredictor tripPredictor;
	final private PredictionCache cache;
	final private MyStartRentalEventHandlerQuadTree eventHandler;

	public CarSharingModeChoiceAlternativeQuadTree(CarSharingChoiceParameters params, TripPredictor tripPredictor,
			PredictionCache cache, MyStartRentalEventHandlerQuadTree eventHandler) {
		this.params = params;
		this.tripPredictor = tripPredictor;
		this.cache = cache;
		this.eventHandler = eventHandler;
	}
	
	public CarSharingModeChoiceAlternativeQuadTree(CarSharingChoiceParameters params, TripPredictor tripPredictor, MyStartRentalEventHandlerQuadTree eventHandler) {
		this(params, tripPredictor, new EmptyPredictionCache(), eventHandler);
	}

	@Override
	public double estimateUtility(ModeChoiceTrip trip) {
		double startT = (int)(trip.getDepartureTime()/eventHandler.getDuration())*eventHandler.getDuration();
		TripPrediction prediction = cache.get(trip);

		if (prediction == null) {
			prediction = tripPredictor.predictTrip(trip);
			cache.put(trip, prediction);
		}
		
		return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
					+ params.getBetaDistance() * prediction.getPredictedTravelDistance() 
					+ params.getBetaDistanceToCar() * eventHandler.getEsti(startT, trip.getOriginLink().getCoord().getX(), trip.getOriginLink().getCoord().getY());
	}

	@Override
	public boolean isChainMode() {
		return params.isChainBased();
	}
}
