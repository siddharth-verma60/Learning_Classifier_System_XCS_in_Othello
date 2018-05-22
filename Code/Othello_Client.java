package Code;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class Othello_Client {

	public static void main(String[] args) {
		int num_games = 1;
		int n = 0;
		double black_wins = 21429;
		double white_wins = 16528;
		double ties = 2043;
		double moving_black_percentage_wins = 0;
		double moving_white_percentage_wins = 0;
		double moving_tie_percentage = 0;
		FileWriter csvfile;
		
		try {
			csvfile=new FileWriter("gameStatus.csv", true);
			//csvfile.append(
					//"gameNo,result,black_wins,ties,white_wins,blackPercentage,whitePercentage,tiePercentage,avg_prediction_error,avg_fitness,avg_prediction,sum_numerosity");
			csvfile.append("\n");

			while (n++ < num_games) {
				Othello game = new Othello();
				Main_XCS xcs_agent = new Main_XCS(game);
				Environment e = new Environment();
				boolean is_complete = false;

				while (true) {
					if (game.isBlackTurn) {
						//double x = (2.0 * n - 40000) / 20000;
						//double aph = 1 / (1 + Math.exp(-x));
						xcs_agent.run_experiment();
					} else {
//						ArrayList<Two_d_array_indices> move_list = e.get_pink_minimax(game);
//						int pos = (int) (Math.random() * move_list.size());
//						Two_d_array_indices action = move_list.get(pos);
//						e.take_action(game, action);
						
						TimeUnit.SECONDS.sleep(1);
						if(game.isBlackTurn){
							continue;
						}
					}

					is_complete = game.is_game_complete();

					if (is_complete) {
						String winner = "";
						game.declareWinner();

						if (game.get_winner().equals("Black")) {
							black_wins++;
							winner = "black";
						} else if (game.get_winner().equals("White")) {
							white_wins++;
							winner = "white";
						} else {
							ties++;
							winner = "tie";
						}

						xcs_agent.QRewardUpdate(xcs_agent.action_Set, xcs_agent.reward, xcs_agent.env);
						xcs_agent.END_reward();
						xcs_agent.appy_GA();
						xcs_agent.write_population_set_to_file();

						moving_black_percentage_wins = ((black_wins / (n)) * 100);
						moving_white_percentage_wins = ((white_wins / (n)) * 100);
						moving_tie_percentage = (((ties / (n)) * 100));

						System.out.println(n + "," + winner + "," + moving_black_percentage_wins + ","
								+ moving_white_percentage_wins + "," + moving_tie_percentage);

						Parameters p = xcs_agent.get_parameters();
						
						csvfile.append(n + "," + winner + "," + black_wins + "," + ties + "," + white_wins
								+ "," + moving_black_percentage_wins + "," + moving_white_percentage_wins + ","
								+ moving_tie_percentage + "," + p.prediction_error + "," + p.fitness + ","
								+ p.prediction + "," + p.numerosity);
						
						csvfile.append("\n");
						
						break;

					}
				}
				csvfile.flush();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} 
	}
}
