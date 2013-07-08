call vars.bat
java -version
dx.bat --dex --output=%PROJECT_ROOT%\classes.dex %PROJECT_ROOT%\build\jar\%MODULE_NAME%.jar