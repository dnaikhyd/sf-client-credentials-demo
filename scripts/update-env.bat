@echo off
echo Updating .env file with correct Salesforce token URL...
echo.

REM Check if .env exists
if not exist .env (
    echo Creating .env from template...
    copy .env.template .env
)

REM Create a temporary file with the updated content
(
    for /f "usebackq tokens=*" %%a in (".env") do (
        echo %%a | findstr /C:"SALESFORCE_TOKEN_URL=" >nul
        if errorlevel 1 (
            echo %%a
        ) else (
            echo SALESFORCE_TOKEN_URL=https://orgfarm-5a64afc6be-dev-ed.develop.my.salesforce.com/services/oauth2/token
        )
    )
) > .env.tmp

REM Replace the original file
move /y .env.tmp .env >nul

echo.
echo [SUCCESS] Updated SALESFORCE_TOKEN_URL in .env file
echo New URL: https://orgfarm-5a64afc6be-dev-ed.develop.my.salesforce.com/services/oauth2/token
echo.
echo You can now run: java SimpleConnectionTest
pause

@REM Made with Bob
