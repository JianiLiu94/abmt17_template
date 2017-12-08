package ch.ethz.matsim.courses.abmt17_template;

public class CarSharingChoiceParameters {
	final private double betaDistance;
	final private double betaTravelTime;
	final private double constant;
	final private double betaDistanceToCar;
	
	final private boolean isChainBased;

	public CarSharingChoiceParameters(double constant, double betaDistance, double betaTravelTime,
			double betaDistanceToCar, boolean isChainBased) {
		
		this.betaDistance = betaDistance;
		this.betaTravelTime = betaTravelTime;
		this.constant = constant;
		this.isChainBased = isChainBased;
		this.betaDistanceToCar = betaDistanceToCar;
		
	}

	public double getBetaDistance() {
		return betaDistance;
	}

	public double getBetaTravelTime() {
		return betaTravelTime;
	}

	public double getConstant() {
		return constant;
	}
	
	public double getBetaDistanceToCar() {
		return betaDistanceToCar;
	}
	
	public boolean isChainBased() {
		return isChainBased;
	}
	
}
