cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:checkers trials:10 maxGens:500 mu:100 io:true netio:true mating:true task:edu.southwestern.tasks.boardGame.SinglePopulationCompetativeCoevolutionBoardGameTask cleanOldNetworks:true fs:false log:Checkers-HNSinglePopCompCoevolve10RandomHallOfFame saveTo:HNSinglePopCompCoevolve10RandomHallOfFame boardGame:edu.southwestern.boardGame.checkers.Checkers genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype hyperNEAT:true boardGameOpponent:edu.southwestern.boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning boardGameOpponentHeuristic:edu.southwestern.boardGame.heuristics.PieceDifferentialBoardGameHeuristic boardGamePlayer:edu.southwestern.boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning minimaxSearchDepth:2 minimaxRandomRate:0.10 hallOfFame:true hallOfFameXRandomChamps:true hallOfFameYPastGens:true