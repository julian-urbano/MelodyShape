@ECHO OFF

:: Set path to collection
SET MIDI=".\midi"

:: List of algorithms
SET ALGORITHMS=(2010-domain 2010-pitchderiv 2010-shape 2011-pitch 2011-time 2011-shape 2012-shapeh 2012-shapel 2012-shapeg 2012-time 2012-shapetime 2013-shapeh 2013-time 2013-shapetime 2014-shapeh 2014-time 2014-shapetime)

:: Check arguments
IF /I "%1"=="help" (
	GOTO HELP
)
IF /I "%1"=="run" (
	IF NOT "%2"=="" ( IF NOT "%3"=="" ( GOTO RUN ) )
	GOTO HELP
)
IF /I "%1"=="between" (
	IF NOT "%2"=="" ( IF NOT "%3"=="" ( GOTO BETWEEN ) )
	GOTO HELP
)
IF /I "%1"=="within" (
	IF NOT "%2"=="" ( GOTO WITHIN )
	GOTO HELP
)
GOTO HELP

::
:RUN
::
FOR %%A IN %ALGORITHMS% DO (
	ECHO %3_%%A
	java -jar "%2" -q %MIDI% -c %MIDI% -t 1 -a %%A > %3_%%A.txt
)
GOTO DONE

::
:BETWEEN
::
FOR %%A IN %ALGORITHMS% DO (
	IF EXIST %2_%%A.txt (
		IF EXIST %3_%%A.txt (
			FC %2_%%A.txt %3_%%A.txt > NUL
			IF ERRORLEVEL 1 ECHO %%A FAIL: MISMATCH
		) ELSE ECHO %%A FAIL: %3 DOES NOT EXIST
	) ELSE ECHO %%A FAIL: %2 DOES NOT EXIST
)
GOTO DONE

::
:WITHIN
::
SETLOCAL EnableDelayedExpansion
FOR %%A IN ("2010-shape 2011-shape 2012-shapeh 2013-shapeh 2014-shapeh" "2011-time 2012-time 2013-time 2014-time" "2012-shapetime 2013-shapetime 2014-shapetime") DO (
SET BASELINE=NULL
FOR %%B IN (%%~A) DO (
	IF !BASELINE!==NULL SET BASELINE=%%B
	IF EXIST %2_%%B.txt (
		FC %2_!BASELINE!.txt %2_%%B.txt > NUL
		IF ERRORLEVEL 1 ECHO !BASELINE! FAIL: %%B MISMATCH
	) ELSE ECHO !BASELINE! FAIL: %%B DOES NOT EXIST
))
ENDLOCAL
GOTO DONE

::
:HELP
::
ECHO RESULTS RUN JARFILE TAG   : run JARFILE and tag with TAG
ECHO RESULTS BETWEEN TAG1 TAG2 : compare all files between tags TAG1 and tAG2
ECHO RESULTS WITHIN TAG        : compare supposably same files with tag TAG
ECHO.
GOTO DONE

::
:DONE
::
