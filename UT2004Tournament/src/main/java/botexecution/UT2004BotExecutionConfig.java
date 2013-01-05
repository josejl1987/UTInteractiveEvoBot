package botexecution;

import java.io.File;

import match.UT2004BotConfig;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

public class UT2004BotExecutionConfig {
	
	/**
	 * Unique id of this bot, used for reference inside tournament results.
	 * <p><p>
	 * DOES NOT MEAN THAT THE EXECUTED BOT WILL HAVE THIS ID IN UT2004!
	 */
	private IToken botId;
		
	/**
	 * Path to the directory that contains runnable jar file with the bot.
	 */
	private String pathToBotJar = null;
	
	/**
	 * Whether the StdErr of the bot execution should be redirected to log (== true, default) or sunk (== false).
	 */
	private boolean redirectStdErr = true;
	
	/**
	 * Whether the StdOut of the bot execution should be redirected to log (== true, default) or sunk (== false).
	 */
	private boolean redirectStdOut = true;

	/**
	 * {@link UT2004BotExecutionConfig#pathToBotJar} as a {@link File}.
	 */
	private File fileToJar;

	/**
	 * Returns ID of this bot configuration. This ID will be used for storing result of the tournament for this bot.
	 * <p><p>
	 * DOES NOT MEAN THAT THE EXECUTED BOT WILL HAVE THIS ID IN UT2004!
	 * 
	 * @return
	 */
	public IToken getBotId() {
		return botId;
	}
	
	/**
	 * Parameter-less constructor.
	 */
	public UT2004BotExecutionConfig() {		
	}

	/**
	 * Copy-constructor;
	 * @param value
	 */
	public UT2004BotExecutionConfig(UT2004BotConfig value) {
		this.botId = value.getBotId();
		this.setPathToBotJar(value.getPathToBotJar());
		this.redirectStdErr = value.isRedirectStdErr();
		this.redirectStdOut = value.isRedirectStdOut();
	}

	/**
	 * Sets ID of this bot configuration. This ID will be used for storing result of the tournament for this bot.
	 * <p><p>
	 * DOES NOT MEAN THAT THE EXECUTED BOT WILL HAVE THIS ID IN UT2004!
	 * 
	 * @param botId
	 */
	public UT2004BotExecutionConfig setBotId(String botId) {
		NullCheck.check(botId, "botId");
		this.botId = Tokens.get(botId);
		return this;
	}
	
	/**
	 * Sets ID of this bot configuration. This ID will be used for storing result of the tournament for this bot.
	 * <p><p>
	 * DOES NOT MEAN THAT THE EXECUTED BOT WILL HAVE THIS ID IN UT2004!
	 * 
	 * @param botId
	 */
	public UT2004BotExecutionConfig setBotId(IToken botId) {
		NullCheck.check(botId, "botId");
		this.botId = botId;
		return this;
	}
	
	/**
	 * Path to the runnable jar file contining the bot to be run.
	 * @return
	 */
	public String getPathToBotJar() {
		return pathToBotJar;
	}

	/** 
	 * Sets the path to jar-file of the bot.
	 * @param botDirPath
	 */
	public UT2004BotExecutionConfig setPathToBotJar(String pathToBotJar) {
		if (pathToBotJar != null) {
			this.fileToJar = new File(pathToBotJar);
		} else {
			this.fileToJar = null;
		}
		this.pathToBotJar = pathToBotJar;
		return this;
	}
	
	/**
	 * Whether the jar specified by this config exists.
	 * @return
	 */
	public boolean isBotJarExist() {
		if (this.pathToBotJar == null) return false;
		File file = getJarFile();
		return file.exists() && file.isFile(); 
	}

	/**
	 * Returns path to jar as a file.
	 * @return
	 */
	public File getJarFile() {
		return fileToJar;
	}
	
	/**
	 * Whether the StdErr of the bot execution should be redirected to log (== true, default) or sunk (== false).
	 * @return
	 */
	public boolean isRedirectStdErr() {
		return redirectStdErr;
	}

	/**
	 * Sets whether the StdErr of the bot execution should be redirected to log (== true, default) or sunk (== false).
	 * @param redirectStdErr
	 */
	public UT2004BotExecutionConfig setRedirectStdErr(boolean redirectStdErr) {
		this.redirectStdErr = redirectStdErr;
		return this;
	}

	/**
	 * Whether the StdOut of the bot execution should be redirected to log (== true, default) or sunk (== false).
	 * @return
	 */
	public boolean isRedirectStdOut() {
		return redirectStdOut;
	}

	/**
	 * Sets whether the StdOut of the bot execution should be redirected to log (== true, default) or sunk (== false).
	 * @param redirectStdOut
	 */
	public UT2004BotExecutionConfig setRedirectStdOut(boolean redirectStdOut) {
		this.redirectStdOut = redirectStdOut;
		return this;
	}

	@Override
	public String toString() {
		return "UT2004BotExecutionConfig[botId=" + botId.getToken() + ", jar=" + pathToBotJar + "]";
	}

}
