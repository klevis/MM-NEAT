cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:othello trials:10 maxGens:500 mu:100 io:true netio:true mating:true task:edu.southwestern.tasks.boardGame.StaticOpponentBoardGameTask cleanOldNetworks:true fs:false log:Othello-HNStaticHuman saveTo:HNStaticHuman boardGame:edu.southwestern.boardGame.othello.Othello boardGameOpponent:edu.southwestern.boardGame.agents.BoardGamePlayerHuman2DBoard boardGameOpponentHeuristic:edu.southwestern.boardGame.heuristics.StaticOthelloWPCHeuristic boardGamePlayer:edu.southwestern.boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning minimaxSearchDepth:2 genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype hyperNEAT:true watch:true