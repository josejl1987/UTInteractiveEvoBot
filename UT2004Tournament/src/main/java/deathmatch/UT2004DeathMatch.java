package deathmatch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateUp;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.utils.guice.AdaptableProvider;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentStats;
import cz.cuni.amis.pogamut.ut2004.analyzer.IUT2004AnalyzerObserver;
import cz.cuni.amis.pogamut.ut2004.analyzer.UT2004Analyzer;
import cz.cuni.amis.pogamut.ut2004.analyzer.stats.UT2004AnalyzerObsStats;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StartPlayers;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerScore;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import botexecution.UT2004BotExecution;
import match.UT2004BotConfig;
import match.UT2004Match;
import match.UT2004MatchResult;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapper;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.FilePath;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.exception.PogamutIOException;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.flag.FlagListener;
import cz.cuni.amis.utils.token.IToken;

public class UT2004DeathMatch extends UT2004Match<UT2004DeathMatchConfig, UT2004DeathMatchResult> {

	public UT2004DeathMatch(UT2004DeathMatchConfig config, LogCategory log) {
		super(false, config, log);		
	}
	
	@Override
	protected void changeBotTeam(UT2004Server server, UnrealId botId, int desiredTeam) {
		// THERE IS NO NEED TO CHANGE BOT TEAM IN DEATHMATCH!
	}

	@Override
	protected UT2004MatchResult waitMatchFinish(UCCWrapper ucc, UT2004Server server, UT2004Analyzer analyzer, Bots bots, long timeoutInMillis) {
		// usually the GB2004 dies out whenever match ends -> just wait till server does not fail + timeout + observe bots
		
		if (log != null && log.isLoggable(Level.WARNING)) {
			log.warning(config.getMatchId().getToken() + ": Waiting for the match to finish...");
		}
		
		if (config.getTimeLimit() * 60 * 1000 + 5 * 60 * 1000 > timeoutInMillis) {
			timeoutInMillis = config.getTimeLimit() * 60 * 1000 + 5 * 60 * 1000; // give additional 5 minutes to UT2004 to restart GB2004
		}
		
		Map<IToken, FlagListener<Boolean>> customBotObservers = new HashMap<IToken, FlagListener<Boolean>>(config.getBots().size());
		FlagListener<IAgentState> serverObs = null;
		FlagListener<Boolean> uccObs = null;
		IWorldEventListener<PlayerScore> scoresListener = null;
		
		final CountDownLatch waitLatch = new CountDownLatch(1);
		final AdaptableProvider<Boolean> oneOfBotsDiedOut = new AdaptableProvider<Boolean>(false);
		final AdaptableProvider<Boolean> serverDiedOut = new AdaptableProvider<Boolean>(false);
		final Map<UnrealId, PlayerScore> scores = new HashMap<UnrealId, PlayerScore>();
		
		boolean exception = false;
		
		try {
			scoresListener = new IWorldEventListener<PlayerScore>() {

				@Override
				public void notify(PlayerScore event) {
					scores.put(event.getId(), event);
				}
				
			};
			server.getWorldView().addEventListener(PlayerScore.class, scoresListener);
			
			for (UT2004BotConfig botConfig : config.getBots().values()) {
				FlagListener<Boolean> obs = new FlagListener<Boolean>() {
					@Override
					public void flagChanged(Boolean changedValue) {
						if (!changedValue) {
							// bot has died out
							oneOfBotsDiedOut.set(true);
							waitLatch.countDown();
						}
					}
				};
				
				bots.bots.get(botConfig.getBotId()).getRunning().addListener(obs);
				customBotObservers.put(botConfig.getBotId(), obs);
				if (!bots.bots.get(botConfig.getBotId()).getRunning().getFlag()) {
					// bot has died out
					oneOfBotsDiedOut.set(true);
					waitLatch.countDown();
					throw new PogamutException("One of custom bots died out from the start, failure!", log, this);
				}			
			}
			
			serverObs = new FlagListener<IAgentState>() {
	
				@Override
				public void flagChanged(IAgentState changedValue) {
					if (changedValue instanceof IAgentStateDown) {
						// server has died out ... consider match to be over...
						serverDiedOut.set(true);
						waitLatch.countDown();
					}
				}
				
			};
			
			server.getState().addListener(serverObs);
			
			if (server.notInState(IAgentStateUp.class)) {
				// server has died out ... consider match to be over...
				serverDiedOut.set(true);
				waitLatch.countDown();
				throw new PogamutException("Server is dead from the start, failure!", log, this);
			}
			
			uccObs = new FlagListener<Boolean>() {

				@Override
				public void flagChanged(Boolean changedValue) {
					if (changedValue) {
						// GAME IS ENDING!
						// Consider match to be over...
						serverDiedOut.set(true);
						waitLatch.countDown();
					}
				}
				
			};
			
			ucc.getGameEnding().addListener(uccObs);
			
			waitLatch.await(timeoutInMillis, TimeUnit.MILLISECONDS);
			if (waitLatch.getCount() > 0) {
				// TIMEOUT!
				throw new PogamutException("TIMEOUT! The match did not end in " + (timeoutInMillis / 1000) + " secs.", log, this);
			}
			
			bots.matchEnd = System.currentTimeMillis();
			
			// WHAT HAS HAPPENED?
			if (oneOfBotsDiedOut.get()) {
				// check whether the server is down as well... but let GB2004 to process it
				try {
					Thread.sleep(22000);
				} catch (InterruptedException e) {
					throw new PogamutInterruptedException("Interrupted while giving GB2004 time to tear down its connection.", log, this);
				}
				try {
					server.getAct().act(new StartPlayers());
				} catch (Exception e) {
					// YEP, server is down
					serverDiedOut.set(true);
				}
				if (!serverDiedOut.get()) {
					// NO SERVER IS STILL RUNNING
					log.warning("ONE OF BOTS HAS DIED OUT, BUT SERVER IS STILL RUNNING ... POSSIBLE MATCH FAILURE!");
				}
			}
                        Thread.sleep(50000);
			if (!serverDiedOut.get() && server.inState(IAgentStateUp.class)) {
				// server is still running? Kill it...
				server.kill();
			}
			// server is DEAD -> assume that the match has ended
			
			// KILL UCC TO ENSURE NOTHING WILL CHANGE AFTER THAT
			if (ucc != null) {
				try {
                                    					Thread.sleep(22000);

					if (log != null && log.isLoggable(Level.INFO)) {
						log.info(config.getMatchId().getToken() + ": Killing UCC...");
					} 
				} catch (Exception e) {				
				}
				try {
					ucc.stop();
				} catch (Exception e) {					
				}
			}
			
			List<UnrealId> winners = new ArrayList<UnrealId>(1);
			int maxFrags = 0;
			
			// PROCESS THE RESULT
			for (Entry<UnrealId, IToken> entry : bots.unrealId2BotId.entrySet()) {
				PlayerScore playerScore = scores.get(entry.getKey());
				if (playerScore == null) {
					throw new PogamutException("Can't resolve the match result. One of the bot with botId '" + entry.getValue().getToken() + "' and corresponding unrealId '" + entry.getKey().getStringId() + "' has no score entry!", log, this);
				}
				if (playerScore.getScore() == maxFrags) {
					winners.add(entry.getKey());
				} else
				if (playerScore.getScore() > maxFrags) {
					maxFrags = playerScore.getScore();
					winners.clear();
					winners.add(entry.getKey());
				}
			}
			for (Entry<UnrealId, IToken> entry : bots.nativeUnrealId2BotId.entrySet()) {
				PlayerScore playerScore = scores.get(entry.getKey());
				if (playerScore == null) {
					throw new PogamutException("Can't resolve the match result. One of the bot with botId '" + entry.getValue().getToken() + "' and corresponding unrealId '" + entry.getKey().getStringId() + "' has no score entry!", log, this);
				}
				if (playerScore.getScore() == maxFrags) {
					winners.add(entry.getKey());
				} else
				if (playerScore.getScore() > maxFrags) {
					maxFrags = playerScore.getScore();
					winners.clear();
					winners.add(entry.getKey());
				}
			}
			
			if (winners.size() == 0) {
				// no one has reached FragLimit
				throw new PogamutException("There is no winner, impossible! **puzzled**", log, this);
			}

			
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning(config.getMatchId().getToken() + ": MATCH FINISHED!");
			}
			
			return processResults(ucc, server, analyzer, bots, winners, scores);
			
		} catch (Exception e) {
			exception = true;
			throw new PogamutException("Failed to perform the match!", e, log, this);
		} finally {
			for (Entry<IToken, FlagListener<Boolean>> entry : customBotObservers.entrySet()) {
				bots.bots.get(entry.getKey()).getRunning().removeListener(entry.getValue());
			}
			server.getState().removeListener(serverObs);
			server.getWorldView().removeEventListener(PlayerScore.class, scoresListener);
		}		

	}
	
	protected UT2004DeathMatchResult processResults(UCCWrapper ucc, UT2004Server server, UT2004Analyzer analyzer, Bots bots, List<UnrealId> winners, Map<UnrealId, PlayerScore> finalScores) {
		
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Processing results...");
		}
		
		UT2004DeathMatchResult result = new UT2004DeathMatchResult();
		
		result.setMatchTimeEnd(((double)bots.matchEnd - (double)bots.matchStart) / (1000));
		
		for (Entry<UnrealId, PlayerScore> entry : finalScores.entrySet()) {
			result.getFinalScores().put(bots.getBotId(entry.getKey()), entry.getValue());
		}
		
		for (Entry<IToken, IUT2004AnalyzerObserver> entry : bots.botObservers.entrySet()) {
			if (!(entry.getValue() instanceof UT2004AnalyzerObsStats)) {
				throw new PogamutException("There is an observer of wrong class, expecting UT2004AnalyzerObsStats, got " + entry.getValue().getClass().getSimpleName() + "!", log, this);
			}
			result.getBotObservers().put(entry.getKey(), (UT2004AnalyzerObsStats)entry.getValue());
		}
		
		List<IToken> botIds = config.getAllBotIds();
		for (IToken botId1 : botIds) {
			result.getTotalKills().put(botId1, 0);
			result.getWasKilled().put(botId1, 0);
			result.getSuicides().put(botId1, 0);
			for (IToken botId2 : botIds) {
				result.getKillCounts().put(botId1, botId2, 0);
			}
		}
		
		for (Entry<IToken, UT2004AnalyzerObsStats> entry : result.getBotObservers().entrySet()) {
			IToken botId = entry.getKey();
			UT2004AnalyzerObsStats obs = entry.getValue();
			AgentStats stats = obs.getStats();
			for (Entry<UnrealId, Integer> killed : stats.getKilled().entrySet()) {
				result.getKillCounts().get(botId).put(bots.getBotId(killed.getKey()), killed.getValue());				
			}
			for (Entry<UnrealId, Integer> killedBy : stats.getKilledBy().entrySet()) {
				if (bots.isNativeBot(killedBy.getKey())) {
					result.getKillCounts().get(bots.getBotId(killedBy.getKey())).put(botId, killedBy.getValue());
				}
			}
			result.getSuicides().put(botId, stats.getSuicides());
			result.getKillCounts().put(botId, botId, stats.getSuicides());
		}
		
		for (IToken nativeBotId1 : config.getNativeBots().keySet()) {
			for (IToken nativeBotId2 : config.getNativeBots().keySet()) {
				if (nativeBotId1 == nativeBotId2) continue;
				result.getKillCounts().get(nativeBotId1).put(nativeBotId2, 0);
			}
			result.getSuicides().put(nativeBotId1, 0);
		}
		
		for (IToken botId : botIds) {
			int totalKills = 0;
			int totalKilled = 0;
			for (IToken other : botIds) {
				if (botId == other) continue;
				totalKills += result.getKillCounts().get(botId, other);
				totalKilled += result.getKillCounts().get(other, botId);
			}
			result.getTotalKills().put(botId, totalKills);
			result.getWasKilled().put(botId, totalKilled);
			if (config.isNativeBot(botId)) {
				result.getSuicides().put(botId, result.getFinalScores().get(botId).getDeaths() - totalKilled);
			}
		}
		
		if (winners.size() <= 0) {
			throw new PogamutException("There is no winner, impossible! **puzzled**", log, this);
		} else 
		if (winners.size() == 1) {
			result.setWinnerBot(bots.getBotId(winners.get(0)));
		} else {
			result.setDraw(true);
		}
		
		if (log != null && log.isLoggable(Level.WARNING)) {
			log.warning(config.getMatchId().getToken() + ": Results processed, " + (result.isDraw() ? "DRAW!" : "winner is Bot[botId=" + result.getWinnerBot().getToken() + ", unrealId=" + bots.getUnrealId(result.getWinnerBot()).getStringId() + "]."));
		}
		
		return result;
	}
	
	protected void outputResults_step1(UT2004DeathMatchResult result, File outputDirectory) {
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Outputting match result into CSV file...");
		}
		
		File file = new File(outputDirectory.getAbsolutePath() + File.separator + "match-" + config.getMatchId().getToken() + "-result.csv");
		FilePath.makeDirsToFile(file);
		try {
			Formatter writer = new Formatter(file);
			writer.format("MatchId;FragLimit;TimeLimit;TimeEnd;Winner\n");
			writer.format
					(
						"%s;%d;%d;%.3f;%s",
						config.getMatchId().getToken(),
						config.getFragLimit(),
						config.getTimeLimit(),
						result.getMatchTimeEnd(),
						result.isDraw() ? "DRAW" : String.valueOf(result.getWinnerBot().getToken())
					);
			try {
				writer.close();
			} catch (Exception e) {			
			}
		} catch (IOException e) {
			throw new PogamutIOException("Failed to write results!", e, log, this);
		}
		
		if (log != null && log.isLoggable(Level.INFO)) {
			log.info(config.getMatchId().getToken() + ": Match result output into " + file.getAbsolutePath() + ".");
		}
		
	}
	
	protected void outputResults_step2(UT2004DeathMatchResult result, File outputDirectory) {
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Outputting match scores into CSV file...");
		}
		
		File file = new File(outputDirectory.getAbsolutePath() + File.separator + "match-" + config.getMatchId().getToken() + "-bot-scores.csv");
		FilePath.makeDirsToFile(file);
		try {
			Formatter writer = new Formatter(file);
			
			List<IToken> bots = new ArrayList<IToken>(config.getBots().keySet());
			List<IToken> nativeBots = new ArrayList<IToken>(config.getNativeBots().keySet());
			
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
			result.setBots(bots);
			result.setNativeBots(nativeBots);
			
			writer.format("botId");
			writer.format(";score;kills;killedByOthers;deaths;suicides");
			for (IToken token : config.getAllBotIds()) {
				writer.format(";");
				writer.format(token.getToken());
			}
			
			for (IToken token : config.getAllBotIds()) {
				writer.format("\n");
				writer.format(token.getToken());
				writer.format(";%d", result.getFinalScores().get(token).getScore());
				writer.format(";%d", result.getTotalKills().get(token));
				writer.format(";%d", result.getWasKilled().get(token));
				writer.format(";%d", result.getFinalScores().get(token).getDeaths());
				writer.format(";%d", result.getSuicides().get(token));				
				for (IToken token2 : config.getAllBotIds()) {
					writer.format(";%d", result.getKillCounts().get(token).get(token2));
				}				
			}
			
			try {
				writer.close();
			} catch (Exception e) {			
			}
		} catch (IOException e) {
			throw new PogamutIOException("Failed to write results!", e, log, this);
		}
		
		if (log != null && log.isLoggable(Level.INFO)) {
			log.info(config.getMatchId().getToken() + ": Match scores output into " + file.getAbsolutePath() + ".");
		}
		
	}
	
	@Override
	protected void outputResults(UCCWrapper ucc, UT2004Server server, UT2004Analyzer analyzer, Bots bots, UT2004MatchResult result,	File outputDirectory) {
		if (!(result instanceof UT2004DeathMatchResult)) {
			throw new PogamutException("Can't out results! Expected results of class UT2004DeathMatchResult and got " + result.getClass().getSimpleName() + "!", log, this);
		}
		outputResults_step1((UT2004DeathMatchResult) result, outputDirectory);
		outputResults_step2((UT2004DeathMatchResult) result, outputDirectory);
	}
	
	@Override
	public UT2004DeathMatchResult execute() {
		try {
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning(config.getMatchId().getToken() + ": Executing!");
			} 
		} catch (Exception e) {				
		}
		
		UCCWrapper ucc = null;
		UT2004Server server = null;
		Bots bots = null;
		UT2004Analyzer analyzer = null;
		String recordFileName = config.getMatchId().getToken() + "-replay-" + UT2004Match.getCurrentDate();
		boolean exception = false;
		
		try {
			// STEP 0
			setupLogger();
			
			// STEP 1
			validate();
			
			// STEP 2
			createGB2004Ini();
			
			// STEP 3
			ucc = startUCC();
			
			// STEP 4
			server = startControlServer(ucc);
			
			// STEP 5
			bots = startBots(ucc, server);
			
			// STEP 6
		//	analyzer = startAnalyzer(ucc, bots, getOutputPath("bots"));
			
			// STEP 7
			matchIsAboutToBegin(ucc, server, analyzer, bots);
			
			// STEP 8
			restartMatch(server, bots);
			
			// STEP 9			
			recordReplay(server, recordFileName);
			
			// STEP 9.5
			UT2004DeathMatchResult result = (UT2004DeathMatchResult) waitMatchFinish(ucc, server, analyzer, bots, config.getTimeLimit() * 1000 + 60 * 1000);
			
			// STEP 11
			copyReplay(ucc, recordFileName, getOutputPath());

			// STEP 12
			outputResults(ucc, server, analyzer, bots, result, getOutputPath());
			
			// STEP 13
			shutdownAll(ucc, server, analyzer, bots);
			
			ucc = null;
			server = null;
			analyzer = null;
			bots = null;
			
			// WE'RE DONE! ... all that is left is a possible cleanup...
			return result;
			
		} catch (Exception e) {
			if (log != null && log.isLoggable(Level.SEVERE)) {
				log.severe(ExceptionToString.process(config.getMatchId().getToken() + ": EXCEPTION!", e));
			}
			exception = true;
			if (e instanceof PogamutException) throw (PogamutException)e;
			throw new PogamutException(e, log, this);
		} finally {		
			try {
				if (log != null && log.isLoggable(Level.INFO)) {
					log.info(config.getMatchId().getToken() + ": Cleaning up...");
				} 
			} catch (Exception e) {				
			}
			
			if (ucc != null) {
				try {
					if (log != null && log.isLoggable(Level.INFO)) {
						log.info(config.getMatchId().getToken() + ": Killing UCC...");
					} 
				} catch (Exception e) {				
				}
				try {
					ucc.stop();
				} catch (Exception e) {					
				}
			}
			if (server != null) {
				try {
					if (log != null && log.isLoggable(Level.INFO)) {
						log.info(config.getMatchId().getToken() + ": Killing UT2004Server...");
					} 
				} catch (Exception e) {				
				}
				try {
					server.kill();
				} catch (Exception e) {					
				}
			}
			if (bots != null) {
				try {
					if (log != null && log.isLoggable(Level.INFO)) {
						log.info(config.getMatchId().getToken() + ": Killing Custom bots...");
					} 
				} catch (Exception e) {				
				}
				for (UT2004BotExecution exec : bots.bots.values()) {
					try {
						exec.stop();					
					} catch (Exception e) {					
					}
				}
				try {
					if (log != null && log.isLoggable(Level.INFO)) {
						log.info(config.getMatchId().getToken() + ": Killing Custom bot observers...");
					} 
				} catch (Exception e) {				
				}
				for (IUT2004AnalyzerObserver obs : bots.botObservers.values()) {
					try {
						obs.kill();
					} catch (Exception e) {						
					}
				}
			}
			if (analyzer != null) {
				try {
					if (log != null && log.isLoggable(Level.INFO)) {
						log.info(config.getMatchId().getToken() + ": Killing UT2004Analyzer...");
					} 
				} catch (Exception e) {				
				}
				try {
					analyzer.kill();
				} catch (Exception e) {					
				}
			}	
			
			try {
				// STEP 10
				restoreGB2004IniBackup();
			} catch (Exception e) {				
			}
			
			try {
				if (log != null && log.isLoggable(Level.WARNING)) {
					if (exception) {
						log.warning(config.getMatchId().getToken() + ": Cleaned up, MATCH FAILED!");
					} else { 
						log.warning(config.getMatchId().getToken() + ": Cleaned up, match finished successfully.");
					}
				} 
			} catch (Exception e) {				
			}
			try {
				closeLogger();
			} catch (Exception e) {
				
			}
		}
		
	}

}
