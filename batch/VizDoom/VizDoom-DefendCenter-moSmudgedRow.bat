cd ..
cd ..
java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:vizdoomdefendcenter trials:10 maxGens:500 mu:50 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomDefendCenterTask cleanOldNetworks:true fs:false noisyTaskStat:edu.utexas.cs.nn.util.stats.Average log:DefendCenter-Control saveTo:Control gameWad:freedoom2.wad watch:false stepByStep:false doomEpisodeLength:2100 doomInputStartX:0 doomInputStartY:70 doomInputHeight:15 doomInputWidth:200 doomInputPixelSmudge:5 doomSmudgeStat:edu.utexas.cs.nn.util.stats.MostExtreme moVizDoom:true