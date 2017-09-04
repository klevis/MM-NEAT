cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:othello trials:10 maxGens:200 mu:50 io:true netio:true mating:true task:edu.southwestern.tasks.boardGame.SinglePopulationCompetativeCoevolutionBoardGameTask cleanOldNetworks:true fs:false log:Othello-HNSinglePopCompCoevolve10Rand saveTo:HNSinglePopCompCoevolve10Rand boardGame:edu.southwestern.boardGame.othello.Othello genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype hyperNEAT:true boardGameOpponentHeuristic:edu.southwestern.boardGame.heuristics.StaticOthelloWPCHeuristic boardGameOpponent:edu.southwestern.boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning boardGamePlayer:edu.southwestern.boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning minimaxSearchDepth:2 minimaxRandomRate:0.10 boardGameStaticOpponentRuns:5 boardGameOthelloFitness:true boardGameSimpleFitness:true randomArgMaxTieBreak:false