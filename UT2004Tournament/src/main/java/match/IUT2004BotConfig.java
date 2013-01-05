package match;

import cz.cuni.amis.utils.token.IToken;

/**
 * Simple interface for data describing the bot.
 * 
 * @author Jimmy
 */
public interface IUT2004BotConfig {

	/**
	 * Returns ID of this bot configuration. This ID will be used for storing result of the tournament for this bot.
	 * <p><p>
	 * DOES NOT MEAN THAT THE EXECUTED BOT WILL HAVE THIS ID IN UT2004!
	 * 
	 * @return
	 */
	public IToken getBotId();
	
	/**
	 * Returns team of the bot.
	 * @return
	 */
	public int getTeamNumber();
	
}
