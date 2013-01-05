package match;

import cz.cuni.amis.utils.token.IToken;

/**
 * Represents the result of the match (very limited). Just stating whether the match was fought by teams/individuals
 * and the winner.
 * 
 * @author Jimmy
 */
public class UT2004MatchResult {

	private boolean individual;
	private IToken winnerBot;
	private int winnerTeam;
	private boolean draw;

	public UT2004MatchResult() {		
	}
	
	public UT2004MatchResult(IToken winnerBot) {
		this.individual = true;
		this.winnerBot = winnerBot;
	}
	
	public UT2004MatchResult(int winnerTeam) {
		this.individual = false;
		this.winnerTeam = winnerTeam;
	}

	public boolean isIndividual() {
		return individual;
	}

	public IToken getWinnerBot() {
		return winnerBot;
	}

	public int getWinnerTeam() {
		return winnerTeam;
	}

	public void setIndividual(boolean individual) {
		this.individual = individual;
	}

	public void setWinnerBot(IToken winnerBot) {
		this.winnerBot = winnerBot;
		this.individual = true;
	}

	public void setWinnerTeam(int winnerTeam) {
		this.winnerTeam = winnerTeam;
		this.individual = false;
	}
	
	public boolean isDraw() {
		return draw;
	}

	public void setDraw(boolean draw) {
		this.draw = draw;
	}
	
}
