REM Not the most efficient way to run all of these experiments, but it will work.
REM Better to run batches of runs of the same type in parellel using launchMultipleAsynchronously.bat
cd ..
cd ..

REM Interleaved domain
start launchMultiple.bat Experiments-GECCO-2015-MsPacMan MsPacMan-ImprisonConflict-OneModule.bat 0 29
start launchMultiple.bat Experiments-GECCO-2015-MsPacMan MsPacMan-ImprisonConflict-TwoModules.bat 0 29
start launchMultiple.bat Experiments-GECCO-2015-MsPacMan MsPacMan-ImprisonConflict-ThreeModules.bat 0 29
start launchMultiple.bat Experiments-GECCO-2015-MsPacMan MsPacMan-ImprisonConflict-Multitask.bat 0 29
start launchMultiple.bat Experiments-GECCO-2015-MsPacMan MsPacMan-ImprisonConflict-MMD.bat 0 29
REM Blended domain
start launchMultiple.bat Experiments-GECCO-2015-MsPacMan MsPacMan-OneLifeConflict-OneModule.bat 0 29
start launchMultiple.bat Experiments-GECCO-2015-MsPacMan MsPacMan-OneLifeConflict-TwoModules.bat 0 29
start launchMultiple.bat Experiments-GECCO-2015-MsPacMan MsPacMan-OneLifeConflict-ThreeModules.bat 0 29
start launchMultiple.bat Experiments-GECCO-2015-MsPacMan MsPacMan-OneLifeConflict-TwoModuleMultitask.bat 0 29
start launchMultiple.bat Experiments-GECCO-2015-MsPacMan MsPacMan-OneLifeConflict-ThreeModuleMultitask.bat 0 29
start launchMultiple.bat Experiments-GECCO-2015-MsPacMan MsPacMan-OneLifeConflict-MMD.bat 0 29