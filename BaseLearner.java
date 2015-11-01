package spamclassificatie;

import java.util.List;

public class BaseLearner {

	// the modifier a <- {-1,1}
	private int a;
	// the index of the variable this base learner is training on i <- {0,1,...,56}
	private int var_index;
	// the threshhold b <- R
	private double b;
	
	// the cumulative weight of all spam
	private double w_plus_all;
	// the cumulative weight of all normal e-mail
	private double w_minus_all;
	
	// the adaBoost algorithm this Base Learner is part of
	AdaBoost parent;
	//
	private double[] weights;
	
	public BaseLearner(double[] weights, int variable, AdaBoost adaBoost) {
		a = (int) (b = w_plus_all = w_minus_all = 0);
		var_index = variable;
		this.weights = weights;
		parent = adaBoost;
		// TODO Auto-generated constructor stub
		train();
		
	}
	// TODO

	private void train()
	{
		// TODO Auto-generated method stub
		//
		// calculate w_- and w_+
		for (int i = 0; i < parent.dataset.size(); i++)
		{
			Mail currentMail = parent.getOriginalFromSortedIndex(i, var_index);
			if (currentMail.y < 0)
				w_minus_all += weights[parent.getOriginalIndex(currentMail)];
			else
				w_plus_all += weights[parent.getOriginalIndex(currentMail)];
		}
		
		// TODO
		double w_minus_A = 0;
		double w_plus_A = 0;
		// TODO
		double classification = 0;
		double e = 0;
		
		// the variables to store the best e yet
		double e_min = Double.MAX_VALUE;
		double x_min = 0;
		double classification_min = 0;
		
		
		// 
		for (int i = 0; i < parent.dataset.size(); i++)
		{
			Mail currentMail = parent.getOriginalFromSortedIndex(i, var_index);
			int currentMail_index = parent.getOriginalIndex(currentMail);
			if (currentMail.y < 0)
				w_minus_A += weights[currentMail_index];
			else
				w_plus_A += weights[currentMail_index];
			// add the weighted classification to the cumulative classification
			classification += weights[currentMail_index] * currentMail.y;
			
			double w_minus_B = w_minus_all - w_minus_A;
			double w_plus_B = w_plus_all - w_plus_A;
			double p_B = (w_minus_B + w_plus_B) == 0 ? 0 : w_plus_B / (w_minus_B + w_plus_B);
			double e_B = 2*p_B * (1 - p_B);
			double p_A = (w_minus_A + w_plus_A) == 0 ? 0 : w_plus_B / (w_minus_A + w_plus_A);
			double e_A = 2*p_A * (1 - p_A);
			e = (w_plus_B + w_minus_B) * e_B + (w_plus_A + w_minus_A) * e_A;
			
			if (e < e_min)
			{
				e_min = e;
				// TODO checken!
				
				x_min = currentMail_index;
				classification_min = classification;
			}
		}
		//
		a = classification_min < 0 ? -1 : 1;
		b = x_min;
	}

	public int classify(Mail mail) {
		// h(x) = a * sign(x_i - b)
		return a * (mail.x[var_index] - b <= 0 ? -1 : 1);
	}
}
