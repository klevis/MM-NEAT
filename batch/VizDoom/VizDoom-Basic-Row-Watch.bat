cd ..
cd ..
java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:vizdoombasic trials:1 maxGens:100 mu:5 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomBasicShootTask cleanOldNetworks:true fs:false noisyTaskStat:edu.utexas.cs.nn.util.stats.Average log:Basic-Row saveTo:Row gameWad:freedoom2.wad stepByStep:false doomInputStartX:0 doomInputStartY:65 doomInputHeight:25 doomInputWidth:200 monitorSubstrates:true showVizDoomInputs:false showCPPN:true substrateGridSize:10 showWeights:true watch:true showNetworks:true