cd ..
cd ..
java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:microRTS trials:3 maxGens:500 mu:10 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.microrts.MicroRTSTask cleanOldNetworks:true fs:false log:MicroRTS-PortfolioIterative saveTo:PortfolioIterative watch:false microRTSEvaluationFunction:edu.utexas.cs.nn.tasks.microrts.evaluation.NN2DEvaluationFunction microRTSFitnessFunction:edu.utexas.cs.nn.tasks.microrts.fitness.ProgressiveFitnessFunction microRTSAgent:micro.ai.portfolio.PortfolioAI microRTSEnemySequence:edu.utexas.cs.nn.tasks.microrts.iterativeevolution.CompetitiveEnemySequence