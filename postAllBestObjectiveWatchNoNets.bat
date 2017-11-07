REM Usage:   postBestObjectiveWatch.bat <experiment directory> <log prefix> <run type> <run number> <number of trials per individual>
REM Example: postBestObjectiveWatch.bat onelifeconflict OneLifeConflict OneModule 0 5
java -ea -jar "target/MM-NEAT-0.0.1-SNAPSHOT.jar" runNumber:%4 parallelEvaluations:false base:%1 log:%2-%3 saveTo:%3 trials:%5 io:false netio:false onlyWatchPareto:true printFitness:true animateNetwork:false ucb1Evaluation:false showSubnetAnalysis:false monitorInputs:false experiment:edu.southwestern.experiment.post.ObjectiveBestNetworksExperiment logLock:true watchLastBest:false monitorSubstrates:false showVizDoomInputs:false showCPPN:false substrateGridSize:10 showWeights:false watch:true showNetworks:false inheritFitness:false