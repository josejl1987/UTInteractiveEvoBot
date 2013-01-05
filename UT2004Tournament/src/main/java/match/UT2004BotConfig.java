package match;

import java.io.File;

import botexecution.UT2004BotExecutionConfig;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

/**
 * Describes configuration of the custom-bot created using Pogamut platform.
 * @author Jimmy
 */
public class UT2004BotConfig extends UT2004BotExecutionConfig implements IUT2004BotConfig {

	/**
	 * Number of the team the bot should be in.
	 */
	private int teamNumber = 255;

	public UT2004BotConfig() {
	}
	
	/**
	 * Copy-constructor.
	 * @param value
	 */
	public UT2004BotConfig(UT2004BotConfig value) {
		super(value);
		this.teamNumber = value.teamNumber;
	}

	@Override
	public int getTeamNumber() {
		return teamNumber;
	}

	/**
	 * Sets team number the bot should play for.
	 * @param teamNumber
	 */
	public UT2004BotConfig setTeamNumber(int teamNumber) {
		this.teamNumber = teamNumber;
		return this;
	}
	
	@Override
	public UT2004BotConfig setBotId(String botId) {
		super.setBotId(botId);
		return this;
	}
	
	@Override
	public UT2004BotConfig setBotId(IToken botId) {
		super.setBotId(botId);
		return this;
	}
	
	@Override
	public UT2004BotConfig setPathToBotJar(String pathToBotJar) {
		super.setPathToBotJar(pathToBotJar);
		return this;
	}
	
	@Override
	public UT2004BotConfig setRedirectStdErr(boolean redirectStdErr) {
		super.setRedirectStdErr(redirectStdErr);
		return this;
	}
	
	@Override
	public UT2004BotConfig setRedirectStdOut(boolean redirectStdOut) {
		super.setRedirectStdOut(redirectStdOut);
		return this;
	}
	
	@Override
	public String toString() {
		return "UT2004BotConfig[botId=" + getBotId().getToken() + ", team=" + teamNumber + ", jar=" + getPathToBotJar() + "]";
	}
}
