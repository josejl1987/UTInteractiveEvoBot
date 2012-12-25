package cz.cuni.amis.pogamut.ut2004.tournament.match;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.ut2004.tournament.GameBots2004Ini;
import cz.cuni.amis.pogamut.ut2004.tournament.utils.UT2004TournamentProperty;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapperConf;
import cz.cuni.amis.utils.Const;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.SafeEquals;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

/**
 * Base configuration of the UT2004 match, you have to specify:
 * <ol>
 * <li>Match id</li>
 * <li>Concrete GB2004Ini file to be used via {@link GameBots2004Ini}</li>
 * <li>Concrete UCC to run via {@link UCCWrapperConf}</li>
 * <li>Concrete list of bots to be used via {@link UT2004BotConfig}</li>
 * </ol>
 * Guess what... everything is preinitialized... but you should at least adjust uccConf.
 * 
 * @author Jimmy
 */
public class UT2004MatchConfig {

	protected File outputDirectory = new File("./results/matches");
	
	protected IToken matchId = Tokens.get("DMMatch");
	
	protected UCCWrapperConf uccConf = new UCCWrapperConf();
	
	protected GameBots2004Ini gb2004Ini = new GameBots2004Ini();
	
	protected Map<IToken, UT2004BotConfig> bots = new HashMap<IToken, UT2004BotConfig>();
	
	protected Map<IToken, UT2004NativeBotConfig> nativeBots = new HashMap<IToken, UT2004NativeBotConfig>();
	
	public UT2004MatchConfig() {
		String unrealHome = Pogamut.getPlatform().getProperty(UT2004TournamentProperty.UT2004_DIR.getKey());
		if (unrealHome != null) {
			uccConf.setUnrealHome(unrealHome);
		}
	}
	
	/**
	 * Copy-constructor.
	 * @param orig
	 */
	public UT2004MatchConfig(UT2004MatchConfig orig) {
		this.matchId = orig.matchId;
		this.uccConf = new UCCWrapperConf(orig.getUccConf());
		this.gb2004Ini = new GameBots2004Ini(orig.getGb2004Ini());
		for (Entry<IToken, UT2004BotConfig> bot : orig.getBots().entrySet()) {
			this.bots.put(bot.getKey(), new UT2004BotConfig(bot.getValue()));
		}
		for (Entry<IToken, UT2004NativeBotConfig> bot : orig.getNativeBots().entrySet()) {
			this.nativeBots.put(bot.getKey(), new UT2004NativeBotConfig(bot.getValue()));
		}
	}
		
	@Override
	public int hashCode() {
		return matchId == null ? super.hashCode() : matchId.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof UT2004MatchConfig)) return false;
		return SafeEquals.equals(matchId, ((UT2004MatchConfig)obj).getMatchId());
	}

	public File getOutputDirectory() {
		return outputDirectory;
	}

	public IToken getMatchId() {
		return matchId;
	}

	public void setMatchId(IToken matchId) {
		this.matchId = matchId;
	}
	
	public void setMatchId(String matchId) {
		this.matchId = Tokens.get(matchId);
	}

	public UCCWrapperConf getUccConf() {
		return uccConf;
	}

	/**
	 * Preinitialized automatically.
	 * @return
	 */
	public GameBots2004Ini getGb2004Ini() {
		return gb2004Ini;
	}
	
	public Map<IToken, UT2004BotConfig> getBots() {
		return bots;
	}
	
	public Map<IToken, UT2004NativeBotConfig> getNativeBots() {
		return nativeBots;
	}
	
	/**
	 * Ids are sorted: 1) custom bots, 2) native bots.
	 * <p><p>
	 * WARNING: O(n*log(n)) complexity!
	 * 
	 * @return
	 */
	public List<IToken> getAllBotIds() {
		List<IToken> bots = new ArrayList<IToken>(getBots().keySet());
		List<IToken> nativeBots = new ArrayList<IToken>(getNativeBots().keySet());
		Collections.sort(bots, new Comparator<IToken>() {
			@Override
			public int compare(IToken o1, IToken o2) {
				return o1.getToken().compareTo(o2.getToken());
			}				
		});
		Collections.sort(nativeBots, new Comparator<IToken>() {
			@Override
			public int compare(IToken o1, IToken o2) {
				return o1.getToken().compareTo(o2.getToken());
			}				
		});		
		List<IToken> botIds = new ArrayList<IToken>(bots);
		botIds.addAll(nativeBots);
		return botIds;
	}
	
	public UT2004MatchConfig setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
		return this;
	}
	
	public UT2004MatchConfig setUccConf(UCCWrapperConf uccConf) {
		this.uccConf = uccConf;
		return this;
	}
	
	/**
	 * No need to call, preinitialized automatically.
	 * @return
	 */
	public UT2004MatchConfig setGb2004Ini(GameBots2004Ini gb2004Ini) {
		this.gb2004Ini = gb2004Ini;
		return this;
	}

	public UT2004MatchConfig setBots(Map<IToken, UT2004BotConfig> bots) {
		this.bots = bots;
		return this;
	}
	
	public UT2004MatchConfig setNativeBots(Map<IToken, UT2004NativeBotConfig> nativeBots) {
		this.nativeBots = nativeBots;
		return this;
	}
	
	public UT2004MatchConfig clearBots() {
		this.bots.clear();
		return this;
	}
	
	public UT2004MatchConfig clearNativeBots() {
		this.nativeBots.clear();
		return this;
	}
	
	/**
	 * Adds NEW bot configuration into the object, checks whether there is no BotId clash (if so, throws an exception).
	 * @param bots
	 * @return
	 */
	public UT2004MatchConfig addBot(UT2004BotConfig... bots) {
		if (bots == null) return this;
		for (UT2004BotConfig bot : bots) {
			NullCheck.check(bot.getBotId(), "bot.getBotId()");
			if (this.bots.containsKey(bot.getBotId())) {
				throw new PogamutException("Can't add another bot under the id " + bot.getBotId().getToken() + ", there is already an existing custom bot configuration under this ID. If you need to override it, use setBot().", this);
			}
			if (this.nativeBots.containsKey(bot.getBotId())) {
				throw new PogamutException("Can't add another bot under the id " + bot.getBotId().getToken() + ", there is already an existing native bot configuration under this ID. If you need to override it, use setBot().", this);
			}
			this.bots.put(bot.getBotId(), bot);
		}
		return this;
	}
	
	/**
	 * Sets bot configuration into the object, does not checks whether there is BotId clash.
	 * 
	 * @param bots
	 * @return
	 */
	public UT2004MatchConfig setBot(UT2004BotConfig... bots) {
		if (bots == null) return this;
		for (UT2004BotConfig bot : bots) {
			NullCheck.check(bot.getBotId(), "bot.getBotId()");
			if (this.nativeBots.containsKey(bot.getBotId())) {
				throw new PogamutException("Can't add another bot under the id " + bot.getBotId().getToken() + ", there is already an existing native bot configuration under this ID. If you need to override it, use setBot().", this);
			}
			this.bots.put(bot.getBotId(), bot);
		}
		return this;
	}
	
	/**
	 * Adds NEW native bot configuration into the object, checks whether there is no BotId clash (if so, throws an exception).
	 * @param bots
	 * @return
	 */
	public UT2004MatchConfig addNativeBot(UT2004NativeBotConfig... bots) {
		if (bots == null) return this;
		for (UT2004NativeBotConfig bot : bots) {
			NullCheck.check(bot.getBotId(), "bot.getBotId()");
			if (this.bots.containsKey(bot.getBotId())) {
				throw new PogamutException("Can't add another bot under the id " + bot.getBotId().getToken() + ", there is already an existing custom bot configuration under this ID. If you need to override it, use setBot().", this);
			}
			if (this.nativeBots.containsKey(bot.getBotId())) {
				throw new PogamutException("Can't add another bot under the id " + bot.getBotId().getToken() + ", there is already an existing native bot configuration under this ID. If you need to override it, use setNativeBot().", this);
			}
			this.nativeBots.put(bot.getBotId(), bot);
		}
		return this;
	}
	
	/**
	 * Sets native bot configuration into the object, does not checks whether there is BotId clash.
	 * @param bots
	 * @return
	 */
	public UT2004MatchConfig setNativeBot(UT2004NativeBotConfig... bots) {
		if (bots == null) return this;
		for (UT2004NativeBotConfig bot : bots) {
			NullCheck.check(bot.getBotId(), "bot.getBotId()");
			if (this.bots.containsKey(bot.getBotId())) {
				throw new PogamutException("Can't add another bot under the id " + bot.getBotId().getToken() + ", there is already an existing custom bot configuration under this ID. If you need to override it, use setBot().", this);
			}
			this.nativeBots.put(bot.getBotId(), bot);
		}
		return this;
	}
	
	public boolean isNativeBot(IToken botId) {
		return nativeBots.containsKey(botId);
	}
	
	//
	// VALIDATION
	//
	
	protected final StringBuffer validationBuffer = new StringBuffer();
	
	protected boolean validationError = false;
	
	/**
	 * Performs validation of the match configuration:
	 * <ol>
	 * <li>Checks whether the match id is non-null.</li>
	 * <li>Checks whether all (custom+native) bots have path-to-jar configured + the file exists.</li>
	 * <li>{@link UccWrapper} is not null and its directory exists.</li>
	 * <li>Presence of GameBots2004.u as a new game type inside UT2004.</li> 
	 * <li>{@link GameBots2004Ini} is not null.</li>
	 * <li>Whether there are at least 2 bots defined for the match.</li>
	 * </ol>
	 * <p><p>
	 * Override to provide/add custom validation (might require copy-paste of the code from this method). 
	 */
	protected void validateInner() {
		if (matchId == null) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("Match ID is NULL");
		}
		for (UT2004BotConfig config : bots.values()) {
			if (config.getBotId() == null) {
				validationError = true;
				validationBuffer.append(Const.NEW_LINE);
				validationBuffer.append("One of custom bots has NULL bot-id.");
			}
			if (config.getPathToBotJar() == null) {
				validationError = true;
				validationBuffer.append(Const.NEW_LINE);
				validationBuffer.append(config.getBotId() == null ? "One of custom bots" : "Bot " + config.getBotId().getToken() + " has NULL path-to-jar.");
			} else {
				if (!config.isBotJarExist()) {
					validationError = true;
					validationBuffer.append(Const.NEW_LINE);
					validationBuffer.append(config.getBotId() == null ? "One of custom bots" : "Bot " + config.getBotId().getToken() + " has path-to-jar pointing to non-existing file " + config.getJarFile().getAbsolutePath());
				}
			}
		}
		for (UT2004NativeBotConfig config : nativeBots.values()) {
			if (config.getBotId() == null) {
				validationError = true;
				validationBuffer.append(Const.NEW_LINE);
				validationBuffer.append("One of native bots has NULL bot-id.");
			}			
		}
		if (gb2004Ini == null) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("GameBots2004Ini is NULL.");			
		}
		if (uccConf == null) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("UccWrapper is NULL.");
		} else {
			if (uccConf.getUnrealHome() == null) {
				validationError = true;
				validationBuffer.append(Const.NEW_LINE);
				validationBuffer.append("UccWrapper does not have UnrealHome set, is NULL.");
			} else {
				File uccHome = new File(uccConf.getUnrealHome());
				if (!uccHome.exists() || !uccHome.isDirectory()) {
					validationError = true;
					validationBuffer.append(Const.NEW_LINE);
					validationBuffer.append("UccWrapper.UnrealHome does not point to directory, used path: " + uccHome.getAbsolutePath());
				} else {
					File uccHome2 = new File(uccConf.getUnrealHome() + File.separator + "System");
					if (!uccHome2.exists() || !uccHome2.isDirectory()) {
						validationError = true;
						validationBuffer.append(Const.NEW_LINE);
						validationBuffer.append("UccWrapper.UnrealHome" + File.separator + "System does not point to directory, used path: " + uccHome2.getAbsolutePath());
					} else { 
						File gb = new File(uccConf.getUnrealHome() + File.separator + "System" + File.separator + "GameBots2004.u");
						if (!gb.exists() || !gb.isFile()) {
							validationError = true;
							validationBuffer.append(Const.NEW_LINE);
							validationBuffer.append("GameBots2004 was not installed into specified UT2004, the file GameBots2004.u was not found at: " + gb.getAbsolutePath());
						}
					}
				}
			}
		}
		if (bots.size() + nativeBots.size() < 2) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("There are not enough bot specified for the match. Custom bots: " + bots.size() + ", native bots: " + nativeBots.size() + ". There must be at least 2 bots to perform the match!");
		}
	}
	
	/**
	 * Checks the contents, whether everything is set-up correctly, if not, raises an exception with explanation.
	 * <p><p>
	 * Recalls {@link UT2004MatchConfig#validateInner()} that should fill {@link UT2004MatchConfig#validationBuffer} with
	 * messages and if there is a fatal error in the configuration, it should set true to {@link UT2004MatchConfig#validationError}.
	 */
	public final void validate() {
		synchronized(validationBuffer) {
			validationError = false;
			if (validationBuffer.length() > 0) {
				validationBuffer.delete(0, validationBuffer.length());
			}
			validateInner();
			if (validationError) {
				throw new PogamutException(this + " validation error!" + validationBuffer.toString(), this);
			}
		}
	}
	
}
