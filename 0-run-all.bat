@echo off
pushd "%~dp0"

echo Starting first backend ...
start cmd /c 1-run-backend-5000.bat
echo You need to wait until the first seed is initialized
echo after that, press Enter to spawn the rest of them
pause

echo Starting second backend ...
start cmd /c 2-run-backend-5001.bat

echo Starting first frontend ...
start cmd /c 3-run-frontend-5100.bat

echo Starting second frontend ...
start cmd /c 4-run-frontend-5101.bat

echo OK, 4 nodes should be up soon: two frontends and two backends
echo You can either shutdown some nodes or add new ones (run 5-8)
echo You can restart seed nodes as well, as long as there is at least one seed active all the time
pause

popd