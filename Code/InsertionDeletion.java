package Code;

import java.util.*;

public class InsertionDeletion {
	static int removed_because_of_hashes=0;
	public static ArrayList<Classifier> insertion(Classifier cl, ArrayList<Classifier> P) {
		int flag = 0;
		for (Classifier ptr : P) {
			if (cl.getCondition().toString().equals(ptr.getCondition().toString())
					&& cl.getAction().i == ptr.getAction().i && cl.getAction().j == ptr.getAction().j) {
				int j = ptr.getNumerosity();
				ptr.setNumerosity(j + 1);
				flag = 1;
			}
		}
		if (flag == 0)
			P.add(cl);
		return P;
	}

	public static double deletion_vote(Classifier cl, ArrayList<Classifier> P) {
		double vote = cl.getAction_set_size() * cl.getNumerosity();
		double fitness_sum = 0.0;
		int num_sum = 0;
		for (Classifier ptr : P) {
			fitness_sum += ptr.getFitness();
			num_sum += ptr.getNumerosity();
		}
		double average_fitness_in_population = fitness_sum / (double) num_sum;
				
		if (cl.getExperience() > Main_XCS.Deletion_Threshold
				&& cl.getFitness() / cl.getNumerosity() < Main_XCS.fitness_Threshold * average_fitness_in_population) {
			vote = vote * average_fitness_in_population / (cl.getFitness() / cl.getNumerosity());
		}
		return vote;
	}

	public static ArrayList<Classifier> delete_from_population(ArrayList<Classifier> P) {
		int sum_numerosity = 0;
		
		for (Classifier ptr : P) {
			sum_numerosity += ptr.getNumerosity();
		}

		if (sum_numerosity > Main_XCS.size_Of_Population) {
			double vote_sum = 0.0;
			for (Classifier ptr : P) {
				vote_sum += deletion_vote(ptr, P);
			}
			double choice_point = Math.random() * vote_sum;

			vote_sum = 0.0;

			for(int i=0; i<P.size();++i) {
				Classifier ptr=P.get(i);
				vote_sum += deletion_vote(ptr, P);
				if (vote_sum > choice_point) {
					if (ptr.getNumerosity() > 1)
						ptr.setNumerosity(ptr.getNumerosity() - 1);
					else{
						P.remove(i);
						//System.out.println("deleted from population!!!!");
					}
				}
				i++;
			}
		}
		return P;
	}
}