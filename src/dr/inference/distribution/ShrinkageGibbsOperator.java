package dr.inference.distribution;

import dr.inference.model.Parameter;
import dr.inference.operators.GibbsOperator;
import dr.inference.operators.SimpleMCMCOperator;
import dr.math.distributions.InverseGammaDistribution;

public class ShrinkageGibbsOperator extends SimpleMCMCOperator implements GibbsOperator{
    IndependentInverseGammaDistributionModel localPrior;
    IndependentInverseGammaDistributionModel globalPrior;
    Parameter data;
    double pathParameter;
    final boolean usePathParameter = true;

    public ShrinkageGibbsOperator(double weight, IndependentInverseGammaDistributionModel localPrior, IndependentInverseGammaDistributionModel globalPrior, Parameter data){
        setWeight(weight);
        this.localPrior = localPrior;
        this.globalPrior = globalPrior;
        this.data = data;
        this.pathParameter = 1;
    }


    @Override
    public int getStepCount() {
        return 0;
    }

    @Override
    public String getPerformanceSuggestion() {
        return null;
    }

    @Override
    public String getOperatorName() {
        return "ShrinkageGibbsOperator";
    }

    @Override
    public double doOperation() {
        Parameter local = localPrior.getData();
        Parameter localShape = localPrior.getShape();
        Parameter localAugmented = localPrior.getScale();

        Parameter global = globalPrior.getData();
        Parameter globalShape = globalPrior.getShape();
        Parameter globalAugmented = globalPrior.getScale();

        for (int i = 0; i < local.getDimension(); i++) {
            double scale = localAugmented.getParameterValue(i) * pathParameter + data.getParameterValue(i) * data.getParameterValue(i) / (2 * global.getParameterValue(0)) + (1 - pathParameter);
            double draw = InverseGammaDistribution.nextInverseGamma(localShape.getParameterValue(i) * pathParameter + .5 + (1 - pathParameter), scale);
            local.setParameterValueQuietly(i, draw);
        }
        local.fireParameterChangedEvent();

        double shape = local.getDimension() / 2 + pathParameter * globalShape.getParameterValue(0) + (1 - pathParameter) * 1;
        double scale = pathParameter * globalAugmented.getParameterValue(0) + (1 - pathParameter);
        for (int i = 0; i < local.getDimension(); i++) {
            scale += .5 * (data.getParameterValue(i) * data.getParameterValue(i)) / local.getParameterValue(i);
        }
        double draw = InverseGammaDistribution.nextInverseGamma(shape, scale);
        global.setParameterValue(0, draw);


        return 0;
    }

    @Override
    public void setPathParameter(double beta) {
        if(usePathParameter)
            pathParameter = beta;
    }
}
