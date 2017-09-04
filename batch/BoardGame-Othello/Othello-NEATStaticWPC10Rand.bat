cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:othello trials:10 maxGens:200 mu:50 io:true netio:true mating:true task:edu.southwestern.tasks.boardGame.StaticOpponentBoardGameTask cleanOldNetworks:true fs:false log:Othello-NEATStaticWPC10Rand saveTo:NEATStaticWPC10Rand boardGame:edu.southwestern.boardGame.othello.Othello boardGameOpponent:edu.southwestern.boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning boardGameOpponentHeuristic:edu.southwestern.boardGame.heuristics.StaticOthelloWPCHeuristic boardGamePlayer:edu.southwestern.boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning minimaxSearchDepth:2 minimaxRandomRate:0.10 boardGameOthelloFitness:true boardGameSimpleFitness:true boardGameWinPercentFitness:true randomArgMaxTieBreak:false evalReport:true heuristicOverrideTerminalStates:true