cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:othello trials:10 maxGens:500 mu:100 io:true netio:true mating:true task:edu.southwestern.tasks.boardGame.SinglePopulationCompetativeCoevolutionBoardGameTask cleanOldNetworks:true fs:false log:Othello-HNSinglePopCompCoevolveHOF20Random5Champs50Gens saveTo:HNSinglePopCompCoevolveHOF20Random5Champs50Gens boardGame:edu.southwestern.boardGame.othello.Othello genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype hyperNEAT:true boardGameOpponentHeuristic:edu.southwestern.boardGame.heuristics.StaticOthelloWPCHeuristic boardGameOpponent:edu.southwestern.boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning boardGamePlayer:edu.southwestern.boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning minimaxSearchDepth:2 hallOfFame:true hallOfFameXRandomChamps:true hallOfFameNumChamps:5 hallOfFameYPastGens:true hallOfFamePastGens:50 minimaxRandomRate:0.20